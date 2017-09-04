
package automata;

import java.util.Map;

/*
 * Automaton.java
 * The Automaton class uses the Vector and Generator classes in order to store
 * associated value pairs (keys hold generation count and values holds vectors)
 */

public class Automaton {
	// create a map which associates generation with vectors
	private Map<Integer, Vector> map;

	// create an automaton given a rule number and an initial seed
	public Automaton(int rule, Vector seed) {
		map = initializeVectorMap(rule, seed);
	}

	// enter values into the map based on rule number and initial seed
	public static Map<Integer, Vector> initializeVectorMap(int rule, Vector seed) {
		// initializeVectorMap() code
		return null;
	}
}
