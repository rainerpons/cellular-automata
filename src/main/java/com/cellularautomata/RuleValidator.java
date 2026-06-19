package com.cellularautomata;

import java.util.OptionalInt;

/** Utility class for validating cellular automaton rules. */
public final class RuleValidator {

  private RuleValidator() {
    // Utility class should not be instantiated
  }

  /**
   * Parses a given text to a valid rule number. A valid rule is an integer between 0 and 255
   * (inclusive).
   *
   * @param text the rule text to parse
   * @return an OptionalInt containing the rule if valid, or an empty OptionalInt if invalid
   */
  public static OptionalInt parseRule(String text) {
    if (text == null || text.trim().isEmpty()) {
      return OptionalInt.empty();
    }
    try {
      int rule = Integer.parseInt(text.trim());
      if (rule >= 0 && rule <= 255) {
        return OptionalInt.of(rule);
      }
    } catch (NumberFormatException e) {
      // Ignored, will return empty below
    }
    return OptionalInt.empty();
  }
}
