package com.cellularautomata.engine;

import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link Grid2D}. */
public class Grid2DTest {

  @Test
  public void testConstructorValidSizeAndEmptyFactory() {
    Grid2D grid = new Grid2D(4);
    Assert.assertEquals(4, grid.getSize());
    Assert.assertEquals(0, grid.countLiveCells());
    for (int r = 0; r < 4; r++) {
      for (int c = 0; c < 4; c++) {
        Assert.assertFalse(grid.isAlive(r, c));
        Assert.assertEquals(0, grid.getCell(r, c));
      }
    }

    Grid2D empty = Grid2D.empty(5);
    Assert.assertEquals(5, empty.getSize());
    Assert.assertEquals(0, empty.countLiveCells());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsZeroSize() {
    new Grid2D(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNegativeSize() {
    new Grid2D(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyFactoryRejectsZeroSize() {
    Grid2D.empty(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsOverflowSize() {
    new Grid2D(50000);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsMaxIntegerSize() {
    new Grid2D(Integer.MAX_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyFactoryRejectsOverflowSize() {
    Grid2D.empty(50000);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCellInitializerRejectsOverflowSize() {
    new Grid2D(50000, (r, c) -> false);
  }

  @Test
  public void testConstructorWithMatrix() {
    boolean[][] matrix = {
      {true, false, true},
      {false, true, false},
      {true, false, false}
    };
    Grid2D grid = new Grid2D(3, matrix);
    Assert.assertEquals(3, grid.getSize());
    Assert.assertEquals(4, grid.countLiveCells());
    Assert.assertTrue(grid.isAlive(0, 0));
    Assert.assertFalse(grid.isAlive(0, 1));
    Assert.assertTrue(grid.isAlive(0, 2));
    Assert.assertEquals(1, grid.getCell(1, 1));
    Assert.assertEquals(0, grid.getCell(1, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullMatrix() {
    new Grid2D(3, (boolean[][]) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsMismatchedMatrixRowCount() {
    boolean[][] matrix = {{true, false}, {false, true}, {false, false}};
    new Grid2D(2, matrix);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullMatrixRow() {
    boolean[][] matrix = {{true, false}, null};
    new Grid2D(2, matrix);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsMismatchedMatrixColumnCount() {
    boolean[][] matrix = {{true, false, false}, {false, true, false}, {false, false}};
    new Grid2D(3, matrix);
  }

  @Test
  public void testConstructorWithCellInitializer() {
    Grid2D grid = new Grid2D(3, (r, c) -> (r + c) == 2);
    Assert.assertEquals(3, grid.getSize());
    Assert.assertEquals(3, grid.countLiveCells());
    Assert.assertTrue(grid.isAlive(0, 2));
    Assert.assertTrue(grid.isAlive(1, 1));
    Assert.assertTrue(grid.isAlive(2, 0));
    Assert.assertFalse(grid.isAlive(0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullInitializer() {
    new Grid2D(3, (java.util.function.BiPredicate<Integer, Integer>) null);
  }

  @Test
  public void testBoundsChecking() {
    Grid2D grid = new Grid2D(3);
    Assert.assertTrue(grid.isInBounds(0, 0));
    Assert.assertTrue(grid.isInBounds(2, 2));
    Assert.assertFalse(grid.isInBounds(-1, 0));
    Assert.assertFalse(grid.isInBounds(0, -1));
    Assert.assertFalse(grid.isInBounds(3, 0));
    Assert.assertFalse(grid.isInBounds(0, 3));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testIsAliveNegativeRowThrows() {
    new Grid2D(3).isAlive(-1, 0);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testIsAliveNegativeColThrows() {
    new Grid2D(3).isAlive(0, -1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testIsAliveRowOutOfBoundsThrows() {
    new Grid2D(3).isAlive(3, 0);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testIsAliveColOutOfBoundsThrows() {
    new Grid2D(3).isAlive(0, 3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetCellOutOfBoundsThrows() {
    new Grid2D(3).getCell(3, 3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testCountLiveNeighborsOutOfBoundsThrows() {
    new Grid2D(3).countLiveNeighbors(-1, 1);
  }

  @Test
  public void testMooreNeighborhoodInteriorCountingAndCenterExclusion() {
    // 3x3 grid with all 8 outer cells alive and center (1, 1) dead
    boolean[][] matrixDeadCenter = {
      {true, true, true},
      {true, false, true},
      {true, true, true}
    };
    Grid2D gridDeadCenter = new Grid2D(3, matrixDeadCenter);
    Assert.assertEquals(8, gridDeadCenter.countLiveNeighbors(1, 1));

    // 3x3 grid with all 9 cells alive (center is alive)
    boolean[][] matrixAliveCenter = {
      {true, true, true},
      {true, true, true},
      {true, true, true}
    };
    Grid2D gridAliveCenter = new Grid2D(3, matrixAliveCenter);
    // Center state does not contribute to its own neighbor count
    Assert.assertEquals(8, gridAliveCenter.countLiveNeighbors(1, 1));
  }

  @Test
  public void testMooreNeighborhoodEdgesAndCornersNoWrapping() {
    // 3x3 all alive grid
    boolean[][] matrix = {
      {true, true, true},
      {true, true, true},
      {true, true, true}
    };
    Grid2D grid = new Grid2D(3, matrix);

    // Corners have exactly 3 valid neighbors (no toroidal wrapping)
    Assert.assertEquals(3, grid.countLiveNeighbors(0, 0));
    Assert.assertEquals(3, grid.countLiveNeighbors(0, 2));
    Assert.assertEquals(3, grid.countLiveNeighbors(2, 0));
    Assert.assertEquals(3, grid.countLiveNeighbors(2, 2));

    // Edges have exactly 5 valid neighbors
    Assert.assertEquals(5, grid.countLiveNeighbors(0, 1));
    Assert.assertEquals(5, grid.countLiveNeighbors(1, 0));
    Assert.assertEquals(5, grid.countLiveNeighbors(1, 2));
    Assert.assertEquals(5, grid.countLiveNeighbors(2, 1));
  }

  @Test
  public void testImmutabilityAndDefensiveCopying() {
    boolean[][] inputMatrix = {
      {true, false},
      {false, false}
    };
    Grid2D grid = new Grid2D(2, inputMatrix);

    // Mutating input array after construction has no effect
    inputMatrix[0][0] = false;
    inputMatrix[1][1] = true;
    Assert.assertTrue(grid.isAlive(0, 0));
    Assert.assertFalse(grid.isAlive(1, 1));
    Assert.assertEquals(1, grid.countLiveCells());

    // Mutating array returned from toArray() has no effect
    boolean[][] exportedArray = grid.toArray();
    exportedArray[0][0] = false;
    Assert.assertTrue(grid.isAlive(0, 0));
  }

  @Test
  public void testConwayBlinkerOscillator() {
    // 5x5 grid with horizontal blinker at (2, 1), (2, 2), (2, 3)
    boolean[][] blinkerH = new boolean[5][5];
    blinkerH[2][1] = true;
    blinkerH[2][2] = true;
    blinkerH[2][3] = true;
    Grid2D gen0 = new Grid2D(5, blinkerH);
    Assert.assertEquals(3, gen0.countLiveCells());

    LifeLikeRule conway = LifeLikePreset.CONWAYS_GAME_OF_LIFE.toRule();

    // Step 1: Blinker becomes vertical at (1, 2), (2, 2), (3, 2)
    Grid2D gen1 = gen0.nextGeneration(conway);
    Assert.assertEquals(3, gen1.countLiveCells());
    Assert.assertTrue(gen1.isAlive(1, 2));
    Assert.assertTrue(gen1.isAlive(2, 2));
    Assert.assertTrue(gen1.isAlive(3, 2));
    Assert.assertFalse(gen1.isAlive(2, 1));
    Assert.assertFalse(gen1.isAlive(2, 3));

    // Step 2: Blinker returns to horizontal
    Grid2D gen2 = gen1.nextGeneration(conway);
    Assert.assertEquals(gen0, gen2);
  }

  @Test
  public void testConwayBlockStillLife() {
    // 4x4 grid with 2x2 block still life at (1, 1), (1, 2), (2, 1), (2, 2)
    boolean[][] block = new boolean[4][4];
    block[1][1] = true;
    block[1][2] = true;
    block[2][1] = true;
    block[2][2] = true;
    Grid2D gen0 = new Grid2D(4, block);
    Assert.assertEquals(4, gen0.countLiveCells());

    LifeLikeRule conway = LifeLikePreset.CONWAYS_GAME_OF_LIFE.toRule();

    Grid2D gen1 = gen0.nextGeneration(conway);
    Assert.assertEquals(gen0, gen1);

    Grid2D gen2 = gen1.nextGeneration(conway);
    Assert.assertEquals(gen0, gen2);
  }

  @Test
  public void testSeedsRuleEvolution() {
    // Seeds rule (B2/S): live cells always die; dead cells with exactly 2 live neighbors are born
    boolean[][] matrix = new boolean[4][4];
    matrix[1][1] = true;
    matrix[1][2] = true;
    Grid2D gen0 = new Grid2D(4, matrix);

    Grid2D gen1 = gen0.nextGeneration(LifeLikePreset.SEEDS.toRule());
    // (1, 1) and (1, 2) had 1 neighbor each, and S is empty, so they die
    Assert.assertFalse(gen1.isAlive(1, 1));
    Assert.assertFalse(gen1.isAlive(1, 2));

    // Dead cells with 2 neighbors: (0, 1), (0, 2), (2, 1), (2, 2)
    Assert.assertTrue(gen1.isAlive(0, 1));
    Assert.assertTrue(gen1.isAlive(0, 2));
    Assert.assertTrue(gen1.isAlive(2, 1));
    Assert.assertTrue(gen1.isAlive(2, 2));
    Assert.assertEquals(4, gen1.countLiveCells());
  }

  @Test
  public void testB0RuleEvolution() {
    // Rule B0/S: dead grid evolves to completely alive grid in generation 1
    LifeLikeRule b0Rule = new LifeLikeRule(Set.of(0), Set.of());
    Grid2D deadGrid = Grid2D.empty(3);
    Assert.assertEquals(0, deadGrid.countLiveCells());

    Grid2D gen1 = deadGrid.nextGeneration(b0Rule);
    Assert.assertEquals(9, gen1.countLiveCells());
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        Assert.assertTrue(gen1.isAlive(r, c));
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNextGenerationRejectsNullRule() {
    Grid2D.empty(3).nextGeneration(null);
  }

  @Test
  public void testValueObjectContracts() {
    Grid2D grid1 = new Grid2D(3, (r, c) -> r == c);
    Grid2D grid2 = new Grid2D(3, (r, c) -> r == c);
    Grid2D gridDifferentPattern = new Grid2D(3, (r, c) -> r == 0);
    Grid2D gridDifferentSize = new Grid2D(4, (r, c) -> r == c);

    Assert.assertEquals(grid1, grid1);
    Assert.assertEquals(grid1, grid2);
    Assert.assertEquals(grid1.hashCode(), grid2.hashCode());
    Assert.assertNotEquals(grid1, gridDifferentPattern);
    Assert.assertNotEquals(grid1, gridDifferentSize);
    Assert.assertNotEquals(grid1, null);
    Assert.assertNotEquals(grid1, "NotAGrid");

    Assert.assertEquals("Grid2D[size=3, liveCells=3]", grid1.toString());
  }
}
