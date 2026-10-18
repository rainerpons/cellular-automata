package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/** JavaFX-free unit tests for {@link LifeLikeRuleSelectionModel}. */
public class LifeLikeRuleSelectionModelTest {

  @Test
  public void testDefaultStateMatchesConwaysGameOfLife() {
    LifeLikeRuleSelectionModel model = new LifeLikeRuleSelectionModel();

    Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, model.getPreset());
    Assert.assertFalse(model.isCustom());
    Assert.assertEquals(Set.of(3), model.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), model.getSurvivalCounts());

    for (int count = LifeLikeRule.MIN_NEIGHBOR_COUNT;
        count <= LifeLikeRule.MAX_NEIGHBOR_COUNT;
        count++) {
      Assert.assertEquals(count == 3, model.isBirthSelected(count));
      Assert.assertFalse(model.isBirthToggleDisabled(count));

      boolean expectedSurvival = count == 2 || count == 3;
      Assert.assertEquals(expectedSurvival, model.isSurvivalSelected(count));
      Assert.assertEquals(!expectedSurvival, model.isSurvivalToggleDisabled(count));
    }
  }

  @Test
  public void testSelectingNamedPresetsUpdatesCountsAndDisabledState() {
    LifeLikeRuleSelectionModel model = new LifeLikeRuleSelectionModel();

    model.selectPreset(LifeLikePreset.HIGH_LIFE);
    Assert.assertEquals(LifeLikePreset.HIGH_LIFE, model.getPreset());
    Assert.assertFalse(model.isCustom());
    Assert.assertEquals(Set.of(3, 6), model.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), model.getSurvivalCounts());
    Assert.assertTrue(model.isBirthToggleDisabled(0));
    Assert.assertFalse(model.isBirthToggleDisabled(3));
    Assert.assertFalse(model.isBirthToggleDisabled(6));

    model.selectPreset(LifeLikePreset.SEEDS);
    Assert.assertEquals(LifeLikePreset.SEEDS, model.getPreset());
    Assert.assertFalse(model.isCustom());
    Assert.assertEquals(Set.of(2), model.getBirthCounts());
    Assert.assertEquals(Set.of(), model.getSurvivalCounts());
    Assert.assertFalse(model.isBirthToggleDisabled(0));
    Assert.assertFalse(model.isSurvivalToggleDisabled(0));

    model.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
    Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, model.getPreset());
    Assert.assertFalse(model.isCustom());
    Assert.assertEquals(Set.of(3), model.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), model.getSurvivalCounts());
  }

  @Test
  public void testCustomStateWhenCountsDoNotMatchPreset() {
    LifeLikeRuleSelectionModel model = new LifeLikeRuleSelectionModel();
    model.selectPreset(LifeLikePreset.HIGH_LIFE);
    Assert.assertFalse(model.isCustom());

    // Toggle to a non-preset combination: B/S23
    model.toggleBirth(3);
    model.toggleBirth(6);
    Assert.assertNull(model.getPreset());
    Assert.assertTrue(model.isCustom());
    Assert.assertEquals(Set.of(), model.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), model.getSurvivalCounts());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSelectPresetRejectsNull() {
    new LifeLikeRuleSelectionModel().selectPreset(null);
  }

  @Test
  public void testManualTogglesSynchronizePresetToMatchingOrCustom() {
    LifeLikeRuleSelectionModel model = new LifeLikeRuleSelectionModel();

    // B3/S23 + Birth 6 -> B36/S23 (HighLife)
    Assert.assertTrue(model.toggleBirth(6));
    Assert.assertEquals(LifeLikePreset.HIGH_LIFE, model.getPreset());
    Assert.assertFalse(model.isCustom());
    Assert.assertEquals(Set.of(3, 6), model.getBirthCounts());

    // Deselect Birth 6 and 3 -> B/S23 (Custom)
    Assert.assertTrue(model.toggleBirth(6));
    Assert.assertTrue(model.toggleBirth(3));
    Assert.assertNull(model.getPreset());
    Assert.assertTrue(model.isCustom());
    Assert.assertEquals(Set.of(), model.getBirthCounts());

    // Select Birth 2 and deselect Survival 2 and 3 -> B2/S (Seeds)
    Assert.assertTrue(model.setBirthSelected(2, true));
    Assert.assertTrue(model.setSurvivalSelected(2, false));
    Assert.assertTrue(model.setSurvivalSelected(3, false));
    Assert.assertEquals(LifeLikePreset.SEEDS, model.getPreset());
    Assert.assertFalse(model.isCustom());
  }

  @Test
  public void testTwoSelectionCapPreventsThirdSelectionAndUpdatesDisabledState() {
    LifeLikeRuleSelectionModel model = new LifeLikeRuleSelectionModel();

    // Survival starts with {2, 3} (at cap of 2). Selecting a 3rd count is rejected.
    Assert.assertFalse(model.setSurvivalSelected(4, true));
    Assert.assertFalse(model.toggleSurvival(4));
    Assert.assertEquals(Set.of(2, 3), model.getSurvivalCounts());
    Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, model.getPreset());

    // Re-selecting an already selected count is permitted and idempotent.
    Assert.assertTrue(model.setSurvivalSelected(2, true));
    Assert.assertEquals(Set.of(2, 3), model.getSurvivalCounts());

    // Deselecting Survival 3 frees a slot and re-enables unselected toggles.
    Assert.assertTrue(model.toggleSurvival(3));
    Assert.assertFalse(model.isSurvivalToggleDisabled(4));
    Assert.assertTrue(model.toggleSurvival(4));
    Assert.assertEquals(Set.of(2, 4), model.getSurvivalCounts());
    Assert.assertTrue(model.isSurvivalToggleDisabled(0));
  }

  @Test
  public void testZeroAndMaxNeighborCountsAreSupportedAndDistinctFromEmptySet() {
    LifeLikeRuleSelectionModel model = new LifeLikeRuleSelectionModel();

    Assert.assertTrue(model.toggleBirth(3));
    Assert.assertEquals(Set.of(), model.getBirthCounts());

    Assert.assertTrue(model.toggleBirth(0));
    Assert.assertEquals(Set.of(0), model.getBirthCounts());

    Assert.assertTrue(model.toggleBirth(8));
    Assert.assertEquals(Set.of(0, 8), model.getBirthCounts());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeNeighborCountThrowsException() {
    new LifeLikeRuleSelectionModel().toggleBirth(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNeighborCountAboveMaxThrowsException() {
    new LifeLikeRuleSelectionModel().toggleSurvival(9);
  }
}
