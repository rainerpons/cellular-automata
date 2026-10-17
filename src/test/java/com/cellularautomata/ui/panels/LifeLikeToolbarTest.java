package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.JavaFxTestHelper;
import com.cellularautomata.ui.panels.LifeLikeToolbar.ToolbarIcon;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LifeLikeToolbarTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testInitialButtonStatesDimensionsIconsTooltipsAndAccessibleText() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          Assert.assertEquals(4, toolbar.getChildren().size());
          Assert.assertSame(toolbar.getPlayPauseButton(), toolbar.getChildren().get(0));
          Assert.assertSame(toolbar.getStepButton(), toolbar.getChildren().get(1));
          Assert.assertSame(toolbar.getResetButton(), toolbar.getChildren().get(2));
          Assert.assertSame(toolbar.getSaveButton(), toolbar.getChildren().get(3));

          // Play/Pause and Step start enabled; Reset and Save start disabled
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());

          Assert.assertFalse(toolbar.isPlaying());
          assertToolbarButtonContract(
              toolbar.getPlayPauseButton(),
              toolbar.getPlayPauseIcon(),
              ToolbarIcon.PLAY,
              LifeLikeToolbar.PLAY_TOOLTIP,
              "Play");
          assertToolbarButtonContract(
              toolbar.getStepButton(),
              toolbar.getStepIcon(),
              ToolbarIcon.STEP,
              LifeLikeToolbar.STEP_TOOLTIP,
              "Step");
          assertToolbarButtonContract(
              toolbar.getResetButton(),
              toolbar.getResetIcon(),
              ToolbarIcon.RESET,
              LifeLikeToolbar.RESET_TOOLTIP,
              "Reset");
          assertToolbarButtonContract(
              toolbar.getSaveButton(),
              toolbar.getSaveIcon(),
              ToolbarIcon.SAVE,
              LifeLikeToolbar.SAVE_TOOLTIP,
              "Save");
        });
  }

  @Test
  public void testSetPlayingTogglesPlayAndPauseIconTooltipAndAccessibleText() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          toolbar.setPlaying(true);
          Assert.assertTrue(toolbar.isPlaying());
          Assert.assertEquals(ToolbarIcon.PAUSE, toolbar.getPlayPauseIcon());
          Assert.assertEquals(
              LifeLikeToolbar.PAUSE_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
          Assert.assertEquals("Pause", toolbar.getPlayPauseButton().getAccessibleText());

          toolbar.setPlaying(false);
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertEquals(ToolbarIcon.PLAY, toolbar.getPlayPauseIcon());
          Assert.assertEquals(
              LifeLikeToolbar.PLAY_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
          Assert.assertEquals("Play", toolbar.getPlayPauseButton().getAccessibleText());
        });
  }

  @Test
  public void testButtonEnablementAndActionListeners() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          toolbar.setResetEnabled(true);
          toolbar.setSaveEnabled(true);

          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());

          AtomicBoolean playPauseFired = new AtomicBoolean(false);
          AtomicBoolean stepFired = new AtomicBoolean(false);
          AtomicBoolean resetFired = new AtomicBoolean(false);
          AtomicBoolean saveFired = new AtomicBoolean(false);

          toolbar.addPlayPauseListener(e -> playPauseFired.set(true));
          toolbar.addStepListener(e -> stepFired.set(true));
          toolbar.addResetListener(e -> resetFired.set(true));
          toolbar.addSaveListener(e -> saveFired.set(true));

          toolbar.getPlayPauseButton().fire();
          toolbar.getStepButton().fire();
          toolbar.getResetButton().fire();
          toolbar.getSaveButton().fire();

          Assert.assertTrue(playPauseFired.get());
          Assert.assertTrue(stepFired.get());
          Assert.assertTrue(resetFired.get());
          Assert.assertTrue(saveFired.get());

          toolbar.setPlayPauseEnabled(false);
          toolbar.setStepEnabled(false);
          toolbar.setResetEnabled(false);
          toolbar.setSaveEnabled(false);

          Assert.assertTrue(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertTrue(toolbar.getStepButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
        });
  }

  @Test
  public void testLifeLikeSidebarContainsOnlyParametersSectionAndDisplayPlaceholderMatches() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel);

          Assert.assertEquals(2, sidebarPanel.getChildren().size());
          Assert.assertSame(parametersPanel.getPanel(), sidebarPanel.getChildren().get(0));
          Assert.assertTrue(sidebarPanel.getChildren().get(1) instanceof Region);

          DisplayPanel defaultDisplay = new DisplayPanel();
          Assert.assertEquals(
              DisplayPanel.DEFAULT_PLACEHOLDER_TEXT, defaultDisplay.getPlaceholderText());

          DisplayPanel lifeLikeDisplay = new DisplayPanel(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT);
          Assert.assertEquals(
              DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT, lifeLikeDisplay.getPlaceholderText());
          Assert.assertFalse(lifeLikeDisplay.getPlaceholderText().contains("Generate"));
        });
  }

  private static void assertToolbarButtonContract(
      Button button,
      ToolbarIcon actualIcon,
      ToolbarIcon expectedIcon,
      String expectedTooltip,
      String expectedAccessibleText) {
    Assert.assertEquals(ContentDisplay.GRAPHIC_ONLY, button.getContentDisplay());
    Assert.assertEquals(expectedIcon, actualIcon);
    Assert.assertTrue(button.getGraphic() instanceof SVGPath);
    SVGPath iconGraphic = (SVGPath) button.getGraphic();
    Assert.assertNotNull(iconGraphic.getContent());
    Assert.assertFalse(iconGraphic.getContent().isBlank());
    Assert.assertEquals(expectedTooltip, button.getTooltip().getText());
    Assert.assertEquals(expectedAccessibleText, button.getAccessibleText());

    double expectedSize = UiStyles.TOOLBAR_BUTTON_SIZE;
    Assert.assertEquals(expectedSize, button.getMinWidth(), 0.001);
    Assert.assertEquals(expectedSize, button.getPrefWidth(), 0.001);
    Assert.assertEquals(expectedSize, button.getMaxWidth(), 0.001);
    Assert.assertEquals(expectedSize, button.getMinHeight(), 0.001);
    Assert.assertEquals(expectedSize, button.getPrefHeight(), 0.001);
    Assert.assertEquals(expectedSize, button.getMaxHeight(), 0.001);
  }
}
