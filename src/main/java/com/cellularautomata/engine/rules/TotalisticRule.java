package com.cellularautomata.engine.rules;

import com.cellularautomata.engine.NeighborhoodValidator;
import java.math.BigInteger;

/** Implementation of Rule for Totalistic cellular automata. */
public final class TotalisticRule implements Rule {

  public static final int MIN_RULE_NUMBER = 0;
  public static final int DEFAULT_RULE_NUMBER = 5;

  public static final int MIN_STATES = 2;
  public static final int MAX_STATES = 5;
  public static final int DEFAULT_STATES = 2;

  private final int ruleNumber;
  private final int states;
  private final String ruleRepresentation;

  /**
   * Constructs a TotalisticRule for the specified number of states and calculates its
   * representation.
   *
   * @param ruleNumber totalistic rule number
   * @param states the number of states
   * @throws IllegalArgumentException if the rule is invalid
   */
  public TotalisticRule(int ruleNumber, int states) {
    if (states < MIN_STATES || states > MAX_STATES) {
      throw new IllegalArgumentException(
          "States must be between " + MIN_STATES + " and " + MAX_STATES);
    }
    if (ruleNumber < MIN_RULE_NUMBER || ruleNumber > calculateMaxRule(states)) {
      throw new IllegalArgumentException("Invalid rule number: " + ruleNumber);
    }

    this.ruleNumber = ruleNumber;
    this.states = states;
    this.ruleRepresentation = toBaseRepresentation(ruleNumber, states);
  }

  /**
   * Calculates the maximum valid rule number for the given number of states. Bounds the result to
   * Integer.MAX_VALUE.
   *
   * @param states the number of states
   * @return the maximum rule number
   */
  public static int calculateMaxRule(int states) {
    BigInteger max = BigInteger.valueOf(states).pow(3 * (states - 1) + 1).subtract(BigInteger.ONE);

    if (max.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
      return Integer.MAX_VALUE;
    }

    return max.intValue();
  }

  @Override
  public int getRuleNumber() {
    return ruleNumber;
  }

  @Override
  public char evaluate(String neighborhood) {
    NeighborhoodValidator.validateMultiStateRadiusOne(neighborhood, states);
    int sum = 0;
    for (int i = 0; i < neighborhood.length(); i++) {
      sum += (neighborhood.charAt(i) - '0');
    }
    int index = (ruleRepresentation.length() - 1) - sum;
    return ruleRepresentation.charAt(index);
  }

  private static String toBaseRepresentation(int ruleNumber, int states) {
    int length = 3 * (states - 1) + 1;
    StringBuilder sb = new StringBuilder(length);
    int current = ruleNumber;
    for (int i = 0; i < length; i++) {
      int remainder = current % states;
      sb.insert(0, Character.forDigit(remainder, 10));
      current /= states;
    }
    return sb.toString();
  }
}
