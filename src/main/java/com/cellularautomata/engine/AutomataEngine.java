package com.cellularautomata.engine;

import java.util.Map;

/** Orchestration layer for cellular automata generation. */
public class AutomataEngine {

  /**
   * Generates an automaton map based on rule, size, and seed type.
   *
   * @param rule the elementary cellular automaton rule (0-255)
   * @param size the size of the vector
   * @param seedType the string identifier for the seed ("uniform", "sparse", "alternating")
   * @return the generated automaton map
   */
  public static Map<Integer, Vector> generate(int rule, int size, String seedType) {
    Vector seed = null;
    if (seedType.equalsIgnoreCase("uniform")) {
      seed = Generator.generateSeed(size);
    } else if (seedType.equalsIgnoreCase("sparse")) {
      seed = Generator.generateSparseSeed(size);
    } else if (seedType.equalsIgnoreCase("alternating")) {
      seed = Generator.generateAlternatingSeed(size);
    }

    if (seed == null) {
      seed = Generator.generateSeed(size);
    }

    return Automaton.initializeVectorMap(rule, seed);
  }
}
