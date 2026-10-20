package com.cellularautomata.ui;

import com.cellularautomata.engine.SeedType;
import java.util.Set;

/**
 * Immutable configuration parameters for a Life-like cellular automaton simulation.
 *
 * <p>This model is headless and free of JavaFX dependencies.
 *
 * @param size side length of the square grid
 * @param birthCounts live neighbor counts causing dead cells to become alive
 * @param survivalCounts live neighbor counts causing live cells to stay alive
 * @param seedType initial cell lattice seed type
 * @param density initial alive cell density percentage (0-100), effective only for {@link
 *     SeedType#DENSITY}
 */
public record LifeLikeConfiguration(
    int size,
    Set<Integer> birthCounts,
    Set<Integer> survivalCounts,
    SeedType seedType,
    int density) {

  /**
   * Constructs a configuration with defensive copies of birth and survival count sets.
   *
   * @param size side length of the square grid
   * @param birthCounts live neighbor counts causing dead cells to become alive
   * @param survivalCounts live neighbor counts causing live cells to stay alive
   * @param seedType initial cell lattice seed type
   * @param density initial alive cell density percentage (0-100)
   * @throws IllegalArgumentException if seedType is null
   */
  public LifeLikeConfiguration {
    if (seedType == null) {
      throw new IllegalArgumentException("Seed type cannot be null.");
    }
    birthCounts = birthCounts != null ? Set.copyOf(birthCounts) : Set.of();
    survivalCounts = survivalCounts != null ? Set.copyOf(survivalCounts) : Set.of();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LifeLikeConfiguration other)) {
      return false;
    }
    if (size != other.size) {
      return false;
    }
    if (seedType != other.seedType) {
      return false;
    }
    if (!birthCounts.equals(other.birthCounts)) {
      return false;
    }
    if (!survivalCounts.equals(other.survivalCounts)) {
      return false;
    }
    if (seedType == SeedType.DENSITY && density != other.density) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(size);
    result = 31 * result + birthCounts.hashCode();
    result = 31 * result + survivalCounts.hashCode();
    result = 31 * result + seedType.hashCode();
    if (seedType == SeedType.DENSITY) {
      result = 31 * result + Integer.hashCode(density);
    }
    return result;
  }
}
