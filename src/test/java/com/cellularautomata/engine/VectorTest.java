package com.cellularautomata.engine;

import org.junit.Assert;
import org.junit.Test;

/**
 * The <code>VectorTest</code> class is responsible for testing the methods from the <code>Vector
 * </code> class.
 *
 * @author Rainer Pons
 */
public class VectorTest {
  /** Asserts that the state of a vector is valid. */
  @Test
  public void testIsValidPositive() {
    boolean expected = true;
    boolean actual = Vector.isValid("01001010");
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that the state of a vector is invalid. */
  @Test
  public void testIsValidNegative() {
    Assert.assertEquals(false, Vector.isValid("123a4"));
  }

  /** Asserts that an empty string is a valid vector state. */
  @Test
  public void testIsValidEmptyString() {
    Assert.assertTrue(Vector.isValid(""));
  }

  /** Asserts that homogeneous binary strings are valid. */
  @Test
  public void testIsValidForValidStates() {
    Assert.assertTrue(Vector.isValid("1101"));
    Assert.assertTrue(Vector.isValid("0000"));
    Assert.assertTrue(Vector.isValid("1111"));
    Assert.assertTrue(Vector.isValid("0101"));
    Assert.assertTrue(Vector.isValid("01234")); // Multi-state valid
  }

  @Test
  public void testIsValidForInvalidStates() {
    Assert.assertFalse(Vector.isValid("123a")); // Multi-state invalid with characters
    Assert.assertFalse(Vector.isValid("hello"));
    Assert.assertFalse(Vector.isValid("102a0"));
    Assert.assertFalse(Vector.isValid(" "));
    Assert.assertFalse(Vector.isValid(null));
  }

  /** Asserts that a valid state is returned unchanged by initializeVector. */
  @Test
  public void testInitializeVectorValid() {
    String state = "01010101";
    Assert.assertEquals(state, Vector.initializeVector(state));
  }

  /** Asserts that invalid characters cause initializeVector to throw an exception. */
  @Test
  public void testInitializeVectorInvalid() {
    String[] invalidStates = {"0100a010", "abc"};
    for (String state : invalidStates) {
      try {
        Vector.initializeVector(state);
        Assert.fail("Expected IllegalArgumentException for state: " + state);
      } catch (IllegalArgumentException e) {
        // Expected behavior
      }
    }
  }

  /** Asserts that passing a null state to initializeVector throws an exception. */
  @Test(expected = IllegalArgumentException.class)
  public void testInitializeVectorNull() {
    Vector.initializeVector(null);
  }

  /** Asserts that the constructor creates a Vector properly for valid state strings. */
  @Test
  public void testVectorConstructorValidState() {
    String state = "111000";
    Vector vector = new Vector(state);
    Assert.assertEquals(state, vector.getState());
    Assert.assertEquals(6, vector.getSize());
  }

  /** Asserts that constructor validation rejects invalid state characters. */
  @Test
  public void testVectorConstructorInvalidState() {
    String[] invalidStates = {"0100a010", "01 10"};
    for (String state : invalidStates) {
      try {
        new Vector(state);
        Assert.fail("Expected IllegalArgumentException for state: " + state);
      } catch (IllegalArgumentException e) {
        // Expected behavior
      }
    }
  }
}
