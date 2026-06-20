package com.cellularautomata.engine;

/** Utility class for elementary cellular automaton rule validation. */
public final class ElementaryRule {

  private ElementaryRule() {
    throw new IllegalStateException("ElementaryRule is a utility class.");
  }

  /**
   * Indicates if an elementary cellular automaton rule is valid.
   *
   * @param rule elementary rule number
   * @return true if the rule is between 0 and 255 (inclusive)
   */
  public static boolean isValid(int rule) {
    return rule >= 0 && rule <= 255;
  }

  /**
   * Validates an elementary cellular automaton rule.
   *
   * @param rule elementary rule number
   * @throws IllegalArgumentException if the rule is invalid
   */
  public static void validate(int rule) {
    if (!isValid(rule)) {
      throw new IllegalArgumentException("Invalid rule number: " + rule);
    }
  }
}
