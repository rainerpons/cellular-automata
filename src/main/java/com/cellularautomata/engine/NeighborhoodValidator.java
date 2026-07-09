package com.cellularautomata.engine;

/** Utility class for validating neighborhoods. */
public final class NeighborhoodValidator {

  private NeighborhoodValidator() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Validates that a neighborhood is exactly 3 characters long and contains only binary digits (0
   * or 1).
   *
   * @param neighborhood the neighborhood string to validate
   * @throws IllegalArgumentException if the neighborhood is null, not 3 characters long, or
   *     contains non-binary digits
   */
  public static void validateBinaryRadiusOne(String neighborhood) {
    if (neighborhood == null || neighborhood.length() != 3) {
      throw new IllegalArgumentException("Neighborhood must be exactly 3 characters");
    }
    for (int i = 0; i < neighborhood.length(); i++) {
      char c = neighborhood.charAt(i);
      if (c != '0' && c != '1') {
        throw new IllegalArgumentException("Neighborhood state must be binary (0 or 1)");
      }
    }
  }
}
