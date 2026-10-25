package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
  private MainApp app;

  @Override
  public void start(Stage stage) {
    this.stage = stage;
    this.app = new MainApp();
    this.app.start(stage);
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
    LifeLikeController controller = app.getActiveLifeLikeController();
    Assert.assertNotNull(controller);
    LifeLikeParametersPanel parametersPanel = findLifeLikeParametersPanel();
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
  public void testToolbarInteractionsAndAccessibilityStates() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    DisplayPanel displayPanel = findDisplayPanel();
    LifeLikeController controller = app.getActiveLifeLikeController();
    LifeLikeToolbar toolbar = findLifeLikeToolbar();

    // 1. Uninitialized state
    Assert.assertFalse(toolbar.isPlaying());
    Assert.assertEquals("Play", toolbar.getPlayPauseButton().getAccessibleText());
    Assert.assertEquals(
        LifeLikeToolbar.PLAY_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
    Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());

    Assert.assertEquals("Step", toolbar.getStepButton().getAccessibleText());
    Assert.assertEquals(
        LifeLikeToolbar.STEP_TOOLTIP, toolbar.getStepButton().getTooltip().getText());
    Assert.assertFalse(toolbar.getStepButton().isDisabled());

    Assert.assertEquals("Reset", toolbar.getResetButton().getAccessibleText());
    Assert.assertEquals(
        LifeLikeToolbar.RESET_TOOLTIP, toolbar.getResetButton().getTooltip().getText());
    Assert.assertTrue(toolbar.getResetButton().isDisabled());

    Assert.assertTrue(toolbar.getSaveButton().isDisabled());

    // 2. Play interaction -> Running state
    interact(() -> toolbar.getPlayPauseButton().fire());

    Assert.assertTrue(toolbar.isPlaying());
    Assert.assertEquals("Pause", toolbar.getPlayPauseButton().getAccessibleText());
    Assert.assertEquals(
        LifeLikeToolbar.PAUSE_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
    Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
    Assert.assertTrue(toolbar.getStepButton().isDisabled());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());
    Assert.assertNotNull(displayPanel.getAutomatonImage());

    // 3. Pause interaction -> Paused state
    interact(() -> toolbar.getPlayPauseButton().fire());

    Assert.assertFalse(toolbar.isPlaying());
    Assert.assertEquals("Play", toolbar.getPlayPauseButton().getAccessibleText());
    Assert.assertEquals(
        LifeLikeToolbar.PLAY_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
    Assert.assertFalse(toolbar.getStepButton().isDisabled());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());

    // 4. Step interaction from paused
    long genBefore = controller.getActiveSimulation().getGeneration();
    interact(() -> toolbar.getStepButton().fire());
    Assert.assertEquals(genBefore + 1, controller.getActiveSimulation().getGeneration());

    // 5. Reset interaction
    interact(() -> toolbar.getResetButton().fire());
    Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());
    Assert.assertFalse(toolbar.isPlaying());
  }

  @Test
  public void testRealGenerationRenderingIntegrationAndPixelColors() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    DisplayPanel displayPanel = findDisplayPanel();
    LifeLikeController controller = app.getActiveLifeLikeController();
    LifeLikeParametersPanel parametersPanel = findLifeLikeParametersPanel();
    LifeLikeToolbar toolbar = findLifeLikeToolbar();

    interact(
        () -> {
          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.SPARSE);
        });

    interact(controller::step);

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
    Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
    Assert.assertFalse(toolbar.getStepButton().isDisabled());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());
    Assert.assertTrue(toolbar.getSaveButton().isDisabled());
  }

  @Test
  public void testErrorViewDisplayAndSuccessfulRecovery() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    DisplayPanel displayPanel = findDisplayPanel();
    LifeLikeController controller = app.getActiveLifeLikeController();
    LifeLikeToolbar toolbar = findLifeLikeToolbar();

    interact(displayPanel::showGenerationError);

    // 1. Verify error view is visible
    Assert.assertTrue(displayPanel.isDisplayingError());
    Assert.assertNull(displayPanel.getAutomatonImage());
    Assert.assertEquals(
        DisplayPanel.GENERATION_ERROR_PRIMARY, displayPanel.getPrimaryErrorMessage());
    Assert.assertEquals(DisplayPanel.ERROR_SECONDARY, displayPanel.getSecondaryErrorMessage());

    // 2. Verify error view container, icon, text style classes, and dashed border
    StackPane imageContainer = (StackPane) displayPanel.getChildren().get(1);
    VBox errorContainer = (VBox) imageContainer.getChildren().get(0);
    Assert.assertTrue(
        "Error container must have dashed border matching placeholder",
        errorContainer.getStyleClass().contains("placeholder"));
    Assert.assertTrue(
        "Error container must have error-container style class",
        errorContainer.getStyleClass().contains("error-container"));
    Assert.assertEquals(400.0, errorContainer.getMinWidth(), 0.001);
    Assert.assertEquals(400.0, errorContainer.getMinHeight(), 0.001);

    FontIcon errorIcon = (FontIcon) errorContainer.getChildren().get(0);
    Assert.assertEquals(MaterialDesignA.ALERT_CIRCLE_OUTLINE, errorIcon.getIconCode());
    Assert.assertEquals(40, errorIcon.getIconSize());
    Assert.assertTrue(errorIcon.getStyleClass().contains("error-icon"));

    Label primaryLabel = (Label) errorContainer.getChildren().get(1);
    Assert.assertTrue(primaryLabel.getStyleClass().contains("error-label"));

    Label secondaryLabel = (Label) errorContainer.getChildren().get(2);
    Assert.assertTrue(secondaryLabel.getStyleClass().contains("error-label"));

    // 3. Test successful recovery
    interact(controller::step);

    Assert.assertFalse(displayPanel.isDisplayingError());
    Assert.assertNotNull(displayPanel.getAutomatonImage());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());
    Assert.assertTrue(toolbar.getSaveButton().isDisabled());
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

  @Test
  public void testCustomPresetDropdownAllSixScenariosInGui() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    LifeLikeParametersPanel parametersPanel = findLifeLikeParametersPanel();
    LifeLikeToolbar toolbar = findLifeLikeToolbar();
    ComboBox<LifeLikePreset> presetComboBox = findPresetComboBox();
    Assert.assertNotNull(presetComboBox);

    // Scenario 1: Selecting Conway's Game of Life displays its name ("Conway's Game of Life") and
    // selects B3/S23.
    Assert.assertEquals("Conway's Game of Life", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Conway's Game of Life", presetComboBox.getButtonCell().getText());
    Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, parametersPanel.getPreset());
    Assert.assertEquals(Set.of(3), parametersPanel.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), parametersPanel.getSurvivalCounts());

    // Scenario 2: Changing B/S to a non-preset combination displays "Custom", not an empty
    // selection.
    interact(() -> parametersPanel.toggleSurvival(2)); // B3/S3 is non-preset
    Assert.assertEquals("Custom", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());
    Assert.assertFalse(presetComboBox.getButtonCell().getText().isEmpty());
    Assert.assertNull(parametersPanel.getPreset());
    Assert.assertEquals(Set.of(3), parametersPanel.getBirthCounts());
    Assert.assertEquals(Set.of(3), parametersPanel.getSurvivalCounts());

    // Scenario 3: Changing B/S to match HighLife automatically displays "HighLife".
    // HighLife is B36/S23: restore S2 and add B6
    interact(
        () -> {
          parametersPanel.toggleSurvival(2);
          parametersPanel.toggleBirth(6);
        });
    Assert.assertEquals("HighLife", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("HighLife", presetComboBox.getButtonCell().getText());
    Assert.assertEquals(LifeLikePreset.HIGH_LIFE, parametersPanel.getPreset());
    Assert.assertEquals(Set.of(3, 6), parametersPanel.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), parametersPanel.getSurvivalCounts());

    // Scenario 4: Restoring B3/S23 automatically displays "Conway's Game of Life".
    interact(() -> parametersPanel.toggleBirth(6)); // toggle off B6 -> B3/S23
    Assert.assertEquals("Conway's Game of Life", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Conway's Game of Life", presetComboBox.getButtonCell().getText());
    Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, parametersPanel.getPreset());
    Assert.assertEquals(Set.of(3), parametersPanel.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), parametersPanel.getSurvivalCounts());

    // Scenario 5: Selecting a named preset while "Custom" is displayed correctly updates the B/S
    // controls and dropdown.
    interact(() -> parametersPanel.toggleSurvival(2)); // make Custom
    Assert.assertEquals("Custom", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());

    interact(
        () -> presetComboBox.setValue(LifeLikePreset.SEEDS)); // Select named preset Seeds (B2/S)
    Assert.assertEquals("Seeds", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Seeds", presetComboBox.getButtonCell().getText());
    Assert.assertEquals(LifeLikePreset.SEEDS, parametersPanel.getPreset());
    Assert.assertEquals(Set.of(2), parametersPanel.getBirthCounts());
    Assert.assertTrue(parametersPanel.getSurvivalCounts().isEmpty());

    // Scenario 6: "Custom" remains displayed through Play, Pause, Step, and Reset unless the user
    // changes the B/S configuration.
    interact(() -> parametersPanel.toggleBirth(6)); // B26/S is non-preset -> Custom
    Assert.assertEquals("Custom", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());

    // Step
    interact(() -> toolbar.getStepButton().fire());
    Assert.assertEquals("Custom", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());

    // Play
    interact(() -> toolbar.getPlayPauseButton().fire());
    Assert.assertEquals("Custom", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());

    // Pause
    interact(() -> toolbar.getPlayPauseButton().fire());
    Assert.assertEquals("Custom", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());

    // Reset
    interact(() -> toolbar.getResetButton().fire());
    Assert.assertEquals("Custom", parametersPanel.getPresetDisplayText());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());
    Assert.assertEquals(Set.of(2, 6), parametersPanel.getBirthCounts());
    Assert.assertTrue(parametersPanel.getSurvivalCounts().isEmpty());
  }

  @Test
  public void testLifeLikeSeedTypesDropdownInGui() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    ComboBox<SeedType> seedComboBox = findSeedComboBox();
    Assert.assertNotNull(seedComboBox);

    Assert.assertEquals(
        List.of(SeedType.DENSITY, SeedType.SPARSE, SeedType.ALTERNATING), seedComboBox.getItems());
    Assert.assertEquals(SeedType.DENSITY, seedComboBox.getValue());
    Assert.assertFalse(seedComboBox.getItems().contains(SeedType.UNIFORM));

    // Return to modules and check Elementary workspace
    Label backLink = lookup(".navigation-link").queryAs(Label.class);
    interact(() -> backLink.getOnMouseClicked().handle(null));

    navigateToWorkspace(WorkspaceConfig.ELEMENTARY);
    ComboBox<SeedType> elementarySeedComboBox = findSeedComboBox();
    Assert.assertNotNull(elementarySeedComboBox);
    Assert.assertEquals(
        List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
        elementarySeedComboBox.getItems());
    Assert.assertTrue(elementarySeedComboBox.getItems().contains(SeedType.UNIFORM));
    Assert.assertFalse(elementarySeedComboBox.getItems().contains(SeedType.DENSITY));
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

  private LifeLikeToolbar findLifeLikeToolbar() {
    return findNode(stage.getScene().getRoot(), LifeLikeToolbar.class, null);
  }

  private LifeLikeParametersPanel findLifeLikeParametersPanel() {
    return app.getActiveLifeLikeParametersPanel();
  }

  @SuppressWarnings("unchecked")
  private ComboBox<LifeLikePreset> findPresetComboBox() {
    return findNode(
        stage.getScene().getRoot(),
        ComboBox.class,
        cb -> !cb.getItems().isEmpty() && cb.getItems().get(0) instanceof LifeLikePreset);
  }

  @SuppressWarnings("unchecked")
  private ComboBox<SeedType> findSeedComboBox() {
    return findNode(
        stage.getScene().getRoot(),
        ComboBox.class,
        cb -> !cb.getItems().isEmpty() && cb.getItems().get(0) instanceof SeedType);
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
