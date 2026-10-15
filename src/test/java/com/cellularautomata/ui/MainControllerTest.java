package com.cellularautomata.ui;

import com.cellularautomata.config.WorkspaceConfig;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

public class MainControllerTest {

  @Test
  public void testGenerateFileNameElementary() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName = MainController.generateFileName(WorkspaceConfig.ELEMENTARY, 110, 64, 2, time);

    // Elementary format should be: elementary_rule110_size64_2026-07-13T15-30-45.png
    Assert.assertEquals("elementary_rule110_size64_2026-07-13T15-30-45.png", fileName);
  }

  @Test
  public void testGenerateFileNameTotalistic() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        MainController.generateFileName(WorkspaceConfig.TOTALISTIC, 777, 128, 3, time);

    // Totalistic format should be: totalistic_states3_rule777_size128_2026-07-13T15-30-45.png
    Assert.assertEquals("totalistic_states3_rule777_size128_2026-07-13T15-30-45.png", fileName);
  }
}
