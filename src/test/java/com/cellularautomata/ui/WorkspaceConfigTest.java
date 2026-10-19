package com.cellularautomata.ui;

import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link WorkspaceConfig}. */
public class WorkspaceConfigTest {

  @Test
  public void testElementaryMetadataAndCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.ELEMENTARY;

    Assert.assertEquals("Cellular Automata (Elementary)", config.getWindowTitle());
    Assert.assertFalse(config.supportsCustomStates());
  }

  @Test
  public void testTotalisticMetadataAndCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.TOTALISTIC;

    Assert.assertEquals("Cellular Automata (Totalistic)", config.getWindowTitle());
    Assert.assertTrue(config.supportsCustomStates());
  }

  @Test
  public void testLifeLikeMetadataAndCapabilities() {
    WorkspaceConfig config = WorkspaceConfig.LIFE_LIKE;

    Assert.assertEquals("Cellular Automata (Life-Like)", config.getWindowTitle());
    Assert.assertFalse(config.supportsCustomStates());
  }
}
