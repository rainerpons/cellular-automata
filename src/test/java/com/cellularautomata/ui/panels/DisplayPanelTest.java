package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.JavaFxTestHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
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
  public void testSetAutomatonImageUpdatesViewAndClearsError() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel panel = new DisplayPanel();
          panel.showGenerationError();
          Assert.assertTrue(panel.isDisplayingError());

          WritableImage image = new WritableImage(400, 400);
          panel.setAutomatonImage(image);

          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertSame(image, panel.getAutomatonImage());

          // Setting null restores placeholder
          panel.setAutomatonImage(null);
          Assert.assertNull(panel.getAutomatonImage());
          Assert.assertFalse(panel.isDisplayingError());
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
              DisplayPanel.GENERATION_ERROR_PRIMARY, panel.getPrimaryErrorMessage());
          Assert.assertEquals(DisplayPanel.ERROR_SECONDARY, panel.getSecondaryErrorMessage());
          Assert.assertEquals(
              DisplayPanel.GENERATION_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
              panel.getErrorMessage());

          // Verify error container structure and styling
          StackPane imageContainer = (StackPane) panel.getChildren().get(1);
          VBox errorContainer = (VBox) imageContainer.getChildren().get(0);
          Assert.assertEquals(Pos.CENTER, errorContainer.getAlignment());
          Assert.assertTrue(errorContainer.getStyleClass().contains("placeholder"));
          Assert.assertTrue(errorContainer.getStyleClass().contains("error-container"));
          Assert.assertEquals(400.0, errorContainer.getMinWidth(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getPrefWidth(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getMaxWidth(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getMinHeight(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getPrefHeight(), 0.001);
          Assert.assertEquals(400.0, errorContainer.getMaxHeight(), 0.001);

          // Verify icon
          Assert.assertTrue(errorContainer.getChildren().get(0) instanceof FontIcon);
          FontIcon icon = (FontIcon) errorContainer.getChildren().get(0);
          Assert.assertEquals(MaterialDesignA.ALERT_CIRCLE_OUTLINE, icon.getIconCode());
          Assert.assertEquals(40, icon.getIconSize());
          Assert.assertTrue(icon.getStyleClass().contains("error-icon"));

          // Verify primary and secondary labels
          Label primary = (Label) errorContainer.getChildren().get(1);
          Assert.assertEquals(DisplayPanel.GENERATION_ERROR_PRIMARY, primary.getText());
          Assert.assertTrue(primary.getStyleClass().contains("error-label"));

          Label secondary = (Label) errorContainer.getChildren().get(2);
          Assert.assertEquals(DisplayPanel.ERROR_SECONDARY, secondary.getText());
          Assert.assertTrue(secondary.getStyleClass().contains("error-label"));
        });
  }

  @Test
  public void testShowRenderingErrorDisplaysExactMessages() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel panel = new DisplayPanel();
          panel.showRenderingError();

          Assert.assertTrue(panel.isDisplayingError());
          Assert.assertEquals(DisplayPanel.RENDERING_ERROR_PRIMARY, panel.getPrimaryErrorMessage());
          Assert.assertEquals(DisplayPanel.ERROR_SECONDARY, panel.getSecondaryErrorMessage());
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
          panel.clearError();
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertNull(panel.getAutomatonImage());

          // Case 2: Image present before error -> restores image
          WritableImage image = new WritableImage(400, 400);
          panel.setAutomatonImage(image);
          panel.showRenderingError();
          Assert.assertTrue(panel.isDisplayingError());
          panel.clearError();
          Assert.assertFalse(panel.isDisplayingError());
          Assert.assertSame(image, panel.getAutomatonImage());
        });
  }
}
