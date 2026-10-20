package com.cellularautomata.ui;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.presets.LifeLikePreset;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/** Unit tests for {@link LifeLikeConfiguration}. */
public class LifeLikeConfigurationTest {

  @Test
  public void testFieldMappingAndAccessorContracts() {
    Set<Integer> birth = Set.of(3);
    Set<Integer> survival = Set.of(2, 3);
    LifeLikeConfiguration config =
        new LifeLikeConfiguration(64, birth, survival, SeedType.DENSITY, 42);

    Assert.assertEquals(64, config.size());
    Assert.assertEquals(birth, config.birthCounts());
    Assert.assertEquals(survival, config.survivalCounts());
    Assert.assertEquals(SeedType.DENSITY, config.seedType());
    Assert.assertEquals(42, config.density());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSeedTypeThrowsIllegalArgumentException() {
    new LifeLikeConfiguration(32, Set.of(3), Set.of(2, 3), null, 50);
  }

  @Test
  public void testNullBirthAndSurvivalCountsDefaultToEmptySets() {
    LifeLikeConfiguration config = new LifeLikeConfiguration(32, null, null, SeedType.UNIFORM, 50);

    Assert.assertNotNull(config.birthCounts());
    Assert.assertTrue(config.birthCounts().isEmpty());
    Assert.assertNotNull(config.survivalCounts());
    Assert.assertTrue(config.survivalCounts().isEmpty());
  }

  @Test
  public void testDensityIgnoredForNonDensitySeedTypesInEqualityAndHashCode() {
    for (SeedType seedType :
        new SeedType[] {SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING}) {
      LifeLikeConfiguration config1 =
          new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), seedType, 10);
      LifeLikeConfiguration config2 =
          new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), seedType, 80);
      LifeLikeConfiguration config3 =
          new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), seedType, 0);
      LifeLikeConfiguration config4 =
          new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), seedType, 100);

      Assert.assertEquals(
          "Configurations with non-density seed type must be equal regardless of density",
          config1,
          config2);
      Assert.assertEquals(config1.hashCode(), config2.hashCode());

      Assert.assertEquals(config1, config3);
      Assert.assertEquals(config1.hashCode(), config3.hashCode());

      Assert.assertEquals(config1, config4);
      Assert.assertEquals(config1.hashCode(), config4.hashCode());
    }
  }

  @Test
  public void testDensityMattersForDensitySeedTypeInEqualityAndHashCode() {
    LifeLikeConfiguration config20 =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 20);
    LifeLikeConfiguration config50 =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 50);
    LifeLikeConfiguration config0 =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 0);
    LifeLikeConfiguration config100 =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 100);

    Assert.assertNotEquals(config20, config50);
    Assert.assertNotEquals(config0, config100);
    Assert.assertNotEquals(config0, config50);
    Assert.assertNotEquals(config50, config100);

    LifeLikeConfiguration config50Duplicate =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 50);
    Assert.assertEquals(config50, config50Duplicate);
    Assert.assertEquals(config50.hashCode(), config50Duplicate.hashCode());
  }

  @Test
  public void testEffectiveConfigurationEqualityHandlesChangeAndReversion() {
    LifeLikeConfiguration original =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 35);
    LifeLikeConfiguration changed =
        new LifeLikeConfiguration(128, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 35);
    LifeLikeConfiguration reverted =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 35);

    Assert.assertNotEquals(original, changed);
    Assert.assertEquals(original, reverted);
    Assert.assertEquals(original.hashCode(), reverted.hashCode());

    // Density change and reversion
    LifeLikeConfiguration densityChanged =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 70);
    LifeLikeConfiguration densityReverted =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.DENSITY, 35);

    Assert.assertNotEquals(original, densityChanged);
    Assert.assertEquals(original, densityReverted);
    Assert.assertEquals(original.hashCode(), densityReverted.hashCode());
  }

  @Test
  public void testEquivalentPresetAndCustomRuleCountsProduceEqualConfigurations() {
    Set<Integer> conwayBirth = LifeLikePreset.CONWAYS_GAME_OF_LIFE.getBirthCounts();
    Set<Integer> conwaySurvival = LifeLikePreset.CONWAYS_GAME_OF_LIFE.getSurvivalCounts();

    LifeLikeConfiguration fromPreset =
        new LifeLikeConfiguration(32, conwayBirth, conwaySurvival, SeedType.UNIFORM, 50);
    LifeLikeConfiguration fromCustom =
        new LifeLikeConfiguration(32, Set.of(3), Set.of(2, 3), SeedType.UNIFORM, 50);

    Assert.assertEquals(fromPreset, fromCustom);
    Assert.assertEquals(fromPreset.hashCode(), fromCustom.hashCode());
  }

  @Test
  public void testDefensiveImmutabilityOfCountSets() {
    Set<Integer> mutableBirth = new HashSet<>(Set.of(3));
    Set<Integer> mutableSurvival = new HashSet<>(Set.of(2, 3));

    LifeLikeConfiguration config =
        new LifeLikeConfiguration(32, mutableBirth, mutableSurvival, SeedType.UNIFORM, 50);

    // Modifying the source set after passing to constructor has no effect
    mutableBirth.add(6);
    mutableSurvival.add(0);

    Assert.assertEquals(Set.of(3), config.birthCounts());
    Assert.assertEquals(Set.of(2, 3), config.survivalCounts());

    // Returned sets from accessor are unmodifiable
    try {
      config.birthCounts().add(6);
      Assert.fail("Expected UnsupportedOperationException when modifying birthCounts()");
    } catch (UnsupportedOperationException expected) {
      // Expected
    }

    try {
      config.survivalCounts().add(0);
      Assert.fail("Expected UnsupportedOperationException when modifying survivalCounts()");
    } catch (UnsupportedOperationException expected) {
      // Expected
    }
  }

  @Test
  public void testEmptySetsDifferFromSetContainingZero() {
    LifeLikeConfiguration emptyBirth =
        new LifeLikeConfiguration(32, Set.of(), Set.of(2, 3), SeedType.UNIFORM, 50);
    LifeLikeConfiguration zeroBirth =
        new LifeLikeConfiguration(32, Set.of(0), Set.of(2, 3), SeedType.UNIFORM, 50);

    Assert.assertNotEquals(emptyBirth, zeroBirth);

    LifeLikeConfiguration emptySurvival =
        new LifeLikeConfiguration(32, Set.of(3), Set.of(), SeedType.UNIFORM, 50);
    LifeLikeConfiguration zeroSurvival =
        new LifeLikeConfiguration(32, Set.of(3), Set.of(0), SeedType.UNIFORM, 50);

    Assert.assertNotEquals(emptySurvival, zeroSurvival);
  }

  @Test
  public void testStandardEqualityContracts() {
    LifeLikeConfiguration config =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.UNIFORM, 50);
    LifeLikeConfiguration same =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.UNIFORM, 50);
    LifeLikeConfiguration differentSize =
        new LifeLikeConfiguration(128, Set.of(3), Set.of(2, 3), SeedType.UNIFORM, 50);
    LifeLikeConfiguration differentSeed =
        new LifeLikeConfiguration(64, Set.of(3), Set.of(2, 3), SeedType.SPARSE, 50);

    Assert.assertEquals(config, config);
    Assert.assertEquals(config, same);
    Assert.assertEquals(same, config);
    Assert.assertEquals(config.hashCode(), same.hashCode());

    Assert.assertNotEquals(config, differentSize);
    Assert.assertNotEquals(config, differentSeed);
    Assert.assertNotEquals(config, null);
    Assert.assertNotEquals(config, "string");
  }
}
