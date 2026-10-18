package com.cellularautomata.engine.automata;

import com.cellularautomata.engine.Generator;
import com.cellularautomata.engine.Grid2D;
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
   * Generates an automaton map based on rule, size, and seed type.
   *
   * @param rule the rule to apply
   * @param size the size of the vector
   * @param states the number of states to generate the seed for
   * @param seedType the string identifier for the seed ("uniform", "sparse", "alternating")
   * @return the generated AutomataResult
   * @throws IllegalArgumentException if seedType is null or unrecognized
   */
  public static AutomataResult generate(Rule rule, int size, int states, String seedType) {
    if (seedType == null) {
      throw new IllegalArgumentException("Seed type cannot be null");
    }

    var seed =
        switch (seedType.toLowerCase(java.util.Locale.ROOT)) {
          case "uniform" -> Generator.generateSeed(size, states);
          case "sparse" -> Generator.generateSparseSeed(size, states);
          case "alternating" -> Generator.generateAlternatingSeed(size, states);
          default -> throw new IllegalArgumentException("Unsupported seed type: " + seedType);
        };

    Map<Integer, Vector> map = Automaton.initializeVectorMap(rule, seed);
    return new AutomataResult(map, seed);
  }

  /**
   * Creates a Life-like 2D cellular automaton simulation with the specified rule, grid size, seed
   * type, and alive cell density percentage.
   *
   * @param rule the Life-like rule
   * @param size side length of the square grid
   * @param seedType seed type string ("density", "uniform", "sparse", "alternating")
   * @param densityPercentage live cell density percentage (0 to 100)
   * @return initialized LifeLikeSimulation
   * @throws IllegalArgumentException if rule or seedType is null or invalid
   */
  public static LifeLikeSimulation createLifeLikeSimulation(
      LifeLikeRule rule, int size, String seedType, double densityPercentage) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    Grid2D grid = Generator.generateGrid(size, seedType, densityPercentage);
    return new LifeLikeSimulation(rule, grid);
  }

  /**
   * Creates a Life-like 2D cellular automaton simulation with the specified rule, grid size, seed
   * type, alive cell density percentage, and Random instance.
   *
   * @param rule the Life-like rule
   * @param size side length of the square grid
   * @param seedType seed type string ("density", "uniform", "sparse", "alternating")
   * @param densityPercentage live cell density percentage (0 to 100)
   * @param random random number generator
   * @return initialized LifeLikeSimulation
   * @throws IllegalArgumentException if rule, seedType, or random is null or invalid
   */
  public static LifeLikeSimulation createLifeLikeSimulation(
      LifeLikeRule rule, int size, String seedType, double densityPercentage, Random random) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    Grid2D grid = Generator.generateGrid(size, seedType, densityPercentage, random);
    return new LifeLikeSimulation(rule, grid);
  }
}
