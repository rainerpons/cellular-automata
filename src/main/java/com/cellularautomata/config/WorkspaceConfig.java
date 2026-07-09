package com.cellularautomata.config;

import com.cellularautomata.engine.Rule;

import com.cellularautomata.engine.ElementaryRule;
import com.cellularautomata.engine.TotalisticRule;

/** Defines the configuration and constraints for a cellular automata workspace. */
public enum WorkspaceConfig {
  /** Elementary cellular automata workspace configuration. */
  ELEMENTARY(
      "Cellular Automata (Elementary)",
      ElementaryRule.MIN_RULE_NUMBER,
      ElementaryRule.MAX_RULE_NUMBER,
      ElementaryRule.DEFAULT_RULE_NUMBER,
      true),

  /** Totalistic cellular automata workspace configuration. */
  TOTALISTIC(
      "Cellular Automata (Totalistic)",
      TotalisticRule.MIN_RULE_NUMBER,
      TotalisticRule.MAX_RULE_NUMBER,
      TotalisticRule.DEFAULT_RULE_NUMBER,
      true);

  private final String windowTitle;
  private final int minRule;
  private final int maxRule;
  private final int defaultRule;
  private final boolean supportsGeneration;

  WorkspaceConfig(
      String windowTitle, int minRule, int maxRule, int defaultRule, boolean supportsGeneration) {
    this.windowTitle = windowTitle;
    this.minRule = minRule;
    this.maxRule = maxRule;
    this.defaultRule = defaultRule;
    this.supportsGeneration = supportsGeneration;
  }

  public String getWindowTitle() {
    return windowTitle;
  }

  public int getMinRule() {
    return minRule;
  }

  public int getMaxRule() {
    return maxRule;
  }

  public int getDefaultRule() {
    return defaultRule;
  }

  public boolean supportsGeneration() {
    return supportsGeneration;
  }

  /**
   * Instantiates the concrete Rule implementation corresponding to this workspace.
   *
   * @param ruleNumber the rule number to instantiate
   * @return a new Rule instance
   */
  public Rule createRule(int ruleNumber) {
    return switch (this) {
      case ELEMENTARY -> new ElementaryRule(ruleNumber);
      case TOTALISTIC -> new TotalisticRule(ruleNumber);
    };
  }
}
