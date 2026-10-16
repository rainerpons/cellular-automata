package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.SVGPath;

/**
 * The simulation controls section of the sidebar for the Life-like cellular automata workspace.
 * Provides icon-only Play/Pause, Step, and Reset buttons that start disabled until an automaton is
 * generated.
 */
public final class SimulationPanel extends GridPane {
  static final String PLAY_ICON_PATH = "M 2 1 L 11 6 L 2 11 Z";
  static final String PAUSE_ICON_PATH = "M 2 1 H 5 V 11 H 2 Z M 7 1 H 10 V 11 H 7 Z";
  static final String STEP_ICON_PATH = "M 1 1 L 8 6 L 1 11 Z M 9 1 H 11 V 11 H 9 Z";
  static final String RESET_ICON_PATH =
      "M 6 1 V 3.5 L 2.5 0 L 6 -3.5 V -1 C 9.3 -1 12 1.7 12 5 "
          + "C 12 8.3 9.3 11 6 11 C 2.7 11 0 8.3 0 5 H 2 C 2 7.2 3.8 9 6 9 "
          + "C 8.2 9 10 7.2 10 5 C 10 2.8 8.2 1 6 1 Z";

  private final Button playPauseButton;
  private final SVGPath playPauseIcon;
  private final Tooltip playPauseTooltip;
  private final Button stepButton;
  private final SVGPath stepIcon;
  private final Button resetButton;
  private final SVGPath resetIcon;

  private boolean playing;

  /** Constructs the simulation panel with disabled Play/Pause, Step, and Reset buttons. */
  public SimulationPanel() {
    setHgap(UiStyles.BUTTON_GAP_X);

    final Label simulationHeading = UiStyles.createHeading("Simulation");
    GridPane.setColumnSpan(simulationHeading, 3);
    GridPane.setMargin(simulationHeading, new Insets(0, 0, UiStyles.SECTION_HEADING_GAP, 0));
    add(simulationHeading, 0, 0);

    playPauseIcon = createIconShape(PLAY_ICON_PATH);
    playPauseTooltip = new Tooltip("Play");
    playPauseTooltip.getStyleClass().add("tooltip");
    playPauseButton = createIconButton(playPauseIcon, playPauseTooltip, "Play");
    add(playPauseButton, 0, 1);

    stepIcon = createIconShape(STEP_ICON_PATH);
    Tooltip stepTooltip = new Tooltip("Step");
    stepTooltip.getStyleClass().add("tooltip");
    stepButton = createIconButton(stepIcon, stepTooltip, "Step");
    add(stepButton, 1, 1);

    resetIcon = createIconShape(RESET_ICON_PATH);
    Tooltip resetTooltip = new Tooltip("Reset");
    resetTooltip.getStyleClass().add("tooltip");
    resetButton = createIconButton(resetIcon, resetTooltip, "Reset");
    add(resetButton, 2, 1);
  }

  private static SVGPath createIconShape(String pathContent) {
    SVGPath iconShape = new SVGPath();
    iconShape.setContent(pathContent);
    iconShape.getStyleClass().add("simulation-icon");
    return iconShape;
  }

  private static Button createIconButton(
      SVGPath iconShape, Tooltip tooltip, String accessibleText) {
    Button button = new Button();
    button.setGraphic(iconShape);
    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    button.getStyleClass().add("simulation-button");
    UiStyles.applyControlHeight(button);
    button.setDisable(true);
    button.setMaxWidth(Double.MAX_VALUE);
    button.setTooltip(tooltip);
    button.setAccessibleText(accessibleText);
    GridPane.setHgrow(button, Priority.ALWAYS);
    return button;
  }

  public void setSimulationEnabled(boolean enabled) {
    playPauseButton.setDisable(!enabled);
    stepButton.setDisable(!enabled);
    resetButton.setDisable(!enabled);
  }

  public void setPlaying(boolean playing) {
    this.playing = playing;
    String label = playing ? "Pause" : "Play";
    playPauseIcon.setContent(playing ? PAUSE_ICON_PATH : PLAY_ICON_PATH);
    playPauseTooltip.setText(label);
    playPauseButton.setAccessibleText(label);
  }

  public boolean isPlaying() {
    return playing;
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
}
