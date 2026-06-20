package com.cellularautomata.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class SidebarPanel extends JPanel {
  SidebarPanel(ParametersPanel parametersPanel, CommandsPanel commandsPanel) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(20, 10, 20, 20));

    add(parametersPanel);
    add(Box.createVerticalStrut(20)); // Gap between parameters and commands
    add(commandsPanel);
  }
}
