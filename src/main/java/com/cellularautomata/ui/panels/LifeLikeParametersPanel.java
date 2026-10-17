package com.cellularautomata.ui.panels;

import com.cellularautomata.config.LifeLikePreset;
import com.cellularautomata.config.SeedType;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.ui.shared.LabeledSlider;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
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
  /**
   * Intentional UI cap limiting selection to at most two birth counts and two survival counts for
   * the current workspace scope. Kept in the UI layer rather than {@link LifeLikeRule} or {@link
   * LifeLikePreset} because general Life-like cellular automata permit any subset of 0..8.
   */
  static final int MAX_SELECTED_COUNTS_PER_SET = 2;

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
    panel.addControlRow("Birth", BIRTH_TOOLTIP, createNeighborToggleRow(birthToggles), true);
    panel.addControlRow(
        "Survival", SURVIVAL_TOOLTIP, createNeighborToggleRow(survivalToggles), false);

    seedComboBox
        .valueProperty()
        .addListener((obs, oldVal, newVal) -> densitySlider.setVisible(newVal == SeedType.DENSITY));

    applyPresetSelection(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
  }

  public int getSizeValue() {
    return sizeSlider.getValue();
  }

  public LifeLikePreset getPreset() {
    return presetComboBox.getValue();
  }

  public Set<Integer> getBirthCounts() {
    return getSelectedCounts(birthToggles);
  }

  public Set<Integer> getSurvivalCounts() {
    return getSelectedCounts(survivalToggles);
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
              if (updatingControls || newVal == null || newVal.isCustom()) {
                return;
              }
              applyPresetSelection(newVal);
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

  private HBox createNeighborToggleRow(List<ToggleButton> targetList) {
    HBox row = new HBox(TOGGLE_GAP);
    GridPane.setHgrow(row, Priority.ALWAYS);
    for (int count = LifeLikeRule.MIN_NEIGHBOR_COUNT;
        count <= LifeLikeRule.MAX_NEIGHBOR_COUNT;
        count++) {
      ToggleButton toggle = new ToggleButton(Integer.toString(count));
      toggle.getStyleClass().add("neighbor-toggle");
      UiStyles.applyControlHeight(toggle);
      toggle.setMinWidth(TOGGLE_MIN_WIDTH);
      toggle.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(toggle, Priority.ALWAYS);
      toggle
          .selectedProperty()
          .addListener((obs, oldVal, newVal) -> handleNeighborToggleChange(targetList, toggle));
      targetList.add(toggle);
      row.getChildren().add(toggle);
    }
    return row;
  }

  private void applyPresetSelection(LifeLikePreset preset) {
    updatingControls = true;
    try {
      applyCountsToToggles(birthToggles, preset.getBirthCounts());
      applyCountsToToggles(survivalToggles, preset.getSurvivalCounts());
    } finally {
      updatingControls = false;
    }
  }

  private void applyCountsToToggles(List<ToggleButton> toggles, Set<Integer> counts) {
    for (int i = 0; i < toggles.size(); i++) {
      ToggleButton toggle = toggles.get(i);
      toggle.setDisable(false);
      toggle.setSelected(counts.contains(i));
    }
    updateToggleSetDisabledState(toggles);
  }

  private void handleNeighborToggleChange(List<ToggleButton> toggles, ToggleButton changedToggle) {
    if (updatingControls) {
      return;
    }
    // Enforce the UI-level cap of at most two selected neighbor counts per set.
    if (changedToggle.isSelected() && countSelected(toggles) > MAX_SELECTED_COUNTS_PER_SET) {
      updatingControls = true;
      try {
        changedToggle.setSelected(false);
      } finally {
        updatingControls = false;
      }
      return;
    }

    updateToggleSetDisabledState(toggles);

    Set<Integer> birthCounts = getBirthCounts();
    Set<Integer> survivalCounts = getSurvivalCounts();

    updatingControls = true;
    try {
      presetComboBox.setValue(LifeLikePreset.fromCounts(birthCounts, survivalCounts));
    } finally {
      updatingControls = false;
    }
  }

  private void updateToggleSetDisabledState(List<ToggleButton> toggles) {
    boolean limitReached = countSelected(toggles) >= MAX_SELECTED_COUNTS_PER_SET;
    for (ToggleButton toggle : toggles) {
      toggle.setDisable(limitReached && !toggle.isSelected());
    }
  }

  private int countSelected(List<ToggleButton> toggles) {
    int selected = 0;
    for (ToggleButton toggle : toggles) {
      if (toggle.isSelected()) {
        selected++;
      }
    }
    return selected;
  }

  private Set<Integer> getSelectedCounts(List<ToggleButton> toggles) {
    Set<Integer> counts = new LinkedHashSet<>();
    for (int i = 0; i < toggles.size(); i++) {
      if (toggles.get(i).isSelected()) {
        counts.add(i);
      }
    }
    return Collections.unmodifiableSet(counts);
  }
}
