package com.cellularautomata.generator;

import org.junit.Assert;
import org.junit.Test;
import com.cellularautomata.generator.Vector;

/**
 * The <code>VectorTest</code> class is responsible for testing the methods from the
 * <code>Vector</code> class.
 * @author Rainer Pons
 */

public class VectorTest {
	/**
	 * Asserts that the state of a vector is valid.
	 */
	@Test
	public void testIsValidPositive() {
		boolean expected = true;
		boolean actual = Vector.isValid("01001010");
		Assert.assertEquals(expected, actual);
	}

	/**
	 * Asserts that the state of a vector is invalid.
	 */
	@Test
	public void testIsValidNegative() {
		boolean expected = false;
		boolean actual = Vector.isValid("01002010");
		Assert.assertEquals(expected, actual);
	}
}