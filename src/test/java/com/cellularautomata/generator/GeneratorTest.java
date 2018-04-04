package com.cellularautomata.generator;

import org.junit.Assert;
import org.junit.Test;
import com.cellularautomata.generator.Generator;

/**
 * The <code>GeneratorTest</code> class is responsible for testing the methods from the
 * <code>Generator</code> class.
 * @author Rainer Pons
 */

public class GeneratorTest {
	/**
	 * Asserts that a local update rule is valid.
	 */
	@Test
	public void testGenerateRulePositive() {
		String expected = "00011110";
		String actual = Generator.generateRule(30);
		Assert.assertEquals(expected, actual);
	}

	/**
	 * Asserts that a local update rule is invalid.
	 */
	@Test
	public void testGenerateRuleNegative() {
		String expected = null;
		String actual = Generator.generateRule(-1);
		Assert.assertEquals(expected, actual);
	}

	/**
	 * Asserts that an initial seed has alternating cell states.
	 */
	@Test
	public void testGenerateAlternatingSeed() {
		String expected = "10101010";
		String actual = Generator.generateAlternatingSeed(8).getState();
		Assert.assertEquals(expected, actual);
	}

	/**
	 * Asserts that a successive neighborhood vector is generated correctly.
	 */
	@Test
	public void testGenerateSuccessor() {
		String expected = "10101011";
		String actual =
				Generator.generateSuccessor(30, Generator.generateAlternatingSeed(8)).getState();
		Assert.assertEquals(expected, actual);
	}
}