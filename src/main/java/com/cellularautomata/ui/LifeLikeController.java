package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.LifeLikeSimulation;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.image.AutomatonImageExport;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.Window;

/**
 * Controller coordinating Life-like cellular automata parameter capture, simulation lifecycle,
 * playback speed synchronization, image rendering, image saving, and toolbar/display state.
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

  /** Renderer converting a {@link Grid2D} instance to a resized {@link BufferedImage}. */
  @FunctionalInterface
  interface GridRenderer {

    /**
     * Renders a grid to a buffered image.
     *
     * @param grid the grid to render
     * @return rendered buffered image
     */
    BufferedImage render(Grid2D grid);
  }

  private final DisplayPanel displayPanel;
  private final LifeLikeParametersPanel parametersPanel;
  private final LifeLikeToolbar toolbar;
  private final LifeLikeStateMachine stateMachine;
  private final SimulationFactory simulationFactory;
  private final GridRenderer gridRenderer;
  private final SimulationScheduler scheduler;
  private final DialogService dialogService;

  private LifeLikeConfiguration activeConfiguration;
  private LifeLikeSimulation activeSimulation;
  private BufferedImage activeBufferedImage;
  private Image activeImage;
  private boolean tickInProgress;

  /**
   * Constructs the Life-like controller with default collaborators.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   */
  LifeLikeController(
      DisplayPanel displayPanel, LifeLikeParametersPanel parametersPanel, LifeLikeToolbar toolbar) {
    this(displayPanel, parametersPanel, toolbar, new DialogService());
  }

  /**
   * Constructs the Life-like controller with default simulation/rendering/scheduler collaborators
   * and an injected dialog service.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param dialogService dialog service for save dialogs and alerts
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      DialogService dialogService) {
    this(displayPanel, parametersPanel, toolbar, new TimelineScheduler(), dialogService);
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
    this(displayPanel, parametersPanel, toolbar, scheduler, new DialogService());
  }

  /**
   * Constructs the Life-like controller with default simulation and rendering collaborators and
   * injected scheduler and dialog service.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param scheduler periodic tick scheduler
   * @param dialogService dialog service for save dialogs and alerts
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      SimulationScheduler scheduler,
      DialogService dialogService) {
    this(
        displayPanel,
        parametersPanel,
        toolbar,
        new LifeLikeStateMachine(),
        AutomataEngine::createLifeLikeSimulation,
        LifeLikeController::defaultRenderGrid,
        scheduler,
        dialogService);
  }

  /**
   * Constructs the Life-like controller with injected simulation, rendering, and scheduling
   * collaborators and a default dialog service.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param stateMachine simulation lifecycle state machine
   * @param simulationFactory factory creating {@link LifeLikeSimulation} instances
   * @param gridRenderer renderer converting {@link Grid2D} to {@link BufferedImage}
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
    this(
        displayPanel,
        parametersPanel,
        toolbar,
        stateMachine,
        simulationFactory,
        gridRenderer,
        scheduler,
        new DialogService());
  }

  /**
   * Constructs the Life-like controller with fully injected collaborators.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param stateMachine simulation lifecycle state machine
   * @param simulationFactory factory creating {@link LifeLikeSimulation} instances
   * @param gridRenderer renderer converting {@link Grid2D} to {@link BufferedImage}
   * @param scheduler periodic tick scheduler
   * @param dialogService dialog service for save dialogs and alerts
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      LifeLikeStateMachine stateMachine,
      SimulationFactory simulationFactory,
      GridRenderer gridRenderer,
      SimulationScheduler scheduler,
      DialogService dialogService) {
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
    if (dialogService == null) {
      throw new IllegalArgumentException("DialogService cannot be null.");
    }
    this.displayPanel = displayPanel;
    this.parametersPanel = parametersPanel;
    this.toolbar = toolbar;
    this.stateMachine = stateMachine;
    this.simulationFactory = simulationFactory;
    this.gridRenderer = gridRenderer;
    this.scheduler = scheduler;
    this.dialogService = dialogService;

    scheduler.setPlaybackSpeed(parametersPanel.getPlaybackSpeed());
    parametersPanel.addPlaybackSpeedListener(scheduler::setPlaybackSpeed);

    toolbar.addPlayPauseListener(event -> togglePlayPause());
    toolbar.addStepListener(event -> step());
    toolbar.addResetListener(event -> reset());
    toolbar.addSaveListener(event -> saveAutomatonImage());

    updateToolbar();
  }

  private static BufferedImage defaultRenderGrid(Grid2D grid) {
    BufferedImage raw = AutomatonImage.getImageFromGrid(grid);
    return AutomatonImage.resizeImage(
        AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, raw);
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

    if (!renderAndDisplay(simulation.getCurrentGrid())) {
      return false;
    }

    activeSimulation = simulation;
    activeConfiguration = currentConfig;
    return true;
  }

  private boolean renderAndDisplay(Grid2D grid) {
    BufferedImage renderedBufferedImage;
    Image fxImage;
    try {
      renderedBufferedImage = gridRenderer.render(grid);
      if (renderedBufferedImage == null) {
        throw new IllegalStateException("Grid renderer returned null");
      }
      fxImage = SwingFXUtils.toFXImage(renderedBufferedImage, null);
    } catch (Exception e) {
      handleRenderingFailure(e);
      return false;
    }

    activeBufferedImage = renderedBufferedImage;
    activeImage = fxImage;
    displayPanel.setAutomatonImage(fxImage);
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
    scheduler.setPlaybackSpeed(parametersPanel.getPlaybackSpeed());
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

    if (!renderAndDisplay(activeSimulation.getCurrentGrid())) {
      return;
    }

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

    if (!renderAndDisplay(activeSimulation.getCurrentGrid())) {
      return;
    }

    stateMachine.onReset();
    updateToolbar();
  }

  /**
   * Opens the file save dialog and exports the currently displayed automaton image as a PNG file.
   * Ignored when the simulation is uninitialized or no rendered image is available.
   */
  public void save() {
    saveAutomatonImage();
  }

  void saveAutomatonImage() {
    if (stateMachine.isUninitialized() || activeSimulation == null || activeBufferedImage == null) {
      return;
    }

    BufferedImage imageToSave = activeBufferedImage;
    LifeLikeRule ruleToSave = activeSimulation.getRule();
    int sizeToSave = activeSimulation.getSize();
    String initialFileName =
        AutomatonFileNameFormatter.formatFileName(ruleToSave, sizeToSave, LocalDateTime.now());
    Window window = displayPanel.getScene() != null ? displayPanel.getScene().getWindow() : null;
    File file =
        dialogService.showSaveFileDialog(
            window, AutomatonImageExport.resolveInitialSaveDirectory(), initialFileName);
    if (file == null) {
      return;
    }

    try {
      AutomatonImage.saveImage(imageToSave, file);
    } catch (IOException ie) {
      System.err.printf("Automaton image save failure: %s%n", ie.getMessage());
      ie.printStackTrace(System.err);
      dialogService.showSaveError();
    }
  }

  void tick() {
    if (tickInProgress || !stateMachine.isRunning() || activeSimulation == null) {
      return;
    }

    tickInProgress = true;
    try {
      LifeLikeSimulation targetSimulation = activeSimulation;
      final Grid2D previousGrid = targetSimulation.getCurrentGrid();

      try {
        targetSimulation.step();
      } catch (Exception e) {
        handleGenerationFailure(e);
        return;
      }

      long expectedGeneration = targetSimulation.getGeneration();
      if (!stateMachine.isRunning()
          || activeSimulation != targetSimulation
          || targetSimulation.getGeneration() != expectedGeneration) {
        return;
      }

      Grid2D currentGrid = targetSimulation.getCurrentGrid();
      BufferedImage renderedBufferedImage;
      Image fxImage;
      try {
        renderedBufferedImage = gridRenderer.render(currentGrid);
        if (renderedBufferedImage == null) {
          throw new IllegalStateException("Grid renderer returned null");
        }
        fxImage = SwingFXUtils.toFXImage(renderedBufferedImage, null);
      } catch (Exception e) {
        handleRenderingFailure(e);
        return;
      }

      if (!stateMachine.isRunning()
          || activeSimulation != targetSimulation
          || targetSimulation.getGeneration() != expectedGeneration) {
        return;
      }

      activeBufferedImage = renderedBufferedImage;
      activeImage = fxImage;
      displayPanel.setAutomatonImage(fxImage);
      updateToolbar();

      if (currentGrid.equals(previousGrid)) {
        pause();
      }
    } finally {
      tickInProgress = false;
    }
  }

  private void handleGenerationFailure(Exception e) {
    scheduler.stop();
    activeSimulation = null;
    activeConfiguration = null;
    activeBufferedImage = null;
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
    activeBufferedImage = null;
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
        toolbar.setSaveEnabled(activeBufferedImage != null);
      }
      case PAUSED -> {
        toolbar.setPlaying(false);
        toolbar.setPlayPauseEnabled(true);
        toolbar.setStepEnabled(true);
        toolbar.setResetEnabled(true);
        toolbar.setSaveEnabled(activeBufferedImage != null);
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
    activeBufferedImage = null;
    activeImage = null;
    updateToolbar();
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

  BufferedImage getActiveBufferedImage() {
    return activeBufferedImage;
  }

  Image getActiveImage() {
    return activeImage;
  }

  boolean hasActiveSimulation() {
    return activeSimulation != null;
  }
}
