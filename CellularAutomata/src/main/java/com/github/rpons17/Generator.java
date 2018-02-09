package com.github.rpons17;

/*
 * The Generator class provides static methods for generating local update
 * rules, initial seeds, and the successor of an arbitrary vector.
 */

public class Generator {
	// generate a rule based on an integer from 0 to 255
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

	// generate a seed based on the cell state uniform distribution
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

	// generate a seed where one cell is 1 and the rest are 0
	public static Vector generateSparseSeed(int size) {
		// generateSparseSeed() code
		String seed = "";
		if (size > 0) {
			int rand = (int)(Math.random() * size);
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

	// generate a seed where each cell is different from its neighbors
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

	// generate a vector given a rule number and the current vector
	public static Vector generateSuccessor(int rule, Vector current) {
		String successor = "";
		if (rule > -1 && rule < 256) {
			String temp = "0".concat(current.getVector()).concat("0");
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
				}
			}
		}
		return new Vector(successor);
	}
}