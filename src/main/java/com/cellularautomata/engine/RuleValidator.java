package com.cellularautomata.engine;

import java.util.OptionalInt;

/** Utility class for validating cellular automaton rules. */
public final class RuleValidator {

  private RuleValidator() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Parses a given text to a valid rule number within the specified bounds.
   *
   * @param text the rule text to parse
   * @param minRule the minimum allowed rule (inclusive)
   * @param maxRule the maximum allowed rule (inclusive)
   * @return an OptionalInt containing the rule if valid, or an empty OptionalInt if invalid
   */
  public static OptionalInt parseRule(String text, int minRule, int maxRule) {
    if (text == null || text.isBlank()) {
      return OptionalInt.empty();
    }
    try {
      var rule = Integer.parseInt(text.trim());
      if (rule >= minRule && rule <= maxRule) {
        return OptionalInt.of(rule);
      }
    } catch (NumberFormatException e) {
      // Ignored, will return empty below
    }
    return OptionalInt.empty();
  }
}
