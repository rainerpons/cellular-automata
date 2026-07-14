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
      Integer.MAX_VALUE, // Bounded dynamically in getMaxRule(int states)
      TotalisticRule.DEFAULT_RULE_NUMBER) {
    @Override
    public boolean supportsCustomStates() {
      return true;
    }

    @Override
    public int getMinStates() {
      return 2;
    }

    @Override
    public int getMaxStates() {
      return 5;
    }

    @Override
    public int getMaxRule(int states) {
      return TotalisticRule.calculateMaxRule(states);
    }
  };

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

  public int getMaxRule() {
    return maxRule;
  }

  /**
   * Returns the maximum allowed rule number for a given state count.
   *
   * @param states the state count
   * @return the maximum rule
   */
  public int getMaxRule(int states) {
    return maxRule;
  }

  public int getDefaultRule() {
    return defaultRule;
  }

  /**
   * Indicates if this workspace supports custom state counts.
   *
   * @return true if custom states are supported
   */
  public boolean supportsCustomStates() {
    return false;
  }

  /**
   * Returns the minimum allowed states for this workspace.
   *
   * @return the minimum states
   */
  public int getMinStates() {
    return 2;
  }

  /**
   * Returns the maximum allowed states for this workspace.
   *
   * @return the maximum states
   */
  public int getMaxStates() {
    return 2;
  }

  /**
   * Returns the default number of states for this workspace.
   *
   * @return the default states
   */
  public int getDefaultStates() {
    return 2;
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
