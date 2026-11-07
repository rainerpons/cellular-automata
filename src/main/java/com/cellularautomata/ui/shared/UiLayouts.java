package com.cellularautomata.ui.shared;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Shared workspace layout construction helpers. */
public final class UiLayouts {

  private UiLayouts() {}

  /**
   * Creates a display container wrapping the given display node and footer node.
   *
   * @param displayNode the display node
   * @param footer the footer node beneath the display node
   * @return the configured display container
   */
  public static VBox createDisplayContainer(Node displayNode, Node footer) {
    VBox.setMargin(footer, new Insets(UiStyles.APP_SPACING, 0, 0, 0));
    return new VBox(displayNode, footer);
  }

  /**
   * Configures a workspace root container with standard spacing, padding, and layout constraints.
   *
   * @param root the workspace root HBox
   * @param displayContainer the display container VBox
   * @param sidebarPanel the sidebar panel node
   */
  public static void configureWorkspaceRoot(HBox root, VBox displayContainer, Node sidebarPanel) {
    root.setSpacing(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, Priority.ALWAYS);
    root.getChildren().addAll(displayContainer, sidebarPanel);
  }
}
