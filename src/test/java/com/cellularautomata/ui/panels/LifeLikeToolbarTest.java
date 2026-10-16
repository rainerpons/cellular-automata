package com.cellularautomata.ui.panels;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LifeLikeToolbarTest {

  @BeforeClass
  public static void initJavaFx() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    try {
      Platform.startup(latch::countDown);
      latch.await(5, TimeUnit.SECONDS);
    } catch (IllegalStateException e) {
      // Toolkit already initialized
    }
  }

  @Test
  public void testInitialButtonStatesIconsTooltipsAndAccessibleText() {
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
    Assert.assertEquals(
        ContentDisplay.GRAPHIC_ONLY, toolbar.getPlayPauseButton().getContentDisplay());
    Assert.assertEquals(LifeLikeToolbar.PLAY_ICON_PATH, toolbar.getPlayPauseIconPath());
    Assert.assertEquals(
        LifeLikeToolbar.PLAY_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
    Assert.assertEquals("Play", toolbar.getPlayPauseButton().getAccessibleText());

    Assert.assertEquals(ContentDisplay.GRAPHIC_ONLY, toolbar.getStepButton().getContentDisplay());
    Assert.assertEquals(LifeLikeToolbar.STEP_ICON_PATH, toolbar.getStepIconPath());
    Assert.assertEquals(
        LifeLikeToolbar.STEP_TOOLTIP, toolbar.getStepButton().getTooltip().getText());
    Assert.assertEquals("Step", toolbar.getStepButton().getAccessibleText());

    Assert.assertEquals(ContentDisplay.GRAPHIC_ONLY, toolbar.getResetButton().getContentDisplay());
    Assert.assertEquals(LifeLikeToolbar.RESET_ICON_PATH, toolbar.getResetIconPath());
    Assert.assertEquals(
        LifeLikeToolbar.RESET_TOOLTIP, toolbar.getResetButton().getTooltip().getText());
    Assert.assertEquals("Reset", toolbar.getResetButton().getAccessibleText());

    Assert.assertEquals(ContentDisplay.GRAPHIC_ONLY, toolbar.getSaveButton().getContentDisplay());
    Assert.assertEquals(LifeLikeToolbar.SAVE_ICON_PATH, toolbar.getSaveIconPath());
    Assert.assertEquals(
        LifeLikeToolbar.SAVE_TOOLTIP, toolbar.getSaveButton().getTooltip().getText());
    Assert.assertEquals("Save", toolbar.getSaveButton().getAccessibleText());
  }

  @Test
  public void testSetPlayingTogglesPlayAndPauseIconTooltipAndAccessibleText() {
    LifeLikeToolbar toolbar = new LifeLikeToolbar();

    toolbar.setPlaying(true);
    Assert.assertTrue(toolbar.isPlaying());
    Assert.assertEquals(LifeLikeToolbar.PAUSE_ICON_PATH, toolbar.getPlayPauseIconPath());
    Assert.assertEquals(
        LifeLikeToolbar.PAUSE_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
    Assert.assertEquals("Pause", toolbar.getPlayPauseButton().getAccessibleText());

    toolbar.setPlaying(false);
    Assert.assertFalse(toolbar.isPlaying());
    Assert.assertEquals(LifeLikeToolbar.PLAY_ICON_PATH, toolbar.getPlayPauseIconPath());
    Assert.assertEquals(
        LifeLikeToolbar.PLAY_TOOLTIP, toolbar.getPlayPauseButton().getTooltip().getText());
    Assert.assertEquals("Play", toolbar.getPlayPauseButton().getAccessibleText());
  }

  @Test
  public void testButtonEnablementAndActionListeners() {
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
  }

  @Test
  public void testLifeLikeSidebarContainsOnlyParametersSectionAndDisplayPlaceholderMatches() {
    LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel);

    Assert.assertEquals(2, sidebarPanel.getChildren().size());
    Assert.assertSame(parametersPanel.getPanel(), sidebarPanel.getChildren().get(0));
    Assert.assertTrue(sidebarPanel.getChildren().get(1) instanceof Region);

    DisplayPanel defaultDisplay = new DisplayPanel();
    Assert.assertEquals(DisplayPanel.DEFAULT_PLACEHOLDER_TEXT, defaultDisplay.getPlaceholderText());

    DisplayPanel lifeLikeDisplay = new DisplayPanel(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT);
    Assert.assertEquals(
        DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT, lifeLikeDisplay.getPlaceholderText());
    Assert.assertFalse(lifeLikeDisplay.getPlaceholderText().contains("Generate"));
  }
}
