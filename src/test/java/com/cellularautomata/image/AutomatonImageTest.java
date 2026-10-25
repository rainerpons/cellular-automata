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
  public void testGetImageFromGridMinAndMaxSupportedSizesWithoutRandomness() {
    // Minimum supported size 4
    Grid2D minGrid = new Grid2D(4, (r, c) -> (r + c) % 2 == 0);
    BufferedImage minImg = AutomatonImage.getImageFromGrid(minGrid);
    Assert.assertEquals(4, minImg.getWidth());
    Assert.assertEquals(4, minImg.getHeight());

    // Maximum supported size 128
    Grid2D maxGrid = new Grid2D(128, (r, c) -> (r + c) % 2 == 0);
    BufferedImage maxImg = AutomatonImage.getImageFromGrid(maxGrid);
    Assert.assertEquals(128, maxImg.getWidth());
    Assert.assertEquals(128, maxImg.getHeight());

    // Verify checkerboard pattern deterministically without randomness
    Assert.assertEquals(BLACK_RGB, minImg.getRGB(0, 0) & 0xFFFFFF);
    Assert.assertEquals(WHITE_RGB, minImg.getRGB(1, 0) & 0xFFFFFF);
    Assert.assertEquals(BLACK_RGB, maxImg.getRGB(0, 0) & 0xFFFFFF);
    Assert.assertEquals(WHITE_RGB, maxImg.getRGB(1, 0) & 0xFFFFFF);
    Assert.assertEquals(BLACK_RGB, maxImg.getRGB(127, 127) & 0xFFFFFF);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetImageFromGridRejectsNullGrid() {
    AutomatonImage.getImageFromGrid(null);
  }

  @Test
  public void testResizingGridImageToDisplayImageSizePreservesDimensions() {
    Grid2D grid4 = new Grid2D(4);
    BufferedImage img4 = AutomatonImage.getImageFromGrid(grid4);
    BufferedImage resized4 =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, img4);

    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized4.getWidth());
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized4.getHeight());

    Grid2D grid128 = new Grid2D(128);
    BufferedImage img128 = AutomatonImage.getImageFromGrid(grid128);
    BufferedImage resized128 =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, img128);

    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized128.getWidth());
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized128.getHeight());
  }

  @Test
  public void testLifeLikeBoundaryGridSizes5And155RenderAndResizeDeterministically() {
    // Boundary size 5
    Grid2D grid5 = new Grid2D(5, (r, c) -> (r == 0 && c == 0) || (r == 4 && c == 4));
    BufferedImage img5 = AutomatonImage.getImageFromGrid(grid5);
    Assert.assertEquals(5, img5.getWidth());
    Assert.assertEquals(5, img5.getHeight());
    Assert.assertEquals(BLACK_RGB, img5.getRGB(0, 0) & 0xFFFFFF);
    Assert.assertEquals(WHITE_RGB, img5.getRGB(1, 1) & 0xFFFFFF);
    Assert.assertEquals(BLACK_RGB, img5.getRGB(4, 4) & 0xFFFFFF);

    BufferedImage resized5 =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, img5);
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized5.getWidth());
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized5.getHeight());

    // Boundary size 155
    Grid2D grid155 = new Grid2D(155, (r, c) -> (r == 0 && c == 0) || (r == 154 && c == 154));
    BufferedImage img155 = AutomatonImage.getImageFromGrid(grid155);
    Assert.assertEquals(155, img155.getWidth());
    Assert.assertEquals(155, img155.getHeight());
    Assert.assertEquals(BLACK_RGB, img155.getRGB(0, 0) & 0xFFFFFF);
    Assert.assertEquals(WHITE_RGB, img155.getRGB(1, 1) & 0xFFFFFF);
    Assert.assertEquals(BLACK_RGB, img155.getRGB(154, 154) & 0xFFFFFF);

    BufferedImage resized155 =
        AutomatonImage.resizeImage(
            AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE, img155);
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized155.getWidth());
    Assert.assertEquals(AutomatonImage.DISPLAY_IMAGE_SIZE, resized155.getHeight());
  }

  @Test
  public void testGetRgbForStateComputesEvenlySpacedGrayscale() {
    // 2 states: 0 -> white, 1 -> black
    Assert.assertEquals(0xFFFFFF, AutomatonImage.getRgbForState(0, 2));
    Assert.assertEquals(0x000000, AutomatonImage.getRgbForState(1, 2));

    // 3 states: 0 -> 255, 1 -> 128 (255 - 255/2 = 128), 2 -> 0
    int gray1 = 255 - (1 * 255 / 2);
    int expectedRgb1 = (gray1 << 16) | (gray1 << 8) | gray1;
    Assert.assertEquals(0xFFFFFF, AutomatonImage.getRgbForState(0, 3));
    Assert.assertEquals(expectedRgb1, AutomatonImage.getRgbForState(1, 3));
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
