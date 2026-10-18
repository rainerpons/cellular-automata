package com.cellularautomata.engine.automata;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.Rule;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomataEngine}. */
public class AutomataEngineTest {

  private static final Rule RULE_30 = new ElementaryRule(30);

  @Test
  public void testUniformSeedGenerationReturnsValidMap() {
    AutomataResult result = AutomataEngine.generate(RULE_30, 8, 2, "uniform");
    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getOriginalSeed());
    Assert.assertNotNull(result.getAutomatonMap());
    Assert.assertEquals(8, result.getOriginalSeed().getSize());
    Assert.assertTrue(result.getAutomatonMap().size() > 0);
  }

  @Test
  public void testSparseSeedGenerationReturnsValidMap() {
    AutomataResult result = AutomataEngine.generate(RULE_30, 8, 2, "sparse");
    Assert.assertNotNull(result);
    Assert.assertEquals(8, result.getOriginalSeed().getSize());

    // Sparse seed creates exactly one live cell in the initial seed
    String seedState = result.getOriginalSeed().getState();
    int liveCount = 0;
    for (char c : seedState.toCharArray()) {
      if (c == '1') {
        liveCount++;
      }
    }
    Assert.assertEquals(1, liveCount);
  }

  @Test
  public void testAlternatingSeedGenerationReturnsValidMap() {
    AutomataResult result = AutomataEngine.generate(RULE_30, 8, 2, "alternating");
    Assert.assertNotNull(result);
    Assert.assertEquals(8, result.getOriginalSeed().getSize());

    // Alternating seed uses either valid starting phase for size 8
    String state = result.getOriginalSeed().getState();
    Assert.assertTrue(state.equals("10101010") || state.equals("01010101"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSeedTypeThrowsException() {
    AutomataEngine.generate(RULE_30, 8, 2, "invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSeedTypeThrowsException() {
    AutomataEngine.generate(RULE_30, 8, 2, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRuleThrowsException() {
    // ElementaryRule constructor throws IllegalArgumentException for rule > 255
    AutomataEngine.generate(new ElementaryRule(256), 8, 2, "uniform");
  }

  @Test
  public void testDifferentCasingForSeedTypeStillWorks() {
    AutomataResult result = AutomataEngine.generate(RULE_30, 8, 2, "uNiFoRm");
    Assert.assertNotNull(result);
    Assert.assertEquals(8, result.getOriginalSeed().getSize());
  }

  @Test
  public void testCreateLifeLikeSimulationDensity() {
    LifeLikeSimulation sim0 =
        AutomataEngine.createLifeLikeSimulation(
            LifeLikeRule.CONWAYS_GAME_OF_LIFE, 8, "density", 0.0);
    Assert.assertEquals(8, sim0.getSize());
    Assert.assertEquals(0L, sim0.getGeneration());
    Assert.assertEquals(0, sim0.getLiveCellCount());

    LifeLikeSimulation sim100 =
        AutomataEngine.createLifeLikeSimulation(
            LifeLikeRule.CONWAYS_GAME_OF_LIFE, 4, "density", 100.0);
    Assert.assertEquals(16, sim100.getLiveCellCount());
  }

  @Test
  public void testCreateLifeLikeSimulationWithRandom() {
    Random r1 = new Random(42);
    Random r2 = new Random(42);
    LifeLikeSimulation sim1 =
        AutomataEngine.createLifeLikeSimulation(
            LifeLikeRule.CONWAYS_GAME_OF_LIFE, 10, "density", 50.0, r1);
    LifeLikeSimulation sim2 =
        AutomataEngine.createLifeLikeSimulation(
            LifeLikeRule.CONWAYS_GAME_OF_LIFE, 10, "density", 50.0, r2);

    Assert.assertEquals(sim1.getInitialGrid(), sim2.getInitialGrid());
  }

  @Test
  public void testCreateLifeLikeSimulationSeedTypes() {
    LifeLikeSimulation sparseSim =
        AutomataEngine.createLifeLikeSimulation(LifeLikeRule.SEEDS, 7, "sparse", 0.0);
    Assert.assertEquals(7, sparseSim.getSize());
    Assert.assertEquals(1, sparseSim.getLiveCellCount());
    Assert.assertTrue(sparseSim.isAlive(3, 3));

    LifeLikeSimulation altSim =
        AutomataEngine.createLifeLikeSimulation(LifeLikeRule.HIGH_LIFE, 6, "alternating", 0.0);
    Assert.assertEquals(6, altSim.getSize());
    Assert.assertEquals(18, altSim.getLiveCellCount());

    LifeLikeSimulation uniformSim =
        AutomataEngine.createLifeLikeSimulation(
            LifeLikeRule.CONWAYS_GAME_OF_LIFE, 5, "uniform", 0.0);
    Assert.assertEquals(5, uniformSim.getSize());
  }

  @Test
  public void testCreateLifeLikeSimulationCaseInsensitive() {
    LifeLikeSimulation sim =
        AutomataEngine.createLifeLikeSimulation(
            LifeLikeRule.CONWAYS_GAME_OF_LIFE, 5, "SpArSe", 0.0);
    Assert.assertEquals(1, sim.getLiveCellCount());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsNullRule() {
    AutomataEngine.createLifeLikeSimulation(null, 5, "sparse", 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsNullSeedType() {
    AutomataEngine.createLifeLikeSimulation(LifeLikeRule.CONWAYS_GAME_OF_LIFE, 5, null, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationRejectsUnsupportedSeedType() {
    AutomataEngine.createLifeLikeSimulation(LifeLikeRule.CONWAYS_GAME_OF_LIFE, 5, "invalid", 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationWithRandomRejectsNullRule() {
    AutomataEngine.createLifeLikeSimulation(null, 5, "sparse", 0.0, new Random());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateLifeLikeSimulationWithRandomRejectsNullRandom() {
    AutomataEngine.createLifeLikeSimulation(
        LifeLikeRule.CONWAYS_GAME_OF_LIFE, 5, "sparse", 0.0, null);
  }
}
