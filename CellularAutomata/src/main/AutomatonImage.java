
package main;

import automata.Automaton;
import automata.Vector;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.JPanel;

/*
 * AutomatonImage.java
 */

@SuppressWarnings("serial") public class AutomatonImage extends JPanel {
	// given a rule and a seed, draw a pixel image of an automaton
	public void paintComponent(int rule, Vector seed) {
		Map<Integer, Vector> map = Automaton.initializeVectorMap(rule, seed);
		BufferedImage image = getImageFromMap(map);
		BufferedImage resizedImage = resizeImage(320, 320, image);
		Graphics2D graphic = resizedImage.createGraphics();
		graphic.drawImage(resizedImage, 0, 0, null);
	}

	// return an image of an automaton given a map
	public static BufferedImage getImageFromMap(Map<Integer, Vector> map) {
		final int size = map.get(new Integer(0)).getSize();
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (Map.Entry<Integer, Vector> entry : map.entrySet()) {
			String pixels = entry.getValue().getVector();
			for (int i = 0; i < pixels.length(); i++) {
				int j = entry.getKey();
				if (Character.toString(pixels.charAt(i)).equals("1")) {
					image.setRGB(i, j, 0); // set to black
				} else {
					image.setRGB(i, j, 16777215); // set to white
				}
			}
		}
		return image;
	}

	// return a resized version of a specified image
	public static BufferedImage resizeImage(int width, int height, BufferedImage image) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphic = resizedImage.createGraphics();
		graphic.drawImage(image, 0, 0, width, height, null);
		graphic.dispose();
		return resizedImage;
	}
}
