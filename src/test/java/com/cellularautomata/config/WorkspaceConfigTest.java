package com.cellularautomata.config;

import org.junit.Assert;
import org.junit.Test;

public class WorkspaceConfigTest {

  @Test
  public void testElementaryCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.ELEMENTARY;

    Assert.assertFalse(config.supportsCustomStates());
    Assert.assertEquals(2, config.getMinStates());
    Assert.assertEquals(2, config.getMaxStates());
    Assert.assertEquals(2, config.getDefaultStates());

    Assert.assertEquals(255, config.getMaxRule(2));
    Assert.assertEquals(255, config.getMaxRule(5)); // Ignored for Elementary
  }

  @Test
  public void testTotalisticCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.TOTALISTIC;

    Assert.assertTrue(config.supportsCustomStates());
    Assert.assertEquals(2, config.getMinStates());
    Assert.assertEquals(5, config.getMaxStates());

    // 2 states max rule = 2^(3*1 + 1) - 1 = 15
    Assert.assertEquals(15, config.getMaxRule(2));
    // 3 states max rule = 3^(3*2 + 1) - 1 = 3^7 - 1 = 2186
    Assert.assertEquals(2186, config.getMaxRule(3));
    // 5 states max rule = 1220703124
    Assert.assertEquals(1220703124, config.getMaxRule(5));
  }
}
