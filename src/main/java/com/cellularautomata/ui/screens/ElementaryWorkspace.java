package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.controllers.ElementaryController;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ElementaryParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.shared.UiLayouts;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Screen component representing the Elementary cellular automata workspace. */
public final class ElementaryWorkspace extends HBox {

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

    DisplayPanel displayPanel = new DisplayPanel();
    ElementaryParametersPanel parametersPanel = new ElementaryParametersPanel();
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
    new ElementaryController(
        displayPanel, parametersPanel, commandsPanel, dialogService, imageService);

    Label backLink = UiStyles.createBackLink(onNavigateBack);
    VBox displayContainer = UiLayouts.createDisplayContainer(displayPanel, backLink);
    UiLayouts.configureWorkspaceRoot(this, displayContainer, sidebarPanel);
  }
}
