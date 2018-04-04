package com.cellularautomata;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
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
import javax.swing.filechooser.FileSystemView;
import com.cellularautomata.automaton.Automaton;
import com.cellularautomata.automaton.AutomatonImage;
import com.cellularautomata.generator.Generator;
import com.cellularautomata.generator.Vector;

/**
 * The <code>Main</code> class is responsible for the logic and the display of the GUI.
 * @author Rainer Pons
 */

public class Main {
	private BufferedImage resizedAutomatonImage;
	private JFrame caFrame;
	private Vector seed;
	private int rule;

	/**
	 * Launches the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initializes every component of the GUI.
	 */
	private void initialize() {
		// creates the cellular automata frame
		caFrame = new JFrame();
		caFrame.setResizable(false);
		caFrame.setTitle("Cellular Automata");
		caFrame.setBounds(100, 100, 640, 360);
		caFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {358, 0, 0};
		gridBagLayout.rowHeights = new int[] {229, 0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		caFrame.getContentPane().setLayout(gridBagLayout);

		// creates the display panel
		JPanel displayPanel = new JPanel();
		displayPanel.setBorder(BorderFactory.createTitledBorder("Display"));
		displayPanel.setToolTipText("");
		GridBagConstraints gbcDisplayPanel = new GridBagConstraints();
		gbcDisplayPanel.gridheight = 2;
		gbcDisplayPanel.insets = new Insets(10, 10, 10, 5);
		gbcDisplayPanel.fill = GridBagConstraints.BOTH;
		gbcDisplayPanel.gridx = 0;
		gbcDisplayPanel.gridy = 0;
		caFrame.getContentPane().add(displayPanel, gbcDisplayPanel);

		// creates the automaton label in display panel
		final JLabel automatonLabel = new JLabel();
		displayPanel.add(automatonLabel);

		// creates the parameters panel
		JPanel parametersPanel = new JPanel();
		parametersPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		GridBagConstraints gbcParametersPanel = new GridBagConstraints();
		gbcParametersPanel.insets = new Insets(10, 5, 5, 10);
		gbcParametersPanel.fill = GridBagConstraints.BOTH;
		gbcParametersPanel.gridx = 1;
		gbcParametersPanel.gridy = 0;
		caFrame.getContentPane().add(parametersPanel, gbcParametersPanel);
		GridBagLayout gblParametersPanel = new GridBagLayout();
		gblParametersPanel.columnWidths = new int[] {103, 0, 0};
		gblParametersPanel.rowHeights = new int[] {31, 45, 48, 50, 0, 0};
		gblParametersPanel.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
		gblParametersPanel.rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		parametersPanel.setLayout(gblParametersPanel);

		// creates the size label in parameters panel
		JLabel sizeLabel = new JLabel("Size");
		GridBagConstraints gbcSizeLabel = new GridBagConstraints();
		gbcSizeLabel.gridwidth = 2;
		gbcSizeLabel.insets = new Insets(0, 0, 5, 0);
		gbcSizeLabel.gridx = 0;
		gbcSizeLabel.gridy = 0;
		parametersPanel.add(sizeLabel, gbcSizeLabel);

		// creates the current size label in parameters panel
		JLabel currentSizeLabel = new JLabel("Current size");
		GridBagConstraints gbcCurrentSizeLabel = new GridBagConstraints();
		gbcCurrentSizeLabel.insets = new Insets(0, 0, 5, 5);
		gbcCurrentSizeLabel.gridx = 0;
		gbcCurrentSizeLabel.gridy = 2;
		parametersPanel.add(currentSizeLabel, gbcCurrentSizeLabel);

		// creates the current size text field in parameters panel
		final JTextField currentSizeTextField = new JTextField();
		currentSizeTextField.setText(Integer.toString(8));
		currentSizeTextField.setEditable(false);
		GridBagConstraints gbcCurrentSizeTextField = new GridBagConstraints();
		gbcCurrentSizeTextField.insets = new Insets(0, 0, 5, 0);
		gbcCurrentSizeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcCurrentSizeTextField.gridx = 1;
		gbcCurrentSizeTextField.gridy = 2;
		parametersPanel.add(currentSizeTextField, gbcCurrentSizeTextField);

		// creates the size slider in parameters panel
		final JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 4, 64, 8);
		sizeSlider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				currentSizeTextField.setText(Integer.toString(sizeSlider.getValue()));
			}
		});
		GridBagConstraints gbcSizeSlider = new GridBagConstraints();
		gbcSizeSlider.fill = GridBagConstraints.HORIZONTAL;
		gbcSizeSlider.gridwidth = 2;
		gbcSizeSlider.insets = new Insets(0, 0, 5, 0);
		gbcSizeSlider.gridx = 0;
		gbcSizeSlider.gridy = 1;
		parametersPanel.add(sizeSlider, gbcSizeSlider);
		sizeSlider.setMinorTickSpacing(1);
		sizeSlider.setMajorTickSpacing(5);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setSnapToTicks(true);

		// creates the rule label in parameters panel
		JLabel ruleLabel = new JLabel("Rule number");
		GridBagConstraints gbcRuleLabel = new GridBagConstraints();
		gbcRuleLabel.insets = new Insets(0, 0, 5, 5);
		gbcRuleLabel.gridx = 0;
		gbcRuleLabel.gridy = 3;
		parametersPanel.add(ruleLabel, gbcRuleLabel);

		// creates the rule text area in parameters panel
		final JTextField ruleTextArea = new JTextField();
		ruleTextArea.setText("30");
		GridBagConstraints gbcRuleTextArea = new GridBagConstraints();
		gbcRuleTextArea.insets = new Insets(0, 0, 5, 0);
		gbcRuleTextArea.fill = GridBagConstraints.HORIZONTAL;
		gbcRuleTextArea.gridx = 1;
		gbcRuleTextArea.gridy = 3;
		parametersPanel.add(ruleTextArea, gbcRuleTextArea);

		// creates the seed label in parameters panel
		JLabel seedLabel = new JLabel("Seed type");
		GridBagConstraints gbcSeedLabel = new GridBagConstraints();
		gbcSeedLabel.insets = new Insets(0, 0, 0, 5);
		gbcSeedLabel.gridx = 0;
		gbcSeedLabel.gridy = 4;
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
		JPanel commandsPanel = new JPanel();
		commandsPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
		GridBagConstraints gbcCommandsPanel = new GridBagConstraints();
		gbcCommandsPanel.insets = new Insets(5, 5, 10, 10);
		gbcCommandsPanel.fill = GridBagConstraints.BOTH;
		gbcCommandsPanel.gridx = 1;
		gbcCommandsPanel.gridy = 1;
		caFrame.getContentPane().add(commandsPanel, gbcCommandsPanel);
		GridBagLayout gblCommandsPanel = new GridBagLayout();
		gblCommandsPanel.columnWidths = new int[] {0, 0, 0};
		gblCommandsPanel.rowHeights = new int[] {0, 0};
		gblCommandsPanel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		gblCommandsPanel.rowWeights = new double[] {1.0, Double.MIN_VALUE};
		commandsPanel.setLayout(gblCommandsPanel);

		// creates the display button in commands panel
		JButton displayButton = new JButton("Display");
		displayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rule = Integer.parseInt(ruleTextArea.getText());
				if (rule > -1 && rule < 256) {
					int size = sizeSlider.getValue();
					String seedType = (String)seedComboBox.getSelectedItem();
					seed = null;
					if (seedType.equalsIgnoreCase("uniform")) {
						seed = Generator.generateSeed(size);
					} else if (seedType.equalsIgnoreCase("sparse")) {
						seed = Generator.generateSparseSeed(size);
					} else if (seedType.equalsIgnoreCase("alternating")) {
						seed = Generator.generateAlternatingSeed(size);
					}
					Map<Integer, Vector> automatonMap = Automaton.initializeVectorMap(rule, seed);
					BufferedImage automatonImage = AutomatonImage.getImageFromMap(automatonMap);
					resizedAutomatonImage = AutomatonImage.resizeImage(280, 280, automatonImage);
					automatonLabel.setIcon(new ImageIcon(resizedAutomatonImage));
				} else {
					JOptionPane.showMessageDialog(caFrame,
							"Rule number should be an integer \nbetween 0 and 255 (inclusive).",
							"Rule Number Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbcDisplayButton = new GridBagConstraints();
		gbcDisplayButton.insets = new Insets(0, 0, 0, 5);
		gbcDisplayButton.gridx = 0;
		gbcDisplayButton.gridy = 0;
		commandsPanel.add(displayButton, gbcDisplayButton);

		// creates the save button in commands panel
		JButton saveButton = new JButton("Save");
		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fileChooser =
						new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				String child = AutomatonImage.getFileName(rule, seed);
				fileChooser.setSelectedFile(new File(child));
				int returnValue = fileChooser.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					String parent = fileChooser.getCurrentDirectory().getAbsolutePath();
					try {
						File file = new File(parent + "/" + child);
						if (!file.exists()) {
							file.createNewFile();
						}
						ImageIO.write(resizedAutomatonImage, "JPG", file);
					} catch (IOException ie) {
						ie.printStackTrace();
					}
				}
			}
		});
		GridBagConstraints gbcSaveButton = new GridBagConstraints();
		gbcSaveButton.gridx = 1;
		gbcSaveButton.gridy = 0;
		commandsPanel.add(saveButton, gbcSaveButton);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.caFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}