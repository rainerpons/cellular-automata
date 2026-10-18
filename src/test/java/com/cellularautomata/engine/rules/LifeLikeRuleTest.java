package com.cellularautomata.engine.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link LifeLikeRule}. */
public class LifeLikeRuleTest {

  @Test
  public void testNeighborCountBoundsAndValidation() {
    Assert.assertEquals(2, LifeLikeRule.STATE_COUNT);
    Assert.assertEquals(0, LifeLikeRule.MIN_NEIGHBOR_COUNT);
    Assert.assertEquals(8, LifeLikeRule.MAX_NEIGHBOR_COUNT);

    Assert.assertFalse(LifeLikeRule.isValidNeighborCount(-1));
    for (int count = LifeLikeRule.MIN_NEIGHBOR_COUNT;
        count <= LifeLikeRule.MAX_NEIGHBOR_COUNT;
        count++) {
      Assert.assertTrue(LifeLikeRule.isValidNeighborCount(count));
    }
    Assert.assertFalse(LifeLikeRule.isValidNeighborCount(9));
  }

  @Test
  public void testValidateNeighborCountsAcceptsValidSets() {
    LifeLikeRule.validateNeighborCounts(Set.of());
    LifeLikeRule.validateNeighborCounts(Set.of(0));
    LifeLikeRule.validateNeighborCounts(Set.of(0, 2, 3, 8));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateNeighborCountsRejectsNullSet() {
    LifeLikeRule.validateNeighborCounts(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateNeighborCountsRejectsNullElement() {
    Set<Integer> countsWithNull = new HashSet<>(Arrays.asList(3, null));
    LifeLikeRule.validateNeighborCounts(countsWithNull);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateNeighborCountsRejectsNegativeCount() {
    LifeLikeRule.validateNeighborCounts(Set.of(-1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateNeighborCountsRejectsCountAboveMax() {
    LifeLikeRule.validateNeighborCounts(Set.of(9));
  }

  @Test
  public void testFormatNotationForPresetsAndEdgeCases() {
    Assert.assertEquals("B3/S23", LifeLikeRule.formatNotation(Set.of(3), Set.of(2, 3)));
    Assert.assertEquals("B36/S23", LifeLikeRule.formatNotation(Set.of(6, 3), Set.of(3, 2)));
    Assert.assertEquals("B2/S", LifeLikeRule.formatNotation(Set.of(2), Set.of()));
    Assert.assertEquals("B/S", LifeLikeRule.formatNotation(Set.of(), Set.of()));
    Assert.assertEquals("B0/S", LifeLikeRule.formatNotation(Set.of(0), Set.of()));
    Assert.assertEquals("B/S0", LifeLikeRule.formatNotation(Set.of(), Set.of(0)));
    Assert.assertEquals("B08/S08", LifeLikeRule.formatNotation(Set.of(8, 0), Set.of(8, 0)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsNegativeCount() {
    LifeLikeRule.formatNotation(Set.of(-1), Set.of(2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsOutOfRangeCount() {
    LifeLikeRule.formatNotation(Set.of(3), Set.of(9));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsNullSet() {
    LifeLikeRule.formatNotation(null, Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNotationRejectsNullElement() {
    Set<Integer> countsWithNull = new HashSet<>(Arrays.asList(2, null));
    LifeLikeRule.formatNotation(countsWithNull, Set.of(2, 3));
  }

  @Test
  public void testPresetsConstantsAndNotation() {
    Assert.assertEquals("B3/S23", LifeLikeRule.CONWAYS_GAME_OF_LIFE.getNotation());
    Assert.assertEquals(Set.of(3), LifeLikeRule.CONWAYS_GAME_OF_LIFE.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), LifeLikeRule.CONWAYS_GAME_OF_LIFE.getSurvivalCounts());

    Assert.assertEquals("B36/S23", LifeLikeRule.HIGH_LIFE.getNotation());
    Assert.assertEquals(Set.of(3, 6), LifeLikeRule.HIGH_LIFE.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), LifeLikeRule.HIGH_LIFE.getSurvivalCounts());

    Assert.assertEquals("B2/S", LifeLikeRule.SEEDS.getNotation());
    Assert.assertEquals(Set.of(2), LifeLikeRule.SEEDS.getBirthCounts());
    Assert.assertEquals(Set.of(), LifeLikeRule.SEEDS.getSurvivalCounts());
  }

  @Test
  public void testConstructorWithPreset() {
    LifeLikeRule conwayFromPreset = new LifeLikeRule(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
    Assert.assertEquals(LifeLikeRule.CONWAYS_GAME_OF_LIFE, conwayFromPreset);
    Assert.assertEquals("B3/S23", conwayFromPreset.getNotation());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullPresetThrows() {
    new LifeLikeRule((LifeLikePreset) null);
  }

  @Test
  public void testDefensiveCopyingOfSets() {
    Set<Integer> mutableBirth = new HashSet<>(Arrays.asList(3));
    Set<Integer> mutableSurvival = new HashSet<>(Arrays.asList(2, 3));
    LifeLikeRule rule = new LifeLikeRule(mutableBirth, mutableSurvival);

    mutableBirth.add(6);
    mutableSurvival.add(4);

    Assert.assertEquals(Set.of(3), rule.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), rule.getSurvivalCounts());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullBirthCounts() {
    new LifeLikeRule(null, Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullSurvivalCounts() {
    new LifeLikeRule(Set.of(3), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsBirthCountWithNull() {
    Set<Integer> withNull = new HashSet<>(Arrays.asList(3, null));
    new LifeLikeRule(withNull, Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNegativeCount() {
    new LifeLikeRule(Set.of(-1), Set.of(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsCountAbove8() {
    new LifeLikeRule(Set.of(3), Set.of(9));
  }

  @Test
  public void testEvaluateBooleanAndIntegerStatesConway() {
    LifeLikeRule conway = LifeLikeRule.CONWAYS_GAME_OF_LIFE;

    // Dead cell (currentlyAlive = false, currentState = 0)
    for (int count = 0; count <= 8; count++) {
      boolean expected = (count == 3);
      int expectedInt = expected ? 1 : 0;
      Assert.assertEquals(expected, conway.evaluate(false, count));
      Assert.assertEquals(expectedInt, conway.evaluate(0, count));
      Assert.assertEquals(expected, conway.isBirth(count));
    }

    // Live cell (currentlyAlive = true, currentState = 1)
    for (int count = 0; count <= 8; count++) {
      boolean expected = (count == 2 || count == 3);
      int expectedInt = expected ? 1 : 0;
      Assert.assertEquals(expected, conway.evaluate(true, count));
      Assert.assertEquals(expectedInt, conway.evaluate(1, count));
      Assert.assertEquals(expected, conway.isSurvival(count));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateBooleanRejectsNegativeCount() {
    LifeLikeRule.CONWAYS_GAME_OF_LIFE.evaluate(false, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateBooleanRejectsCountAbove8() {
    LifeLikeRule.CONWAYS_GAME_OF_LIFE.evaluate(true, 9);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateIntRejectsInvalidCellState2() {
    LifeLikeRule.CONWAYS_GAME_OF_LIFE.evaluate(2, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateIntRejectsInvalidCellStateNegative() {
    LifeLikeRule.CONWAYS_GAME_OF_LIFE.evaluate(-1, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateIntRejectsNegativeCount() {
    LifeLikeRule.CONWAYS_GAME_OF_LIFE.evaluate(0, -1);
  }

  @Test
  public void testIsBirthAndIsSurvivalOutOfBoundsReturnFalse() {
    Assert.assertFalse(LifeLikeRule.CONWAYS_GAME_OF_LIFE.isBirth(-1));
    Assert.assertFalse(LifeLikeRule.CONWAYS_GAME_OF_LIFE.isBirth(9));
    Assert.assertFalse(LifeLikeRule.CONWAYS_GAME_OF_LIFE.isSurvival(-1));
    Assert.assertFalse(LifeLikeRule.CONWAYS_GAME_OF_LIFE.isSurvival(9));
  }

  @Test
  public void testEmptyBirthAndSurvivalRule() {
    LifeLikeRule emptyRule = new LifeLikeRule(Set.of(), Set.of());
    Assert.assertEquals("B/S", emptyRule.getNotation());
    Assert.assertTrue(emptyRule.getBirthCounts().isEmpty());
    Assert.assertTrue(emptyRule.getSurvivalCounts().isEmpty());

    for (int count = 0; count <= 8; count++) {
      Assert.assertFalse(emptyRule.evaluate(false, count));
      Assert.assertFalse(emptyRule.evaluate(true, count));
      Assert.assertEquals(0, emptyRule.evaluate(0, count));
      Assert.assertEquals(0, emptyRule.evaluate(1, count));
      Assert.assertFalse(emptyRule.isBirth(count));
      Assert.assertFalse(emptyRule.isSurvival(count));
    }
  }

  @Test
  public void testB0RuleDistinctFromEmptyBirth() {
    LifeLikeRule b0Rule = new LifeLikeRule(Set.of(0), Set.of());
    LifeLikeRule emptyRule = new LifeLikeRule(Set.of(), Set.of());

    Assert.assertNotEquals(emptyRule, b0Rule);
    Assert.assertEquals("B0/S", b0Rule.getNotation());

    // 0 neighbors causes birth
    Assert.assertTrue(b0Rule.evaluate(false, 0));
    Assert.assertEquals(1, b0Rule.evaluate(0, 0));
    Assert.assertTrue(b0Rule.isBirth(0));

    // Other counts do not cause birth
    for (int count = 1; count <= 8; count++) {
      Assert.assertFalse(b0Rule.evaluate(false, count));
      Assert.assertEquals(0, b0Rule.evaluate(0, count));
      Assert.assertFalse(b0Rule.isBirth(count));
    }
  }

  @Test
  public void testRuleIndependenceFromUiLimits() {
    // UI limits manual toggles to 2 per row, but domain model accepts arbitrary valid counts
    Set<Integer> birth = Set.of(1, 3, 5, 7);
    Set<Integer> survival = Set.of(0, 2, 4, 6, 8);
    LifeLikeRule customRule = new LifeLikeRule(birth, survival);

    Assert.assertEquals("B1357/S02468", customRule.getNotation());
    Assert.assertEquals(birth, customRule.getBirthCounts());
    Assert.assertEquals(survival, customRule.getSurvivalCounts());

    for (int count = 0; count <= 8; count++) {
      Assert.assertEquals(birth.contains(count), customRule.evaluate(false, count));
      Assert.assertEquals(survival.contains(count), customRule.evaluate(true, count));
    }
  }

  @Test
  public void testValueObjectContracts() {
    LifeLikeRule conway1 = new LifeLikeRule(Set.of(3), Set.of(2, 3));
    LifeLikeRule conway2 = LifeLikeRule.CONWAYS_GAME_OF_LIFE;
    LifeLikeRule highLife = LifeLikeRule.HIGH_LIFE;

    Assert.assertEquals(conway1, conway1);
    Assert.assertEquals(conway1, conway2);
    Assert.assertEquals(conway1.hashCode(), conway2.hashCode());
    Assert.assertNotEquals(conway1, highLife);
    Assert.assertNotEquals(conway1, null);
    Assert.assertNotEquals(conway1, "B3/S23");

    Assert.assertEquals("B3/S23", conway1.toString());
  }
}
