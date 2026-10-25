package com.cellularautomata.ui;

import com.cellularautomata.engine.rules.LifeLikeRule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formats default file names for saving rendered cellular automata images. */
final class AutomatonFileNameFormatter {

  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");

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

    String timestamp = time.format(TIMESTAMP_FORMATTER);
    String baseName = config.name().toLowerCase(Locale.ROOT);

    return switch (config) {
      case ELEMENTARY -> "%s_rule%d_size%d_%s.png".formatted(baseName, ruleNumber, size, timestamp);
      case TOTALISTIC ->
          "%s_states%d_rule%d_size%d_%s.png"
              .formatted(baseName, states, ruleNumber, size, timestamp);
      case LIFE_LIKE ->
          throw new UnsupportedOperationException(
              "Life-like rules use birth/survival notation; use formatFileName(LifeLikeRule, int,"
                  + " LocalDateTime)");
    };
  }

  /**
   * Generates a default PNG file name for saving a rendered Life-like automaton image.
   *
   * <p>Replaces the {@code '/'} separator in {@link LifeLikeRule#getNotation()} with {@code '-'} so
   * the resulting file name is valid across POSIX and Windows filesystems (for example, {@code
   * life_like_ruleB3-S23_size8_2026-10-25T12-00-00.png}).
   *
   * @param rule the active Life-like rule
   * @param size the active automaton grid size
   * @param time the timestamp to include in the file name
   * @return the formatted file name
   * @throws IllegalArgumentException if rule or time is null
   */
  static String formatFileName(LifeLikeRule rule, int size, LocalDateTime time) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    if (time == null) {
      throw new IllegalArgumentException("Time cannot be null.");
    }

    String timestamp = time.format(TIMESTAMP_FORMATTER);
    String baseName = WorkspaceConfig.LIFE_LIKE.name().toLowerCase(Locale.ROOT);
    String safeRule = rule.getNotation().replace('/', '-');
    return "%s_rule%s_size%d_%s.png".formatted(baseName, safeRule, size, timestamp);
  }
}
