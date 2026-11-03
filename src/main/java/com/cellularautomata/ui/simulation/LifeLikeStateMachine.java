package com.cellularautomata.ui.simulation;

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
 *   <li>{@link State#FIXED_POINT}: Simulation halted automatically after two consecutive
 *       generations produced identical grids; Play and Step remain disabled until Reset.
 * </ul>
 *
 * <p>Legal state transitions:
 *
 * <ul>
 *   <li>{@code UNINITIALIZED -> RUNNING} via {@link #onPlay()}
 *   <li>{@code UNINITIALIZED -> PAUSED} via {@link #onStep()}
 *   <li>{@code RUNNING -> PAUSED} via {@link #onPause()} or {@link #onReset()}
 *   <li>{@code RUNNING -> FIXED_POINT} via {@link #onFixedPoint()}
 *   <li>{@code RUNNING -> UNINITIALIZED} via {@link #onError()} or {@link #onStop()}
 *   <li>{@code PAUSED -> RUNNING} via {@link #onPlay()}
 *   <li>{@code PAUSED -> PAUSED} via {@link #onStep()} or {@link #onReset()}
 *   <li>{@code PAUSED -> FIXED_POINT} via {@link #onFixedPoint()}
 *   <li>{@code PAUSED -> UNINITIALIZED} via {@link #onError()} or {@link #onStop()}
 *   <li>{@code FIXED_POINT -> PAUSED} via {@link #onReset()}
 *   <li>{@code FIXED_POINT -> UNINITIALIZED} via {@link #onError()} or {@link #onStop()}
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
    PAUSED,
    /**
     * Simulation halted automatically after reaching a fixed point (two consecutive identical
     * grids). Play and Step remain disabled until Reset.
     */
    FIXED_POINT
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

  public boolean isFixedPoint() {
    return state == State.FIXED_POINT;
  }

  /**
   * Transitions to {@link State#RUNNING} from {@link State#UNINITIALIZED} or {@link State#PAUSED}.
   *
   * @throws IllegalStateException if called while already running or at a fixed point
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
   * @throws IllegalStateException if called when uninitialized, already paused, or at a fixed point
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
   * @throws IllegalStateException if called while running or at a fixed point
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
   * Transitions to {@link State#FIXED_POINT} from {@link State#RUNNING} or {@link State#PAUSED}
   * when two consecutive generations produce identical grids.
   *
   * @throws IllegalStateException if called when uninitialized or already at a fixed point
   */
  public void onFixedPoint() {
    if (state == State.RUNNING || state == State.PAUSED) {
      state = State.FIXED_POINT;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to FIXED_POINT via onFixedPoint from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#PAUSED} from {@link State#RUNNING}, {@link State#PAUSED}, or {@link
   * State#FIXED_POINT}.
   *
   * @throws IllegalStateException if called when uninitialized
   */
  public void onReset() {
    if (state == State.RUNNING || state == State.PAUSED || state == State.FIXED_POINT) {
      state = State.PAUSED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to PAUSED via onReset from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#UNINITIALIZED} from {@link State#RUNNING}, {@link State#PAUSED}, or
   * {@link State#FIXED_POINT}.
   *
   * @throws IllegalStateException if called when already uninitialized
   */
  public void onError() {
    if (state == State.RUNNING || state == State.PAUSED || state == State.FIXED_POINT) {
      state = State.UNINITIALIZED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to UNINITIALIZED via onError from state: %s".formatted(state));
  }

  /**
   * Transitions to {@link State#UNINITIALIZED} from {@link State#RUNNING}, {@link State#PAUSED}, or
   * {@link State#FIXED_POINT}.
   *
   * @throws IllegalStateException if called when already uninitialized
   */
  public void onStop() {
    if (state == State.RUNNING || state == State.PAUSED || state == State.FIXED_POINT) {
      state = State.UNINITIALIZED;
      return;
    }
    throw new IllegalStateException(
        "Cannot transition to UNINITIALIZED via onStop from state: %s".formatted(state));
  }
}
