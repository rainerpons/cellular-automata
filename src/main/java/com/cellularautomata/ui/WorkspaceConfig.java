package com.cellularautomata.ui;

/** Defines the metadata for a cellular automata workspace. */
public enum WorkspaceConfig {
  /** Elementary cellular automata workspace configuration. */
  ELEMENTARY("Cellular Automata (Elementary)"),

  /** Totalistic cellular automata workspace configuration. */
  TOTALISTIC("Cellular Automata (Totalistic)"),

  /** Life-like cellular automata workspace configuration. */
  LIFE_LIKE("Cellular Automata (Life-Like)");

  /** The application window title for this workspace. */
  private final String windowTitle;

  WorkspaceConfig(String windowTitle) {
    this.windowTitle = windowTitle;
  }

  public String getWindowTitle() {
    return windowTitle;
  }
}
