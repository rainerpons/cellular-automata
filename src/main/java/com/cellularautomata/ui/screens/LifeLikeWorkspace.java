package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.components.LifeLikeToolbar;
import com.cellularautomata.ui.controllers.LifeLikeController;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.shared.UiStyles;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Screen component representing the Life-like cellular automata workspace. */
public final class LifeLikeWorkspace extends HBox {

  private final DisplayPanel displayPanel;
  private final LifeLikeParametersPanel parametersPanel;
  private final LifeLikeToolbar toolbar;
  private final SidebarPanel sidebarPanel;
  private final LifeLikeController controller;

  /**
   * Constructs the Life-like workspace with default dialog and image services.
   *
   * @param onNavigateBack navigation callback invoked when returning to module selection
   */
  public LifeLikeWorkspace(Runnable onNavigateBack) {
    this(new DialogService(), onNavigateBack);
  }

  /**
   * Constructs the Life-like workspace with an injected dialog service and default image service.
   *
   * @param dialogService dialog service used by workspace controllers
   * @param onNavigateBack navigation callback invoked when returning to module selection
   */
  public LifeLikeWorkspace(DialogService dialogService, Runnable onNavigateBack) {
    this(dialogService != null ? new AutomatonImageService(dialogService) : null, onNavigateBack);
  }

  /**
   * Constructs the Life-like workspace with injected dialog and image services.
   *
   * @param dialogService dialog service used by workspace controllers
   * @param imageService image service used by workspace controllers
   * @param onNavigateBack navigation callback invoked when returning to module selection
   */
  public LifeLikeWorkspace(
      DialogService dialogService, AutomatonImageService imageService, Runnable onNavigateBack) {
    this(
        imageService != null
            ? imageService
            : (dialogService != null ? new AutomatonImageService(dialogService) : null),
        onNavigateBack);
  }

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

    this.toolbar = new LifeLikeToolbar();
    this.displayPanel = new DisplayPanel(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT);
    this.parametersPanel = new LifeLikeParametersPanel();
    this.sidebarPanel = new SidebarPanel(parametersPanel);
    this.controller = new LifeLikeController(displayPanel, parametersPanel, toolbar, imageService);

    Label backLink =
        UiStyles.createBackLink(
            () -> {
              dispose();
              onNavigateBack.run();
            });

    Region footerSpacer = new Region();
    HBox.setHgrow(footerSpacer, Priority.ALWAYS);
    HBox footerRow = new HBox(backLink, footerSpacer, toolbar);
    footerRow.setAlignment(Pos.CENTER_LEFT);

    VBox displayContainer = UiStyles.createDisplayContainer(displayPanel, footerRow);
    UiStyles.configureWorkspaceRoot(this, displayContainer, sidebarPanel);
  }

  /** Releases resources held by the workspace controller. */
  public void dispose() {
    controller.dispose();
  }

  public LifeLikeController getController() {
    return controller;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Parameters panel is intentionally exposed for testing and inspection")
  public LifeLikeParametersPanel getParametersPanel() {
    return parametersPanel;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Display panel is intentionally exposed for testing and inspection")
  public DisplayPanel getDisplayPanel() {
    return displayPanel;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Toolbar is intentionally exposed for testing and inspection")
  public LifeLikeToolbar getToolbar() {
    return toolbar;
  }
}
