package com.cellularautomata.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * The parameters section of the sidebar. Provides controls for configuring the automaton size, rule
 * number, and seed type. Exposes read-only accessors so {@link MainFrame} can retrieve the current
 * values without knowing the internal control types.
 */
class ParametersPanel extends JPanel {
  private JSlider sizeSlider;
  private JTextField ruleTextArea;
  private JComboBox<String> seedComboBox;

  ParametersPanel() {
    setLayout(new GridBagLayout());

    // Add section heading.
    addHeading();

    // Add parameter controls.
    addSizeControls();
    addRuleControls();
    addSeedControls();
  }

  int getSizeValue() {
    return sizeSlider.getValue();
  }

  String getRuleText() {
    return ruleTextArea.getText();
  }

  String getSeedType() {
    return (String) seedComboBox.getSelectedItem();
  }

  private void addHeading() {
    final JLabel heading = UiStyles.createHeading("Parameters");
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, UiStyles.SECTION_HEADING_GAP, 0);
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(heading, gbc);
  }

  private void addSizeControls() {
    final JLabel sizeLabel = new JLabel("Size (8)");
    GridBagConstraints gbcSizeLabel = new GridBagConstraints();
    gbcSizeLabel.gridwidth = 2;
    gbcSizeLabel.anchor = GridBagConstraints.WEST;
    gbcSizeLabel.insets = new Insets(0, 0, UiStyles.LABEL_TO_CONTROL_GAP, 0);
    gbcSizeLabel.gridx = 0;
    gbcSizeLabel.gridy = 1;
    add(sizeLabel, gbcSizeLabel);

    sizeSlider = new JSlider(JSlider.HORIZONTAL, 4, 64, 8);
    sizeSlider.addChangeListener(e -> sizeLabel.setText("Size (" + sizeSlider.getValue() + ")"));
    GridBagConstraints gbcSizeSlider = new GridBagConstraints();
    gbcSizeSlider.fill = GridBagConstraints.HORIZONTAL;
    gbcSizeSlider.gridwidth = 2;
    gbcSizeSlider.weightx = 1.0;
    gbcSizeSlider.insets = new Insets(0, 0, UiStyles.CONTROL_GROUP_GAP, 0);
    gbcSizeSlider.gridx = 0;
    gbcSizeSlider.gridy = 2;
    add(sizeSlider, gbcSizeSlider);
  }

  private void addRuleControls() {
    GridBagConstraints gbcRuleLabel = new GridBagConstraints();
    gbcRuleLabel.anchor = GridBagConstraints.WEST;
    gbcRuleLabel.insets =
        new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, UiStyles.FORM_LABEL_COLUMN_GAP);
    gbcRuleLabel.gridx = 0;
    gbcRuleLabel.gridy = 3;
    add(new JLabel("Rule number"), gbcRuleLabel);

    ruleTextArea = new JTextField("30");
    UiStyles.applyControlHeight(ruleTextArea);
    GridBagConstraints gbcRuleTextArea = new GridBagConstraints();
    gbcRuleTextArea.fill = GridBagConstraints.HORIZONTAL;
    gbcRuleTextArea.weightx = 1.0;
    gbcRuleTextArea.insets = new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0);
    gbcRuleTextArea.gridx = 1;
    gbcRuleTextArea.gridy = 3;
    add(ruleTextArea, gbcRuleTextArea);
  }

  private void addSeedControls() {
    GridBagConstraints gbcSeedLabel = new GridBagConstraints();
    gbcSeedLabel.anchor = GridBagConstraints.WEST;
    gbcSeedLabel.insets = new Insets(0, 0, 0, UiStyles.FORM_LABEL_COLUMN_GAP);
    gbcSeedLabel.gridx = 0;
    gbcSeedLabel.gridy = 4;
    add(new JLabel("Seed type"), gbcSeedLabel);

    String[] seedTypes = {"Uniform", "Sparse", "Alternating"};
    seedComboBox = new JComboBox<>(seedTypes);
    UiStyles.applyControlHeight(seedComboBox);
    GridBagConstraints gbcSeedComboBox = new GridBagConstraints();
    gbcSeedComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbcSeedComboBox.weightx = 1.0;
    gbcSeedComboBox.gridx = 1;
    gbcSeedComboBox.gridy = 4;
    add(seedComboBox, gbcSeedComboBox);
  }
}
