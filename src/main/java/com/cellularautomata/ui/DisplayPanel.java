package com.cellularautomata.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
    setLayout(new GridBagLayout());

    // Inner container aligns heading and image to a shared left edge.
    JPanel innerContainer = new JPanel();
    innerContainer.setLayout(new BoxLayout(innerContainer, BoxLayout.Y_AXIS));

    // Add section heading.
    JLabel displayHeading = UiStyles.createHeading("Display");
    displayHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
    displayHeading.setBorder(
        BorderFactory.createEmptyBorder(0, 0, UiStyles.DISPLAY_HEADING_GAP, 0));
    innerContainer.add(displayHeading);

    // Add placeholder label, replaced by the automaton image after generation.
    automatonLabel =
        new JLabel(
            "<html><center>Click the Generate automaton button to begin.<br><br>"
                + "<font color='gray'>Adjust size, rule, or seed type first.</font>"
                + "</center></html>");
    automatonLabel.setHorizontalAlignment(JLabel.CENTER);
    automatonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    innerContainer.add(automatonLabel);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    add(innerContainer, gbc);
  }

  void setAutomatonImage(ImageIcon icon) {
    automatonLabel.setText(null);
    automatonLabel.setIcon(icon);
  }
}
