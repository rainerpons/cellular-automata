package com.cellularautomata.generator;

/**
 * The <code>Vector</code> class represents a neighborhood vector of cells for an elementary
 * cellular automaton. Each cell has a value within the cell state set <code>{0, 1}</code>.
 * @author Rainer Pons
 */

public class Vector {
	/**
	 * Stores a binary string representing the current state of an entire neighborhood vector.
	 */
	private String state;

	/**
	 * Creates a <code>Vector</code> object from a specified state.
	 * @param state collection of states for each individual cell
	 */
	public Vector(String state) {
		this.state = initializeVector(state);
	}

	/**
	 * Returns the neighborhood vector as a <code>String</code>.
	 * @return collection of states for each individual cell
	 */
	public String getState() {
		return state;
	}

	/**
	 * Returns the length (amount of cells) of a neighborhood vector.
	 * @return length of a neighborhood vector
	 */
	public int getSize() {
		return state.length();
	}

	/**
	 * Indicates if a string contains only binary digits.
	 * @param state collection of states for each individual cell
	 * @return true if <code>state</code> has only ones and zeroes, or false otherwise
	 */
	public static boolean isValid(String state) {
		for (int i = 0; i < state.length(); i++) {
			String s = Character.toString(state.charAt(i));
			if (!"0".equals(s) && !"1".equals(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Initializes a <code>Vector</code> object given that the state is valid.
	 * @param state collection of states for each individual cell
	 * @return state if valid, or null otherwise
	 */
	public static String initializeVector(String state) {
		return isValid(state) ? state : null;
	}
}