
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
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MainApp {
	private JFrame CAFrame;

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
		gbl_parametersPanel.rowHeights = new int[] {16, 38, 16, 38, 41, 0, 0};
		gbl_parametersPanel.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
		gbl_parametersPanel.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		parametersPanel.setLayout(gbl_parametersPanel);

		// initialize the size label in parameters panel
		JLabel sizeLabel = new JLabel("Size");
		GridBagConstraints gbc_sizeLabel = new GridBagConstraints();
		gbc_sizeLabel.gridwidth = 2;
		gbc_sizeLabel.anchor = GridBagConstraints.NORTH;
		gbc_sizeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_sizeLabel.gridx = 0;
		gbc_sizeLabel.gridy = 0;
		parametersPanel.add(sizeLabel, gbc_sizeLabel);

		// initialize the size slider in parameters panel
		JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 4, 32, 8);
		GridBagConstraints gbc_sizeSlider = new GridBagConstraints();
		gbc_sizeSlider.gridwidth = 2;
		gbc_sizeSlider.anchor = GridBagConstraints.NORTH;
		gbc_sizeSlider.insets = new Insets(0, 0, 5, 0);
		gbc_sizeSlider.gridx = 0;
		gbc_sizeSlider.gridy = 1;
		parametersPanel.add(sizeSlider, gbc_sizeSlider);
		sizeSlider.setMinorTickSpacing(1);
		sizeSlider.setMajorTickSpacing(7);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setSnapToTicks(true);

		// initialize the speed label in parameters panel
		JLabel speedLabel = new JLabel("Speed");
		GridBagConstraints gbc_speedLabel = new GridBagConstraints();
		gbc_speedLabel.gridwidth = 2;
		gbc_speedLabel.anchor = GridBagConstraints.NORTH;
		gbc_speedLabel.insets = new Insets(0, 0, 5, 0);
		gbc_speedLabel.gridx = 0;
		gbc_speedLabel.gridy = 2;
		parametersPanel.add(speedLabel, gbc_speedLabel);

		// initialize the speed slider in parameters panel
		JSlider speedSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 11, 5);
		speedSlider.setPaintTicks(true);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setMajorTickSpacing(5);
		speedSlider.setSnapToTicks(true);
		GridBagConstraints gbc_speedSlider = new GridBagConstraints();
		gbc_speedSlider.gridwidth = 2;
		gbc_speedSlider.insets = new Insets(0, 0, 5, 0);
		gbc_speedSlider.anchor = GridBagConstraints.NORTH;
		gbc_speedSlider.gridx = 0;
		gbc_speedSlider.gridy = 3;
		parametersPanel.add(speedSlider, gbc_speedSlider);

		// initialize the rule label in parameters panel
		JLabel ruleLabel = new JLabel("Rule number");
		GridBagConstraints gbc_ruleLabel = new GridBagConstraints();
		gbc_ruleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ruleLabel.gridx = 0;
		gbc_ruleLabel.gridy = 4;
		parametersPanel.add(ruleLabel, gbc_ruleLabel);

		// initialize the rule text area in parameters panel
		JTextField ruleTextArea = new JTextField();
		ruleTextArea.setText("30");
		GridBagConstraints gbc_ruleTextArea = new GridBagConstraints();
		gbc_ruleTextArea.insets = new Insets(0, 0, 5, 0);
		gbc_ruleTextArea.fill = GridBagConstraints.BOTH;
		gbc_ruleTextArea.gridx = 1;
		gbc_ruleTextArea.gridy = 4;
		parametersPanel.add(ruleTextArea, gbc_ruleTextArea);

		// initialize the seed label in parameters panel
		JLabel seedLabel = new JLabel("Seed type");
		GridBagConstraints gbc_seedLabel = new GridBagConstraints();
		gbc_seedLabel.insets = new Insets(0, 0, 0, 5);
		gbc_seedLabel.gridx = 0;
		gbc_seedLabel.gridy = 5;
		parametersPanel.add(seedLabel, gbc_seedLabel);

		// initialize the seed combo box in parameters panel
		String[] seedTypes = {"Uniform", "Sparse", "Alternating"};
		@SuppressWarnings({"rawtypes", "unchecked"})
		JComboBox seedComboBox = new JComboBox(seedTypes);
		GridBagConstraints gbc_seedComboBox = new GridBagConstraints();
		gbc_seedComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_seedComboBox.gridx = 1;
		gbc_seedComboBox.gridy = 5;
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
		gbl_commandsPanel.rowHeights = new int[] {0, 0, 0};
		gbl_commandsPanel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		gbl_commandsPanel.rowWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		commandsPanel.setLayout(gbl_commandsPanel);

		// initialize the display button in commands panel
		JButton displayButton = new JButton("Display");
		displayButton.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				int rule = Integer.parseInt(ruleTextArea.getText());
				if (rule > -1 && rule < 256) {
					int size = sizeSlider.getValue();
					String seedType = (String)seedComboBox.getSelectedItem();
					Vector seed = null;
					if (seedType.equalsIgnoreCase("uniform")) {
						seed = Generator.generateSeed(size);
					} else if (seedType.equalsIgnoreCase("sparse")) {
						seed = Generator.generateSparseSeed(size);
					} else if (seedType.equalsIgnoreCase("alternating")) {
						seed = Generator.generateAlternatingSeed(size);
					}
					Map<Integer, Vector> automatonMap = Automaton.initializeVectorMap(rule, seed);
					BufferedImage automatonImage = AutomatonImage.getImageFromMap(automatonMap);
					BufferedImage resizedAutomatonImage = AutomatonImage.resizeImage(280, 280, automatonImage);
					automatonLabel.setIcon(new ImageIcon(resizedAutomatonImage));
				}
			}
		});
		GridBagConstraints gbc_displayButton = new GridBagConstraints();
		gbc_displayButton.insets = new Insets(0, 0, 5, 5);
		gbc_displayButton.gridx = 0;
		gbc_displayButton.gridy = 0;
		commandsPanel.add(displayButton, gbc_displayButton);

		// initialize the save button in commands panel
		JButton saveButton = new JButton("Save");
		GridBagConstraints gbc_saveButton = new GridBagConstraints();
		gbc_saveButton.insets = new Insets(0, 0, 5, 0);
		gbc_saveButton.gridx = 1;
		gbc_saveButton.gridy = 0;
		commandsPanel.add(saveButton, gbc_saveButton);

		// initialize the previous button in commands panel
		JButton previousButton = new JButton("Previous");
		GridBagConstraints gbc_previousButton = new GridBagConstraints();
		gbc_previousButton.insets = new Insets(0, 0, 0, 5);
		gbc_previousButton.gridx = 0;
		gbc_previousButton.gridy = 1;
		commandsPanel.add(previousButton, gbc_previousButton);

		// initialize the next button in commands panel
		JButton nextButton = new JButton("Next");
		GridBagConstraints gbc_nextButton = new GridBagConstraints();
		gbc_nextButton.gridx = 1;
		gbc_nextButton.gridy = 1;
		commandsPanel.add(nextButton, gbc_nextButton);
	}
}
