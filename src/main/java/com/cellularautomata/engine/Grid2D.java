package com.cellularautomata.engine;

import com.cellularautomata.engine.rules.LifeLikeRule;
import java.util.Arrays;
import java.util.function.BiPredicate;

/**
 * Immutable two-dimensional finite square grid of binary cell states (dead or alive) for Life-like
 * cellular automata simulations.
 */
public final class Grid2D {
  private final int size;
  private final boolean[] cells;
  private final int liveCellCount;

  /**
   * Constructs an empty (all dead) square grid of the specified size.
   *
   * @param size side length of the square grid (N &gt;= 1)
   * @throws IllegalArgumentException if size is less than 1 or causes integer overflow
   */
  public Grid2D(int size) {
    int totalCells = validateSize(size);
    this.size = size;
    this.cells = new boolean[totalCells];
    this.liveCellCount = 0;
  }

  /**
   * Constructs a square grid from a 2D boolean array.
   *
   * @param size side length of the square grid (N &gt;= 1)
   * @param matrix 2D boolean array representing cell states (row-major)
   * @throws IllegalArgumentException if size &lt; 1, size causes integer overflow, matrix is null,
   *     or dimensions do not match size
   */
  public Grid2D(int size, boolean[][] matrix) {
    if (matrix == null) {
      throw new IllegalArgumentException("Matrix cannot be null.");
    }
    if (matrix.length != size) {
      throw new IllegalArgumentException(
          "Matrix row count %d does not match size %d".formatted(matrix.length, size));
    }
    int totalCells = validateSize(size);
    this.size = size;
    this.cells = new boolean[totalCells];
    int count = 0;
    for (int r = 0; r < size; r++) {
      if (matrix[r] == null || matrix[r].length != size) {
        throw new IllegalArgumentException(
            "Matrix column count at row %d does not match size %d".formatted(r, size));
      }
      int rowOffset = r * size;
      for (int c = 0; c < size; c++) {
        boolean val = matrix[r][c];
        this.cells[rowOffset + c] = val;
        if (val) {
          count++;
        }
      }
    }
    this.liveCellCount = count;
  }

  /**
   * Constructs a square grid initialized by a coordinate predicate.
   *
   * @param size side length of the square grid (N &gt;= 1)
   * @param cellInitializer predicate taking (row, col) returning true if cell is alive
   * @throws IllegalArgumentException if size &lt; 1, size causes integer overflow, or
   *     cellInitializer is null
   */
  public Grid2D(int size, BiPredicate<Integer, Integer> cellInitializer) {
    int totalCells = validateSize(size);
    if (cellInitializer == null) {
      throw new IllegalArgumentException("Cell initializer cannot be null.");
    }
    this.size = size;
    this.cells = new boolean[totalCells];
    int count = 0;
    for (int r = 0; r < size; r++) {
      int rowOffset = r * size;
      for (int c = 0; c < size; c++) {
        boolean val = cellInitializer.test(r, c);
        this.cells[rowOffset + c] = val;
        if (val) {
          count++;
        }
      }
    }
    this.liveCellCount = count;
  }

  private Grid2D(int size, boolean[] cells, int liveCellCount) {
    this.size = size;
    this.cells = cells;
    this.liveCellCount = liveCellCount;
  }

  private static int validateSize(int size) {
    if (size < 1) {
      throw new IllegalArgumentException("Grid size must be at least 1: %d".formatted(size));
    }
    try {
      return Math.multiplyExact(size, size);
    } catch (ArithmeticException e) {
      throw new IllegalArgumentException(
          "Grid size causes integer overflow: %d".formatted(size), e);
    }
  }

  /**
   * Creates an empty (all dead) square grid of the specified size.
   *
   * @param size side length of the square grid
   * @return empty Grid2D instance
   */
  public static Grid2D empty(int size) {
    return new Grid2D(size);
  }

  public int getSize() {
    return size;
  }

  public boolean isInBounds(int row, int col) {
    return row >= 0 && row < size && col >= 0 && col < size;
  }

  private void validateCoordinates(int row, int col) {
    if (!isInBounds(row, col)) {
      throw new IndexOutOfBoundsException(
          "Coordinates out of bounds: (%d, %d) for size %d".formatted(row, col, size));
    }
  }

  public boolean isAlive(int row, int col) {
    validateCoordinates(row, col);
    return cells[row * size + col];
  }

  public int getCell(int row, int col) {
    validateCoordinates(row, col);
    return cells[row * size + col] ? 1 : 0;
  }

  /**
   * Counts the number of living cells in the 8-cell Moore neighborhood (radius 1) around (row,
   * col).
   *
   * @param row center row
   * @param col center col
   * @return live neighbor count in [0, 8]
   * @throws IndexOutOfBoundsException if (row, col) is outside the grid bounds
   */
  public int countLiveNeighbors(int row, int col) {
    validateCoordinates(row, col);
    int count = 0;
    for (int dr = -1; dr <= 1; dr++) {
      int nr = row + dr;
      if (nr < 0 || nr >= size) {
        continue;
      }
      int rowOffset = nr * size;
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int nc = col + dc;
        if (nc >= 0 && nc < size && cells[rowOffset + nc]) {
          count++;
        }
      }
    }
    return count;
  }

  /**
   * Returns the total count of living cells in the grid.
   *
   * @return total number of live cells
   */
  public int countLiveCells() {
    return liveCellCount;
  }

  /**
   * Returns a copy of the grid cell states as a 2D boolean array.
   *
   * @return 2D boolean array copy of cell states
   */
  public boolean[][] toArray() {
    boolean[][] copy = new boolean[size][size];
    for (int r = 0; r < size; r++) {
      int rowOffset = r * size;
      for (int c = 0; c < size; c++) {
        copy[r][c] = cells[rowOffset + c];
      }
    }
    return copy;
  }

  /**
   * Computes the next generation grid according to the provided Life-like rule.
   *
   * @param rule the Life-like rule to evaluate
   * @return new Grid2D for the next generation
   * @throws IllegalArgumentException if rule is null
   */
  public Grid2D nextGeneration(LifeLikeRule rule) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    boolean[] nextCells = new boolean[cells.length];
    int nextLiveCount = 0;
    for (int r = 0; r < size; r++) {
      int rowOffset = r * size;
      for (int c = 0; c < size; c++) {
        int idx = rowOffset + c;
        boolean current = cells[idx];
        int liveNeighbors = countLiveNeighbors(r, c);
        boolean next = rule.evaluate(current, liveNeighbors);
        nextCells[idx] = next;
        if (next) {
          nextLiveCount++;
        }
      }
    }
    return new Grid2D(size, nextCells, nextLiveCount);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Grid2D other)) {
      return false;
    }
    return size == other.size && Arrays.equals(cells, other.cells);
  }

  @Override
  public int hashCode() {
    return 31 * size + Arrays.hashCode(cells);
  }

  @Override
  public String toString() {
    return "Grid2D[size=" + size + ", liveCells=" + liveCellCount + "]";
  }
}
