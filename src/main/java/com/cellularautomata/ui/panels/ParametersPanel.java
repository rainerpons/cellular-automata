package com.cellularautomata.ui.panels;

import com.cellularautomata.engine.SeedType;
import com.cellularautomata.ui.shared.LabeledSlider;
import com.cellularautomata.ui.shared.UiStyles;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Reusable two-column grid container for the Parameters section of a workspace sidebar. Owns the
 * section heading, column sizing constraints, row insertion, tooltip attachment on parameter
 * labels, and shared parameter rows.
 */
public final class ParametersPanel extends GridPane {
  public static final int MIN_SIZE = 4;
  public static final int MAX_SIZE = 128;
  public static final int DEFAULT_SIZE = 8;

  public static final String SEED_TYPE_TOOLTIP =
      "Determines how cells are initially populated when generating a new automaton.";

  public static final List<SeedType> ONE_DIMENSIONAL_SEEDS =
      List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING);
  public static final List<SeedType> TWO_DIMENSIONAL_SEEDS =
      List.of(SeedType.DENSITY, SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING);

  private int currentRow = 1;

  /** Constructs a parameters panel with the section heading and two-column constraints. */
  public ParametersPanel() {
    setHgap(UiStyles.FORM_LABEL_COLUMN_GAP);

    ColumnConstraints labelColumn = new ColumnConstraints();
    labelColumn.setMinWidth(UiStyles.FORM_LABEL_COLUMN_MIN_WIDTH);
    ColumnConstraints controlColumn = new ColumnConstraints();
    controlColumn.setHgrow(Priority.ALWAYS);
    getColumnConstraints().addAll(labelColumn, controlColumn);

    addHeading();
  }

  /**
   * Adds the standard horizontal Size slider row (4 to 128, default 8) to the panel.
   *
   * @return the created {@link LabeledSlider}
   */
  public LabeledSlider addSizeSliderRow() {
    LabeledSlider sizeSlider =
        new LabeledSlider(
            MIN_SIZE, MAX_SIZE, DEFAULT_SIZE, 1, false, true, size -> "Size (%d)".formatted(size));
    addSliderRow(sizeSlider, null, true);
    return sizeSlider;
  }

  /**
   * Adds a horizontal {@link LabeledSlider} row with its label in column 0 and slider in column 1.
   *
   * @param labeledSlider the labeled slider to add
   * @param tooltipText optional tooltip text for the label, or {@code null} for no tooltip
   * @param includeBottomGap whether to apply the standard row bottom margin
   * @return the row's label control
   */
  public Label addSliderRow(
      LabeledSlider labeledSlider, String tooltipText, boolean includeBottomGap) {
    Label label = labeledSlider.getLabel();
    if (tooltipText != null) {
      UiStyles.installTooltip(label, tooltipText);
    }
    if (includeBottomGap) {
      Insets bottomMargin = new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0);
      GridPane.setMargin(label, bottomMargin);
      GridPane.setMargin(labeledSlider.getSlider(), bottomMargin);
    }
    add(label, 0, currentRow);
    add(labeledSlider.getSlider(), 1, currentRow++);
    return label;
  }

  /**
   * Adds a labeled parameter control row with the label in column 0 and control in column 1.
   *
   * @param labelText the parameter label text
   * @param tooltipText optional tooltip text for the label, or {@code null} for no tooltip
   * @param control the control node for column 1
   * @param includeBottomGap whether to apply the standard row bottom margin
   * @return the created label control
   */
  public Label addControlRow(
      String labelText, String tooltipText, Node control, boolean includeBottomGap) {
    Label label = new Label(labelText);
    if (tooltipText != null) {
      UiStyles.installTooltip(label, tooltipText);
    }
    if (includeBottomGap) {
      Insets bottomMargin = new Insets(0, 0, UiStyles.FORM_ROW_BOTTOM_GAP, 0);
      GridPane.setMargin(label, bottomMargin);
      GridPane.setMargin(control, bottomMargin);
    }
    add(label, 0, currentRow);
    add(control, 1, currentRow++);
    return label;
  }

  /**
   * Adds a Seed type dropdown row with the shared Seed type tooltip on its label.
   *
   * @param seedTypes the selectable seed types
   * @param defaultSeedType the initially selected seed type
   * @param includeBottomGap whether to apply the standard row bottom margin
   * @return the created combo box
   */
  public ComboBox<SeedType> addSeedTypeRow(
      List<SeedType> seedTypes, SeedType defaultSeedType, boolean includeBottomGap) {
    ComboBox<SeedType> seedComboBox = UiStyles.createDropdown(seedTypes, defaultSeedType);
    addControlRow("Seed type", SEED_TYPE_TOOLTIP, seedComboBox, includeBottomGap);
    return seedComboBox;
  }

  Label findLabelByPrefix(String prefix) {
    for (Node child : getChildren()) {
      Integer col = GridPane.getColumnIndex(child);
      if (child instanceof Label label
          && (col == null || col == 0)
          && label.getText() != null
          && label.getText().startsWith(prefix)) {
        return label;
      }
    }
    return null;
  }

  private void addHeading() {
    final Label heading = UiStyles.createHeading("Parameters");
    GridPane.setColumnSpan(heading, 2);
    GridPane.setMargin(heading, new Insets(0, 0, UiStyles.PARAMETERS_HEADING_GAP, 0));
    add(heading, 0, 0);
  }
}
