package com.cellularautomata.engine.presets;

import com.cellularautomata.engine.rules.LifeLikeRule;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
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

    Assert.assertEquals("HighLife", LifeLikePreset.HIGH_LIFE.getDisplayName());
    Assert.assertEquals("HighLife", LifeLikePreset.HIGH_LIFE.toString());
    Assert.assertEquals(Set.of(3, 6), LifeLikePreset.HIGH_LIFE.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), LifeLikePreset.HIGH_LIFE.getSurvivalCounts());

    Assert.assertEquals("Seeds", LifeLikePreset.SEEDS.getDisplayName());
    Assert.assertEquals("Seeds", LifeLikePreset.SEEDS.toString());
    Assert.assertEquals(Set.of(2), LifeLikePreset.SEEDS.getBirthCounts());
    Assert.assertEquals(Set.of(), LifeLikePreset.SEEDS.getSurvivalCounts());
  }

  @Test
  public void testToRule() {
    LifeLikeRule conwayRule = LifeLikePreset.CONWAYS_GAME_OF_LIFE.toRule();
    Assert.assertEquals("B3/S23", conwayRule.getNotation());
    Assert.assertEquals(Set.of(3), conwayRule.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), conwayRule.getSurvivalCounts());

    LifeLikeRule highLifeRule = LifeLikePreset.HIGH_LIFE.toRule();
    Assert.assertEquals("B36/S23", highLifeRule.getNotation());
    Assert.assertEquals(Set.of(3, 6), highLifeRule.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), highLifeRule.getSurvivalCounts());

    LifeLikeRule seedsRule = LifeLikePreset.SEEDS.toRule();
    Assert.assertEquals("B2/S", seedsRule.getNotation());
    Assert.assertEquals(Set.of(2), seedsRule.getBirthCounts());
    Assert.assertEquals(Set.of(), seedsRule.getSurvivalCounts());
  }

  @Test
  public void testFromCountsResolvesMatchingPreset() {
    Assert.assertEquals(
        Optional.of(LifeLikePreset.CONWAYS_GAME_OF_LIFE),
        LifeLikePreset.fromCounts(Set.of(3), Set.of(2, 3)));
    Assert.assertEquals(
        Optional.of(LifeLikePreset.HIGH_LIFE),
        LifeLikePreset.fromCounts(Set.of(3, 6), Set.of(2, 3)));
    Assert.assertEquals(
        Optional.of(LifeLikePreset.SEEDS), LifeLikePreset.fromCounts(Set.of(2), Set.of()));
  }

  @Test
  public void testFromCountsReturnsEmptyForNonPresetCounts() {
    Assert.assertEquals(Optional.empty(), LifeLikePreset.fromCounts(Set.of(), Set.of()));
    Assert.assertEquals(Optional.empty(), LifeLikePreset.fromCounts(Set.of(0, 3), Set.of(2, 3)));
    Assert.assertEquals(Optional.empty(), LifeLikePreset.fromCounts(Set.of(3), Set.of(0, 2)));
    Assert.assertEquals(Optional.empty(), LifeLikePreset.fromCounts(Set.of(1, 2), Set.of(3, 4)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromCountsRejectsNullBirthSet() {
    LifeLikePreset.fromCounts(null, Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromCountsRejectsNullSurvivalSet() {
    LifeLikePreset.fromCounts(Set.of(3), null);
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
