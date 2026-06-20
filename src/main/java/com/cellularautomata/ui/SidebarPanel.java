package com.cellularautomata.ui;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

class SidebarPanel extends JPanel {
  SidebarPanel(ParametersPanel parametersPanel, CommandsPanel commandsPanel) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(
        new EmptyBorder(
            UiStyles.SIDEBAR_PADDING_TOP,
            UiStyles.SIDEBAR_PADDING_LEFT,
            UiStyles.SIDEBAR_PADDING_BOTTOM,
            UiStyles.SIDEBAR_PADDING_RIGHT));

    add(parametersPanel);
    add(Box.createVerticalStrut(UiStyles.SECTION_SEPARATOR_GAP));

    JSeparator separator = new JSeparator();
    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    add(separator);

    add(Box.createVerticalStrut(UiStyles.SECTION_SEPARATOR_GAP));
    add(commandsPanel);
    add(Box.createVerticalGlue()); // Push everything up
  }
}
