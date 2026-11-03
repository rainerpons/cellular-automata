package com.cellularautomata.ui;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.TotalisticRule;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonFileNameFormatter}. */
public class AutomatonFileNameFormatterTest {

  @Test
  public void testFormatFileNameElementary() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    ElementaryRule rule = new ElementaryRule(110);
    String fileName = AutomatonFileNameFormatter.formatFileName(rule, 64, time);

    Assert.assertEquals("elementary_rule110_size64_2026-07-13T15-30-45.png", fileName);
  }

  @Test
  public void testFormatFileNameTotalistic() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    TotalisticRule rule = new TotalisticRule(777, 3);
    String fileName = AutomatonFileNameFormatter.formatFileName(rule, 128, time);

    Assert.assertEquals("totalistic_states3_rule777_size128_2026-07-13T15-30-45.png", fileName);
  }

  @Test
  public void testFormatFileNameLifeLikeRuleStandardAndEdgeNotations() {
    LifeLikeRule conway = new LifeLikeRule(Set.of(3), Set.of(2, 3));
    Assert.assertEquals(
        "life_like_ruleB3-S23_size8.png", AutomatonFileNameFormatter.formatFileName(conway, 8));
    Assert.assertEquals(
        "life_like_ruleB3-S23_size64.png", AutomatonFileNameFormatter.formatFileName(conway, 64));

    LifeLikeRule seeds = new LifeLikeRule(Set.of(2), Set.of());
    Assert.assertEquals(
        "life_like_ruleB2-S_size16.png", AutomatonFileNameFormatter.formatFileName(seeds, 16));

    LifeLikeRule emptyBirth = new LifeLikeRule(Set.of(), Set.of(2, 3));
    Assert.assertEquals(
        "life_like_ruleB-S23_size8.png", AutomatonFileNameFormatter.formatFileName(emptyBirth, 8));

    LifeLikeRule bothEmpty = new LifeLikeRule(Set.of(), Set.of());
    Assert.assertEquals(
        "life_like_ruleB-S_size5.png", AutomatonFileNameFormatter.formatFileName(bothEmpty, 5));

    LifeLikeRule zeroCounts = new LifeLikeRule(Set.of(0), Set.of(0));
    Assert.assertEquals(
        "life_like_ruleB0-S0_size155.png",
        AutomatonFileNameFormatter.formatFileName(zeroCounts, 155));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameElementaryNullRuleThrows() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    AutomatonFileNameFormatter.formatFileName((ElementaryRule) null, 64, time);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameElementaryNullTimeThrows() {
    AutomatonFileNameFormatter.formatFileName(new ElementaryRule(110), 64, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameTotalisticNullRuleThrows() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    AutomatonFileNameFormatter.formatFileName((TotalisticRule) null, 128, time);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameTotalisticNullTimeThrows() {
    AutomatonFileNameFormatter.formatFileName(new TotalisticRule(777, 3), 128, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameLifeLikeNullRuleThrows() {
    AutomatonFileNameFormatter.formatFileName(null, 64);
  }
}
