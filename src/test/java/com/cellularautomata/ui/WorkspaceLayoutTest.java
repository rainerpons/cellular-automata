package com.cellularautomata.ui;

import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkspaceLayoutTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testOneDimensionalWorkspacesShareTopLevelStructureAndIncludeCommandsPanel() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          MainApp app = new MainApp();

          for (WorkspaceConfig config :
              new WorkspaceConfig[] {WorkspaceConfig.ELEMENTARY, WorkspaceConfig.TOTALISTIC}) {
            AtomicBoolean navigatedBack = new AtomicBoolean(false);
            HBox root = app.createWorkspace(config, () -> navigatedBack.set(true));

            Assert.assertEquals(2, root.getChildren().size());
            Assert.assertTrue(root.getChildren().get(0) instanceof VBox);
            Assert.assertTrue(root.getChildren().get(1) instanceof SidebarPanel);

            VBox displayContainer = (VBox) root.getChildren().get(0);
            SidebarPanel sidebarPanel = (SidebarPanel) root.getChildren().get(1);
            Assert.assertEquals(Priority.ALWAYS, HBox.getHgrow(sidebarPanel));

            Assert.assertEquals(2, displayContainer.getChildren().size());
            Assert.assertTrue(displayContainer.getChildren().get(0) instanceof DisplayPanel);
            Assert.assertTrue(displayContainer.getChildren().get(1) instanceof Label);

            DisplayPanel displayPanel = (DisplayPanel) displayContainer.getChildren().get(0);
            Assert.assertEquals(
                DisplayPanel.DEFAULT_PLACEHOLDER_TEXT, displayPanel.getPlaceholderText());

            Label backLink = (Label) displayContainer.getChildren().get(1);
            Assert.assertEquals("← Back to Modules", backLink.getText());
            backLink.getOnMouseClicked().handle(null);
            Assert.assertTrue(navigatedBack.get());

            Assert.assertTrue(containsChildOfType(sidebarPanel, ParametersPanel.class));
            Assert.assertTrue(containsChildOfType(sidebarPanel, CommandsPanel.class));
          }
        });
  }

  @Test
  public void testLifeLikeWorkspaceAlignsFooterToolbarWithBackLinkAndOmitsCommandsSection() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          MainApp app = new MainApp();
          AtomicBoolean navigatedBack = new AtomicBoolean(false);
          HBox root = app.createWorkspace(WorkspaceConfig.LIFE_LIKE, () -> navigatedBack.set(true));

          Assert.assertEquals(2, root.getChildren().size());
          Assert.assertTrue(root.getChildren().get(0) instanceof VBox);
          Assert.assertTrue(root.getChildren().get(1) instanceof SidebarPanel);

          VBox displayContainer = (VBox) root.getChildren().get(0);
          SidebarPanel sidebarPanel = (SidebarPanel) root.getChildren().get(1);
          Assert.assertEquals(Priority.ALWAYS, HBox.getHgrow(sidebarPanel));

          Assert.assertEquals(2, displayContainer.getChildren().size());
          Assert.assertTrue(displayContainer.getChildren().get(0) instanceof DisplayPanel);
          Assert.assertTrue(displayContainer.getChildren().get(1) instanceof HBox);

          DisplayPanel displayPanel = (DisplayPanel) displayContainer.getChildren().get(0);
          Assert.assertEquals(
              DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT, displayPanel.getPlaceholderText());
          Assert.assertFalse(displayPanel.getPlaceholderText().contains("Generate"));

          HBox footerRow = (HBox) displayContainer.getChildren().get(1);
          Assert.assertEquals(Pos.CENTER_LEFT, footerRow.getAlignment());
          Assert.assertEquals(3, footerRow.getChildren().size());

          Assert.assertTrue(footerRow.getChildren().get(0) instanceof Label);
          Label backLink = (Label) footerRow.getChildren().get(0);
          Assert.assertEquals("← Back to Modules", backLink.getText());
          backLink.getOnMouseClicked().handle(null);
          Assert.assertTrue(navigatedBack.get());

          Node spacer = footerRow.getChildren().get(1);
          Assert.assertTrue(spacer instanceof Region);
          Assert.assertEquals(Priority.ALWAYS, HBox.getHgrow(spacer));

          Assert.assertTrue(footerRow.getChildren().get(2) instanceof LifeLikeToolbar);
          LifeLikeToolbar toolbar = (LifeLikeToolbar) footerRow.getChildren().get(2);
          Assert.assertEquals(Pos.CENTER_RIGHT, toolbar.getAlignment());

          Assert.assertTrue(containsChildOfType(sidebarPanel, ParametersPanel.class));
          Assert.assertFalse(containsChildOfType(sidebarPanel, CommandsPanel.class));
        });
  }

  private static boolean containsChildOfType(SidebarPanel sidebarPanel, Class<?> type) {
    for (Node child : sidebarPanel.getChildren()) {
      if (type.isInstance(child)) {
        return true;
      }
    }
    return false;
  }
}
