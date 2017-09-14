
package main;

import automata.Automaton;
import automata.Generator;
import automata.Vector;
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

/*
 * MainApp.java 
 */

public class MainApp {
	private BufferedImage resizedAutomatonImage;
	private JFrame CAFrame;
	private Vector seed;
	private int rule;

	// launch the application
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.CAFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// create the application
	public MainApp() {
		initialize();
	}

	// initialize the contents of the frame
	private void initialize() {
		// initialize the cellular automata frame
		CAFrame = new JFrame();
		CAFrame.setResizable(false);
		CAFrame.setTitle("Cellular Automata");
		CAFrame.setBounds(100, 100, 640, 360);
		CAFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {358, 0, 0};
		gridBagLayout.rowHeights = new int[] {229, 0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		CAFrame.getContentPane().setLayout(gridBagLayout);

		// initialize the display panel
		JPanel displayPanel = new JPanel();
		displayPanel.setBorder(BorderFactory.createTitledBorder("Display"));
		displayPanel.setToolTipText("");
		GridBagConstraints gbc_displayPanel = new GridBagConstraints();
		gbc_displayPanel.gridheight = 2;
		gbc_displayPanel.insets = new Insets(10, 10, 10, 5);
		gbc_displayPanel.fill = GridBagConstraints.BOTH;
		gbc_displayPanel.gridx = 0;
		gbc_displayPanel.gridy = 0;
		CAFrame.getContentPane().add(displayPanel, gbc_displayPanel);

		// initialize the automaton label in display panel
		JLabel automatonLabel = new JLabel();
		displayPanel.add(automatonLabel);

		// initialize the parameters panel
		JPanel parametersPanel = new JPanel();
		parametersPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		GridBagConstraints gbc_parametersPanel = new GridBagConstraints();
		gbc_parametersPanel.insets = new Insets(10, 5, 5, 10);
		gbc_parametersPanel.fill = GridBagConstraints.BOTH;
		gbc_parametersPanel.gridx = 1;
		gbc_parametersPanel.gridy = 0;
		CAFrame.getContentPane().add(parametersPanel, gbc_parametersPanel);
		GridBagLayout gbl_parametersPanel = new GridBagLayout();
		gbl_parametersPanel.columnWidths = new int[] {103, 0, 0};
		gbl_parametersPanel.rowHeights = new int[] {31, 45, 48, 50, 0, 0};
		gbl_parametersPanel.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
		gbl_parametersPanel.rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		parametersPanel.setLayout(gbl_parametersPanel);

		// initialize the size label in parameters panel
		JLabel sizeLabel = new JLabel("Size");
		GridBagConstraints gbc_sizeLabel = new GridBagConstraints();
		gbc_sizeLabel.gridwidth = 2;
		gbc_sizeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_sizeLabel.gridx = 0;
		gbc_sizeLabel.gridy = 0;
		parametersPanel.add(sizeLabel, gbc_sizeLabel);

		// initialize the current size label in parameters panel
		JLabel currentSizeLabel = new JLabel("Current size");
		GridBagConstraints gbc_currentSizeLabel = new GridBagConstraints();
		gbc_currentSizeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_currentSizeLabel.gridx = 0;
		gbc_currentSizeLabel.gridy = 2;
		parametersPanel.add(currentSizeLabel, gbc_currentSizeLabel);

		// initialize the current size text field in parameters panel
		JTextField currentSizeTextField = new JTextField();
		currentSizeTextField.setText(Integer.toString(8));
		currentSizeTextField.setEditable(false);
		GridBagConstraints gbc_currentSizeTextField = new GridBagConstraints();
		gbc_currentSizeTextField.insets = new Insets(0, 0, 5, 0);
		gbc_currentSizeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_currentSizeTextField.gridx = 1;
		gbc_currentSizeTextField.gridy = 2;
		parametersPanel.add(currentSizeTextField, gbc_currentSizeTextField);

		// initialize the size slider in parameters panel
		JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 4, 64, 8);
		sizeSlider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseDragged(MouseEvent e) {
				currentSizeTextField.setText(Integer.toString(sizeSlider.getValue()));
			}
		});
		GridBagConstraints gbc_sizeSlider = new GridBagConstraints();
		gbc_sizeSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeSlider.gridwidth = 2;
		gbc_sizeSlider.insets = new Insets(0, 0, 5, 0);
		gbc_sizeSlider.gridx = 0;
		gbc_sizeSlider.gridy = 1;
		parametersPanel.add(sizeSlider, gbc_sizeSlider);
		sizeSlider.setMinorTickSpacing(1);
		sizeSlider.setMajorTickSpacing(5);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setSnapToTicks(true);

		// initialize the rule label in parameters panel
		JLabel ruleLabel = new JLabel("Rule number");
		GridBagConstraints gbc_ruleLabel = new GridBagConstraints();
		gbc_ruleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ruleLabel.gridx = 0;
		gbc_ruleLabel.gridy = 3;
		parametersPanel.add(ruleLabel, gbc_ruleLabel);

		// initialize the rule text area in parameters panel
		JTextField ruleTextArea = new JTextField();
		ruleTextArea.setText("30");
		GridBagConstraints gbc_ruleTextArea = new GridBagConstraints();
		gbc_ruleTextArea.insets = new Insets(0, 0, 5, 0);
		gbc_ruleTextArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_ruleTextArea.gridx = 1;
		gbc_ruleTextArea.gridy = 3;
		parametersPanel.add(ruleTextArea, gbc_ruleTextArea);

		// initialize the seed label in parameters panel
		JLabel seedLabel = new JLabel("Seed type");
		GridBagConstraints gbc_seedLabel = new GridBagConstraints();
		gbc_seedLabel.insets = new Insets(0, 0, 0, 5);
		gbc_seedLabel.gridx = 0;
		gbc_seedLabel.gridy = 4;
		parametersPanel.add(seedLabel, gbc_seedLabel);

		// initialize the seed combo box in parameters panel
		String[] seedTypes = {"Uniform", "Sparse", "Alternating"};
		@SuppressWarnings({"rawtypes", "unchecked"})
		JComboBox seedComboBox = new JComboBox(seedTypes);
		GridBagConstraints gbc_seedComboBox = new GridBagConstraints();
		gbc_seedComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_seedComboBox.gridx = 1;
		gbc_seedComboBox.gridy = 4;
		parametersPanel.add(seedComboBox, gbc_seedComboBox);

		// initialize the commands panel
		JPanel commandsPanel = new JPanel();
		commandsPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
		GridBagConstraints gbc_commandsPanel = new GridBagConstraints();
		gbc_commandsPanel.insets = new Insets(5, 5, 10, 10);
		gbc_commandsPanel.fill = GridBagConstraints.BOTH;
		gbc_commandsPanel.gridx = 1;
		gbc_commandsPanel.gridy = 1;
		CAFrame.getContentPane().add(commandsPanel, gbc_commandsPanel);
		GridBagLayout gbl_commandsPanel = new GridBagLayout();
		gbl_commandsPanel.columnWidths = new int[] {0, 0, 0};
		gbl_commandsPanel.rowHeights = new int[] {0, 0};
		gbl_commandsPanel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		gbl_commandsPanel.rowWeights = new double[] {1.0, Double.MIN_VALUE};
		commandsPanel.setLayout(gbl_commandsPanel);

		// initialize the display button in commands panel
		JButton displayButton = new JButton("Display");
		displayButton.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
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
					JOptionPane.showMessageDialog(CAFrame,
							"Rule number should be an integer \nbetween 0 and 255 (inclusive).", "Rule Number Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbc_displayButton = new GridBagConstraints();
		gbc_displayButton.insets = new Insets(0, 0, 0, 5);
		gbc_displayButton.gridx = 0;
		gbc_displayButton.gridy = 0;
		commandsPanel.add(displayButton, gbc_displayButton);

		// initialize the save button in commands panel
		JButton saveButton = new JButton("Save");
		saveButton.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
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
		GridBagConstraints gbc_saveButton = new GridBagConstraints();
		gbc_saveButton.gridx = 1;
		gbc_saveButton.gridy = 0;
		commandsPanel.add(saveButton, gbc_saveButton);
	}
}
