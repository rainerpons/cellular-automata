package com.cellularautomata.ui;

import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import java.util.Set;
import java.util.TreeSet;

/**
 * JavaFX-free presentation model managing preset and birth/survival neighbor count selection state
 * for the Life-like workspace. Enforces the UI-level two-selection cap per set separately from the
 * domain neighbor count range ({@code 0..8}) defined in {@link LifeLikeRule}.
 */
public final class LifeLikeRuleSelectionModel {
  /**
   * Intentional UI cap limiting selection to at most two birth counts and two survival counts for
   * the current workspace scope. Kept in the UI layer rather than {@link LifeLikeRule} or {@link
   * LifeLikePreset} because general Life-like cellular automata permit any subset of 0..8.
   */
  public static final int MAX_SELECTED_COUNTS_PER_SET = 2;

  private final Set<Integer> birthCounts = new TreeSet<>();
  private final Set<Integer> survivalCounts = new TreeSet<>();

  /** Constructs the selection model initialized with Conway's Game of Life. */
  public LifeLikeRuleSelectionModel() {
    selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
  }

  public LifeLikePreset getPreset() {
    return LifeLikePreset.fromCounts(birthCounts, survivalCounts).orElse(null);
  }

  public boolean isCustom() {
    return getPreset() == null;
  }

  public Set<Integer> getBirthCounts() {
    return Set.copyOf(birthCounts);
  }

  public Set<Integer> getSurvivalCounts() {
    return Set.copyOf(survivalCounts);
  }

  public boolean isBirthSelected(int neighborCount) {
    validateNeighborCount(neighborCount);
    return birthCounts.contains(neighborCount);
  }

  public boolean isSurvivalSelected(int neighborCount) {
    validateNeighborCount(neighborCount);
    return survivalCounts.contains(neighborCount);
  }

  public boolean isBirthToggleDisabled(int neighborCount) {
    return !isBirthSelected(neighborCount) && birthCounts.size() >= MAX_SELECTED_COUNTS_PER_SET;
  }

  public boolean isSurvivalToggleDisabled(int neighborCount) {
    return !isSurvivalSelected(neighborCount)
        && survivalCounts.size() >= MAX_SELECTED_COUNTS_PER_SET;
  }

  /**
   * Applies the birth and survival neighbor counts of the specified preset.
   *
   * @param preset the preset to apply
   * @throws IllegalArgumentException if preset is null
   */
  public void selectPreset(LifeLikePreset preset) {
    if (preset == null) {
      throw new IllegalArgumentException("Preset cannot be null.");
    }
    birthCounts.clear();
    birthCounts.addAll(preset.getBirthCounts());
    survivalCounts.clear();
    survivalCounts.addAll(preset.getSurvivalCounts());
  }

  public boolean setBirthSelected(int neighborCount, boolean selected) {
    return updateSelection(birthCounts, neighborCount, selected);
  }

  public boolean setSurvivalSelected(int neighborCount, boolean selected) {
    return updateSelection(survivalCounts, neighborCount, selected);
  }

  /**
   * Toggles the selection state of the specified birth neighbor count.
   *
   * @param neighborCount the neighbor count (0 to 8)
   * @return true if the toggle change was accepted, false if blocked by the selection cap
   */
  public boolean toggleBirth(int neighborCount) {
    return setBirthSelected(neighborCount, !isBirthSelected(neighborCount));
  }

  /**
   * Toggles the selection state of the specified survival neighbor count.
   *
   * @param neighborCount the neighbor count (0 to 8)
   * @return true if the toggle change was accepted, false if blocked by the selection cap
   */
  public boolean toggleSurvival(int neighborCount) {
    return setSurvivalSelected(neighborCount, !isSurvivalSelected(neighborCount));
  }

  private boolean updateSelection(Set<Integer> targetCounts, int neighborCount, boolean selected) {
    validateNeighborCount(neighborCount);
    if (selected
        && !targetCounts.contains(neighborCount)
        && targetCounts.size() >= MAX_SELECTED_COUNTS_PER_SET) {
      return false;
    }
    if (selected) {
      targetCounts.add(neighborCount);
    } else {
      targetCounts.remove(neighborCount);
    }
    return true;
  }

  private static void validateNeighborCount(int neighborCount) {
    if (!LifeLikeRule.isValidNeighborCount(neighborCount)) {
      throw new IllegalArgumentException("Neighbor count must be between 0 and 8.");
    }
  }
}
