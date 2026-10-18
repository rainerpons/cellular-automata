package com.cellularautomata.engine.rules;

import java.util.Set;

/** Constants, domain validation, and notation formatting for Life-like cellular automata rules. */
public final class LifeLikeRule {
  /** Number of cell states in binary Life-like cellular automata (dead or alive). */
  public static final int STATE_COUNT = 2;

  /** Minimum possible number of living neighbors in a 2D Moore neighborhood. */
  public static final int MIN_NEIGHBOR_COUNT = 0;

  /** Maximum possible number of living neighbors in a 2D Moore neighborhood. */
  public static final int MAX_NEIGHBOR_COUNT = 8;

  private LifeLikeRule() {}

  /**
   * Indicates if a neighbor count is valid in a 2D Moore neighborhood.
   *
   * @param count the neighbor count
   * @return true if the count is between {@link #MIN_NEIGHBOR_COUNT} and {@link
   *     #MAX_NEIGHBOR_COUNT} (inclusive)
   */
  public static boolean isValidNeighborCount(int count) {
    return count >= MIN_NEIGHBOR_COUNT && count <= MAX_NEIGHBOR_COUNT;
  }

  /**
   * Validates that a set of neighbor counts is non-null and contains only valid neighbor counts (0
   * to 8).
   *
   * @param counts the neighbor counts to validate
   * @throws IllegalArgumentException if the set is null, contains null, or contains a count outside
   *     0 to 8
   */
  public static void validateNeighborCounts(Set<Integer> counts) {
    if (counts == null) {
      throw new IllegalArgumentException("Neighbor count set cannot be null.");
    }
    for (Integer count : counts) {
      if (count == null || !isValidNeighborCount(count)) {
        throw new IllegalArgumentException("Neighbor counts must be between 0 and 8.");
      }
    }
  }

  /**
   * Formats birth and survival neighbor count sets into standard B/S rule notation with counts in
   * ascending order (for example, {@code B3/S23} or {@code B2/S}).
   *
   * @param birthCounts the birth neighbor counts (0 to 8)
   * @param survivalCounts the survival neighbor counts (0 to 8)
   * @return the formatted B/S notation string
   * @throws IllegalArgumentException if either set is null, contains null, or contains a count
   *     outside 0 to 8
   */
  public static String formatNotation(Set<Integer> birthCounts, Set<Integer> survivalCounts) {
    return "B%s/S%s".formatted(formatCountSet(birthCounts), formatCountSet(survivalCounts));
  }

  private static String formatCountSet(Set<Integer> counts) {
    validateNeighborCounts(counts);
    StringBuilder builder = new StringBuilder();
    counts.stream().sorted().forEach(builder::append);
    return builder.toString();
  }
}
