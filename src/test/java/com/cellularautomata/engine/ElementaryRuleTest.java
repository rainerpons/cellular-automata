package com.cellularautomata.engine;

import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link ElementaryRule}. */
public class ElementaryRuleTest {

  @Test
  public void testLowerBoundaryRule() {
    ElementaryRule rule0 = new ElementaryRule(0);
    Assert.assertEquals(0, rule0.getRuleNumber());
    // 0 is 00000000 in binary, all evaluations should be '0'
    Assert.assertEquals('0', rule0.evaluate("111"));
    Assert.assertEquals('0', rule0.evaluate("000"));
  }

  @Test
  public void testUpperBoundaryRule() {
    ElementaryRule rule255 = new ElementaryRule(255);
    Assert.assertEquals(255, rule255.getRuleNumber());
    // 255 is 11111111 in binary, all evaluations should be '1'
    Assert.assertEquals('1', rule255.evaluate("111"));
    Assert.assertEquals('1', rule255.evaluate("000"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidLowerBound() {
    new ElementaryRule(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUpperBound() {
    new ElementaryRule(256);
  }

  @Test
  public void testRepresentativeEvaluateBehavior() {
    // Rule 30 is 00011110
    // Index map:
    // 111 (7) -> '0'
    // 110 (6) -> '0'
    // 101 (5) -> '0'
    // 100 (4) -> '1'
    // 011 (3) -> '1'
    // 010 (2) -> '1'
    // 001 (1) -> '1'
    // 000 (0) -> '0'
    ElementaryRule rule30 = new ElementaryRule(30);

    Assert.assertEquals('0', rule30.evaluate("111"));
    Assert.assertEquals('0', rule30.evaluate("110"));
    Assert.assertEquals('0', rule30.evaluate("101"));
    Assert.assertEquals('1', rule30.evaluate("100"));
    Assert.assertEquals('1', rule30.evaluate("011"));
    Assert.assertEquals('1', rule30.evaluate("010"));
    Assert.assertEquals('1', rule30.evaluate("001"));
    Assert.assertEquals('0', rule30.evaluate("000"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNeighborhoodLength() {
    ElementaryRule rule30 = new ElementaryRule(30);
    rule30.evaluate("10"); // Too short
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNeighborhoodCharacters() {
    ElementaryRule rule30 = new ElementaryRule(30);
    // NeighborhoodValidator throws IllegalArgumentException for non-binary characters
    rule30.evaluate("1a0");
  }
}
