package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.automata.LifeLikeSimulation;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import java.util.Set;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/** Integration tests for {@link LifeLikeController}. */
public class LifeLikeControllerTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testConstructorRejectsNullArguments() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel display = new DisplayPanel();
          LifeLikeParametersPanel params = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          LifeLikeStateMachine sm = new LifeLikeStateMachine();
          LifeLikeController.SimulationFactory factory = (r, s, st, d) -> null;
          LifeLikeController.GridRenderer renderer = g -> null;
          SimulationScheduler scheduler = new ControlledScheduler();

          assertThrowsIllegalArgument(() -> new LifeLikeController(null, params, toolbar));
          assertThrowsIllegalArgument(() -> new LifeLikeController(display, null, toolbar));
          assertThrowsIllegalArgument(() -> new LifeLikeController(display, params, null));

          assertThrowsIllegalArgument(
              () ->
                  new LifeLikeController(null, params, toolbar, sm, factory, renderer, scheduler));
          assertThrowsIllegalArgument(
              () ->
                  new LifeLikeController(display, null, toolbar, sm, factory, renderer, scheduler));
          assertThrowsIllegalArgument(
              () ->
                  new LifeLikeController(display, params, null, sm, factory, renderer, scheduler));
          assertThrowsIllegalArgument(
              () ->
                  new LifeLikeController(
                      display, params, toolbar, null, factory, renderer, scheduler));
          assertThrowsIllegalArgument(
              () ->
                  new LifeLikeController(display, params, toolbar, sm, null, renderer, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, toolbar, sm, factory, null, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, toolbar, sm, factory, renderer, null));
        });
  }

  @Test
  public void testInitialToolbarStateMatchesUninitialized() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);

          Assert.assertTrue(controller.getStateMachine().isUninitialized());
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
        });
  }

  @Test
  public void testStepFromUninitializedInitializesAndAdvancesExactlyOneGeneration() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.UNIFORM);

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.step();

          Assert.assertTrue(controller.hasActiveSimulation());
          LifeLikeSimulation sim = controller.getActiveSimulation();
          Assert.assertNotNull(sim);
          Assert.assertEquals(1, sim.getGeneration());

          LifeLikeRule rule = sim.getRule();
          Assert.assertEquals(Set.of(3), rule.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), rule.getSurvivalCounts());
          Assert.assertEquals(8, sim.getCurrentGrid().getSize());

          Assert.assertEquals(
              LifeLikeStateMachine.State.PAUSED, controller.getStateMachine().getState());
          Assert.assertTrue(controller.getStateMachine().isPaused());

          Image activeImage = controller.getActiveImage();
          Assert.assertNotNull(activeImage);
          Assert.assertSame(activeImage, displayPanel.getAutomatonImage());
          Assert.assertEquals(400, (int) activeImage.getWidth());
          Assert.assertEquals(400, (int) activeImage.getHeight());

          Assert.assertFalse(displayPanel.isDisplayingError());
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());

          // Verify pixel colors against generation 1 grid content
          PixelReader pixelReader = activeImage.getPixelReader();
          Grid2D currentGrid = sim.getCurrentGrid();
          int size = currentGrid.getSize();
          for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
              int sampleX = (int) ((col + 0.5) * 400.0 / size);
              int sampleY = (int) ((row + 0.5) * 400.0 / size);
              int argb = pixelReader.getArgb(sampleX, sampleY) & 0xFFFFFF;
              int expectedRgb = currentGrid.isAlive(row, col) ? 0x000000 : 0xFFFFFF;
              Assert.assertEquals("Mismatch at row " + row + ", col " + col, expectedRgb, argb);
            }
          }
        });
  }

  @Test
  public void testStepWhenAlreadyInitializedAndUnchangedAdvancesOneMoreGeneration() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.step();

          LifeLikeSimulation sim1 = controller.getActiveSimulation();
          Assert.assertEquals(1, sim1.getGeneration());

          controller.step();
          Assert.assertSame(sim1, controller.getActiveSimulation());
          Assert.assertEquals(2, sim1.getGeneration());
          Assert.assertTrue(controller.getStateMachine().isPaused());
        });
  }

  @Test
  public void testStepWithChangedSettingsReinitializesAndAdvancesToGenerationOne() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.step();
          Assert.assertEquals(8, controller.getActiveSimulation().getCurrentGrid().getSize());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());

          // Change form parameters
          parametersPanel.setSizeValue(16);
          parametersPanel.selectPreset(LifeLikePreset.SEEDS);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          controller.step();
          Assert.assertEquals(16, controller.getActiveSimulation().getCurrentGrid().getSize());
          Assert.assertEquals(
              Set.of(2), controller.getActiveSimulation().getRule().getBirthCounts());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
        });
  }

  @Test
  public void testPlayPauseFromUninitializedInitializesGen0AndStartsScheduler() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  displayPanelImage -> controllerDefaultRender(displayPanelImage),
                  scheduler);

          controller.togglePlayPause();

          Assert.assertTrue(controller.getStateMachine().isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());

          // Verify toolbar in RUNNING state
          Assert.assertTrue(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertTrue(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());

          // Advance ticks via scheduler
          scheduler.tick();
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());

          scheduler.tick();
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());

          // Pause
          controller.togglePlayPause();
          Assert.assertTrue(controller.getStateMachine().isPaused());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());

          // Resume
          controller.togglePlayPause();
          Assert.assertTrue(controller.getStateMachine().isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());
        });
  }

  @Test
  public void testResetReturnsToGenerationZeroStopsSchedulerAndTransitionsToPaused() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  displayPanelImage -> controllerDefaultRender(displayPanelImage),
                  scheduler);

          // Reset when uninitialized is ignored
          controller.reset();
          Assert.assertTrue(controller.getStateMachine().isUninitialized());

          // Start running and advance 2 generations
          controller.togglePlayPause();
          scheduler.tick();
          scheduler.tick();
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());

          Grid2D gen0Grid = controller.getActiveSimulation().getInitialGrid();

          // Reset while running
          controller.reset();
          Assert.assertTrue(controller.getStateMachine().isPaused());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());
          Assert.assertEquals(gen0Grid, controller.getActiveSimulation().getCurrentGrid());
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
        });
  }

  @Test
  public void testGenerationFailureHandlingClearsActiveStateAndShowsError() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController.SimulationFactory failingFactory =
              (rule, size, seedType, density) -> {
                throw new RuntimeException("Simulated generation failure");
              };

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  failingFactory,
                  grid -> null,
                  scheduler);

          controller.step();

          Assert.assertNull(controller.getActiveSimulation());
          Assert.assertNull(controller.getActiveConfiguration());
          Assert.assertNull(controller.getActiveImage());
          Assert.assertTrue(controller.getStateMachine().isUninitialized());
          Assert.assertFalse(scheduler.isRunning());

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.GENERATION_ERROR_PRIMARY, displayPanel.getPrimaryErrorMessage());
          Assert.assertEquals(
              DisplayPanel.ERROR_SECONDARY, displayPanel.getSecondaryErrorMessage());

          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
        });
  }

  @Test
  public void testRenderingFailureHandlingClearsActiveStateAndShowsError() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController.GridRenderer failingRenderer =
              grid -> {
                throw new RuntimeException("Simulated rendering failure");
              };

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  failingRenderer,
                  scheduler);

          controller.step();

          Assert.assertNull(controller.getActiveSimulation());
          Assert.assertNull(controller.getActiveConfiguration());
          Assert.assertNull(controller.getActiveImage());
          Assert.assertTrue(controller.getStateMachine().isUninitialized());
          Assert.assertFalse(scheduler.isRunning());

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.RENDERING_ERROR_PRIMARY, displayPanel.getPrimaryErrorMessage());
          Assert.assertEquals(
              DisplayPanel.ERROR_SECONDARY, displayPanel.getSecondaryErrorMessage());

          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
        });
  }

  @Test
  public void testDisposeStopsSchedulerAndCleansUp() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  displayPanelImage -> controllerDefaultRender(displayPanelImage),
                  scheduler);

          controller.togglePlayPause();
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertTrue(controller.hasActiveSimulation());

          controller.dispose();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertTrue(controller.getStateMachine().isUninitialized());
          Assert.assertNull(controller.getActiveSimulation());
          Assert.assertNull(controller.getActiveConfiguration());
          Assert.assertNull(controller.getActiveImage());
        });
  }

  @Test
  public void testToolbarButtonActionsTriggerControllerOperations() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  displayPanelImage -> controllerDefaultRender(displayPanelImage),
                  scheduler);

          // Step via toolbar button
          toolbar.getStepButton().fire();
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Assert.assertTrue(controller.getStateMachine().isPaused());

          // Play via toolbar button
          toolbar.getPlayPauseButton().fire();
          Assert.assertTrue(controller.getStateMachine().isRunning());

          // Pause via toolbar button
          toolbar.getPlayPauseButton().fire();
          Assert.assertTrue(controller.getStateMachine().isPaused());

          // Reset via toolbar button
          toolbar.getResetButton().fire();
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());
        });
  }

  @Test
  public void testStepFromPausedAdvancesWithVisiblyDifferentGenerations() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.DENSITY);
          parametersPanel.setDensityValue(50);

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.step();

          Grid2D gen1Grid = controller.getActiveSimulation().getCurrentGrid();
          Image gen1Image = controller.getActiveImage();
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());

          controller.step();

          Grid2D gen2Grid = controller.getActiveSimulation().getCurrentGrid();
          Image gen2Image = controller.getActiveImage();
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());

          Assert.assertNotEquals(gen1Grid, gen2Grid);
          Assert.assertNotSame(gen1Image, gen2Image);
        });
  }

  @Test
  public void testResetRestoresExactInitialGridWithRandomizedSeedAndIgnoresFormEdits() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          parametersPanel.setSizeValue(16);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.DENSITY);
          parametersPanel.setDensityValue(50);

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.step();
          controller.step();
          controller.step();

          Assert.assertEquals(3, controller.getActiveSimulation().getGeneration());
          Grid2D initialGrid = controller.getActiveSimulation().getInitialGrid();
          Grid2D gen3Grid = controller.getActiveSimulation().getCurrentGrid();
          Assert.assertNotEquals(initialGrid, gen3Grid);

          // Mutate form values before resetting
          parametersPanel.setSizeValue(32);
          parametersPanel.selectPreset(LifeLikePreset.SEEDS);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          controller.reset();

          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());
          Assert.assertEquals(initialGrid, controller.getActiveSimulation().getCurrentGrid());
          Assert.assertEquals(16, controller.getActiveSimulation().getSize());
          Assert.assertTrue(controller.getStateMachine().isPaused());
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
        });
  }

  @Test
  public void testFormEditsWhileRunningDoNotMutateAndChangeThenRevertRetainsSimulation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  displayPanelImage -> controllerDefaultRender(displayPanelImage),
                  scheduler);

          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.UNIFORM);

          controller.togglePlayPause();
          Assert.assertTrue(controller.getStateMachine().isRunning());
          scheduler.tick();
          scheduler.tick();

          LifeLikeSimulation sim = controller.getActiveSimulation();
          Assert.assertEquals(2, sim.getGeneration());
          Assert.assertEquals(8, sim.getSize());

          // Form edits while running do not mutate active simulation
          parametersPanel.setSizeValue(16);
          parametersPanel.selectPreset(LifeLikePreset.HIGH_LIFE);

          Assert.assertEquals(8, sim.getSize());
          Assert.assertEquals(2, sim.getGeneration());

          // Pause
          controller.togglePlayPause();
          Assert.assertTrue(controller.getStateMachine().isPaused());

          // Revert form values back to match active configuration
          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.UNIFORM);

          controller.step();

          // Simulation instance is retained (not replaced) and advances to generation 3
          Assert.assertSame(sim, controller.getActiveSimulation());
          Assert.assertEquals(3, sim.getGeneration());

          // Now apply distinct changed settings and step
          parametersPanel.setSizeValue(16);
          parametersPanel.selectPreset(LifeLikePreset.SEEDS);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          controller.step();

          Assert.assertNotSame(sim, controller.getActiveSimulation());
          Assert.assertEquals(16, controller.getActiveSimulation().getSize());
          Assert.assertEquals(
              Set.of(2), controller.getActiveSimulation().getRule().getBirthCounts());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
        });
  }

  @Test
  public void testSlowRendererAndCancellationStaleTicksRejected() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  displayPanelImage -> controllerDefaultRender(displayPanelImage),
                  scheduler);

          controller.togglePlayPause();
          Assert.assertTrue(scheduler.isRunning());

          // Pause cancels scheduler
          controller.togglePlayPause();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertTrue(controller.getStateMachine().isPaused());

          // Stale tick while paused is rejected
          long genBefore = controller.getActiveSimulation().getGeneration();
          controller.tick();
          Assert.assertEquals(genBefore, controller.getActiveSimulation().getGeneration());

          // Resume running
          controller.togglePlayPause();
          Assert.assertTrue(scheduler.isRunning());

          // Reset cancels scheduler and rejects stale tick
          controller.reset();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertTrue(controller.getStateMachine().isPaused());
          controller.tick();
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());

          // Resume running then dispose
          controller.togglePlayPause();
          Assert.assertTrue(scheduler.isRunning());
          controller.dispose();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertTrue(controller.getStateMachine().isUninitialized());
          controller.tick(); // Stale tick after dispose does not throw
        });
  }

  @Test
  public void testSuccessfulRetryAfterGenerationOrRenderingFailure() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          // 1. Generation failure followed by successful retry
          final boolean[] failGeneration = {true};
          LifeLikeController.SimulationFactory factory =
              (rule, size, seedType, density) -> {
                if (failGeneration[0]) {
                  throw new RuntimeException("Simulated generation failure");
                }
                return com.cellularautomata.engine.automata.AutomataEngine.createLifeLikeSimulation(
                    rule, size, seedType, density);
              };

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  factory,
                  displayPanelImage -> controllerDefaultRender(displayPanelImage),
                  scheduler);

          controller.step();
          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertNull(controller.getActiveSimulation());

          // Retry with fixed factory
          failGeneration[0] = false;
          controller.step();
          Assert.assertFalse(displayPanel.isDisplayingError());
          Assert.assertNotNull(displayPanel.getAutomatonImage());
          Assert.assertNotNull(controller.getActiveSimulation());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());

          // 2. Rendering failure followed by successful retry
          final boolean[] failRendering = {true};
          LifeLikeController.GridRenderer renderer =
              grid -> {
                if (failRendering[0]) {
                  throw new RuntimeException("Simulated rendering failure");
                }
                return controllerDefaultRender(grid);
              };

          LifeLikeController controller2 =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  renderer,
                  scheduler);

          controller2.step();
          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertNull(controller2.getActiveSimulation());

          // Retry with fixed renderer
          failRendering[0] = false;
          controller2.step();
          Assert.assertFalse(displayPanel.isDisplayingError());
          Assert.assertNotNull(displayPanel.getAutomatonImage());
          Assert.assertNotNull(controller2.getActiveSimulation());
        });
  }

  @Test
  public void testDisplayPanelSizeRemains400x400RegardlessOfGridSize() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);

          // Test min size 4
          parametersPanel.setSizeValue(4);
          controller.step();
          Image image4 = controller.getActiveImage();
          Assert.assertEquals(400.0, image4.getWidth(), 0.001);
          Assert.assertEquals(400.0, image4.getHeight(), 0.001);

          // Test max size 128
          parametersPanel.setSizeValue(128);
          controller.step();
          Image image128 = controller.getActiveImage();
          Assert.assertEquals(400.0, image128.getWidth(), 0.001);
          Assert.assertEquals(400.0, image128.getHeight(), 0.001);
        });
  }

  private static Image controllerDefaultRender(Grid2D grid) {
    java.awt.image.BufferedImage raw =
        com.cellularautomata.image.AutomatonImage.getImageFromGrid(grid);
    java.awt.image.BufferedImage resized =
        com.cellularautomata.image.AutomatonImage.resizeImage(
            com.cellularautomata.image.AutomatonImage.DISPLAY_IMAGE_SIZE,
            com.cellularautomata.image.AutomatonImage.DISPLAY_IMAGE_SIZE,
            raw);
    return javafx.embed.swing.SwingFXUtils.toFXImage(resized, null);
  }

  private static void assertThrowsIllegalArgument(Runnable runnable) {
    try {
      runnable.run();
      Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }
  }
}
