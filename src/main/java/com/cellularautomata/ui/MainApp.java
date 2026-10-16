package com.cellularautomata.ui;

import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ElementaryParametersPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
import com.cellularautomata.ui.panels.LifeLikeToolbar;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.panels.TotalisticParametersPanel;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The MainApp class owns JavaFX UI setup and event wiring, while delegating generation and image
 * work.
 */
public class MainApp extends Application {

  private final DialogService dialogService = new DialogService();

  /** Constructs the main application. */
  public MainApp() {}

  @Override
  public void start(Stage primaryStage) {
    UiFonts.registerBundledFonts();

    primaryStage.setResizable(false);

    showModuleSelectionScreen(primaryStage);

    primaryStage.show();
  }

  private void showModuleSelectionScreen(Stage primaryStage) {
    primaryStage.setTitle("Cellular Automata");

    ModuleSelectionScreen moduleSelectionScreen = new ModuleSelectionScreen();
    moduleSelectionScreen.setOnContinue(config -> launchWorkspace(primaryStage, config));

    Scene scene = new Scene(moduleSelectionScreen);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    primaryStage.setScene(scene);
  }

  private void launchWorkspace(Stage primaryStage, WorkspaceConfig config) {
    primaryStage.setTitle(config.getWindowTitle());

    Label backLink = new Label("← Back to Modules");
    backLink.getStyleClass().add("navigation-link");
    backLink.setOnMouseClicked(e -> showModuleSelectionScreen(primaryStage));

    final VBox displayContainer;
    final SidebarPanel sidebarPanel;
    if (config == WorkspaceConfig.LIFE_LIKE) {
      LifeLikeToolbar toolbar = new LifeLikeToolbar();

      Region footerSpacer = new Region();
      HBox.setHgrow(footerSpacer, Priority.ALWAYS);
      HBox footerRow = new HBox(backLink, footerSpacer, toolbar);
      footerRow.setAlignment(Pos.CENTER_LEFT);
      VBox.setMargin(footerRow, new Insets(UiStyles.APP_SPACING, 0, 0, 0));

      DisplayPanel displayPanel = new DisplayPanel(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT);
      displayContainer = new VBox(displayPanel, footerRow);

      LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
      sidebarPanel = new SidebarPanel(parametersPanel);
    } else {
      DisplayPanel displayPanel = new DisplayPanel();
      VBox.setMargin(backLink, new Insets(UiStyles.APP_SPACING, 0, 0, 0));
      displayContainer = new VBox(displayPanel, backLink);

      CommandsPanel commandsPanel = new CommandsPanel();
      if (config == WorkspaceConfig.TOTALISTIC) {
        TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
        sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
        new MainController(displayPanel, parametersPanel, commandsPanel, dialogService, config);
      } else {
        ElementaryParametersPanel parametersPanel = new ElementaryParametersPanel();
        sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
        new MainController(displayPanel, parametersPanel, commandsPanel, dialogService, config);
      }
    }

    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, Priority.ALWAYS);
    root.getChildren().addAll(displayContainer, sidebarPanel);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }
}
