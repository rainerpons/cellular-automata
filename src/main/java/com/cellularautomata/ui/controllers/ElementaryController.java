package com.cellularautomata.ui.controllers;

import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.AutomataResult;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.image.AutomatonImageUtils;
import com.cellularautomata.ui.AutomatonFileNameFormatter;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ElementaryParametersPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.image.BufferedImage;
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
  private final AutomatonImageService imageService;

  private BufferedImage resizedAutomatonImage;
  private ElementaryRule rule;
  private int generatedSize;

  /**
   * Constructs the Elementary controller with the default dialog service and image service.
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
  public ElementaryController(
      DisplayPanel displayPanel,
      ElementaryParametersPanel parametersPanel,
      CommandsPanel commandsPanel,
      DialogService dialogService) {
    this(
        displayPanel,
        parametersPanel,
        commandsPanel,
        dialogService,
        new AutomatonImageService(dialogService));
  }

  /**
   * Constructs the Elementary controller with fully injected collaborators.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the Elementary parameters panel
   * @param commandsPanel the commands panel
   * @param dialogService the dialog service
   * @param imageService the automaton image service
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
      DialogService dialogService,
      AutomatonImageService imageService) {
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
    if (imageService == null) {
      throw new IllegalArgumentException("AutomatonImageService cannot be null.");
    }
    this.displayPanel = displayPanel;
    this.parametersPanel = parametersPanel;
    this.commandsPanel = commandsPanel;
    this.dialogService = dialogService;
    this.imageService = imageService;
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
        AutomatonImageUtils.getImageFromMap(result.getAutomatonMap(), ElementaryRule.STATE_COUNT);
    resizedAutomatonImage =
        AutomatonImageUtils.resizeImage(
            AutomatonImageUtils.DISPLAY_IMAGE_SIZE,
            AutomatonImageUtils.DISPLAY_IMAGE_SIZE,
            automatonImage);

    displayPanel.setAutomatonImage(SwingFXUtils.toFXImage(resizedAutomatonImage, null));
    commandsPanel.setSaveEnabled(true);
  }

  void saveAutomatonImage() {
    if (rule == null || resizedAutomatonImage == null) {
      return;
    }

    String initialFileName = AutomatonFileNameFormatter.formatFileName(rule, generatedSize);
    Window window = displayPanel.getScene() != null ? displayPanel.getScene().getWindow() : null;
    imageService.saveImage(window, resizedAutomatonImage, initialFileName);
  }
}
