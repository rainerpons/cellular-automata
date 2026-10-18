package com.cellularautomata.engine.rules;

import java.util.Objects;
import java.util.Set;

/**
 * Domain model, domain validation, and notation formatting for Life-like cellular automata rules.
 */
public final class LifeLikeRule {
  /** Number of cell states in binary Life-like cellular automata (dead or alive). */
  public static final int STATE_COUNT = 2;

  /** Minimum possible number of living neighbors in a 2D Moore neighborhood. */
  public static final int MIN_NEIGHBOR_COUNT = 0;

  /** Maximum possible number of living neighbors in a 2D Moore neighborhood. */
  public static final int MAX_NEIGHBOR_COUNT = 8;

  public static final LifeLikeRule CONWAYS_GAME_OF_LIFE =
      new LifeLikeRule(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
  public static final LifeLikeRule HIGH_LIFE = new LifeLikeRule(LifeLikePreset.HIGH_LIFE);
  public static final LifeLikeRule SEEDS = new LifeLikeRule(LifeLikePreset.SEEDS);

  private final Set<Integer> birthCounts;
  private final Set<Integer> survivalCounts;
  private final boolean[] birthTable;
  private final boolean[] survivalTable;
  private final String notation;

  /**
   * Constructs a Life-like rule from the specified birth and survival neighbor count sets.
   *
   * @param birthCounts set of neighbor counts that cause a dead cell to become alive (0 to 8)
   * @param survivalCounts set of neighbor counts that cause a live cell to stay alive (0 to 8)
   * @throws IllegalArgumentException if either set is null, contains null, or contains an invalid
   *     count
   */
  public LifeLikeRule(Set<Integer> birthCounts, Set<Integer> survivalCounts) {
    validateNeighborCounts(birthCounts);
    validateNeighborCounts(survivalCounts);
    this.birthCounts = Set.copyOf(birthCounts);
    this.survivalCounts = Set.copyOf(survivalCounts);
    this.birthTable = new boolean[MAX_NEIGHBOR_COUNT + 1];
    for (int count : this.birthCounts) {
      this.birthTable[count] = true;
    }
    this.survivalTable = new boolean[MAX_NEIGHBOR_COUNT + 1];
    for (int count : this.survivalCounts) {
      this.survivalTable[count] = true;
    }
    this.notation = formatNotation(this.birthCounts, this.survivalCounts);
  }

  /**
   * Constructs a Life-like rule from a predefined {@link LifeLikePreset}.
   *
   * @param preset the preset to initialize from
   * @throws IllegalArgumentException if preset is null
   */
  public LifeLikeRule(LifeLikePreset preset) {
    this(requirePreset(preset).getBirthCounts(), preset.getSurvivalCounts());
  }

  private static LifeLikePreset requirePreset(LifeLikePreset preset) {
    if (preset == null) {
      throw new IllegalArgumentException("Preset cannot be null.");
    }
    return preset;
  }

  /**
   * Evaluates the next state of a cell given its current boolean state and live neighbor count.
   *
   * @param currentlyAlive true if the cell is currently alive, false if dead
   * @param liveNeighborCount the number of live neighbors in the cell's Moore neighborhood (0 to 8)
   * @return true if the cell is alive in the next generation, false otherwise
   * @throws IllegalArgumentException if liveNeighborCount is outside 0 to 8
   */
  public boolean evaluate(boolean currentlyAlive, int liveNeighborCount) {
    if (!isValidNeighborCount(liveNeighborCount)) {
      throw new IllegalArgumentException(
          "Neighbor count must be between "
              + MIN_NEIGHBOR_COUNT
              + " and "
              + MAX_NEIGHBOR_COUNT
              + ": "
              + liveNeighborCount);
    }
    return currentlyAlive ? survivalTable[liveNeighborCount] : birthTable[liveNeighborCount];
  }

  /**
   * Evaluates the next state of a cell given its current integer state (0 or 1) and live neighbor
   * count.
   *
   * @param currentState 0 for dead, 1 for alive
   * @param liveNeighborCount the number of live neighbors in the cell's Moore neighborhood (0 to 8)
   * @return 1 if the cell is alive in the next generation, 0 if dead
   * @throws IllegalArgumentException if currentState is not 0 or 1, or liveNeighborCount is outside
   *     0 to 8
   */
  public int evaluate(int currentState, int liveNeighborCount) {
    if (currentState != 0 && currentState != 1) {
      throw new IllegalArgumentException("Cell state must be 0 or 1: " + currentState);
    }
    return evaluate(currentState == 1, liveNeighborCount) ? 1 : 0;
  }

  public boolean isBirth(int count) {
    return isValidNeighborCount(count) && birthTable[count];
  }

  public boolean isSurvival(int count) {
    return isValidNeighborCount(count) && survivalTable[count];
  }

  public Set<Integer> getBirthCounts() {
    return birthCounts;
  }

  public Set<Integer> getSurvivalCounts() {
    return survivalCounts;
  }

  public String getNotation() {
    return notation;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LifeLikeRule other)) {
      return false;
    }
    return birthCounts.equals(other.birthCounts) && survivalCounts.equals(other.survivalCounts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(birthCounts, survivalCounts);
  }

  @Override
  public String toString() {
    return notation;
  }
}
