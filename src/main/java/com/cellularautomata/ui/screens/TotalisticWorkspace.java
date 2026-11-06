package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.controllers.TotalisticController;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.panels.TotalisticParametersPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.shared.UiStyles;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Screen component representing the Totalistic cellular automata workspace. */
public final class TotalisticWorkspace extends HBox {

  private final DisplayPanel displayPanel;
  private final TotalisticParametersPanel parametersPanel;
  private final CommandsPanel commandsPanel;
  private final SidebarPanel sidebarPanel;
  private final TotalisticController controller;

  /**
   * Constructs the Totalistic workspace with default dialog and image services.
   *
   * @param onNavigateBack navigation callback invoked when returning to module selection
   */
  public TotalisticWorkspace(Runnable onNavigateBack) {
    this(new DialogService(), onNavigateBack);
  }

  /**
   * Constructs the Totalistic workspace with an injected dialog service and default image service.
   *
   * @param dialogService dialog service used by workspace controllers
   * @param onNavigateBack navigation callback invoked when returning to module selection
   */
  public TotalisticWorkspace(DialogService dialogService, Runnable onNavigateBack) {
    this(
        dialogService,
        dialogService != null ? new AutomatonImageService(dialogService) : null,
        onNavigateBack);
  }

  /**
   * Constructs the Totalistic workspace with injected dialog and image services.
   *
   * @param dialogService dialog service used by workspace controllers
   * @param imageService image service used by workspace controllers
   * @param onNavigateBack navigation callback invoked when returning to module selection
   * @throws IllegalArgumentException if any argument is null
   */
  public TotalisticWorkspace(
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
    this.parametersPanel = new TotalisticParametersPanel();
    this.commandsPanel = new CommandsPanel();
    this.sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
    this.controller =
        new TotalisticController(
            displayPanel, parametersPanel, commandsPanel, dialogService, imageService);

    Label backLink = UiStyles.createBackLink(onNavigateBack);
    VBox displayContainer = UiStyles.createDisplayContainer(displayPanel, backLink);
    UiStyles.configureWorkspaceRoot(this, displayContainer, sidebarPanel);
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Controller is intentionally exposed for testing and coordination")
  public TotalisticController getController() {
    return controller;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Parameters panel is intentionally exposed for testing and inspection")
  public TotalisticParametersPanel getParametersPanel() {
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
