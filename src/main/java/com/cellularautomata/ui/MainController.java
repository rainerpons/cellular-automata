package com.cellularautomata.ui;

import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.engine.AutomataEngine;
import com.cellularautomata.engine.AutomataResult;
import com.cellularautomata.engine.Rule;
import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.Vector;
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
  private Vector seed;
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
    OptionalInt parsedRule =
        RuleValidator.parseRule(
            parametersPanel.getRuleText(), config.getMinRule(), config.getMaxRule());
    if (!parsedRule.isPresent()) {
      dialogService.showRuleError();
      return;
    }

    int parsedRuleInt = parsedRule.getAsInt();
    rule = config.createRule(parsedRuleInt);
    AutomataResult result =
        AutomataEngine.generate(
            rule, parametersPanel.getSizeValue(), parametersPanel.getSeedType());
    seed = result.getOriginalSeed();

    BufferedImage automatonImage = AutomatonImage.getImageFromMap(result.getAutomatonMap());
    resizedAutomatonImage = AutomatonImage.resizeImage(400, 400, automatonImage);

    displayPanel.setAutomatonImage(SwingFXUtils.toFXImage(resizedAutomatonImage, null));
    commandsPanel.setSaveEnabled(true);
  }

  private void saveAutomatonImage() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialFileName(AutomatonImage.getFileName(rule.getRuleNumber(), seed));
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
}
