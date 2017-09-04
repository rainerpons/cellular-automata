
package automata;

/*
 * Vector.java
 * The Vector class represents a neighborhood vector of cells for an elementary
 * cellular automaton with the cell state {0, 1}.
 */

public class Vector {
	// store a string of 0s and 1s
	private String vector;

	// create vector from a specified String
	public Vector(String vector) {
		this.vector = initializeVector(vector);
	}

	// return the vector as a String
	public String getVector() {
		return vector;
	}

	// return the size of the vector
	public int getSize() {
		return vector.length();
	}

	// indicate if a string only contains 0s and 1s
	public static boolean isValid(String vector) {
		for (int i = 0; i < vector.length(); i++) {
			String s = Character.toString(vector.charAt(i));
			if (!s.equals("0") && !s.equals("1")) {
				return false;
			}
		}
		return true;
	}

	// initialize vector if input is valid
	public static String initializeVector(String vector) {
		if (isValid(vector)) {
			return vector;
		}
		return null;
	}
}
