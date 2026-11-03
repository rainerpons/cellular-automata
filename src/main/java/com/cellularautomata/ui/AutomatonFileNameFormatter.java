package com.cellularautomata.ui;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.TotalisticRule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formats default file names for saving rendered cellular automata images. */
public final class AutomatonFileNameFormatter {

  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");

  private AutomatonFileNameFormatter() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Generates a default PNG file name for saving a rendered Elementary automaton image.
   *
   * @param rule the Elementary rule
   * @param size the automaton size
   * @param time the timestamp to include in the file name
   * @return the formatted file name
   * @throws IllegalArgumentException if rule or time is null
   */
  public static String formatFileName(ElementaryRule rule, int size, LocalDateTime time) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    if (time == null) {
      throw new IllegalArgumentException("Time cannot be null.");
    }

    String timestamp = time.format(TIMESTAMP_FORMATTER);
    String baseName = WorkspaceConfig.ELEMENTARY.name().toLowerCase(Locale.ROOT);
    return "%s_rule%d_size%d_%s.png".formatted(baseName, rule.getRuleNumber(), size, timestamp);
  }

  /**
   * Generates a default PNG file name for saving a rendered Totalistic automaton image.
   *
   * @param rule the Totalistic rule
   * @param size the automaton size
   * @param time the timestamp to include in the file name
   * @return the formatted file name
   * @throws IllegalArgumentException if rule or time is null
   */
  public static String formatFileName(TotalisticRule rule, int size, LocalDateTime time) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    if (time == null) {
      throw new IllegalArgumentException("Time cannot be null.");
    }

    String timestamp = time.format(TIMESTAMP_FORMATTER);
    String baseName = WorkspaceConfig.TOTALISTIC.name().toLowerCase(Locale.ROOT);
    return "%s_states%d_rule%d_size%d_%s.png"
        .formatted(baseName, rule.getStates(), rule.getRuleNumber(), size, timestamp);
  }

  /**
   * Generates a default PNG file name for saving a rendered Life-like automaton image.
   *
   * <p>Replaces the {@code '/'} separator in {@link LifeLikeRule#getNotation()} with {@code '-'} so
   * the resulting file name is valid across POSIX and Windows filesystems (for example, {@code
   * life_like_ruleB3-S23_size8.png}).
   *
   * @param rule the active Life-like rule
   * @param size the active automaton grid size
   * @return the formatted file name
   * @throws IllegalArgumentException if rule is null
   */
  public static String formatFileName(LifeLikeRule rule, int size) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }

    String baseName = WorkspaceConfig.LIFE_LIKE.name().toLowerCase(Locale.ROOT);
    String safeRule = rule.getNotation().replace('/', '-');
    return "%s_rule%s_size%d.png".formatted(baseName, safeRule, size);
  }
}
