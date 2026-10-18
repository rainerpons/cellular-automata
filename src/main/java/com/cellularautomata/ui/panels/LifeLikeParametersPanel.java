package com.cellularautomata.ui.panels;

import com.cellularautomata.config.SeedType;
import com.cellularautomata.engine.rules.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.ui.shared.LabeledSlider;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * The parameters section for the Life-like cellular automata workspace. Composes {@link
 * ParametersPanel} to configure grid size, rule presets, seed type, conditional alive-cell density,
 * and birth and survival neighbor count toggles (0 to 8).
 */
public final class LifeLikeParametersPanel {
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

  private boolean updatingControls;

  /** Constructs the Life-like parameters panel with default Conway's Game of Life settings. */
  public LifeLikeParametersPanel() {
    sizeSlider = panel.addSizeSliderRow();
    presetComboBox = addPresetRow();
    seedComboBox = panel.addSeedTypeRow(List.of(SeedType.values()), SeedType.DENSITY, true);
    densitySlider = addDensityRow();
    panel.addControlRow("Birth", BIRTH_TOOLTIP, createNeighborToggleRow(birthToggles, true), true);
    panel.addControlRow(
        "Survival", SURVIVAL_TOOLTIP, createNeighborToggleRow(survivalToggles, false), false);

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

  void setSizeValue(int size) {
    sizeSlider.setValue(size);
  }

  String getSizeLabelText() {
    return sizeSlider.getLabelText();
  }

  void selectPreset(LifeLikePreset preset) {
    presetComboBox.setValue(preset);
  }

  ComboBox<LifeLikePreset> getPresetComboBox() {
    return presetComboBox;
  }

  void toggleBirth(int neighborCount) {
    ToggleButton toggle = birthToggles.get(neighborCount);
    if (!toggle.isDisabled()) {
      toggle.setSelected(!toggle.isSelected());
    }
  }

  void toggleSurvival(int neighborCount) {
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

  void selectSeedType(SeedType seedType) {
    seedComboBox.setValue(seedType);
  }

  ComboBox<SeedType> getSeedComboBox() {
    return seedComboBox;
  }

  void setDensityValue(double density) {
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

  private ComboBox<LifeLikePreset> addPresetRow() {
    ComboBox<LifeLikePreset> comboBox =
        UiStyles.createDropdown(
            List.of(LifeLikePreset.values()), LifeLikePreset.CONWAYS_GAME_OF_LIFE);
    comboBox
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (updatingControls || newVal == null) {
                return;
              }
              selectionModel.selectPreset(newVal);
              syncControlsFromModel();
            });
    panel.addControlRow("Preset", null, comboBox, true);
    return comboBox;
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
    } else {
      selectionModel.setSurvivalSelected(neighborCount, selected);
    }
    syncControlsFromModel();
  }

  private void syncControlsFromModel() {
    updatingControls = true;
    try {
      presetComboBox.setValue(selectionModel.getPreset());
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
