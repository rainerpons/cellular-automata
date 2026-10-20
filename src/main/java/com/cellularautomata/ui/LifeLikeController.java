package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.LifeLikeSimulation;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Controller coordinating Life-like cellular automata parameter capture, simulation initialization,
 * image rendering, and display error state.
 */
public final class LifeLikeController {

  /** Factory for creating {@link LifeLikeSimulation} instances. */
  @FunctionalInterface
  public interface SimulationFactory {

    /**
     * Creates a simulation instance.
     *
     * @param rule the Life-like rule
     * @param size side length of the square grid
     * @param seedType initial cell lattice seed type
     * @param densityPercentage live cell density percentage
     * @return initialized simulation
     */
    LifeLikeSimulation create(
        LifeLikeRule rule, int size, SeedType seedType, double densityPercentage);
  }

  /** Renderer converting a {@link Grid2D} instance to a JavaFX {@link Image}. */
  @FunctionalInterface
  public interface GridRenderer {

    /**
     * Renders a grid to an image.
     *
     * @param grid the grid to render
     * @return rendered JavaFX image
     */
    Image render(Grid2D grid);
  }

  private final DisplayPanel displayPanel;
  private final LifeLikeParametersPanel parametersPanel;
  private final LifeLikeToolbar toolbar;

  private SimulationFactory simulationFactory;
  private GridRenderer gridRenderer;

  private LifeLikeConfiguration activeConfiguration;
  private LifeLikeSimulation activeSimulation;
  private Image activeImage;

  /**
   * Constructs the Life-like controller with default engine simulation creation and image
   * rendering.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   */
  public LifeLikeController(
      DisplayPanel displayPanel, LifeLikeParametersPanel parametersPanel, LifeLikeToolbar toolbar) {
    this(
        displayPanel,
        parametersPanel,
        toolbar,
        AutomataEngine::createLifeLikeSimulation,
        LifeLikeController::defaultRenderGrid);
  }

  /**
   * Package-private constructor providing injection seams for simulation creation and image
   * rendering.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param simulationFactory factory creating {@link LifeLikeSimulation} instances
   * @param gridRenderer renderer converting {@link Grid2D} to JavaFX {@link Image}
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      SimulationFactory simulationFactory,
      GridRenderer gridRenderer) {
    if (displayPanel == null) {
      throw new IllegalArgumentException("DisplayPanel cannot be null.");
    }
    if (parametersPanel == null) {
      throw new IllegalArgumentException("ParametersPanel cannot be null.");
    }
    if (toolbar == null) {
      throw new IllegalArgumentException("Toolbar cannot be null.");
    }
    this.displayPanel = displayPanel;
    this.parametersPanel = parametersPanel;
    this.toolbar = toolbar;
    this.simulationFactory =
        simulationFactory != null ? simulationFactory : AutomataEngine::createLifeLikeSimulation;
    this.gridRenderer = gridRenderer != null ? gridRenderer : LifeLikeController::defaultRenderGrid;
    this.displayPanel.getProperties().put(LifeLikeController.class, this);
  }

  private static Image defaultRenderGrid(Grid2D grid) {
    BufferedImage raw = AutomatonImage.getImageFromGrid(grid);
    BufferedImage resized =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, raw);
    return SwingFXUtils.toFXImage(resized, null);
  }

  /** Captures current form parameters as an immutable configuration model. */
  public LifeLikeConfiguration captureConfiguration() {
    return new LifeLikeConfiguration(
        parametersPanel.getSizeValue(),
        parametersPanel.getBirthCounts(),
        parametersPanel.getSurvivalCounts(),
        parametersPanel.getSeedType(),
        parametersPanel.getDensityValue());
  }

  /** Initializes the simulation, retaining any existing simulation if parameters are unchanged. */
  public void initializeSimulation() {
    initializeSimulation(false);
  }

  /**
   * Initializes the simulation, optionally forcing reinitialization with a fresh seed even if
   * parameters have not changed.
   *
   * @param forceReinitialize true to force a fresh seed initialization
   */
  public void initializeSimulation(boolean forceReinitialize) {
    LifeLikeConfiguration currentConfig = captureConfiguration();
    if (!forceReinitialize
        && activeSimulation != null
        && currentConfig.equals(activeConfiguration)) {
      return;
    }

    LifeLikeRule rule;
    LifeLikeSimulation simulation;
    try {
      rule = new LifeLikeRule(currentConfig.birthCounts(), currentConfig.survivalCounts());
      simulation =
          simulationFactory.create(
              rule, currentConfig.size(), currentConfig.seedType(), currentConfig.density());
      if (simulation == null) {
        throw new IllegalStateException("Simulation factory returned null");
      }
    } catch (Exception e) {
      handleGenerationFailure();
      return;
    }

    Image renderedImage;
    try {
      renderedImage = gridRenderer.render(simulation.getCurrentGrid());
      if (renderedImage == null) {
        throw new IllegalStateException("Grid renderer returned null");
      }
    } catch (Exception e) {
      handleRenderingFailure();
      return;
    }

    activeSimulation = simulation;
    activeConfiguration = currentConfig;
    activeImage = renderedImage;

    displayPanel.setAutomatonImage(renderedImage);
    toolbar.setSaveEnabled(true);
    toolbar.setResetEnabled(true);
  }

  /** Generates the initial simulation state. */
  public void generateInitial() {
    initializeSimulation();
  }

  /** Reinitializes the simulation with a fresh seed. */
  public void reinitializeSimulation() {
    initializeSimulation(true);
  }

  /** Resets the active simulation to generation 0 and re-renders the initial grid. */
  public void resetSimulation() {
    if (activeSimulation == null) {
      return;
    }
    activeSimulation.reset();
    try {
      Image rendered = gridRenderer.render(activeSimulation.getCurrentGrid());
      if (rendered == null) {
        throw new IllegalStateException("Grid renderer returned null");
      }
      activeImage = rendered;
      displayPanel.setAutomatonImage(rendered);
      toolbar.setSaveEnabled(true);
      toolbar.setResetEnabled(true);
    } catch (Exception e) {
      handleRenderingFailure();
    }
  }

  private void handleGenerationFailure() {
    activeSimulation = null;
    activeConfiguration = null;
    activeImage = null;
    toolbar.setSaveEnabled(false);
    toolbar.setResetEnabled(false);
    displayPanel.showGenerationError();
  }

  private void handleRenderingFailure() {
    activeSimulation = null;
    activeConfiguration = null;
    activeImage = null;
    toolbar.setSaveEnabled(false);
    toolbar.setResetEnabled(false);
    displayPanel.showRenderingError();
  }

  void setSimulationFactory(SimulationFactory simulationFactory) {
    this.simulationFactory =
        simulationFactory != null ? simulationFactory : AutomataEngine::createLifeLikeSimulation;
  }

  void setGridRenderer(GridRenderer gridRenderer) {
    this.gridRenderer = gridRenderer != null ? gridRenderer : LifeLikeController::defaultRenderGrid;
  }

  public LifeLikeSimulation getActiveSimulation() {
    return activeSimulation;
  }

  public LifeLikeConfiguration getActiveConfiguration() {
    return activeConfiguration;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Rendered JavaFX image is intentionally exposed for view inspection")
  public Image getActiveImage() {
    return activeImage;
  }

  public boolean hasActiveSimulation() {
    return activeSimulation != null;
  }

  DisplayPanel getDisplayPanel() {
    return displayPanel;
  }

  LifeLikeParametersPanel getParametersPanel() {
    return parametersPanel;
  }

  LifeLikeToolbar getToolbar() {
    return toolbar;
  }
}
