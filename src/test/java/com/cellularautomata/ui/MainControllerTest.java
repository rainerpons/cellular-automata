package com.cellularautomata.ui;

import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.Rule;
import com.cellularautomata.engine.rules.TotalisticRule;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.NumericRuleParametersPanel;
import java.io.File;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Window;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/** Tests for {@link MainController}. */
public class MainControllerTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testElementaryRuleBoundsAndInstantiation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          MainController controller =
              createController(
                  WorkspaceConfig.ELEMENTARY,
                  new StubParametersPanel(8, 2, "30", SeedType.UNIFORM),
                  new RecordingDialogService());

          Assert.assertEquals(ElementaryRule.MIN_RULE_NUMBER, controller.getMinRule());
          Assert.assertEquals(ElementaryRule.MAX_RULE_NUMBER, controller.getMaxRule(2));
          Assert.assertEquals(ElementaryRule.MAX_RULE_NUMBER, controller.getMaxRule(5));

          Rule rule = controller.instantiateRule(110, 2);
          Assert.assertTrue(rule instanceof ElementaryRule);
          Assert.assertEquals(110, rule.getRuleNumber());
        });
  }

  @Test
  public void testTotalisticRuleBoundsAndInstantiation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          MainController controller =
              createController(
                  WorkspaceConfig.TOTALISTIC,
                  new StubParametersPanel(8, 3, "5", SeedType.UNIFORM),
                  new RecordingDialogService());

          Assert.assertEquals(TotalisticRule.MIN_RULE_NUMBER, controller.getMinRule());
          Assert.assertEquals(15, controller.getMaxRule(2));
          Assert.assertEquals(2186, controller.getMaxRule(3));
          Assert.assertEquals(1220703124, controller.getMaxRule(5));

          Rule rule = controller.instantiateRule(777, 3);
          Assert.assertTrue(rule instanceof TotalisticRule);
          Assert.assertEquals(777, rule.getRuleNumber());
        });
  }

  @Test
  public void testLifeLikeNumericRuleOperationsThrowUnsupportedOperationException() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          MainController controller =
              createController(
                  WorkspaceConfig.LIFE_LIKE,
                  new StubParametersPanel(8, 2, "0", SeedType.UNIFORM),
                  new RecordingDialogService());

          assertThrowsUnsupported(controller::getMinRule);
          assertThrowsUnsupported(() -> controller.getMaxRule(2));
          assertThrowsUnsupported(() -> controller.instantiateRule(0, 2));
        });
  }

  @Test
  public void testGenerateElementaryEnablesSaveAndRequestsExpectedFileName() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DisplayPanel displayPanel = new DisplayPanel();
          CommandsPanel commandsPanel = new CommandsPanel();
          RecordingDialogService dialogService = new RecordingDialogService();
          StubParametersPanel parameters = new StubParametersPanel(16, 2, "110", SeedType.SPARSE);

          new MainController(
              displayPanel, parameters, commandsPanel, dialogService, WorkspaceConfig.ELEMENTARY);

          Button generateButton = findButton(commandsPanel, "Generate automaton");
          Button saveButton = findButton(commandsPanel, "Save image");

          Assert.assertTrue(saveButton.isDisabled());
          generateButton.fire();

          Assert.assertFalse(saveButton.isDisabled());
          Assert.assertFalse(dialogService.ruleErrorShown);
          Assert.assertFalse(dialogService.statesErrorShown);

          saveButton.fire();
          Assert.assertNotNull(dialogService.lastSuggestedFileName);
          Assert.assertTrue(
              dialogService.lastSuggestedFileName.startsWith("elementary_rule110_size16_"));
          Assert.assertTrue(dialogService.lastSuggestedFileName.endsWith(".png"));
        });
  }

  @Test
  public void testValidationErrorsPreventGenerationAndShowAppropriateDialogs() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          // Invalid Elementary rule text
          CommandsPanel elementaryCommands = new CommandsPanel();
          RecordingDialogService elementaryDialogs = new RecordingDialogService();
          new MainController(
              new DisplayPanel(),
              new StubParametersPanel(8, 2, "256", SeedType.UNIFORM),
              elementaryCommands,
              elementaryDialogs,
              WorkspaceConfig.ELEMENTARY);

          findButton(elementaryCommands, "Generate automaton").fire();
          Assert.assertTrue(elementaryDialogs.ruleErrorShown);
          Assert.assertEquals(0, elementaryDialogs.lastMinRule);
          Assert.assertEquals(255, elementaryDialogs.lastMaxRule);
          Assert.assertTrue(findButton(elementaryCommands, "Save image").isDisabled());

          // Out-of-bounds Totalistic state count
          CommandsPanel totalisticCommands = new CommandsPanel();
          RecordingDialogService totalisticDialogs = new RecordingDialogService();
          new MainController(
              new DisplayPanel(),
              new StubParametersPanel(8, 6, "5", SeedType.UNIFORM),
              totalisticCommands,
              totalisticDialogs,
              WorkspaceConfig.TOTALISTIC);

          findButton(totalisticCommands, "Generate automaton").fire();
          Assert.assertTrue(totalisticDialogs.statesErrorShown);
          Assert.assertEquals(TotalisticRule.MIN_STATES, totalisticDialogs.lastMinStates);
          Assert.assertEquals(TotalisticRule.MAX_STATES, totalisticDialogs.lastMaxStates);
          Assert.assertFalse(totalisticDialogs.ruleErrorShown);
          Assert.assertTrue(findButton(totalisticCommands, "Save image").isDisabled());
        });
  }

  private static MainController createController(
      WorkspaceConfig config,
      NumericRuleParametersPanel parametersPanel,
      DialogService dialogService) {
    return new MainController(
        new DisplayPanel(), parametersPanel, new CommandsPanel(), dialogService, config);
  }

  private static Button findButton(CommandsPanel commandsPanel, String text) {
    for (Node child : commandsPanel.getChildren()) {
      if (child instanceof Button button && text.equals(button.getText())) {
        return button;
      }
    }
    throw new AssertionError("Button not found: " + text);
  }

  private static void assertThrowsUnsupported(Runnable action) {
    try {
      action.run();
      Assert.fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
      // Expected
    }
  }

  private record StubParametersPanel(int size, int states, String ruleText, SeedType seedType)
      implements NumericRuleParametersPanel {
    @Override
    public int getSizeValue() {
      return size;
    }

    @Override
    public int getStatesValue() {
      return states;
    }

    @Override
    public String getRuleText() {
      return ruleText;
    }

    @Override
    public SeedType getSeedType() {
      return seedType;
    }
  }

  private static final class RecordingDialogService extends DialogService {
    private boolean ruleErrorShown;
    private int lastMinRule;
    private int lastMaxRule;
    private boolean statesErrorShown;
    private int lastMinStates;
    private int lastMaxStates;
    private String lastSuggestedFileName;

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
    public File showSaveFileDialog(Window owner, File initialDirectory, String initialFileName) {
      this.lastSuggestedFileName = initialFileName;
      return null;
    }
  }
}
