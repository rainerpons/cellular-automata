package com.cellularautomata.ui.panels;

import com.cellularautomata.config.SeedType;

/**
 * Contract for parameter panels in workspaces that configure cellular automata using numeric rules.
 */
public interface NumericRuleParametersPanel {
  int getSizeValue();

  int getStatesValue();

  String getRuleText();

  SeedType getSeedType();
}
