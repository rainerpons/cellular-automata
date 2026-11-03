package com.cellularautomata.ui.controllers;

import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.AutomataResult;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.image.AutomatonImageExport;
import com.cellularautomata.ui.AutomatonFileNameFormatter;
import com.cellularautomata.ui.DialogService;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ElementaryParametersPanel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.OptionalInt;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Window;

/**
 * Controller responsible for coordinating actions in the Elementary cellular automata workspace.
 */
public final class ElementaryController {

  private final DisplayPanel displayPanel;
  private final ElementaryParametersPanel parametersPanel;
  private final CommandsPanel commandsPanel;
  private final DialogService dialogService;

  private BufferedImage resizedAutomatonImage;
  private ElementaryRule rule;
  private int generatedSize;

  /**
   * Constructs the Elementary controller with the default dialog service.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the Elementary parameters panel
   * @param commandsPanel the commands panel
   */
  public ElementaryController(
      DisplayPanel displayPanel,
      ElementaryParametersPanel parametersPanel,
      CommandsPanel commandsPanel) {
    this(displayPanel, parametersPanel, commandsPanel, new DialogService());
  }

  /**
   * Constructs the Elementary controller with an injected dialog service.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the Elementary parameters panel
   * @param commandsPanel the commands panel
   * @param dialogService the dialog service
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification =
          "Controller intentionally retains references to active JavaFX workspace panels to"
              + " coordinate UI state")
  public ElementaryController(
      DisplayPanel displayPanel,
      ElementaryParametersPanel parametersPanel,
      CommandsPanel commandsPanel,
      DialogService dialogService) {
    if (displayPanel == null) {
      throw new IllegalArgumentException("DisplayPanel cannot be null.");
    }
    if (parametersPanel == null) {
      throw new IllegalArgumentException("ParametersPanel cannot be null.");
    }
    if (commandsPanel == null) {
      throw new IllegalArgumentException("CommandsPanel cannot be null.");
    }
    if (dialogService == null) {
      throw new IllegalArgumentException("DialogService cannot be null.");
    }
    this.displayPanel = displayPanel;
    this.parametersPanel = parametersPanel;
    this.commandsPanel = commandsPanel;
    this.dialogService = dialogService;
    setupActionListeners();
  }

  private void setupActionListeners() {
    commandsPanel.addGenerateListener(e -> generateAutomaton());
    commandsPanel.addSaveListener(e -> saveAutomatonImage());
  }

  void generateAutomaton() {
    OptionalInt parsedRule =
        RuleValidator.parseRule(
            parametersPanel.getRuleText(),
            ElementaryRule.MIN_RULE_NUMBER,
            ElementaryRule.MAX_RULE_NUMBER);
    if (!parsedRule.isPresent()) {
      dialogService.showRuleError(ElementaryRule.MIN_RULE_NUMBER, ElementaryRule.MAX_RULE_NUMBER);
      return;
    }

    int size = parametersPanel.getSizeValue();
    rule = new ElementaryRule(parsedRule.getAsInt());
    AutomataResult result =
        AutomataEngine.generate(
            rule, size, ElementaryRule.STATE_COUNT, parametersPanel.getSeedType());
    generatedSize = size;

    BufferedImage automatonImage =
        AutomatonImage.getImageFromMap(result.getAutomatonMap(), ElementaryRule.STATE_COUNT);
    resizedAutomatonImage =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, automatonImage);

    displayPanel.setAutomatonImage(SwingFXUtils.toFXImage(resizedAutomatonImage, null));
    commandsPanel.setSaveEnabled(true);
  }

  void saveAutomatonImage() {
    if (rule == null || resizedAutomatonImage == null) {
      return;
    }

    String initialFileName =
        AutomatonFileNameFormatter.formatFileName(rule, generatedSize, LocalDateTime.now());
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
}
