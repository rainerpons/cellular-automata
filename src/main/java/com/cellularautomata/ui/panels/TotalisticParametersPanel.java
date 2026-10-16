package com.cellularautomata.ui.panels;

import com.cellularautomata.config.SeedType;
import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.ui.shared.LabeledSlider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

/**
 * The parameters section for the Totalistic cellular automata workspace. Composes {@link
 * ParametersPanel} to configure automaton size, state count, totalistic rule number, and
 * one-dimensional seed type.
 */
public final class TotalisticParametersPanel {
  private static final WorkspaceConfig CONFIG = WorkspaceConfig.TOTALISTIC;

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
        panel.addIntegerSpinnerRow(
            "States",
            CONFIG.getMinStates(),
            CONFIG.getMaxStates(),
            CONFIG.getDefaultStates(),
            true);
    ruleSpinner =
        panel.addRuleSpinnerRow(
            CONFIG.getMinRule(),
            CONFIG.getDefaultRule(),
            () -> CONFIG.getMaxRule(getStatesValue()),
            true);
    seedComboBox = panel.addSeedTypeRow(SeedType.oneDimensionalValues(), SeedType.UNIFORM, false);
  }

  public int getSizeValue() {
    return sizeSlider.getValue();
  }

  public int getStatesValue() {
    return statesSpinner.getValue();
  }

  public String getRuleText() {
    return ruleSpinner.getEditor().getText();
  }

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
