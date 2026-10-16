package com.cellularautomata.config;

import java.util.List;

/** Seed initialization types supported by cellular automata workspaces. */
public enum SeedType {
  /** Random initialization using a configurable alive-cell density percentage. */
  DENSITY("Density"),

  /**
   * Uniform random distribution across cell states (an independent 50/50 random alive/dead
   * distribution in binary Life-like automata, rather than all cells alive).
   */
  UNIFORM("Uniform"),

  /** Single active cell placed randomly among dead cells. */
  SPARSE("Sparse"),

  /** Alternating active and dead cells. */
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
