package com.cellularautomata.config;

import java.util.Set;

/**
 * Predefined and custom rule presets for the Life-like cellular automata
 * workspace. Each named
 * preset defines a fixed set of birth and survival neighbor counts in the range
 * 0 to 8.
 */
public enum LifeLikePreset {
  /** Conway's Game of Life (B3/S23). */
  CONWAYS_GAME_OF_LIFE("Conway's Game of Life", Set.of(3), Set.of(2, 3)),

  /** HighLife (B36/S23). */
  HIGH_LIFE("HighLife", Set.of(3, 6), Set.of(2, 3)),

  /** Seeds (B2/S). */
  SEEDS("Seeds", Set.of(2), Set.of()),

  /** Custom user-configured birth and survival counts. */
  CUSTOM("Custom", Set.of(), Set.of());

  private static final int MIN_NEIGHBOR_COUNT = 0;
  private static final int MAX_NEIGHBOR_COUNT = 8;

  private final String displayName;
  private final Set<Integer> birthCounts;
  private final Set<Integer> survivalCounts;

  LifeLikePreset(String displayName, Set<Integer> birthCounts, Set<Integer> survivalCounts) {
    this.displayName = displayName;
    this.birthCounts = Set.copyOf(birthCounts);
    this.survivalCounts = Set.copyOf(survivalCounts);
  }

  public String getDisplayName() {
    return displayName;
  }

  public Set<Integer> getBirthCounts() {
    return Set.copyOf(birthCounts);
  }

  public Set<Integer> getSurvivalCounts() {
    return Set.copyOf(survivalCounts);
  }

  public boolean isCustom() {
    return this == CUSTOM;
  }

  /**
   * Resolves the preset matching the given birth and survival neighbor count
   * sets, or {@link
   * #CUSTOM} if no named preset matches.
   *
   * @param birthCounts    the selected birth neighbor counts
   * @param survivalCounts the selected survival neighbor counts
   * @return the matching preset or {@link #CUSTOM}
   */
  public static LifeLikePreset fromCounts(Set<Integer> birthCounts, Set<Integer> survivalCounts) {
    if (birthCounts == null || survivalCounts == null) {
      throw new IllegalArgumentException("Birth and survival count sets cannot be null.");
    }

    for (LifeLikePreset preset : values()) {
      if (!preset.isCustom()
          && preset.birthCounts.equals(birthCounts)
          && preset.survivalCounts.equals(survivalCounts)) {
        return preset;
      }
    }

    return CUSTOM;
  }

  /**
   * Formats birth and survival neighbor count sets into standard B/S rule
   * notation with counts in
   * ascending order (for example, {@code B3/S23} or {@code B2/S}).
   *
   * @param birthCounts    the birth neighbor counts (0 to 8)
   * @param survivalCounts the survival neighbor counts (0 to 8)
   * @return the formatted B/S notation string
   */
  public static String formatNotation(Set<Integer> birthCounts, Set<Integer> survivalCounts) {
    return "B%s/S%s".formatted(formatCountSet(birthCounts), formatCountSet(survivalCounts));
  }

  private static String formatCountSet(Set<Integer> counts) {
    validateCountSet(counts);

    StringBuilder builder = new StringBuilder();
    counts
        .stream()
        .sorted()
        .forEach(builder::append);

    return builder.toString();
  }

  private static void validateCountSet(Set<Integer> counts) {
    if (counts == null) {
      throw new IllegalArgumentException("Neighbor count set cannot be null.");
    }

    for (Integer count : counts) {
      if (count == null || count < MIN_NEIGHBOR_COUNT || count > MAX_NEIGHBOR_COUNT) {
        throw new IllegalArgumentException("Neighbor counts must be between 0 and 8.");
      }
    }
  }

  @Override
  public String toString() {
    return displayName;
  }
}
