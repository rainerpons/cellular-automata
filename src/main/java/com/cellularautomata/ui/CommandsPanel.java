package com.cellularautomata.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The commands section of the sidebar. Contains the Generate and Save action buttons and exposes
 * listener registration methods so {@link MainFrame} can wire up behavior without knowing about the
 * button internals.
 */
class CommandsPanel extends JPanel {
  private final JButton displayButton;
  private final JButton saveButton;

  CommandsPanel() {
    setLayout(new GridBagLayout());

    // Add section heading.
    final JLabel commandsHeading = UiStyles.createHeading("Commands");
    GridBagConstraints gbcCommandsHeading = new GridBagConstraints();
    gbcCommandsHeading.gridwidth = 2;
    gbcCommandsHeading.anchor = GridBagConstraints.WEST;
    gbcCommandsHeading.insets = new Insets(0, 0, UiStyles.HEADING_BOTTOM_GAP, 0);
    gbcCommandsHeading.gridx = 0;
    gbcCommandsHeading.gridy = 0;
    add(commandsHeading, gbcCommandsHeading);

    // Add command buttons.
    displayButton = new JButton("Generate automaton");
    UiStyles.applyControlHeight(displayButton);
    GridBagConstraints gbcDisplayButton = new GridBagConstraints();
    gbcDisplayButton.anchor = GridBagConstraints.WEST;
    gbcDisplayButton.insets = new Insets(0, 0, 0, UiStyles.BUTTON_GAP_X);
    gbcDisplayButton.gridx = 0;
    gbcDisplayButton.gridy = 1;
    add(displayButton, gbcDisplayButton);

    saveButton = new JButton("Save image");
    UiStyles.applyControlHeight(saveButton);
    saveButton.setEnabled(false);
    GridBagConstraints gbcSaveButton = new GridBagConstraints();
    gbcSaveButton.anchor = GridBagConstraints.EAST;
    gbcSaveButton.weightx = 1.0;
    gbcSaveButton.gridx = 1;
    gbcSaveButton.gridy = 1;
    add(saveButton, gbcSaveButton);
  }

  @Override
  public java.awt.Dimension getMaximumSize() {
    // Prevent BoxLayout from stretching this panel beyond its natural height.
    return new java.awt.Dimension(Integer.MAX_VALUE, getPreferredSize().height);
  }

  void addGenerateListener(ActionListener listener) {
    displayButton.addActionListener(listener);
  }

  void addSaveListener(ActionListener listener) {
    saveButton.addActionListener(listener);
  }

  void setSaveEnabled(boolean enabled) {
    saveButton.setEnabled(enabled);
  }
}
