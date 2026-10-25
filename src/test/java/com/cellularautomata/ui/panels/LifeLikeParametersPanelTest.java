package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.ui.JavaFxTestHelper;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
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

          Assert.assertEquals(
              LifeLikeParametersPanel.MIN_SIZE, (int) panel.getSizeSlider().getMin());
          Assert.assertEquals(
              LifeLikeParametersPanel.MAX_SIZE, (int) panel.getSizeSlider().getMax());
          Assert.assertEquals(
              LifeLikeParametersPanel.SIZE_MAJOR_TICK,
              panel.getSizeSlider().getMajorTickUnit(),
              0.001);
          Assert.assertTrue(panel.getSizeSlider().isShowTickMarks());
          Assert.assertFalse(panel.getSizeSlider().isSnapToTicks());
          Assert.assertEquals(LifeLikeParametersPanel.DEFAULT_SIZE, panel.getSizeValue());
          Assert.assertEquals(
              "Size (%d)".formatted(LifeLikeParametersPanel.DEFAULT_SIZE),
              panel.getSizeLabelText());
          Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, panel.getPreset());
          Assert.assertEquals(SeedType.DENSITY, panel.getSeedType());
          Assert.assertEquals(
              LifeLikeParametersPanel.SUPPORTED_SEEDS, panel.getSeedComboBox().getItems());
          Assert.assertFalse(panel.getSeedComboBox().getItems().contains(SeedType.UNIFORM));
          Assert.assertEquals(50, panel.getDensityValue());
          Assert.assertEquals("Density (50%)", panel.getDensityLabelText());
          Assert.assertTrue(panel.isDensityVisible());
          Assert.assertTrue(panel.isDensityManaged());
          Assert.assertEquals(Set.of(3), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
          Assert.assertEquals(
              LifeLikeParametersPanel.DEFAULT_PLAYBACK_SPEED, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("1.00x", panel.getPlaybackSpeedDisplayText());
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
          Label playbackSpeedLabel = grid.findLabelByPrefix("Playback speed");
          Label ruleLabel = grid.findLabelByPrefix("Rule");

          Assert.assertNotNull(sizeLabel);
          Assert.assertNotNull(presetLabel);
          Assert.assertNotNull(seedLabel);
          Assert.assertNotNull(densityLabel);
          Assert.assertNotNull(birthLabel);
          Assert.assertNotNull(survivalLabel);
          Assert.assertNotNull(playbackSpeedLabel);
          Assert.assertNull(ruleLabel);

          Assert.assertEquals(Integer.valueOf(1), GridPane.getRowIndex(sizeLabel));
          Assert.assertEquals(Integer.valueOf(2), GridPane.getRowIndex(presetLabel));
          Assert.assertEquals(Integer.valueOf(3), GridPane.getRowIndex(seedLabel));
          Assert.assertEquals(Integer.valueOf(4), GridPane.getRowIndex(densityLabel));
          Assert.assertEquals(Integer.valueOf(5), GridPane.getRowIndex(birthLabel));
          Assert.assertEquals(Integer.valueOf(6), GridPane.getRowIndex(survivalLabel));
          Assert.assertEquals(Integer.valueOf(7), GridPane.getRowIndex(playbackSpeedLabel));

          // Density slider is placed horizontally on the same row in column 1
          Assert.assertEquals(Integer.valueOf(4), GridPane.getRowIndex(panel.getDensitySlider()));
          Assert.assertEquals(
              Integer.valueOf(1), GridPane.getColumnIndex(panel.getDensitySlider()));

          // Playback speed spinner is placed immediately below Survival on row 7 in column 1
          Assert.assertEquals(
              Integer.valueOf(7), GridPane.getRowIndex(panel.getPlaybackSpeedSpinner()));
          Assert.assertEquals(
              Integer.valueOf(1), GridPane.getColumnIndex(panel.getPlaybackSpeedSpinner()));
          Assert.assertNotNull(GridPane.getMargin(survivalLabel));
          Assert.assertEquals(
              UiStyles.FORM_ROW_BOTTOM_GAP, GridPane.getMargin(survivalLabel).getBottom(), 0.001);
          Assert.assertNull(GridPane.getMargin(playbackSpeedLabel));
          Assert.assertNull(GridPane.getMargin(panel.getPlaybackSpeedSpinner()));

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
          Label playbackSpeedLabel = grid.findLabelByPrefix("Playback speed");

          Assert.assertNull(sizeLabel.getTooltip());
          Assert.assertNull(presetLabel.getTooltip());
          Assert.assertNull(playbackSpeedLabel.getTooltip());

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

          panel.setSizeValue(5);
          Assert.assertEquals(5, panel.getSizeValue());
          Assert.assertEquals("Size (5)", panel.getSizeLabelText());

          panel.setSizeValue(64);
          Assert.assertEquals(64, panel.getSizeValue());
          Assert.assertEquals("Size (64)", panel.getSizeLabelText());

          panel.setSizeValue(155);
          Assert.assertEquals(155, panel.getSizeValue());
          Assert.assertEquals("Size (155)", panel.getSizeLabelText());
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

          // Hidden and unmanaged for Sparse and Alternating
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

  @Test
  public void testCustomPresetDropdownScenario1ConwaysGameOfLife() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          panel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);

          Assert.assertEquals("Conway's Game of Life", panel.getPresetDisplayText());
          Assert.assertEquals(
              "Conway's Game of Life", panel.getPresetComboBox().getButtonCell().getText());
          Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, panel.getPreset());
          Assert.assertEquals(Set.of(3), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
          Assert.assertTrue(panel.getBirthToggle(3).isSelected());
          Assert.assertTrue(panel.getSurvivalToggle(2).isSelected());
          Assert.assertTrue(panel.getSurvivalToggle(3).isSelected());
        });
  }

  @Test
  public void testCustomPresetDropdownScenario2NonPresetDisplaysCustom() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          panel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);

          // Change to non-preset B3/S3 by unchecking survival 2
          panel.toggleSurvival(2);

          Assert.assertEquals("Custom", panel.getPresetDisplayText());
          Assert.assertEquals("Custom", panel.getPresetComboBox().getButtonCell().getText());
          Assert.assertNull(panel.getPreset());
          Assert.assertFalse(panel.getPresetComboBox().getButtonCell().getText().isEmpty());
          Assert.assertEquals(Set.of(3), panel.getBirthCounts());
          Assert.assertEquals(Set.of(3), panel.getSurvivalCounts());
        });
  }

  @Test
  public void testCustomPresetDropdownScenario3MatchingHighLifeDisplaysHighLife() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          // Start from Conway B3/S23, toggle birth 6 on to get B36/S23 (HighLife)
          panel.toggleBirth(6);

          Assert.assertEquals("HighLife", panel.getPresetDisplayText());
          Assert.assertEquals("HighLife", panel.getPresetComboBox().getButtonCell().getText());
          Assert.assertEquals(LifeLikePreset.HIGH_LIFE, panel.getPreset());
          Assert.assertEquals(Set.of(3, 6), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
          Assert.assertTrue(panel.getBirthToggle(3).isSelected());
          Assert.assertTrue(panel.getBirthToggle(6).isSelected());
          Assert.assertTrue(panel.getSurvivalToggle(2).isSelected());
          Assert.assertTrue(panel.getSurvivalToggle(3).isSelected());
        });
  }

  @Test
  public void testCustomPresetDropdownScenario4RestoringConwayDisplaysConway() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          // Switch to HighLife B36/S23
          panel.toggleBirth(6);
          Assert.assertEquals("HighLife", panel.getPresetDisplayText());

          // Toggle birth 6 off -> restores B3/S23
          panel.toggleBirth(6);

          Assert.assertEquals("Conway's Game of Life", panel.getPresetDisplayText());
          Assert.assertEquals(
              "Conway's Game of Life", panel.getPresetComboBox().getButtonCell().getText());
          Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, panel.getPreset());
          Assert.assertEquals(Set.of(3), panel.getBirthCounts());
          Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
        });
  }

  @Test
  public void testCustomPresetDropdownScenario5SelectingPresetWhileCustomDisplayed() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          // Change to custom by toggling survival 2 off
          panel.toggleSurvival(2);
          Assert.assertEquals("Custom", panel.getPresetDisplayText());
          Assert.assertEquals("Custom", panel.getPresetComboBox().getButtonCell().getText());

          // Select named preset Seeds (B2/S)
          panel.selectPreset(LifeLikePreset.SEEDS);

          Assert.assertEquals("Seeds", panel.getPresetDisplayText());
          Assert.assertEquals("Seeds", panel.getPresetComboBox().getButtonCell().getText());
          Assert.assertEquals(LifeLikePreset.SEEDS, panel.getPreset());
          Assert.assertEquals(Set.of(2), panel.getBirthCounts());
          Assert.assertTrue(panel.getSurvivalCounts().isEmpty());
          Assert.assertTrue(panel.getBirthToggle(2).isSelected());
          Assert.assertFalse(panel.getBirthToggle(3).isSelected());
          for (int count = 0; count <= 8; count++) {
            Assert.assertFalse(panel.getSurvivalToggle(count).isSelected());
          }
        });
  }

  @Test
  public void testLifeLikeSizeSliderPropertiesAndFullIntegerSelectionRange() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          Slider slider = panel.getSizeSlider();

          Assert.assertEquals(LifeLikeParametersPanel.MIN_SIZE, slider.getMin(), 0.001);
          Assert.assertEquals(LifeLikeParametersPanel.MAX_SIZE, slider.getMax(), 0.001);
          Assert.assertEquals(LifeLikeParametersPanel.DEFAULT_SIZE, slider.getValue(), 0.001);
          Assert.assertEquals(
              LifeLikeParametersPanel.SIZE_MAJOR_TICK, slider.getMajorTickUnit(), 0.001);
          Assert.assertEquals(0, slider.getMinorTickCount());
          Assert.assertFalse(slider.isSnapToTicks());
          Assert.assertTrue(slider.isShowTickMarks());
          Assert.assertTrue(slider.isShowTickLabels());

          for (int size = LifeLikeParametersPanel.MIN_SIZE;
              size <= LifeLikeParametersPanel.MAX_SIZE;
              size++) {
            panel.setSizeValue(size);
            Assert.assertEquals(size, panel.getSizeValue());
            Assert.assertEquals("Size (%d)".formatted(size), panel.getSizeLabelText());
          }
        });
  }

  @Test
  public void testPlaybackSpeedSpinnerDefaultBoundsStepsFormattingAndNoWrapAround() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          Spinner<Double> spinner = panel.getPlaybackSpeedSpinner();

          Assert.assertFalse(spinner.isEditable());
          Assert.assertTrue(
              spinner.getStyleClass().contains(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL));
          Assert.assertEquals(Pos.CENTER, spinner.getEditor().getAlignment());
          Assert.assertEquals(UiStyles.CONTROL_HEIGHT, spinner.getMinHeight(), 0.001);
          Assert.assertEquals(UiStyles.CONTROL_HEIGHT, spinner.getPrefHeight(), 0.001);

          SpinnerValueFactory.DoubleSpinnerValueFactory factory =
              (SpinnerValueFactory.DoubleSpinnerValueFactory) spinner.getValueFactory();
          Assert.assertFalse(factory.isWrapAround());
          Assert.assertEquals(LifeLikeParametersPanel.MIN_PLAYBACK_SPEED, factory.getMin(), 0.001);
          Assert.assertEquals(LifeLikeParametersPanel.MAX_PLAYBACK_SPEED, factory.getMax(), 0.001);
          Assert.assertEquals(
              LifeLikeParametersPanel.PLAYBACK_SPEED_STEP, factory.getAmountToStepBy(), 0.001);

          // Default is 1.00x
          Assert.assertEquals(1.00, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("1.00x", panel.getPlaybackSpeedDisplayText());

          // Step down to 0.25x and verify no wraparound below minimum
          panel.setPlaybackSpeed(LifeLikeParametersPanel.MIN_PLAYBACK_SPEED);
          Assert.assertEquals(0.25, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("0.25x", panel.getPlaybackSpeedDisplayText());

          spinner.decrement();
          Assert.assertEquals(0.25, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("0.25x", panel.getPlaybackSpeedDisplayText());

          // Step up through all 0.25x increments from 0.25x to 2.00x
          double[] expectedSpeeds = {0.25, 0.50, 0.75, 1.00, 1.25, 1.50, 1.75, 2.00};
          String[] expectedTexts = {
            "0.25x", "0.50x", "0.75x", "1.00x", "1.25x", "1.50x", "1.75x", "2.00x"
          };

          for (int i = 0; i < expectedSpeeds.length; i++) {
            Assert.assertEquals(expectedSpeeds[i], panel.getPlaybackSpeed(), 0.001);
            Assert.assertEquals(expectedTexts[i], panel.getPlaybackSpeedDisplayText());
            if (i < expectedSpeeds.length - 1) {
              spinner.increment();
            }
          }

          // Verify no wraparound above maximum
          spinner.increment();
          Assert.assertEquals(2.00, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("2.00x", panel.getPlaybackSpeedDisplayText());
        });
  }

  @Test
  public void testPlaybackSpeedClampingSnappingConverterAndListener() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
          List<Double> observedSpeeds = new ArrayList<>();
          panel.addPlaybackSpeedListener(observedSpeeds::add);

          // Clamping below min and above max
          panel.setPlaybackSpeed(0.05);
          Assert.assertEquals(0.25, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("0.25x", panel.getPlaybackSpeedDisplayText());

          panel.setPlaybackSpeed(3.50);
          Assert.assertEquals(2.00, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("2.00x", panel.getPlaybackSpeedDisplayText());

          // Snapping to nearest 0.25 increment
          panel.setPlaybackSpeed(1.10);
          Assert.assertEquals(1.00, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("1.00x", panel.getPlaybackSpeedDisplayText());

          panel.setPlaybackSpeed(1.15);
          Assert.assertEquals(1.25, panel.getPlaybackSpeed(), 0.001);
          Assert.assertEquals("1.25x", panel.getPlaybackSpeedDisplayText());

          Assert.assertEquals(List.of(0.25, 2.00, 1.00, 1.25), observedSpeeds);

          // Converter formatting and parsing
          StringConverter<Double> converter =
              panel.getPlaybackSpeedSpinner().getValueFactory().getConverter();
          Assert.assertEquals("", converter.toString(null));
          Assert.assertEquals("1.50x", converter.toString(1.5));
          Assert.assertEquals(Double.valueOf(1.75), converter.fromString("1.75x"));
          Assert.assertEquals(Double.valueOf(0.50), converter.fromString(" 0.50X "));
          Assert.assertEquals(Double.valueOf(1.25), converter.fromString("1.25"));
          Assert.assertEquals(Double.valueOf(1.25), converter.fromString(null));
          Assert.assertEquals(Double.valueOf(1.25), converter.fromString(""));
          Assert.assertEquals(Double.valueOf(1.25), converter.fromString("x"));
          Assert.assertEquals(Double.valueOf(1.25), converter.fromString("invalid"));

          // Reject invalid non-finite speeds and null listeners
          assertThrowsIllegalArgument(() -> panel.setPlaybackSpeed(Double.NaN));
          assertThrowsIllegalArgument(() -> panel.setPlaybackSpeed(Double.POSITIVE_INFINITY));
          assertThrowsIllegalArgument(() -> panel.setPlaybackSpeed(Double.NEGATIVE_INFINITY));
          assertThrowsIllegalArgument(() -> panel.addPlaybackSpeedListener(null));
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
