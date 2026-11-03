package com.cellularautomata.ui;

import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link WorkspaceConfig}. */
public class WorkspaceConfigTest {

  @Test
  public void testElementaryWindowTitle() {
    Assert.assertEquals(
        "Cellular Automata (Elementary)", WorkspaceConfig.ELEMENTARY.getWindowTitle());
  }

  @Test
  public void testTotalisticWindowTitle() {
    Assert.assertEquals(
        "Cellular Automata (Totalistic)", WorkspaceConfig.TOTALISTIC.getWindowTitle());
  }

  @Test
  public void testLifeLikeWindowTitle() {
    Assert.assertEquals(
        "Cellular Automata (Life-Like)", WorkspaceConfig.LIFE_LIKE.getWindowTitle());
  }
}
