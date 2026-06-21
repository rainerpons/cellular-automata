package com.cellularautomata.ui;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * The right column of the application window. Stacks {@link ParametersPanel} and {@link
 * CommandsPanel} vertically with a separator between them and owns the sidebar padding and
 * alignment contract.
 */
class SidebarPanel extends JPanel {
  SidebarPanel(ParametersPanel parametersPanel, CommandsPanel commandsPanel) {
    // Configure layout and spacing.
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    // Stack panels with a separator between them.
    add(parametersPanel);
    add(Box.createRigidArea(new Dimension(0, UiStyles.SECTION_SEPARATOR_GAP / 2)));

    JSeparator separator = new JSeparator();
    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    add(separator);

    add(Box.createRigidArea(new Dimension(0, UiStyles.SECTION_SEPARATOR_GAP / 2)));
    add(commandsPanel);
    // Consume remaining sidebar height so sidebar sections stay grouped at the top
    add(Box.createVerticalGlue());
  }
}
