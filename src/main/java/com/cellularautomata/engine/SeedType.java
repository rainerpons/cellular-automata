package com.cellularautomata.engine;

import java.util.List;

/** Initial cell pattern types selectable across cellular automata workspaces. */
public enum SeedType {
  /** Random pattern populated according to a selected alive-cell percentage. */
  DENSITY("density", "Density"),

  /** Random pattern with an equal likelihood of each cell state. */
  UNIFORM("uniform", "Uniform"),

  /** Pattern containing a single active cell among inactive cells. */
  SPARSE("sparse", "Sparse"),

  /** Pattern alternating between active and inactive cell states. */
  ALTERNATING("alternating", "Alternating");

  private final String identifier;
  private final String displayName;

  SeedType(String identifier, String displayName) {
    this.identifier = identifier;
    this.displayName = displayName;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
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

  /**
   * Returns the seed types supported by two-dimensional (Life-like) workspaces.
   *
   * @return an unmodifiable list of 2D seed types
   */
  public static List<SeedType> twoDimensionalValues() {
    return List.of(DENSITY, UNIFORM, SPARSE, ALTERNATING);
  }

  /**
   * Resolves a {@link SeedType} from a case-insensitive match against its identifier, enum name, or
   * display name.
   *
   * @param value the string identifier to match
   * @return the matching {@link SeedType}
   * @throws IllegalArgumentException if value is null or unrecognized
   */
  public static SeedType fromIdentifier(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Seed type cannot be null.");
    }
    for (SeedType seedType : values()) {
      if (seedType.identifier.equalsIgnoreCase(value)
          || seedType.name().equalsIgnoreCase(value)
          || seedType.displayName.equalsIgnoreCase(value)) {
        return seedType;
      }
    }
    throw new IllegalArgumentException("Unsupported seed type: %s".formatted(value));
  }
}
