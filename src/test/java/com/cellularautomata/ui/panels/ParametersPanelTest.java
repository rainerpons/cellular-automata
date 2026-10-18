package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.TotalisticRule;
import com.cellularautomata.ui.JavaFxTestHelper;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParametersPanelTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testElementaryParametersPanelDefaultsLayoutAndTooltips() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          ElementaryParametersPanel panel = new ElementaryParametersPanel();
          ParametersPanel grid = panel.getPanel();

          Assert.assertEquals(8, panel.getSizeValue());
          Assert.assertEquals("Size (8)", panel.getSizeLabelText());
          Assert.assertEquals(ElementaryRule.STATE_COUNT, panel.getStatesValue());
          Assert.assertEquals(
              Integer.toString(ElementaryRule.DEFAULT_RULE_NUMBER), panel.getRuleText());
          Assert.assertEquals(SeedType.UNIFORM, panel.getSeedType());
          Assert.assertEquals(
              List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
              panel.getSeedComboBox().getItems());

          panel.setSizeValue(64);
          Assert.assertEquals(64, panel.getSizeValue());
          Assert.assertEquals("Size (64)", panel.getSizeLabelText());

          Label sizeLabel = grid.findLabelByPrefix("Size (");
          Label ruleLabel = grid.findLabelByPrefix("Rule number");
          Label seedLabel = grid.findLabelByPrefix("Seed type");
          Assert.assertNull(grid.findLabelByPrefix("States"));

          Assert.assertEquals(Integer.valueOf(1), GridPane.getRowIndex(sizeLabel));
          Assert.assertEquals(Integer.valueOf(2), GridPane.getRowIndex(ruleLabel));
          Assert.assertEquals(Integer.valueOf(3), GridPane.getRowIndex(seedLabel));

          Assert.assertNull(sizeLabel.getTooltip());
          Assert.assertNull(ruleLabel.getTooltip());
          Assert.assertNotNull(seedLabel.getTooltip());
          Assert.assertEquals(ParametersPanel.SEED_TYPE_TOOLTIP, seedLabel.getTooltip().getText());
        });
  }

  @Test
  public void testTotalisticParametersPanelDefaultsDynamicMaxRuleAndTooltips() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          TotalisticParametersPanel panel = new TotalisticParametersPanel();
          ParametersPanel grid = panel.getPanel();

          Assert.assertEquals(8, panel.getSizeValue());
          Assert.assertEquals("Size (8)", panel.getSizeLabelText());
          Assert.assertEquals(TotalisticRule.DEFAULT_STATES, panel.getStatesValue());
          Assert.assertEquals(
              Integer.toString(TotalisticRule.DEFAULT_RULE_NUMBER), panel.getRuleText());
          Assert.assertEquals(SeedType.UNIFORM, panel.getSeedType());
          Assert.assertEquals(
              List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
              panel.getSeedComboBox().getItems());

          Label sizeLabel = grid.findLabelByPrefix("Size (");
          Label statesLabel = grid.findLabelByPrefix("States");
          Label ruleLabel = grid.findLabelByPrefix("Rule number");
          Label seedLabel = grid.findLabelByPrefix("Seed type");

          Assert.assertEquals(Integer.valueOf(1), GridPane.getRowIndex(sizeLabel));
          Assert.assertEquals(Integer.valueOf(2), GridPane.getRowIndex(statesLabel));
          Assert.assertEquals(Integer.valueOf(3), GridPane.getRowIndex(ruleLabel));
          Assert.assertEquals(Integer.valueOf(4), GridPane.getRowIndex(seedLabel));

          Assert.assertNull(sizeLabel.getTooltip());
          Assert.assertNull(statesLabel.getTooltip());
          Assert.assertNull(ruleLabel.getTooltip());
          Assert.assertNotNull(seedLabel.getTooltip());
          Assert.assertEquals(ParametersPanel.SEED_TYPE_TOOLTIP, seedLabel.getTooltip().getText());

          // When states is set to 2, max rule is 15; incrementing clamps to 15
          panel.getStatesSpinner().getValueFactory().setValue(2);
          Assert.assertEquals(2, panel.getStatesValue());
          panel.getRuleSpinner().getValueFactory().increment(20);
          Assert.assertEquals(Integer.valueOf(15), panel.getRuleSpinner().getValue());
        });
  }
}
