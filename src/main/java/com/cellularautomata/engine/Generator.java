package com.cellularautomata.engine;

import java.util.Random;

/**
 * The <code>Generator</code> class is a utility class for generating local update rules, initial
 * seeds, and successors of arbitrary vectors.
 *
 * @author Rainer Pons
 */
public final class Generator {
  /** Shared random number generator. */
  private static final Random RANDOM = new Random();

  /** Prevents the instantiation of the <code>Generator</code> class. */
  private Generator() {
    throw new IllegalStateException("Generator is a utility class.");
  }

  /**
   * Generates a local update rule based on an integer value from 0 to 255 (inclusive).
   *
   * @param rule local update rule number
   * @return rule as binary string
   * @throws IllegalArgumentException if the rule is invalid
   */
  public static String generateRule(int rule) {
    ElementaryRule.validate(rule);
    String binary = Integer.toBinaryString(rule);
    while (binary.length() < 8) {
      binary = "0".concat(binary);
    }
    return binary;
  }

  /**
   * Generates an initial seed where the state of each cell uniformly distributed.
   *
   * @param size amount of individual cells
   * @return initial seed as binary string
   */
  public static Vector generateSeed(int size) {
    String seed = "";
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        double rand = Math.random();
        if (rand < 0.5) {
          seed = seed.concat("1");
        } else {
          seed = seed.concat("0");
        }
      }
    }
    return new Vector(seed);
  }

  /**
   * Generates an initial seed where exactly one cell has state one and the rest have state zero.
   *
   * @param size amount of individual cells
   * @return sparse initial seed as binary string
   */
  public static Vector generateSparseSeed(int size) {
    String seed = "";
    if (size > 0) {
      int rand = RANDOM.nextInt(size);
      for (int i = 0; i < rand; i++) {
        seed = seed.concat("0");
      }
      seed = seed.concat("1");
      for (int i = rand + 1; i < size; i++) {
        seed = seed.concat("0");
      }
    }
    return new Vector(seed);
  }

  /**
   * Generates a seed where each individual cell is different from its neighborhood cells.
   *
   * @param size amount of individual cells
   * @return alternating initial seed as binary string
   */
  public static Vector generateAlternatingSeed(int size) {
    String seed = "";
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        if (i % 2 == 0) {
          seed = seed.concat("1");
        } else {
          seed = seed.concat("0");
        }
      }
    }
    return new Vector(seed);
  }

  /**
   * Generates a successive neighborhood vector given a local update rule and a vector.
   *
   * @param rule local update rule number
   * @param current vector to determine the successor
   * @return successive neighborhood vector based on the rule and the seed
   * @throws IllegalArgumentException if the rule is invalid or the vector is null
   */
  public static Vector generateSuccessor(int rule, Vector current) {
    if (current == null) {
      throw new IllegalArgumentException("Vector cannot be null.");
    }
    String successor = "";
    String temp = "0".concat(current.getState()).concat("0");
    String gen = generateRule(rule);
    for (int i = 0; i < current.getSize(); i++) {
      String sub = temp.substring(i, i + 3);
      successor =
          successor.concat(
              Character.toString(
                  switch (sub) {
                    case "111" -> gen.charAt(0);
                    case "110" -> gen.charAt(1);
                    case "101" -> gen.charAt(2);
                    case "100" -> gen.charAt(3);
                    case "011" -> gen.charAt(4);
                    case "010" -> gen.charAt(5);
                    case "001" -> gen.charAt(6);
                    case "000" -> gen.charAt(7);
                    default -> throw new IllegalStateException();
                  }));
    }
    return new Vector(successor);
  }
}
