package com.cellularautomata.engine.automata;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.rules.LifeLikeRule;

/** Manages simulation state and generational progression for 2D Life-like cellular automata. */
public final class LifeLikeSimulation {
  private final LifeLikeRule rule;
  private final Grid2D initialGrid;
  private Grid2D currentGrid;
  private long generation;

  /**
   * Constructs a LifeLikeSimulation with the specified rule and initial grid.
   *
   * @param rule the Life-like rule governing cell transitions
   * @param initialGrid the initial generation-0 grid
   * @throws IllegalArgumentException if rule or initialGrid is null
   */
  public LifeLikeSimulation(LifeLikeRule rule, Grid2D initialGrid) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    if (initialGrid == null) {
      throw new IllegalArgumentException("Initial grid cannot be null.");
    }
    this.rule = rule;
    this.initialGrid = initialGrid;
    this.currentGrid = initialGrid;
    this.generation = 0L;
  }

  /** Advances the simulation by one generation according to the rule. */
  public void step() {
    this.currentGrid = this.currentGrid.nextGeneration(this.rule);
    this.generation++;
  }

  /** Resets the simulation to its initial state and generation 0. */
  public void reset() {
    this.currentGrid = this.initialGrid;
    this.generation = 0L;
  }

  public Grid2D getCurrentGrid() {
    return currentGrid;
  }

  public Grid2D getInitialGrid() {
    return initialGrid;
  }

  public long getGeneration() {
    return generation;
  }

  public int getLiveCellCount() {
    return currentGrid.countLiveCells();
  }

  public LifeLikeRule getRule() {
    return rule;
  }

  public int getSize() {
    return currentGrid.getSize();
  }

  public boolean isAlive(int row, int col) {
    return currentGrid.isAlive(row, col);
  }
}
