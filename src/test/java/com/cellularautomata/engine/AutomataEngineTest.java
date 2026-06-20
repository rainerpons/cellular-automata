package com.cellularautomata.engine;

import org.junit.Assert;
import org.junit.Test;

public class AutomataEngineTest {

  @Test
  public void testUniformSeedGenerationReturnsValidMap() {
    AutomataResult result = AutomataEngine.generate(30, 8, "uniform");
    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getOriginalSeed());
    Assert.assertNotNull(result.getAutomatonMap());
    Assert.assertEquals(8, result.getOriginalSeed().getSize());
    Assert.assertTrue(result.getAutomatonMap().size() > 0);
  }

  @Test
  public void testSparseSeedGenerationReturnsValidMap() {
    AutomataResult result = AutomataEngine.generate(30, 8, "sparse");
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
    AutomataResult result = AutomataEngine.generate(30, 8, "alternating");
    Assert.assertNotNull(result);
    Assert.assertEquals(8, result.getOriginalSeed().getSize());

    // Alternating seed uses the expected initial seed pattern
    String expected = "10101010"; // Size 8 alternating
    Assert.assertEquals(expected, result.getOriginalSeed().getState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSeedTypeThrowsException() {
    AutomataEngine.generate(30, 8, "invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSeedTypeThrowsException() {
    AutomataEngine.generate(30, 8, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRuleThrowsException() {
    // ElementaryRule.validate throws IllegalArgumentException for rule > 255
    AutomataEngine.generate(256, 8, "uniform");
  }

  @Test
  public void testDifferentCasingForSeedTypeStillWorks() {
    AutomataResult result = AutomataEngine.generate(30, 8, "uNiFoRm");
    Assert.assertNotNull(result);
    Assert.assertEquals(8, result.getOriginalSeed().getSize());
  }
}
