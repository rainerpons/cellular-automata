package com.github.rpons17;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AutomatonImage extends JPanel {
	// return an image of an automaton given an associative array
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

	// return a resized version of an image with specified size
	public static BufferedImage resizeImage(int width, int height, BufferedImage image) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphic = resizedImage.createGraphics();
		graphic.drawImage(image, 0, 0, width, height, null);
		graphic.dispose();
		return resizedImage;
	}

	// return the name of a file given a rule number and initial seed
	public static String getFileName(int rule, Vector seed) {
		return "rule" + rule + "_seed" + seed.getVector() + ".jpg";
	}
}