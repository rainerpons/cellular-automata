package com.cellularautomata.generator;

import java.util.Random;

/**
 * The <code>Generator</code> class is a utility class for generating local update rules, initial
 * seeds, and successors of arbitrary vectors.
 * @author Rainer Pons
 */

public class Generator {
	/**
	 * Prevents the instantiation of the <code>Generator</code> class.
	 */
	private Generator() {
		throw new IllegalStateException("Generator is a utility class.");
	}

	/**
	 * Generates a local update rule based on an integer value from 0 to 255 (inclusive).
	 * @param rule local update rule number
	 * @return rule as binary string
	 */
	public static String generateRule(int rule) {
		if (rule > -1 && rule < 256) {
			String binary = Integer.toBinaryString(rule);
			while (binary.length() < 8) {
				binary = "0".concat(binary);
			}
			return binary;
		}
		return null;
	}

	/**
	 * Generates an initial seed where the state of each cell uniformly distributed.
	 * @param size amount of individual cells
	 * @return initial seed as binary string
	 */
	public static Vector generateSeed(int size) {
		String seed = "";
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				double rand = Math.random();
				if (rand < 0.5) {
					seed = seed.concat("1");
				} else {
					seed = seed.concat("0");
				}
			}
		}
		return new Vector(seed);
	}

	/**
	 * Generates an initial seed where exactly one cell has state one and the rest have state zero.
	 * @param size amount of individual cells
	 * @return sparse initial seed as binary string
	 */
	public static Vector generateSparseSeed(int size) {
		String seed = "";
		if (size > 0) {
			int rand = new Random().nextInt(size);
			for (int i = 0; i < rand; i++) {
				seed = seed.concat("0");
			}
			seed = seed.concat("1");
			for (int i = rand + 1; i < size; i++) {
				seed = seed.concat("0");
			}
		}
		return new Vector(seed);
	}

	/**
	 * Generates a seed where each individual cell is different from its neighborhood cells.
	 * @param size amount of individual cells
	 * @return alternating initial seed as binary string
	 */
	public static Vector generateAlternatingSeed(int size) {
		String seed = "";
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (i % 2 == 0) {
					seed = seed.concat("1");
				} else {
					seed = seed.concat("0");
				}
			}
		}
		return new Vector(seed);
	}

	/**
	 * Generates a successive neighborhood vector given a local update rule and a vector.
	 * @param rule local update rule number
	 * @param current vector to determine the successor
	 * @return successive neighborhood vector based on the rule and the seed
	 */
	public static Vector generateSuccessor(int rule, Vector current) {
		String successor = "";
		if (rule > -1 && rule < 256) {
			String temp = "0".concat(current.getState()).concat("0");
			String gen = generateRule(rule);
			for (int i = 0; i < current.getSize(); i++) {
				String sub = temp.substring(i, i + 3);
				switch (sub) {
					case "111":
						successor = successor.concat(Character.toString(gen.charAt(0)));
						break;
					case "110":
						successor = successor.concat(Character.toString(gen.charAt(1)));
						break;
					case "101":
						successor = successor.concat(Character.toString(gen.charAt(2)));
						break;
					case "100":
						successor = successor.concat(Character.toString(gen.charAt(3)));
						break;
					case "011":
						successor = successor.concat(Character.toString(gen.charAt(4)));
						break;
					case "010":
						successor = successor.concat(Character.toString(gen.charAt(5)));
						break;
					case "001":
						successor = successor.concat(Character.toString(gen.charAt(6)));
						break;
					case "000":
						successor = successor.concat(Character.toString(gen.charAt(7)));
						break;
					default:
						throw new IllegalStateException();
				}
			}
		}
		return new Vector(successor);
	}
}