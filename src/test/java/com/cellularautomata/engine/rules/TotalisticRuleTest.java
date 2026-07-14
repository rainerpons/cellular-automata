package com.cellularautomata.engine.rules;

import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link TotalisticRule}. */
public class TotalisticRuleTest {

  @Test
  public void testCalculateMaxRule() {
    Assert.assertEquals(15, TotalisticRule.calculateMaxRule(2));
    Assert.assertEquals(2186, TotalisticRule.calculateMaxRule(3));
    Assert.assertEquals(1048575, TotalisticRule.calculateMaxRule(4));
    Assert.assertEquals(1220703124, TotalisticRule.calculateMaxRule(5));
  }

  @Test
  public void testRule0YieldsState0() {
    TotalisticRule rule0 = new TotalisticRule(0, 2);
    // 0000 in binary. All sums should yield '0'.
    Assert.assertEquals('0', rule0.evaluate("111")); // sum 3
    Assert.assertEquals('0', rule0.evaluate("101")); // sum 2
    Assert.assertEquals('0', rule0.evaluate("001")); // sum 1
    Assert.assertEquals('0', rule0.evaluate("000")); // sum 0
  }

  @Test
  public void testRule15YieldsState1() {
    TotalisticRule rule15 = new TotalisticRule(15, 2);
    // 1111 in binary. All sums should yield '1'.
    Assert.assertEquals('1', rule15.evaluate("111")); // sum 3
    Assert.assertEquals('1', rule15.evaluate("101")); // sum 2
    Assert.assertEquals('1', rule15.evaluate("001")); // sum 1
    Assert.assertEquals('1', rule15.evaluate("000")); // sum 0
  }

  @Test
  public void testRule7YieldsCorrectStates() {
    TotalisticRule rule7 = new TotalisticRule(7, 2);
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

  @Test
  public void test3StatesRule() {
    // 3 states (0, 1, 2). Length = 3*(3-1) + 1 = 7. Max sum = 6. Max rule = 3^7 - 1 = 2186
    // Let's test a known rule. Rule 2186 -> all '2's.
    TotalisticRule rule2186 = new TotalisticRule(2186, 3);
    Assert.assertEquals('2', rule2186.evaluate("222")); // sum 6
    Assert.assertEquals('2', rule2186.evaluate("000")); // sum 0

    // Rule 1 in base 3 -> length 7 -> 0000001
    // sum 6 -> index 0 -> '0'
    // sum 0 -> index 6 -> '1'
    TotalisticRule rule1 = new TotalisticRule(1, 3);
    Assert.assertEquals('0', rule1.evaluate("222")); // sum 6
    Assert.assertEquals('1', rule1.evaluate("000")); // sum 0
  }

  @Test
  public void test5StatesRule() {
    // 5 states (0..4). Length = 3*(5-1) + 1 = 13. Max sum = 12.
    // Rule 1 in base 5 -> 0000000000001
    // sum 12 -> index 0 -> '0'
    // sum 0 -> index 12 -> '1'
    TotalisticRule rule1 = new TotalisticRule(1, 5);
    Assert.assertEquals('0', rule1.evaluate("444")); // sum 12
    Assert.assertEquals('1', rule1.evaluate("000")); // sum 0
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateInvalidLengthThrowsException() {
    new TotalisticRule(7, 2).evaluate("10");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateInvalidCharactersThrowsException() {
    new TotalisticRule(7, 2).evaluate("1a0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvaluateInvalidStateCharacterThrowsException() {
    // State 2 is not valid for 2 states
    new TotalisticRule(7, 2).evaluate("120");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRuleNegative() {
    new TotalisticRule(-1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRuleTooHigh() {
    new TotalisticRule(16, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidStatesTooLow() {
    new TotalisticRule(7, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidStatesTooHigh() {
    new TotalisticRule(7, 6);
  }
}
