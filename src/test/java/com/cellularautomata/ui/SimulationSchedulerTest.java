package com.cellularautomata.ui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import org.junit.Assert;
import org.junit.Test;

/** Unit tests for {@link SimulationScheduler} implementations. */
public class SimulationSchedulerTest {

  @Test
  public void testControlledSchedulerLifecycleAndManualTick() {
    ControlledScheduler scheduler = new ControlledScheduler();
    Assert.assertFalse(scheduler.isRunning());

    AtomicInteger ticks = new AtomicInteger(0);
    scheduler.start(ticks::incrementAndGet);
    Assert.assertTrue(scheduler.isRunning());

    scheduler.tick();
    scheduler.tick();
    Assert.assertEquals(2, ticks.get());

    scheduler.stop();
    Assert.assertFalse(scheduler.isRunning());
  }

  @Test(expected = IllegalStateException.class)
  public void testControlledSchedulerTickFailsWhenStopped() {
    ControlledScheduler scheduler = new ControlledScheduler();
    scheduler.tick();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControlledSchedulerRejectsNullAction() {
    ControlledScheduler scheduler = new ControlledScheduler();
    scheduler.start(null);
  }

  @Test
  public void testTimelineSchedulerLifecycleAndTickExecution() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          TimelineScheduler scheduler = new TimelineScheduler(Duration.millis(50));
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertNull(scheduler.getTimeline());

          AtomicInteger ticks = new AtomicInteger(0);
          scheduler.start(ticks::incrementAndGet);
          Assert.assertTrue(scheduler.isRunning());

          Timeline timeline = scheduler.getTimeline();
          Assert.assertNotNull(timeline);
          Assert.assertEquals(1, timeline.getKeyFrames().size());
          KeyFrame keyFrame = timeline.getKeyFrames().get(0);
          Assert.assertEquals(Duration.millis(50), keyFrame.getTime());

          // Verify the scheduled KeyFrame fires the registered tick action deterministically
          keyFrame.getOnFinished().handle(new ActionEvent());
          Assert.assertEquals(1, ticks.get());
          keyFrame.getOnFinished().handle(new ActionEvent());
          Assert.assertEquals(2, ticks.get());

          scheduler.stop();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertNull(scheduler.getTimeline());
        });
  }

  @Test
  public void testTimelineSchedulerDefaultConstructorUsesDefaultRate() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          TimelineScheduler scheduler = new TimelineScheduler();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertNull(scheduler.getTimeline());

          AtomicInteger ticks = new AtomicInteger(0);
          scheduler.start(ticks::incrementAndGet);
          Assert.assertTrue(scheduler.isRunning());

          Timeline timeline = scheduler.getTimeline();
          Assert.assertNotNull(timeline);
          Assert.assertEquals(1, timeline.getKeyFrames().size());
          Assert.assertEquals(
              Duration.millis(TimelineScheduler.DEFAULT_TICK_RATE_MS),
              timeline.getKeyFrames().get(0).getTime());

          timeline.getKeyFrames().get(0).getOnFinished().handle(new ActionEvent());
          Assert.assertEquals(1, ticks.get());

          // Calling start while running stops previous timeline and creates a new one
          Timeline firstTimeline = scheduler.getTimeline();
          scheduler.start(ticks::incrementAndGet);
          Timeline secondTimeline = scheduler.getTimeline();
          Assert.assertNotNull(secondTimeline);
          Assert.assertNotSame(firstTimeline, secondTimeline);

          scheduler.stop();
          Assert.assertFalse(scheduler.isRunning());
        });
  }

  @Test
  public void testTimelineSchedulerAsynchronousExecutionOnFxThread() throws InterruptedException {
    TimelineScheduler scheduler = new TimelineScheduler(Duration.millis(20));
    CountDownLatch latch = new CountDownLatch(2);
    AtomicBoolean ranOnFxThread = new AtomicBoolean(false);
    AtomicInteger tickCount = new AtomicInteger(0);

    try {
      JavaFxTestHelper.runOnFxThread(
          () ->
              scheduler.start(
                  () -> {
                    if (Platform.isFxApplicationThread()) {
                      ranOnFxThread.set(true);
                    }
                    tickCount.incrementAndGet();
                    latch.countDown();
                  }));

      boolean completed = latch.await(2, TimeUnit.SECONDS);
      Assert.assertTrue("TimelineScheduler should fire ticks within timeout", completed);
      Assert.assertTrue("Tick callback must execute on FX Application Thread", ranOnFxThread.get());
      Assert.assertTrue("At least two ticks should have executed", tickCount.get() >= 2);
    } finally {
      JavaFxTestHelper.runOnFxThread(
          () -> {
            Timeline activeTimeline = scheduler.getTimeline();
            scheduler.stop();
            Assert.assertFalse(scheduler.isRunning());
            Assert.assertNull(scheduler.getTimeline());
            if (activeTimeline != null) {
              Assert.assertEquals(Timeline.Status.STOPPED, activeTimeline.getStatus());
            }
          });
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTimelineSchedulerRejectsNullDuration() {
    new TimelineScheduler(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTimelineSchedulerRejectsNonPositiveDuration() {
    new TimelineScheduler(Duration.ZERO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTimelineSchedulerRejectsNullAction() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          TimelineScheduler scheduler = new TimelineScheduler();
          scheduler.start(null);
        });
  }

  @Test
  public void testControlledSchedulerPlaybackSpeedAndValidation() {
    ControlledScheduler scheduler = new ControlledScheduler();
    Assert.assertEquals(
        TimelineScheduler.DEFAULT_PLAYBACK_SPEED, scheduler.getPlaybackSpeed(), 0.001);
    Assert.assertFalse(scheduler.isRunning());
    Assert.assertEquals(0, scheduler.getStartCount());

    scheduler.setPlaybackSpeed(0.50);
    Assert.assertEquals(0.50, scheduler.getPlaybackSpeed(), 0.001);
    Assert.assertFalse(scheduler.isRunning());

    scheduler.start(() -> {});
    Assert.assertTrue(scheduler.isRunning());
    Assert.assertEquals(1, scheduler.getStartCount());

    scheduler.setPlaybackSpeed(1.75);
    Assert.assertEquals(1.75, scheduler.getPlaybackSpeed(), 0.001);
    Assert.assertTrue(scheduler.isRunning());
    Assert.assertEquals(1, scheduler.getStartCount());

    assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(0.0));
    assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(-0.25));
    assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(Double.NaN));
    assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(Double.POSITIVE_INFINITY));
    assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void testTimelineSchedulerPlaybackSpeedScalingAndLiveUpdateWithoutDuplicateLoops() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          TimelineScheduler scheduler = new TimelineScheduler();
          Assert.assertEquals(
              TimelineScheduler.DEFAULT_PLAYBACK_SPEED, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertEquals(
              Duration.millis(TimelineScheduler.DEFAULT_TICK_RATE_MS),
              scheduler.getEffectiveTickInterval());

          // Setting playback speed while stopped updates interval without starting
          scheduler.setPlaybackSpeed(0.25);
          Assert.assertEquals(0.25, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertEquals(Duration.millis(480.0), scheduler.getEffectiveTickInterval());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertNull(scheduler.getTimeline());

          // Starting applies the pre-selected 0.25x speed (480ms KeyFrame)
          AtomicInteger ticks = new AtomicInteger(0);
          scheduler.start(ticks::incrementAndGet);
          Assert.assertTrue(scheduler.isRunning());

          Timeline firstTimeline = scheduler.getTimeline();
          Assert.assertNotNull(firstTimeline);
          Assert.assertEquals(
              Duration.millis(480.0), firstTimeline.getKeyFrames().get(0).getTime());

          // Changing speed while running stops the previous Timeline and starts a new one at 2.00x
          // (60ms KeyFrame)
          scheduler.setPlaybackSpeed(2.00);
          Assert.assertEquals(2.00, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertEquals(Duration.millis(60.0), scheduler.getEffectiveTickInterval());
          Assert.assertTrue(scheduler.isRunning());

          Timeline secondTimeline = scheduler.getTimeline();
          Assert.assertNotNull(secondTimeline);
          Assert.assertNotSame(firstTimeline, secondTimeline);
          Assert.assertEquals(Timeline.Status.STOPPED, firstTimeline.getStatus());
          Assert.assertEquals(Timeline.Status.RUNNING, secondTimeline.getStatus());
          Assert.assertEquals(
              Duration.millis(60.0), secondTimeline.getKeyFrames().get(0).getTime());

          // Active tick callback is preserved on the updated timeline
          secondTimeline.getKeyFrames().get(0).getOnFinished().handle(new ActionEvent());
          Assert.assertEquals(1, ticks.get());

          // Invalid speeds are rejected
          assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(0.0));
          assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(-1.0));
          assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(Double.NaN));
          assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(Double.POSITIVE_INFINITY));
          assertThrowsIllegalArgument(() -> scheduler.setPlaybackSpeed(Double.NEGATIVE_INFINITY));

          scheduler.stop();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertEquals(Timeline.Status.STOPPED, secondTimeline.getStatus());
        });
  }

  private static void assertThrowsIllegalArgument(Runnable runnable) {
    try {
      runnable.run();
      Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }
  }
}
