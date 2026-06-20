package com.cellularautomata.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Encapsulates the result of generating a cellular automaton. */
public final class AutomataResult {
  private final Map<Integer, Vector> automatonMap;
  private final Vector originalSeed;

  public AutomataResult(Map<Integer, Vector> automatonMap, Vector originalSeed) {
    if (automatonMap == null) {
      throw new IllegalArgumentException("automatonMap cannot be null");
    }
    if (originalSeed == null) {
      throw new IllegalArgumentException("originalSeed cannot be null");
    }
    this.automatonMap = Collections.unmodifiableMap(new HashMap<>(automatonMap));
    this.originalSeed = originalSeed;
  }

  public Map<Integer, Vector> getAutomatonMap() {
    return automatonMap;
  }

  public Vector getOriginalSeed() {
    return originalSeed;
  }
}
