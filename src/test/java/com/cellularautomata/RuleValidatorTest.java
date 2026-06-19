package com.cellularautomata;

import org.junit.Assert;
import org.junit.Test;

public class RuleValidatorTest {

  @Test
  public void testValidLowerBound() {
    Assert.assertTrue(RuleValidator.isValidRule("0"));
  }

  @Test
  public void testValidUpperBound() {
    Assert.assertTrue(RuleValidator.isValidRule("255"));
  }

  @Test
  public void testValidMiddle() {
    Assert.assertTrue(RuleValidator.isValidRule("128"));
  }

  @Test
  public void testValidWithWhitespace() {
    Assert.assertTrue(RuleValidator.isValidRule("  30  "));
  }

  @Test
  public void testNonNumericInput() {
    Assert.assertFalse(RuleValidator.isValidRule("abc"));
  }

  @Test
  public void testDecimalInput() {
    Assert.assertFalse(RuleValidator.isValidRule("12.5"));
  }

  @Test
  public void testBelowRangeInput() {
    Assert.assertFalse(RuleValidator.isValidRule("-1"));
  }

  @Test
  public void testAboveRangeInput() {
    Assert.assertFalse(RuleValidator.isValidRule("256"));
  }

  @Test
  public void testLargeNumericInput() {
    Assert.assertFalse(RuleValidator.isValidRule("2150000000"));
  }

  @Test
  public void testEmptyInput() {
    Assert.assertFalse(RuleValidator.isValidRule(""));
  }

  @Test
  public void testNullInput() {
    Assert.assertFalse(RuleValidator.isValidRule(null));
  }
}
