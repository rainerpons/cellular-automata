package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.ui.shared.LabeledSlider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

/**
 * The parameters section for the Elementary cellular automata workspace. Composes {@link
 * ParametersPanel} to configure automaton size, elementary rule number, and one-dimensional seed
 * type.
 */
public final class ElementaryParametersPanel implements NumericRuleParametersPanel {
  private final ParametersPanel panel = new ParametersPanel();
  private final LabeledSlider sizeSlider;
  private final Spinner<Integer> ruleSpinner;
  private final ComboBox<SeedType> seedComboBox;

  /** Constructs the Elementary parameters panel with default size, rule, and seed values. */
  public ElementaryParametersPanel() {
    sizeSlider = panel.addSizeSliderRow();
    ruleSpinner =
        NumericSpinnerFactory.createRuleSpinner(
            ElementaryRule.MIN_RULE_NUMBER,
            ElementaryRule.DEFAULT_RULE_NUMBER,
            () -> ElementaryRule.MAX_RULE_NUMBER);
    panel.addControlRow("Rule number", null, ruleSpinner, true);
    seedComboBox = panel.addSeedTypeRow(SeedType.oneDimensionalValues(), SeedType.UNIFORM, false);
  }

  @Override
  public int getSizeValue() {
    return sizeSlider.getValue();
  }

  @Override
  public int getStatesValue() {
    return ElementaryRule.STATE_COUNT;
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

  Spinner<Integer> getRuleSpinner() {
    return ruleSpinner;
  }

  ComboBox<SeedType> getSeedComboBox() {
    return seedComboBox;
  }
}
