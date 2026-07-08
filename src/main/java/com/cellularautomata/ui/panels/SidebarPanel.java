package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.UiStyles;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * The right column of the application window. Stacks ParametersPanel and CommandsPanel vertically
 * with a separator between them and owns the sidebar padding and alignment contract.
 */
public final class SidebarPanel extends VBox {
  /**
   * Constructs the sidebar panel.
   *
   * @param parametersPanel the parameters panel
   * @param commandsPanel the commands panel
   */
  public SidebarPanel(ParametersPanel parametersPanel, CommandsPanel commandsPanel) {
    // Stack panels with a separator between them.
    getChildren().add(parametersPanel);

    Separator separator = new Separator();
    VBox.setMargin(
        separator,
        new Insets(
            UiStyles.SECTION_SEPARATOR_TOP_GAP, 0, UiStyles.SECTION_SEPARATOR_BOTTOM_GAP, 0));
    getChildren().add(separator);

    getChildren().add(commandsPanel);

    // Consume remaining sidebar height so sidebar sections stay grouped at the top
    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);
    getChildren().add(spacer);
  }
}
