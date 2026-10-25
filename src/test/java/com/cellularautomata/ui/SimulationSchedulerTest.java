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
}
