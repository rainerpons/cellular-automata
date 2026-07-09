package com.cellularautomata.config;

/** Defines the configuration and constraints for a cellular automata workspace. */
public enum WorkspaceConfig {
  /** Elementary cellular automata workspace configuration. */
  ELEMENTARY("Cellular Automata (Elementary)", 0, 255, 30, true),

  /** Totalistic cellular automata workspace configuration. */
  TOTALISTIC("Cellular Automata (Totalistic)", 0, 15, 0, false);

  private final String windowTitle;
  private final int minRule;
  private final int maxRule;
  private final int defaultRule;
  private final boolean enableGeneration;

  WorkspaceConfig(
      String windowTitle, int minRule, int maxRule, int defaultRule, boolean enableGeneration) {
    this.windowTitle = windowTitle;
    this.minRule = minRule;
    this.maxRule = maxRule;
    this.defaultRule = defaultRule;
    this.enableGeneration = enableGeneration;
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
   * @return true if generation is enabled, false otherwise.
   */
  public boolean isEnableGeneration() {
    return enableGeneration;
  }
}
