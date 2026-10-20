package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.ui.JavaFxTestHelper;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.List;
import java.util.Set;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LifeLikeParametersPanelTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testDefaultStateMatchesConwaysGameOfLifeAndDensity() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

          Assert.assertEquals(8, panel.getSizeValue());
          Assert.assertEquals("Size (8)", panel.getSizeLabelText());
          Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, panel.getPreset());
          Assert.assertEquals(SeedType.DENSITY, panel.getSeedType());
          Assert.assertEquals(
              List.of(SeedType.DENSITY, SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING),
              panel.getSeedComboBox().getItems());
          Assert.assertEquals(50, panel.getDensityValue());
          Assert.assertEquals("Density (50%)", panel.getDensityLabelText());
          Assert.assertTrue(panel.isDensityVisible());
          Assert.assertTrue(panel.isDensityManaged());
          Assert.assertEquals(Set.of(3), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
        });
  }

  @Test
  public void testParameterRowOrderHorizontalSliderLayoutAndNoRuleNotationField() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          ParametersPanel grid = panel.getPanel();

          Label sizeLabel = grid.findLabelByPrefix("Size (");
          Label presetLabel = grid.findLabelByPrefix("Preset");
          Label seedLabel = grid.findLabelByPrefix("Seed type");
          Label densityLabel = grid.findLabelByPrefix("Density (");
          Label birthLabel = grid.findLabelByPrefix("Birth");
          Label survivalLabel = grid.findLabelByPrefix("Survival");
          Label ruleLabel = grid.findLabelByPrefix("Rule");

          Assert.assertNotNull(sizeLabel);
          Assert.assertNotNull(presetLabel);
          Assert.assertNotNull(seedLabel);
          Assert.assertNotNull(densityLabel);
          Assert.assertNotNull(birthLabel);
          Assert.assertNotNull(survivalLabel);
          Assert.assertNull(ruleLabel);

          Assert.assertEquals(Integer.valueOf(1), GridPane.getRowIndex(sizeLabel));
          Assert.assertEquals(Integer.valueOf(2), GridPane.getRowIndex(presetLabel));
          Assert.assertEquals(Integer.valueOf(3), GridPane.getRowIndex(seedLabel));
          Assert.assertEquals(Integer.valueOf(4), GridPane.getRowIndex(densityLabel));
          Assert.assertEquals(Integer.valueOf(5), GridPane.getRowIndex(birthLabel));
          Assert.assertEquals(Integer.valueOf(6), GridPane.getRowIndex(survivalLabel));

          // Density slider is placed horizontally on the same row in column 1
          Assert.assertEquals(Integer.valueOf(4), GridPane.getRowIndex(panel.getDensitySlider()));
          Assert.assertEquals(
              Integer.valueOf(1), GridPane.getColumnIndex(panel.getDensitySlider()));

          // Slider labels have a fixed minimum width so digit changes do not shift the slider track
          Assert.assertEquals(UiStyles.FORM_LABEL_COLUMN_MIN_WIDTH, sizeLabel.getMinWidth(), 0.001);
          Assert.assertEquals(
              UiStyles.FORM_LABEL_COLUMN_MIN_WIDTH, densityLabel.getMinWidth(), 0.001);
        });
  }

  @Test
  public void testParameterTooltipsAttachedOnlyToExpectedLabels() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          ParametersPanel grid = panel.getPanel();

          Label sizeLabel = grid.findLabelByPrefix("Size (");
          Label presetLabel = grid.findLabelByPrefix("Preset");
          Label seedLabel = grid.findLabelByPrefix("Seed type");
          Label densityLabel = grid.findLabelByPrefix("Density (");
          Label birthLabel = grid.findLabelByPrefix("Birth");
          Label survivalLabel = grid.findLabelByPrefix("Survival");

          Assert.assertNull(sizeLabel.getTooltip());
          Assert.assertNull(presetLabel.getTooltip());

          Assert.assertNotNull(seedLabel.getTooltip());
          Assert.assertEquals(ParametersPanel.SEED_TYPE_TOOLTIP, seedLabel.getTooltip().getText());

          Assert.assertNotNull(densityLabel.getTooltip());
          Assert.assertEquals(
              LifeLikeParametersPanel.DENSITY_TOOLTIP, densityLabel.getTooltip().getText());

          Assert.assertNotNull(birthLabel.getTooltip());
          Assert.assertEquals(
              LifeLikeParametersPanel.BIRTH_TOOLTIP, birthLabel.getTooltip().getText());

          Assert.assertNotNull(survivalLabel.getTooltip());
          Assert.assertEquals(
              LifeLikeParametersPanel.SURVIVAL_TOOLTIP, survivalLabel.getTooltip().getText());
        });
  }

  @Test
  public void testSizeSliderUpdatesLabelWithSingleNumberFormat() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

          panel.setSizeValue(4);
          Assert.assertEquals(4, panel.getSizeValue());
          Assert.assertEquals("Size (4)", panel.getSizeLabelText());

          panel.setSizeValue(64);
          Assert.assertEquals(64, panel.getSizeValue());
          Assert.assertEquals("Size (64)", panel.getSizeLabelText());

          panel.setSizeValue(128);
          Assert.assertEquals(128, panel.getSizeValue());
          Assert.assertEquals("Size (128)", panel.getSizeLabelText());
        });
  }

  @Test
  public void testPresetSelectionUpdatesToggles() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

          panel.selectPreset(LifeLikePreset.HIGH_LIFE);
          Assert.assertEquals(Set.of(3, 6), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());

          panel.selectPreset(LifeLikePreset.SEEDS);
          Assert.assertEquals(Set.of(2), panel.getBirthCounts());
          Assert.assertEquals(Set.of(), panel.getSurvivalCounts());

          panel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
          Assert.assertEquals(Set.of(3), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
        });
  }

  @Test
  public void testCustomStateWhenTogglesDoNotMatchPreset() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          panel.selectPreset(LifeLikePreset.HIGH_LIFE);

          panel.toggleBirth(6);
          panel.toggleBirth(3);
          Assert.assertNull(panel.getPreset());
          Assert.assertTrue(panel.getSelectionModel().isCustom());
          Assert.assertEquals(Set.of(), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
        });
  }

  @Test
  public void testManualToggleUpdatesPresetToMatchingPresetOrCustom() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

          // Start at B3/S23 (Conway's Game of Life), add Birth 6 -> B36/S23 (HighLife)
          panel.toggleBirth(6);
          Assert.assertEquals(LifeLikePreset.HIGH_LIFE, panel.getPreset());

          // Deselect Birth 6 and Birth 3 -> B/S23 (Custom)
          panel.toggleBirth(6);
          panel.toggleBirth(3);
          Assert.assertNull(panel.getPreset());
          Assert.assertTrue(panel.getSelectionModel().isCustom());

          // Select Birth 2 and deselect Survival 2 and 3 -> B2/S (Seeds)
          panel.toggleBirth(2);
          panel.toggleSurvival(2);
          panel.toggleSurvival(3);
          Assert.assertEquals(LifeLikePreset.SEEDS, panel.getPreset());
        });
  }

  @Test
  public void testMaximumTwoSelectionsPerSetPreventsThirdSelectionWithoutDeselectingExisting() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

          // Survival already has 2 and 3 selected (2 total). Unselected survival toggles are
          // disabled.
          Assert.assertTrue(panel.getSurvivalToggle(4).isDisabled());
          Assert.assertFalse(panel.getSurvivalToggle(2).isDisabled());
          Assert.assertFalse(panel.getSurvivalToggle(3).isDisabled());

          // Attempting to toggle a 3rd survival count via helper or direct setSelected is prevented
          panel.toggleSurvival(4);
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());

          panel.getSurvivalToggle(4).setSelected(true);
          Assert.assertFalse(panel.getSurvivalToggle(4).isSelected());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());

          // Deselecting Survival 3 re-enables other survival toggles
          panel.toggleSurvival(3);
          Assert.assertFalse(panel.getSurvivalToggle(4).isDisabled());
          panel.toggleSurvival(4);
          Assert.assertEquals(Set.of(2, 4), panel.getSurvivalCounts());
        });
  }

  @Test
  public void testZeroCountIsDistinctFromEmptySet() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

          // Deselect Birth 3 -> empty birth set
          panel.toggleBirth(3);
          Assert.assertEquals(Set.of(), panel.getBirthCounts());

          // Select Birth 0 -> zero count in birth set
          panel.toggleBirth(0);
          Assert.assertEquals(Set.of(0), panel.getBirthCounts());
        });
  }

  @Test
  public void testDensityVisibilityAndSliderBehavior() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

          Assert.assertTrue(panel.isDensityVisible());
          Assert.assertTrue(panel.isDensityManaged());
          Assert.assertFalse(panel.getDensitySlider().isSnapToTicks());
          Assert.assertEquals(25.0, panel.getDensitySlider().getMajorTickUnit(), 0.001);
          Assert.assertTrue(panel.getDensitySlider().isShowTickMarks());
          Assert.assertTrue(panel.getDensitySlider().isShowTickLabels());

          // Non-tick values are reachable without snapping
          panel.setDensityValue(0);
          Assert.assertEquals(0, panel.getDensityValue());
          Assert.assertEquals("Density (0%)", panel.getDensityLabelText());

          panel.setDensityValue(37);
          Assert.assertEquals(37, panel.getDensityValue());
          Assert.assertEquals("Density (37%)", panel.getDensityLabelText());

          panel.setDensityValue(100);
          Assert.assertEquals(100, panel.getDensityValue());
          Assert.assertEquals("Density (100%)", panel.getDensityLabelText());

          // Hidden and unmanaged for Uniform, Sparse, Alternating
          panel.selectSeedType(SeedType.UNIFORM);
          Assert.assertFalse(panel.isDensityVisible());
          Assert.assertFalse(panel.isDensityManaged());

          panel.selectSeedType(SeedType.SPARSE);
          Assert.assertFalse(panel.isDensityVisible());
          Assert.assertFalse(panel.isDensityManaged());

          panel.selectSeedType(SeedType.ALTERNATING);
          Assert.assertFalse(panel.isDensityVisible());
          Assert.assertFalse(panel.isDensityManaged());

          // Restored when switching back to Density
          panel.selectSeedType(SeedType.DENSITY);
          Assert.assertTrue(panel.isDensityVisible());
          Assert.assertTrue(panel.isDensityManaged());
        });
  }
}
