
package tests;

import org.junit.Assert;
import org.junit.Test;
import automata.Generator;
import automata.Vector;

/*
 * GeneratorTest.java
 */

public class GeneratorTest {
	// assert that a rule is valid
	@Test
	public void testGenerateRulePositive() {
		String expected = "00011110";
		String actual = Generator.generateRule(30);
		Assert.assertEquals(expected, actual);
	}

	// assert that a rule is invalid
	@Test
	public void testGenerateRuleNegative() {
		String expected = null;
		String actual = Generator.generateRule(-1);
		Assert.assertEquals(expected, actual);
	}

	// assert that a seed is alternating
	@Test
	public void testGenerateAlternatingSeed() {
		String expected = "10101010";
		String actual = Generator.generateAlternatingSeed(8).getVector();
		Assert.assertEquals(expected, actual);
	}

	// assert that successor is generated properly
	@Test
	public void testGenerateSuccessor() {
		String expected = "10101011";
		String actual = Generator.generateSuccessor(30, Generator.generateAlternatingSeed(8)).getVector();
		Assert.assertEquals(expected, actual);
	}
}
