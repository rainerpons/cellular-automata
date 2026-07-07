package com.cellularautomata.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * The parameters section of the sidebar. Provides controls for configuring the automaton size, rule
 * number, and seed type. Exposes read-only accessors so {@link MainApp} can retrieve the current
 * values without knowing the internal control types.
 */
final class ParametersPanel extends GridPane {
  static final int MIN_SIZE = 4;
  static final int MAX_SIZE = 128;
  static final int DEFAULT_SIZE = 8;
  static final int DEFAULT_RULE = 30;

  private Slider sizeSlider;
  private Spinner<Integer> ruleSpinner;
  private ComboBox<String> seedComboBox;

  ParametersPanel() {
    setHgap(UiStyles.FORM_LABEL_COLUMN_GAP);
    // VGap handled via custom margins or keeping it simple with setVgap
    setVgap(UiStyles.FORM_ROW_BOTTOM_GAP);

    // Add section heading.
    addHeading();

    // Add parameter controls.
    addSizeControls();
    addRuleControls();
    addSeedControls();
  }

  int getSizeValue() {
    return (int) sizeSlider.getValue();
  }

  String getRuleText() {
    return ruleSpinner.getEditor().getText();
  }

  String getSeedType() {
    return seedComboBox.getValue();
  }

  private void addHeading() {
    final Label heading = UiStyles.createHeading("Parameters");
    GridPane.setColumnSpan(heading, 2);
    GridPane.setMargin(heading, new Insets(0, 0, UiStyles.PARAMETERS_HEADING_GAP, 0));
    add(heading, 0, 0);
  }

  private void addSizeControls() {
    final Label sizeLabel = new Label("Size (" + DEFAULT_SIZE + ")");
    GridPane.setColumnSpan(sizeLabel, 2);
    GridPane.setMargin(sizeLabel, new Insets(0, 0, UiStyles.LABEL_TO_CONTROL_GAP, 0));
    add(sizeLabel, 0, 1);

    sizeSlider = new Slider(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE);
    sizeSlider.setBlockIncrement(1);
    sizeSlider.setMajorTickUnit(1);
    sizeSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> sizeLabel.setText("Size (" + newVal.intValue() + ")"));
    GridPane.setColumnSpan(sizeSlider, 2);
    GridPane.setMargin(sizeSlider, new Insets(0, 0, UiStyles.CONTROL_GROUP_GAP, 0));
    add(sizeSlider, 0, 2);
  }

  private void addRuleControls() {
    Label ruleLabel = new Label("Rule number");
    add(ruleLabel, 0, 3);

    ruleSpinner = new Spinner<>(0, 255, DEFAULT_RULE, 1);
    ruleSpinner.setEditable(true);
    ruleSpinner.getEditor().setAlignment(Pos.CENTER_LEFT);
    UiStyles.applyControlHeight(ruleSpinner);
    ruleSpinner.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(ruleSpinner, Priority.ALWAYS);
    add(ruleSpinner, 1, 3);
  }

  private void addSeedControls() {
    Label seedLabel = new Label("Seed type");
    add(seedLabel, 0, 4);

    seedComboBox =
        new ComboBox<>(FXCollections.observableArrayList("Uniform", "Sparse", "Alternating"));
    seedComboBox.setValue("Uniform");
    UiStyles.applyControlHeight(seedComboBox);
    seedComboBox.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(seedComboBox, Priority.ALWAYS);
    add(seedComboBox, 1, 4);
  }
}
