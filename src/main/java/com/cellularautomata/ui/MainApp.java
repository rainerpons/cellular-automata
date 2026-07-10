package com.cellularautomata.ui;

import com.cellularautomata.config.WorkspaceConfig;
import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

    DisplayPanel displayPanel = new DisplayPanel();
    Label backLink = new Label("← Back to Modules");
    backLink.getStyleClass().add("navigation-link");
    backLink.setOnMouseClicked(e -> showModuleSelectionScreen(primaryStage));
    VBox.setMargin(backLink, new Insets(UiStyles.APP_SPACING, 0, 0, 0));
    VBox displayContainer = new VBox(displayPanel, backLink);
    ParametersPanel parametersPanel = new ParametersPanel(config);
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);

    new MainController(displayPanel, parametersPanel, commandsPanel, dialogService, config);

    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, javafx.scene.layout.Priority.ALWAYS);
    root.getChildren().addAll(displayContainer, sidebarPanel);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }
}
