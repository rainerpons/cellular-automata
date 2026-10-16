package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

/**
 * The horizontal simulation and action toolbar displayed beneath the automaton display in the
 * Life-like workspace. Provides icon-only Play/Pause, Step, Reset, and Save buttons. Play/Pause and
 * Step start enabled to initiate or advance the simulation, while Reset and Save start disabled
 * until an automaton has been initialized.
 */
public final class LifeLikeToolbar extends HBox {
  static final String PLAY_ICON_PATH = "M 2 1 L 11 6 L 2 11 Z";
  static final String PAUSE_ICON_PATH = "M 2 1 H 5 V 11 H 2 Z M 7 1 H 10 V 11 H 7 Z";
  static final String STEP_ICON_PATH = "M 1 1 L 8 6 L 1 11 Z M 9 1 H 11 V 11 H 9 Z";
  static final String RESET_ICON_PATH =
      "M 6 1 V 3.5 L 2.5 0 L 6 -3.5 V -1 C 9.3 -1 12 1.7 12 5 "
          + "C 12 8.3 9.3 11 6 11 C 2.7 11 0 8.3 0 5 H 2 C 2 7.2 3.8 9 6 9 "
          + "C 8.2 9 10 7.2 10 5 C 10 2.8 8.2 1 6 1 Z";
  static final String SAVE_ICON_PATH =
      "M 1 1 H 9 L 11 3 V 11 H 1 Z M 3 1 V 5 H 8 V 1 Z M 3 7 V 10 H 9 V 7 Z";

  static final String PLAY_TOOLTIP =
      "Start or resume the simulation, advancing through generations automatically.";
  static final String PAUSE_TOOLTIP = "Pause the simulation at the current generation.";
  static final String STEP_TOOLTIP = "Advance the simulation by one generation.";
  static final String RESET_TOOLTIP = "Return the simulation to its initial generation.";
  static final String SAVE_TOOLTIP = "Save the currently displayed automaton as an image.";

  private final Button playPauseButton;
  private final SVGPath playPauseIcon;
  private final Tooltip playPauseTooltip;
  private final Button stepButton;
  private final SVGPath stepIcon;
  private final Button resetButton;
  private final SVGPath resetIcon;
  private final Button saveButton;
  private final SVGPath saveIcon;

  private boolean playing;

  /**
   * Constructs the Life-like toolbar with Play/Pause and Step enabled and Reset and Save disabled.
   */
  public LifeLikeToolbar() {
    super(UiStyles.BUTTON_GAP_X);
    setAlignment(Pos.CENTER_RIGHT);

    playPauseIcon = createIconShape(PLAY_ICON_PATH);
    playPauseButton = createIconButton(playPauseIcon, "Play", true);
    playPauseTooltip = UiStyles.installTooltip(playPauseButton, PLAY_TOOLTIP);

    stepIcon = createIconShape(STEP_ICON_PATH);
    stepButton = createIconButton(stepIcon, "Step", true);
    UiStyles.installTooltip(stepButton, STEP_TOOLTIP);

    resetIcon = createIconShape(RESET_ICON_PATH);
    resetButton = createIconButton(resetIcon, "Reset", false);
    UiStyles.installTooltip(resetButton, RESET_TOOLTIP);

    saveIcon = createIconShape(SAVE_ICON_PATH);
    saveButton = createIconButton(saveIcon, "Save", false);
    UiStyles.installTooltip(saveButton, SAVE_TOOLTIP);

    getChildren().addAll(playPauseButton, stepButton, resetButton, saveButton);
  }

  private static SVGPath createIconShape(String pathContent) {
    SVGPath iconShape = new SVGPath();
    iconShape.setContent(pathContent);
    iconShape.getStyleClass().add("simulation-icon");
    return iconShape;
  }

  private static Button createIconButton(
      SVGPath iconShape, String accessibleText, boolean enabled) {
    Button button = new Button();
    button.setGraphic(iconShape);
    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    button.getStyleClass().add("simulation-button");
    UiStyles.applyControlHeight(button);
    button.setMinWidth(UiStyles.CONTROL_HEIGHT);
    button.setPrefWidth(UiStyles.CONTROL_HEIGHT);
    button.setDisable(!enabled);
    button.setAccessibleText(accessibleText);
    return button;
  }

  public void setPlaying(boolean playing) {
    this.playing = playing;
    playPauseIcon.setContent(playing ? PAUSE_ICON_PATH : PLAY_ICON_PATH);
    playPauseTooltip.setText(playing ? PAUSE_TOOLTIP : PLAY_TOOLTIP);
    playPauseButton.setAccessibleText(playing ? "Pause" : "Play");
  }

  public boolean isPlaying() {
    return playing;
  }

  public void setPlayPauseEnabled(boolean enabled) {
    playPauseButton.setDisable(!enabled);
  }

  public void setStepEnabled(boolean enabled) {
    stepButton.setDisable(!enabled);
  }

  public void setResetEnabled(boolean enabled) {
    resetButton.setDisable(!enabled);
  }

  public void setSaveEnabled(boolean enabled) {
    saveButton.setDisable(!enabled);
  }

  /**
   * Registers an action listener on the Play/Pause button.
   *
   * @param listener the action listener
   */
  public void addPlayPauseListener(EventHandler<ActionEvent> listener) {
    playPauseButton.setOnAction(listener);
  }

  /**
   * Registers an action listener on the Step button.
   *
   * @param listener the action listener
   */
  public void addStepListener(EventHandler<ActionEvent> listener) {
    stepButton.setOnAction(listener);
  }

  /**
   * Registers an action listener on the Reset button.
   *
   * @param listener the action listener
   */
  public void addResetListener(EventHandler<ActionEvent> listener) {
    resetButton.setOnAction(listener);
  }

  /**
   * Registers an action listener on the Save button.
   *
   * @param listener the action listener
   */
  public void addSaveListener(EventHandler<ActionEvent> listener) {
    saveButton.setOnAction(listener);
  }

  Button getPlayPauseButton() {
    return playPauseButton;
  }

  String getPlayPauseIconPath() {
    return playPauseIcon.getContent();
  }

  Button getStepButton() {
    return stepButton;
  }

  String getStepIconPath() {
    return stepIcon.getContent();
  }

  Button getResetButton() {
    return resetButton;
  }

  String getResetIconPath() {
    return resetIcon.getContent();
  }

  Button getSaveButton() {
    return saveButton;
  }

  String getSaveIconPath() {
    return saveIcon.getContent();
  }
}
