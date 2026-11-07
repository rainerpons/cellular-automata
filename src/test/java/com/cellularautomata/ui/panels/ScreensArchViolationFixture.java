package com.cellularautomata.ui.panels;

/** Test fixture providing synthetic classes that violate screens architectural boundaries. */
public final class ScreensArchViolationFixture {
  private ScreensArchViolationFixture() {}

  /** Synthetic class in a UI subpackage that violates boundaries by depending on screens. */
  public static class ViolatesUiSubpackagesToScreens {
    com.cellularautomata.ui.screens.ModuleSelectionScreen screen;
  }
}
