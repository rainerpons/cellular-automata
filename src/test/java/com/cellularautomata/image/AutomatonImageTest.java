package com.cellularautomata.image;

import com.cellularautomata.engine.Grid2D;
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

  private static final int BLACK_RGB = 0x000000;
  private static final int WHITE_RGB = 0xFFFFFF;

  @Test
  public void testGetImageFromMapAndResizeImage() {
    Map<Integer, Vector> map =
        Map.of(
            0, new Vector("01"),
            1, new Vector("10"));

    BufferedImage image = AutomatonImage.getImageFromMap(map, 2);
    Assert.assertEquals(2, image.getWidth());
    Assert.assertEquals(2, image.getHeight());
    Assert.assertEquals(WHITE_RGB, image.getRGB(0, 0) & 0xFFFFFF);
    Assert.assertEquals(BLACK_RGB, image.getRGB(1, 0) & 0xFFFFFF);

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

  @Test
  public void testGetImageFromGridWithKnownSmallGridsRendersBlackAndWhitePixels() {
    // 2x2 known grid: alive at (0, 0) and (1, 1), dead at (0, 1) and (1, 0)
    boolean[][] matrix2x2 = {
      {true, false},
      {false, true}
    };
    Grid2D grid2x2 = new Grid2D(2, matrix2x2);
    BufferedImage img2x2 = AutomatonImage.getImageFromGrid(grid2x2);

    Assert.assertEquals(2, img2x2.getWidth());
    Assert.assertEquals(2, img2x2.getHeight());
    Assert.assertEquals(BLACK_RGB, img2x2.getRGB(0, 0) & 0xFFFFFF);
    Assert.assertEquals(WHITE_RGB, img2x2.getRGB(1, 0) & 0xFFFFFF);
    Assert.assertEquals(WHITE_RGB, img2x2.getRGB(0, 1) & 0xFFFFFF);
    Assert.assertEquals(BLACK_RGB, img2x2.getRGB(1, 1) & 0xFFFFFF);

    // 4x4 known grid with single alive cell at row 1, col 2
    Grid2D grid4x4 = new Grid2D(4, (r, c) -> r == 1 && c == 2);
    BufferedImage img4x4 = AutomatonImage.getImageFromGrid(grid4x4);

    Assert.assertEquals(4, img4x4.getWidth());
    Assert.assertEquals(4, img4x4.getHeight());
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++) {
        int expectedRgb = (row == 1 && col == 2) ? BLACK_RGB : WHITE_RGB;
        Assert.assertEquals(
            "Mismatch at row " + row + ", col " + col,
            expectedRgb,
            img4x4.getRGB(col, row) & 0xFFFFFF);
      }
    }
  }

  @Test
  public void testGridImageRenderingAndResizingAtBoundarySizes() {
    for (int size : new int[] {4, 155}) {
      Grid2D grid = new Grid2D(size, (r, c) -> (r + c) % 2 == 0);
      BufferedImage img = AutomatonImage.getImageFromGrid(grid);
      Assert.assertEquals(size, img.getWidth());
      Assert.assertEquals(size, img.getHeight());
      Assert.assertEquals(BLACK_RGB, img.getRGB(0, 0) & 0xFFFFFF);
      Assert.assertEquals(WHITE_RGB, img.getRGB(1, 0) & 0xFFFFFF);

      BufferedImage resized =
          AutomatonImage.resizeImage(
              AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, img);
      Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized.getWidth());
      Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized.getHeight());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetImageFromGridRejectsNullGrid() {
    AutomatonImage.getImageFromGrid(null);
  }

  @Test
  public void testGetRgbForStateComputesEvenlySpacedGrayscale() {
    // 2 states: 0 -> white, 1 -> black
    Assert.assertEquals(0xFFFFFF, AutomatonImage.getRgbForState(0, 2));
    Assert.assertEquals(0x000000, AutomatonImage.getRgbForState(1, 2));

    // 3 states: 0 -> white (0xFFFFFF), 1 -> mid-gray (0x808080), 2 -> black (0x000000)
    Assert.assertEquals(0xFFFFFF, AutomatonImage.getRgbForState(0, 3));
    Assert.assertEquals(0x808080, AutomatonImage.getRgbForState(1, 3));
    Assert.assertEquals(0x000000, AutomatonImage.getRgbForState(2, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRgbForStateRejectsSingleState() {
    AutomatonImage.getRgbForState(0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRgbForStateRejectsZeroStates() {
    AutomatonImage.getRgbForState(0, 0);
  }
}
