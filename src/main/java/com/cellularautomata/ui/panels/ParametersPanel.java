package com.cellularautomata.ui.panels;

import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

/**
 * The parameters section of the sidebar. Provides controls for configuring the automaton size, rule
 * number, and seed type. Exposes read-only accessors for external consumers to retrieve the current
 * values without exposing internal control implementations.
 */
public final class ParametersPanel extends GridPane {
  static final int MIN_SIZE = 4;
  static final int MAX_SIZE = 128;
  static final int DEFAULT_SIZE = 8;

  private final WorkspaceConfig config;

  private Slider sizeSlider;
  private Spinner<Integer> statesSpinner;
  private Spinner<Integer> ruleSpinner;
  private ComboBox<String> seedComboBox;

  private int currentRow = 1;

  /**
   * Constructs the parameters panel.
   *
   * @param config the workspace configuration
   */
  public ParametersPanel(WorkspaceConfig config) {
    this.config = config;

    setHgap(UiStyles.FORM_LABEL_COLUMN_GAP);

    // Add section heading.
    addHeading();

    // Add parameter controls.
    addSizeControls();
    if (config.supportsCustomStates()) {
      addStatesControls();
    }
    addRuleControls();
    addSeedControls();
  }

  /**
   * Gets the size value.
   *
   * @return the size value
   */
  public int getSizeValue() {
    return (int) sizeSlider.getValue();
  }

  /**
   * Gets the states value.
   *
   * @return the states value
   */
  public int getStatesValue() {
    if (statesSpinner == null) {
      return config.getDefaultStates();
    }
    return statesSpinner.getValue();
  }

  /**
   * Gets the rule text.
   *
   * @return the rule text
   */
  public String getRuleText() {
    return ruleSpinner.getEditor().getText();
  }

  /**
   * Gets the seed type.
   *
   * @return the seed type
   */
  public String getSeedType() {
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
    add(sizeLabel, 0, currentRow++);

    sizeSlider = new Slider(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE);
    sizeSlider.setBlockIncrement(1);
    sizeSlider.setMajorTickUnit(1);
    sizeSlider.setMinorTickCount(0);
    sizeSlider.setSnapToTicks(true);
    sizeSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> sizeLabel.setText("Size (" + newVal.intValue() + ")"));
    GridPane.setColumnSpan(sizeSlider, 2);
    GridPane.setMargin(sizeSlider, new Insets(0, 0, UiStyles.CONTROL_GROUP_GAP, 0));
    add(sizeSlider, 0, currentRow++);
  }

  private <T> Spinner<T> createSpinner(SpinnerValueFactory<T> factory) {
    Spinner<T> spinner = new Spinner<>();
    spinner.setValueFactory(factory);
    spinner.setEditable(true);
    spinner
        .getEditor()
        .setTextFormatter(
            new TextFormatter<>(
                change -> {
                  if (!change.getControlNewText().matches("\\d*")) {
                    return null;
                  }
                  return change;
                }));
    spinner.getEditor().setAlignment(Pos.CENTER_LEFT);
    UiStyles.applyControlHeight(spinner);
    spinner.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(spinner, Priority.ALWAYS);
    GridPane.setMargin(spinner, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    return spinner;
  }

  private void addStatesControls() {
    Label statesLabel = new Label("States");
    GridPane.setMargin(statesLabel, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(statesLabel, 0, currentRow);

    SpinnerValueFactory<Integer> valueFactory =
        new SpinnerValueFactory.IntegerSpinnerValueFactory(
            config.getMinStates(), config.getMaxStates(), config.getDefaultStates());
    statesSpinner = createSpinner(valueFactory);
    add(statesSpinner, 1, currentRow++);
  }

  private void addRuleControls() {
    Label ruleLabel = new Label("Rule number");
    GridPane.setMargin(ruleLabel, new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0));
    add(ruleLabel, 0, currentRow);

    SpinnerValueFactory<Integer> valueFactory =
        new SpinnerValueFactory<Integer>() {
          @Override
          public void decrement(int steps) {
            int current = getValue() == null ? config.getMinRule() : getValue();
            setValue(Math.max(config.getMinRule(), current - steps));
          }

          @Override
          public void increment(int steps) {
            int states = getStatesValue();
            int maxRule = config.getMaxRule(states);
            int current = getValue() == null ? config.getMinRule() : getValue();
            setValue(Math.min(maxRule, current + steps));
          }
        };
    valueFactory.setValue(config.getDefaultRule());
    valueFactory.setConverter(
        new StringConverter<Integer>() {
          @Override
          public String toString(Integer value) {
            return value == null ? "" : value.toString();
          }

          @Override
          public Integer fromString(String string) {
            try {
              if (string == null || string.isEmpty()) {
                return valueFactory.getValue();
              }
              return Integer.parseInt(string);
            } catch (NumberFormatException e) {
              return valueFactory.getValue();
            }
          }
        });

    ruleSpinner = createSpinner(valueFactory);
    add(ruleSpinner, 1, currentRow++);
  }

  private void addSeedControls() {
    Label seedLabel = new Label("Seed type");
    add(seedLabel, 0, currentRow);

    seedComboBox =
        new ComboBox<>(FXCollections.observableArrayList("Uniform", "Sparse", "Alternating"));
    seedComboBox.setValue("Uniform");
    UiStyles.applyControlHeight(seedComboBox);
    seedComboBox.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(seedComboBox, Priority.ALWAYS);
    add(seedComboBox, 1, currentRow++);
  }
}
