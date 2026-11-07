package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.components.LifeLikeToolbar;
import com.cellularautomata.ui.controllers.LifeLikeController;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.shared.UiLayouts;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Screen component representing the Life-like cellular automata workspace. */
public final class LifeLikeWorkspace extends HBox {

  /**
   * Constructs the Life-like workspace with an injected image service.
   *
   * @param imageService image service used by workspace controllers
   * @param onNavigateBack navigation callback invoked when returning to module selection
   * @throws IllegalArgumentException if any argument is null
   */
  public LifeLikeWorkspace(AutomatonImageService imageService, Runnable onNavigateBack) {
    if (imageService == null) {
      throw new IllegalArgumentException("AutomatonImageService cannot be null.");
    }
    if (onNavigateBack == null) {
      throw new IllegalArgumentException("onNavigateBack cannot be null.");
    }

    LifeLikeToolbar toolbar = new LifeLikeToolbar();
    DisplayPanel displayPanel = new DisplayPanel(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT);
    LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel);
    LifeLikeController controller =
        new LifeLikeController(displayPanel, parametersPanel, toolbar, imageService);

    Label backLink =
        UiStyles.createBackLink(
            () -> {
              controller.dispose();
              onNavigateBack.run();
            });

    Region footerSpacer = new Region();
    HBox.setHgrow(footerSpacer, Priority.ALWAYS);
    HBox footerRow = new HBox(backLink, footerSpacer, toolbar);
    footerRow.setAlignment(Pos.CENTER_LEFT);

    VBox displayContainer = UiLayouts.createDisplayContainer(displayPanel, footerRow);
    UiLayouts.configureWorkspaceRoot(this, displayContainer, sidebarPanel);
  }
}
