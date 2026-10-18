package com.cellularautomata.config;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.Rule;
import com.cellularautomata.engine.rules.TotalisticRule;

/** Defines the configuration and constraints for a cellular automata workspace. */
public enum WorkspaceConfig {
  /** Elementary cellular automata workspace configuration. */
  ELEMENTARY("Cellular Automata (Elementary)", ElementaryRule.STATE_COUNT),

  /** Totalistic cellular automata workspace configuration. */
  TOTALISTIC(
      "Cellular Automata (Totalistic)",
      TotalisticRule.MIN_STATES,
      TotalisticRule.MAX_STATES,
      TotalisticRule.DEFAULT_STATES),

  /** Life-like cellular automata workspace configuration. */
  LIFE_LIKE("Cellular Automata (Life-Like)", LifeLikeRule.STATE_COUNT);

  private static final String LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE =
      "Life-like rules use birth and survival counts";

  /** The application window title for this workspace. */
  private final String windowTitle;

  /** The minimum allowed number of states. */
  private final int minStates;

  /** The maximum allowed number of states. */
  private final int maxStates;

  /** The default number of states. */
  private final int defaultStates;

  WorkspaceConfig(String windowTitle, int stateCount) {
    this(windowTitle, stateCount, stateCount, stateCount);
  }

  WorkspaceConfig(String windowTitle, int minStates, int maxStates, int defaultStates) {
    this.windowTitle = windowTitle;
    this.minStates = minStates;
    this.maxStates = maxStates;
    this.defaultStates = defaultStates;
  }

  public String getWindowTitle() {
    return windowTitle;
  }

  /**
   * Returns the minimum allowed rule number for numeric-rule workspaces.
   *
   * @return the minimum rule number
   * @throws UnsupportedOperationException if this workspace does not use numeric rule numbers
   */
  public int getMinRule() {
    return switch (this) {
      case ELEMENTARY -> ElementaryRule.MIN_RULE_NUMBER;
      case TOTALISTIC -> TotalisticRule.MIN_RULE_NUMBER;
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE);
    };
  }

  /**
   * Returns the maximum allowed rule number for a given state count in numeric-rule workspaces.
   *
   * @param states the state count
   * @return the maximum rule number
   * @throws UnsupportedOperationException if this workspace does not use numeric rule numbers
   */
  public int getMaxRule(int states) {
    return switch (this) {
      case ELEMENTARY -> ElementaryRule.MAX_RULE_NUMBER;
      case TOTALISTIC -> TotalisticRule.calculateMaxRule(states);
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE);
    };
  }

  /**
   * Returns the default rule number for numeric-rule workspaces.
   *
   * @return the default rule number
   * @throws UnsupportedOperationException if this workspace does not use numeric rule numbers
   */
  public int getDefaultRule() {
    return switch (this) {
      case ELEMENTARY -> ElementaryRule.DEFAULT_RULE_NUMBER;
      case TOTALISTIC -> TotalisticRule.DEFAULT_RULE_NUMBER;
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE);
    };
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
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE);
    };
  }
}
