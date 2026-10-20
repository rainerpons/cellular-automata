package com.cellularautomata.engine;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.Rule;
import com.cellularautomata.engine.rules.TotalisticRule;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/**
 * The <code>GeneratorTest</code> class is responsible for testing the methods from the <code>
 * Generator</code> class.
 *
 * @author Rainer Pons
 */
public class GeneratorTest {

  private static final Rule RULE_30 = new ElementaryRule(30);

  /** Asserts that a uniformly distributed seed has the requested size and valid state. */
  @Test
  public void testGenerateSeedPositive() {
    Vector seed = Generator.generateSeed(16, 2);
    Assert.assertEquals(16, seed.getSize());
    Assert.assertTrue(Vector.isValid(seed.getState()));
  }

  /** Asserts that non-positive seed sizes produce an empty vector. */
  @Test
  public void testGenerateSeedNonPositiveSize() {
    Vector zeroSize = Generator.generateSeed(0, 2);
    Vector negativeSize = Generator.generateSeed(-1, 2);

    Assert.assertEquals(0, zeroSize.getSize());
    Assert.assertEquals("", zeroSize.getState());
    Assert.assertEquals(0, negativeSize.getSize());
    Assert.assertEquals("", negativeSize.getState());
  }

  /** Asserts that a sparse seed has exactly one active cell. */
  @Test
  public void testGenerateSparseSeedPositive() {
    Vector seed = Generator.generateSparseSeed(9, 2);
    String state = seed.getState();

    Assert.assertEquals(9, seed.getSize());
    Assert.assertTrue(Vector.isValid(state));
    Assert.assertEquals(1, state.chars().filter(ch -> ch == '1').count());
    Assert.assertEquals(8, state.chars().filter(ch -> ch == '0').count());
  }

  /** Asserts that non-positive sparse seed sizes produce an empty vector. */
  @Test
  public void testGenerateSparseSeedNonPositiveSize() {
    Vector zeroSize = Generator.generateSparseSeed(0, 2);
    Vector negativeSize = Generator.generateSparseSeed(-1, 2);

    Assert.assertEquals(0, zeroSize.getSize());
    Assert.assertEquals("", zeroSize.getState());
    Assert.assertEquals(0, negativeSize.getSize());
    Assert.assertEquals("", negativeSize.getState());
  }

  /** Asserts that repeated alternating seed generation toggles phases. */
  @Test
  public void testGenerateAlternatingSeedTogglesPhases() {
    String first = Generator.generateAlternatingSeed(8, 2).getState();
    String second = Generator.generateAlternatingSeed(8, 2).getState();
    String third = Generator.generateAlternatingSeed(8, 2).getState();

    Assert.assertNotEquals(first, second);
    Assert.assertEquals(first, third);
    Assert.assertTrue(first.equals("10101010") || first.equals("01010101"));
    Assert.assertTrue(second.equals("10101010") || second.equals("01010101"));
  }

  /** Asserts that non-positive alternating seed sizes produce an empty vector. */
  @Test
  public void testGenerateAlternatingSeedNonPositiveSize() {
    Vector seed = Generator.generateAlternatingSeed(0, 2);
    Assert.assertEquals(0, seed.getSize());
    Assert.assertEquals("", seed.getState());
  }

  /** Asserts that a successive neighborhood vector is generated correctly. */
  @Test
  public void testGenerateSuccessor() {
    String expected = "10101011";
    String actual = Generator.generateSuccessor(RULE_30, new Vector("10101010")).getState();
    Assert.assertEquals(expected, actual);
  }

  /** Asserts successor generation for a known sparse seed under rule RULE_30. */
  @Test
  public void testGenerateSuccessorSparseSeed() {
    String expected = "000111000";
    String actual = Generator.generateSuccessor(RULE_30, new Vector("000010000")).getState();
    Assert.assertEquals(expected, actual);
  }

  /** Asserts that null vectors throw an exception during successor generation. */
  @Test(expected = IllegalArgumentException.class)
  public void testGenerateSuccessorNullVector() {
    Generator.generateSuccessor(RULE_30, null);
  }

  /** Asserts that null rules throw an exception during successor generation. */
  @Test(expected = IllegalArgumentException.class)
  public void testGenerateSuccessorNullRule() {
    Generator.generateSuccessor(null, new Vector("10101010"));
  }

  /** Asserts successor generation across additional representative rules. */
  @Test
  public void testGenerateSuccessorAdditionalRules() {
    Assert.assertEquals(
        "00000000",
        Generator.generateSuccessor(new ElementaryRule(0), new Vector("11111111")).getState());
    Assert.assertEquals(
        "11111111",
        Generator.generateSuccessor(new ElementaryRule(255), new Vector("00000000")).getState());
  }

  /** Asserts that successor generation preserves the input vector size. */
  @Test
  public void testGenerateSuccessorPreservesSize() {
    Vector singleCell = new Vector("1");
    Vector alternating = Generator.generateAlternatingSeed(8, 2);
    Vector sparse = new Vector("000010000");

    Assert.assertEquals(
        singleCell.getSize(), Generator.generateSuccessor(RULE_30, singleCell).getSize());
    Assert.assertEquals(
        alternating.getSize(),
        Generator.generateSuccessor(new ElementaryRule(110), alternating).getSize());
    Assert.assertEquals(
        sparse.getSize(), Generator.generateSuccessor(new ElementaryRule(255), sparse).getSize());
  }

  /** Asserts that an empty input vector produces an empty successor under rule RULE_30. */
  @Test
  public void testGenerateSuccessorEmptyVector() {
    Vector empty = new Vector("");
    Vector successor = Generator.generateSuccessor(RULE_30, empty);

    Assert.assertEquals(0, successor.getSize());
    Assert.assertEquals("", successor.getState());
  }

  /** Asserts that rule RULE_30 evolves a known sparse seed through multiple generations. */
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
      current = Generator.generateSuccessor(RULE_30, current);
      Assert.assertEquals(expected[generation], current.getState());
    }
  }

  /**
   * Asserts successor generation works correctly with TotalisticRule to verify abstraction
   * boundary.
   */
  @Test
  public void testGenerateSuccessorTotalisticRule() {
    Rule rule15 = new TotalisticRule(15, 2);
    // 15 in totalistic yields 1 for all valid neighborhoods.
    Vector current = new Vector("00000000");
    Vector successor = Generator.generateSuccessor(rule15, current);
    Assert.assertEquals("11111111", successor.getState());
  }

  @Test
  public void testMultiStateSeedGenerations() {
    Vector uniform = Generator.generateSeed(10, 3);
    Assert.assertEquals(10, uniform.getSize());
    for (char c : uniform.getState().toCharArray()) {
      Assert.assertTrue(c >= '0' && c <= '2');
    }

    Vector sparse = Generator.generateSparseSeed(5, 4);
    Assert.assertEquals(5, sparse.getSize());
    int count3 = 0;
    int count0 = 0;
    for (char c : sparse.getState().toCharArray()) {
      if (c == '3') count3++;
      if (c == '0') count0++;
    }
    Assert.assertEquals(1, count3);
    Assert.assertEquals(4, count0);

    Vector alternating = Generator.generateAlternatingSeed(4, 5);
    Assert.assertEquals(4, alternating.getSize());
    String state = alternating.getState();
    Assert.assertTrue(state.equals("4040") || state.equals("0404"));
  }

  @Test
  public void testGenerateGridWithStronglyTypedSeedType() {
    // DENSITY
    Grid2D density0 = Generator.generateGrid(4, SeedType.DENSITY, 0.0);
    Assert.assertEquals(0, density0.countLiveCells());

    Grid2D density100 = Generator.generateGrid(4, SeedType.DENSITY, 100.0);
    Assert.assertEquals(16, density100.countLiveCells());

    // UNIFORM
    Grid2D uniform = Generator.generateGrid(6, SeedType.UNIFORM, 0.0);
    Assert.assertEquals(6, uniform.getSize());

    // SPARSE
    Grid2D sparse = Generator.generateGrid(5, SeedType.SPARSE, 0.0);
    Assert.assertEquals(5, sparse.getSize());
    Assert.assertEquals(1, sparse.countLiveCells());

    // ALTERNATING
    Grid2D alternating = Generator.generateGrid(4, SeedType.ALTERNATING, 0.0);
    Assert.assertEquals(4, alternating.getSize());
    Assert.assertTrue(alternating.isAlive(0, 0));
    Assert.assertFalse(alternating.isAlive(0, 1));
    Assert.assertTrue(alternating.isAlive(1, 1));
  }

  @Test
  public void testGenerateGridWithStringIdentifiersCaseInsensitive() {
    Grid2D density = Generator.generateGrid(4, "DeNsItY", 100.0);
    Assert.assertEquals(16, density.countLiveCells());

    Grid2D uniform = Generator.generateGrid(4, "uNiFoRm", 0.0);
    Assert.assertEquals(4, uniform.getSize());

    Grid2D sparse = Generator.generateGrid(4, "sPaRsE", 0.0);
    Assert.assertEquals(1, sparse.countLiveCells());

    Grid2D alternating = Generator.generateGrid(4, "ALTERNATING", 0.0);
    Assert.assertEquals(4, alternating.getSize());
  }

  @Test
  public void testRandomSparsePlacementIn2D() {
    // Has exactly 1 live cell
    Grid2D sparse = Generator.generateGrid(10, SeedType.SPARSE, 0.0);
    Assert.assertEquals(1, sparse.countLiveCells());

    // With injected seeded Random(123) produces reproducible cell location
    Grid2D seeded1 = Generator.generateGrid(10, SeedType.SPARSE, 0.0, new Random(123));
    Grid2D seeded2 = Generator.generateGrid(10, SeedType.SPARSE, 0.0, new Random(123));
    Assert.assertEquals(seeded1, seeded2);
    Assert.assertEquals(1, seeded1.countLiveCells());
  }

  @Test
  public void testDensityValidationValidExtremes() {
    // 0.0 produces completely dead grid
    Grid2D zeroDensity = Generator.generateGrid(5, SeedType.DENSITY, 0.0);
    Assert.assertEquals(0, zeroDensity.countLiveCells());

    // 100.0 produces completely alive grid
    Grid2D fullDensity = Generator.generateGrid(5, SeedType.DENSITY, 100.0);
    Assert.assertEquals(25, fullDensity.countLiveCells());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDensityValidationRejectsNaN() {
    Generator.generateGrid(4, SeedType.DENSITY, Double.NaN);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDensityValidationRejectsPositiveInfinity() {
    Generator.generateGrid(4, SeedType.DENSITY, Double.POSITIVE_INFINITY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDensityValidationRejectsNegativeInfinity() {
    Generator.generateGrid(4, SeedType.DENSITY, Double.NEGATIVE_INFINITY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDensityValidationRejectsNegative() {
    Generator.generateGrid(4, SeedType.DENSITY, -0.01);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDensityValidationRejectsAbove100() {
    Generator.generateGrid(4, SeedType.DENSITY, 100.01);
  }

  @Test
  public void testGenerate1DSeedSupportsUniformSparseAlternating() {
    Vector uniform = Generator.generate1DSeed(SeedType.UNIFORM, 10, 2);
    Assert.assertEquals(10, uniform.getSize());

    Vector sparse = Generator.generate1DSeed(SeedType.SPARSE, 10, 2);
    Assert.assertEquals(10, sparse.getSize());
    long liveCount = sparse.getState().chars().filter(ch -> ch == '1').count();
    Assert.assertEquals(1, liveCount);

    Vector alternating = Generator.generate1DSeed(SeedType.ALTERNATING, 10, 2);
    Assert.assertEquals(10, alternating.getSize());
    String state = alternating.getState();
    Assert.assertTrue(state.equals("1010101010") || state.equals("0101010101"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DSeedRejectsDensity() {
    Generator.generate1DSeed(SeedType.DENSITY, 10, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DSeedWithRandomRejectsDensity() {
    Generator.generate1DSeed(SeedType.DENSITY, 10, 2, new Random());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DSeedRejectsNullSeedType() {
    Generator.generate1DSeed(null, 10, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DSeedRejectsNullRandom() {
    Generator.generate1DSeed(SeedType.UNIFORM, 10, 2, null);
  }

  @Test
  public void testReproducibleResultsWithIdenticalRngSeeds() {
    // 1D UNIFORM
    Vector vec1 = Generator.generate1DSeed(SeedType.UNIFORM, 16, 2, new Random(42));
    Vector vec2 = Generator.generate1DSeed(SeedType.UNIFORM, 16, 2, new Random(42));
    Assert.assertEquals(vec1.getState(), vec2.getState());

    // 1D SPARSE
    Vector sparse1 = Generator.generate1DSeed(SeedType.SPARSE, 16, 2, new Random(42));
    Vector sparse2 = Generator.generate1DSeed(SeedType.SPARSE, 16, 2, new Random(42));
    Assert.assertEquals(sparse1.getState(), sparse2.getState());

    // 2D DENSITY
    Grid2D gridDensity1 = Generator.generateGrid(8, SeedType.DENSITY, 50.0, new Random(42));
    Grid2D gridDensity2 = Generator.generateGrid(8, SeedType.DENSITY, 50.0, new Random(42));
    Assert.assertEquals(gridDensity1, gridDensity2);

    // 2D UNIFORM
    Grid2D gridUniform1 = Generator.generateGrid(8, SeedType.UNIFORM, 0.0, new Random(42));
    Grid2D gridUniform2 = Generator.generateGrid(8, SeedType.UNIFORM, 0.0, new Random(42));
    Assert.assertEquals(gridUniform1, gridUniform2);

    // 2D SPARSE
    Grid2D gridSparse1 = Generator.generateGrid(8, SeedType.SPARSE, 0.0, new Random(42));
    Grid2D gridSparse2 = Generator.generateGrid(8, SeedType.SPARSE, 0.0, new Random(42));
    Assert.assertEquals(gridSparse1, gridSparse2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateGridRejectsNullSeedType() {
    Generator.generateGrid(4, (SeedType) null, 50.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateGridRejectsNullRandom() {
    Generator.generateGrid(4, SeedType.DENSITY, 50.0, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateGridRejectsUnsupportedSeedType() {
    Generator.generateGrid(4, "unknown", 50.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateGridRejectsInvalidSize() {
    Generator.generateGrid(0, SeedType.DENSITY, 50.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateGridSparseRejectsZeroSize() {
    Generator.generateGrid(0, SeedType.SPARSE, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateGridSparseRejectsNegativeSize() {
    Generator.generateGrid(-1, SeedType.SPARSE, 0.0);
  }

  @Test
  public void testGenerateGridSparseRejectsInvalidDimensionsBeforeRandomCoordinates() {
    Random failIfCalled =
        new Random() {
          @Override
          public int nextInt(int bound) {
            throw new AssertionError("Random should not be queried when size is invalid");
          }
        };

    try {
      Generator.generateGrid(0, SeedType.SPARSE, 0.0, failIfCalled);
      Assert.fail("Expected IllegalArgumentException for size 0");
    } catch (IllegalArgumentException expected) {
      // Expected: rejected before random generation
    }

    try {
      Generator.generateGrid(-1, SeedType.SPARSE, 0.0, failIfCalled);
      Assert.fail("Expected IllegalArgumentException for size -1");
    } catch (IllegalArgumentException expected) {
      // Expected: rejected before random generation
    }
  }
}
