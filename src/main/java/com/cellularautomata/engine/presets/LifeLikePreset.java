package com.cellularautomata.engine.presets;

import com.cellularautomata.engine.rules.LifeLikeRule;
import java.util.List;
import java.util.Optional;
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
  SEEDS("Seeds", Set.of(2), Set.of());

  private static final List<LifeLikePreset> PRESETS = List.of(values());

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
    return birthCounts;
  }

  public Set<Integer> getSurvivalCounts() {
    return survivalCounts;
  }

  /**
   * Converts this preset into an immutable {@link LifeLikeRule} instance.
   *
   * @return a new rule instance configured with this preset's neighbor counts
   */
  public LifeLikeRule toRule() {
    return new LifeLikeRule(birthCounts, survivalCounts);
  }

  /**
   * Resolves the preset matching the given birth and survival neighbor count sets, if any.
   *
   * @param birthCounts the selected birth neighbor counts (0 to 8)
   * @param survivalCounts the selected survival neighbor counts (0 to 8)
   * @return an {@link Optional} containing the matching preset, or empty if no named preset matches
   * @throws IllegalArgumentException if either set is null, contains null, or contains a count
   *     outside 0 to 8
   */
  public static Optional<LifeLikePreset> fromCounts(
      Set<Integer> birthCounts, Set<Integer> survivalCounts) {
    LifeLikeRule.validateNeighborCounts(birthCounts);
    LifeLikeRule.validateNeighborCounts(survivalCounts);
    for (LifeLikePreset preset : PRESETS) {
      if (preset.birthCounts.equals(birthCounts) && preset.survivalCounts.equals(survivalCounts)) {
        return Optional.of(preset);
      }
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    return displayName;
  }
}
