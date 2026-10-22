package com.cellularautomata.ui;

import java.util.concurrent.atomic.AtomicInteger;
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
  public void testTimelineSchedulerLifecycle() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          TimelineScheduler scheduler = new TimelineScheduler(Duration.millis(50));
          Assert.assertFalse(scheduler.isRunning());

          AtomicInteger ticks = new AtomicInteger(0);
          scheduler.start(ticks::incrementAndGet);
          Assert.assertTrue(scheduler.isRunning());

          scheduler.stop();
          Assert.assertFalse(scheduler.isRunning());
        });
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
