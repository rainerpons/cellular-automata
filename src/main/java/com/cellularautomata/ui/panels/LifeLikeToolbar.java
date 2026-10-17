package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

/**
 * The horizontal simulation and action toolbar displayed beneath the automaton display in the
 * Life-like workspace. Provides icon-only Play/Pause, Step, Reset, and Save buttons. Play/Pause and
 * Step start enabled to initiate or advance the simulation, while Reset and Save start disabled
 * until an automaton has been initialized.
 */
public final class LifeLikeToolbar extends HBox {
  /** Named vector icons rendered inside the Life-like simulation toolbar buttons. */
  enum ToolbarIcon {
    PLAY("M 2 1 L 11 6 L 2 11 Z"),
    PAUSE("M 2 1 H 5 V 11 H 2 Z M 7 1 H 10 V 11 H 7 Z"),
    STEP("M 1 5 H 7 V 2 L 12 6 L 7 10 V 7 H 1 Z"),
    RESET(
        "M 6 2.5 V 5 L 2.5 1.5 L 6 -2 V 0.5 C 9.3 0.5 12 3.2 12 6.5 "
            + "C 12 9.8 9.3 12.5 6 12.5 C 2.7 12.5 0 9.8 0 6.5 H 2 C 2 8.7 3.8 10.5 6 10.5 "
            + "C 8.2 10.5 10 8.7 10 6.5 C 10 4.3 8.2 2.5 6 2.5 Z"),
    SAVE("M 1 1 H 9 L 11 3 V 11 H 1 Z M 3 1 V 5 H 8 V 1 Z M 3 7 V 10 H 9 V 7 Z");

    private final String svgPath;

    ToolbarIcon(String svgPath) {
      this.svgPath = svgPath;
    }

    SVGPath createGraphic() {
      SVGPath iconShape = new SVGPath();
      iconShape.getStyleClass().add("simulation-icon");
      applyTo(iconShape);
      return iconShape;
    }

    void applyTo(SVGPath iconShape) {
      iconShape.setContent(svgPath);
      iconShape.getProperties().put(ToolbarIcon.class, this);
    }

    static ToolbarIcon fromButton(Button button) {
      if (button.getGraphic() instanceof SVGPath iconShape
          && iconShape.getProperties().get(ToolbarIcon.class) instanceof ToolbarIcon icon) {
        return icon;
      }
      return null;
    }
  }

  static final String PLAY_TOOLTIP =
      "Start or resume the simulation, advancing through generations automatically.";
  static final String PAUSE_TOOLTIP = "Pause the simulation at the current generation.";
  static final String STEP_TOOLTIP = "Advance the simulation by one generation.";
  static final String RESET_TOOLTIP = "Return the simulation to its initial generation.";
  static final String SAVE_TOOLTIP = "Save the currently displayed automaton as an image.";

  private final Button playPauseButton;
  private final Button stepButton;
  private final Button resetButton;
  private final Button saveButton;

  private boolean playing;

  /**
   * Constructs the Life-like toolbar with Play/Pause and Step enabled and Reset and Save disabled.
   */
  public LifeLikeToolbar() {
    super(UiStyles.BUTTON_GAP_X);
    setAlignment(Pos.CENTER_RIGHT);

    playPauseButton = createToolbarButton(ToolbarIcon.PLAY, "Play", PLAY_TOOLTIP, true);
    stepButton = createToolbarButton(ToolbarIcon.STEP, "Step", STEP_TOOLTIP, true);
    resetButton = createToolbarButton(ToolbarIcon.RESET, "Reset", RESET_TOOLTIP, false);
    saveButton = createToolbarButton(ToolbarIcon.SAVE, "Save", SAVE_TOOLTIP, false);

    getChildren().addAll(playPauseButton, stepButton, resetButton, saveButton);
  }

  public void setPlaying(boolean playing) {
    this.playing = playing;
    ToolbarIcon targetIcon = playing ? ToolbarIcon.PAUSE : ToolbarIcon.PLAY;
    targetIcon.applyTo((SVGPath) playPauseButton.getGraphic());
    playPauseButton.getTooltip().setText(playing ? PAUSE_TOOLTIP : PLAY_TOOLTIP);
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

  ToolbarIcon getPlayPauseIcon() {
    return ToolbarIcon.fromButton(playPauseButton);
  }

  Button getStepButton() {
    return stepButton;
  }

  ToolbarIcon getStepIcon() {
    return ToolbarIcon.fromButton(stepButton);
  }

  Button getResetButton() {
    return resetButton;
  }

  ToolbarIcon getResetIcon() {
    return ToolbarIcon.fromButton(resetButton);
  }

  Button getSaveButton() {
    return saveButton;
  }

  ToolbarIcon getSaveIcon() {
    return ToolbarIcon.fromButton(saveButton);
  }

  private static Button createToolbarButton(
      ToolbarIcon icon, String accessibleText, String tooltipText, boolean enabled) {
    Button button = new Button();
    button.setGraphic(icon.createGraphic());
    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    button.getStyleClass().add("simulation-button");
    button.setMinSize(UiStyles.TOOLBAR_BUTTON_SIZE, UiStyles.TOOLBAR_BUTTON_SIZE);
    button.setPrefSize(UiStyles.TOOLBAR_BUTTON_SIZE, UiStyles.TOOLBAR_BUTTON_SIZE);
    button.setMaxSize(UiStyles.TOOLBAR_BUTTON_SIZE, UiStyles.TOOLBAR_BUTTON_SIZE);
    button.setDisable(!enabled);
    button.setAccessibleText(accessibleText);
    UiStyles.installTooltip(button, tooltipText);
    return button;
  }
}
