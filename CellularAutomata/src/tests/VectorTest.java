
package tests;

import automata.Vector;
import org.junit.Assert;
import org.junit.Test;

/*
 * VectorTest.java
 */

public class VectorTest {
	// assert true if vector is valid
	@Test
	public void testIsValidPositive() {
		boolean expected = true;
		boolean actual = Vector.isValid("01001010");
		Assert.assertEquals(expected, actual);
	}

	// assert false if vector is invalid
	@Test
	public void testIsValidNegative() {
		boolean expected = false;
		boolean actual = Vector.isValid("01002010");
		Assert.assertEquals(expected, actual);
	}
}
