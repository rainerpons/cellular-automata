package com.cellularautomata.ui;

/**
 * Deterministic test scheduler allowing manual simulation ticks without thread sleeps or JavaFX
 * animation timers.
 */
public final class ControlledScheduler implements SimulationScheduler {

  private Runnable tickAction;
  private boolean running;

  @Override
  public void start(Runnable tickAction) {
    if (tickAction == null) {
      throw new IllegalArgumentException("Tick action cannot be null.");
    }
    this.tickAction = tickAction;
    this.running = true;
  }

  @Override
  public void stop() {
    this.running = false;
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  /**
   * Manually triggers a single simulation tick if the scheduler is running.
   *
   * @throws IllegalStateException if the scheduler is not currently running
   */
  public void tick() {
    if (!running || tickAction == null) {
      throw new IllegalStateException("Scheduler is not running.");
    }
    tickAction.run();
  }
}
