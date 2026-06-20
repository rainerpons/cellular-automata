package com.cellularautomata.engine;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class AutomataResultTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullMap() {
    new AutomataResult(null, new Vector("11111111"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullSeed() {
    new AutomataResult(new HashMap<>(), null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testReturnedMapCannotBeModified() {
    Map<Integer, Vector> map = new HashMap<>();
    map.put(0, new Vector("11111111"));
    AutomataResult result = new AutomataResult(map, new Vector("11111111"));

    result.getAutomatonMap().put(1, new Vector("00000000"));
  }

  @Test
  public void testMutatingOriginalMapDoesNotAffectResult() {
    Map<Integer, Vector> originalMap = new HashMap<>();
    Vector seed = new Vector("11111111");
    originalMap.put(0, seed);

    AutomataResult result = new AutomataResult(originalMap, seed);

    // Mutate original map
    originalMap.put(1, new Vector("00000000"));

    // Ensure result map remains unchanged
    Assert.assertEquals(1, result.getAutomatonMap().size());
    Assert.assertTrue(result.getAutomatonMap().containsKey(0));
    Assert.assertFalse(result.getAutomatonMap().containsKey(1));
  }
}
