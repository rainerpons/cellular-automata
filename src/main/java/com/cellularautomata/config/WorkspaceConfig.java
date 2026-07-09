package com.cellularautomata.config;

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

  /**
   * Gets the application window title for this workspace.
   *
   * @return the application window title for this workspace
   */
  public String getWindowTitle() {
    return windowTitle;
  }

  /**
   * Gets the minimum allowed rule number.
   *
   * @return the minimum allowed rule number
   */
  public int getMinRule() {
    return minRule;
  }

  /**
   * Gets the maximum allowed rule number.
   *
   * @return the maximum allowed rule number
   */
  public int getMaxRule() {
    return maxRule;
  }

  /**
   * Gets the default rule number.
   *
   * @return the default rule number
   */
  public int getDefaultRule() {
    return defaultRule;
  }

  /**
   * Indicates whether the workspace supports generation yet.
   *
   * @return true if generation is supported, false otherwise.
   */
  public boolean supportsGeneration() {
    return supportsGeneration;
  }
}
