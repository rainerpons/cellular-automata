package com.cellularautomata.ui.controllers;

/**
 * Common contract for workspace controllers managing cellular automaton modules.
 *
 * <p>Defines a unified interaction contract across module controllers, establishing a common
 * controller API for saving operations without sharing implementation or constraining
 * module-specific generation, parameter capture, or simulation lifecycle coordination.
 */
public interface AutomatonController {

  /**
   * Coordinates saving the currently generated automaton image.
   *
   * <p>Module controllers determine the active automaton image, format the module-specific default
   * export filename, and delegate the save dialog presentation, user cancellation, error handling,
   * and file writing workflow to {@link com.cellularautomata.ui.services.AutomatonImageService}.
   */
  void save();
}
