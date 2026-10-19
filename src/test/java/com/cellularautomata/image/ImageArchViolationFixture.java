package com.cellularautomata.image;

/** Test fixture providing synthetic classes that violate image architectural boundaries. */
public final class ImageArchViolationFixture {
  private ImageArchViolationFixture() {}

  public static class ViolatesImageToUi {
    javafx.scene.Node node;
  }
}
