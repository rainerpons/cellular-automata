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

          assertThrowsIllegalArgument(() -> new LifeLikeController(null, params, toolbar));
          assertThrowsIllegalArgument(() -> new LifeLikeController(display, null, toolbar));
          assertThrowsIllegalArgument(() -> new LifeLikeController(display, params, null));
        });
  }

  @Test
  public void testValidConfigurationCreatesSimulationAtGenerationZeroWithRuleAndRenderedGrid() {
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
          controller.generateInitial();

          Assert.assertTrue(controller.hasActiveSimulation());
          LifeLikeSimulation sim = controller.getActiveSimulation();
          Assert.assertNotNull(sim);
          Assert.assertEquals(0, sim.getGeneration());

          LifeLikeRule rule = sim.getRule();
          Assert.assertEquals(Set.of(3), rule.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), rule.getSurvivalCounts());

          Grid2D currentGrid = sim.getCurrentGrid();
          Assert.assertEquals(8, currentGrid.getSize());

          Image activeImage = controller.getActiveImage();
          Assert.assertNotNull(activeImage);
          Assert.assertSame(activeImage, displayPanel.getAutomatonImage());
          Assert.assertEquals(400, (int) activeImage.getWidth());
          Assert.assertEquals(400, (int) activeImage.getHeight());

          Assert.assertFalse(displayPanel.isDisplayingError());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());

          // Verify pixel colors against initial grid content
          PixelReader pixelReader = activeImage.getPixelReader();
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
  public void testEditingFormValuesDoesNotMutateActiveConfigurationGridOrCurrentImage() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          parametersPanel.setSizeValue(16);
          parametersPanel.selectPreset(LifeLikePreset.HIGH_LIFE);
          parametersPanel.selectSeedType(SeedType.SPARSE);

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.generateInitial();

          LifeLikeConfiguration originalConfig = controller.getActiveConfiguration();
          Grid2D originalGrid = controller.getActiveSimulation().getCurrentGrid();
          Image originalImage = controller.getActiveImage();

          // Mutate form values on the parameters panel
          parametersPanel.setSizeValue(64);
          parametersPanel.selectPreset(LifeLikePreset.SEEDS);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          // Verify active state remains completely unchanged
          Assert.assertEquals(originalConfig, controller.getActiveConfiguration());
          Assert.assertEquals(originalGrid, controller.getActiveSimulation().getCurrentGrid());
          Assert.assertSame(originalImage, controller.getActiveImage());
          Assert.assertSame(originalImage, displayPanel.getAutomatonImage());
        });
  }

  @Test
  public void testReplacingSimulationUsesUpdatedValues() {
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
          controller.generateInitial();

          // Update form values
          parametersPanel.setSizeValue(32);
          parametersPanel.selectPreset(LifeLikePreset.SEEDS);
          parametersPanel.selectSeedType(SeedType.ALTERNATING);

          controller.initializeSimulation();

          Assert.assertEquals(32, controller.getActiveSimulation().getCurrentGrid().getSize());
          Assert.assertEquals(
              Set.of(2), controller.getActiveSimulation().getRule().getBirthCounts());
          Assert.assertEquals(
              Set.of(), controller.getActiveSimulation().getRule().getSurvivalCounts());
          Assert.assertEquals(0, controller.getActiveSimulation().getGeneration());
          Assert.assertEquals(32, controller.getActiveConfiguration().size());
          Assert.assertEquals(SeedType.ALTERNATING, controller.getActiveConfiguration().seedType());
        });
  }

  @Test
  public void testUnchangedConfigurationRetainsExistingSimulationUnlessForced() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.generateInitial();

          LifeLikeSimulation sim1 = controller.getActiveSimulation();
          Image img1 = controller.getActiveImage();

          // Calling initializeSimulation(false) when configuration is identical retains active
          // instance
          controller.initializeSimulation(false);
          Assert.assertSame(sim1, controller.getActiveSimulation());
          Assert.assertSame(img1, controller.getActiveImage());

          // Calling reinitializeSimulation (forceReinitialize = true) creates a fresh simulation
          controller.reinitializeSimulation();
          Assert.assertNotSame(sim1, controller.getActiveSimulation());
        });
  }

  @Test
  public void testResetSimulationReturnsToGenerationZeroAndRerendersGrid() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.generateInitial();

          LifeLikeSimulation sim = controller.getActiveSimulation();
          Grid2D gen0Grid = sim.getCurrentGrid();

          sim.step();
          Assert.assertEquals(1, sim.getGeneration());

          controller.resetSimulation();
          Assert.assertEquals(0, sim.getGeneration());
          Assert.assertEquals(gen0Grid, sim.getCurrentGrid());
          Assert.assertNotNull(controller.getActiveImage());
          Assert.assertSame(controller.getActiveImage(), displayPanel.getAutomatonImage());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
        });
  }

  @Test
  public void testGenerationFailureClearsActiveStateShowsErrorAndDisablesToolbar() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          // Initialize successfully first
          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.generateInitial();
          Assert.assertTrue(controller.hasActiveSimulation());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());

          // Inject failing simulation factory
          controller.setSimulationFactory(
              (rule, size, seedType, density) -> {
                throw new RuntimeException("Simulated generation failure");
              });

          parametersPanel.setSizeValue(64);
          controller.initializeSimulation();

          Assert.assertNull(controller.getActiveSimulation());
          Assert.assertNull(controller.getActiveConfiguration());
          Assert.assertNull(controller.getActiveImage());
          Assert.assertFalse(controller.hasActiveSimulation());

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.GENERATION_ERROR_PRIMARY, displayPanel.getPrimaryErrorMessage());
          Assert.assertEquals(
              DisplayPanel.ERROR_SECONDARY, displayPanel.getSecondaryErrorMessage());
          Assert.assertEquals(
              DisplayPanel.GENERATION_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
              displayPanel.getErrorMessage());

          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
        });
  }

  @Test
  public void testRenderingFailureClearsActiveStateShowsErrorAndDisablesToolbar() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          // Initialize successfully first
          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.generateInitial();
          Assert.assertTrue(controller.hasActiveSimulation());

          // Inject failing grid renderer
          controller.setGridRenderer(
              grid -> {
                throw new RuntimeException("Simulated rendering failure");
              });

          parametersPanel.setSizeValue(64);
          controller.initializeSimulation();

          Assert.assertNull(controller.getActiveSimulation());
          Assert.assertNull(controller.getActiveConfiguration());
          Assert.assertNull(controller.getActiveImage());
          Assert.assertFalse(controller.hasActiveSimulation());

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.RENDERING_ERROR_PRIMARY, displayPanel.getPrimaryErrorMessage());
          Assert.assertEquals(
              DisplayPanel.ERROR_SECONDARY, displayPanel.getSecondaryErrorMessage());
          Assert.assertEquals(
              DisplayPanel.RENDERING_ERROR_PRIMARY + "\n" + DisplayPanel.ERROR_SECONDARY,
              displayPanel.getErrorMessage());

          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
        });
  }

  @Test
  public void testSuccessfulRetryRestoresImageClearsErrorAndEnablesToolbar() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          // Inject failing factory
          LifeLikeController.SimulationFactory failingFactory =
              (rule, size, seedType, density) -> {
                throw new RuntimeException("Simulated failure");
              };

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar, failingFactory, null);
          controller.generateInitial();

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());

          // Restore normal factory and re-generate
          controller.setSimulationFactory(null);
          // Null resets to default factory
          controller = new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.generateInitial();

          Assert.assertFalse(displayPanel.isDisplayingError());
          Assert.assertNotNull(displayPanel.getAutomatonImage());
          Assert.assertNotNull(controller.getActiveSimulation());
          Assert.assertFalse(toolbar.getSaveButton().isDisabled());
          Assert.assertFalse(toolbar.getResetButton().isDisabled());
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
          controller.initializeSimulation();
          Image image4 = controller.getActiveImage();
          Assert.assertEquals(400.0, image4.getWidth(), 0.001);
          Assert.assertEquals(400.0, image4.getHeight(), 0.001);

          // Test max size 128
          parametersPanel.setSizeValue(128);
          controller.initializeSimulation();
          Image image128 = controller.getActiveImage();
          Assert.assertEquals(400.0, image128.getWidth(), 0.001);
          Assert.assertEquals(400.0, image128.getHeight(), 0.001);
        });
  }

  @Test
  public void testResetWithFailingRendererHandlesRenderingFailure() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
          LifeLikeToolbar toolbar = new LifeLikeToolbar();

          LifeLikeController controller =
              new LifeLikeController(displayPanel, parametersPanel, toolbar);
          controller.generateInitial();

          controller.getActiveSimulation().step();

          // Set failing renderer before reset
          controller.setGridRenderer(
              grid -> {
                throw new RuntimeException("Simulated reset render failure");
              });

          controller.resetSimulation();

          Assert.assertTrue(displayPanel.isDisplayingError());
          Assert.assertEquals(
              DisplayPanel.RENDERING_ERROR_PRIMARY, displayPanel.getPrimaryErrorMessage());
          Assert.assertTrue(toolbar.getSaveButton().isDisabled());
          Assert.assertTrue(toolbar.getResetButton().isDisabled());
        });
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
