package com.cellularautomata.ui;

import com.cellularautomata.engine.Grid2D;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.automata.LifeLikeSimulation;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.stage.Window;
import javax.imageio.ImageIO;
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
          DialogService dialogService = new DialogService();

          assertThrowsIllegalArgument(() -> new LifeLikeController(null, params, toolbar));
          assertThrowsIllegalArgument(() -> new LifeLikeController(display, null, toolbar));
          assertThrowsIllegalArgument(() -> new LifeLikeController(display, params, null));

          assertThrowsIllegalArgument(
              () -> new LifeLikeController(null, params, toolbar, dialogService));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, null, toolbar, dialogService));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, null, dialogService));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, toolbar, (DialogService) null));

          assertThrowsIllegalArgument(
              () -> new LifeLikeController(null, params, toolbar, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, null, toolbar, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, null, scheduler));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, toolbar, (SimulationScheduler) null));

          assertThrowsIllegalArgument(
              () -> new LifeLikeController(null, params, toolbar, scheduler, dialogService));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, null, toolbar, scheduler, dialogService));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, null, scheduler, dialogService));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, toolbar, null, dialogService));
          assertThrowsIllegalArgument(
              () -> new LifeLikeController(display, params, toolbar, scheduler, null));

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
          assertThrowsIllegalArgument(
              () ->
                  new LifeLikeController(
                      display, params, toolbar, sm, factory, renderer, scheduler, null));
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
              new LifeLikeController(
                  displayPanel, parametersPanel, toolbar, new ControlledScheduler());

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
              new LifeLikeController(
                  displayPanel, parametersPanel, toolbar, new ControlledScheduler());
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
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());

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
              new LifeLikeController(
                  displayPanel, parametersPanel, toolbar, new ControlledScheduler());
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
              new LifeLikeController(
                  displayPanel, parametersPanel, toolbar, new ControlledScheduler());
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
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

          controller.togglePlayPause();

          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());

          // Redundant play is a safe no-op
          controller.play();
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());

          // Verify toolbar in RUNNING state
          Assert.assertTrue(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertTrue(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());

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
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());

          // Redundant pause is a safe no-op
          controller.pause();
          Assert.assertFalse(controller.isRunning());
          Assert.assertFalse(scheduler.isRunning());

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
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

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
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
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
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

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
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

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
              new LifeLikeController(
                  displayPanel, parametersPanel, toolbar, new ControlledScheduler());
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
              new LifeLikeController(
                  displayPanel, parametersPanel, toolbar, new ControlledScheduler());
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
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
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
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

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
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          controller.step();

          // Simulation instance is retained (not replaced) and advances to generation 3
          Assert.assertSame(sim, controller.getActiveSimulation());
          Assert.assertEquals(3, sim.getGeneration());

          // Now apply distinct changed settings and step
          parametersPanel.setSizeValue(16);
          parametersPanel.selectPreset(LifeLikePreset.SEEDS);
          parametersPanel.selectSeedType(SeedType.SPARSE);

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
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

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
                  grid -> new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB),
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
                return new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
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
  public void testBoundarySizesDeterministicSimulationAndRendering() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel, parametersPanel, toolbar, new ControlledScheduler());

          // Test min size with deterministic SPARSE seed
          parametersPanel.setSizeValue(LifeLikeParametersPanel.MIN_SIZE);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.SPARSE);
          controller.step();

          Assert.assertEquals(
              LifeLikeParametersPanel.MIN_SIZE, controller.getActiveSimulation().getSize());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Image minImage = controller.getActiveImage();
          Assert.assertNotNull(minImage);
          Assert.assertEquals(400.0, minImage.getWidth(), 0.001);
          Assert.assertEquals(400.0, minImage.getHeight(), 0.001);

          // Test max size with deterministic ALTERNATING seed
          parametersPanel.setSizeValue(LifeLikeParametersPanel.MAX_SIZE);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);
          controller.step();

          Assert.assertEquals(
              LifeLikeParametersPanel.MAX_SIZE, controller.getActiveSimulation().getSize());
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Image maxImage = controller.getActiveImage();
          Assert.assertNotNull(maxImage);
          Assert.assertEquals(400.0, maxImage.getWidth(), 0.001);
          Assert.assertEquals(400.0, maxImage.getHeight(), 0.001);

          controller.dispose();
        });
  }

  @Test
  public void testCustomPresetDropdownScenario6CustomRemainsDisplayedThroughPlayPauseStepReset() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, panel, toolbar, new ControlledScheduler());

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

  @Test
  public void
      testPlaybackSpeedSelectionAppliesWhenPausedUninitializedAndRunningWithoutResettingOrDuplicatingLoops() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

          Assert.assertEquals(1.00, scheduler.getPlaybackSpeed(), 0.001);
          LifeLikeConfiguration initialCapturedConfig = controller.captureConfiguration();

          // 1. Selecting speed while UNINITIALIZED does not start playback and does not alter
          // LifeLikeConfiguration
          parametersPanel.setPlaybackSpeed(0.50);
          Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, controller.getState());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertEquals(0.50, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertEquals(initialCapturedConfig, controller.captureConfiguration());

          // 2. Starting playback applies the pre-selected 0.50x speed
          controller.play();
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertEquals(0.50, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertEquals(1, scheduler.getStartCount());

          scheduler.tick();
          scheduler.tick();
          LifeLikeSimulation activeSim = controller.getActiveSimulation();
          Assert.assertEquals(2, activeSim.getGeneration());

          // 3. Changing speed while RUNNING updates scheduler speed immediately without resetting
          // or starting a duplicate loop
          parametersPanel.setPlaybackSpeed(1.75);
          Assert.assertEquals(1.75, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertEquals(1, scheduler.getStartCount());
          Assert.assertSame(activeSim, controller.getActiveSimulation());
          Assert.assertEquals(2, activeSim.getGeneration());

          scheduler.tick();
          Assert.assertEquals(3, activeSim.getGeneration());

          // 4. Pausing and changing speed while PAUSED does not start scheduler or reinitialize on
          // Step/Play
          controller.pause();
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(scheduler.isRunning());

          parametersPanel.setPlaybackSpeed(0.25);
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertEquals(0.25, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertEquals(initialCapturedConfig, controller.captureConfiguration());

          // Step retains the same simulation instance and advances to generation 4
          controller.step();
          Assert.assertSame(activeSim, controller.getActiveSimulation());
          Assert.assertEquals(4, activeSim.getGeneration());

          // Resume applies 0.25x speed and retains generation 4
          controller.play();
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertEquals(0.25, scheduler.getPlaybackSpeed(), 0.001);
          Assert.assertEquals(2, scheduler.getStartCount());
          Assert.assertSame(activeSim, controller.getActiveSimulation());
          Assert.assertEquals(4, activeSim.getGeneration());
        });
  }

  @Test
  public void testReentrantTickDuringSlowRenderAndMidTickCancellationPreventStaleUpdates() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          AtomicReference<LifeLikeController> controllerRef = new AtomicReference<>();
          AtomicBoolean triggerReentrantTick = new AtomicBoolean(false);
          AtomicBoolean triggerMidRenderPause = new AtomicBoolean(false);

          LifeLikeController.GridRenderer customRenderer =
              grid -> {
                if (triggerReentrantTick.get()) {
                  // Simulate an overlapping tick callback firing while rendering is still in
                  // progress
                  controllerRef.get().tick();
                }
                if (triggerMidRenderPause.get()) {
                  controllerRef.get().pause();
                }
                BufferedImage raw = AutomatonImage.getImageFromGrid(grid);
                return AutomatonImage.resizeImage(
                    AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, raw);
              };

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  com.cellularautomata.engine.automata.AutomataEngine::createLifeLikeSimulation,
                  customRenderer,
                  scheduler);
          controllerRef.set(controller);

          controller.play();
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());

          // 1. Overlapping/re-entrant tick during slow render is ignored (advances only 1
          // generation, not 2)
          triggerReentrantTick.set(true);
          scheduler.tick();
          triggerReentrantTick.set(false);
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Assert.assertTrue(controller.isRunning());

          // 2. Mid-render pause cancels in-flight tick from overwriting display image
          Image imageBeforeCancelledTick = displayPanel.getAutomatonImage();
          triggerMidRenderPause.set(true);
          scheduler.tick();
          triggerMidRenderPause.set(false);

          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertSame(imageBeforeCancelledTick, displayPanel.getAutomatonImage());
        });
  }

  @Test
  public void testFixedPointAutoPauseStopsOnConsecutiveIdenticalGridsAndRetainsFinalImageForSave() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          // SPARSE in Conway's Game of Life (B3/S23) has 1 live cell at gen 0, 0 live cells at gen
          // 1, and 0 live cells at gen 2 (consecutive identical dead grids on tick 2)
          parametersPanel.setSizeValue(8);
          parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          parametersPanel.selectSeedType(SeedType.SPARSE);

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar, scheduler);

          controller.play();
          Assert.assertTrue(controller.isRunning());
          Assert.assertEquals(1, controller.getActiveSimulation().getLiveCellCount());

          // Tick 1: 1 live cell -> 0 live cells (grid changed, so playback continues)
          scheduler.tick();
          Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
          Assert.assertEquals(0, controller.getActiveSimulation().getLiveCellCount());
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertTrue(toolbar.isPlaying());

          // Tick 2: 0 live cells -> 0 live cells (consecutive identical grids -> auto-pause!)
          scheduler.tick();
          Assert.assertEquals(2, controller.getActiveSimulation().getGeneration());
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(controller.isRunning());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getPlayPauseButton().isDisabled());
          Assert.assertFalse(toolbar.getStepButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
          Assert.assertNotNull(controller.getActiveImage());
          Assert.assertSame(controller.getActiveImage(), displayPanel.getAutomatonImage());

          // Resuming from a fixed point pauses cleanly on the very next tick without a runaway loop
          controller.play();
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());

          scheduler.tick();
          Assert.assertEquals(3, controller.getActiveSimulation().getGeneration());
          Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
          Assert.assertFalse(scheduler.isRunning());
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
        });
  }

  @Test
  public void testOscillatorBlinkerContinuesRunningAcrossPeriodsWithoutAutoPausing() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();
          ControlledScheduler scheduler = new ControlledScheduler();

          // Construct a period-2 blinker (horizontal 3-cell bar in a 5x5 grid under B3/S23)
          boolean[][] blinkerCells = new boolean[5][5];
          blinkerCells[2][1] = true;
          blinkerCells[2][2] = true;
          blinkerCells[2][3] = true;
          Grid2D horizontalBlinker = new Grid2D(5, blinkerCells);

          LifeLikeController.SimulationFactory blinkerFactory =
              (rule, size, seedType, density) -> new LifeLikeSimulation(rule, horizontalBlinker);

          LifeLikeController controller =
              new LifeLikeController(
                  displayPanel,
                  parametersPanel,
                  toolbar,
                  new LifeLikeStateMachine(),
                  blinkerFactory,
                  grid -> {
                    BufferedImage raw = AutomatonImage.getImageFromGrid(grid);
                    return AutomatonImage.resizeImage(
                        AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, raw);
                  },
                  scheduler);

          controller.play();
          Assert.assertTrue(controller.isRunning());
          Assert.assertEquals(horizontalBlinker, controller.getActiveSimulation().getCurrentGrid());

          // Tick 1: vertical blinker (differs from horizontal)
          scheduler.tick();
          Grid2D verticalBlinker = controller.getActiveSimulation().getCurrentGrid();
          Assert.assertNotEquals(horizontalBlinker, verticalBlinker);
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());

          // Tick 2: returns to horizontal blinker (matches gen 0, differs from gen 1 -> NOT a
          // fixed point)
          scheduler.tick();
          Assert.assertEquals(horizontalBlinker, controller.getActiveSimulation().getCurrentGrid());
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());

          // Tick 3 and Tick 4: continues oscillating without auto-pausing
          scheduler.tick();
          Assert.assertEquals(verticalBlinker, controller.getActiveSimulation().getCurrentGrid());
          Assert.assertTrue(controller.isRunning());

          scheduler.tick();
          Assert.assertEquals(horizontalBlinker, controller.getActiveSimulation().getCurrentGrid());
          Assert.assertEquals(4, controller.getActiveSimulation().getGeneration());
          Assert.assertTrue(controller.isRunning());
          Assert.assertTrue(scheduler.isRunning());
          Assert.assertTrue(toolbar.isPlaying());
        });
  }

  @Test
  public void
      testSaveExportsCurrentlyDisplayedPixelsWithExpectedFileNameAndNoPlaybackSideEffects() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          try {
            Path tempDir = Files.createTempDirectory("lifelike_save_test");
            File savedGen1File = tempDir.resolve("gen1.png").toFile();
            File savedGen2File = tempDir.resolve("gen2.png").toFile();
            savedGen1File.deleteOnExit();
            savedGen2File.deleteOnExit();
            tempDir.toFile().deleteOnExit();

            DisplayPanel displayPanel = new DisplayPanel();
            LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
            LifeLikeToolbar toolbar = new LifeLikeToolbar();
            ControlledScheduler scheduler = new ControlledScheduler();
            RecordingSaveDialogService dialogService = new RecordingSaveDialogService();

            parametersPanel.setSizeValue(8);
            parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
            parametersPanel.selectSeedType(SeedType.ALTERNATING);

            LifeLikeController controller =
                new LifeLikeController(
                    displayPanel, parametersPanel, toolbar, scheduler, dialogService);

            // 1. Save in UNINITIALIZED state is a no-op and button is disabled
            Assert.assertTrue(toolbar.getSaveButton().isDisabled());
            controller.save();
            Assert.assertEquals(0, dialogService.saveDialogCallCount);

            // 2. Step to generation 1 (PAUSED)
            controller.step();
            Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
            Assert.assertFalse(toolbar.getSaveButton().isDisabled());

            LifeLikeSimulation activeSim = controller.getActiveSimulation();
            Grid2D gen1Grid = activeSim.getCurrentGrid();
            Image gen1FxImage = controller.getActiveImage();

            // Mutate form controls without stepping/playing to verify Save uses the active
            // simulation's effective rule and size
            parametersPanel.setSizeValue(32);
            parametersPanel.selectPreset(LifeLikePreset.SEEDS);

            dialogService.fileToReturn = savedGen1File;
            toolbar.getSaveButton().fire();

            Assert.assertEquals(1, dialogService.saveDialogCallCount);
            Assert.assertFalse(dialogService.saveErrorShown);
            Assert.assertNotNull(dialogService.lastSuggestedFileName);
            Assert.assertTrue(
                dialogService.lastSuggestedFileName.startsWith("life_like_ruleB3-S23_size8_"));
            Assert.assertTrue(dialogService.lastSuggestedFileName.endsWith(".png"));
            Assert.assertTrue(savedGen1File.exists());

            // Verify exported PNG pixels match generation 1 grid
            BufferedImage exportedGen1 = ImageIO.read(savedGen1File);
            Assert.assertNotNull(exportedGen1);
            Assert.assertEquals(400, exportedGen1.getWidth());
            Assert.assertEquals(400, exportedGen1.getHeight());
            assertExportedImageMatchesGrid(exportedGen1, gen1Grid);

            // Verify zero playback side effects while PAUSED
            Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
            Assert.assertFalse(scheduler.isRunning());
            Assert.assertSame(activeSim, controller.getActiveSimulation());
            Assert.assertEquals(1, activeSim.getGeneration());
            Assert.assertSame(gen1FxImage, controller.getActiveImage());

            // 3. Revert form controls to match active simulation, Play, advance to generation 2
            // (RUNNING), and Save while RUNNING
            parametersPanel.setSizeValue(8);
            parametersPanel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
            parametersPanel.selectSeedType(SeedType.ALTERNATING);

            controller.play();
            scheduler.tick();
            Assert.assertTrue(controller.isRunning());
            Assert.assertEquals(2, activeSim.getGeneration());
            Grid2D gen2Grid = activeSim.getCurrentGrid();
            Assert.assertNotEquals(gen1Grid, gen2Grid);

            dialogService.fileToReturn = savedGen2File;
            toolbar.getSaveButton().fire();

            Assert.assertEquals(2, dialogService.saveDialogCallCount);
            Assert.assertFalse(dialogService.saveErrorShown);
            Assert.assertTrue(savedGen2File.exists());

            BufferedImage exportedGen2 = ImageIO.read(savedGen2File);
            Assert.assertNotNull(exportedGen2);
            assertExportedImageMatchesGrid(exportedGen2, gen2Grid);

            // Verify zero playback side effects while RUNNING (did not pause, reset, advance, or
            // regenerate)
            Assert.assertTrue(controller.isRunning());
            Assert.assertTrue(scheduler.isRunning());
            Assert.assertSame(activeSim, controller.getActiveSimulation());
            Assert.assertEquals(2, activeSim.getGeneration());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Test
  public void testSaveDialogCancelAndIoExceptionHandling() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          try {
            Path tempDir = Files.createTempDirectory("lifelike_save_error_test");
            tempDir.toFile().deleteOnExit();

            DisplayPanel displayPanel = new DisplayPanel();
            LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
            LifeLikeToolbar toolbar = new LifeLikeToolbar();
            ControlledScheduler scheduler = new ControlledScheduler();
            RecordingSaveDialogService dialogService = new RecordingSaveDialogService();

            LifeLikeController controller =
                new LifeLikeController(
                    displayPanel, parametersPanel, toolbar, scheduler, dialogService);

            controller.step();
            Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());

            // 1. User cancels save dialog (returns null) -> no error shown, state unchanged
            dialogService.fileToReturn = null;
            controller.save();
            Assert.assertEquals(1, dialogService.saveDialogCallCount);
            Assert.assertFalse(dialogService.saveErrorShown);
            Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());

            // 2. IOException when writing to a path with a missing parent directory ->
            // showSaveError invoked, simulation state remains intact
            dialogService.fileToReturn = tempDir.resolve("missing_parent_dir/out.png").toFile();
            controller.save();
            Assert.assertEquals(2, dialogService.saveDialogCallCount);
            Assert.assertTrue(dialogService.saveErrorShown);
            Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, controller.getState());
            Assert.assertEquals(1, controller.getActiveSimulation().getGeneration());
            Assert.assertFalse(toolbar.getSaveButton().isDisabled());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private static void assertExportedImageMatchesGrid(BufferedImage exportedImage, Grid2D grid) {
    int size = grid.getSize();
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        int sampleX = (int) ((col + 0.5) * 400.0 / size);
        int sampleY = (int) ((row + 0.5) * 400.0 / size);
        int actualRgb = exportedImage.getRGB(sampleX, sampleY) & 0xFFFFFF;
        int expectedRgb = grid.isAlive(row, col) ? 0x000000 : 0xFFFFFF;
        Assert.assertEquals(
            "Exported pixel mismatch at row " + row + ", col " + col, expectedRgb, actualRgb);
      }
    }
  }

  private static final class RecordingSaveDialogService extends DialogService {
    private int saveDialogCallCount;
    private String lastSuggestedFileName;
    private File fileToReturn;
    private boolean saveErrorShown;

    @Override
    public File showSaveFileDialog(Window owner, File initialDirectory, String initialFileName) {
      this.saveDialogCallCount++;
      this.lastSuggestedFileName = initialFileName;
      return fileToReturn;
    }

    @Override
    public void showSaveError() {
      this.saveErrorShown = true;
    }
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
