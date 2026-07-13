package com.cellularautomata.engine.automata;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.Rule;
import org.junit.Assert;
import org.junit.Test;

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
      if (c == '1') liveCount++;
    }
    Assert.assertEquals(1, liveCount);
  }

  @Test
  public void testAlternatingSeedGenerationReturnsValidMap() {
    AutomataResult result = AutomataEngine.generate(RULE_30, 8, 2, "alternating");
    Assert.assertNotNull(result);
    Assert.assertEquals(8, result.getOriginalSeed().getSize());

    // Alternating seed uses the expected initial seed pattern
    String expected = "10101010"; // Size 8 alternating
    Assert.assertEquals(expected, result.getOriginalSeed().getState());
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
}
