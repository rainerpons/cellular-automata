package com.cellularautomata.ui;

/** Strategy interface for scheduling periodic simulation generation ticks. */
public interface SimulationScheduler {

  /**
   * Starts periodic execution of the specified tick action.
   *
   * @param tickAction the action executed periodically on each tick
   * @throws IllegalArgumentException if tickAction is null
   */
  void start(Runnable tickAction);

  /** Stops any ongoing periodic execution. */
  void stop();

  /**
   * Checks whether the scheduler is currently active.
   *
   * @return true if actively executing periodic ticks
   */
  boolean isRunning();

  /**
   * Sets the playback speed multiplier applied to periodic simulation ticks.
   *
   * @param speed the playback speed multiplier (must be finite and strictly positive)
   * @throws IllegalArgumentException if speed is NaN, infinite, or not strictly positive
   */
  void setPlaybackSpeed(double speed);

  double getPlaybackSpeed();
}
