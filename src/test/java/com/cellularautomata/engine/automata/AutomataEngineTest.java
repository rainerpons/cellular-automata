package com.cellularautomata.engine.automata;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.engine.presets.LifeLikePreset;
import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.Rule;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomataEngine}. */
public class AutomataEngineTest {

  private static final Rule RULE_30 = new ElementaryRule(30);
  private static final LifeLikeRule CONWAY = LifeLikePreset.CONWAYS_GAME_OF_LIFE.toRule();

  @Test
  public void testGenerate1DWithSeedTypeEnum() {
    AutomataResult uniform = AutomataEngine.generate(RULE_30, 8, 2, SeedType.UNIFORM);
    Assert.assertNotNull(uniform);
    Assert.assertEquals(8, uniform.getOriginalSeed().getSize());

    AutomataResult sparse = AutomataEngine.generate(RULE_30, 8, 2, SeedType.SPARSE);
    Assert.assertNotNull(sparse);
    long liveCount = sparse.getOriginalSeed().getState().chars().filter(c -> c == '1').count();
    Assert.assertEquals(1, liveCount);

    AutomataResult alternating = AutomataEngine.generate(RULE_30, 8, 2, SeedType.ALTERNATING);
    Assert.assertNotNull(alternating);
    String state = alternating.getOriginalSeed().getState();
    Assert.assertTrue(state.equals("10101010") || state.equals("01010101"));
  }

  @Test
  public void testGenerate1DWithSeedTypeEnumAndRandom() {
    Random r1 = new Random(42);
    Random r2 = new Random(42);
    AutomataResult result1 = AutomataEngine.generate(RULE_30, 16, 2, SeedType.UNIFORM, r1);
    AutomataResult result2 = AutomataEngine.generate(RULE_30, 16, 2, SeedType.UNIFORM, r2);
    Assert.assertEquals(result1.getOriginalSeed().getState(), result2.getOriginalSeed().getState());
  }

  @Test
  public void testGenerate1DWithStringSeedType() {
    AutomataResult uniform =
        AutomataEngine.generate(RULE_30, 8, 2, SeedType.UNIFORM.getIdentifier());
    Assert.assertNotNull(uniform);
    Assert.assertEquals(8, uniform.getOriginalSeed().getSize());

    AutomataResult sparse = AutomataEngine.generate(RULE_30, 8, 2, "SpArSe");
    Assert.assertNotNull(sparse);
    long liveCount = sparse.getOriginalSeed().getState().chars().filter(c -> c == '1').count();
    Assert.assertEquals(1, liveCount);

    AutomataResult alternating = AutomataEngine.generate(RULE_30, 8, 2, "ALTERNATING");
    Assert.assertNotNull(alternating);
  }

  @Test
  public void testGenerate1DWithStringSeedTypeAndRandom() {
    Random r1 = new Random(99);
    Random r2 = new Random(99);
    AutomataResult res1 =
        AutomataEngine.generate(RULE_30, 10, 2, SeedType.UNIFORM.getIdentifier(), r1);
    AutomataResult res2 =
        AutomataEngine.generate(RULE_30, 10, 2, SeedType.UNIFORM.getIdentifier(), r2);
    Assert.assertEquals(res1.getOriginalSeed().getState(), res2.getOriginalSeed().getState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsDensityEnum() {
    AutomataEngine.generate(RULE_30, 8, 2, SeedType.DENSITY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsDensityEnumWithRandom() {
    AutomataEngine.generate(RULE_30, 8, 2, SeedType.DENSITY, new Random());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsDensityString() {
    AutomataEngine.generate(RULE_30, 8, 2, SeedType.DENSITY.getIdentifier());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsDensityStringWithRandom() {
    AutomataEngine.generate(RULE_30, 8, 2, SeedType.DENSITY.getIdentifier(), new Random());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsNullRule() {
    AutomataEngine.generate(null, 8, 2, SeedType.UNIFORM);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsNullSeedTypeEnum() {
    AutomataEngine.generate(RULE_30, 8, 2, (SeedType) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsNullSeedTypeString() {
    AutomataEngine.generate(RULE_30, 8, 2, (String) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsNullRandom() {
    AutomataEngine.generate(RULE_30, 8, 2, SeedType.UNIFORM, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate1DRejectsInvalidSeedString() {
    AutomataEngine.generate(RULE_30, 8, 2, "invalid");
  }

  @Test
  public void testCreateLifeLikeSimulationWithSeedTypeEnum() {
    LifeLikeSimulation density0 =
        AutomataEngine.createLifeLikeSimulation(CONWAY, 6, SeedType.DENSITY, 0.0);
    Assert.assertEquals(6, density0.getSize());
    Assert.assertEquals(0, density0.getLiveCellCount());

    LifeLikeSimulation density100 =
        AutomataEngine.createLifeLikeSimulation(CONWAY, 4, SeedType.DENSITY, 100.0);
    Assert.assertEquals(16, density100.getLiveCellCount());

    LifeLikeSimulation uniform =
        AutomataEngine.createLifeLikeSimulation(CONWAY, 5, SeedType.UNIFORM, 0.0);
    Assert.assertEquals(5, uniform.getSize());

    LifeLikeSimulation sparse =
        AutomataEngine.createLifeLikeSimulation(CONWAY, 7, SeedType.SPARSE, 0.0);
    Assert.assertEquals(7, sparse.getSize());
    Assert.assertEquals(1, sparse.getLiveCellCount());

    LifeLikeSimulation alternating =
        AutomataEngine.createLifeLikeSimulation(CONWAY, 4, SeedType.ALTERNATING, 0.0);
    Assert.assertEquals(4, alternating.getSize());
    Assert.assertEquals(8, alternating.getLiveCellCount());
  }

  @Test
  public void testCreateLifeLikeSimulationWithSeedTypeEnumAndRandom() {
    Random r1 = new Random(42);
    Random r2 = new Random(42);
    LifeLikeSimulation sim1 =
        AutomataEngine.createLifeLikeSimulation(CONWAY, 10, SeedType.DENSITY, 50.0, r1);
    LifeLikeSimulation sim2 =
        AutomataEngine.createLifeLikeSimulation(CONWAY, 10, SeedType.DENSITY, 50.0, r2);
    Assert.assertEquals(sim1.getInitialGrid(), sim2.getInitialGrid());
  }

  @Test
  public void testCreateLifeLikeSimulationWithStringIdentifier() {
    LifeLikeSimulation sim = AutomataEngine.createLifeLikeSimulation(CONWAY, 8, "DeNsItY", 100.0);
    Assert.assertEquals(64, sim.getLiveCellCount());

    LifeLikeSimulation sparse = AutomataEngine.createLifeLikeSimulation(CONWAY, 5, "SpArSe", 0.0);
    Assert.assertEquals(1, sparse.getLiveCellCount());
  }

  @Test
  public void testCreateLifeLikeSimulationWithStringIdentifierAndRandom() {
    Random r1 = new Random(55);
    Random r2 = new Random(55);
    LifeLikeSimulation sim1 =
        AutomataEngine.createLifeLikeSimulation(
            CONWAY, 8, SeedType.UNIFORM.getIdentifier(), 0.0, r1);
    LifeLikeSimulation sim2 =
        AutomataEngine.createLifeLikeSimulation(
            CONWAY, 8, SeedType.UNIFORM.getIdentifier(), 0.0, r2);
    Assert.assertEquals(sim1.getInitialGrid(), sim2.getInitialGrid());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsNullRule() {
    AutomataEngine.createLifeLikeSimulation(null, 5, SeedType.SPARSE, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsNullSeedTypeEnum() {
    AutomataEngine.createLifeLikeSimulation(CONWAY, 5, (SeedType) null, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsNullSeedTypeString() {
    AutomataEngine.createLifeLikeSimulation(CONWAY, 5, (String) null, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsNullRandom() {
    AutomataEngine.createLifeLikeSimulation(CONWAY, 5, SeedType.SPARSE, 0.0, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsZeroSize() {
    AutomataEngine.createLifeLikeSimulation(CONWAY, 0, SeedType.UNIFORM, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsNegativeSize() {
    AutomataEngine.createLifeLikeSimulation(CONWAY, -1, SeedType.UNIFORM, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsInvalidSeedString() {
    AutomataEngine.createLifeLikeSimulation(CONWAY, 5, "unknown", 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsInvalidDensity() {
    AutomataEngine.createLifeLikeSimulation(CONWAY, 5, SeedType.DENSITY, 105.0);
  }
}
