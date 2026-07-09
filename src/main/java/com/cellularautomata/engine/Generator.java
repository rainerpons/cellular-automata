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
   * Generates an initial seed where the state of each cell uniformly distributed.
   *
   * @param size amount of individual cells
   * @return initial seed as binary string
   */
  public static Vector generateSeed(int size) {
    StringBuilder seed = new StringBuilder(size > 0 ? size : 0);
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        if (RANDOM.nextBoolean()) {
          seed.append('1');
        } else {
          seed.append('0');
        }
      }
    }
    return new Vector(seed.toString());
  }

  /**
   * Generates an initial seed where exactly one cell has state one and the rest have state zero.
   *
   * @param size amount of individual cells
   * @return sparse initial seed as binary string
   */
  public static Vector generateSparseSeed(int size) {
    StringBuilder seed = new StringBuilder(size > 0 ? size : 0);
    if (size > 0) {
      var rand = RANDOM.nextInt(size);
      for (int i = 0; i < rand; i++) {
        seed.append('0');
      }
      seed.append('1');
      for (int i = rand + 1; i < size; i++) {
        seed.append('0');
      }
    }
    return new Vector(seed.toString());
  }

  /**
   * Generates a seed where each individual cell is different from its neighborhood cells.
   *
   * @param size amount of individual cells
   * @return alternating initial seed as binary string
   */
  public static Vector generateAlternatingSeed(int size) {
    StringBuilder seed = new StringBuilder(size > 0 ? size : 0);
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        if (i % 2 == 0) {
          seed.append('1');
        } else {
          seed.append('0');
        }
      }
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
    var temp = "0".concat(current.getState()).concat("0");
    for (int i = 0; i < current.getSize(); i++) {
      var neighborhood = temp.substring(i, i + 3);
      successor.append(rule.evaluate(neighborhood));
    }
    return new Vector(successor.toString());
  }
}
