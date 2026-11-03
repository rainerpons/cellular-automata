package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.TotalisticRule;
import com.cellularautomata.ui.components.LabeledSlider;
import java.util.List;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

/**
 * The parameters section for the Totalistic cellular automata workspace. Composes {@link
 * ParametersPanel} to configure automaton size, state count, totalistic rule number, and
 * one-dimensional seed type.
 */
public final class TotalisticParametersPanel {
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
            () -> TotalisticRule.calculateMaxRule(resolveEffectiveStates()));
    panel.addControlRow("Rule number", null, ruleSpinner, true);
    seedComboBox = panel.addSeedTypeRow(SUPPORTED_SEEDS, SeedType.UNIFORM, false);
  }

  public int getSizeValue() {
    return sizeSlider.getValue();
  }

  public int getStatesValue() {
    return statesSpinner.getValue();
  }

  public String getStatesText() {
    return statesSpinner.getEditor().getText();
  }

  public String getRuleText() {
    return ruleSpinner.getEditor().getText();
  }

  public SeedType getSeedType() {
    return seedComboBox.getValue();
  }

  /**
   * Sets the automaton size value on the size slider.
   *
   * @param size automaton size
   */
  public void setSizeValue(int size) {
    sizeSlider.setValue(size);
  }

  /**
   * Sets the state count value on the states spinner.
   *
   * @param states number of cell states
   */
  public void setStatesValue(int states) {
    statesSpinner.getValueFactory().setValue(states);
  }

  /**
   * Sets the state count text in the states spinner editor.
   *
   * @param statesText state count text
   */
  public void setStatesText(String statesText) {
    statesSpinner.getEditor().setText(statesText);
  }

  /**
   * Sets the rule text in the rule spinner editor.
   *
   * @param ruleText rule number text
   */
  public void setRuleText(String ruleText) {
    ruleSpinner.getEditor().setText(ruleText);
  }

  /**
   * Selects an initial seed type in the seed type dropdown.
   *
   * @param seedType initial cell lattice seed type
   */
  public void selectSeedType(SeedType seedType) {
    seedComboBox.setValue(seedType);
  }

  ParametersPanel getPanel() {
    return panel;
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

  private int resolveEffectiveStates() {
    return RuleValidator.parseRule(
            getStatesText(), TotalisticRule.MIN_STATES, TotalisticRule.MAX_STATES)
        .orElseGet(
            () ->
                Math.clamp(getStatesValue(), TotalisticRule.MIN_STATES, TotalisticRule.MAX_STATES));
  }
}
