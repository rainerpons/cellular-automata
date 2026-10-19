package com.cellularautomata.ui;

import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ElementaryParametersPanel;
import com.cellularautomata.ui.panels.LifeLikeParametersPanel;
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

  HBox createWorkspace(WorkspaceConfig config, Runnable onNavigateBack) {
    Label backLink = createBackLink(onNavigateBack);
    return switch (config) {
      case ELEMENTARY -> createElementaryWorkspace(backLink);
      case TOTALISTIC -> createTotalisticWorkspace(backLink);
      case LIFE_LIKE -> createLifeLikeWorkspace(backLink);
    };
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

    HBox root = createWorkspace(config, () -> showModuleSelectionScreen(primaryStage));

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  private static Label createBackLink(Runnable onNavigateBack) {
    Label backLink = new Label("← Back to Modules");
    backLink.getStyleClass().add("navigation-link");
    backLink.setOnMouseClicked(e -> onNavigateBack.run());
    return backLink;
  }

  private HBox createElementaryWorkspace(Label backLink) {
    DisplayPanel displayPanel = new DisplayPanel();
    ElementaryParametersPanel parametersPanel = new ElementaryParametersPanel();
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
    new MainController(
        displayPanel, parametersPanel, commandsPanel, dialogService, WorkspaceConfig.ELEMENTARY);
    return createWorkspaceRoot(
        createOneDimensionalDisplayContainer(displayPanel, backLink), sidebarPanel);
  }

  private HBox createTotalisticWorkspace(Label backLink) {
    DisplayPanel displayPanel = new DisplayPanel();
    TotalisticParametersPanel parametersPanel = new TotalisticParametersPanel();
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
    new MainController(
        displayPanel, parametersPanel, commandsPanel, dialogService, WorkspaceConfig.TOTALISTIC);
    return createWorkspaceRoot(
        createOneDimensionalDisplayContainer(displayPanel, backLink), sidebarPanel);
  }

  private HBox createLifeLikeWorkspace(Label backLink) {
    LifeLikeToolbar toolbar = new LifeLikeToolbar();

    Region footerSpacer = new Region();
    HBox.setHgrow(footerSpacer, Priority.ALWAYS);
    HBox footerRow = new HBox(backLink, footerSpacer, toolbar);
    footerRow.setAlignment(Pos.CENTER_LEFT);
    VBox.setMargin(footerRow, new Insets(UiStyles.APP_SPACING, 0, 0, 0));

    DisplayPanel displayPanel = new DisplayPanel(DisplayPanel.LIFE_LIKE_PLACEHOLDER_TEXT);
    VBox displayContainer = new VBox(displayPanel, footerRow);

    LifeLikeParametersPanel parametersPanel = new LifeLikeParametersPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel);
    return createWorkspaceRoot(displayContainer, sidebarPanel);
  }

  private static VBox createOneDimensionalDisplayContainer(
      DisplayPanel displayPanel, Label backLink) {
    VBox.setMargin(backLink, new Insets(UiStyles.APP_SPACING, 0, 0, 0));
    return new VBox(displayPanel, backLink);
  }

  private static HBox createWorkspaceRoot(VBox displayContainer, SidebarPanel sidebarPanel) {
    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, Priority.ALWAYS);
    root.getChildren().addAll(displayContainer, sidebarPanel);
    return root;
  }
}
