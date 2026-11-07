package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.controllers.TotalisticController;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.panels.TotalisticParametersPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.shared.UiLayouts;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Screen component representing the Totalistic cellular automata workspace. */
public final class TotalisticWorkspace extends HBox {

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

    DisplayPanel displayPanel = new DisplayPanel();
    TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
    new TotalisticController(
        displayPanel, parametersPanel, commandsPanel, dialogService, imageService);

    Label backLink = UiStyles.createBackLink(onNavigateBack);
    VBox displayContainer = UiLayouts.createDisplayContainer(displayPanel, backLink);
    UiLayouts.configureWorkspaceRoot(this, displayContainer, sidebarPanel);
  }
}
