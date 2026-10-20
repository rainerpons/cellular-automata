package com.cellularautomata.engine;

import com.cellularautomata.engine.rules.Rule;
import java.util.Random;

/**
 * Utility class for generating local update rules, initial seeds, and successors of 1D vectors and
 * 2D lattices.
 */
public final class Generator {
  /** Shared random number generator. */
  private static final Random RANDOM = new Random();

  /** State for the alternating seed to toggle between phases on each generation. */
  private static boolean alternatingStartsWithOne = true;

  private Generator() {
    throw new IllegalStateException("Generator is a utility class.");
  }

  /**
   * Generates a 1D seed based on the specified seed type, size, states, and Random instance.
   *
   * @param seedType 1D-compatible seed type
   * @param size size of the seed
   * @param states number of states
   * @param random random number generator
   * @return generated initial seed as Vector
   * @throws IllegalArgumentException if seedType or random is null, or if seedType is DENSITY
   */
  // CHECKSTYLE.SUPPRESS: AbbreviationAsWordInName
  public static Vector generate1DSeed(SeedType seedType, int size, int states, Random random) {
    if (seedType == null) {
      throw new IllegalArgumentException("Seed type cannot be null.");
    }
    if (random == null) {
      throw new IllegalArgumentException("Random cannot be null.");
    }
    return switch (seedType) {
      case DENSITY ->
          throw new IllegalArgumentException(
              "Unsupported seed type for 1D automata: %s".formatted(seedType.getIdentifier()));
      case UNIFORM -> generateSeed(size, states, random);
      case SPARSE -> generateSparseSeed(size, states, random);
      case ALTERNATING -> generateAlternatingSeed(size, states);
    };
  }

  /**
   * Generates a 1D seed based on seed type, size, and states using default randomness.
   *
   * @param seedType 1D-compatible seed type
   * @param size size of the seed
   * @param states number of states
   * @return generated initial seed as Vector
   * @throws IllegalArgumentException if seedType is null or DENSITY
   */
  // CHECKSTYLE.SUPPRESS: AbbreviationAsWordInName
  public static Vector generate1DSeed(SeedType seedType, int size, int states) {
    return generate1DSeed(seedType, size, states, RANDOM);
  }

  /**
   * Generates an initial seed where the state of each cell is uniformly distributed.
   *
   * @param size amount of individual cells
   * @param states the number of states (2 to N)
   * @return initial seed as string
   */
  public static Vector generateSeed(int size, int states) {
    return generateSeed(size, states, RANDOM);
  }

  /**
   * Generates an initial seed where cell states are uniformly distributed with the given Random.
   *
   * @param size amount of individual cells
   * @param states the number of states (2 to N)
   * @param random random number generator
   * @return initial seed as string
   * @throws IllegalArgumentException if random is null
   */
  public static Vector generateSeed(int size, int states, Random random) {
    if (random == null) {
      throw new IllegalArgumentException("Random cannot be null.");
    }
    StringBuilder seed = new StringBuilder(size > 0 ? size : 0);
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        int val = random.nextInt(states);
        seed.append(Character.forDigit(val, 10));
      }
    }
    return new Vector(seed.toString());
  }

  /**
   * Generates a seed containing all zeroes except for a single active cell at a random position.
   *
   * @param size size of the seed string
   * @param states number of states (used to determine the maximum active state value)
   * @return a Vector containing the sparse seed
   */
  public static Vector generateSparseSeed(int size, int states) {
    return generateSparseSeed(size, states, RANDOM);
  }

  /**
   * Generates a sparse seed with a single active cell at a random position using the given Random.
   *
   * @param size size of the seed string
   * @param states number of states (used to determine the maximum active state value)
   * @param random random number generator
   * @return a Vector containing the sparse seed
   * @throws IllegalArgumentException if random is null
   */
  public static Vector generateSparseSeed(int size, int states, Random random) {
    if (random == null) {
      throw new IllegalArgumentException("Random cannot be null.");
    }
    StringBuilder seed = new StringBuilder(size > 0 ? size : 0);
    if (size > 0) {
      var rand = random.nextInt(size);
      for (int i = 0; i < rand; i++) {
        seed.append('0');
      }
      seed.append(Character.forDigit(states - 1, 10));
      for (int i = rand + 1; i < size; i++) {
        seed.append('0');
      }
    }
    return new Vector(seed.toString());
  }

  /**
   * Generates a seed where each cell alternates states.
   *
   * @param size amount of individual cells
   * @param states the number of states
   * @return alternating initial seed as string
   */
  public static Vector generateAlternatingSeed(int size, int states) {
    StringBuilder seed = new StringBuilder(size > 0 ? size : 0);
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        if ((i % 2 == 0) == alternatingStartsWithOne) {
          seed.append(Character.forDigit(states - 1, 10));
        } else {
          seed.append('0');
        }
      }
      alternatingStartsWithOne = !alternatingStartsWithOne;
    }
    return new Vector(seed.toString());
  }

  /**
   * Generates a successive neighborhood vector given a rule and a vector.
   *
   * @param rule the rule to apply
   * @param current vector to determine the successor
   * @return successive neighborhood vector based on the rule and the seed
   * @throws IllegalArgumentException if the rule or the vector is null
   */
  public static Vector generateSuccessor(Rule rule, Vector current) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    if (current == null) {
      throw new IllegalArgumentException("Vector cannot be null.");
    }
    StringBuilder successor = new StringBuilder(current.getSize());
    var temp = "0%s0".formatted(current.getState());
    for (int i = 0; i < current.getSize(); i++) {
      var neighborhood = temp.substring(i, i + 3);
      successor.append(rule.evaluate(neighborhood));
    }
    return new Vector(successor.toString());
  }

  /**
   * Generates a 2D grid seed based on size, seed type, alive density, and Random instance.
   *
   * @param size side length of the square grid
   * @param seedType seed type
   * @param densityPercentage live cell density percentage (0 to 100)
   * @param random random number generator
   * @return generated Grid2D
   * @throws IllegalArgumentException if parameters are invalid or null
   */
  public static Grid2D generateGrid(
      int size, SeedType seedType, double densityPercentage, Random random) {
    if (size < 1) {
      throw new IllegalArgumentException("Grid size must be at least 1: %d".formatted(size));
    }
    if (seedType == null) {
      throw new IllegalArgumentException("Seed type cannot be null.");
    }
    if (random == null) {
      throw new IllegalArgumentException("Random cannot be null.");
    }
    return switch (seedType) {
      case DENSITY -> {
        double probability = validateAndNormalizeDensity(densityPercentage);
        if (probability == 0.0) {
          yield Grid2D.empty(size);
        }
        yield new Grid2D(size, (r, c) -> probability == 1.0 || random.nextDouble() < probability);
      }
      case UNIFORM -> new Grid2D(size, (r, c) -> random.nextBoolean());
      case SPARSE -> {
        int targetRow = random.nextInt(size);
        int targetCol = random.nextInt(size);
        yield new Grid2D(size, (r, c) -> r == targetRow && c == targetCol);
      }
      case ALTERNATING -> new Grid2D(size, (r, c) -> (r + c) % 2 == 0);
    };
  }

  /**
   * Generates a 2D grid seed based on size, seed type, and alive density percentage.
   *
   * @param size side length of the square grid
   * @param seedType seed type
   * @param densityPercentage live cell density percentage (0 to 100)
   * @return generated Grid2D
   */
  public static Grid2D generateGrid(int size, SeedType seedType, double densityPercentage) {
    return generateGrid(size, seedType, densityPercentage, new Random());
  }

  /**
   * Backwards-compatible overload generating a 2D grid seed from a string seed type.
   *
   * @param size side length of the square grid
   * @param seedType seed type string ("density", "uniform", "sparse", "alternating")
   * @param densityPercentage live cell density percentage (0 to 100)
   * @param random random number generator
   * @return generated Grid2D
   */
  public static Grid2D generateGrid(
      int size, String seedType, double densityPercentage, Random random) {
    return generateGrid(size, SeedType.fromIdentifier(seedType), densityPercentage, random);
  }

  /**
   * Backwards-compatible overload generating a 2D grid seed using default randomness.
   *
   * @param size side length of the square grid
   * @param seedType seed type string ("density", "uniform", "sparse", "alternating")
   * @param densityPercentage live cell density percentage (0 to 100)
   * @return generated Grid2D
   */
  public static Grid2D generateGrid(int size, String seedType, double densityPercentage) {
    return generateGrid(size, SeedType.fromIdentifier(seedType), densityPercentage);
  }

  private static double validateAndNormalizeDensity(double densityPercentage) {
    if (Double.isNaN(densityPercentage)
        || Double.isInfinite(densityPercentage)
        || densityPercentage < 0.0
        || densityPercentage > 100.0) {
      throw new IllegalArgumentException(
          "Density percentage must be between 0.0 and 100.0: %s".formatted(densityPercentage));
    }
    return densityPercentage / 100.0;
  }
}
