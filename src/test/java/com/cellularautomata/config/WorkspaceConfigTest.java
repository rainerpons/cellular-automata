package com.cellularautomata.config;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.TotalisticRule;
import org.junit.Assert;
import org.junit.Test;

public class WorkspaceConfigTest {

  @Test
  public void testElementaryCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.ELEMENTARY;

    Assert.assertEquals("Cellular Automata (Elementary)", config.getWindowTitle());
    Assert.assertFalse(config.supportsCustomStates());
    Assert.assertEquals(2, config.getMinStates());
    Assert.assertEquals(2, config.getMaxStates());
    Assert.assertEquals(2, config.getDefaultStates());

    Assert.assertEquals(ElementaryRule.MIN_RULE_NUMBER, config.getMinRule());
    Assert.assertEquals(ElementaryRule.DEFAULT_RULE_NUMBER, config.getDefaultRule());
    Assert.assertEquals(ElementaryRule.MAX_RULE_NUMBER, config.getMaxRule(2));
    Assert.assertEquals(ElementaryRule.MAX_RULE_NUMBER, config.getMaxRule(5));
  }

  @Test
  public void testTotalisticCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.TOTALISTIC;

    Assert.assertEquals("Cellular Automata (Totalistic)", config.getWindowTitle());
    Assert.assertTrue(config.supportsCustomStates());
    Assert.assertEquals(TotalisticRule.MIN_STATES, config.getMinStates());
    Assert.assertEquals(TotalisticRule.MAX_STATES, config.getMaxStates());
    Assert.assertEquals(TotalisticRule.DEFAULT_STATES, config.getDefaultStates());

    Assert.assertEquals(TotalisticRule.MIN_RULE_NUMBER, config.getMinRule());
    Assert.assertEquals(TotalisticRule.DEFAULT_RULE_NUMBER, config.getDefaultRule());
    // 2 states max rule = 2^(3*1 + 1) - 1 = 15
    Assert.assertEquals(15, config.getMaxRule(2));
    // 3 states max rule = 3^(3*2 + 1) - 1 = 3^7 - 1 = 2186
    Assert.assertEquals(2186, config.getMaxRule(3));
    // 5 states max rule = 1220703124
    Assert.assertEquals(1220703124, config.getMaxRule(5));
  }

  @Test
  public void testLifeLikeCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.LIFE_LIKE;

    Assert.assertEquals("Cellular Automata (Life-Like)", config.getWindowTitle());
    Assert.assertFalse(config.supportsCustomStates());
    Assert.assertEquals(2, config.getMinStates());
    Assert.assertEquals(2, config.getMaxStates());
    Assert.assertEquals(2, config.getDefaultStates());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testLifeLikeGetMinRuleThrows() {
    WorkspaceConfig.LIFE_LIKE.getMinRule();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testLifeLikeGetDefaultRuleThrows() {
    WorkspaceConfig.LIFE_LIKE.getDefaultRule();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testLifeLikeGetMaxRuleThrows() {
    WorkspaceConfig.LIFE_LIKE.getMaxRule(2);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testLifeLikeInstantiateRuleThrows() {
    WorkspaceConfig.LIFE_LIKE.instantiateRule(0, 2);
  }
}
