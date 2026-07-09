package com.cellularautomata.engine;

import java.util.OptionalInt;
import org.junit.Assert;
import org.junit.Test;

public class RuleValidatorTest {

  private static final int RULE_30 = 30;

  @Test
  public void testValidInputs() {
    String[] validInputs = {"0", "255", " " + RULE_30 + " "};
    int[] expectedOutputs = {0, 255, RULE_30};

    for (int i = 0; i < validInputs.length; i++) {
      OptionalInt result = RuleValidator.parseRule(validInputs[i], 0, 255);
      Assert.assertTrue("Should be valid: " + validInputs[i], result.isPresent());
      Assert.assertEquals(expectedOutputs[i], result.getAsInt());
    }
  }

  @Test
  public void testInvalidInputs() {
    String[] invalidInputs = {"abc", "12.5", "-1", "256", "2150000000", "", null};

    for (String input : invalidInputs) {
      OptionalInt result = RuleValidator.parseRule(input, 0, 255);
      Assert.assertFalse("Should be invalid: " + input, result.isPresent());
    }
  }

  @Test
  public void testTotalisticBounds() {
    Assert.assertTrue(RuleValidator.parseRule("15", 0, 15).isPresent());
    Assert.assertFalse(RuleValidator.parseRule("16", 0, 15).isPresent());
    Assert.assertTrue(RuleValidator.parseRule("0", 0, 15).isPresent());
  }
}
