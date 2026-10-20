package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.testfx.framework.junit.ApplicationTest;

/** UI tests verifying Life-Like workspace navigation, generation, error display, and regression. */
public class LifeLikeUiTest extends ApplicationTest {

  @BeforeClass
  public static void setupHeadless() {
    System.setProperty("testfx.robot", "glass");
  }

  private Stage stage;

  @Override
  public void start(Stage stage) {
    this.stage = stage;
    MainApp app = new MainApp();
    app.start(stage);
  }

  @Test
  public void testNavigationToLifeLikeWorkspaceParameterSelectionAndPlaceholder() {
    // 1. Initial screen is Module Selection Screen
    Label title = lookup(".h1").queryAs(Label.class);
    Assert.assertEquals("Choose a module", title.getText());

    Button continueButton = lookup(".continue-button").queryAs(Button.class);
    Assert.assertTrue(continueButton.isDisabled());

    // 2. Select Life-Like module card
    ModuleCard lifeLikeCard = findModuleCard(WorkspaceConfig.LIFE_LIKE);
    Assert.assertNotNull(lifeLikeCard);
    interact(() -> lifeLikeCard.getOnMouseClicked().handle(null));
    Assert.assertFalse(continueButton.isDisabled());

    // 3. Continue to Life-Like workspace
    interact(continueButton::fire);

    Assert.assertEquals(WorkspaceConfig.LIFE_LIKE.getWindowTitle(), stage.getTitle());

    // 4. Verify initial placeholder is visible
    DisplayPanel displayPanel = findDisplayPanel();
    Assert.assertNotNull(displayPanel);
    Assert.assertEquals(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT, displayPanel.getPlaceholderText());
    Assert.assertNull(displayPanel.getAutomatonImage());
    Assert.assertFalse(displayPanel.isDisplayingError());

    // 5. Verify parameter controls can be selected
    LifeLikeController controller = getLifeLikeController(displayPanel);
    Assert.assertNotNull(controller);
    LifeLikeParametersPanel parametersPanel = controller.getParametersPanel();
    Assert.assertNotNull(parametersPanel);

    interact(
        () -> {
          parametersPanel.setSizeValue(16);
          parametersPanel.selectPreset(LifeLikePreset.HIGH_LIFE);
          parametersPanel.selectSeedType(SeedType.SPARSE);
        });

    Assert.assertEquals(16, parametersPanel.getSizeValue());
    Assert.assertEquals(LifeLikePreset.HIGH_LIFE, parametersPanel.getPreset());
    Assert.assertEquals(SeedType.SPARSE, parametersPanel.getSeedType());

    // 6. Navigate back to modules
    Label backLink = lookup(".navigation-link").queryAs(Label.class);
    interact(() -> backLink.getOnMouseClicked().handle(null));

    Label returnedTitle = lookup(".h1").queryAs(Label.class);
    Assert.assertEquals("Choose a module", returnedTitle.getText());
  }

  @Test
  public void testRealGenerationRenderingIntegrationAndPixelColors() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    DisplayPanel displayPanel = findDisplayPanel();
    LifeLikeController controller = getLifeLikeController(displayPanel);
    LifeLikeParametersPanel parametersPanel = controller.getParametersPanel();
    LifeLikeToolbar toolbar = controller.getToolbar();

    interact(
        () -> {
          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.UNIFORM);
        });

    interact(controller::generateInitial);

    // Verify image presence and dimensions
    Image fxImage = displayPanel.getAutomatonImage();
    Assert.assertNotNull(fxImage);
    Assert.assertEquals(400.0, fxImage.getWidth(), 0.001);
    Assert.assertEquals(400.0, fxImage.getHeight(), 0.001);

    // Verify observable pixel content matches exact alive/dead cells
    Grid2D grid = controller.getActiveSimulation().getCurrentGrid();
    PixelReader reader = fxImage.getPixelReader();
    int size = grid.getSize();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        int x = (int) ((c + 0.5) * 400.0 / size);
        int y = (int) ((r + 0.5) * 400.0 / size);
        int rgb = reader.getArgb(x, y) & 0xFFFFFF;
        int expected = grid.isAlive(r, c) ? 0x000000 : 0xFFFFFF;
        Assert.assertEquals("Pixel mismatch at (" + r + ", " + c + ")", expected, rgb);
      }
    }

    // Verify toolbar controls state
    Assert.assertFalse(toolbar.getSaveButton().isDisabled());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());
  }

  @Test
  public void testErrorViewDisplayAndSuccessfulRecovery() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    DisplayPanel displayPanel = findDisplayPanel();
    LifeLikeController controller = getLifeLikeController(displayPanel);
    LifeLikeToolbar toolbar = controller.getToolbar();

    controller.setSimulationFactory(
        (rule, size, seedType, density) -> {
          throw new RuntimeException("Simulated generation failure");
        });

    interact(controller::reinitializeSimulation);

    // 1. Verify error view is visible
    Assert.assertTrue(displayPanel.isDisplayingError());
    Assert.assertNull(displayPanel.getAutomatonImage());
    Assert.assertEquals(
        DisplayPanel.GENERATION_ERROR_PRIMARY, displayPanel.getPrimaryErrorMessage());
    Assert.assertEquals(DisplayPanel.ERROR_SECONDARY, displayPanel.getSecondaryErrorMessage());
    Assert.assertTrue(toolbar.getSaveButton().isDisabled());
    Assert.assertTrue(toolbar.getResetButton().isDisabled());

    // 2. Verify error view container, icon, text colors, and dashed border
    StackPane imageContainer = (StackPane) displayPanel.getChildren().get(1);
    VBox errorContainer = (VBox) imageContainer.getChildren().get(0);
    Assert.assertTrue(
        "Error container must have dashed border matching placeholder",
        errorContainer.getStyleClass().contains("placeholder"));
    Assert.assertEquals(400.0, errorContainer.getMinWidth(), 0.001);
    Assert.assertEquals(400.0, errorContainer.getMinHeight(), 0.001);

    FontIcon errorIcon = (FontIcon) errorContainer.getChildren().get(0);
    Assert.assertEquals(MaterialDesignA.ALERT_CIRCLE_OUTLINE, errorIcon.getIconCode());
    Assert.assertEquals(40, errorIcon.getIconSize());
    Assert.assertEquals(Color.web("#ef4444"), errorIcon.getIconColor());

    Label primaryLabel = (Label) errorContainer.getChildren().get(1);
    Assert.assertEquals(Color.web("#ef4444"), primaryLabel.getTextFill());
    Assert.assertTrue(primaryLabel.getStyleClass().contains("error-label"));

    Label secondaryLabel = (Label) errorContainer.getChildren().get(2);
    Assert.assertEquals(Color.web("#ef4444"), secondaryLabel.getTextFill());
    Assert.assertTrue(secondaryLabel.getStyleClass().contains("error-label"));

    // 3. Test successful recovery
    controller.setSimulationFactory(null);
    interact(controller::reinitializeSimulation);

    Assert.assertFalse(displayPanel.isDisplayingError());
    Assert.assertNotNull(displayPanel.getAutomatonImage());
    Assert.assertFalse(toolbar.getSaveButton().isDisabled());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());
  }

  @Test
  public void testSmokeElementaryAndTotalisticGenerationRenderingRegressionProtection() {
    // 1. Elementary regression test
    navigateToWorkspace(WorkspaceConfig.ELEMENTARY);

    DisplayPanel elementaryDisplay = findDisplayPanel();
    CommandsPanel elementaryCommands = findCommandsPanel();
    Button elementaryGenerate = findButton(elementaryCommands, "Generate automaton");
    Button elementarySave = findButton(elementaryCommands, "Save image");

    Assert.assertNull(elementaryDisplay.getAutomatonImage());
    Assert.assertTrue(elementarySave.isDisabled());

    interact(elementaryGenerate::fire);

    Assert.assertNotNull(elementaryDisplay.getAutomatonImage());
    Assert.assertEquals(400.0, elementaryDisplay.getAutomatonImage().getWidth(), 0.001);
    Assert.assertEquals(400.0, elementaryDisplay.getAutomatonImage().getHeight(), 0.001);
    Assert.assertFalse(elementarySave.isDisabled());

    // 2. Return to modules and test Totalistic regression
    Label backLink1 = lookup(".navigation-link").queryAs(Label.class);
    interact(() -> backLink1.getOnMouseClicked().handle(null));

    navigateToWorkspace(WorkspaceConfig.TOTALISTIC);

    DisplayPanel totalisticDisplay = findDisplayPanel();
    CommandsPanel totalisticCommands = findCommandsPanel();
    Button totalisticGenerate = findButton(totalisticCommands, "Generate automaton");
    Button totalisticSave = findButton(totalisticCommands, "Save image");

    Assert.assertNull(totalisticDisplay.getAutomatonImage());
    Assert.assertTrue(totalisticSave.isDisabled());

    interact(totalisticGenerate::fire);

    Assert.assertNotNull(totalisticDisplay.getAutomatonImage());
    Assert.assertEquals(400.0, totalisticDisplay.getAutomatonImage().getWidth(), 0.001);
    Assert.assertEquals(400.0, totalisticDisplay.getAutomatonImage().getHeight(), 0.001);
    Assert.assertFalse(totalisticSave.isDisabled());
  }

  private void navigateToWorkspace(WorkspaceConfig config) {
    interact(
        () -> {
          // If not on module selection screen, navigate back
          for (Node node : lookup(".navigation-link").queryAll()) {
            if (node instanceof Label label && "← Back to Modules".equals(label.getText())) {
              label.getOnMouseClicked().handle(null);
              break;
            }
          }
        });

    ModuleCard card = findModuleCard(config);
    interact(() -> card.getOnMouseClicked().handle(null));
    Button continueButton = lookup(".continue-button").queryAs(Button.class);
    interact(continueButton::fire);
  }

  private ModuleCard findModuleCard(WorkspaceConfig config) {
    return findNode(
        stage.getScene().getRoot(), ModuleCard.class, card -> card.getConfig() == config);
  }

  private DisplayPanel findDisplayPanel() {
    return findNode(stage.getScene().getRoot(), DisplayPanel.class, null);
  }

  private CommandsPanel findCommandsPanel() {
    return findNode(stage.getScene().getRoot(), CommandsPanel.class, null);
  }

  private static LifeLikeController getLifeLikeController(DisplayPanel displayPanel) {
    Object controller = displayPanel.getProperties().get(LifeLikeController.class);
    if (controller instanceof LifeLikeController llc) {
      return llc;
    }
    throw new AssertionError("LifeLikeController property not found on DisplayPanel");
  }

  private static <T extends Node> T findNode(
      Node root, Class<T> clazz, java.util.function.Predicate<T> predicate) {
    if (clazz.isInstance(root)) {
      T cast = clazz.cast(root);
      if (predicate == null || predicate.test(cast)) {
        return cast;
      }
    }
    if (root instanceof Parent parent) {
      for (Node child : parent.getChildrenUnmodifiable()) {
        T found = findNode(child, clazz, predicate);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static Button findButton(CommandsPanel panel, String text) {
    for (Node child : panel.getChildren()) {
      if (child instanceof Button button && text.equals(button.getText())) {
        return button;
      }
    }
    throw new AssertionError("Button '" + text + "' not found in CommandsPanel");
  }
}
