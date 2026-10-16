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
   * Constructs the sidebar panel for the Elementary workspace.
   *
   * @param parametersPanel the Elementary parameters panel
   * @param commandsPanel the commands panel
   */
  public SidebarPanel(ElementaryParametersPanel parametersPanel, CommandsPanel commandsPanel) {
    addSections(parametersPanel.getPanel(), commandsPanel);
  }

  /**
   * Constructs the sidebar panel for the Totalistic workspace.
   *
   * @param parametersPanel the Totalistic parameters panel
   * @param commandsPanel the commands panel
   */
  public SidebarPanel(TotalisticParametersPanel parametersPanel, CommandsPanel commandsPanel) {
    addSections(parametersPanel.getPanel(), commandsPanel);
  }

  /**
   * Constructs the sidebar panel for the Life-like workspace, containing only the parameters
   * section.
   *
   * @param parametersPanel the Life-like parameters panel
   */
  public SidebarPanel(LifeLikeParametersPanel parametersPanel) {
    addSections(parametersPanel.getPanel());
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
