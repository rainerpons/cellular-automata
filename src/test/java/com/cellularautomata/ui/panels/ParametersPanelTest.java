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
  public void testElementaryParametersPanelDefaultsMutatorsLayoutAndTooltips() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          ElementaryParametersPanel panel = new ElementaryParametersPanel();
          ParametersPanel grid = panel.getPanel();

          Assert.assertEquals(8, panel.getSizeValue());
          Assert.assertEquals("Size (8)", panel.getSizeLabelText());
          Assert.assertEquals(
              Integer.toString(ElementaryRule.DEFAULT_RULE_NUMBER), panel.getRuleText());
          Assert.assertEquals(SeedType.UNIFORM, panel.getSeedType());
          Assert.assertEquals(
              List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
              panel.getSeedComboBox().getItems());

          panel.setSizeValue(64);
          Assert.assertEquals(64, panel.getSizeValue());
          Assert.assertEquals("Size (64)", panel.getSizeLabelText());

          panel.setRuleText("110");
          Assert.assertEquals("110", panel.getRuleText());

          panel.selectSeedType(SeedType.SPARSE);
          Assert.assertEquals(SeedType.SPARSE, panel.getSeedType());

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
  public void testTotalisticParametersPanelDefaultsMutatorsDynamicMaxRuleAndTooltips() {
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

          panel.setSizeValue(32);
          Assert.assertEquals(32, panel.getSizeValue());
          Assert.assertEquals("Size (32)", panel.getSizeLabelText());

          panel.setStatesValue(4);
          Assert.assertEquals(4, panel.getStatesValue());

          panel.setRuleText("777");
          Assert.assertEquals("777", panel.getRuleText());

          panel.selectSeedType(SeedType.ALTERNATING);
          Assert.assertEquals(SeedType.ALTERNATING, panel.getSeedType());

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
          panel.setStatesValue(2);
          Assert.assertEquals(2, panel.getStatesValue());
          Assert.assertEquals("2", panel.getStatesText());
          panel.getRuleSpinner().getValueFactory().setValue(5);
          panel.getRuleSpinner().getValueFactory().increment(20);
          Assert.assertEquals(Integer.valueOf(15), panel.getRuleSpinner().getValue());

          // Uncommitted states text ("3") dynamically updates ruleSpinner max rule to 2186
          panel.setStatesText("3");
          Assert.assertEquals("3", panel.getStatesText());
          panel.getRuleSpinner().getValueFactory().increment(5000);
          Assert.assertEquals(Integer.valueOf(2186), panel.getRuleSpinner().getValue());

          // Out-of-bounds states value or uncommitted invalid states text does not throw when
          // incrementing ruleSpinner
          panel.setStatesValue(6);
          panel.getRuleSpinner().getValueFactory().setValue(5);
          panel.getRuleSpinner().getValueFactory().increment(10);
          Assert.assertEquals(Integer.valueOf(15), panel.getRuleSpinner().getValue());

          panel.setStatesValue(1);
          panel.getRuleSpinner().getValueFactory().setValue(5);
          panel.getRuleSpinner().getValueFactory().increment(20);
          Assert.assertEquals(Integer.valueOf(15), panel.getRuleSpinner().getValue());

          panel.setStatesText("");
          panel.getRuleSpinner().getValueFactory().setValue(5);
          panel.getRuleSpinner().getValueFactory().increment(20);
          Assert.assertEquals(Integer.valueOf(15), panel.getRuleSpinner().getValue());
        });
  }

  @Test
  public void testOneDimensionalParametersPanelSeedTypesIncludeUniformSparseAlternating() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          ParametersPanel panel = new ParametersPanel();
          javafx.scene.control.ComboBox<SeedType> comboBox =
              panel.addSeedTypeRow(
                  ElementaryParametersPanel.SUPPORTED_SEEDS, SeedType.UNIFORM, false);
          Assert.assertEquals(
              List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
              comboBox.getItems());
          Assert.assertTrue(comboBox.getItems().contains(SeedType.UNIFORM));
          Assert.assertFalse(comboBox.getItems().contains(SeedType.DENSITY));
          Assert.assertEquals(SeedType.UNIFORM, comboBox.getValue());
        });
  }
}
