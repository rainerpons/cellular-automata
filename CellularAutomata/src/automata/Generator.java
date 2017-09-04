
package automata;

/*
 * Generator.java
 * The Generator class provides static methods for generating local update
 * rules, initial seeds, and the successor of an arbitrary vector.
 */

public class Generator {
	// generate a rule based on an integer from 0 to 255
	public static String generateRule(int rule) {
		// generateRule() code
		return null;
	}

	// generate a seed based on the cell state uniform distribution
	public static Vector generateSeed(int size) {
		// generateSeed() code
		return null;
	}

	// generate a seed where one cell is 1 and the rest are 0
	public static Vector generateSparseSeed(int size) {
		// generateSparseSeed() code
		return null;
	}

	// generate a seed where each cell is different from its neighbors
	public static Vector generateAlternatingSeed(int size) {
		// generateAlternatingSeed() code
		return null;
	}

	// generate a seed where some contiguous cells are 1 and the rest are 0
	public static Vector generateBlockSeed(int size) {
		// generateBlockSeed() code
		return null;
	}

	// generate a vector given a rule number and the current vector
	public static Vector generateSuccessor(int rule, Vector current) {
		// generateSuccessor() code
		return null;
	}
}
