package com.cellularautomata.ui;

import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.AutomataResult;
import com.cellularautomata.engine.rules.Rule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ParametersPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/** Controller responsible for coordinating actions between the UI panels and the core engine. */
class MainController {
  private final DisplayPanel displayPanel;
  private final ParametersPanel parametersPanel;
  private final CommandsPanel commandsPanel;
  private final DialogService dialogService;
  private final WorkspaceConfig config;

  private BufferedImage resizedAutomatonImage;
  private Rule rule;

  /**
   * Constructs the main controller to wire up the application logic.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param commandsPanel the commands panel
   * @param dialogService the dialog service
   * @param config the workspace configuration
   */
  MainController(
      DisplayPanel displayPanel,
      ParametersPanel parametersPanel,
      CommandsPanel commandsPanel,
      DialogService dialogService,
      WorkspaceConfig config) {
    this.displayPanel = displayPanel;
    this.parametersPanel = parametersPanel;
    this.commandsPanel = commandsPanel;
    this.dialogService = dialogService;
    this.config = config;
    setupActionListeners();
  }

  private void setupActionListeners() {
    commandsPanel.addGenerateListener(e -> generateAutomaton());
    commandsPanel.addSaveListener(e -> saveAutomatonImage());
  }

  private void generateAutomaton() {
    int states = parametersPanel.getStatesValue();

    // Defensive safeguard for manual text entry outside the spinner bounds
    if (config == WorkspaceConfig.TOTALISTIC
        && (states < com.cellularautomata.engine.rules.TotalisticRule.MIN_STATES
            || states > com.cellularautomata.engine.rules.TotalisticRule.MAX_STATES)) {
      dialogService.showStatesError(
          com.cellularautomata.engine.rules.TotalisticRule.MIN_STATES,
          com.cellularautomata.engine.rules.TotalisticRule.MAX_STATES);
      return;
    }

    int maxRule =
        config == WorkspaceConfig.TOTALISTIC
            ? com.cellularautomata.engine.rules.TotalisticRule.calculateMaxRule(states)
            : config.getMaxRule();
    OptionalInt parsedRule =
        RuleValidator.parseRule(parametersPanel.getRuleText(), config.getMinRule(), maxRule);
    if (!parsedRule.isPresent()) {
      dialogService.showRuleError(config.getMinRule(), maxRule);
      return;
    }

    int parsedRuleInt = parsedRule.getAsInt();
    rule = config.instantiateRule(parsedRuleInt, states);
    AutomataResult result =
        AutomataEngine.generate(
            rule, parametersPanel.getSizeValue(), states, parametersPanel.getSeedType());

    BufferedImage automatonImage = AutomatonImage.getImageFromMap(result.getAutomatonMap(), states);
    resizedAutomatonImage = AutomatonImage.resizeImage(400, 400, automatonImage);

    displayPanel.setAutomatonImage(SwingFXUtils.toFXImage(resizedAutomatonImage, null));
    commandsPanel.setSaveEnabled(true);
  }

  private void saveAutomatonImage() {
    FileChooser fileChooser = new FileChooser();

    File desktop = new File(System.getProperty("user.home"), "Desktop");
    if (desktop.exists() && desktop.isDirectory()) {
      fileChooser.setInitialDirectory(desktop);
    } else {
      File home = new File(System.getProperty("user.home"));
      if (home.exists() && home.isDirectory()) {
        fileChooser.setInitialDirectory(home);
      }
    }

    fileChooser.setInitialFileName(generateDefaultFileName());
    Window window = displayPanel.getScene().getWindow();
    File file = fileChooser.showSaveDialog(window);
    if (file == null) {
      return;
    }

    try {
      AutomatonImage.saveImage(resizedAutomatonImage, file);
    } catch (IOException ie) {
      ie.printStackTrace();
      dialogService.showSaveError();
    }
  }

  private String generateDefaultFileName() {
    String timestamp =
        java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    if (config == WorkspaceConfig.TOTALISTIC) {
      return config.name().toLowerCase()
          + "_states"
          + parametersPanel.getStatesValue()
          + "_rule"
          + rule.getRuleNumber()
          + "_size"
          + parametersPanel.getSizeValue()
          + "_"
          + timestamp
          + ".png";
    }
    return config.name().toLowerCase()
        + "_rule"
        + rule.getRuleNumber()
        + "_size"
        + parametersPanel.getSizeValue()
        + "_"
        + timestamp
        + ".png";
  }
}
