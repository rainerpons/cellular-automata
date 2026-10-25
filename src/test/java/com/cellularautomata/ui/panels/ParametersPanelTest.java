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

  @Test
  public void testSeedTypeConstants() {
    Assert.assertEquals(
        List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
        ElementaryParametersPanel.SUPPORTED_SEEDS);
    Assert.assertEquals(
        List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
        TotalisticParametersPanel.SUPPORTED_SEEDS);

    try {
      ElementaryParametersPanel.SUPPORTED_SEEDS.add(SeedType.DENSITY);
      Assert.fail(
          "Expected UnsupportedOperationException on ElementaryParametersPanel.SUPPORTED_SEEDS mutation");
    } catch (UnsupportedOperationException expected) {
      // Expected unmodifiable list
    }

    try {
      TotalisticParametersPanel.SUPPORTED_SEEDS.add(SeedType.DENSITY);
      Assert.fail(
          "Expected UnsupportedOperationException on TotalisticParametersPanel.SUPPORTED_SEEDS mutation");
    } catch (UnsupportedOperationException expected) {
      // Expected unmodifiable list
    }
  }

  @Test
  public void testSliderConstants() {
    Assert.assertEquals(4, ParametersPanel.DEFAULT_MIN_SIZE);
    Assert.assertEquals(128, ParametersPanel.DEFAULT_MAX_SIZE);
    Assert.assertEquals(8, ParametersPanel.DEFAULT_SIZE);
    Assert.assertEquals(1.0, ParametersPanel.DEFAULT_SIZE_MAJOR_TICK, 0.001);

    Assert.assertEquals(ParametersPanel.DEFAULT_MIN_SIZE, ParametersPanel.MIN_SIZE);
    Assert.assertEquals(ParametersPanel.DEFAULT_MAX_SIZE, ParametersPanel.MAX_SIZE);
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
