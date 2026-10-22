package com.cellularautomata.ui;

/**
 * Headless state machine managing lifecycle states and legal transitions for Life-like cellular
 * automata simulations.
 *
 * <p>Supported states:
 *
 * <ul>
 *   <li>{@link State#UNINITIALIZED}: Initial state before any simulation has stepped or played.
 *   <li>{@link State#RUNNING}: Simulation is actively advancing across generations.
 *   <li>{@link State#PAUSED}: Simulation is halted at the current generation.
 * </ul>
 *
 * <p>Legal state transitions:
 *
 * <ul>
 *   <li>{@code UNINITIALIZED -> RUNNING} via {@link #onPlay()}
 *   <li>{@code UNINITIALIZED -> PAUSED} via {@link #onStep()}
 *   <li>{@code RUNNING -> PAUSED} via {@link #onPause()} or {@link #onReset()}
 *   <li>{@code RUNNING -> UNINITIALIZED} via {@link #onError()} or {@link #onStop()}
 *   <li>{@code PAUSED -> RUNNING} via {@link #onPlay()}
 *   <li>{@code PAUSED -> PAUSED} via {@link #onStep()} or {@link #onReset()}
 *   <li>{@code PAUSED -> UNINITIALIZED} via {@link #onError()} or {@link #onStop()}
 * </ul>
 *
 * <p>Any attempt to perform an illegal transition throws {@link IllegalStateException}.
 */
public final class LifeLikeStateMachine {

  /** Lifecycle states of a Life-like cellular automaton simulation. */
  public enum State {
    /** Initial state before any simulation generation has executed. */
    UNINITIALIZED,
    /** Simulation is actively advancing across generations via the scheduler. */
    RUNNING,
    /** Simulation is halted at its current generation. */
    PAUSED
  }

  private State state;

  /** Constructs the state machine in the {@link State#UNINITIALIZED} state. */
  public LifeLikeStateMachine() {
    this.state = State.UNINITIALIZED;
  }

  public State getState() {
    return state;
  }

  public boolean isUninitialized() {
    return state == State.UNINITIALIZED;
  }

  public boolean isRunning() {
    return state == State.RUNNING;
  }

  public boolean isPaused() {
    return state == State.PAUSED;
  }

  /**
   * Transitions to {@link State#RUNNING} from {@link State#UNINITIALIZED} or {@link State#PAUSED}.
   *
   * @throws IllegalStateException if called while already running
   */
  public void onPlay() {
    if (state == State.UNINITIALIZED || state == State.PAUSED) {
      state = State.RUNNING;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to RUNNING via onPlay from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#PAUSED} from {@link State#RUNNING}.
   *
   * @throws IllegalStateException if called when uninitialized or already paused
   */
  public void onPause() {
    if (state == State.RUNNING) {
      state = State.PAUSED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to PAUSED via onPause from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#PAUSED} from {@link State#UNINITIALIZED} or {@link State#PAUSED}.
   *
   * @throws IllegalStateException if called while running
   */
  public void onStep() {
    if (state == State.UNINITIALIZED || state == State.PAUSED) {
      state = State.PAUSED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to PAUSED via onStep from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#PAUSED} from {@link State#RUNNING} or {@link State#PAUSED}.
   *
   * @throws IllegalStateException if called when uninitialized
   */
  public void onReset() {
    if (state == State.RUNNING || state == State.PAUSED) {
      state = State.PAUSED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to PAUSED via onReset from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#UNINITIALIZED} from {@link State#RUNNING} or {@link State#PAUSED}.
   *
   * @throws IllegalStateException if called when already uninitialized
   */
  public void onError() {
    if (state == State.RUNNING || state == State.PAUSED) {
      state = State.UNINITIALIZED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to UNINITIALIZED via onError from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#UNINITIALIZED} from {@link State#RUNNING} or {@link State#PAUSED}.
   *
   * @throws IllegalStateException if called when already uninitialized
   */
  public void onStop() {
    if (state == State.RUNNING || state == State.PAUSED) {
      state = State.UNINITIALIZED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to UNINITIALIZED via onStop from state: %s".formatted(state));
  }
}
