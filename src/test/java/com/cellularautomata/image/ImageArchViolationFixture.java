package com.cellularautomata.image;

/** Test fixture providing synthetic classes that violate image architectural boundaries. */
public final class ImageArchViolationFixture {
  private ImageArchViolationFixture() {}

  public static class ViolatesImageToJavaFx {
    javafx.scene.Node node;
  }

  public static class ViolatesImageToUiPresentation {
    com.cellularautomata.ui.panels.ParametersPanel panel;
  }
}
