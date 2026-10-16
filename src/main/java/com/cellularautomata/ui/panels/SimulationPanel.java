package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * The simulation controls section of the sidebar for the Life-like cellular automata workspace.
 * Provides icon-only Play/Pause, Step, and Reset buttons that start disabled until an automaton is
 * generated.
 */
public final class SimulationPanel extends GridPane {
  static final String PLAY_ICON = "▶";
  static final String PAUSE_ICON = "⏸";
  static final String STEP_ICON = "⏭";
  static final String RESET_ICON = "↺";

  private final Button playPauseButton;
  private final Tooltip playPauseTooltip;
  private final Button stepButton;
  private final Button resetButton;

  private boolean playing;

  /** Constructs the simulation panel with disabled Play/Pause, Step, and Reset buttons. */
  public SimulationPanel() {
    setHgap(UiStyles.BUTTON_GAP_X);

    final Label simulationHeading = UiStyles.createHeading("Simulation");
    GridPane.setColumnSpan(simulationHeading, 3);
    GridPane.setMargin(simulationHeading, new Insets(0, 0, UiStyles.SECTION_HEADING_GAP, 0));
    add(simulationHeading, 0, 0);

    playPauseTooltip = new Tooltip("Play");
    playPauseTooltip.getStyleClass().add("tooltip");
    playPauseButton = createIconButton(PLAY_ICON, playPauseTooltip, "Play");
    add(playPauseButton, 0, 1);

    Tooltip stepTooltip = new Tooltip("Step");
    stepTooltip.getStyleClass().add("tooltip");
    stepButton = createIconButton(STEP_ICON, stepTooltip, "Step");
    add(stepButton, 1, 1);

    Tooltip resetTooltip = new Tooltip("Reset");
    resetTooltip.getStyleClass().add("tooltip");
    resetButton = createIconButton(RESET_ICON, resetTooltip, "Reset");
    add(resetButton, 2, 1);
  }

  private static Button createIconButton(String iconText, Tooltip tooltip, String accessibleText) {
    Button button = new Button(iconText);
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
    playPauseButton.setText(playing ? PAUSE_ICON : PLAY_ICON);
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

  Button getStepButton() {
    return stepButton;
  }

  Button getResetButton() {
    return resetButton;
  }
}
