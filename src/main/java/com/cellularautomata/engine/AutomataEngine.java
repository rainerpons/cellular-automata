package com.cellularautomata.engine;

import java.util.Map;

/** Orchestration layer for cellular automata generation. */
public final class AutomataEngine {

  private AutomataEngine() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Generates an automaton map based on rule, size, and seed type.
   *
   * @param rule the elementary cellular automaton rule (0-255)
   * @param size the size of the vector
   * @param seedType the string identifier for the seed ("uniform", "sparse", "alternating")
   * @return the generated AutomataResult
   * @throws IllegalArgumentException if seedType is null or unrecognized
   */
  public static AutomataResult generate(int rule, int size, String seedType) {
    if (seedType == null) {
      throw new IllegalArgumentException("Seed type cannot be null");
    }

    var seed =
        switch (seedType.toLowerCase(java.util.Locale.ROOT)) {
          case "uniform" -> Generator.generateSeed(size);
          case "sparse" -> Generator.generateSparseSeed(size);
          case "alternating" -> Generator.generateAlternatingSeed(size);
          default -> throw new IllegalArgumentException("Unsupported seed type: " + seedType);
        };

    Map<Integer, Vector> map = Automaton.initializeVectorMap(rule, seed);
    return new AutomataResult(map, seed);
  }
}
