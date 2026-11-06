package com.cellularautomata.ui.controllers;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.automata.AutomataEngine;
import com.cellularautomata.engine.automata.LifeLikeSimulation;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.image.AutomatonImageUtils;
import com.cellularautomata.ui.AutomatonFileNameFormatter;
import com.cellularautomata.ui.components.LifeLikeToolbar;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.simulation.LifeLikeConfiguration;
import com.cellularautomata.ui.simulation.LifeLikeStateMachine;
import com.cellularautomata.ui.simulation.SimulationScheduler;
import com.cellularautomata.ui.simulation.TimelineScheduler;
import java.awt.image.BufferedImage;
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

  private record RenderedFrame(BufferedImage bufferedImage, Image fxImage) {}

  private final DisplayPanel displayPanel;
  private final LifeLikeParametersPanel parametersPanel;
  private final LifeLikeToolbar toolbar;
  private final LifeLikeStateMachine stateMachine;
  private final SimulationFactory simulationFactory;
  private final GridRenderer gridRenderer;
  private final SimulationScheduler scheduler;
  private final AutomatonImageService imageService;

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
  public LifeLikeController(
      DisplayPanel displayPanel, LifeLikeParametersPanel parametersPanel, LifeLikeToolbar toolbar) {
    this(displayPanel, parametersPanel, toolbar, new AutomatonImageService());
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
  public LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      DialogService dialogService) {
    this(displayPanel, parametersPanel, toolbar, new AutomatonImageService(dialogService));
  }

  /**
   * Constructs the Life-like controller with default simulation/rendering/scheduler collaborators
   * and an injected image service.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param imageService automaton image service for saving images
   */
  public LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      AutomatonImageService imageService) {
    this(displayPanel, parametersPanel, toolbar, new TimelineScheduler(), imageService);
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
    this(displayPanel, parametersPanel, toolbar, scheduler, new AutomatonImageService());
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
        scheduler,
        new AutomatonImageService(dialogService));
  }

  /**
   * Constructs the Life-like controller with default simulation and rendering collaborators and
   * injected scheduler and image service.
   *
   * @param displayPanel the display panel
   * @param parametersPanel the parameters panel
   * @param toolbar the simulation toolbar
   * @param scheduler periodic tick scheduler
   * @param imageService automaton image service for saving images
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      SimulationScheduler scheduler,
      AutomatonImageService imageService) {
    this(
        displayPanel,
        parametersPanel,
        toolbar,
        new LifeLikeStateMachine(),
        AutomataEngine::createLifeLikeSimulation,
        LifeLikeController::defaultRenderGrid,
        scheduler,
        imageService);
  }

  /**
   * Constructs the Life-like controller with injected simulation, rendering, and scheduling
   * collaborators and a default image service.
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
        new AutomatonImageService());
  }

  /**
   * Constructs the Life-like controller with injected collaborators and dialog service.
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
    this(
        displayPanel,
        parametersPanel,
        toolbar,
        stateMachine,
        simulationFactory,
        gridRenderer,
        scheduler,
        new AutomatonImageService(dialogService));
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
   * @param imageService automaton image service for saving images
   */
  LifeLikeController(
      DisplayPanel displayPanel,
      LifeLikeParametersPanel parametersPanel,
      LifeLikeToolbar toolbar,
      LifeLikeStateMachine stateMachine,
      SimulationFactory simulationFactory,
      GridRenderer gridRenderer,
      SimulationScheduler scheduler,
      AutomatonImageService imageService) {
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
    if (imageService == null) {
      throw new IllegalArgumentException("AutomatonImageService cannot be null.");
    }
    this.displayPanel = displayPanel;
    this.parametersPanel = parametersPanel;
    this.toolbar = toolbar;
    this.stateMachine = stateMachine;
    this.simulationFactory = simulationFactory;
    this.gridRenderer = gridRenderer;
    this.scheduler = scheduler;
    this.imageService = imageService;

    scheduler.setPlaybackSpeed(parametersPanel.getPlaybackSpeed());
    parametersPanel.addPlaybackSpeedListener(scheduler::setPlaybackSpeed);

    toolbar.addPlayPauseListener(event -> togglePlayPause());
    toolbar.addStepListener(event -> step());
    toolbar.addResetListener(event -> reset());
    toolbar.addSaveListener(event -> saveAutomatonImage());

    updateToolbar();
  }

  private static BufferedImage defaultRenderGrid(Grid2D grid) {
    BufferedImage raw = AutomatonImageUtils.getImageFromGrid(grid);
    return AutomatonImageUtils.resizeImage(
        AutomatonImageUtils.DISPLAY_IMAGE_SIZE, AutomatonImageUtils.DISPLAY_IMAGE_SIZE, raw);
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

    LifeLikeSimulation simulation = createSimulation(currentConfig);
    if (simulation == null) {
      return false;
    }

    if (!renderAndDisplay(simulation.getCurrentGrid())) {
      return false;
    }

    activeSimulation = simulation;
    activeConfiguration = currentConfig;
    return true;
  }

  private LifeLikeSimulation createSimulation(LifeLikeConfiguration config) {
    try {
      LifeLikeRule rule = new LifeLikeRule(config.birthCounts(), config.survivalCounts());
      LifeLikeSimulation simulation =
          simulationFactory.create(rule, config.size(), config.seedType(), config.density());
      if (simulation == null) {
        throw new IllegalStateException("Simulation factory returned null.");
      }
      return simulation;
    } catch (RuntimeException e) {
      handleGenerationError(e);
      return null;
    }
  }

  private RenderedFrame renderFrame(Grid2D grid) {
    try {
      BufferedImage renderedBufferedImage = gridRenderer.render(grid);
      if (renderedBufferedImage == null) {
        throw new IllegalStateException("Grid renderer returned null.");
      }
      Image fxImage = SwingFXUtils.toFXImage(renderedBufferedImage, null);
      return new RenderedFrame(renderedBufferedImage, fxImage);
    } catch (RuntimeException e) {
      handleRenderingError(e);
      return null;
    }
  }

  private void applyRenderedFrame(RenderedFrame frame) {
    activeBufferedImage = frame.bufferedImage();
    activeImage = frame.fxImage();
    displayPanel.setAutomatonImage(frame.fxImage());
  }

  private boolean renderAndDisplay(Grid2D grid) {
    RenderedFrame frame = renderFrame(grid);
    if (frame == null) {
      return false;
    }
    applyRenderedFrame(frame);
    return true;
  }

  private boolean advanceGeneration(LifeLikeSimulation simulation) {
    try {
      simulation.step();
      return true;
    } catch (RuntimeException e) {
      handleGenerationError(e);
      return false;
    }
  }

  private boolean resetSimulation(LifeLikeSimulation simulation) {
    try {
      simulation.reset();
      return true;
    } catch (RuntimeException e) {
      handleGenerationError(e);
      return false;
    }
  }

  void play() {
    if (stateMachine.isRunning() || stateMachine.isFixedPoint()) {
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

  void pause() {
    if (!stateMachine.isRunning()) {
      return;
    }
    scheduler.stop();
    stateMachine.onPause();
    updateToolbar();
  }

  void togglePlayPause() {
    if (stateMachine.isFixedPoint()) {
      return;
    }
    if (stateMachine.isRunning()) {
      pause();
      return;
    }
    play();
  }

  void step() {
    if (stateMachine.isRunning() || stateMachine.isFixedPoint()) {
      return;
    }
    if (!prepareSimulation()) {
      return;
    }
    final Grid2D previousGrid = activeSimulation.getCurrentGrid();
    if (!advanceGeneration(activeSimulation)) {
      return;
    }
    Grid2D currentGrid = activeSimulation.getCurrentGrid();
    if (!renderAndDisplay(currentGrid)) {
      return;
    }
    stateMachine.onStep();
    if (currentGrid.equals(previousGrid)) {
      reachFixedPoint();
      return;
    }
    updateToolbar();
  }

  void reset() {
    if (stateMachine.isUninitialized() || activeSimulation == null) {
      return;
    }
    scheduler.stop();
    if (!resetSimulation(activeSimulation)) {
      return;
    }
    if (!renderAndDisplay(activeSimulation.getCurrentGrid())) {
      return;
    }
    stateMachine.onReset();
    updateToolbar();
  }

  void save() {
    saveAutomatonImage();
  }

  void saveAutomatonImage() {
    if (stateMachine.isUninitialized() || activeSimulation == null || activeBufferedImage == null) {
      return;
    }

    BufferedImage imageToSave = activeBufferedImage;
    LifeLikeRule ruleToSave = activeSimulation.getRule();
    int sizeToSave = activeSimulation.getSize();
    boolean wasRunning = stateMachine.isRunning();
    if (wasRunning) {
      pause();
    }

    String initialFileName = AutomatonFileNameFormatter.formatFileName(ruleToSave, sizeToSave);
    Window window = displayPanel.getScene() != null ? displayPanel.getScene().getWindow() : null;
    imageService.saveImage(window, imageToSave, initialFileName);

    if (wasRunning) {
      resumeAfterSave();
    }
  }

  private void resumeAfterSave() {
    if (!stateMachine.isPaused() || activeSimulation == null) {
      return;
    }
    stateMachine.onPlay();
    updateToolbar();
    scheduler.setPlaybackSpeed(parametersPanel.getPlaybackSpeed());
    scheduler.start(this::tick);
  }

  void tick() {
    if (tickInProgress) {
      return;
    }
    tickInProgress = true;
    try {
      executeTick();
    } finally {
      tickInProgress = false;
    }
  }

  private void executeTick() {
    if (!stateMachine.isRunning() || activeSimulation == null) {
      return;
    }

    LifeLikeSimulation targetSimulation = activeSimulation;
    final Grid2D previousGrid = targetSimulation.getCurrentGrid();
    long expectedGeneration = targetSimulation.getGeneration() + 1;

    if (!advanceGeneration(targetSimulation)) {
      return;
    }

    if (!isTickValid(targetSimulation, expectedGeneration)) {
      return;
    }

    Grid2D currentGrid = targetSimulation.getCurrentGrid();
    if (currentGrid.equals(previousGrid)) {
      // Skip rendering because the displayed image already represents the unchanged grid.
      reachFixedPoint();
      return;
    }

    RenderedFrame frame = renderFrame(currentGrid);
    if (frame == null || !isTickValid(targetSimulation, expectedGeneration)) {
      return;
    }

    applyRenderedFrame(frame);
    updateToolbar();
  }

  private boolean isTickValid(LifeLikeSimulation targetSimulation, long expectedGeneration) {
    return stateMachine.isRunning()
        && activeSimulation == targetSimulation
        && targetSimulation.getGeneration() == expectedGeneration;
  }

  private void reachFixedPoint() {
    scheduler.stop();
    stateMachine.onFixedPoint();
    updateToolbar();
  }

  private void handleGenerationError(RuntimeException e) {
    resetOnFailure("Automaton generation failure", e);
    displayPanel.showGenerationError();
  }

  private void handleRenderingError(RuntimeException e) {
    resetOnFailure("Automaton rendering failure", e);
    displayPanel.showRenderingError();
  }

  private void resetOnFailure(String logPrefix, RuntimeException e) {
    scheduler.stop();
    activeSimulation = null;
    activeConfiguration = null;
    activeBufferedImage = null;
    activeImage = null;
    if (!stateMachine.isUninitialized()) {
      stateMachine.onError();
    }
    updateToolbar();
    System.err.printf("%s: %s%n", logPrefix, e.getMessage());
    e.printStackTrace(System.err);
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
      case FIXED_POINT -> {
        toolbar.setPlaying(false);
        toolbar.setPlayPauseEnabled(false);
        toolbar.setStepEnabled(false);
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
