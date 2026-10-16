package com.cellularautomata.ui.shared;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.function.IntFunction;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * A reusable horizontal slider control paired with a dynamically updated value label. The label is
 * given a fixed minimum width so changes in numeric digit length do not shift the slider track.
 */
public final class LabeledSlider {
  private final Label label;
  private final Slider slider;

  /**
   * Constructs a labeled slider with the specified range, tick configuration, and label formatter.
   *
   * @param min the minimum slider value
   * @param max the maximum slider value
   * @param defaultValue the initial slider value
   * @param majorTickUnit the unit distance between major tick marks
   * @param showTicks whether tick marks and tick labels are displayed
   * @param snapToTicks whether the slider value snaps to ticks
   * @param labelFormatter formats the rounded integer value into the label text
   */
  public LabeledSlider(
      int min,
      int max,
      int defaultValue,
      double majorTickUnit,
      boolean showTicks,
      boolean snapToTicks,
      IntFunction<String> labelFormatter) {
    this.label = new Label(labelFormatter.apply(defaultValue));
    this.label.setMinWidth(UiStyles.FORM_LABEL_COLUMN_MIN_WIDTH);
    this.label.managedProperty().bind(this.label.visibleProperty());

    this.slider = new Slider(min, max, defaultValue);
    this.slider.managedProperty().bind(this.slider.visibleProperty());
    this.slider.setBlockIncrement(1);
    this.slider.setMajorTickUnit(majorTickUnit);
    this.slider.setMinorTickCount(0);
    this.slider.setShowTickMarks(showTicks);
    this.slider.setShowTickLabels(showTicks);
    this.slider.setSnapToTicks(snapToTicks);
    this.slider.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(this.slider, Priority.ALWAYS);
    this.slider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                this.label.setText(labelFormatter.apply((int) Math.round(newVal.doubleValue()))));
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification =
          "JavaFX label node is intentionally exposed for grid layout and tooltip attachment")
  public Label getLabel() {
    return label;
  }

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "JavaFX slider node is intentionally exposed for grid layout and inspection")
  public Slider getSlider() {
    return slider;
  }

  public int getValue() {
    return (int) Math.round(slider.getValue());
  }

  public void setValue(double value) {
    slider.setValue(value);
  }

  public String getLabelText() {
    return label.getText();
  }

  public void setVisible(boolean visible) {
    label.setVisible(visible);
    slider.setVisible(visible);
  }

  public boolean isVisible() {
    return label.isVisible() && slider.isVisible();
  }

  public boolean isManaged() {
    return label.isManaged() && slider.isManaged();
  }
}
