package com.cellularautomata.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * JavaFX {@link Timeline}-backed implementation of {@link SimulationScheduler}.
 *
 * <p>Executes ticks at a configurable rate (default 120ms at 1.00x playback speed) on the JavaFX
 * application thread.
 */
public final class TimelineScheduler implements SimulationScheduler {

  /** Default interval between simulation generation ticks in milliseconds. */
  public static final int DEFAULT_TICK_RATE_MS = 120;

  /** Default playback speed multiplier. */
  public static final double DEFAULT_PLAYBACK_SPEED = 1.0;

  private final Duration baseTickInterval;
  private double playbackSpeed = DEFAULT_PLAYBACK_SPEED;
  private Runnable activeTickAction;
  private Timeline timeline;

  /** Constructs a scheduler with the default 120ms tick interval. */
  public TimelineScheduler() {
    this(Duration.millis(DEFAULT_TICK_RATE_MS));
  }

  /**
   * Constructs a scheduler with a custom base tick interval.
   *
   * @param tickInterval the base interval between ticks at 1.00x playback speed
   * @throws IllegalArgumentException if tickInterval is null or not strictly positive
   */
  public TimelineScheduler(Duration tickInterval) {
    if (tickInterval == null
        || tickInterval.isUnknown()
        || tickInterval.lessThanOrEqualTo(Duration.ZERO)) {
      throw new IllegalArgumentException("Tick interval must be strictly positive.");
    }
    this.baseTickInterval = tickInterval;
  }

  @Override
  public void start(Runnable tickAction) {
    if (tickAction == null) {
      throw new IllegalArgumentException("Tick action cannot be null.");
    }
    stop();
    this.activeTickAction = tickAction;
    startTimeline(tickAction);
  }

  @Override
  public void stop() {
    if (timeline != null) {
      timeline.stop();
      timeline = null;
    }
    activeTickAction = null;
  }

  @Override
  public boolean isRunning() {
    return timeline != null && timeline.getStatus() == Timeline.Status.RUNNING;
  }

  @Override
  public void setPlaybackSpeed(double speed) {
    if (Double.isNaN(speed) || Double.isInfinite(speed) || speed <= 0.0) {
      throw new IllegalArgumentException("Playback speed must be finite and strictly positive.");
    }
    this.playbackSpeed = speed;
    if (isRunning() && activeTickAction != null) {
      Runnable tickAction = activeTickAction;
      timeline.stop();
      timeline = null;
      startTimeline(tickAction);
    }
  }

  @Override
  public double getPlaybackSpeed() {
    return playbackSpeed;
  }

  Duration getEffectiveTickInterval() {
    return baseTickInterval.divide(playbackSpeed);
  }

  Timeline getTimeline() {
    return timeline;
  }

  private void startTimeline(Runnable tickAction) {
    timeline = new Timeline(new KeyFrame(getEffectiveTickInterval(), event -> tickAction.run()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }
}
