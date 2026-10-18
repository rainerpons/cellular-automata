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
}
