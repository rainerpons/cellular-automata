package com.cellularautomata.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * JavaFX {@link Timeline}-backed implementation of {@link SimulationScheduler}.
 *
 * <p>Executes ticks at a fixed rate (default 150ms) on the JavaFX application thread.
 */
public final class TimelineScheduler implements SimulationScheduler {

  /** Default interval between simulation generation ticks in milliseconds. */
  public static final int DEFAULT_TICK_RATE_MS = 150;

  private final Duration tickInterval;
  private Timeline timeline;

  /** Constructs a scheduler with the default 150ms tick interval. */
  public TimelineScheduler() {
    this(Duration.millis(DEFAULT_TICK_RATE_MS));
  }

  /**
   * Constructs a scheduler with a custom tick interval.
   *
   * @param tickInterval the interval between ticks
   * @throws IllegalArgumentException if tickInterval is null or not strictly positive
   */
  public TimelineScheduler(Duration tickInterval) {
    if (tickInterval == null
        || tickInterval.isUnknown()
        || tickInterval.lessThanOrEqualTo(Duration.ZERO)) {
      throw new IllegalArgumentException("Tick interval must be strictly positive.");
    }
    this.tickInterval = tickInterval;
  }

  @Override
  public void start(Runnable tickAction) {
    if (tickAction == null) {
      throw new IllegalArgumentException("Tick action cannot be null.");
    }
    stop();
    timeline = new Timeline(new KeyFrame(tickInterval, event -> tickAction.run()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  @Override
  public void stop() {
    if (timeline != null) {
      timeline.stop();
      timeline = null;
    }
  }

  @Override
  public boolean isRunning() {
    return timeline != null && timeline.getStatus() == Timeline.Status.RUNNING;
  }

  Timeline getTimeline() {
    return timeline;
  }
}
