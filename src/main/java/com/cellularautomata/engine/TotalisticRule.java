package com.cellularautomata.engine;

/** Implementation of Rule for Totalistic cellular automata. */
public final class TotalisticRule implements Rule {

  public static final int MIN_RULE_NUMBER = 0;
  public static final int MAX_RULE_NUMBER = 15;
  public static final int DEFAULT_RULE_NUMBER = 7;
  public static final int RULE_WIDTH = 4;

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
    while (binary.length() < RULE_WIDTH) {
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
    NeighborhoodValidator.validateBinaryRadiusOne(neighborhood);
    int sum = 0;
    for (int i = 0; i < neighborhood.length(); i++) {
      if (neighborhood.charAt(i) == '1') {
        sum++;
      }
    }
    int index = (binaryRule.length() - 1) - sum;
    return binaryRule.charAt(index);
  }

  /**
   * Indicates if a totalistic cellular automaton rule is valid.
   *
   * @param rule totalistic rule number
   * @return true if the rule is between 0 and 15 (inclusive)
   */
  public static boolean isValid(int rule) {
    return rule >= MIN_RULE_NUMBER && rule <= MAX_RULE_NUMBER;
  }
}
