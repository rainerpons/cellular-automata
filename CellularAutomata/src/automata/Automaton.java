
package automata;

import java.util.HashMap;
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
		Map<Integer, Vector> map = new HashMap<Integer, Vector>();
		Vector successor = Generator.generateSuccessor(rule, seed);
		int generation = 0;

		map.put(generation, seed);
		while (generation < seed.getSize()) {
			generation++;
			successor = Generator.generateSuccessor(rule, successor);
			map.put(generation, successor);
		}
		return map;
	}
}
