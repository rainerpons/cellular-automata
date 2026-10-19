package com.cellularautomata.engine.automata;

import com.cellularautomata.engine.Generator;
import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.Vector;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.Rule;
import java.util.Map;
import java.util.Random;

/** Orchestration layer for cellular automata generation and simulation. */
public final class AutomataEngine {

  private AutomataEngine() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Generates a 1D automaton result based on rule, size, states, seed type, and Random instance.
   *
   * @param rule the rule to apply
   * @param size the size of the vector
   * @param states the number of states to generate the seed for
   * @param seedType the seed type
   * @param random the random number generator
   * @return the generated AutomataResult
   * @throws IllegalArgumentException if rule is null, or if seed generation arguments are invalid
   */
  public static AutomataResult generate(
      Rule rule, int size, int states, SeedType seedType, Random random) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    Vector seed = Generator.generate1DSeed(seedType, size, states, random);
    Map<Integer, Vector> map = Automaton.initializeVectorMap(rule, seed);
    return new AutomataResult(map, seed);
  }

  /**
   * Generates a 1D automaton result using default randomness.
   *
   * @param rule the rule to apply
   * @param size the size of the vector
   * @param states the number of states to generate the seed for
   * @param seedType the seed type
   * @return the generated AutomataResult
   * @throws IllegalArgumentException if rule is null, or if seed generation arguments are invalid
   */
  public static AutomataResult generate(Rule rule, int size, int states, SeedType seedType) {
    return generate(rule, size, states, seedType, new Random());
  }

  /**
   * Generates a 1D automaton result using a string seed identifier and default randomness.
   *
   * @param rule the rule to apply
   * @param size the size of the vector
   * @param states the number of states to generate the seed for
   * @param seedType string identifier for the seed ("uniform", "sparse", "alternating")
   * @return the generated AutomataResult
   * @throws IllegalArgumentException if rule is null, or if seedType is null or unrecognized
   */
  public static AutomataResult generate(Rule rule, int size, int states, String seedType) {
    return generate(rule, size, states, SeedType.fromIdentifier(seedType));
  }

  /**
   * Generates a 1D automaton result based on rule, size, states, string seed, and Random instance.
   *
   * @param rule the rule to apply
   * @param size the size of the vector
   * @param states the number of states to generate the seed for
   * @param seedType the string identifier for the seed
   * @param random the random number generator
   * @return the generated AutomataResult
   * @throws IllegalArgumentException if rule is null, or if seedType or random is null or
   *     unrecognized
   */
  public static AutomataResult generate(
      Rule rule, int size, int states, String seedType, Random random) {
    return generate(rule, size, states, SeedType.fromIdentifier(seedType), random);
  }

  /**
   * Creates a Life-like 2D cellular automaton simulation with the specified parameters.
   *
   * @param rule the Life-like rule
   * @param size side length of the square grid
   * @param seedType the seed type
   * @param densityPercentage live cell density percentage (0 to 100)
   * @param random random number generator
   * @return initialized LifeLikeSimulation
   * @throws IllegalArgumentException if rule is null, or if grid generation arguments are invalid
   */
  public static LifeLikeSimulation createLifeLikeSimulation(
      LifeLikeRule rule, int size, SeedType seedType, double densityPercentage, Random random) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    Grid2D grid = Generator.generateGrid(size, seedType, densityPercentage, random);
    return new LifeLikeSimulation(rule, grid);
  }

  /**
   * Creates a Life-like 2D cellular automaton simulation using default randomness.
   *
   * @param rule the Life-like rule
   * @param size side length of the square grid
   * @param seedType the seed type
   * @param densityPercentage live cell density percentage (0 to 100)
   * @return initialized LifeLikeSimulation
   * @throws IllegalArgumentException if rule is null, or if grid generation arguments are invalid
   */
  public static LifeLikeSimulation createLifeLikeSimulation(
      LifeLikeRule rule, int size, SeedType seedType, double densityPercentage) {
    return createLifeLikeSimulation(rule, size, seedType, densityPercentage, new Random());
  }

  /**
   * Creates a Life-like 2D cellular automaton simulation with string seed type and Random instance.
   *
   * @param rule the Life-like rule
   * @param size side length of the square grid
   * @param seedType seed type string ("density", "uniform", "sparse", "alternating")
   * @param densityPercentage live cell density percentage (0 to 100)
   * @param random random number generator
   * @return initialized LifeLikeSimulation
   * @throws IllegalArgumentException if rule is null, or if seedType, random, or grid parameters
   *     are invalid
   */
  public static LifeLikeSimulation createLifeLikeSimulation(
      LifeLikeRule rule, int size, String seedType, double densityPercentage, Random random) {
    return createLifeLikeSimulation(
        rule, size, SeedType.fromIdentifier(seedType), densityPercentage, random);
  }

  /**
   * Creates a Life-like 2D cellular automaton simulation with string seed type.
   *
   * @param rule the Life-like rule
   * @param size side length of the square grid
   * @param seedType seed type string ("density", "uniform", "sparse", "alternating")
   * @param densityPercentage live cell density percentage (0 to 100)
   * @return initialized LifeLikeSimulation
   * @throws IllegalArgumentException if rule is null, or if seedType or grid parameters are invalid
   */
  public static LifeLikeSimulation createLifeLikeSimulation(
      LifeLikeRule rule, int size, String seedType, double densityPercentage) {
    return createLifeLikeSimulation(
        rule, size, SeedType.fromIdentifier(seedType), densityPercentage, new Random());
  }
}
