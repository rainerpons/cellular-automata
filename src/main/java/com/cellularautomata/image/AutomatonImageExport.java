package com.cellularautomata.image;

import com.cellularautomata.config.WorkspaceConfig;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Export metadata and destination utilities for saving rendered automaton images. Owns default file
 * naming conventions and initial save directory resolution separately from {@link AutomatonImage}
 * pixel rendering and resizing.
 */
public final class AutomatonImageExport {

  private AutomatonImageExport() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Generates a default PNG file name for saving a rendered automaton image.
   *
   * @param config the workspace configuration
   * @param ruleNumber the rule number
   * @param size the automaton size
   * @param states the number of cell states
   * @param time the timestamp to include in the file name
   * @return the formatted file name
   * @throws UnsupportedOperationException if image saving is not supported for the workspace
   */
  public static String generateFileName(
      WorkspaceConfig config, int ruleNumber, int size, int states, LocalDateTime time) {
    String timestamp = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    String baseName = config.name().toLowerCase(Locale.ROOT);

    return switch (config) {
      case ELEMENTARY ->
          String.format("%s_rule%d_size%d_%s.png", baseName, ruleNumber, size, timestamp);
      case TOTALISTIC ->
          String.format(
              "%s_states%d_rule%d_size%d_%s.png", baseName, states, ruleNumber, size, timestamp);
      case LIFE_LIKE ->
          throw new UnsupportedOperationException("Life-like image saving is not yet supported");
    };
  }

  /**
   * Resolves the preferred initial directory for saving automaton images, preferring {@code
   * ~/Desktop} and falling back to {@code user.home}.
   *
   * @return the initial save directory, or {@code null} if neither exists
   */
  public static File resolveInitialSaveDirectory() {
    String userHome = System.getProperty("user.home");
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
