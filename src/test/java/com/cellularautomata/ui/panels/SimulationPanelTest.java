package com.cellularautomata.ui.panels;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimulationPanelTest {

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
  public void testButtonsDisabledInitiallyWithExpectedIconsAndTooltips() {
    SimulationPanel panel = new SimulationPanel();

    Assert.assertTrue(panel.getPlayPauseButton().isDisabled());
    Assert.assertTrue(panel.getStepButton().isDisabled());
    Assert.assertTrue(panel.getResetButton().isDisabled());

    Assert.assertFalse(panel.isPlaying());
    Assert.assertEquals(SimulationPanel.PLAY_ICON, panel.getPlayPauseButton().getText());
    Assert.assertEquals("Play", panel.getPlayPauseButton().getTooltip().getText());
    Assert.assertEquals("Play", panel.getPlayPauseButton().getAccessibleText());

    Assert.assertEquals(SimulationPanel.STEP_ICON, panel.getStepButton().getText());
    Assert.assertEquals("Step", panel.getStepButton().getTooltip().getText());
    Assert.assertEquals("Step", panel.getStepButton().getAccessibleText());

    Assert.assertEquals(SimulationPanel.RESET_ICON, panel.getResetButton().getText());
    Assert.assertEquals("Reset", panel.getResetButton().getTooltip().getText());
    Assert.assertEquals("Reset", panel.getResetButton().getAccessibleText());
  }

  @Test
  public void testSetPlayingTogglesPlayAndPauseState() {
    SimulationPanel panel = new SimulationPanel();

    panel.setPlaying(true);
    Assert.assertTrue(panel.isPlaying());
    Assert.assertEquals(SimulationPanel.PAUSE_ICON, panel.getPlayPauseButton().getText());
    Assert.assertEquals("Pause", panel.getPlayPauseButton().getTooltip().getText());
    Assert.assertEquals("Pause", panel.getPlayPauseButton().getAccessibleText());

    panel.setPlaying(false);
    Assert.assertFalse(panel.isPlaying());
    Assert.assertEquals(SimulationPanel.PLAY_ICON, panel.getPlayPauseButton().getText());
    Assert.assertEquals("Play", panel.getPlayPauseButton().getTooltip().getText());
    Assert.assertEquals("Play", panel.getPlayPauseButton().getAccessibleText());
  }

  @Test
  public void testEnableAndFireSimulationListeners() {
    SimulationPanel panel = new SimulationPanel();
    panel.setSimulationEnabled(true);

    Assert.assertFalse(panel.getPlayPauseButton().isDisabled());
    Assert.assertFalse(panel.getStepButton().isDisabled());
    Assert.assertFalse(panel.getResetButton().isDisabled());

    AtomicBoolean playPauseFired = new AtomicBoolean(false);
    AtomicBoolean stepFired = new AtomicBoolean(false);
    AtomicBoolean resetFired = new AtomicBoolean(false);

    panel.addPlayPauseListener(e -> playPauseFired.set(true));
    panel.addStepListener(e -> stepFired.set(true));
    panel.addResetListener(e -> resetFired.set(true));

    panel.getPlayPauseButton().fire();
    panel.getStepButton().fire();
    panel.getResetButton().fire();

    Assert.assertTrue(playPauseFired.get());
    Assert.assertTrue(stepFired.get());
    Assert.assertTrue(resetFired.get());
  }
}
