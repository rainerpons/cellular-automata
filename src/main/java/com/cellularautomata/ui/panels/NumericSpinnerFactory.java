package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import java.util.function.IntSupplier;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

/**
 * Package-private factory for editable numeric and dynamic-range rule spinners used by the
 * one-dimensional parameter panels ({@link ElementaryParametersPanel} and {@link
 * TotalisticParametersPanel}).
 */
final class NumericSpinnerFactory {
  private NumericSpinnerFactory() {}

  /**
   * Creates an editable integer spinner bounded by a fixed range.
   *
   * @param min the minimum spinner value
   * @param max the maximum spinner value
   * @param defaultValue the initial spinner value
   * @return the configured integer spinner
   */
  static Spinner<Integer> createIntegerSpinner(int min, int max, int defaultValue) {
    SpinnerValueFactory<Integer> valueFactory =
        new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, defaultValue);
    return createNumericSpinner(valueFactory);
  }

  /**
   * Creates an editable rule number spinner with dynamic maximum rule bounds.
   *
   * @param minRule the minimum allowed rule number
   * @param defaultRule the initial rule number
   * @param maxRuleSupplier supplier for the current maximum allowed rule number
   * @return the configured rule spinner
   */
  static Spinner<Integer> createRuleSpinner(
      int minRule, int defaultRule, IntSupplier maxRuleSupplier) {
    SpinnerValueFactory<Integer> valueFactory =
        new SpinnerValueFactory<Integer>() {
          @Override
          public void decrement(int steps) {
            int current = getValue() == null ? minRule : getValue();
            setValue(Math.max(minRule, current - steps));
          }

          @Override
          public void increment(int steps) {
            int maxRule = maxRuleSupplier.getAsInt();
            int current = getValue() == null ? minRule : getValue();
            setValue(Math.min(maxRule, current + steps));
          }
        };
    valueFactory.setValue(defaultRule);
    valueFactory.setConverter(
        new StringConverter<Integer>() {
          @Override
          public String toString(Integer value) {
            return value == null ? "" : value.toString();
          }

          @Override
          public Integer fromString(String string) {
            try {
              if (string == null || string.isEmpty()) {
                return valueFactory.getValue();
              }
              return Integer.parseInt(string);
            } catch (NumberFormatException e) {
              return valueFactory.getValue();
            }
          }
        });

    return createNumericSpinner(valueFactory);
  }

  private static <T> Spinner<T> createNumericSpinner(SpinnerValueFactory<T> factory) {
    Spinner<T> spinner = new Spinner<>();
    spinner.setValueFactory(factory);
    spinner.setEditable(true);
    spinner
        .getEditor()
        .setTextFormatter(
            new TextFormatter<>(
                change -> {
                  if (!change.getControlNewText().matches("\\d*")) {
                    return null;
                  }
                  return change;
                }));
    spinner.getEditor().setAlignment(Pos.CENTER_LEFT);
    UiStyles.applyControlHeight(spinner);
    spinner.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(spinner, Priority.ALWAYS);
    return spinner;
  }
}
