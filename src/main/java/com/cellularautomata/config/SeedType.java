package com.cellularautomata.config;

/** Seed initialization types supported by the Life-like cellular automata workspace. */
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

  @Override
  public String toString() {
    return displayName;
  }
}
