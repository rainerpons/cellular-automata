package com.cellularautomata.engine.rules;

import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link TotalisticRule}. */
public class TotalisticRuleTest {

  @Test
  public void testRule0YieldsState0() {
    TotalisticRule rule0 = new TotalisticRule(0);
    // 0000 in binary. All sums should yield '0'.
    Assert.assertEquals('0', rule0.evaluate("111")); // sum 3
    Assert.assertEquals('0', rule0.evaluate("101")); // sum 2
    Assert.assertEquals('0', rule0.evaluate("001")); // sum 1
    Assert.assertEquals('0', rule0.evaluate("000")); // sum 0
  }

  @Test
  public void testRule15YieldsState1() {
    TotalisticRule rule15 = new TotalisticRule(15);
    // 1111 in binary. All sums should yield '1'.
    Assert.assertEquals('1', rule15.evaluate("111")); // sum 3
    Assert.assertEquals('1', rule15.evaluate("101")); // sum 2
    Assert.assertEquals('1', rule15.evaluate("001")); // sum 1
    Assert.assertEquals('1', rule15.evaluate("000")); // sum 0
  }

  @Test
  public void testRule7YieldsCorrectStates() {
    TotalisticRule rule7 = new TotalisticRule(7);
    // 7 is 0111 in binary.
    // index 0 -> sum 3 -> '0'
    // index 1 -> sum 2 -> '1'
    // index 2 -> sum 1 -> '1'
    // index 3 -> sum 0 -> '1'

    // Sum 3
    Assert.assertEquals('0', rule7.evaluate("111"));

    // Sum 2
    Assert.assertEquals('1', rule7.evaluate("110"));
    Assert.assertEquals('1', rule7.evaluate("101"));
    Assert.assertEquals('1', rule7.evaluate("011"));

    // Sum 1
    Assert.assertEquals('1', rule7.evaluate("100"));
    Assert.assertEquals('1', rule7.evaluate("010"));
    Assert.assertEquals('1', rule7.evaluate("001"));

    // Sum 0
    Assert.assertEquals('1', rule7.evaluate("000"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateInvalidLengthThrowsException() {
    new TotalisticRule(7).evaluate("10");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateInvalidCharactersThrowsException() {
    new TotalisticRule(7).evaluate("1a0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRuleNegative() {
    new TotalisticRule(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRuleTooHigh() {
    new TotalisticRule(16);
  }
}
