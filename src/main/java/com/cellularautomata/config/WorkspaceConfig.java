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
      ElementaryRule.DEFAULT_RULE_NUMBER,
      2,
      2,
      2) {
    @Override
    public int getMaxRule(int states) {
      return ElementaryRule.MAX_RULE_NUMBER;
    }
  },

  /** Totalistic cellular automata workspace configuration. */
  TOTALISTIC(
      "Cellular Automata (Totalistic)",
      TotalisticRule.MIN_RULE_NUMBER,
      TotalisticRule.DEFAULT_RULE_NUMBER,
      TotalisticRule.MIN_STATES,
      TotalisticRule.MAX_STATES,
      TotalisticRule.DEFAULT_STATES) {
    @Override
    public int getMaxRule(int states) {
      return TotalisticRule.calculateMaxRule(states);
    }
  };

  /** The application window title for this workspace. */
  private final String windowTitle;

  /** The minimum allowed rule number. */
  private final int minRule;

  /** The default rule number. */
  private final int defaultRule;

  /** The minimum allowed number of states. */
  private final int minStates;

  /** The maximum allowed number of states. */
  private final int maxStates;

  /** The default number of states. */
  private final int defaultStates;

  WorkspaceConfig(
      String windowTitle,
      int minRule,
      int defaultRule,
      int minStates,
      int maxStates,
      int defaultStates) {
    this.windowTitle = windowTitle;
    this.minRule = minRule;
    this.defaultRule = defaultRule;
    this.minStates = minStates;
    this.maxStates = maxStates;
    this.defaultStates = defaultStates;
  }

  public String getWindowTitle() {
    return windowTitle;
  }

  public int getMinRule() {
    return minRule;
  }

  /**
   * Returns the maximum allowed rule number for a given state count.
   *
   * @param states the state count
   * @return the maximum rule
   */
  public abstract int getMaxRule(int states);

  public int getDefaultRule() {
    return defaultRule;
  }

  /**
   * Indicates if this workspace supports custom state counts, derived from whether the configured
   * minimum and maximum state counts differ.
   *
   * @return true if custom states are supported
   */
  public boolean supportsCustomStates() {
    return minStates != maxStates;
  }

  public int getMinStates() {
    return minStates;
  }

  public int getMaxStates() {
    return maxStates;
  }

  public int getDefaultStates() {
    return defaultStates;
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
