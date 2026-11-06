package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.controllers.ElementaryController;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ElementaryParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.shared.UiStyles;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Screen component representing the Elementary cellular automata workspace. */
public final class ElementaryWorkspace extends HBox {

  private final DisplayPanel displayPanel;
  private final ElementaryParametersPanel parametersPanel;
  private final CommandsPanel commandsPanel;
  private final SidebarPanel sidebarPanel;
  private final ElementaryController controller;

  /**
   * Constructs the Elementary workspace with default dialog and image services.
   *
   * @param onNavigateBack navigation callback invoked when returning to module selection
   */
  public ElementaryWorkspace(Runnable onNavigateBack) {
    this(new DialogService(), onNavigateBack);
  }

  /**
   * Constructs the Elementary workspace with an injected dialog service and default image service.
   *
   * @param dialogService dialog service used by workspace controllers
   * @param onNavigateBack navigation callback invoked when returning to module selection
   */
  public ElementaryWorkspace(DialogService dialogService, Runnable onNavigateBack) {
    this(
        dialogService,
        dialogService != null ? new AutomatonImageService(dialogService) : null,
        onNavigateBack);
  }

  /**
   * Constructs the Elementary workspace with injected dialog and image services.
   *
   * @param dialogService dialog service used by workspace controllers
   * @param imageService image service used by workspace controllers
   * @param onNavigateBack navigation callback invoked when returning to module selection
   * @throws IllegalArgumentException if any argument is null
   */
  public ElementaryWorkspace(
      DialogService dialogService, AutomatonImageService imageService, Runnable onNavigateBack) {
    if (dialogService == null) {
      throw new IllegalArgumentException("DialogService cannot be null.");
    }
    if (imageService == null) {
      throw new IllegalArgumentException("AutomatonImageService cannot be null.");
    }
    if (onNavigateBack == null) {
      throw new IllegalArgumentException("onNavigateBack cannot be null.");
    }

    this.displayPanel = new DisplayPanel();
    this.parametersPanel = new ElementaryParametersPanel();
    this.commandsPanel = new CommandsPanel();
    this.sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
    this.controller =
        new ElementaryController(
            displayPanel, parametersPanel, commandsPanel, dialogService, imageService);

    Label backLink = UiStyles.createBackLink(onNavigateBack);
    VBox displayContainer = UiStyles.createDisplayContainer(displayPanel, backLink);
    UiStyles.configureWorkspaceRoot(this, displayContainer, sidebarPanel);
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Controller is intentionally exposed for testing and coordination")
  public ElementaryController getController() {
    return controller;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Parameters panel is intentionally exposed for testing and inspection")
  public ElementaryParametersPanel getParametersPanel() {
    return parametersPanel;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Commands panel is intentionally exposed for testing and inspection")
  public CommandsPanel getCommandsPanel() {
    return commandsPanel;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Display panel is intentionally exposed for testing and inspection")
  public DisplayPanel getDisplayPanel() {
    return displayPanel;
  }
}
