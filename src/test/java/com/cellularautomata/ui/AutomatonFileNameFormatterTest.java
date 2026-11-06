package com.cellularautomata.ui;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.TotalisticRule;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonFileNameFormatter}. */
public class AutomatonFileNameFormatterTest {

  @Test
  public void testFormatFileNameElementary() {
    ElementaryRule rule = new ElementaryRule(110);
    String fileName = AutomatonFileNameFormatter.formatFileName(rule, 64);

    Assert.assertEquals("elementary_rule110_size64.png", fileName);
  }

  @Test
  public void testFormatFileNameTotalistic() {
    TotalisticRule rule = new TotalisticRule(100, 3);
    String fileName = AutomatonFileNameFormatter.formatFileName(rule, 64);

    Assert.assertEquals("totalistic_states3_rule100_size64.png", fileName);

    TotalisticRule rule777 = new TotalisticRule(777, 3);
    Assert.assertEquals(
        "totalistic_states3_rule777_size128.png",
        AutomatonFileNameFormatter.formatFileName(rule777, 128));
  }

  @Test
  public void testFormatFileNameLifeLikeRuleStandardAndEdgeNotations() {
    LifeLikeRule conway = new LifeLikeRule(Set.of(3), Set.of(2, 3));
    Assert.assertEquals(
        "life_like_ruleB3-S23_size64.png", AutomatonFileNameFormatter.formatFileName(conway, 64));
    Assert.assertEquals(
        "life_like_ruleB3-S23_size8.png", AutomatonFileNameFormatter.formatFileName(conway, 8));

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
    AutomatonFileNameFormatter.formatFileName((ElementaryRule) null, 64);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameTotalisticNullRuleThrows() {
    AutomatonFileNameFormatter.formatFileName((TotalisticRule) null, 128);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameLifeLikeNullRuleThrows() {
    AutomatonFileNameFormatter.formatFileName((LifeLikeRule) null, 64);
  }

  @Test
  public void testPrivateConstructorThrowsIllegalStateException() throws Exception {
    var constructor = AutomatonFileNameFormatter.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      Assert.fail("Expected InvocationTargetException wrapping IllegalStateException");
    } catch (java.lang.reflect.InvocationTargetException e) {
      Assert.assertTrue(e.getCause() instanceof IllegalStateException);
      Assert.assertEquals("Utility class", e.getCause().getMessage());
    }
  }
}
