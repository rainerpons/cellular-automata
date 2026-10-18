package com.cellularautomata.image;

import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.engine.Vector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonImage}. */
public class AutomatonImageTest {

  @Test
  public void testGenerateFileNameElementary() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName = AutomatonImage.generateFileName(WorkspaceConfig.ELEMENTARY, 110, 64, 2, time);

    Assert.assertEquals("elementary_rule110_size64_2026-07-13T15-30-45.png", fileName);
  }

  @Test
  public void testGenerateFileNameTotalistic() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        AutomatonImage.generateFileName(WorkspaceConfig.TOTALISTIC, 777, 128, 3, time);

    Assert.assertEquals("totalistic_states3_rule777_size128_2026-07-13T15-30-45.png", fileName);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGenerateFileNameLifeLikeThrows() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    AutomatonImage.generateFileName(WorkspaceConfig.LIFE_LIKE, 0, 64, 2, time);
  }

  @Test
  public void testGetImageFromMapAndResizeImage() {
    Map<Integer, Vector> map =
        Map.of(
            0, new Vector("01"),
            1, new Vector("10"));

    BufferedImage image = AutomatonImage.getImageFromMap(map, 2);
    Assert.assertEquals(2, image.getWidth());
    Assert.assertEquals(2, image.getHeight());
    Assert.assertEquals(0xFFFFFF, image.getRGB(0, 0) & 0xFFFFFF);
    Assert.assertEquals(0x000000, image.getRGB(1, 0) & 0xFFFFFF);

    BufferedImage resized =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, image);
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized.getWidth());
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized.getHeight());
  }

  @Test
  public void testResolveInitialSaveDirectory() {
    File dir = AutomatonImage.resolveInitialSaveDirectory();
    if (dir != null) {
      Assert.assertTrue(dir.exists());
      Assert.assertTrue(dir.isDirectory());
    }
  }
}
