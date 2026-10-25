package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.TotalisticRule;
import com.cellularautomata.ui.shared.LabeledSlider;
import java.util.List;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

/**
 * The parameters section for the Totalistic cellular automata workspace. Composes {@link
 * ParametersPanel} to configure automaton size, state count, totalistic rule number, and
 * one-dimensional seed type.
 */
public final class TotalisticParametersPanel implements NumericRuleParametersPanel {
  public static final List<SeedType> SUPPORTED_SEEDS =
      List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING);

  private final ParametersPanel panel = new ParametersPanel();
  private final LabeledSlider sizeSlider;
  private final Spinner<Integer> statesSpinner;
  private final Spinner<Integer> ruleSpinner;
  private final ComboBox<SeedType> seedComboBox;

  /**
   * Constructs the Totalistic parameters panel with default size, states, rule, and seed values.
   */
  public TotalisticParametersPanel() {
    sizeSlider = panel.addSizeSliderRow();
    statesSpinner =
        NumericSpinnerFactory.createIntegerSpinner(
            TotalisticRule.MIN_STATES, TotalisticRule.MAX_STATES, TotalisticRule.DEFAULT_STATES);
    panel.addControlRow("States", null, statesSpinner, true);
    ruleSpinner =
        NumericSpinnerFactory.createRuleSpinner(
            TotalisticRule.MIN_RULE_NUMBER,
            TotalisticRule.DEFAULT_RULE_NUMBER,
            () -> TotalisticRule.calculateMaxRule(getStatesValue()));
    panel.addControlRow("Rule number", null, ruleSpinner, true);
    seedComboBox = panel.addSeedTypeRow(SUPPORTED_SEEDS, SeedType.UNIFORM, false);
  }

  @Override
  public int getSizeValue() {
    return sizeSlider.getValue();
  }

  @Override
  public int getStatesValue() {
    return statesSpinner.getValue();
  }

  @Override
  public String getRuleText() {
    return ruleSpinner.getEditor().getText();
  }

  @Override
  public SeedType getSeedType() {
    return seedComboBox.getValue();
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

  Spinner<Integer> getStatesSpinner() {
    return statesSpinner;
  }

  Spinner<Integer> getRuleSpinner() {
    return ruleSpinner;
  }

  ComboBox<SeedType> getSeedComboBox() {
    return seedComboBox;
  }
}
