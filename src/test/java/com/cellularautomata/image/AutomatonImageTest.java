package com.cellularautomata.image;

import com.cellularautomata.engine.Vector;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonImage}. */
public class AutomatonImageTest {

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
  public void testSaveImageWritesReadablePngFile() throws IOException {
    Map<Integer, Vector> map =
        Map.of(
            0, new Vector("01"),
            1, new Vector("10"));
    BufferedImage image = AutomatonImage.getImageFromMap(map, 2);
    Path tempFile = Files.createTempFile("automaton-test-", ".png");
    try {
      AutomatonImage.saveImage(image, tempFile.toFile());
      BufferedImage reloaded = ImageIO.read(tempFile.toFile());
      Assert.assertNotNull(reloaded);
      Assert.assertEquals(2, reloaded.getWidth());
      Assert.assertEquals(2, reloaded.getHeight());
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }
}
