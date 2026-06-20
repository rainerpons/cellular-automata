package com.cellularautomata.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Encapsulates the result of generating a cellular automaton. */
public class AutomataResult {
  private final Map<Integer, Vector> automatonMap;
  private final Vector originalSeed;

  public AutomataResult(Map<Integer, Vector> automatonMap, Vector originalSeed) {
    this.automatonMap = new HashMap<>(automatonMap);
    this.originalSeed = originalSeed;
  }

  public Map<Integer, Vector> getAutomatonMap() {
    return Collections.unmodifiableMap(automatonMap);
  }

  public Vector getOriginalSeed() {
    return originalSeed;
  }
}
