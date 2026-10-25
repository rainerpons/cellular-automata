package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.automata.LifeLikeSimulation;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import com.cellularautomata.ui.panels.ParametersPanel;
import com.cellularautomata.ui.shared.UiStyles;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;
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
  private TestSaveDialogService dialogService;

  @Override
  public void start(Stage stage) {
    this.stage = stage;
    this.dialogService = new TestSaveDialogService();
    this.app = new MainApp(dialogService);
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
    LifeLikeParametersPanel parametersPanel = findLifeLikeParametersPanel();
    LifeLikeToolbar toolbar = findLifeLikeToolbar();

    interact(() -> parametersPanel.selectSeedType(SeedType.ALTERNATING));

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
    Assert.assertFalse(toolbar.getSaveButton().isDisabled());
    Assert.assertNotNull(displayPanel.getAutomatonImage());

    // 3. Pause interaction -> Paused state
    interact(() -> toolbar.getPlayPauseButton().fire());

    Assert.assertFalse(toolbar.isPlaying());
    Assert.assertEquals("Play", toolbar.getPlayPauseButton().getAccessibleText());
    Assert.assertEquals(
        LifeLikeToolbar.PLAY_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
    Assert.assertFalse(toolbar.getStepButton().isDisabled());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());
    Assert.assertFalse(toolbar.getSaveButton().isDisabled());

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
    Assert.assertFalse(toolbar.getSaveButton().isDisabled());
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
        DisplayPanel.GENERATION_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
        displayPanel.getErrorMessage());
    Assert.assertTrue(toolbar.getSaveButton().isDisabled());

    // 2. Verify error view container and icon presentation
    VBox errorContainer = lookup(".error-container").queryAs(VBox.class);
    Assert.assertNotNull(errorContainer);
    Assert.assertTrue(
        "Error container must have dashed border matching placeholder",
        errorContainer.getStyleClass().contains("placeholder"));
    Assert.assertEquals(400.0, errorContainer.getMinWidth(), 0.001);
    Assert.assertEquals(400.0, errorContainer.getMinHeight(), 0.001);

    FontIcon errorIcon = lookup(".error-icon").queryAs(FontIcon.class);
    Assert.assertNotNull(errorIcon);
    Assert.assertEquals(MaterialDesignA.ALERT_CIRCLE_OUTLINE, errorIcon.getIconCode());
    Assert.assertEquals(40, errorIcon.getIconSize());

    // 3. Test successful recovery
    interact(controller::step);

    Assert.assertFalse(displayPanel.isDisplayingError());
    Assert.assertNotNull(displayPanel.getAutomatonImage());
    Assert.assertFalse(toolbar.getResetButton().isDisabled());
    Assert.assertFalse(toolbar.getSaveButton().isDisabled());
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
  public void testCustomPresetDropdownPresentationAndVisibleTransitions() {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    LifeLikeParametersPanel parametersPanel = findLifeLikeParametersPanel();
    LifeLikeToolbar toolbar = findLifeLikeToolbar();
    ComboBox<LifeLikePreset> presetComboBox = findPresetComboBox();
    Assert.assertNotNull(presetComboBox);

    // 1. Verify default "Conway's Game of Life" presentation
    Assert.assertEquals("Conway's Game of Life", presetComboBox.getButtonCell().getText());
    Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, parametersPanel.getPreset());

    // 2. Toggle off survival 2 (producing custom B3/S3) and verify ComboBox button cell visibly
    // presents "Custom" (and specifically is NOT empty/blank)
    interact(() -> parametersPanel.toggleSurvival(2));
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());
    Assert.assertFalse(presetComboBox.getButtonCell().getText().isBlank());
    Assert.assertNull(parametersPanel.getPreset());

    // 3. Select a named preset (e.g., Seeds) and verify button cell updates to "Seeds"
    interact(() -> presetComboBox.setValue(LifeLikePreset.SEEDS));
    Assert.assertEquals("Seeds", presetComboBox.getButtonCell().getText());
    Assert.assertEquals(LifeLikePreset.SEEDS, parametersPanel.getPreset());

    // 4. Transition to Custom and verify Step/Reset preserves visible "Custom" display
    interact(() -> parametersPanel.toggleBirth(6)); // Seeds (B2/S) + B6 -> Custom B26/S
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());

    interact(() -> toolbar.getStepButton().fire());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());

    interact(() -> toolbar.getResetButton().fire());
    Assert.assertEquals("Custom", presetComboBox.getButtonCell().getText());
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

  @Test
  public void testPlaybackSpeedSpinnerPresentationLiveSpeedChangeAndSaveExportInGui()
      throws Exception {
    navigateToWorkspace(WorkspaceConfig.LIFE_LIKE);

    DisplayPanel displayPanel = findDisplayPanel();
    LifeLikeController controller = app.getActiveLifeLikeController();
    LifeLikeParametersPanel parametersPanel = findLifeLikeParametersPanel();
    LifeLikeToolbar toolbar = findLifeLikeToolbar();
    ParametersPanel grid = findNode(stage.getScene().getRoot(), ParametersPanel.class, null);
    Spinner<Double> speedSpinner = findPlaybackSpeedSpinner();

    Assert.assertNotNull(displayPanel);
    Assert.assertNotNull(grid);
    Assert.assertNotNull(speedSpinner);

    // 1. Verify spinner presentation, split-arrow geometry, and row placement immediately below
    // Survival (row 7)
    Assert.assertTrue(
        speedSpinner.getStyleClass().contains(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL));
    Assert.assertFalse(speedSpinner.isEditable());
    Assert.assertEquals(UiStyles.CONTROL_HEIGHT, speedSpinner.getMinHeight(), 0.001);
    Assert.assertEquals(UiStyles.CONTROL_HEIGHT, speedSpinner.getPrefHeight(), 0.001);

    Label survivalLabel = findNode(grid, Label.class, lbl -> "Survival".equals(lbl.getText()));
    Label playbackSpeedLabel =
        findNode(grid, Label.class, lbl -> "Playback speed".equals(lbl.getText()));
    Assert.assertNotNull(survivalLabel);
    Assert.assertNotNull(playbackSpeedLabel);
    Assert.assertEquals(Integer.valueOf(6), GridPane.getRowIndex(survivalLabel));
    Assert.assertEquals(Integer.valueOf(7), GridPane.getRowIndex(playbackSpeedLabel));
    Assert.assertEquals(Integer.valueOf(7), GridPane.getRowIndex(speedSpinner));
    Assert.assertEquals(Integer.valueOf(1), GridPane.getColumnIndex(speedSpinner));

    Node decArrow = speedSpinner.lookup(".decrement-arrow-button");
    Node editorField = speedSpinner.lookup(".text-field");
    Node incArrow = speedSpinner.lookup(".increment-arrow-button");
    Assert.assertNotNull(decArrow);
    Assert.assertNotNull(editorField);
    Assert.assertNotNull(incArrow);

    Bounds decBounds = decArrow.localToScene(decArrow.getBoundsInLocal());
    Bounds editorBounds = editorField.localToScene(editorField.getBoundsInLocal());
    Bounds incBounds = incArrow.localToScene(incArrow.getBoundsInLocal());
    Assert.assertTrue(
        "Decrement button must be on the left of the text field",
        decBounds.getMinX() < editorBounds.getMinX());
    Assert.assertTrue(
        "Increment button must be on the right of the text field",
        editorBounds.getMinX() < incBounds.getMinX());

    // Verify footer alignment: Back link aligned with left edge of DisplayPanel, transport toolbar
    // flush with right edge of DisplayPanel
    Label backLink = lookup(".navigation-link").queryAs(Label.class);
    Assert.assertEquals("← Back to Modules", backLink.getText());
    Assert.assertEquals(4, toolbar.getChildren().size());

    Bounds displayBounds = displayPanel.localToScene(displayPanel.getBoundsInLocal());
    Bounds backBounds = backLink.localToScene(backLink.getBoundsInLocal());
    Bounds toolbarBounds = toolbar.localToScene(toolbar.getBoundsInLocal());
    Assert.assertEquals(displayBounds.getMinX(), backBounds.getMinX(), 2.0);
    Assert.assertEquals(displayBounds.getMaxX(), toolbarBounds.getMaxX(), 2.0);
    Assert.assertTrue(toolbarBounds.getMaxX() < stage.getScene().getWidth() - 100.0);

    // 2. Verify default 1.00x and value changes across bounds (0.25x to 2.00x)
    Assert.assertEquals(1.00, parametersPanel.getPlaybackSpeed(), 0.001);
    Assert.assertEquals("1.00x", parametersPanel.getPlaybackSpeedDisplayText());

    interact(() -> parametersPanel.setPlaybackSpeed(0.25));
    Assert.assertEquals("0.25x", speedSpinner.getEditor().getText());
    interact(() -> speedSpinner.decrement());
    Assert.assertEquals("0.25x", speedSpinner.getEditor().getText());

    interact(() -> parametersPanel.setPlaybackSpeed(2.00));
    Assert.assertEquals("2.00x", speedSpinner.getEditor().getText());
    interact(() -> speedSpinner.increment());
    Assert.assertEquals("2.00x", speedSpinner.getEditor().getText());

    // 3. Configure deterministic simulation, start playback, and change speed during playback
    // without losing generation state
    interact(
        () -> {
          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);
          parametersPanel.setPlaybackSpeed(1.00);
          toolbar.getPlayPauseButton().fire();
        });

    LifeLikeSimulation activeSim = controller.getActiveSimulation();
    Assert.assertNotNull(activeSim);
    Assert.assertTrue(controller.isRunning());

    interact(
        () -> {
          speedSpinner.increment(); // 1.25x
          speedSpinner.increment(); // 1.50x
        });

    Assert.assertEquals(1.50, parametersPanel.getPlaybackSpeed(), 0.001);
    Assert.assertEquals("1.50x", speedSpinner.getEditor().getText());
    Assert.assertTrue(controller.isRunning());
    Assert.assertSame(activeSim, controller.getActiveSimulation());

    // 4. Pause, step, and click the real GUI Save button to export and verify pixels
    interact(
        () -> {
          toolbar.getPlayPauseButton().fire();
          toolbar.getStepButton().fire();
        });

    Assert.assertFalse(controller.isRunning());
    Assert.assertFalse(toolbar.getSaveButton().isDisabled());
    Grid2D displayedGrid = controller.getActiveSimulation().getCurrentGrid();

    Path tempDir = Files.createTempDirectory("lifelike_ui_save_test");
    File exportedFile = tempDir.resolve("exported_from_gui.png").toFile();
    exportedFile.deleteOnExit();
    tempDir.toFile().deleteOnExit();

    dialogService.targetSaveFile = exportedFile;
    interact(() -> toolbar.getSaveButton().fire());

    Assert.assertNotNull(dialogService.lastSuggestedFileName);
    Assert.assertTrue(
        dialogService.lastSuggestedFileName.startsWith("life_like_ruleB3-S23_size8_"));
    Assert.assertTrue(dialogService.lastSuggestedFileName.endsWith(".png"));
    Assert.assertTrue(exportedFile.exists());

    BufferedImage diskImage = ImageIO.read(exportedFile);
    Assert.assertNotNull(diskImage);
    Assert.assertEquals(400, diskImage.getWidth());
    Assert.assertEquals(400, diskImage.getHeight());

    int size = displayedGrid.getSize();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        int x = (int) ((c + 0.5) * 400.0 / size);
        int y = (int) ((r + 0.5) * 400.0 / size);
        int actualRgb = diskImage.getRGB(x, y) & 0xFFFFFF;
        int expectedRgb = displayedGrid.isAlive(r, c) ? 0x000000 : 0xFFFFFF;
        Assert.assertEquals(
            "Exported pixel mismatch at (" + r + ", " + c + ")", expectedRgb, actualRgb);
      }
    }
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

  @SuppressWarnings("unchecked")
  private Spinner<Double> findPlaybackSpeedSpinner() {
    return findNode(
        stage.getScene().getRoot(), Spinner.class, sp -> sp.getValue() instanceof Double);
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

  private static final class TestSaveDialogService extends DialogService {
    private String lastSuggestedFileName;
    private File targetSaveFile;

    @Override
    public File showSaveFileDialog(Window owner, File initialDirectory, String initialFileName) {
      this.lastSuggestedFileName = initialFileName;
      return targetSaveFile;
    }
  }
}
