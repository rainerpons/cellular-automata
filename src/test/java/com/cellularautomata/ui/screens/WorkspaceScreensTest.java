package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.JavaFxTestHelper;
import com.cellularautomata.ui.WorkspaceConfig;
import com.cellularautomata.ui.components.LifeLikeToolbar;
import com.cellularautomata.ui.components.ModuleCard;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkspaceScreensTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testModuleSelectionScreenInteractionsAndNavigationCallback() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          ModuleSelectionScreen screen = new ModuleSelectionScreen();
          Button continueBtn = (Button) screen.lookup(".continue-button");
          Assert.assertNotNull(continueBtn);
          Assert.assertTrue(continueBtn.isDisabled());

          AtomicReference<WorkspaceConfig> chosenConfig = new AtomicReference<>();
          screen.setOnContinue(chosenConfig::set);

          // Firing continue without selection does not invoke callback
          continueBtn.fire();
          Assert.assertNull(chosenConfig.get());

          ModuleCard elementaryCard =
              findNode(
                  screen, ModuleCard.class, card -> card.getConfig() == WorkspaceConfig.ELEMENTARY);
          ModuleCard totalisticCard =
              findNode(
                  screen, ModuleCard.class, card -> card.getConfig() == WorkspaceConfig.TOTALISTIC);
          ModuleCard lifeLikeCard =
              findNode(
                  screen, ModuleCard.class, card -> card.getConfig() == WorkspaceConfig.LIFE_LIKE);
          Assert.assertNotNull(elementaryCard);
          Assert.assertNotNull(totalisticCard);
          Assert.assertNotNull(lifeLikeCard);

          Assert.assertEquals(WorkspaceConfig.ELEMENTARY, elementaryCard.getConfig());
          Assert.assertEquals(WorkspaceConfig.TOTALISTIC, totalisticCard.getConfig());
          Assert.assertEquals(WorkspaceConfig.LIFE_LIKE, lifeLikeCard.getConfig());

          // Select Elementary card
          elementaryCard.getOnMouseClicked().handle(null);
          Assert.assertFalse(continueBtn.isDisabled());
          Assert.assertTrue(elementaryCard.getStyleClass().contains("module-card-selected"));
          continueBtn.fire();
          Assert.assertEquals(WorkspaceConfig.ELEMENTARY, chosenConfig.get());

          // Switch selection to Totalistic card
          totalisticCard.getOnMouseClicked().handle(null);
          Assert.assertFalse(elementaryCard.getStyleClass().contains("module-card-selected"));
          Assert.assertTrue(totalisticCard.getStyleClass().contains("module-card-selected"));
          continueBtn.fire();
          Assert.assertEquals(WorkspaceConfig.TOTALISTIC, chosenConfig.get());

          // Switch selection to Life-Like card
          lifeLikeCard.getOnMouseClicked().handle(null);
          Assert.assertFalse(totalisticCard.getStyleClass().contains("module-card-selected"));
          Assert.assertTrue(lifeLikeCard.getStyleClass().contains("module-card-selected"));
          continueBtn.fire();
          Assert.assertEquals(WorkspaceConfig.LIFE_LIKE, chosenConfig.get());

          // Setting onContinue to null safely handles clicks without throwing
          screen.setOnContinue(null);
          continueBtn.fire();
        });
  }

  @Test
  public void testElementaryWorkspaceConstructorNullValidation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);

          assertThrows(
              IllegalArgumentException.class,
              () -> new ElementaryWorkspace(null, imageService, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new ElementaryWorkspace(dialogService, null, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new ElementaryWorkspace(dialogService, imageService, null));
        });
  }

  @Test
  public void testElementaryWorkspaceBackLinkExecutesNavigationCallback() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);
          AtomicBoolean navigated = new AtomicBoolean(false);
          ElementaryWorkspace workspace =
              new ElementaryWorkspace(dialogService, imageService, () -> navigated.set(true));

          Label backLink = (Label) workspace.lookup(".navigation-link");
          Assert.assertNotNull(backLink);
          Assert.assertEquals("← Back to Modules", backLink.getText());

          backLink.getOnMouseClicked().handle(null);
          Assert.assertTrue(navigated.get());
        });
  }

  @Test
  public void testTotalisticWorkspaceConstructorNullValidation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);

          assertThrows(
              IllegalArgumentException.class,
              () -> new TotalisticWorkspace(null, imageService, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new TotalisticWorkspace(dialogService, null, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new TotalisticWorkspace(dialogService, imageService, null));
        });
  }

  @Test
  public void testTotalisticWorkspaceBackLinkExecutesNavigationCallback() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);
          AtomicBoolean navigated = new AtomicBoolean(false);
          TotalisticWorkspace workspace =
              new TotalisticWorkspace(dialogService, imageService, () -> navigated.set(true));

          Label backLink = (Label) workspace.lookup(".navigation-link");
          Assert.assertNotNull(backLink);
          Assert.assertEquals("← Back to Modules", backLink.getText());

          backLink.getOnMouseClicked().handle(null);
          Assert.assertTrue(navigated.get());
        });
  }

  @Test
  public void testLifeLikeWorkspaceConstructorNullValidation() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          AutomatonImageService imageService = new AutomatonImageService(new DialogService());

          assertThrows(IllegalArgumentException.class, () -> new LifeLikeWorkspace(null, () -> {}));
          assertThrows(
              IllegalArgumentException.class, () -> new LifeLikeWorkspace(imageService, null));
        });
  }

  @Test
  public void testLifeLikeWorkspaceBackLinkStopsPlaybackAndNavigatesBack() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          AtomicBoolean navigated = new AtomicBoolean(false);
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);
          LifeLikeWorkspace workspace =
              new LifeLikeWorkspace(imageService, () -> navigated.set(true));

          LifeLikeToolbar toolbar = findNode(workspace, LifeLikeToolbar.class, null);
          Assert.assertNotNull(toolbar);

          Button playPauseButton = playPauseButton(toolbar);
          Button resetButton = resetButton(toolbar);
          Button saveButton = saveButton(toolbar);

          // Start simulation playback
          playPauseButton.fire();
          Assert.assertTrue(toolbar.isPlaying());
          Assert.assertFalse(resetButton.isDisabled());

          Label backLink = (Label) workspace.lookup(".navigation-link");
          Assert.assertNotNull(backLink);
          Assert.assertEquals("← Back to Modules", backLink.getText());

          // Clicking the back-link stops playback, resets transport controls, and navigates back
          backLink.getOnMouseClicked().handle(null);
          Assert.assertTrue(navigated.get());
          Assert.assertFalse(toolbar.isPlaying());
          Assert.assertTrue(resetButton.isDisabled());
          Assert.assertTrue(saveButton.isDisabled());
        });
  }

  private static Button findToolbarButton(LifeLikeToolbar toolbar, String accessibleText) {
    for (Node node : toolbar.getChildren()) {
      if (node instanceof Button button && accessibleText.equals(button.getAccessibleText())) {
        return button;
      }
    }
    throw new AssertionError("Toolbar button not found for accessible text: " + accessibleText);
  }

  private static Button playPauseButton(LifeLikeToolbar toolbar) {
    for (Node node : toolbar.getChildren()) {
      if (node instanceof Button button
          && ("Play".equals(button.getAccessibleText())
              || "Pause".equals(button.getAccessibleText()))) {
        return button;
      }
    }
    throw new AssertionError("Play/Pause button not found in toolbar");
  }

  private static Button resetButton(LifeLikeToolbar toolbar) {
    return findToolbarButton(toolbar, "Reset");
  }

  private static Button saveButton(LifeLikeToolbar toolbar) {
    return findToolbarButton(toolbar, "Save");
  }

  private static <T extends Node> T findNode(
      Node root, Class<T> clazz, java.util.function.Predicate<T> predicate) {
    if (clazz.isInstance(root)) {
      T cast = clazz.cast(root);
      if (predicate == null || predicate.test(cast)) {
        return cast;
      }
    }
    if (root instanceof Parent parent) {
      for (Node child : parent.getChildrenUnmodifiable()) {
        T found = findNode(child, clazz, predicate);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static <T extends Throwable> void assertThrows(
      Class<T> expectedType, Runnable executable) {
    try {
      executable.run();
    } catch (Throwable actual) {
      if (expectedType.isInstance(actual)) {
        return;
      }
      throw new AssertionError(
          "Expected "
              + expectedType.getName()
              + " to be thrown, but "
              + actual.getClass().getName()
              + " was thrown instead",
          actual);
    }
    throw new AssertionError(
        "Expected " + expectedType.getName() + " to be thrown, but nothing was thrown");
  }
}
