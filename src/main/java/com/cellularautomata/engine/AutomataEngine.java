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

    Vector seed;
    if (seedType.equalsIgnoreCase("uniform")) {
      seed = Generator.generateSeed(size);
    } else if (seedType.equalsIgnoreCase("sparse")) {
      seed = Generator.generateSparseSeed(size);
    } else if (seedType.equalsIgnoreCase("alternating")) {
      seed = Generator.generateAlternatingSeed(size);
    } else {
      throw new IllegalArgumentException("Unsupported seed type: " + seedType);
    }

    Map<Integer, Vector> map = Automaton.initializeVectorMap(rule, seed);
    return new AutomataResult(map, seed);
  }
}
