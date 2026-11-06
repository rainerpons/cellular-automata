package com.cellularautomata.ui.controllers;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.TotalisticRule;
import com.cellularautomata.image.AutomatonImageUtils;
import com.cellularautomata.ui.JavaFxTestHelper;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.TotalisticParametersPanel;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/** Tests for {@link TotalisticController}. */
public class TotalisticControllerTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testConstructorsAndNullArgumentValidation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel display = new DisplayPanel();
          TotalisticParametersPanel params = new TotalisticParametersPanel();
          CommandsPanel commands = new CommandsPanel();
          DialogService dialogService = new DialogService();

          assertThrowsIllegalArgument(() -> new TotalisticController(null, params, commands));
          assertThrowsIllegalArgument(() -> new TotalisticController(display, null, commands));
          assertThrowsIllegalArgument(() -> new TotalisticController(display, params, null));

          assertThrowsIllegalArgument(
              () -> new TotalisticController(null, params, commands, dialogService));
          assertThrowsIllegalArgument(
              () -> new TotalisticController(display, null, commands, dialogService));
          assertThrowsIllegalArgument(
              () -> new TotalisticController(display, params, null, dialogService));
          assertThrowsIllegalArgument(
              () -> new TotalisticController(display, params, commands, (DialogService) null));

          AutomatonImageService imageService = new AutomatonImageService(dialogService);

          assertThrowsIllegalArgument(
              () -> new TotalisticController(null, params, commands, dialogService, imageService));
          assertThrowsIllegalArgument(
              () -> new TotalisticController(display, null, commands, dialogService, imageService));
          assertThrowsIllegalArgument(
              () -> new TotalisticController(display, params, null, dialogService, imageService));
          assertThrowsIllegalArgument(
              () -> new TotalisticController(display, params, commands, null, imageService));
          assertThrowsIllegalArgument(
              () -> new TotalisticController(display, params, commands, dialogService, null));

          TotalisticController controller = new TotalisticController(display, params, commands);
          Assert.assertNotNull(controller);
          Assert.assertNull(display.getAutomatonImage());
          Assert.assertTrue(findButton(commands, "Save image").isDisabled());

          TotalisticController controllerWith5Args =
              new TotalisticController(display, params, commands, dialogService, imageService);
          Assert.assertNotNull(controllerWith5Args);

          findButton(commands, "Generate automaton").fire();
          Assert.assertNotNull(display.getAutomatonImage());
          Assert.assertFalse(findButton(commands, "Save image").isDisabled());
        });
  }

  @Test
  public void
      testGenerateAcrossStatesRuleBoundsSeedTypesAndSizesProducesMultiStateGrayscaleImage() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          int[][] stateAndMaxRulePairs = {
            {2, 15},
            {3, 2186},
            {4, 1048575},
            {5, 1220703124}
          };
          int[] sizes = {4, 16};
          SeedType[] seedTypes = {SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING};

          for (int[] pair : stateAndMaxRulePairs) {
            int states = pair[0];
            int maxRule = pair[1];
            Assert.assertEquals(maxRule, TotalisticRule.calculateMaxRule(states));

            int allOnesRule = maxRule / (states - 1);
            int[] rulesToTest = {TotalisticRule.MIN_RULE_NUMBER, allOnesRule, maxRule};

            for (int rule : rulesToTest) {
              for (int size : sizes) {
                for (SeedType seedType : seedTypes) {
                  DisplayPanel displayPanel = new DisplayPanel();
                  TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
                  CommandsPanel commandsPanel = new CommandsPanel();
                  RecordingDialogService dialogService = new RecordingDialogService();

                  new TotalisticController(
                      displayPanel, parametersPanel, commandsPanel, dialogService);

                  Button generateButton = findButton(commandsPanel, "Generate automaton");
                  Button saveButton = findButton(commandsPanel, "Save image");

                  Assert.assertNull(displayPanel.getAutomatonImage());
                  Assert.assertTrue(saveButton.isDisabled());

                  parametersPanel.setStatesValue(states);
                  parametersPanel.setRuleText(Integer.toString(rule));
                  parametersPanel.setSizeValue(size);
                  parametersPanel.selectSeedType(seedType);

                  generateButton.fire();

                  Assert.assertFalse(dialogService.ruleErrorShown);
                  Assert.assertFalse(dialogService.statesErrorShown);
                  Assert.assertFalse(dialogService.saveErrorShown);
                  Assert.assertFalse(saveButton.isDisabled());

                  Image image = displayPanel.getAutomatonImage();
                  Assert.assertNotNull(image);
                  Assert.assertEquals(
                      AutomatonImageUtils.DISPLAY_IMAGE_SIZE, (int) image.getWidth());
                  Assert.assertEquals(
                      AutomatonImageUtils.DISPLAY_IMAGE_SIZE, (int) image.getHeight());

                  if (states > 2 && rule == allOnesRule) {
                    assertContainsIntermediateGrayscalePixels(image, states);
                  }
                }
              }
            }
          }
        });
  }

  @Test
  public void testOutOfBoundsAndUncommittedStateInputsShowStatesErrorAndValidUncommittedSucceeds() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          int[] invalidStatesValues = {0, 1, 6, 10};

          for (int states : invalidStatesValues) {
            DisplayPanel displayPanel = new DisplayPanel();
            TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
            CommandsPanel commandsPanel = new CommandsPanel();
            RecordingDialogService dialogService = new RecordingDialogService();

            new TotalisticController(displayPanel, parametersPanel, commandsPanel, dialogService);

            parametersPanel.setStatesValue(states);
            parametersPanel.setRuleText("5");
            findButton(commandsPanel, "Generate automaton").fire();

            Assert.assertTrue(
                "Expected states error for states=" + states, dialogService.statesErrorShown);
            Assert.assertEquals(TotalisticRule.MIN_STATES, dialogService.lastMinStates);
            Assert.assertEquals(TotalisticRule.MAX_STATES, dialogService.lastMaxStates);
            Assert.assertFalse(dialogService.ruleErrorShown);
            Assert.assertNull(displayPanel.getAutomatonImage());
            Assert.assertTrue(findButton(commandsPanel, "Save image").isDisabled());
          }

          String[] invalidStatesTexts = {"", "0", "1", "6", "abc", "99999999999999999999"};

          for (String statesText : invalidStatesTexts) {
            DisplayPanel displayPanel = new DisplayPanel();
            TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
            CommandsPanel commandsPanel = new CommandsPanel();
            RecordingDialogService dialogService = new RecordingDialogService();

            new TotalisticController(displayPanel, parametersPanel, commandsPanel, dialogService);

            parametersPanel.setStatesText("");
            parametersPanel.setStatesText(statesText);
            parametersPanel.setRuleText("5");
            findButton(commandsPanel, "Generate automaton").fire();

            Assert.assertTrue(
                "Expected states error for statesText=" + statesText,
                dialogService.statesErrorShown);
            Assert.assertEquals(TotalisticRule.MIN_STATES, dialogService.lastMinStates);
            Assert.assertEquals(TotalisticRule.MAX_STATES, dialogService.lastMaxStates);
            Assert.assertFalse(dialogService.ruleErrorShown);
            Assert.assertNull(displayPanel.getAutomatonImage());
            Assert.assertTrue(findButton(commandsPanel, "Save image").isDisabled());
          }

          // Valid uncommitted manual text entry ("3") while committed spinner value is
          // still 2
          // should allow rules up to 2186 (e.g. "100") and generate a 3-state automaton
          DisplayPanel displayPanel = new DisplayPanel();
          TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
          CommandsPanel commandsPanel = new CommandsPanel();
          RecordingDialogService dialogService = new RecordingDialogService();

          new TotalisticController(displayPanel, parametersPanel, commandsPanel, dialogService);

          parametersPanel.setStatesText("3");
          parametersPanel.setRuleText("100");
          parametersPanel.setSizeValue(8);
          findButton(commandsPanel, "Generate automaton").fire();

          Assert.assertFalse(dialogService.statesErrorShown);
          Assert.assertFalse(dialogService.ruleErrorShown);
          Assert.assertNotNull(displayPanel.getAutomatonImage());
          Assert.assertFalse(findButton(commandsPanel, "Save image").isDisabled());

          findButton(commandsPanel, "Save image").fire();
          Assert.assertEquals(
              "totalistic_states3_rule100_size8.png", dialogService.lastSuggestedFileName);
        });
  }

  @Test
  public void testInvalidRuleForSpecificStateCountsShowsStateDependentRuleBoundsError() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          Object[][] invalidStateRuleCases = {
            {2, "16", 15},
            {2, "-1", 15},
            {2, "abc", 15},
            {2, "", 15},
            {3, "2187", 2186},
            {4, "1048576", 1048575},
            {5, "1220703125", 1220703124}
          };

          for (Object[] testCase : invalidStateRuleCases) {
            int states = (Integer) testCase[0];
            String invalidRule = (String) testCase[1];
            int expectedMaxRule = (Integer) testCase[2];

            DisplayPanel displayPanel = new DisplayPanel();
            TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
            CommandsPanel commandsPanel = new CommandsPanel();
            RecordingDialogService dialogService = new RecordingDialogService();

            new TotalisticController(displayPanel, parametersPanel, commandsPanel, dialogService);

            parametersPanel.setStatesValue(states);
            parametersPanel.setRuleText("");
            parametersPanel.setRuleText(invalidRule);
            findButton(commandsPanel, "Generate automaton").fire();

            Assert.assertTrue(
                "Expected rule error for states=" + states + ", rule=" + invalidRule,
                dialogService.ruleErrorShown);
            Assert.assertEquals(TotalisticRule.MIN_RULE_NUMBER, dialogService.lastMinRule);
            Assert.assertEquals(expectedMaxRule, dialogService.lastMaxRule);
            Assert.assertFalse(dialogService.statesErrorShown);
            Assert.assertNull(displayPanel.getAutomatonImage());
            Assert.assertTrue(findButton(commandsPanel, "Save image").isDisabled());
          }
        });
  }

  @Test
  public void
      testSaveWorkflowUsesGeneratedRuleStatesExportsPngHandlesCancelAndReportsIoException() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          try {
            Path tempDir = Files.createTempDirectory("totalistic_save_test");
            File targetFile = tempDir.resolve("totalistic_exported.png").toFile();
            targetFile.deleteOnExit();
            tempDir.toFile().deleteOnExit();

            DisplayPanel displayPanel = new DisplayPanel();
            TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
            CommandsPanel commandsPanel = new CommandsPanel();
            RecordingDialogService dialogService = new RecordingDialogService();

            TotalisticController controller =
                new TotalisticController(
                    displayPanel, parametersPanel, commandsPanel, dialogService);

            // Calling save before generation is a no-op
            controller.saveAutomatonImage();
            Assert.assertEquals(0, dialogService.saveDialogCallCount);

            // Configure states=3, rule=777, size=16, alternating seed and generate
            parametersPanel.setStatesValue(3);
            parametersPanel.setRuleText("777");
            parametersPanel.setSizeValue(16);
            parametersPanel.selectSeedType(SeedType.ALTERNATING);
            findButton(commandsPanel, "Generate automaton").fire();

            Button saveButton = findButton(commandsPanel, "Save image");
            Assert.assertFalse(saveButton.isDisabled());

            // Edit size slider, states spinner, and rule text AFTER generation without
            // regenerating; save filename must still reflect the generated rule (states=3,
            // rule=777) and size (16)
            parametersPanel.setSizeValue(64);
            parametersPanel.setStatesValue(4);
            parametersPanel.setRuleText("12");

            // 1. Successful save to temporary file
            dialogService.fileToReturn = targetFile;
            saveButton.fire();

            Assert.assertEquals(1, dialogService.saveDialogCallCount);
            Assert.assertEquals(
                "totalistic_states3_rule777_size16.png", dialogService.lastSuggestedFileName);
            Assert.assertFalse(dialogService.saveErrorShown);
            Assert.assertTrue(Files.exists(targetFile.toPath()));

            BufferedImage savedImage = ImageIO.read(targetFile);
            Assert.assertNotNull(savedImage);
            Assert.assertEquals(AutomatonImageUtils.DISPLAY_IMAGE_SIZE, savedImage.getWidth());
            Assert.assertEquals(AutomatonImageUtils.DISPLAY_IMAGE_SIZE, savedImage.getHeight());

            // 2. Cancelled save (dialog returns null) does not show error
            dialogService.fileToReturn = null;
            saveButton.fire();
            Assert.assertEquals(2, dialogService.saveDialogCallCount);
            Assert.assertFalse(dialogService.saveErrorShown);

            // 3. IOException during save (passing existing directory as file target)
            // triggers
            // showSaveError
            dialogService.fileToReturn = tempDir.toFile();
            saveButton.fire();
            Assert.assertEquals(3, dialogService.saveDialogCallCount);
            Assert.assertTrue(dialogService.saveErrorShown);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Test
  public void testSaveWorkflowWithInjectedImageServiceCancellationAndFailure() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          try {
            Path tempDir = Files.createTempDirectory("totalistic_injected_test");
            tempDir.toFile().deleteOnExit();

            DisplayPanel displayPanel = new DisplayPanel();
            TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
            CommandsPanel commandsPanel = new CommandsPanel();
            RecordingDialogService dialogService = new RecordingDialogService();
            RecordingImageService imageService = new RecordingImageService(dialogService);

            TotalisticController controller =
                new TotalisticController(
                    displayPanel, parametersPanel, commandsPanel, dialogService, imageService);

            // Calling save before generation is a no-op
            controller.saveAutomatonImage();
            Assert.assertEquals(0, imageService.saveImageCallCount);

            // Configure states=3, rule=100, size=8 and generate
            parametersPanel.setStatesValue(3);
            parametersPanel.setRuleText("100");
            parametersPanel.setSizeValue(8);
            findButton(commandsPanel, "Generate automaton").fire();

            Button saveButton = findButton(commandsPanel, "Save image");
            Assert.assertFalse(saveButton.isDisabled());

            // 1. Save Cancellation via injected service
            dialogService.fileToReturn = null;
            saveButton.fire();

            Assert.assertEquals(1, imageService.saveImageCallCount);
            Assert.assertEquals(
                "totalistic_states3_rule100_size8.png", imageService.lastSuggestedFileName);
            Assert.assertNotNull(imageService.lastImage);
            Assert.assertFalse(
                "showSaveError should not be called on cancel", dialogService.saveErrorShown);

            // 2. Save Failure via injected service (file in non-existent directory triggers
            // IOException)
            File missingDirTarget = new File(tempDir.toFile(), "missing_subdir/exported.png");
            dialogService.fileToReturn = missingDirTarget;
            saveButton.fire();

            Assert.assertEquals(2, imageService.saveImageCallCount);
            Assert.assertTrue(
                "showSaveError must be called when writer fails", dialogService.saveErrorShown);

            // 3. Save Success via injected service
            File validTarget = new File(tempDir.toFile(), "successful_export.png");
            validTarget.deleteOnExit();
            dialogService.fileToReturn = validTarget;
            dialogService.saveErrorShown = false;
            saveButton.fire();

            Assert.assertEquals(3, imageService.saveImageCallCount);
            Assert.assertFalse(dialogService.saveErrorShown);
            Assert.assertTrue(validTarget.exists());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private static final class RecordingImageService extends AutomatonImageService {
    private int saveImageCallCount;
    private String lastSuggestedFileName;
    private BufferedImage lastImage;

    RecordingImageService(DialogService dialogService) {
      super(dialogService);
    }

    @Override
    public boolean saveImage(Window owner, BufferedImage image, String suggestedFileName) {
      this.saveImageCallCount++;
      this.lastSuggestedFileName = suggestedFileName;
      this.lastImage = image;
      return super.saveImage(owner, image, suggestedFileName);
    }
  }

  private static void assertContainsIntermediateGrayscalePixels(Image image, int states) {
    PixelReader reader = image.getPixelReader();
    Assert.assertNotNull(reader);
    int expectedStateOneRgb = AutomatonImageUtils.getRgbForState(1, states);
    Assert.assertNotEquals(0x000000, expectedStateOneRgb);
    Assert.assertNotEquals(0xFFFFFF, expectedStateOneRgb);

    // Generation 1+ (bottom half of the image) under the all-ones rule is entirely
    // state 1
    int sampleX = AutomatonImageUtils.DISPLAY_IMAGE_SIZE / 2;
    int sampleY = (AutomatonImageUtils.DISPLAY_IMAGE_SIZE * 3) / 4;
    int actualRgb = reader.getArgb(sampleX, sampleY) & 0xFFFFFF;
    Assert.assertEquals(expectedStateOneRgb, actualRgb);
  }

  private static Button findButton(CommandsPanel commandsPanel, String text) {
    for (Node child : commandsPanel.getChildren()) {
      if (child instanceof Button button && text.equals(button.getText())) {
        return button;
      }
    }
    throw new AssertionError("Button not found: " + text);
  }

  private static void assertThrowsIllegalArgument(Runnable action) {
    try {
      action.run();
      Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }
  }

  private static final class RecordingDialogService extends DialogService {
    private boolean ruleErrorShown;
    private int lastMinRule;
    private int lastMaxRule;
    private boolean statesErrorShown;
    private int lastMinStates;
    private int lastMaxStates;
    private boolean saveErrorShown;
    private int saveDialogCallCount;
    private String lastSuggestedFileName;
    private File fileToReturn;

    @Override
    public void showRuleError(int minRule, int maxRule) {
      this.ruleErrorShown = true;
      this.lastMinRule = minRule;
      this.lastMaxRule = maxRule;
    }

    @Override
    public void showStatesError(int minStates, int maxStates) {
      this.statesErrorShown = true;
      this.lastMinStates = minStates;
      this.lastMaxStates = maxStates;
    }

    @Override
    public void showSaveError() {
      this.saveErrorShown = true;
    }

    @Override
    public File showSaveFileDialog(Window owner, File initialDirectory, String initialFileName) {
      this.saveDialogCallCount++;
      this.lastSuggestedFileName = initialFileName;
      return fileToReturn;
    }
  }
}
