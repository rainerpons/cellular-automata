package com.cellularautomata.ui;

/** Test fixture providing synthetic classes that violate headless UI architectural boundaries. */
public final class SelectionModelArchViolationFixture {
  private SelectionModelArchViolationFixture() {}

  public static class LifeLikeRuleSelectionModel {
    javafx.scene.control.Button button;
  }

  public static class WorkspaceConfig {
    javafx.scene.control.Button button;
  }

  public static class LifeLikeConfiguration {
    javafx.scene.control.Button button;
  }

  public static class LifeLikeStateMachine {
    javafx.scene.control.Button button;
  }

  public static class AutomatonFileNameFormatter {
    javafx.scene.control.Button button;
  }
}
