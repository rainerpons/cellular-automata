package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;

/**
 * The horizontal simulation and action toolbar displayed beneath the automaton display in the
 * Life-like workspace. Provides icon-only Play/Pause, Step, Reset, and Save buttons. Play/Pause and
 * Step start enabled to initiate or advance the simulation, while Reset and Save start disabled
 * until an automaton has been initialized.
 */
public final class LifeLikeToolbar extends HBox {
  static final int TOOLBAR_ICON_SIZE = 18;

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

    playPauseButton = createToolbarButton(MaterialDesignP.PLAY, "Play", PLAY_TOOLTIP, true);
    stepButton = createToolbarButton(MaterialDesignD.DEBUG_STEP_OVER, "Step", STEP_TOOLTIP, true);
    resetButton = createToolbarButton(MaterialDesignR.RESTART, "Reset", RESET_TOOLTIP, false);
    saveButton = createToolbarButton(MaterialDesignC.CONTENT_SAVE, "Save", SAVE_TOOLTIP, false);

    getChildren().addAll(playPauseButton, stepButton, resetButton, saveButton);
  }

  public void setPlaying(boolean playing) {
    this.playing = playing;
    FontIcon playPauseIcon = (FontIcon) playPauseButton.getGraphic();
    playPauseIcon.setIconCode(playing ? MaterialDesignP.PAUSE : MaterialDesignP.PLAY);
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

  Button getStepButton() {
    return stepButton;
  }

  Button getResetButton() {
    return resetButton;
  }

  Button getSaveButton() {
    return saveButton;
  }

  private static Button createToolbarButton(
      Ikon icon, String accessibleText, String tooltipText, boolean enabled) {
    FontIcon iconGraphic = new FontIcon(icon);
    iconGraphic.setIconSize(TOOLBAR_ICON_SIZE);
    iconGraphic.getStyleClass().add("simulation-icon");

    Button button = new Button();
    button.setGraphic(iconGraphic);
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
