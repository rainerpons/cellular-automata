package com.cellularautomata.ui.controllers;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.ui.DialogService;
import com.cellularautomata.ui.JavaFxTestHelper;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ElementaryParametersPanel;
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

/** Tests for {@link ElementaryController}. */
public class ElementaryControllerTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testConstructorsAndNullArgumentValidation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel display = new DisplayPanel();
          ElementaryParametersPanel params = new ElementaryParametersPanel();
          CommandsPanel commands = new CommandsPanel();
          DialogService dialogService = new DialogService();

          assertThrowsIllegalArgument(() -> new ElementaryController(null, params, commands));
          assertThrowsIllegalArgument(() -> new ElementaryController(display, null, commands));
          assertThrowsIllegalArgument(() -> new ElementaryController(display, params, null));

          assertThrowsIllegalArgument(
              () -> new ElementaryController(null, params, commands, dialogService));
          assertThrowsIllegalArgument(
              () -> new ElementaryController(display, null, commands, dialogService));
          assertThrowsIllegalArgument(
              () -> new ElementaryController(display, params, null, dialogService));
          assertThrowsIllegalArgument(
              () -> new ElementaryController(display, params, commands, null));

          ElementaryController controller = new ElementaryController(display, params, commands);
          Assert.assertNotNull(controller);
          Assert.assertNull(display.getAutomatonImage());
          Assert.assertTrue(findButton(commands, "Save image").isDisabled());

          findButton(commands, "Generate automaton").fire();
          Assert.assertNotNull(display.getAutomatonImage());
          Assert.assertFalse(findButton(commands, "Save image").isDisabled());
        });
  }

  @Test
  public void testGenerateAcrossRulesSizesAndSeedTypesProducesBinaryDisplayImageAndEnablesSave() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          int[] rules = {ElementaryRule.MIN_RULE_NUMBER, 30, 110, ElementaryRule.MAX_RULE_NUMBER};
          int[] sizes = {4, 16, 64};
          SeedType[] seedTypes = {SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING};

          for (int rule : rules) {
            for (int size : sizes) {
              for (SeedType seedType : seedTypes) {
                DisplayPanel displayPanel = new DisplayPanel();
                ElementaryParametersPanel parametersPanel = new ElementaryParametersPanel();
                CommandsPanel commandsPanel = new CommandsPanel();
                RecordingDialogService dialogService = new RecordingDialogService();

                new ElementaryController(
                    displayPanel, parametersPanel, commandsPanel, dialogService);

                Button generateButton = findButton(commandsPanel, "Generate automaton");
                Button saveButton = findButton(commandsPanel, "Save image");

                Assert.assertNull(displayPanel.getAutomatonImage());
                Assert.assertTrue(saveButton.isDisabled());

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
                Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, (int) image.getWidth());
                Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, (int) image.getHeight());
                assertStrictlyBinaryPixels(image);
              }
            }
          }
        });
  }

  @Test
  public void testInvalidRuleInputsShowFixedRuleErrorAndDoNotUpdateDisplayOrEnableSave() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          String[] invalidRules = {"-1", "256", "1000", "99999999999999999999", "abc", ""};

          for (String invalidRule : invalidRules) {
            DisplayPanel displayPanel = new DisplayPanel();
            ElementaryParametersPanel parametersPanel = new ElementaryParametersPanel();
            CommandsPanel commandsPanel = new CommandsPanel();
            RecordingDialogService dialogService = new RecordingDialogService();

            new ElementaryController(displayPanel, parametersPanel, commandsPanel, dialogService);

            parametersPanel.setRuleText("");
            parametersPanel.setRuleText(invalidRule);
            findButton(commandsPanel, "Generate automaton").fire();

            Assert.assertTrue(
                "Expected rule error for input: " + invalidRule, dialogService.ruleErrorShown);
            Assert.assertEquals(ElementaryRule.MIN_RULE_NUMBER, dialogService.lastMinRule);
            Assert.assertEquals(ElementaryRule.MAX_RULE_NUMBER, dialogService.lastMaxRule);
            Assert.assertFalse(dialogService.statesErrorShown);
            Assert.assertNull(displayPanel.getAutomatonImage());
            Assert.assertTrue(findButton(commandsPanel, "Save image").isDisabled());
          }
        });
  }

  @Test
  public void testSaveWorkflowExportsPngHandlesCancelAndReportsIoException() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          try {
            Path tempDir = Files.createTempDirectory("elementary_save_test");
            File targetFile = tempDir.resolve("elementary_exported.png").toFile();
            targetFile.deleteOnExit();
            tempDir.toFile().deleteOnExit();

            DisplayPanel displayPanel = new DisplayPanel();
            ElementaryParametersPanel parametersPanel = new ElementaryParametersPanel();
            CommandsPanel commandsPanel = new CommandsPanel();
            RecordingDialogService dialogService = new RecordingDialogService();

            ElementaryController controller =
                new ElementaryController(
                    displayPanel, parametersPanel, commandsPanel, dialogService);

            // Calling save before generation is a no-op
            controller.saveAutomatonImage();
            Assert.assertEquals(0, dialogService.saveDialogCallCount);

            // Configure rule 110, size 16, alternating seed and generate
            parametersPanel.setRuleText("110");
            parametersPanel.setSizeValue(16);
            parametersPanel.selectSeedType(SeedType.ALTERNATING);
            findButton(commandsPanel, "Generate automaton").fire();

            Button saveButton = findButton(commandsPanel, "Save image");
            Assert.assertFalse(saveButton.isDisabled());

            // Modify size slider and rule text AFTER generation without regenerating;
            // save filename must still reflect the generated rule (110) and size (16)
            parametersPanel.setSizeValue(64);
            parametersPanel.setRuleText("30");

            // 1. Successful save to temporary file
            dialogService.fileToReturn = targetFile;
            saveButton.fire();

            Assert.assertEquals(1, dialogService.saveDialogCallCount);
            Assert.assertNotNull(dialogService.lastSuggestedFileName);
            Assert.assertTrue(
                "Unexpected filename: " + dialogService.lastSuggestedFileName,
                dialogService.lastSuggestedFileName.matches(
                    "^elementary_rule110_size16_\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2}-\\d{2}\\.png$"));
            Assert.assertFalse(dialogService.saveErrorShown);
            Assert.assertTrue(Files.exists(targetFile.toPath()));

            BufferedImage savedImage = ImageIO.read(targetFile);
            Assert.assertNotNull(savedImage);
            Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, savedImage.getWidth());
            Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, savedImage.getHeight());

            // 2. Cancelled save (dialog returns null) does not show error
            dialogService.fileToReturn = null;
            saveButton.fire();
            Assert.assertEquals(2, dialogService.saveDialogCallCount);
            Assert.assertFalse(dialogService.saveErrorShown);

            // 3. IOException during save (passing existing directory as file target) triggers
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

  private static void assertStrictlyBinaryPixels(Image image) {
    PixelReader reader = image.getPixelReader();
    Assert.assertNotNull(reader);
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    for (int y = 0; y < height; y += 25) {
      for (int x = 0; x < width; x += 25) {
        int rgb = reader.getArgb(x, y) & 0xFFFFFF;
        Assert.assertTrue(
            "Expected black or white pixel but found: " + Integer.toHexString(rgb),
            rgb == 0x000000 || rgb == 0xFFFFFF);
      }
    }
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
