package com.github.rpons17;

import org.junit.Assert;
import org.junit.Test;

public class VectorTest {
	// assert that a string is valid
	@Test
	public void testIsValidPositive() {
		boolean expected = true;
		boolean actual = Vector.isValid("01001010");
		Assert.assertEquals(expected, actual);
	}

	// assert that a string is invalid
	@Test
	public void testIsValidNegative() {
		boolean expected = false;
		boolean actual = Vector.isValid("01002010");
		Assert.assertEquals(expected, actual);
	}
}