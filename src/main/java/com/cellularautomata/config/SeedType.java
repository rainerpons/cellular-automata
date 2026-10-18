package com.cellularautomata.config;

import java.util.List;

/** Initial cell pattern types selectable across cellular automata workspaces. */
public enum SeedType {
  /** Random pattern populated according to a selected alive-cell percentage. */
  DENSITY("Density"),

  /** Random pattern with an equal likelihood of each cell state. */
  UNIFORM("Uniform"),

  /** Pattern containing a single active cell among inactive cells. */
  SPARSE("Sparse"),

  /** Pattern alternating between active and inactive cell states. */
  ALTERNATING("Alternating");

  private final String displayName;

  SeedType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  /**
   * Returns the seed types supported by one-dimensional (Elementary and Totalistic) workspaces.
   *
   * @return an unmodifiable list of 1D seed types
   */
  public static List<SeedType> oneDimensionalValues() {
    return List.of(UNIFORM, SPARSE, ALTERNATING);
  }

  @Override
  public String toString() {
    return displayName;
  }
}
