package com.cellularautomata.ui;

/** Defines the metadata and capabilities for a cellular automata workspace. */
public enum WorkspaceConfig {
  /** Elementary cellular automata workspace configuration. */
  ELEMENTARY("Cellular Automata (Elementary)", false),

  /** Totalistic cellular automata workspace configuration. */
  TOTALISTIC("Cellular Automata (Totalistic)", true),

  /** Life-like cellular automata workspace configuration. */
  LIFE_LIKE("Cellular Automata (Life-Like)", false);

  /** The application window title for this workspace. */
  private final String windowTitle;

  /** Whether this workspace supports user-configurable state counts. */
  private final boolean customStatesSupported;

  WorkspaceConfig(String windowTitle, boolean customStatesSupported) {
    this.windowTitle = windowTitle;
    this.customStatesSupported = customStatesSupported;
  }

  public String getWindowTitle() {
    return windowTitle;
  }

  public boolean supportsCustomStates() {
    return customStatesSupported;
  }
}
