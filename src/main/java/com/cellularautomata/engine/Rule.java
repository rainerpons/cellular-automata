package com.cellularautomata.engine;

/** Defines the transition behavior for a family of cellular automata rules. */
public interface Rule {

  /**
   * Gets the rule number.
   *
   * @return the rule number
   */
  int getRuleNumber();

  /**
   * Evaluates the neighborhood and returns the next state. Note: The string neighborhood
   * representation is currently intended for one-dimensional, radius-1 binary rules.
   *
   * @param neighborhood binary string of neighborhood states (e.g. "101")
   * @return the next state character (e.g. '0' or '1')
   */
  char evaluate(String neighborhood);
}
