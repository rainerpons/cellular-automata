package com.cellularautomata.image;

import java.io.File;

/**
 * Destination utilities for saving rendered automaton images. Owns initial save directory
 * resolution separately from {@link AutomatonImage} pixel rendering and resizing.
 */
public final class AutomatonImageExport {

  private AutomatonImageExport() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Resolves the preferred initial directory for saving automaton images, preferring {@code
   * ~/Desktop} and falling back to {@code user.home}.
   *
   * @return the initial save directory, or {@code null} if neither exists
   */
  public static File resolveInitialSaveDirectory() {
    return resolveInitialSaveDirectory(System.getProperty("user.home"));
  }

  static File resolveInitialSaveDirectory(String userHome) {
    if (userHome == null || userHome.isBlank()) {
      return null;
    }
    File desktop = new File(userHome, "Desktop");
    if (desktop.exists() && desktop.isDirectory()) {
      return desktop;
    }
    File home = new File(userHome);
    if (home.exists() && home.isDirectory()) {
      return home;
    }
    return null;
  }
}
