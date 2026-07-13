package com.cellularautomata.config;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.Rule;
import com.cellularautomata.engine.rules.TotalisticRule;

/** Defines the configuration and constraints for a cellular automata workspace. */
public enum WorkspaceConfig {
  /** Elementary cellular automata workspace configuration. */
  ELEMENTARY(
      "Cellular Automata (Elementary)",
      ElementaryRule.MIN_RULE_NUMBER,
      ElementaryRule.MAX_RULE_NUMBER,
      ElementaryRule.DEFAULT_RULE_NUMBER),

  /** Totalistic cellular automata workspace configuration. */
  TOTALISTIC(
      "Cellular Automata (Totalistic)",
      TotalisticRule.MIN_RULE_NUMBER,
      Integer
          .MAX_VALUE, // Managed dynamically via getMaxRule(states) but keep int bounds for parsing
      TotalisticRule.DEFAULT_RULE_NUMBER);

  /** The application window title for this workspace. */
  private final String windowTitle;

  /** The minimum allowed rule number. */
  private final int minRule;

  /** The maximum allowed rule number. */
  private final int maxRule;

  /** The default rule number. */
  private final int defaultRule;

  WorkspaceConfig(String windowTitle, int minRule, int maxRule, int defaultRule) {
    this.windowTitle = windowTitle;
    this.minRule = minRule;
    this.maxRule = maxRule;
    this.defaultRule = defaultRule;
  }

  public String getWindowTitle() {
    return windowTitle;
  }

  public int getMinRule() {
    return minRule;
  }

  public int getMaxRule(int states) {
    if (this == TOTALISTIC) {
      java.math.BigInteger max =
          java.math.BigInteger.valueOf(states)
              .pow(3 * (states - 1) + 1)
              .subtract(java.math.BigInteger.ONE);
      return max.compareTo(java.math.BigInteger.valueOf(Integer.MAX_VALUE)) > 0
          ? Integer.MAX_VALUE
          : max.intValue();
    }
    return maxRule;
  }

  public int getDefaultRule() {
    return defaultRule;
  }

  /**
   * Instantiates the concrete Rule implementation corresponding to this workspace.
   *
   * @param ruleNumber the rule number to instantiate
   * @param states the number of states (ignored for ELEMENTARY)
   * @return a new Rule instance
   */
  public Rule instantiateRule(int ruleNumber, int states) {
    return switch (this) {
      case ELEMENTARY -> new ElementaryRule(ruleNumber);
      case TOTALISTIC -> new TotalisticRule(ruleNumber, states);
    };
  }
}
