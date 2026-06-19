package com.cellularautomata.generator;

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
    boolean expected = false;
    boolean actual = Vector.isValid("01002010");
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that an empty string is a valid vector state. */
  @Test
  public void testIsValidEmptyString() {
    Assert.assertTrue(Vector.isValid(""));
  }

  /** Asserts that homogeneous binary strings are valid. */
  @Test
  public void testIsValidHomogeneousStates() {
    Assert.assertTrue(Vector.isValid("0000"));
    Assert.assertTrue(Vector.isValid("1111"));
  }

  /** Asserts that non-binary characters are rejected during validation. */
  @Test
  public void testIsValidNonBinaryCharacters() {
    Assert.assertFalse(Vector.isValid("01a1"));
    Assert.assertFalse(Vector.isValid("01 10"));
    Assert.assertFalse(Vector.isValid("01-10"));
  }

  /** Asserts that a valid state is returned unchanged by initializeVector. */
  @Test
  public void testInitializeVectorPositive() {
    String expected = "0101";
    String actual = Vector.initializeVector("0101");
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that an invalid state is rejected by initializeVector. */
  @Test(expected = IllegalArgumentException.class)
  public void testInitializeVectorNegative() {
    Vector.initializeVector("bad");
  }

  /** Asserts that an invalid state is rejected by initializeVector. */
  @Test(expected = IllegalArgumentException.class)
  public void testInitializeVectorNegativeExtended() {
    Vector.initializeVector("01002010");
  }

  /** Asserts that a null state is rejected by initializeVector. */
  @Test(expected = IllegalArgumentException.class)
  public void testInitializeVectorNull() {
    Vector.initializeVector(null);
  }

  /** Asserts that a vector constructed from a valid state exposes that state. */
  @Test
  public void testVectorConstructorValidState() {
    Vector vector = new Vector("1010");
    Assert.assertEquals("1010", vector.getState());
    Assert.assertEquals(4, vector.getSize());
  }

  /** Asserts that a vector constructed from an invalid state throws an exception. */
  @Test(expected = IllegalArgumentException.class)
  public void testVectorConstructorInvalidState() {
    new Vector("01002010");
  }

  /** Asserts that a vector constructed from a null state throws an exception. */
  @Test(expected = IllegalArgumentException.class)
  public void testVectorConstructorNullState() {
    new Vector(null);
  }
}
