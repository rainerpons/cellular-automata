package com.cellularautomata.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

class DisplayPanel extends JPanel {
  private final JLabel automatonLabel;

  DisplayPanel() {
    setLayout(new GridBagLayout());

    JPanel innerContainer = new JPanel();
    innerContainer.setLayout(new BoxLayout(innerContainer, BoxLayout.Y_AXIS));

    JLabel displayHeading = createHeading("Display");
    displayHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
    displayHeading.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    innerContainer.add(displayHeading);

    automatonLabel =
        new JLabel(
            "<html><center>Click Generate automaton to begin.<br><br>"
                + "<font color='gray'>Adjust size, rule, or seed type first.</font>"
                + "</center></html>");
    automatonLabel.setHorizontalAlignment(JLabel.CENTER);
    automatonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    innerContainer.add(automatonLabel);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    add(innerContainer, gbc);
  }

  void setAutomatonImage(ImageIcon icon) {
    automatonLabel.setText(null);
    automatonLabel.setIcon(icon);
  }

  private JLabel createHeading(String text) {
    JLabel heading = new JLabel(text);
    heading.putClientProperty("FlatLaf.styleClass", "h3");
    return heading;
  }
}
