package com.cellularautomata.config;

/** Test fixture providing synthetic classes that violate config architectural boundaries. */
public final class ConfigArchViolationFixture {
  private ConfigArchViolationFixture() {}

  public static class ViolatesConfigToEngine {
    com.cellularautomata.engine.rules.ElementaryRule rule;
  }

  public static class ViolatesConfigToUi {
    javafx.scene.control.Button button;
  }

  public static class ViolatesConfigToImage {
    com.cellularautomata.image.AutomatonImage image;
  }
}
