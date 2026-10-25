package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.JavaFxTestHelper;
import java.util.List;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;

/** Tests for {@link DisplayPanel}. */
public class DisplayPanelTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testDefaultAndCustomPlaceholders() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel defaultPanel = new DisplayPanel();
          Assert.assertEquals(
              DisplayPanel.DEFAULT_PLACEHOLDER_TEXT, defaultPanel.getPlaceholderText());
          Assert.assertNull(defaultPanel.getAutomatonImage());
          Assert.assertFalse(defaultPanel.isDisplayingError());

          DisplayPanel lifeLikePanel = new DisplayPanel(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT);
          Assert.assertEquals(
              DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT, lifeLikePanel.getPlaceholderText());
          Assert.assertNull(lifeLikePanel.getAutomatonImage());
          Assert.assertFalse(lifeLikePanel.isDisplayingError());
        });
  }

  @Test
  public void testSetAutomatonImageUpdatesViewClearsErrorAndMaintainsMutualExclusivity() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel panel = new DisplayPanel();

          // 1. Initial / Placeholder active state
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertNull(panel.getAutomatonImage());
          Assert.assertNotNull(panel.lookup(".placeholder"));
          Assert.assertNull(panel.lookup(".error-container"));

          // 2. Error active state
          panel.showGenerationError();
          Assert.assertTrue(panel.isDisplayingError());
          Assert.assertNotNull(panel.lookup(".error-container"));

          // 3. Image active state clears error and restores image view
          WritableImage image = new WritableImage(400, 400);
          panel.setAutomatonImage(image);
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertSame(image, panel.getAutomatonImage());
          Assert.assertNull(panel.lookup(".placeholder"));
          Assert.assertNull(panel.lookup(".error-container"));

          // 4. Setting null restores placeholder and clears image
          panel.setAutomatonImage(null);
          Assert.assertNull(panel.getAutomatonImage());
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertNotNull(panel.lookup(".placeholder"));
          Assert.assertNull(panel.lookup(".error-container"));

          // 5. showPlaceholder() explicitly restores placeholder
          panel.setAutomatonImage(image);
          Assert.assertSame(image, panel.getAutomatonImage());
          panel.showPlaceholder();
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertNull(panel.getAutomatonImage());
          Assert.assertNotNull(panel.lookup(".placeholder"));
          Assert.assertNull(panel.lookup(".error-container"));
        });
  }

  @Test
  public void testShowGenerationErrorDisplaysExactMessagesAndStyling() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel panel = new DisplayPanel();
          panel.showGenerationError();

          Assert.assertTrue(panel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.GENERATION_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
              panel.getErrorMessage());

          // Locate error container via semantic CSS selector
          VBox errorContainer = (VBox) panel.lookup(".error-container");
          Assert.assertNotNull(errorContainer);
          Assert.assertEquals(Pos.CENTER, errorContainer.getAlignment());
          Assert.assertTrue(errorContainer.getStyleClass().contains("placeholder"));
          Assert.assertEquals(400.0, errorContainer.getMinWidth(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getPrefWidth(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getMaxWidth(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getMinHeight(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getPrefHeight(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getMaxHeight(), 0.001);

          // Locate error icon via semantic CSS selector
          FontIcon icon = (FontIcon) panel.lookup(".error-icon");
          Assert.assertNotNull(icon);
          Assert.assertEquals(MaterialDesignA.ALERT_CIRCLE_OUTLINE, icon.getIconCode());
          Assert.assertEquals(40, icon.getIconSize());

          // Locate error labels via semantic CSS selector
          Set<Node> errorLabels = panel.lookupAll(".error-label");
          Assert.assertEquals(2, errorLabels.size());
          List<String> labelTexts =
              errorLabels.stream().map(node -> ((Label) node).getText()).toList();
          Assert.assertTrue(labelTexts.contains(DisplayPanel.GENERATION_ERROR_PRIMARY));
          Assert.assertTrue(labelTexts.contains(DisplayPanel.ERROR_SECONDARY));
        });
  }

  @Test
  public void testShowRenderingErrorDisplaysExactMessages() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel panel = new DisplayPanel();
          panel.showRenderingError();

          Assert.assertTrue(panel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.RENDERING_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
              panel.getErrorMessage());

          // Locate error labels via semantic CSS selector
          Set<Node> errorLabels = panel.lookupAll(".error-label");
          Assert.assertEquals(2, errorLabels.size());
          List<String> labelTexts =
              errorLabels.stream().map(node -> ((Label) node).getText()).toList();
          Assert.assertTrue(labelTexts.contains(DisplayPanel.RENDERING_ERROR_PRIMARY));
          Assert.assertTrue(labelTexts.contains(DisplayPanel.ERROR_SECONDARY));
        });
  }

  @Test
  public void testClearErrorRestoresActiveImageOrPlaceholder() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel panel = new DisplayPanel();

          // Case 1: No previous image -> restores placeholder
          panel.showGenerationError();
          Assert.assertTrue(panel.isDisplayingError());
          Assert.assertNotNull(panel.lookup(".error-container"));
          panel.clearError();
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertNull(panel.getAutomatonImage());
          Assert.assertNotNull(panel.lookup(".placeholder"));
          Assert.assertNull(panel.lookup(".error-container"));

          // Case 2: Image present before error -> restores image
          WritableImage image = new WritableImage(400, 400);
          panel.setAutomatonImage(image);
          Assert.assertNull(panel.lookup(".placeholder"));
          panel.showRenderingError();
          Assert.assertTrue(panel.isDisplayingError());
          Assert.assertNotNull(panel.lookup(".error-container"));
          panel.clearError();
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertSame(image, panel.getAutomatonImage());
          Assert.assertNull(panel.lookup(".placeholder"));
          Assert.assertNull(panel.lookup(".error-container"));
        });
  }
}
