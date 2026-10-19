package com.cellularautomata.engine;

/** Test fixture providing synthetic classes that violate engine architectural boundaries. */
public final class EngineArchViolationFixture {
  private EngineArchViolationFixture() {}

  public static class ViolatesEngineToUi {
    com.cellularautomata.ui.DialogService dialogService;
  }

  public static class ViolatesEngineToConfig {
    com.cellularautomata.config.WorkspaceConfig config;
  }

  public static class ViolatesEngineToImage {
    com.cellularautomata.image.AutomatonImage image;
  }
}
