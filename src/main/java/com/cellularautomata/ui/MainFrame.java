package com.cellularautomata.ui;

import com.cellularautomata.engine.AutomataEngine;
import com.cellularautomata.engine.AutomataResult;
import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.Vector;
import com.cellularautomata.image.AutomatonImage;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
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
    UiFonts.registerBundledFonts();

    try {
      FlatDarkLaf.setup();
    } catch (Exception e) {
      System.err.println("Could not set FlatDarkLaf look and feel.");
    }

    applyGlobalFont();

    caFrame = new JFrame();
    caFrame.setResizable(false);
    caFrame.setTitle("Cellular Automata");
    caFrame.setBounds(100, 100, 950, 500);
    caFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    javax.swing.JPanel contentPane = new javax.swing.JPanel();
    contentPane.setLayout(new javax.swing.BoxLayout(contentPane, javax.swing.BoxLayout.X_AXIS));
    contentPane.setBorder(
        javax.swing.BorderFactory.createEmptyBorder(
            UiStyles.APP_SPACING,
            UiStyles.APP_SPACING,
            UiStyles.APP_SPACING,
            UiStyles.APP_SPACING));
    caFrame.setContentPane(contentPane);

    displayPanel = new DisplayPanel();
    displayPanel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);

    parametersPanel = new ParametersPanel();
    commandsPanel = new CommandsPanel();
    sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
    sidebarPanel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);

    contentPane.add(displayPanel);
    contentPane.add(
        javax.swing.Box.createRigidArea(new java.awt.Dimension(UiStyles.APP_SPACING, 0)));
    contentPane.add(sidebarPanel);

    setupActionListeners();
  }

  private static void applyGlobalFont() {
    Font plexSans = new Font(UiFonts.IBM_PLEX_SANS, Font.PLAIN, 13);
    // Only apply if the font was successfully registered; fall back to FlatLaf default otherwise.
    if (!plexSans.getFamily().equals(UiFonts.IBM_PLEX_SANS)) {
      return;
    }
    String[] uiKeys = {
      "Button.font", "Label.font", "ComboBox.font", "TextField.font",
      "Slider.font", "Panel.font", "OptionPane.font", "OptionPane.buttonFont",
      "OptionPane.messageFont", "TitledBorder.font"
    };
    for (String key : uiKeys) {
      UIManager.put(key, plexSans);
    }
  }

  private void setupActionListeners() {
    commandsPanel.addGenerateListener(e -> generateAutomaton());
    commandsPanel.addSaveListener(e -> saveAutomatonImage());
  }

  private void generateAutomaton() {
    OptionalInt parsedRule = RuleValidator.parseRule(parametersPanel.getRuleText());
    if (!parsedRule.isPresent()) {
      showRuleError();
      return;
    }

    rule = parsedRule.getAsInt();
    AutomataResult result =
        AutomataEngine.generate(
            rule, parametersPanel.getSizeValue(), parametersPanel.getSeedType());
    seed = result.getOriginalSeed();

    BufferedImage automatonImage = AutomatonImage.getImageFromMap(result.getAutomatonMap());
    // Empirically scaled from 280x280 to 400x400 for an 800x500 window
    resizedAutomatonImage = AutomatonImage.resizeImage(400, 400, automatonImage);

    displayPanel.setAutomatonImage(new ImageIcon(resizedAutomatonImage));
    commandsPanel.setSaveEnabled(true);
  }

  private void saveAutomatonImage() {
    JFileChooser fileChooser =
        new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    fileChooser.setSelectedFile(new File(AutomatonImage.getFileName(rule, seed)));
    int returnValue = fileChooser.showSaveDialog(null);
    if (returnValue != JFileChooser.APPROVE_OPTION) {
      return;
    }

    try {
      AutomatonImage.saveImage(resizedAutomatonImage, fileChooser.getSelectedFile());
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  private void showRuleError() {
    JOptionPane.showMessageDialog(
        caFrame,
        "Rule must be a whole number between 0 and 255.",
        "Rule Number Error",
        JOptionPane.ERROR_MESSAGE);
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
