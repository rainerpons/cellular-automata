package com.cellularautomata.ui;

import com.cellularautomata.engine.AutomataEngine;
import com.cellularautomata.engine.AutomataResult;
import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.Vector;
import com.cellularautomata.image.AutomatonImage;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

  /** Initializes every component of the GUI. */
  public MainFrame() {
    try {
      FlatDarkLaf.setup();
    } catch (Exception e) {
      System.err.println("Could not set FlatDarkLaf look and feel.");
    }

    // creates the cellular automata frame
    caFrame = new JFrame();
    caFrame.setResizable(false);
    caFrame.setTitle("Cellular Automata");
    caFrame.setBounds(100, 100, 800, 500);
    caFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {450, 0, 0};
    gridBagLayout.rowHeights = new int[] {229, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
    caFrame.getContentPane().setLayout(gridBagLayout);

    // creates the display panel
    final JPanel displayPanel = new JPanel();
    displayPanel.setLayout(new BorderLayout());
    displayPanel.setToolTipText("");
    GridBagConstraints gbcDisplayPanel = new GridBagConstraints();
    gbcDisplayPanel.gridheight = 2;
    gbcDisplayPanel.insets = new Insets(20, 20, 20, 10);
    gbcDisplayPanel.fill = GridBagConstraints.BOTH;
    gbcDisplayPanel.gridx = 0;
    gbcDisplayPanel.gridy = 0;
    caFrame.getContentPane().add(displayPanel, gbcDisplayPanel);

    JLabel displayHeading = new JLabel("Display");
    displayHeading.putClientProperty("FlatLaf.styleClass", "h3");
    displayHeading.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    displayPanel.add(displayHeading, BorderLayout.NORTH);

    // creates the automaton label in display panel
    final JLabel automatonLabel =
        new JLabel(
            "<html><center>Click Generate Automaton to begin.<br><br>"
                + "<font color='gray'>Adjust size, rule, or seed type first.</font>"
                + "</center></html>");
    automatonLabel.setHorizontalAlignment(JLabel.CENTER);
    displayPanel.add(automatonLabel, BorderLayout.CENTER);

    // creates the parameters panel
    final JPanel parametersPanel = new JPanel();
    GridBagConstraints gbcParametersPanel = new GridBagConstraints();
    gbcParametersPanel.insets = new Insets(20, 10, 5, 20);
    gbcParametersPanel.fill = GridBagConstraints.BOTH;
    gbcParametersPanel.gridx = 1;
    gbcParametersPanel.gridy = 0;
    caFrame.getContentPane().add(parametersPanel, gbcParametersPanel);
    GridBagLayout gblParametersPanel = new GridBagLayout();
    gblParametersPanel.columnWidths = new int[] {120, 0, 0};
    gblParametersPanel.rowHeights = new int[] {30, 40, 50, 50, 50, 0};
    gblParametersPanel.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
    gblParametersPanel.rowWeights = new double[] {0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
    parametersPanel.setLayout(gblParametersPanel);

    JLabel parametersHeading = new JLabel("Parameters");
    parametersHeading.putClientProperty("FlatLaf.styleClass", "h3");
    GridBagConstraints gbcParametersHeading = new GridBagConstraints();
    gbcParametersHeading.gridwidth = 2;
    gbcParametersHeading.anchor = GridBagConstraints.WEST;
    gbcParametersHeading.insets = new Insets(0, 0, 15, 0);
    gbcParametersHeading.gridx = 0;
    gbcParametersHeading.gridy = 0;
    parametersPanel.add(parametersHeading, gbcParametersHeading);

    // creates the size label in parameters panel
    final JLabel sizeLabel = new JLabel("Size (8)");
    GridBagConstraints gbcSizeLabel = new GridBagConstraints();
    gbcSizeLabel.gridwidth = 2;
    gbcSizeLabel.anchor = GridBagConstraints.WEST;
    gbcSizeLabel.insets = new Insets(0, 0, 5, 0);
    gbcSizeLabel.gridx = 0;
    gbcSizeLabel.gridy = 1;
    parametersPanel.add(sizeLabel, gbcSizeLabel);

    // creates the size slider in parameters panel
    final JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 4, 64, 8);
    sizeSlider.addChangeListener(
        new ChangeListener() {
          @Override
          public void stateChanged(ChangeEvent e) {
            sizeLabel.setText("Size (" + sizeSlider.getValue() + ")");
          }
        });
    GridBagConstraints gbcSizeSlider = new GridBagConstraints();
    gbcSizeSlider.fill = GridBagConstraints.HORIZONTAL;
    gbcSizeSlider.gridwidth = 2;
    gbcSizeSlider.insets = new Insets(0, 0, 15, 0);
    gbcSizeSlider.gridx = 0;
    gbcSizeSlider.gridy = 2;
    parametersPanel.add(sizeSlider, gbcSizeSlider);

    // creates the rule label in parameters panel
    GridBagConstraints gbcRuleLabel = new GridBagConstraints();
    gbcRuleLabel.anchor = GridBagConstraints.WEST;
    gbcRuleLabel.insets = new Insets(0, 0, 15, 10);
    gbcRuleLabel.gridx = 0;
    gbcRuleLabel.gridy = 3;
    JLabel ruleLabel = new JLabel("Rule number");
    parametersPanel.add(ruleLabel, gbcRuleLabel);

    // creates the rule text area in parameters panel
    final JTextField ruleTextArea = new JTextField();
    ruleTextArea.setText("30");
    GridBagConstraints gbcRuleTextArea = new GridBagConstraints();
    gbcRuleTextArea.insets = new Insets(0, 0, 15, 0);
    gbcRuleTextArea.fill = GridBagConstraints.HORIZONTAL;
    gbcRuleTextArea.gridx = 1;
    gbcRuleTextArea.gridy = 3;
    parametersPanel.add(ruleTextArea, gbcRuleTextArea);

    // creates the seed label in parameters panel
    GridBagConstraints gbcSeedLabel = new GridBagConstraints();
    gbcSeedLabel.anchor = GridBagConstraints.WEST;
    gbcSeedLabel.insets = new Insets(0, 0, 0, 10);
    gbcSeedLabel.gridx = 0;
    gbcSeedLabel.gridy = 4;
    JLabel seedLabel = new JLabel("Seed type");
    parametersPanel.add(seedLabel, gbcSeedLabel);

    // creates the seed combo box in parameters panel
    String[] seedTypes = {"Uniform", "Sparse", "Alternating"};
    final JComboBox<String> seedComboBox = new JComboBox<>(seedTypes);
    GridBagConstraints gbcSeedComboBox = new GridBagConstraints();
    gbcSeedComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbcSeedComboBox.gridx = 1;
    gbcSeedComboBox.gridy = 4;
    parametersPanel.add(seedComboBox, gbcSeedComboBox);

    // creates the commands panel
    final JPanel commandsPanel = new JPanel();
    GridBagConstraints gbcCommandsPanel = new GridBagConstraints();
    gbcCommandsPanel.insets = new Insets(5, 10, 20, 20);
    gbcCommandsPanel.fill = GridBagConstraints.BOTH;
    gbcCommandsPanel.gridx = 1;
    gbcCommandsPanel.gridy = 1;
    caFrame.getContentPane().add(commandsPanel, gbcCommandsPanel);
    GridBagLayout gblCommandsPanel = new GridBagLayout();
    gblCommandsPanel.columnWidths = new int[] {0, 0, 0};
    gblCommandsPanel.rowHeights = new int[] {30, 0, 0};
    gblCommandsPanel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
    gblCommandsPanel.rowWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
    commandsPanel.setLayout(gblCommandsPanel);

    JLabel commandsHeading = new JLabel("Commands");
    commandsHeading.putClientProperty("FlatLaf.styleClass", "h3");
    GridBagConstraints gbcCommandsHeading = new GridBagConstraints();
    gbcCommandsHeading.gridwidth = 2;
    gbcCommandsHeading.anchor = GridBagConstraints.WEST;
    gbcCommandsHeading.insets = new Insets(0, 0, 10, 0);
    gbcCommandsHeading.gridx = 0;
    gbcCommandsHeading.gridy = 0;
    commandsPanel.add(commandsHeading, gbcCommandsHeading);

    // creates the save button in commands panel
    final JButton saveButton = new JButton("Save Image");
    saveButton.setEnabled(false);

    // creates the display button in commands panel
    JButton displayButton = new JButton("Generate Automaton");
    displayButton.addActionListener(
        e -> {
          String ruleText = ruleTextArea.getText();
          OptionalInt parsedRule = RuleValidator.parseRule(ruleText);
          if (parsedRule.isPresent()) {
            rule = parsedRule.getAsInt();
            int size = sizeSlider.getValue();
            String seedType = (String) seedComboBox.getSelectedItem();

            AutomataResult result = AutomataEngine.generate(rule, size, seedType);
            seed = result.getOriginalSeed(); // retain seed for filename creation during save
            BufferedImage automatonImage = AutomatonImage.getImageFromMap(result.getAutomatonMap());
            // Empirically scaled from 280x280 to 400x400 for an 800x500 window
            resizedAutomatonImage = AutomatonImage.resizeImage(400, 400, automatonImage);
            automatonLabel.setText(null);
            automatonLabel.setIcon(new ImageIcon(resizedAutomatonImage));
            saveButton.setEnabled(true);
          } else {
            JOptionPane.showMessageDialog(
                caFrame,
                "Rule must be a whole number between 0 and 255.",
                "Rule Number Error",
                JOptionPane.ERROR_MESSAGE);
          }
        });
    GridBagConstraints gbcDisplayButton = new GridBagConstraints();
    gbcDisplayButton.insets = new Insets(0, 0, 0, 5);
    gbcDisplayButton.gridx = 0;
    gbcDisplayButton.gridy = 1;
    commandsPanel.add(displayButton, gbcDisplayButton);

    saveButton.addActionListener(
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
    GridBagConstraints gbcSaveButton = new GridBagConstraints();
    gbcSaveButton.gridx = 1;
    gbcSaveButton.gridy = 1;
    commandsPanel.add(saveButton, gbcSaveButton);
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
