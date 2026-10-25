package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.ui.LifeLikeRuleSelectionModel;
import com.cellularautomata.ui.shared.LabeledSlider;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleConsumer;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * The parameters section for the Life-like cellular automata workspace. Composes {@link
 * ParametersPanel} to configure grid size, rule presets, seed type, conditional alive-cell density,
 * birth and survival neighbor count toggles (0 to 8), and playback speed.
 */
public final class LifeLikeParametersPanel {
  public static final int MIN_SIZE = 5;
  public static final int MAX_SIZE = 155;
  public static final int DEFAULT_SIZE = 8;
  public static final double SIZE_MAJOR_TICK = 25.0;
  public static final List<SeedType> SUPPORTED_SEEDS =
      List.of(SeedType.DENSITY, SeedType.SPARSE, SeedType.ALTERNATING);
  public static final double MIN_PLAYBACK_SPEED = 0.25;
  public static final double MAX_PLAYBACK_SPEED = 2.00;
  public static final double DEFAULT_PLAYBACK_SPEED = 1.00;
  public static final double PLAYBACK_SPEED_STEP = 0.25;

  static final int MIN_DENSITY = 0;
  static final int MAX_DENSITY = 100;
  static final int DEFAULT_DENSITY = 50;

  static final String BIRTH_TOOLTIP =
      "Select the number of living neighbors required for a dead cell to become alive in the next"
          + " generation.";
  static final String SURVIVAL_TOOLTIP =
      "Select the number of living neighbors required for an existing living cell to remain alive"
          + " in the next generation.";
  static final String DENSITY_TOOLTIP =
      "Controls the percentage of cells that are initially alive when generating a new automaton.";

  private static final int TOGGLE_GAP = 4;
  private static final int TOGGLE_MIN_WIDTH = 28;

  private final LifeLikeRuleSelectionModel selectionModel = new LifeLikeRuleSelectionModel();
  private final ParametersPanel panel = new ParametersPanel();
  private final List<ToggleButton> birthToggles = new ArrayList<>();
  private final List<ToggleButton> survivalToggles = new ArrayList<>();

  private final LabeledSlider sizeSlider;
  private final ComboBox<LifeLikePreset> presetComboBox;
  private final ComboBox<SeedType> seedComboBox;
  private final LabeledSlider densitySlider;
  private final Spinner<Double> playbackSpeedSpinner;

  private boolean updatingControls;

  /** Constructs the Life-like parameters panel with default Conway's Game of Life settings. */
  public LifeLikeParametersPanel() {
    sizeSlider =
        panel.addSizeSliderRow(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE, SIZE_MAJOR_TICK, true, false);
    presetComboBox = addPresetRow();
    seedComboBox = panel.addSeedTypeRow(SUPPORTED_SEEDS, SeedType.DENSITY, true);
    densitySlider = addDensityRow();
    panel.addControlRow("Birth", BIRTH_TOOLTIP, createNeighborToggleRow(birthToggles, true), true);
    panel.addControlRow(
        "Survival", SURVIVAL_TOOLTIP, createNeighborToggleRow(survivalToggles, false), true);
    playbackSpeedSpinner =
        NumericSpinnerFactory.createPlaybackSpeedSpinner(
            MIN_PLAYBACK_SPEED, MAX_PLAYBACK_SPEED, DEFAULT_PLAYBACK_SPEED, PLAYBACK_SPEED_STEP);
    panel.addControlRow("Playback speed", null, playbackSpeedSpinner, false);

    seedComboBox
        .valueProperty()
        .addListener((obs, oldVal, newVal) -> densitySlider.setVisible(newVal == SeedType.DENSITY));

    syncControlsFromModel();
  }

  public int getSizeValue() {
    return sizeSlider.getValue();
  }

  public LifeLikePreset getPreset() {
    return selectionModel.getPreset();
  }

  /**
   * Returns the display text for the active preset, or {@code "Custom"} when arbitrary birth and
   * survival counts are selected.
   *
   * @return the preset display name or {@code "Custom"}
   */
  public String getPresetDisplayText() {
    LifeLikePreset preset = selectionModel.getPreset();
    if (preset == null) {
      return "Custom";
    }
    return preset.getDisplayName();
  }

  public Set<Integer> getBirthCounts() {
    return selectionModel.getBirthCounts();
  }

  public Set<Integer> getSurvivalCounts() {
    return selectionModel.getSurvivalCounts();
  }

  public SeedType getSeedType() {
    return seedComboBox.getValue();
  }

  public int getDensityValue() {
    return densitySlider.getValue();
  }

  ParametersPanel getPanel() {
    return panel;
  }

  LifeLikeRuleSelectionModel getSelectionModel() {
    return selectionModel;
  }

  /**
   * Sets the grid size value on the size slider.
   *
   * @param size grid side length
   */
  public void setSizeValue(int size) {
    sizeSlider.setValue(size);
  }

  String getSizeLabelText() {
    return sizeSlider.getLabelText();
  }

  /**
   * Selects a rule preset in the preset dropdown.
   *
   * @param preset the rule preset to select
   */
  public void selectPreset(LifeLikePreset preset) {
    presetComboBox.setValue(preset);
  }

  ComboBox<LifeLikePreset> getPresetComboBox() {
    return presetComboBox;
  }

  /**
   * Toggles the selection state of a birth neighbor count toggle.
   *
   * @param neighborCount live neighbor count from 0 to 8
   */
  public void toggleBirth(int neighborCount) {
    ToggleButton toggle = birthToggles.get(neighborCount);
    if (!toggle.isDisabled()) {
      toggle.setSelected(!toggle.isSelected());
    }
  }

  /**
   * Toggles the selection state of a survival neighbor count toggle.
   *
   * @param neighborCount live neighbor count from 0 to 8
   */
  public void toggleSurvival(int neighborCount) {
    ToggleButton toggle = survivalToggles.get(neighborCount);
    if (!toggle.isDisabled()) {
      toggle.setSelected(!toggle.isSelected());
    }
  }

  ToggleButton getBirthToggle(int neighborCount) {
    return birthToggles.get(neighborCount);
  }

  ToggleButton getSurvivalToggle(int neighborCount) {
    return survivalToggles.get(neighborCount);
  }

  /**
   * Selects an initial seed type in the seed type dropdown.
   *
   * @param seedType initial cell lattice seed type
   */
  public void selectSeedType(SeedType seedType) {
    seedComboBox.setValue(seedType);
  }

  ComboBox<SeedType> getSeedComboBox() {
    return seedComboBox;
  }

  /**
   * Sets the alive-cell density percentage value on the density slider.
   *
   * @param density alive-cell density percentage (0 to 100)
   */
  public void setDensityValue(double density) {
    densitySlider.setValue(density);
  }

  String getDensityLabelText() {
    return densitySlider.getLabelText();
  }

  boolean isDensityVisible() {
    return densitySlider.isVisible();
  }

  boolean isDensityManaged() {
    return densitySlider.isManaged();
  }

  Slider getDensitySlider() {
    return densitySlider.getSlider();
  }

  Slider getSizeSlider() {
    return sizeSlider.getSlider();
  }

  public double getPlaybackSpeed() {
    Double value = playbackSpeedSpinner.getValue();
    return value == null ? DEFAULT_PLAYBACK_SPEED : value;
  }

  /**
   * Sets the playback speed multiplier on the playback speed spinner, clamping to {@code [0.25,
   * 2.00]} and snapping to the nearest {@code 0.25} increment.
   *
   * @param speed playback speed multiplier
   * @throws IllegalArgumentException if speed is NaN or infinite
   */
  public void setPlaybackSpeed(double speed) {
    if (Double.isNaN(speed) || Double.isInfinite(speed)) {
      throw new IllegalArgumentException("Playback speed must be finite.");
    }
    double clamped = Math.max(MIN_PLAYBACK_SPEED, Math.min(MAX_PLAYBACK_SPEED, speed));
    double snapped =
        Math.round((clamped - MIN_PLAYBACK_SPEED) / PLAYBACK_SPEED_STEP) * PLAYBACK_SPEED_STEP
            + MIN_PLAYBACK_SPEED;
    playbackSpeedSpinner.getValueFactory().setValue(snapped);
  }

  public String getPlaybackSpeedDisplayText() {
    return playbackSpeedSpinner.getEditor().getText();
  }

  /**
   * Registers a listener notified whenever the playback speed value changes.
   *
   * @param listener callback receiving the updated playback speed multiplier
   * @throws IllegalArgumentException if listener is null
   */
  public void addPlaybackSpeedListener(DoubleConsumer listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Playback speed listener cannot be null.");
    }
    playbackSpeedSpinner
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal != null) {
                listener.accept(newVal);
              }
            });
  }

  Spinner<Double> getPlaybackSpeedSpinner() {
    return playbackSpeedSpinner;
  }

  private ComboBox<LifeLikePreset> addPresetRow() {
    ComboBox<LifeLikePreset> comboBox =
        UiStyles.createDropdown(
            List.of(LifeLikePreset.values()), LifeLikePreset.CONWAYS_GAME_OF_LIFE);
    comboBox.setPromptText("Custom");
    PresetButtonCell buttonCell = new PresetButtonCell();
    comboBox.setButtonCell(buttonCell);
    comboBox
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              buttonCell.update(newVal);
              if (updatingControls || newVal == null) {
                return;
              }
              selectionModel.selectPreset(newVal);
              syncControlsFromModel();
            });
    panel.addControlRow("Preset", null, comboBox, true);
    return comboBox;
  }

  private static final class PresetButtonCell extends ListCell<LifeLikePreset> {
    PresetButtonCell() {
      update(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
    }

    void update(LifeLikePreset item) {
      updateItem(item, item == null);
    }

    @Override
    protected void updateItem(LifeLikePreset item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item == null) {
        setText("Custom");
      } else {
        setText(item.getDisplayName());
      }
    }
  }

  private LabeledSlider addDensityRow() {
    LabeledSlider slider =
        new LabeledSlider(
            MIN_DENSITY,
            MAX_DENSITY,
            DEFAULT_DENSITY,
            25,
            true,
            false,
            density -> "Density (%d%%)".formatted(density));
    panel.addSliderRow(slider, DENSITY_TOOLTIP, true);
    return slider;
  }

  private HBox createNeighborToggleRow(List<ToggleButton> targetList, boolean isBirth) {
    HBox row = new HBox(TOGGLE_GAP);
    GridPane.setHgrow(row, Priority.ALWAYS);
    for (int count = LifeLikeRule.MIN_NEIGHBOR_COUNT;
        count <= LifeLikeRule.MAX_NEIGHBOR_COUNT;
        count++) {
      final int neighborCount = count;
      ToggleButton toggle = new ToggleButton(Integer.toString(count));
      toggle.getStyleClass().add("neighbor-toggle");
      UiStyles.applyControlHeight(toggle);
      toggle.setMinWidth(TOGGLE_MIN_WIDTH);
      toggle.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(toggle, Priority.ALWAYS);
      toggle
          .selectedProperty()
          .addListener(
              (obs, oldVal, newVal) -> handleNeighborToggleChange(isBirth, neighborCount, newVal));
      targetList.add(toggle);
      row.getChildren().add(toggle);
    }
    return row;
  }

  private void handleNeighborToggleChange(boolean isBirth, int neighborCount, boolean selected) {
    if (updatingControls) {
      return;
    }
    if (isBirth) {
      selectionModel.setBirthSelected(neighborCount, selected);
      syncControlsFromModel();
      return;
    }
    selectionModel.setSurvivalSelected(neighborCount, selected);
    syncControlsFromModel();
  }

  private void syncControlsFromModel() {
    updatingControls = true;
    try {
      presetComboBox.setValue(selectionModel.getPreset());
      if (presetComboBox.getButtonCell() instanceof PresetButtonCell cell) {
        cell.update(selectionModel.getPreset());
      }
      for (int count = LifeLikeRule.MIN_NEIGHBOR_COUNT;
          count <= LifeLikeRule.MAX_NEIGHBOR_COUNT;
          count++) {
        ToggleButton birthToggle = birthToggles.get(count);
        birthToggle.setSelected(selectionModel.isBirthSelected(count));
        birthToggle.setDisable(selectionModel.isBirthToggleDisabled(count));

        ToggleButton survivalToggle = survivalToggles.get(count);
        survivalToggle.setSelected(selectionModel.isSurvivalSelected(count));
        survivalToggle.setDisable(selectionModel.isSurvivalToggleDisabled(count));
      }
    } finally {
      updatingControls = false;
    }
  }
}
