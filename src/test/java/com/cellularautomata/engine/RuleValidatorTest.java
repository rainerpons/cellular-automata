package com.cellularautomata.engine;

import java.util.OptionalInt;
import org.junit.Assert;
import org.junit.Test;

public class RuleValidatorTest {

  @Test
  public void testValidInputs() {
    String[] validInputs = {"0", "255", " 30 "};
    int[] expectedOutputs = {0, 255, 30};

    for (int i = 0; i < validInputs.length; i++) {
      OptionalInt result = RuleValidator.parseRule(validInputs[i]);
      Assert.assertTrue("Should be valid: " + validInputs[i], result.isPresent());
      Assert.assertEquals(expectedOutputs[i], result.getAsInt());
    }
  }

  @Test
  public void testInvalidInputs() {
    String[] invalidInputs = {"abc", "12.5", "-1", "256", "2150000000", "", null};

    for (String input : invalidInputs) {
      OptionalInt result = RuleValidator.parseRule(input);
      Assert.assertFalse("Should be invalid: " + input, result.isPresent());
    }
  }
}
