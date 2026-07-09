package com.cellularautomata.engine;

import com.cellularautomata.engine.rules.Rule;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>Automaton</code> class uses the <code>Vector</code> and <code>Generator</code> classes
 * in order to store associated value pairs. The keys hold generation count and values hold vectors.
 *
 * @author Rainer Pons
 */
public final class Automaton {
  /** Creates a map which associates the generation count of a vector with the vector itself. */
  private Map<Integer, Vector> map;

  /**
   * Creates an automaton given a rule and an initial seed.
   *
   * @param rule the rule to apply
   * @param seed initial neighborhood vector
   */
  public Automaton(Rule rule, Vector seed) {
    map = initializeVectorMap(rule, seed);
  }

  /**
   * Enters values and their successors into the map.
   *
   * @param rule the rule to apply
   * @param seed initial neighborhood vector
   * @return a map with associated value pairs of generation counts and neighborhood vectors
   * @throws IllegalArgumentException if the rule is null or the seed is null
   */
  public static Map<Integer, Vector> initializeVectorMap(Rule rule, Vector seed) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    if (seed == null) {
      throw new IllegalArgumentException("Initial seed cannot be null.");
    }
    var map = new HashMap<Integer, Vector>();
    var successor = Generator.generateSuccessor(rule, seed);
    int generation = 0;

    map.put(generation, seed);
    while (generation < seed.getSize() - 1) {
      generation++;
      map.put(generation, successor);
      successor = Generator.generateSuccessor(rule, successor);
    }
    return map;
  }

  /** Outputs the generation count of neighborhood vectors and the vectors themselves as text. */
  public void displayVectorMap() {
    for (Map.Entry<Integer, Vector> entry : map.entrySet()) {
      System.out.println("Generation " + entry.getKey() + ": " + entry.getValue().getState());
    }
  }
}
