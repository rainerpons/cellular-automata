package com.cellularautomata.ui;

import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.AutomataResult;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.Rule;
import com.cellularautomata.engine.rules.TotalisticRule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.image.AutomatonImageExport;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.NumericRuleParametersPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.OptionalInt;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Window;

/** Controller responsible for coordinating actions between the UI panels and the core engine. */
class MainController {
  private static final String LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE =
      "Life-like rules use birth and survival counts";

  private final DisplayPanel displayPanel;
  private final NumericRuleParametersPanel parametersPanel;
  private final CommandsPanel commandsPanel;
  private final DialogService dialogService;
  private final WorkspaceConfig config;

  private BufferedImage resizedAutomatonImage;
  private Rule rule;

  /**
   * Constructs the main controller for a numeric-rule workspace.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the numeric-rule parameters panel
   * @param commandsPanel the commands panel
   * @param dialogService the dialog service
   * @param config the workspace configuration
   */
  MainController(
      DisplayPanel displayPanel,
      NumericRuleParametersPanel parametersPanel,
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
    if (config.supportsCustomStates() && !TotalisticRule.isValidStateCount(states)) {
      dialogService.showStatesError(TotalisticRule.MIN_STATES, TotalisticRule.MAX_STATES);
      return;
    }

    int minRule = getMinRule();
    int maxRule = getMaxRule(states);
    OptionalInt parsedRule =
        RuleValidator.parseRule(parametersPanel.getRuleText(), minRule, maxRule);
    if (!parsedRule.isPresent()) {
      dialogService.showRuleError(minRule, maxRule);
      return;
    }

    int parsedRuleInt = parsedRule.getAsInt();
    rule = instantiateRule(parsedRuleInt, states);
    AutomataResult result =
        AutomataEngine.generate(
            rule, parametersPanel.getSizeValue(), states, parametersPanel.getSeedType());

    BufferedImage automatonImage = AutomatonImage.getImageFromMap(result.getAutomatonMap(), states);
    resizedAutomatonImage =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, automatonImage);

    displayPanel.setAutomatonImage(SwingFXUtils.toFXImage(resizedAutomatonImage, null));
    commandsPanel.setSaveEnabled(true);
  }

  private void saveAutomatonImage() {
    String initialFileName =
        AutomatonImageExport.generateFileName(
            config,
            rule.getRuleNumber(),
            parametersPanel.getSizeValue(),
            parametersPanel.getStatesValue(),
            LocalDateTime.now());
    Window window = displayPanel.getScene() != null ? displayPanel.getScene().getWindow() : null;
    File file =
        dialogService.showSaveFileDialog(
            window, AutomatonImageExport.resolveInitialSaveDirectory(), initialFileName);
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

  int getMinRule() {
    return switch (config) {
      case ELEMENTARY -> ElementaryRule.MIN_RULE_NUMBER;
      case TOTALISTIC -> TotalisticRule.MIN_RULE_NUMBER;
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE);
    };
  }

  int getMaxRule(int states) {
    return switch (config) {
      case ELEMENTARY -> ElementaryRule.MAX_RULE_NUMBER;
      case TOTALISTIC -> TotalisticRule.calculateMaxRule(states);
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE);
    };
  }

  Rule instantiateRule(int ruleNumber, int states) {
    return switch (config) {
      case ELEMENTARY -> new ElementaryRule(ruleNumber);
      case TOTALISTIC -> new TotalisticRule(ruleNumber, states);
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(LIFE_LIKE_NUMERIC_RULE_UNSUPPORTED_MESSAGE);
    };
  }
}
