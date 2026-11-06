package com.cellularautomata.ui;

import com.cellularautomata.engine.rules.ElementaryRule;
import com.cellularautomata.engine.rules.LifeLikeRule;
import com.cellularautomata.engine.rules.TotalisticRule;
import java.util.Locale;

/** Formats default file names for saving rendered cellular automata images. */
public final class AutomatonFileNameFormatter {

  private AutomatonFileNameFormatter() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Generates a default PNG file name for saving a rendered Elementary automaton image.
   *
   * @param rule the Elementary rule
   * @param size the automaton size
   * @return the formatted file name
   * @throws IllegalArgumentException if rule is null
   */
  public static String formatFileName(ElementaryRule rule, int size) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    String baseName = WorkspaceConfig.ELEMENTARY.name().toLowerCase(Locale.ROOT);
    return "%s_rule%d_size%d.png".formatted(baseName, rule.getRuleNumber(), size);
  }

  /**
   * Generates a default PNG file name for saving a rendered Totalistic automaton image.
   *
   * @param rule the Totalistic rule
   * @param size the automaton size
   * @return the formatted file name
   * @throws IllegalArgumentException if rule is null
   */
  public static String formatFileName(TotalisticRule rule, int size) {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.");
    }
    String baseName = WorkspaceConfig.TOTALISTIC.name().toLowerCase(Locale.ROOT);
    return "%s_states%d_rule%d_size%d.png"
        .formatted(baseName, rule.getStates(), rule.getRuleNumber(), size);
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
