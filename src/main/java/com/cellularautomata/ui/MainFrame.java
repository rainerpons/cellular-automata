package com.cellularautomata.ui;

import com.cellularautomata.engine.AutomataEngine;
import com.cellularautomata.engine.AutomataResult;
import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.Vector;
import com.cellularautomata.image.AutomatonImage;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

/**
 * The <code>MainFrame</code> class owns Swing UI setup and event wiring, while delegating
 * generation and image work.
 */
public class MainFrame {
  private BufferedImage resizedAutomatonImage;
  private JFrame caFrame;
  private Vector seed;
  private int rule;

  private final DisplayPanel displayPanel;
  private final ParametersPanel parametersPanel;
  private final CommandsPanel commandsPanel;
  private final SidebarPanel sidebarPanel;

  /** Initializes every component of the GUI. */
  public MainFrame() {
    try {
      FlatDarkLaf.setup();
    } catch (Exception e) {
      System.err.println("Could not set FlatDarkLaf look and feel.");
    }

    caFrame = new JFrame();
    caFrame.setResizable(false);
    caFrame.setTitle("Cellular Automata");
    caFrame.setBounds(100, 100, 800, 500);
    caFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {450, 0, 0};
    gridBagLayout.rowHeights = new int[] {0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {1.0, Double.MIN_VALUE};
    caFrame.getContentPane().setLayout(gridBagLayout);

    displayPanel = new DisplayPanel();
    GridBagConstraints gbcDisplayPanel = new GridBagConstraints();
    gbcDisplayPanel.insets = new Insets(20, 20, 20, 10);
    gbcDisplayPanel.fill = GridBagConstraints.BOTH;
    gbcDisplayPanel.gridx = 0;
    gbcDisplayPanel.gridy = 0;
    caFrame.getContentPane().add(displayPanel, gbcDisplayPanel);

    parametersPanel = new ParametersPanel();
    commandsPanel = new CommandsPanel();
    sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);

    GridBagConstraints gbcSidebarPanel = new GridBagConstraints();
    gbcSidebarPanel.fill = GridBagConstraints.BOTH;
    gbcSidebarPanel.gridx = 1;
    gbcSidebarPanel.gridy = 0;
    caFrame.getContentPane().add(sidebarPanel, gbcSidebarPanel);

    setupActionListeners();
  }

  private void setupActionListeners() {
    commandsPanel.addGenerateListener(
        e -> {
          String ruleText = parametersPanel.getRuleText();
          OptionalInt parsedRule = RuleValidator.parseRule(ruleText);
          if (parsedRule.isPresent()) {
            rule = parsedRule.getAsInt();
            int size = parametersPanel.getSizeValue();
            String seedType = parametersPanel.getSeedType();

            AutomataResult result = AutomataEngine.generate(rule, size, seedType);
            seed = result.getOriginalSeed(); // retain seed for filename creation during save
            BufferedImage automatonImage = AutomatonImage.getImageFromMap(result.getAutomatonMap());
            // Empirically scaled from 280x280 to 400x400 for an 800x500 window
            resizedAutomatonImage = AutomatonImage.resizeImage(400, 400, automatonImage);
            displayPanel.setAutomatonImage(new ImageIcon(resizedAutomatonImage));
            commandsPanel.setSaveEnabled(true);
          } else {
            JOptionPane.showMessageDialog(
                caFrame,
                "Rule must be a whole number between 0 and 255.",
                "Rule Number Error",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    commandsPanel.addSaveListener(
        e -> {
          JFileChooser fileChooser =
              new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
          String child = AutomatonImage.getFileName(rule, seed);
          fileChooser.setSelectedFile(new File(child));
          int returnValue = fileChooser.showSaveDialog(null);
          if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
              AutomatonImage.saveImage(resizedAutomatonImage, file);
            } catch (IOException ie) {
              ie.printStackTrace();
            }
          }
        });
  }

  /**
   * Shows or hides this MainFrame depending on the value of parameter b.
   *
   * @param b if true, makes the MainFrame visible, otherwise hides the MainFrame.
   */
  public void setVisible(boolean b) {
    caFrame.setVisible(b);
  }
}
