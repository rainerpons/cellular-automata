package com.cellularautomata.engine.rules;

import java.util.Set;

/**
 * Mathematical birth and survival rule presets for Life-like cellular automata (distinct from
 * workspace presets). Each named preset defines a fixed set of birth and survival neighbor counts
 * in the range 0 to 8.
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
   * Resolves the preset matching the given birth and survival neighbor count sets, or {@link
   * #CUSTOM} if no named preset matches.
   *
   * @param birthCounts the selected birth neighbor counts (0 to 8)
   * @param survivalCounts the selected survival neighbor counts (0 to 8)
   * @return the matching preset or {@link #CUSTOM}
   * @throws IllegalArgumentException if either set is null, contains null, or contains a count
   *     outside 0 to 8
   */
  public static LifeLikePreset fromCounts(Set<Integer> birthCounts, Set<Integer> survivalCounts) {
    LifeLikeRule.validateNeighborCounts(birthCounts);
    LifeLikeRule.validateNeighborCounts(survivalCounts);
    for (LifeLikePreset preset : values()) {
      if (!preset.isCustom()
          && preset.birthCounts.equals(birthCounts)
          && preset.survivalCounts.equals(survivalCounts)) {
        return preset;
      }
    }
    return CUSTOM;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
