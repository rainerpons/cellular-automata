package com.cellularautomata.ui;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The left column of the application window. Displays the section heading and the generated
 * automaton image, or a placeholder prompt before generation.
 */
class DisplayPanel extends JPanel {
  private final JLabel automatonLabel;

  DisplayPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    // Add section heading.
    JLabel displayHeading = UiStyles.createHeading("Display");
    displayHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
    displayHeading.setBorder(
        BorderFactory.createEmptyBorder(0, 0, UiStyles.DISPLAY_HEADING_GAP, 0));
    add(displayHeading);

    // Add placeholder label, replaced by the automaton image after generation.
    automatonLabel =
        new JLabel(
            "<html><center>Click the Generate automaton button to begin.<br><br>"
                + "Select size, rule, and seed type.</center></html>");
    automatonLabel.setHorizontalAlignment(JLabel.CENTER);
    automatonLabel.setVerticalAlignment(JLabel.CENTER);
    automatonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    java.awt.Dimension previewSize = new java.awt.Dimension(400, 400);
    automatonLabel.setPreferredSize(previewSize);
    automatonLabel.setMinimumSize(previewSize);
    automatonLabel.setMaximumSize(previewSize);
    add(automatonLabel);
  }

  void setAutomatonImage(ImageIcon icon) {
    automatonLabel.setText(null);
    automatonLabel.setIcon(icon);
  }
}
