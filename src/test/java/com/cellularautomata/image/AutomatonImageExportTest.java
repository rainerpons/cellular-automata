package com.cellularautomata.image;

import com.cellularautomata.config.WorkspaceConfig;
import java.io.File;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonImageExport}. */
public class AutomatonImageExportTest {

  @Test
  public void testGenerateFileNameElementary() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        AutomatonImageExport.generateFileName(WorkspaceConfig.ELEMENTARY, 110, 64, 2, time);

    Assert.assertEquals("elementary_rule110_size64_2026-07-13T15-30-45.png", fileName);
  }

  @Test
  public void testGenerateFileNameTotalistic() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        AutomatonImageExport.generateFileName(WorkspaceConfig.TOTALISTIC, 777, 128, 3, time);

    Assert.assertEquals("totalistic_states3_rule777_size128_2026-07-13T15-30-45.png", fileName);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGenerateFileNameLifeLikeThrows() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    AutomatonImageExport.generateFileName(WorkspaceConfig.LIFE_LIKE, 0, 64, 2, time);
  }

  @Test
  public void testResolveInitialSaveDirectory() {
    File dir = AutomatonImageExport.resolveInitialSaveDirectory();
    if (dir != null) {
      Assert.assertTrue(dir.exists());
      Assert.assertTrue(dir.isDirectory());
    }
  }
}
