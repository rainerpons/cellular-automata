package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.ui.components.LabeledSlider;
import java.util.List;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

/**
 * The parameters section for the Elementary cellular automata workspace. Composes {@link
 * ParametersPanel} to configure automaton size, elementary rule number, and one-dimensional seed
 * type.
 */
public final class ElementaryParametersPanel {
  public static final List<SeedType> SUPPORTED_SEEDS =
      List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING);

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
    seedComboBox = panel.addSeedTypeRow(SUPPORTED_SEEDS, SeedType.UNIFORM, false);
  }

  public int getSizeValue() {
    return sizeSlider.getValue();
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

  Spinner<Integer> getRuleSpinner() {
    return ruleSpinner;
  }

  ComboBox<SeedType> getSeedComboBox() {
    return seedComboBox;
  }
}
