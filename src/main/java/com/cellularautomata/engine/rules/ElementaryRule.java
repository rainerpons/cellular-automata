package com.cellularautomata.engine.rules;

import com.cellularautomata.engine.NeighborhoodValidator;

/** Implementation of Rule for Elementary cellular automata. */
public final class ElementaryRule implements Rule {

  public static final int MIN_RULE_NUMBER = 0;
  public static final int MAX_RULE_NUMBER = 255;
  public static final int DEFAULT_RULE_NUMBER = 30;
  private static final int RULE_WIDTH = 8;

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
    int index = (binaryRule.length() - 1) - Integer.parseInt(neighborhood, 2);
    return binaryRule.charAt(index);
  }

  /**
   * Indicates if an elementary cellular automaton rule is valid.
   *
   * @param rule elementary rule number
   * @return true if the rule is between 0 and 255 (inclusive)
   */
  public static boolean isValid(int rule) {
    return rule >= MIN_RULE_NUMBER && rule <= MAX_RULE_NUMBER;
  }
}
