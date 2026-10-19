package com.cellularautomata.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formats default file names for saving rendered cellular automata images. */
final class AutomatonFileNameFormatter {

  private AutomatonFileNameFormatter() {
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
   * @throws IllegalArgumentException if config or time is null
   * @throws UnsupportedOperationException if image saving is not supported for the workspace
   */
  static String formatFileName(
      WorkspaceConfig config, int ruleNumber, int size, int states, LocalDateTime time) {
    if (config == null) {
      throw new IllegalArgumentException("WorkspaceConfig cannot be null.");
    }
    if (time == null) {
      throw new IllegalArgumentException("Time cannot be null.");
    }

    String timestamp = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    String baseName = config.name().toLowerCase(Locale.ROOT);

    return switch (config) {
      case ELEMENTARY -> "%s_rule%d_size%d_%s.png".formatted(baseName, ruleNumber, size, timestamp);
      case TOTALISTIC ->
          "%s_states%d_rule%d_size%d_%s.png"
              .formatted(baseName, states, ruleNumber, size, timestamp);
      case LIFE_LIKE ->
          throw new UnsupportedOperationException("Life-like image saving is not yet supported");
    };
  }
}
