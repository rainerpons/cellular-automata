package com.cellularautomata.engine;

/** Implementation of Rule for Elementary cellular automata. */
public final class ElementaryRule implements Rule {

  private final int ruleNumber;
  private final String binaryRule;

  /**
   * Constructs an ElementaryRule and calculates its binary representation.
   *
   * @param ruleNumber elementary rule number
   * @throws IllegalArgumentException if the rule is invalid
   */
  public ElementaryRule(int ruleNumber) {
    if (!isValid(ruleNumber)) {
      throw new IllegalArgumentException("Invalid rule number: " + ruleNumber);
    }
    this.ruleNumber = ruleNumber;

    String binary = Integer.toBinaryString(ruleNumber);
    while (binary.length() < 8) {
      binary = "0".concat(binary);
    }
    this.binaryRule = binary;
  }

  @Override
  public int getRuleNumber() {
    return ruleNumber;
  }

  @Override
  public char evaluate(String neighborhood) {
    if (neighborhood == null || neighborhood.length() != 3) {
      throw new IllegalArgumentException("Neighborhood must be exactly 3 characters");
    }
    int index = 7 - Integer.parseInt(neighborhood, 2);
    return binaryRule.charAt(index);
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
}
