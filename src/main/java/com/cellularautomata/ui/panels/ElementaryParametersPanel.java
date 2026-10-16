package com.cellularautomata.ui.panels;

import com.cellularautomata.config.SeedType;
import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.ui.shared.LabeledSlider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

/**
 * The parameters section for the Elementary cellular automata workspace. Composes {@link
 * ParametersPanel} to configure automaton size, elementary rule number, and one-dimensional seed
 * type.
 */
public final class ElementaryParametersPanel {
  private static final WorkspaceConfig CONFIG = WorkspaceConfig.ELEMENTARY;

  private final ParametersPanel panel = new ParametersPanel();
  private final LabeledSlider sizeSlider;
  private final Spinner<Integer> ruleSpinner;
  private final ComboBox<SeedType> seedComboBox;

  /** Constructs the Elementary parameters panel with default size, rule, and seed values. */
  public ElementaryParametersPanel() {
    sizeSlider = panel.addSizeSliderRow();
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
    return CONFIG.getDefaultStates();
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

  Spinner<Integer> getRuleSpinner() {
    return ruleSpinner;
  }

  ComboBox<SeedType> getSeedComboBox() {
    return seedComboBox;
  }
}
