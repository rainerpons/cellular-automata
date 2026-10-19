package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import java.util.Set;
import java.util.TreeSet;

/**
 * JavaFX-free presentation model managing preset and birth/survival neighbor count selection state
 * for the Life-like workspace. Enforces the UI-level two-selection cap per set separately from the
 * domain neighbor count range ({@code 0..8}) defined in {@link LifeLikeRule}.
 */
final class LifeLikeRuleSelectionModel {
  /**
   * Intentional UI cap limiting selection to at most two birth counts and two survival counts for
   * the current workspace scope. Kept in the UI layer rather than {@link LifeLikeRule} or {@link
   * LifeLikePreset} because general Life-like cellular automata permit any subset of 0..8.
   */
  static final int MAX_SELECTED_COUNTS_PER_SET = 2;

  private final Set<Integer> birthCounts = new TreeSet<>();
  private final Set<Integer> survivalCounts = new TreeSet<>();

  LifeLikeRuleSelectionModel() {
    selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
  }

  LifeLikePreset getPreset() {
    return LifeLikePreset.fromCounts(birthCounts, survivalCounts).orElse(null);
  }

  boolean isCustom() {
    return getPreset() == null;
  }

  Set<Integer> getBirthCounts() {
    return Set.copyOf(birthCounts);
  }

  Set<Integer> getSurvivalCounts() {
    return Set.copyOf(survivalCounts);
  }

  boolean isBirthSelected(int neighborCount) {
    validateNeighborCount(neighborCount);
    return birthCounts.contains(neighborCount);
  }

  boolean isSurvivalSelected(int neighborCount) {
    validateNeighborCount(neighborCount);
    return survivalCounts.contains(neighborCount);
  }

  boolean isBirthToggleDisabled(int neighborCount) {
    return !isBirthSelected(neighborCount) && birthCounts.size() >= MAX_SELECTED_COUNTS_PER_SET;
  }

  boolean isSurvivalToggleDisabled(int neighborCount) {
    return !isSurvivalSelected(neighborCount)
        && survivalCounts.size() >= MAX_SELECTED_COUNTS_PER_SET;
  }

  void selectPreset(LifeLikePreset preset) {
    if (preset == null) {
      throw new IllegalArgumentException("Preset cannot be null.");
    }
    birthCounts.clear();
    birthCounts.addAll(preset.getBirthCounts());
    survivalCounts.clear();
    survivalCounts.addAll(preset.getSurvivalCounts());
  }

  boolean setBirthSelected(int neighborCount, boolean selected) {
    return updateSelection(birthCounts, neighborCount, selected);
  }

  boolean setSurvivalSelected(int neighborCount, boolean selected) {
    return updateSelection(survivalCounts, neighborCount, selected);
  }

  boolean toggleBirth(int neighborCount) {
    return setBirthSelected(neighborCount, !isBirthSelected(neighborCount));
  }

  boolean toggleSurvival(int neighborCount) {
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
