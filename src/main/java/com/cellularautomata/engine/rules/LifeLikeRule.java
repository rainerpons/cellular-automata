package com.cellularautomata.engine.rules;

/** Constants and domain bounds for Life-like cellular automata rules. */
public final class LifeLikeRule {
  /** Number of cell states in binary Life-like cellular automata (dead or alive). */
  public static final int STATE_COUNT = 2;

  /** Minimum possible number of living neighbors in a 2D Moore neighborhood. */
  public static final int MIN_NEIGHBOR_COUNT = 0;

  /** Maximum possible number of living neighbors in a 2D Moore neighborhood. */
  public static final int MAX_NEIGHBOR_COUNT = 8;

  private LifeLikeRule() {}
}
