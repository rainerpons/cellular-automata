package com.cellularautomata;

/** Utility class for validating cellular automaton rules. */
public class RuleValidator {

  /**
   * Validates if a given text represents a valid rule number. A valid rule is an integer between 0
   * and 255 (inclusive).
   *
   * @param text the rule text to validate
   * @return true if the text is a valid rule, false otherwise
   */
  public static boolean isValidRule(String text) {
    if (text == null || text.trim().isEmpty()) {
      return false;
    }
    try {
      int rule = Integer.parseInt(text.trim());
      return rule >= 0 && rule <= 255;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
