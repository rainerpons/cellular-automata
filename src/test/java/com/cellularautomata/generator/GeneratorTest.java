package com.cellularautomata.generator;

import org.junit.Assert;
import org.junit.Test;

/**
 * The <code>GeneratorTest</code> class is responsible for testing the methods from the <code>
 * Generator</code> class.
 *
 * @author Rainer Pons
 */
public class GeneratorTest {
  /** Asserts that a local update rule is valid. */
  @Test
  public void testGenerateRulePositive() {
    String expected = "00011110";
    String actual = Generator.generateRule(30);
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that a local update rule is invalid. */
  @Test
  public void testGenerateRuleNegative() {
    String expected = null;
    String actual = Generator.generateRule(-1);
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that rule numbers above the valid range are rejected. */
  @Test
  public void testGenerateRuleAboveUpperBound() {
    Assert.assertNull(Generator.generateRule(256));
    Assert.assertNull(Generator.generateRule(300));
  }

  /** Asserts that rule numbers below the valid range are rejected. */
  @Test
  public void testGenerateRuleBelowLowerBound() {
    Assert.assertNull(Generator.generateRule(-100));
  }

  /** Asserts that the lower boundary rule is zero-padded to eight bits. */
  @Test
  public void testGenerateRuleLowerBoundary() {
    String expected = "00000000";
    String actual = Generator.generateRule(0);
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that the upper boundary rule is represented as eight ones. */
  @Test
  public void testGenerateRuleUpperBoundary() {
    String expected = "11111111";
    String actual = Generator.generateRule(255);
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that low non-zero rules are left-padded with zeroes. */
  @Test
  public void testGenerateRulePadding() {
    Assert.assertEquals("00000001", Generator.generateRule(1));
    Assert.assertEquals("10000000", Generator.generateRule(128));
  }

  /** Asserts that a uniformly distributed seed has the requested size and valid state. */
  @Test
  public void testGenerateSeedPositive() {
    Vector seed = Generator.generateSeed(16);
    Assert.assertEquals(16, seed.getSize());
    Assert.assertTrue(Vector.isValid(seed.getState()));
  }

  /** Asserts that non-positive seed sizes produce an empty vector. */
  @Test
  public void testGenerateSeedNonPositiveSize() {
    Vector zeroSize = Generator.generateSeed(0);
    Vector negativeSize = Generator.generateSeed(-1);

    Assert.assertEquals(0, zeroSize.getSize());
    Assert.assertEquals("", zeroSize.getState());
    Assert.assertEquals(0, negativeSize.getSize());
    Assert.assertEquals("", negativeSize.getState());
  }

  /** Asserts that a sparse seed has exactly one active cell. */
  @Test
  public void testGenerateSparseSeedPositive() {
    Vector seed = Generator.generateSparseSeed(9);
    String state = seed.getState();

    Assert.assertEquals(9, seed.getSize());
    Assert.assertTrue(Vector.isValid(state));
    Assert.assertEquals(1, state.chars().filter(ch -> ch == '1').count());
    Assert.assertEquals(8, state.chars().filter(ch -> ch == '0').count());
  }

  /** Asserts that non-positive sparse seed sizes produce an empty vector. */
  @Test
  public void testGenerateSparseSeedNonPositiveSize() {
    Vector zeroSize = Generator.generateSparseSeed(0);
    Vector negativeSize = Generator.generateSparseSeed(-1);

    Assert.assertEquals(0, zeroSize.getSize());
    Assert.assertEquals("", zeroSize.getState());
    Assert.assertEquals(0, negativeSize.getSize());
    Assert.assertEquals("", negativeSize.getState());
  }

  /** Asserts that an initial seed has alternating cell states. */
  @Test
  public void testGenerateAlternatingSeed() {
    String expected = "10101010";
    String actual = Generator.generateAlternatingSeed(8).getState();
    Assert.assertEquals(expected, actual);
  }

  /** Asserts alternating seeds for small sizes follow the even-one odd-zero pattern. */
  @Test
  public void testGenerateAlternatingSeedSmallSizes() {
    Assert.assertEquals("1", Generator.generateAlternatingSeed(1).getState());
    Assert.assertEquals("10", Generator.generateAlternatingSeed(2).getState());
    Assert.assertEquals("10101", Generator.generateAlternatingSeed(5).getState());
  }

  /** Asserts that non-positive alternating seed sizes produce an empty vector. */
  @Test
  public void testGenerateAlternatingSeedNonPositiveSize() {
    Vector seed = Generator.generateAlternatingSeed(0);
    Assert.assertEquals(0, seed.getSize());
    Assert.assertEquals("", seed.getState());
  }

  /** Asserts that a successive neighborhood vector is generated correctly. */
  @Test
  public void testGenerateSuccessor() {
    String expected = "10101011";
    String actual =
        Generator.generateSuccessor(30, Generator.generateAlternatingSeed(8)).getState();
    Assert.assertEquals(expected, actual);
  }

  /** Asserts successor generation for a known sparse seed under rule 30. */
  @Test
  public void testGenerateSuccessorSparseSeed() {
    String expected = "000111000";
    String actual = Generator.generateSuccessor(30, new Vector("000010000")).getState();
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that invalid rules produce an empty successor vector. */
  @Test
  public void testGenerateSuccessorInvalidRule() {
    Vector successor = Generator.generateSuccessor(256, new Vector("10101010"));
    Assert.assertEquals(0, successor.getSize());
    Assert.assertEquals("", successor.getState());
  }

  /** Asserts successor generation across additional representative rules. */
  @Test
  public void testGenerateSuccessorAdditionalRules() {
    Assert.assertEquals(
        "00000000", Generator.generateSuccessor(0, new Vector("11111111")).getState());
    Assert.assertEquals(
        "11111111", Generator.generateSuccessor(255, new Vector("00000000")).getState());
  }

  /** Asserts that successor generation preserves the input vector size. */
  @Test
  public void testGenerateSuccessorPreservesSize() {
    Vector singleCell = new Vector("1");
    Vector alternating = Generator.generateAlternatingSeed(8);
    Vector sparse = new Vector("000010000");

    Assert.assertEquals(
        singleCell.getSize(), Generator.generateSuccessor(30, singleCell).getSize());
    Assert.assertEquals(
        alternating.getSize(), Generator.generateSuccessor(110, alternating).getSize());
    Assert.assertEquals(sparse.getSize(), Generator.generateSuccessor(255, sparse).getSize());
  }

  /** Asserts that an empty input vector produces an empty successor under rule 30. */
  @Test
  public void testGenerateSuccessorEmptyVector() {
    Vector empty = new Vector("");
    Vector successor = Generator.generateSuccessor(30, empty);

    Assert.assertEquals(0, successor.getSize());
    Assert.assertEquals("", successor.getState());
  }

  /** Asserts that rule 30 evolves a known sparse seed through multiple generations. */
  @Test
  public void testGenerateSuccessorRule30MultipleGenerations() {
    String[] expected = {
      "000010000",
      "000111000",
      "001100100",
      "011011110",
      "110010001",
      "101111011",
      "101000010",
      "101100111",
      "101011100"
    };

    Vector current = new Vector(expected[0]);
    for (int generation = 1; generation < expected.length; generation++) {
      current = Generator.generateSuccessor(30, current);
      Assert.assertEquals(expected[generation], current.getState());
    }
  }
}
