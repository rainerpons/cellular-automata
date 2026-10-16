package com.cellularautomata.ui.panels;

import com.cellularautomata.config.LifeLikePreset;
import com.cellularautomata.config.SeedType;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LifeLikeParametersPanelTest {

  @BeforeClass
  public static void initJavaFx() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    try {
      Platform.startup(latch::countDown);
      latch.await(5, TimeUnit.SECONDS);
    } catch (IllegalStateException e) {
      // Toolkit already initialized
    }
  }

  @Test
  public void testDefaultStateMatchesConwaysGameOfLifeAndDensity() {
    LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

    Assert.assertEquals(8, panel.getSizeValue());
    Assert.assertEquals("Size (8)", panel.getSizeLabelText());
    Assert.assertEquals(LifeLikePreset.CONWAYS_GAME_OF_LIFE, panel.getPreset());
    Assert.assertEquals(Set.of(3), panel.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
    Assert.assertEquals("B3/S23", panel.getRuleNotation());
    Assert.assertFalse(panel.isRuleNotationEditable());
    Assert.assertEquals(SeedType.DENSITY, panel.getSeedType());
    Assert.assertEquals(50, panel.getDensityValue());
    Assert.assertEquals("Density (50%)", panel.getDensityLabelText());
    Assert.assertTrue(panel.isDensityVisible());
    Assert.assertTrue(panel.isDensityManaged());
  }

  @Test
  public void testSizeSliderUpdatesLabelWithSingleNumberFormat() {
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
  }

  @Test
  public void testPresetSelectionUpdatesTogglesAndRuleNotation() {
    LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

    panel.selectPreset(LifeLikePreset.HIGH_LIFE);
    Assert.assertEquals(Set.of(3, 6), panel.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
    Assert.assertEquals("B36/S23", panel.getRuleNotation());

    panel.selectPreset(LifeLikePreset.SEEDS);
    Assert.assertEquals(Set.of(2), panel.getBirthCounts());
    Assert.assertEquals(Set.of(), panel.getSurvivalCounts());
    Assert.assertEquals("B2/S", panel.getRuleNotation());

    panel.selectPreset(LifeLikePreset.CONWAYS_GAME_OF_LIFE);
    Assert.assertEquals(Set.of(3), panel.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
    Assert.assertEquals("B3/S23", panel.getRuleNotation());
  }

  @Test
  public void testSelectingCustomPresetDirectlyLeavesCurrentCountsUnchanged() {
    LifeLikeParametersPanel panel = new LifeLikeParametersPanel();
    panel.selectPreset(LifeLikePreset.HIGH_LIFE);

    panel.selectPreset(LifeLikePreset.CUSTOM);
    Assert.assertEquals(LifeLikePreset.CUSTOM, panel.getPreset());
    Assert.assertEquals(Set.of(3, 6), panel.getBirthCounts());
    Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
    Assert.assertEquals("B36/S23", panel.getRuleNotation());
  }

  @Test
  public void testManualToggleUpdatesPresetToMatchingPresetOrCustom() {
    LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

    // Start at B3/S23 (Conway's Game of Life), add Birth 6 -> B36/S23 (HighLife)
    panel.toggleBirth(6);
    Assert.assertEquals(LifeLikePreset.HIGH_LIFE, panel.getPreset());
    Assert.assertEquals("B36/S23", panel.getRuleNotation());

    // Deselect Birth 6 and Birth 3 -> B/S23 (Custom)
    panel.toggleBirth(6);
    panel.toggleBirth(3);
    Assert.assertEquals(LifeLikePreset.CUSTOM, panel.getPreset());
    Assert.assertEquals("B/S23", panel.getRuleNotation());

    // Select Birth 2 and deselect Survival 2 and 3 -> B2/S (Seeds)
    panel.toggleBirth(2);
    panel.toggleSurvival(2);
    panel.toggleSurvival(3);
    Assert.assertEquals(LifeLikePreset.SEEDS, panel.getPreset());
    Assert.assertEquals("B2/S", panel.getRuleNotation());
  }

  @Test
  public void testMaximumTwoSelectionsPerSetPreventsThirdSelectionWithoutDeselectingExisting() {
    LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

    // Survival already has 2 and 3 selected (2 total). Unselected survival toggles are disabled.
    Assert.assertTrue(panel.getSurvivalToggle(4).isDisabled());
    Assert.assertFalse(panel.getSurvivalToggle(2).isDisabled());
    Assert.assertFalse(panel.getSurvivalToggle(3).isDisabled());

    // Attempting to toggle a 3rd survival count via helper or direct setSelected is prevented
    panel.toggleSurvival(4);
    Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());

    panel.getSurvivalToggle(4).setSelected(true);
    Assert.assertFalse(panel.getSurvivalToggle(4).isSelected());
    Assert.assertEquals(Set.of(2, 3), panel.getSurvivalCounts());
    Assert.assertEquals("B3/S23", panel.getRuleNotation());

    // Deselecting Survival 3 re-enables other survival toggles
    panel.toggleSurvival(3);
    Assert.assertFalse(panel.getSurvivalToggle(4).isDisabled());
    panel.toggleSurvival(4);
    Assert.assertEquals(Set.of(2, 4), panel.getSurvivalCounts());
    Assert.assertEquals("B3/S24", panel.getRuleNotation());
  }

  @Test
  public void testZeroCountIsDistinctFromEmptySet() {
    LifeLikeParametersPanel panel = new LifeLikeParametersPanel();

    // Deselect Birth 3 -> empty birth set
    panel.toggleBirth(3);
    Assert.assertEquals(Set.of(), panel.getBirthCounts());
    Assert.assertEquals("B/S23", panel.getRuleNotation());

    // Select Birth 0 -> zero count in birth set
    panel.toggleBirth(0);
    Assert.assertEquals(Set.of(0), panel.getBirthCounts());
    Assert.assertEquals("B0/S23", panel.getRuleNotation());
  }

  @Test
  public void testDensityVisibilityAndSliderBehavior() {
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
  }
}
