package com.cellularautomata.automaton;

import java.util.HashMap;
import java.util.Map;
import com.cellularautomata.generator.Generator;
import com.cellularautomata.generator.Vector;

/**
 * The <code>Automaton</code> class uses the <code>Vector</code> and <code>Generator</code> classes
 * in order to store associated value pairs. The keys hold generation count and values hold vectors.
 * @author Rainer Pons
 */

public class Automaton {
	/**
	 * Creates a map which associates the generation count of a vector with the vector itself.
	 */
	private Map<Integer, Vector> map;

	/**
	 * Creates an automaton given a local update rule number and an initial seed.
	 * @param rule local update rule number
	 * @param seed initial neighborhood vector
	 */
	public Automaton(int rule, Vector seed) {
		map = initializeVectorMap(rule, seed);
	}

	/**
	 * Enters values and their successors into the map.
	 * @param rule local update rule number
	 * @param seed initial neighborhood vector
	 * @return a map with associated value pairs of generation counts and neighborhood vectors
	 */
	public static Map<Integer, Vector> initializeVectorMap(int rule, Vector seed) {
		if (rule > -1 && rule < 256 && seed != null) {
			Map<Integer, Vector> map = new HashMap<>();
			Vector successor = Generator.generateSuccessor(rule, seed);
			int generation = 0;

			map.put(generation, seed);
			while (generation < seed.getSize() - 1) {
				generation++;
				map.put(generation, successor);
				successor = Generator.generateSuccessor(rule, successor);
			}
			return map;
		}
		return null;
	}

	/**
	 * Outputs the generation count of neighborhood vectors and the vectors themselves as text.
	 */
	public void displayVectorMap() {
		for (Map.Entry<Integer, Vector> entry : map.entrySet()) {
			System.out.println("Generation " + entry.getKey() + ": " + entry.getValue().getState());
		}
	}
}