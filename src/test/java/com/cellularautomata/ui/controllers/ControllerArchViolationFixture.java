package com.cellularautomata.ui.controllers;

/** Test fixture providing synthetic controller classes that violate architectural rules. */
public final class ControllerArchViolationFixture {
  private ControllerArchViolationFixture() {}

  /** Noncompliant controller class that does not implement {@link AutomatonController}. */
  public static class NoncompliantController {}
}
