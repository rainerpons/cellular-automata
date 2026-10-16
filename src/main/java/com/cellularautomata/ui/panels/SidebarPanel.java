package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * The right column of the application window. Stacks sidebar sections vertically with separators
 * between them and owns the sidebar padding and alignment contract.
 */
public final class SidebarPanel extends VBox {
  /**
   * Constructs the sidebar panel for Elementary and Totalistic workspaces.
   *
   * @param parametersPanel the parameters panel
   * @param commandsPanel the commands panel
   */
  public SidebarPanel(ParametersPanel parametersPanel, CommandsPanel commandsPanel) {
    addSections(parametersPanel, commandsPanel);
  }

  /**
   * Constructs the sidebar panel for the Life-like workspace.
   *
   * @param parametersPanel the Life-like parameters panel
   * @param simulationPanel the simulation controls panel
   * @param commandsPanel the commands panel
   */
  public SidebarPanel(
      LifeLikeParametersPanel parametersPanel,
      SimulationPanel simulationPanel,
      CommandsPanel commandsPanel) {
    addSections(parametersPanel, simulationPanel, commandsPanel);
  }

  private void addSections(Node... sections) {
    for (int i = 0; i < sections.length; i++) {
      if (i > 0) {
        getChildren().add(createSeparator());
      }
      getChildren().add(sections[i]);
    }

    // Consume remaining sidebar height so sidebar sections stay grouped at the top
    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);
    getChildren().add(spacer);
  }

  private static Separator createSeparator() {
    Separator separator = new Separator();
    VBox.setMargin(
        separator,
        new Insets(
            UiStyles.SECTION_SEPARATOR_TOP_GAP, 0, UiStyles.SECTION_SEPARATOR_BOTTOM_GAP, 0));
    return separator;
  }
}
