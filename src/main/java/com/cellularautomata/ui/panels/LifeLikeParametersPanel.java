package com.cellularautomata.ui.panels;

import com.cellularautomata.config.LifeLikePreset;
import com.cellularautomata.config.SeedType;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * The parameters section of the sidebar for the Life-like cellular automata workspace. Provides
 * controls for grid size, rule presets, birth and survival neighbor count toggles (0 to 8), derived
 * read-only B/S notation, seed type, and conditional alive-cell density.
 */
public final class LifeLikeParametersPanel extends GridPane {
  static final int MIN_SIZE = 4;
  static final int MAX_SIZE = 128;
  static final int DEFAULT_SIZE = 8;

  static final int MIN_NEIGHBOR_COUNT = 0;
  static final int MAX_NEIGHBOR_COUNT = 8;
  static final int MAX_SELECTED_COUNTS_PER_SET = 2;

  static final int MIN_DENSITY = 0;
  static final int MAX_DENSITY = 100;
  static final int DEFAULT_DENSITY = 50;

  private static final int TOGGLE_GAP = 4;
  private static final int TOGGLE_MIN_WIDTH = 28;

  private final List<ToggleButton> birthToggles = new ArrayList<>();
  private final List<ToggleButton> survivalToggles = new ArrayList<>();

  private Label sizeLabel;
  private Slider sizeSlider;
  private ComboBox<LifeLikePreset> presetComboBox;
  private TextField ruleNotationField;
  private ComboBox<SeedType> seedComboBox;
  private Label densityLabel;
  private Slider densitySlider;

  private boolean updatingControls;
  private int currentRow = 1;

  /** Constructs the Life-like parameters panel with default Conway's Game of Life settings. */
  public LifeLikeParametersPanel() {
    setHgap(UiStyles.FORM_LABEL_COLUMN_GAP);

    addHeading();
    addSizeControls();
    addPresetControls();
    addBirthControls();
    addSurvivalControls();
    addRuleNotationControls();
    addSeedControls();
    addDensityControls();

    applyPresetSelection(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
  }

  public int getSizeValue() {
    return (int) Math.round(sizeSlider.getValue());
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

  public String getRuleNotation() {
    return ruleNotationField.getText();
  }

  public SeedType getSeedType() {
    return seedComboBox.getValue();
  }

  public int getDensityValue() {
    return (int) Math.round(densitySlider.getValue());
  }

  void setSizeValue(int size) {
    sizeSlider.setValue(size);
  }

  String getSizeLabelText() {
    return sizeLabel.getText();
  }

  void selectPreset(LifeLikePreset preset) {
    presetComboBox.setValue(preset);
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

  boolean isRuleNotationEditable() {
    return ruleNotationField.isEditable();
  }

  void selectSeedType(SeedType seedType) {
    seedComboBox.setValue(seedType);
  }

  void setDensityValue(double density) {
    densitySlider.setValue(density);
  }

  String getDensityLabelText() {
    return densityLabel.getText();
  }

  boolean isDensityVisible() {
    return densityLabel.isVisible() && densitySlider.isVisible();
  }

  boolean isDensityManaged() {
    return densityLabel.isManaged() && densitySlider.isManaged();
  }

  Slider getDensitySlider() {
    return densitySlider;
  }

  private void addHeading() {
    final Label heading = UiStyles.createHeading("Parameters");
    GridPane.setColumnSpan(heading, 2);
    GridPane.setMargin(heading, new Insets(0, 0, UiStyles.PARAMETERS_HEADING_GAP, 0));
    add(heading, 0, 0);
  }

  private void addSizeControls() {
    sizeLabel = new Label("Size (" + DEFAULT_SIZE + ")");
    GridPane.setColumnSpan(sizeLabel, 2);
    GridPane.setMargin(sizeLabel, new Insets(0, 0, UiStyles.LABEL_TO_CONTROL_GAP, 0));
    add(sizeLabel, 0, currentRow++);

    sizeSlider = new Slider(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE);
    sizeSlider.setBlockIncrement(1);
    sizeSlider.setMajorTickUnit(1);
    sizeSlider.setMinorTickCount(0);
    sizeSlider.setSnapToTicks(true);
    sizeSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                sizeLabel.setText("Size (" + Math.round(newVal.doubleValue()) + ")"));
    GridPane.setColumnSpan(sizeSlider, 2);
    GridPane.setMargin(sizeSlider, new Insets(0, 0, UiStyles.CONTROL_GROUP_GAP, 0));
    add(sizeSlider, 0, currentRow++);
  }

  private void addPresetControls() {
    Label presetLabel = new Label("Preset");
    GridPane.setMargin(presetLabel, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(presetLabel, 0, currentRow);

    presetComboBox = new ComboBox<>(FXCollections.observableArrayList(LifeLikePreset.values()));
    presetComboBox.setValue(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
    UiStyles.applyControlHeight(presetComboBox);
    presetComboBox.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(presetComboBox, Priority.ALWAYS);
    GridPane.setMargin(presetComboBox, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    presetComboBox
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (updatingControls || newVal == null || newVal.isCustom()) {
                return;
              }
              applyPresetSelection(newVal);
            });
    add(presetComboBox, 1, currentRow++);
  }

  private void addBirthControls() {
    Label birthLabel = new Label("Birth");
    GridPane.setMargin(birthLabel, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(birthLabel, 0, currentRow);

    HBox birthRow = createNeighborToggleRow(birthToggles);
    GridPane.setMargin(birthRow, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(birthRow, 1, currentRow++);
  }

  private void addSurvivalControls() {
    Label survivalLabel = new Label("Survival");
    GridPane.setMargin(survivalLabel, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(survivalLabel, 0, currentRow);

    HBox survivalRow = createNeighborToggleRow(survivalToggles);
    GridPane.setMargin(survivalRow, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(survivalRow, 1, currentRow++);
  }

  private HBox createNeighborToggleRow(List<ToggleButton> targetList) {
    HBox row = new HBox(TOGGLE_GAP);
    GridPane.setHgrow(row, Priority.ALWAYS);
    for (int count = MIN_NEIGHBOR_COUNT; count <= MAX_NEIGHBOR_COUNT; count++) {
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

  private void addRuleNotationControls() {
    Label ruleLabel = new Label("Rule");
    GridPane.setMargin(ruleLabel, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(ruleLabel, 0, currentRow);

    ruleNotationField = new TextField();
    ruleNotationField.setEditable(false);
    ruleNotationField.setFocusTraversable(false);
    ruleNotationField.getStyleClass().add("read-only-field");
    UiStyles.applyControlHeight(ruleNotationField);
    ruleNotationField.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(ruleNotationField, Priority.ALWAYS);
    GridPane.setMargin(ruleNotationField, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(ruleNotationField, 1, currentRow++);
  }

  private void addSeedControls() {
    Label seedLabel = new Label("Seed type");
    add(seedLabel, 0, currentRow);

    seedComboBox = new ComboBox<>(FXCollections.observableArrayList(SeedType.values()));
    seedComboBox.setValue(SeedType.DENSITY);
    UiStyles.applyControlHeight(seedComboBox);
    seedComboBox.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(seedComboBox, Priority.ALWAYS);
    seedComboBox
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              boolean showDensity = newVal == SeedType.DENSITY;
              densityLabel.setVisible(showDensity);
              densitySlider.setVisible(showDensity);
            });
    add(seedComboBox, 1, currentRow++);
  }

  private void addDensityControls() {
    densityLabel = new Label("Density (" + DEFAULT_DENSITY + "%)");
    densityLabel.managedProperty().bind(densityLabel.visibleProperty());
    GridPane.setColumnSpan(densityLabel, 2);
    GridPane.setMargin(
        densityLabel,
        new Insets(UiStyles.FORM_ROW_BOTTOM_GAP, 0, UiStyles.LABEL_TO_CONTROL_GAP, 0));
    add(densityLabel, 0, currentRow++);

    densitySlider = new Slider(MIN_DENSITY, MAX_DENSITY, DEFAULT_DENSITY);
    densitySlider.managedProperty().bind(densitySlider.visibleProperty());
    densitySlider.setBlockIncrement(1);
    densitySlider.setMajorTickUnit(25);
    densitySlider.setMinorTickCount(0);
    densitySlider.setShowTickMarks(true);
    densitySlider.setShowTickLabels(true);
    densitySlider.setSnapToTicks(false);
    densitySlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                densityLabel.setText("Density (" + Math.round(newVal.doubleValue()) + "%)"));
    GridPane.setColumnSpan(densitySlider, 2);
    add(densitySlider, 0, currentRow++);
  }

  private void applyPresetSelection(LifeLikePreset preset) {
    updatingControls = true;
    try {
      applyCountsToToggles(birthToggles, preset.getBirthCounts());
      applyCountsToToggles(survivalToggles, preset.getSurvivalCounts());
      updateRuleNotation();
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
    ruleNotationField.setText(LifeLikePreset.formatNotation(birthCounts, survivalCounts));

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

  private void updateRuleNotation() {
    ruleNotationField.setText(LifeLikePreset.formatNotation(getBirthCounts(), getSurvivalCounts()));
  }
}
