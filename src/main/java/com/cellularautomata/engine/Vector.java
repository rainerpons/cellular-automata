package com.cellularautomata.engine;

/**
 * Represents a one-dimensional vector of cellular automaton states. Each cell contains a
 * non-negative integer state encoded as a digit.
 *
 * @author Rainer Pons
 */
public final class Vector {
  /** Stores the state of each cell as a string of digits. */
  private final String state;

  /**
   * Creates a <code>Vector</code> object from a specified state.
   *
   * @param state collection of states for each individual cell
   * @throws IllegalArgumentException if the state is invalid or null
   */
  public Vector(String state) {
    this.state = initializeVector(state);
  }

  /**
   * Returns the neighborhood vector as a <code>String</code>.
   *
   * @return collection of states for each individual cell
   */
  public String getState() {
    return state;
  }

  /**
   * Returns the length (amount of cells) of a neighborhood vector.
   *
   * @return length of a neighborhood vector
   */
  public int getSize() {
    return state.length();
  }

  /**
   * Indicates if a string contains only valid state digits.
   *
   * @param state collection of states for each individual cell
   * @return true if <code>state</code> has only digits, or false otherwise
   */
  public static boolean isValid(String state) {
    if (state == null) {
      return false;
    }
    for (int i = 0; i < state.length(); i++) {
      char c = state.charAt(i);
      if (!Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Initializes a <code>Vector</code> object given that the state is valid.
   *
   * @param state collection of states for each individual cell
   * @return state if valid
   * @throws IllegalArgumentException if the state is invalid or null
   */
  public static String initializeVector(String state) {
    if (state == null) {
      throw new IllegalArgumentException("State string cannot be null.");
    }
    if (!isValid(state)) {
      throw new IllegalArgumentException("Invalid state string.");
    }
    return state;
  }
}
