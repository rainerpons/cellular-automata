package com.cellularautomata.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

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
  public void testFormatNotationForPresetsAndEdgeCases() {
    Assert.assertEquals("B3/S23", LifeLikePreset.formatNotation(Set.of(3), Set.of(2, 3)));
    Assert.assertEquals("B36/S23", LifeLikePreset.formatNotation(Set.of(6, 3), Set.of(3, 2)));
    Assert.assertEquals("B2/S", LifeLikePreset.formatNotation(Set.of(2), Set.of()));
    Assert.assertEquals("B/S", LifeLikePreset.formatNotation(Set.of(), Set.of()));
    Assert.assertEquals("B0/S", LifeLikePreset.formatNotation(Set.of(0), Set.of()));
    Assert.assertEquals("B/S0", LifeLikePreset.formatNotation(Set.of(), Set.of(0)));
    Assert.assertEquals("B08/S08", LifeLikePreset.formatNotation(Set.of(8, 0), Set.of(8, 0)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsNegativeCount() {
    LifeLikePreset.formatNotation(Set.of(-1), Set.of(2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsOutOfRangeCount() {
    LifeLikePreset.formatNotation(Set.of(3), Set.of(9));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsNullSet() {
    LifeLikePreset.formatNotation(null, Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsNullElement() {
    Set<Integer> countsWithNull = new HashSet<>(Arrays.asList(2, null));
    LifeLikePreset.formatNotation(countsWithNull, Set.of(2, 3));
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
  public void testFromCountsRejectsNull() {
    LifeLikePreset.fromCounts(null, Set.of(2, 3));
  }
}
