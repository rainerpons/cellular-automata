package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.LifeLikeSimulation;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Controller coordinating Life-like cellular automata parameter capture, simulation lifecycle,
 * image rendering, and toolbar/display state.
 */
public final class LifeLikeController {

  /** Factory for creating {@link LifeLikeSimulation} instances. */
  @FunctionalInterface
  interface SimulationFactory {

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
  interface GridRenderer {

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
  private final LifeLikeStateMachine stateMachine;
  private final SimulationFactory simulationFactory;
  private final GridRenderer gridRenderer;
  private final SimulationScheduler scheduler;

  private LifeLikeConfiguration activeConfiguration;
  private LifeLikeSimulation activeSimulation;
  private Image activeImage;

  /**
   * Constructs the Life-like controller with default collaborators.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   */
  LifeLikeController(
      DisplayPanel displayPanel, LifeLikeParametersPanel parametersPanel, LifeLikeToolbar toolbar) {
    this(displayPanel, parametersPanel, toolbar, new TimelineScheduler());
  }

  /**
   * Constructs the Life-like controller with default simulation and rendering collaborators and an
   * injected scheduler.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param scheduler periodic tick scheduler
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      SimulationScheduler scheduler) {
    this(
        displayPanel,
        parametersPanel,
        toolbar,
        new LifeLikeStateMachine(),
        AutomataEngine::createLifeLikeSimulation,
        LifeLikeController::defaultRenderGrid,
        scheduler);
  }

  /**
   * Constructs the Life-like controller with fully injected collaborators.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param stateMachine simulation lifecycle state machine
   * @param simulationFactory factory creating {@link LifeLikeSimulation} instances
   * @param gridRenderer renderer converting {@link Grid2D} to JavaFX {@link Image}
   * @param scheduler periodic tick scheduler
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      LifeLikeStateMachine stateMachine,
      SimulationFactory simulationFactory,
      GridRenderer gridRenderer,
      SimulationScheduler scheduler) {
    if (displayPanel == null) {
      throw new IllegalArgumentException("DisplayPanel cannot be null.");
    }
    if (parametersPanel == null) {
      throw new IllegalArgumentException("ParametersPanel cannot be null.");
    }
    if (toolbar == null) {
      throw new IllegalArgumentException("Toolbar cannot be null.");
    }
    if (stateMachine == null) {
      throw new IllegalArgumentException("StateMachine cannot be null.");
    }
    if (simulationFactory == null) {
      throw new IllegalArgumentException("SimulationFactory cannot be null.");
    }
    if (gridRenderer == null) {
      throw new IllegalArgumentException("GridRenderer cannot be null.");
    }
    if (scheduler == null) {
      throw new IllegalArgumentException("Scheduler cannot be null.");
    }
    this.displayPanel = displayPanel;
    this.parametersPanel = parametersPanel;
    this.toolbar = toolbar;
    this.stateMachine = stateMachine;
    this.simulationFactory = simulationFactory;
    this.gridRenderer = gridRenderer;
    this.scheduler = scheduler;

    toolbar.addPlayPauseListener(event -> togglePlayPause());
    toolbar.addStepListener(event -> step());
    toolbar.addResetListener(event -> reset());

    updateToolbar();
  }

  private static Image defaultRenderGrid(Grid2D grid) {
    BufferedImage raw = AutomatonImage.getImageFromGrid(grid);
    BufferedImage resized =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, raw);
    return SwingFXUtils.toFXImage(resized, null);
  }

  /**
   * Captures current form parameters as an immutable configuration model.
   *
   * @return the captured configuration snapshot
   */
  public LifeLikeConfiguration captureConfiguration() {
    return new LifeLikeConfiguration(
        parametersPanel.getSizeValue(),
        parametersPanel.getBirthCounts(),
        parametersPanel.getSurvivalCounts(),
        parametersPanel.getSeedType(),
        parametersPanel.getDensityValue());
  }

  private boolean prepareSimulation() {
    LifeLikeConfiguration currentConfig = captureConfiguration();
    boolean needsInit =
        stateMachine.isUninitialized()
            || activeSimulation == null
            || !currentConfig.equals(activeConfiguration);

    if (!needsInit) {
      return true;
    }

    LifeLikeSimulation simulation;
    try {
      LifeLikeRule rule =
          new LifeLikeRule(currentConfig.birthCounts(), currentConfig.survivalCounts());
      simulation =
          simulationFactory.create(
              rule, currentConfig.size(), currentConfig.seedType(), currentConfig.density());
      if (simulation == null) {
        throw new IllegalStateException("Simulation factory returned null");
      }
    } catch (Exception e) {
      handleGenerationFailure(e);
      return false;
    }

    Image renderedImage;
    try {
      renderedImage = gridRenderer.render(simulation.getCurrentGrid());
      if (renderedImage == null) {
        throw new IllegalStateException("Grid renderer returned null");
      }
    } catch (Exception e) {
      handleRenderingFailure(e);
      return false;
    }

    activeSimulation = simulation;
    activeConfiguration = currentConfig;
    activeImage = renderedImage;
    displayPanel.setAutomatonImage(renderedImage);
    return true;
  }

  /**
   * Starts or resumes the simulation. Initializes generation 0 if uninitialized or if parameters
   * have changed.
   */
  public void play() {
    if (stateMachine.isRunning()) {
      return;
    }
    if (!prepareSimulation()) {
      return;
    }
    stateMachine.onPlay();
    updateToolbar();
    scheduler.start(this::tick);
  }

  /** Pauses the simulation at the current generation. */
  public void pause() {
    if (!stateMachine.isRunning()) {
      return;
    }
    scheduler.stop();
    stateMachine.onPause();
    updateToolbar();
  }

  /** Toggles simulation between running and paused. */
  public void togglePlayPause() {
    if (stateMachine.isRunning()) {
      pause();
    } else {
      play();
    }
  }

  /**
   * Advances the simulation by exactly one generation. When uninitialized or if settings have
   * changed, initializes generation 0 first before advancing. Ignored while running.
   */
  public void step() {
    if (stateMachine.isRunning()) {
      return;
    }
    if (!prepareSimulation()) {
      return;
    }

    try {
      activeSimulation.step();
    } catch (Exception e) {
      handleGenerationFailure(e);
      return;
    }

    Image renderedImage;
    try {
      renderedImage = gridRenderer.render(activeSimulation.getCurrentGrid());
      if (renderedImage == null) {
        throw new IllegalStateException("Grid renderer returned null");
      }
    } catch (Exception e) {
      handleRenderingFailure(e);
      return;
    }

    activeImage = renderedImage;
    displayPanel.setAutomatonImage(renderedImage);
    stateMachine.onStep();
    updateToolbar();
  }

  /**
   * Resets the active simulation to generation 0 and re-renders the initial grid. Ignored when
   * uninitialized.
   */
  public void reset() {
    if (stateMachine.isUninitialized() || activeSimulation == null) {
      return;
    }
    scheduler.stop();
    activeSimulation.reset();

    Image renderedImage;
    try {
      renderedImage = gridRenderer.render(activeSimulation.getCurrentGrid());
      if (renderedImage == null) {
        throw new IllegalStateException("Grid renderer returned null");
      }
    } catch (Exception e) {
      handleRenderingFailure(e);
      return;
    }

    activeImage = renderedImage;
    displayPanel.setAutomatonImage(renderedImage);
    stateMachine.onReset();
    updateToolbar();
  }

  void tick() {
    if (!stateMachine.isRunning() || activeSimulation == null) {
      return;
    }

    try {
      activeSimulation.step();
    } catch (Exception e) {
      handleGenerationFailure(e);
      return;
    }

    Image renderedImage;
    try {
      renderedImage = gridRenderer.render(activeSimulation.getCurrentGrid());
      if (renderedImage == null) {
        throw new IllegalStateException("Grid renderer returned null");
      }
    } catch (Exception e) {
      handleRenderingFailure(e);
      return;
    }

    activeImage = renderedImage;
    displayPanel.setAutomatonImage(renderedImage);
  }

  private void handleGenerationFailure(Exception e) {
    scheduler.stop();
    activeSimulation = null;
    activeConfiguration = null;
    activeImage = null;
    if (!stateMachine.isUninitialized()) {
      stateMachine.onError();
    }
    updateToolbar();
    System.err.printf("Automaton generation failure: %s%n", e.getMessage());
    e.printStackTrace(System.err);
    displayPanel.showGenerationError();
  }

  private void handleRenderingFailure(Exception e) {
    scheduler.stop();
    activeSimulation = null;
    activeConfiguration = null;
    activeImage = null;
    if (!stateMachine.isUninitialized()) {
      stateMachine.onError();
    }
    updateToolbar();
    System.err.printf("Automaton rendering failure: %s%n", e.getMessage());
    e.printStackTrace(System.err);
    displayPanel.showRenderingError();
  }

  private void updateToolbar() {
    switch (stateMachine.getState()) {
      case UNINITIALIZED -> {
        toolbar.setPlaying(false);
        toolbar.setPlayPauseEnabled(true);
        toolbar.setStepEnabled(true);
        toolbar.setResetEnabled(false);
        toolbar.setSaveEnabled(false);
      }
      case RUNNING -> {
        toolbar.setPlaying(true);
        toolbar.setPlayPauseEnabled(true);
        toolbar.setStepEnabled(false);
        toolbar.setResetEnabled(true);
        toolbar.setSaveEnabled(false);
      }
      case PAUSED -> {
        toolbar.setPlaying(false);
        toolbar.setPlayPauseEnabled(true);
        toolbar.setStepEnabled(true);
        toolbar.setResetEnabled(true);
        toolbar.setSaveEnabled(false);
      }
      default ->
          throw new IllegalStateException(
              "Unhandled simulation state: %s".formatted(stateMachine.getState()));
    }
  }

  /**
   * Releases resources by stopping the scheduler, transitioning the state machine to stopped, and
   * clearing active simulation references.
   */
  public void dispose() {
    scheduler.stop();
    if (!stateMachine.isUninitialized()) {
      stateMachine.onStop();
    }
    activeSimulation = null;
    activeConfiguration = null;
    activeImage = null;
  }

  LifeLikeStateMachine.State getState() {
    return stateMachine.getState();
  }

  boolean isRunning() {
    return stateMachine.isRunning();
  }

  LifeLikeSimulation getActiveSimulation() {
    return activeSimulation;
  }

  LifeLikeConfiguration getActiveConfiguration() {
    return activeConfiguration;
  }

  Image getActiveImage() {
    return activeImage;
  }

  boolean hasActiveSimulation() {
    return activeSimulation != null;
  }
}
