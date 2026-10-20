package com.cellularautomata.ui;

import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonFileNameFormatter}. */
public class AutomatonFileNameFormatterTest {

  @Test
  public void testFormatFileNameElementary() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        AutomatonFileNameFormatter.formatFileName(WorkspaceConfig.ELEMENTARY, 110, 64, 2, time);

    Assert.assertEquals("elementary_rule110_size64_2026-07-13T15-30-45.png", fileName);
  }

  @Test
  public void testFormatFileNameTotalistic() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        AutomatonFileNameFormatter.formatFileName(WorkspaceConfig.TOTALISTIC, 777, 128, 3, time);

    Assert.assertEquals("totalistic_states3_rule777_size128_2026-07-13T15-30-45.png", fileName);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFormatFileNameLifeLikeThrows() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    AutomatonFileNameFormatter.formatFileName(WorkspaceConfig.LIFE_LIKE, 0, 64, 2, time);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameNullConfigThrows() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    AutomatonFileNameFormatter.formatFileName(null, 110, 64, 2, time);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatFileNameNullTimeThrows() {
    AutomatonFileNameFormatter.formatFileName(WorkspaceConfig.ELEMENTARY, 110, 64, 2, null);
  }
}
