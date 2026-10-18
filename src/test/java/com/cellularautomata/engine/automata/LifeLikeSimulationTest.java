package com.cellularautomata.engine.automata;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.rules.LifeLikeRule;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link LifeLikeSimulation}. */
public class LifeLikeSimulationTest {

  @Test
  public void testConstructionAndInitialState() {
    Grid2D initialGrid = Grid2D.ofSparse(5, 2, 2);
    LifeLikeSimulation simulation =
        new LifeLikeSimulation(LifeLikeRule.CONWAYS_GAME_OF_LIFE, initialGrid);

    Assert.assertEquals(0L, simulation.getGeneration());
    Assert.assertEquals(LifeLikeRule.CONWAYS_GAME_OF_LIFE, simulation.getRule());
    Assert.assertEquals(initialGrid, simulation.getInitialGrid());
    Assert.assertEquals(initialGrid, simulation.getCurrentGrid());
    Assert.assertEquals(5, simulation.getSize());
    Assert.assertEquals(1, simulation.getLiveCellCount());
    Assert.assertTrue(simulation.isAlive(2, 2));
    Assert.assertFalse(simulation.isAlive(0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullRule() {
    new LifeLikeSimulation(null, Grid2D.empty(4));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullInitialGrid() {
    new LifeLikeSimulation(LifeLikeRule.CONWAYS_GAME_OF_LIFE, null);
  }

  @Test
  public void testStepAdvancesSimulation() {
    // 5x5 horizontal blinker
    boolean[][] blinker = new boolean[5][5];
    blinker[2][1] = true;
    blinker[2][2] = true;
    blinker[2][3] = true;
    Grid2D initialGrid = new Grid2D(5, blinker);

    LifeLikeSimulation simulation =
        new LifeLikeSimulation(LifeLikeRule.CONWAYS_GAME_OF_LIFE, initialGrid);

    simulation.step();
    Assert.assertEquals(1L, simulation.getGeneration());
    Assert.assertEquals(3, simulation.getLiveCellCount());
    Assert.assertTrue(simulation.isAlive(1, 2));
    Assert.assertTrue(simulation.isAlive(2, 2));
    Assert.assertTrue(simulation.isAlive(3, 2));
    Assert.assertFalse(simulation.isAlive(2, 1));
    Assert.assertFalse(simulation.isAlive(2, 3));
    Assert.assertNotEquals(simulation.getCurrentGrid(), simulation.getInitialGrid());

    simulation.step();
    Assert.assertEquals(2L, simulation.getGeneration());
    Assert.assertEquals(3, simulation.getLiveCellCount());
    Assert.assertEquals(initialGrid, simulation.getCurrentGrid());
  }

  @Test
  public void testResetRestoresInitialGridAndGeneration() {
    Grid2D initialGrid = Grid2D.ofSparse(5, 2, 2);
    LifeLikeSimulation simulation =
        new LifeLikeSimulation(LifeLikeRule.CONWAYS_GAME_OF_LIFE, initialGrid);

    Assert.assertEquals(1, simulation.getLiveCellCount());

    // Single isolated cell dies under Conway's Game of Life
    simulation.step();
    Assert.assertEquals(1L, simulation.getGeneration());
    Assert.assertEquals(0, simulation.getLiveCellCount());
    Assert.assertFalse(simulation.isAlive(2, 2));

    simulation.step();
    Assert.assertEquals(2L, simulation.getGeneration());
    Assert.assertEquals(0, simulation.getLiveCellCount());

    // Reset restores initial grid and generation 0
    simulation.reset();
    Assert.assertEquals(0L, simulation.getGeneration());
    Assert.assertEquals(1, simulation.getLiveCellCount());
    Assert.assertTrue(simulation.isAlive(2, 2));
    Assert.assertEquals(initialGrid, simulation.getCurrentGrid());
    Assert.assertEquals(initialGrid, simulation.getInitialGrid());
  }

  @Test
  public void testDelegatedCellQueriesMatchCurrentGrid() {
    boolean[][] matrix = {
      {true, false, false},
      {false, true, false},
      {false, false, true}
    };
    Grid2D grid = new Grid2D(3, matrix);
    LifeLikeSimulation simulation = new LifeLikeSimulation(LifeLikeRule.CONWAYS_GAME_OF_LIFE, grid);

    Assert.assertEquals(3, simulation.getSize());
    Assert.assertEquals(3, simulation.getLiveCellCount());
    Assert.assertTrue(simulation.isAlive(0, 0));
    Assert.assertTrue(simulation.isAlive(1, 1));
    Assert.assertTrue(simulation.isAlive(2, 2));
    Assert.assertFalse(simulation.isAlive(0, 1));
  }
}
