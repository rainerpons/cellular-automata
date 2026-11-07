package com.cellularautomata.ui;

import com.cellularautomata.ui.components.LifeLikeToolbar;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.screens.ElementaryWorkspace;
import com.cellularautomata.ui.screens.LifeLikeWorkspace;
import com.cellularautomata.ui.screens.TotalisticWorkspace;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);
          for (WorkspaceConfig config :
              new WorkspaceConfig[] {WorkspaceConfig.ELEMENTARY, WorkspaceConfig.TOTALISTIC}) {
            HBox root =
                switch (config) {
                  case ELEMENTARY -> new ElementaryWorkspace(dialogService, imageService, () -> {});
                  case TOTALISTIC -> new TotalisticWorkspace(dialogService, imageService, () -> {});
                  default -> throw new IllegalArgumentException("Unexpected config: " + config);
                };

            Assert.assertEquals(UiStyles.APP_SPACING, root.getSpacing(), 0.001);
            Assert.assertEquals(new Insets(UiStyles.APP_SPACING), root.getPadding());

            DisplayPanel displayPanel = findNode(root, DisplayPanel.class, null);
            SidebarPanel sidebarPanel = findNode(root, SidebarPanel.class, null);
            Label backLink =
                findNode(root, Label.class, lbl -> "← Back to Modules".equals(lbl.getText()));

            Assert.assertNotNull(displayPanel);
            Assert.assertNotNull(sidebarPanel);
            Assert.assertNotNull(backLink);

            Assert.assertEquals(Priority.ALWAYS, HBox.getHgrow(sidebarPanel));
            Assert.assertEquals(
                DisplayPanel.DEFAULT_PLACEHOLDER_TEXT, displayPanel.getPlaceholderText());

            Assert.assertTrue(containsChildOfType(sidebarPanel, ParametersPanel.class));
            Assert.assertTrue(containsChildOfType(sidebarPanel, CommandsPanel.class));
          }
        });
  }

  @Test
  public void testLifeLikeWorkspaceAlignsFooterToolbarWithBackLinkAndOmitsCommandsSection() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);
          HBox root = new LifeLikeWorkspace(imageService, () -> {});

          Assert.assertEquals(UiStyles.APP_SPACING, root.getSpacing(), 0.001);
          Assert.assertEquals(new Insets(UiStyles.APP_SPACING), root.getPadding());

          DisplayPanel displayPanel = findNode(root, DisplayPanel.class, null);
          SidebarPanel sidebarPanel = findNode(root, SidebarPanel.class, null);
          LifeLikeToolbar toolbar = findNode(root, LifeLikeToolbar.class, null);
          Label backLink =
              findNode(root, Label.class, lbl -> "← Back to Modules".equals(lbl.getText()));

          Assert.assertNotNull(displayPanel);
          Assert.assertNotNull(sidebarPanel);
          Assert.assertNotNull(toolbar);
          Assert.assertNotNull(backLink);

          Assert.assertEquals(Priority.ALWAYS, HBox.getHgrow(sidebarPanel));
          Assert.assertEquals(
              DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT, displayPanel.getPlaceholderText());
          Assert.assertFalse(displayPanel.getPlaceholderText().contains("Generate"));

          // Footer row aligns back-link on left, expanding spacer in middle, and toolbar on right
          HBox footerRow = (HBox) backLink.getParent();
          Assert.assertEquals(Pos.CENTER_LEFT, footerRow.getAlignment());
          Assert.assertSame(footerRow, toolbar.getParent());

          Node spacer = null;
          for (Node child : footerRow.getChildren()) {
            if (child != backLink && child != toolbar && child instanceof Region) {
              spacer = child;
              break;
            }
          }
          Assert.assertNotNull(spacer);
          Assert.assertEquals(Priority.ALWAYS, HBox.getHgrow(spacer));
          Assert.assertEquals(Pos.CENTER_RIGHT, toolbar.getAlignment());

          Assert.assertTrue(containsChildOfType(sidebarPanel, ParametersPanel.class));
          Assert.assertFalse(containsChildOfType(sidebarPanel, CommandsPanel.class));
        });
  }

  private static <T extends Node> T findNode(
      Node root, Class<T> clazz, java.util.function.Predicate<T> predicate) {
    if (clazz.isInstance(root)) {
      T cast = clazz.cast(root);
      if (predicate == null || predicate.test(cast)) {
        return cast;
      }
    }
    if (root instanceof Parent parent) {
      for (Node child : parent.getChildrenUnmodifiable()) {
        T found = findNode(child, clazz, predicate);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
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
