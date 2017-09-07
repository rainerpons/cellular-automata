
package main;

import automata.Automaton;
import automata.Vector;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.JComponent;

/*
 * AutomatonImage.java
 */

@SuppressWarnings("serial") public class AutomatonImage extends JComponent {
	// given a rule and a seed, draw a pixel image of an automaton
	public void paintComponent(int rule, Vector seed, Graphics g) {
		Map<Integer, Vector> map = Automaton.initializeVectorMap(rule, seed);
		BufferedImage image = getImageFromMap(map);
		g.drawImage(image, 0, 0, null);
	}

	// return an image of an automaton given a map
	public static BufferedImage getImageFromMap(Map<Integer, Vector> map) {
		final int size = 320;
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (Map.Entry<Integer, Vector> entry : map.entrySet()) {
			String pixels = entry.getValue().getVector();
			for (int i = 0; i < pixels.length(); i++) {
				String p = Character.toString(pixels.charAt(i));
				for (int j = 0; j < size; j++) {
					if (p.equals("1")) {
						image.setRGB(i, j % size, 0); // set to black
					} else {
						image.setRGB(i, j % size, 16777215); // set to white
					}
				}
			}
		}
		return image;
	}
}
