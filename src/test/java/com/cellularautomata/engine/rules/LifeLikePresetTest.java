package com.cellularautomata.engine.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link LifeLikePreset}. */
public class LifeLikePresetTest {

  @Test
  public void testPresetMetadataAndCounts() {
    Assert.assertEquals(
        "Conway's Game of Life", LifeLikePreset.CONWAYS_GAME_OF_LIFE.getDisplayName());
    Assert.assertEquals("Conway's Game of Life", LifeLikePreset.CONWAYS_GAME_OF_LIFE.toString());
    Assert.assertEquals(Set.of(3), LifeLikePreset.CONWAYS_GAME_OF_LIFE.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), LifeLikePreset.CONWAYS_GAME_OF_LIFE.getSurvivalCounts());
    Assert.assertFalse(LifeLikePreset.CONWAYS_GAME_OF_LIFE.isCustom());

    Assert.assertEquals("HighLife", LifeLikePreset.HIGH_LIFE.getDisplayName());
    Assert.assertEquals(Set.of(3, 6), LifeLikePreset.HIGH_LIFE.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), LifeLikePreset.HIGH_LIFE.getSurvivalCounts());
    Assert.assertFalse(LifeLikePreset.HIGH_LIFE.isCustom());

    Assert.assertEquals("Seeds", LifeLikePreset.SEEDS.getDisplayName());
    Assert.assertEquals(Set.of(2), LifeLikePreset.SEEDS.getBirthCounts());
    Assert.assertEquals(Set.of(), LifeLikePreset.SEEDS.getSurvivalCounts());
    Assert.assertFalse(LifeLikePreset.SEEDS.isCustom());

    Assert.assertEquals("Custom", LifeLikePreset.CUSTOM.getDisplayName());
    Assert.assertTrue(LifeLikePreset.CUSTOM.isCustom());
  }

  @Test
  public void testFromCountsResolvesMatchingPresetOrCustom() {
    Assert.assertEquals(
        LifeLikePreset.CONWAYS_GAME_OF_LIFE, LifeLikePreset.fromCounts(Set.of(3), Set.of(2, 3)));
    Assert.assertEquals(
        LifeLikePreset.HIGH_LIFE, LifeLikePreset.fromCounts(Set.of(3, 6), Set.of(2, 3)));
    Assert.assertEquals(LifeLikePreset.SEEDS, LifeLikePreset.fromCounts(Set.of(2), Set.of()));
    Assert.assertEquals(LifeLikePreset.CUSTOM, LifeLikePreset.fromCounts(Set.of(), Set.of()));
    Assert.assertEquals(
        LifeLikePreset.CUSTOM, LifeLikePreset.fromCounts(Set.of(0, 3), Set.of(2, 3)));
    Assert.assertEquals(LifeLikePreset.CUSTOM, LifeLikePreset.fromCounts(Set.of(3), Set.of(0, 2)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromCountsRejectsNullSet() {
    LifeLikePreset.fromCounts(null, Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromCountsRejectsNullElement() {
    Set<Integer> countsWithNull = new HashSet<>(Arrays.asList(2, null));
    LifeLikePreset.fromCounts(countsWithNull, Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromCountsRejectsNegativeCount() {
    LifeLikePreset.fromCounts(Set.of(-1), Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromCountsRejectsOutOfRangeCount() {
    LifeLikePreset.fromCounts(Set.of(3), Set.of(9));
  }
}
