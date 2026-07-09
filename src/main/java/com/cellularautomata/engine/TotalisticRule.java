package com.cellularautomata.engine;

/** Implementation of Rule for Totalistic cellular automata. */
public final class TotalisticRule implements Rule {

  private final int ruleNumber;
  private final String binaryRule;

  /**
   * Constructs a TotalisticRule and calculates its binary representation.
   *
   * @param ruleNumber totalistic rule number
   * @throws IllegalArgumentException if the rule is invalid
   */
  public TotalisticRule(int ruleNumber) {
    if (!isValid(ruleNumber)) {
      throw new IllegalArgumentException("Invalid rule number: " + ruleNumber);
    }
    this.ruleNumber = ruleNumber;

    String binary = Integer.toBinaryString(ruleNumber);
    while (binary.length() < 4) {
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
    int sum = 0;
    for (int i = 0; i < neighborhood.length(); i++) {
      if (neighborhood.charAt(i) == '1') {
        sum++;
      } else if (neighborhood.charAt(i) != '0') {
        throw new IllegalArgumentException("Neighborhood state must be binary (0 or 1)");
      }
    }
    int index = 3 - sum;
    return binaryRule.charAt(index);
  }

  /**
   * Indicates if a totalistic cellular automaton rule is valid.
   *
   * @param rule totalistic rule number
   * @return true if the rule is between 0 and 15 (inclusive)
   */
  public static boolean isValid(int rule) {
    return rule >= 0 && rule <= 15;
  }
}
