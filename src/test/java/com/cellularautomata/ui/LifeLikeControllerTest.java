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
              () -> new LifeLikeController(null, params, toolbar, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, null, toolbar, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, null, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, toolbar, (SimulationScheduler) null));

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

          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
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
          parametersPanel.selectSeedType(SeedType.SPARSE);

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

          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());

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
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
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

          Assert.assertTrue(controller.isRunning());
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
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());

          // Resume
          controller.togglePlayPause();
          Assert.assertTrue(controller.isRunning());
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
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());

          // Start running and advance 2 generations
          controller.togglePlayPause();
          scheduler.tick();
          scheduler.tick();
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());

          Grid2D gen0Grid = controller.getActiveSimulation().getInitialGrid();

          // Reset while running
          controller.reset();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
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
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
          Assert.assertFalse(scheduler.isRunning());

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.GENERATION_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
              displayPanel.getErrorMessage());

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
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
          Assert.assertFalse(scheduler.isRunning());

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.RENDERING_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
              displayPanel.getErrorMessage());

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
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
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
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());

          // Play via toolbar button
          toolbar.getPlayPauseButton().fire();
          Assert.assertTrue(controller.isRunning());

          // Pause via toolbar button
          toolbar.getPlayPauseButton().fire();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());

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
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
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
          parametersPanel.selectSeedType(SeedType.SPARSE);

          controller.togglePlayPause();
          Assert.assertTrue(controller.isRunning());
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
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());

          // Revert form values back to match active configuration
          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.SPARSE);

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
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());

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
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          controller.tick();
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());

          // Resume running then dispose
          controller.togglePlayPause();
          Assert.assertTrue(scheduler.isRunning());
          controller.dispose();
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
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
  public void testControllerLifecycleAndOperations() {
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
                  LifeLikeControllerTest::controllerDefaultRender,
                  scheduler);

          // 1. Initial Uninitialized state
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
          Assert.assertFalse(controller.isRunning());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertFalse(controller.hasActiveSimulation());

          // Calling reset while uninitialized is a safe no-op
          controller.reset();
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());

          // 2. Step from uninitialized initializes and advances to generation 1 in PAUSED state
          controller.step();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(controller.isRunning());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertTrue(controller.hasActiveSimulation());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Assert.assertNotNull(controller.getActiveImage());

          // 3. Step again advances to generation 2
          controller.step();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());

          // 4. Play starts the scheduler and transitions to RUNNING
          controller.play();
          Assert.assertEquals(LifeLikeStateMachine.State.RUNNING, controller.getState());
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());

          // Redundant play is a safe no-op
          controller.play();
          Assert.assertEquals(LifeLikeStateMachine.State.RUNNING, controller.getState());
          Assert.assertTrue(controller.isRunning());

          // Scheduler tick advances generation to 3
          scheduler.tick();
          Assert.assertEquals(3, controller.getActiveSimulation().getGeneration());

          // 5. Pause halts the scheduler and transitions to PAUSED
          controller.pause();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(controller.isRunning());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertEquals(3, controller.getActiveSimulation().getGeneration());

          // Redundant pause is a safe no-op
          controller.pause();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(controller.isRunning());

          // 6. Reset restores initial generation 0
          controller.reset();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());
          Assert.assertFalse(scheduler.isRunning());

          // 7. Dispose stops scheduler and cleans up all active state
          controller.play();
          Assert.assertTrue(scheduler.isRunning());
          controller.dispose();
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
          Assert.assertFalse(controller.isRunning());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertNull(controller.getActiveSimulation());
          Assert.assertNull(controller.getActiveConfiguration());
          Assert.assertNull(controller.getActiveImage());
        });
  }

  @Test
  public void testBoundarySizes5And155DeterministicSimulationAndRendering() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);

          // Test min size 5 with deterministic SPARSE seed
          parametersPanel.setSizeValue(5);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.SPARSE);
          controller.step();

          Assert.assertEquals(5, controller.getActiveSimulation().getSize());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Image image5 = controller.getActiveImage();
          Assert.assertNotNull(image5);
          Assert.assertEquals(400.0, image5.getWidth(), 0.001);
          Assert.assertEquals(400.0, image5.getHeight(), 0.001);

          // Test max size 155 with deterministic ALTERNATING seed
          parametersPanel.setSizeValue(155);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);
          controller.step();

          Assert.assertEquals(155, controller.getActiveSimulation().getSize());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Image image155 = controller.getActiveImage();
          Assert.assertNotNull(image155);
          Assert.assertEquals(400.0, image155.getWidth(), 0.001);
          Assert.assertEquals(400.0, image155.getHeight(), 0.001);

          controller.dispose();
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

          // Test min size 5
          parametersPanel.setSizeValue(5);
          controller.step();
          Image image5 = controller.getActiveImage();
          Assert.assertEquals(400.0, image5.getWidth(), 0.001);
          Assert.assertEquals(400.0, image5.getHeight(), 0.001);

          // Test max size 155
          parametersPanel.setSizeValue(155);
          controller.step();
          Image image155 = controller.getActiveImage();
          Assert.assertEquals(400.0, image155.getWidth(), 0.001);
          Assert.assertEquals(400.0, image155.getHeight(), 0.001);
        });
  }

  @Test
  public void testFourArgumentConstructorUsesInjectedSchedulerAndDefaultCollaborators() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

          controller.step();
          Assert.assertTrue(controller.hasActiveSimulation());
          Assert.assertNotNull(controller.getActiveImage());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());

          controller.play();
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());

          scheduler.tick();
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());

          controller.pause();
          Assert.assertFalse(controller.isRunning());
          Assert.assertFalse(scheduler.isRunning());
        });
  }

  @Test
  public void testCustomPresetDropdownScenario6CustomRemainsDisplayedThroughPlayPauseStepReset() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller = new LifeLikeController(displayPanel, panel, toolbar);

          // Make configuration custom: B3/S3
          panel.toggleSurvival(2);
          Assert.assertEquals("Custom", panel.getPresetDisplayText());

          // 1. Step
          controller.step();
          Assert.assertEquals("Custom", panel.getPresetDisplayText());
          Assert.assertNull(panel.getPreset());

          // 2. Play
          controller.play();
          Assert.assertEquals("Custom", panel.getPresetDisplayText());
          Assert.assertNull(panel.getPreset());

          // 3. Pause
          controller.pause();
          Assert.assertEquals("Custom", panel.getPresetDisplayText());
          Assert.assertNull(panel.getPreset());

          // 4. Reset
          controller.reset();
          Assert.assertEquals("Custom", panel.getPresetDisplayText());
          Assert.assertNull(panel.getPreset());

          controller.dispose();
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
