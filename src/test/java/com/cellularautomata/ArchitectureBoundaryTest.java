package com.cellularautomata;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.cellularautomata.engine.EngineArchViolationFixture;
import com.cellularautomata.engine.rules.RulesArchViolationFixture;
import com.cellularautomata.image.ImageArchViolationFixture;
import com.cellularautomata.ui.SelectionModelArchViolationFixture;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ArchUnit dependency boundary tests verifying the architectural contracts documented in {@code
 * docs/ARCHITECTURE.md}.
 *
 * <p>These tests intentionally enforce high-level package ownership boundaries ({@code UI ->
 * Engine}, {@code Image -> Engine}) without constraining internal class structure.
 */
public class ArchitectureBoundaryTest {
  private static JavaClasses productionClasses;

  @BeforeClass
  public static void importProductionClasses() {
    productionClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.cellularautomata");
  }

  // --- Rule Definitions ---

  public static ArchRule engineDoesNotDependOnUiOrJavaFxRule() {
    return noClasses()
        .that()
        .resideInAPackage("..engine..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..ui..", "javafx..")
        .because(
            "Engine owns mathematical rules, validation, and simulation state and must remain"
                + " independent of JavaFX presentation, controllers, and UI configurations");
  }

  public static ArchRule engineDoesNotDependOnImageRule() {
    return noClasses()
        .that()
        .resideInAPackage("com.cellularautomata.engine..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("com.cellularautomata.image..")
        .because("Engine must remain self-contained and not depend on image utilities");
  }

  public static ArchRule engineRulesDoNotDependOnPresetsRule() {
    return noClasses()
        .that()
        .resideInAPackage("..engine.rules..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..engine.presets..")
        .because("Presets must depend on mathematical rules, never the reverse");
  }

  public static ArchRule engineRulesDoNotDependOnAutomataOrGeneratorRule() {
    return noClasses()
        .that()
        .resideInAPackage("com.cellularautomata.engine.rules..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("com.cellularautomata.engine.automata..")
        .orShould()
        .dependOnClassesThat()
        .haveFullyQualifiedName("com.cellularautomata.engine.Generator")
        .because(
            "Engine rules are pure mathematical rule definitions and must not depend on simulation"
                + " runners or generation algorithms");
  }

  public static ArchRule lifeLikeRuleSelectionModelDoesNotDependOnJavaFxRule() {
    return noClasses()
        .that()
        .haveSimpleName("LifeLikeRuleSelectionModel")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("javafx..")
        .because(
            "LifeLikeRuleSelectionModel must manage selection state and preset synchronization"
                + " without JavaFX dependencies so it is independently testable");
  }

  public static ArchRule workspaceConfigDoesNotDependOnJavaFxRule() {
    return noClasses()
        .that()
        .haveSimpleName("WorkspaceConfig")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("javafx..")
        .because(
            "WorkspaceConfig owns workspace metadata and must remain headless without JavaFX dependencies");
  }

  public static ArchRule lifeLikeConfigurationDoesNotDependOnJavaFxRule() {
    return noClasses()
        .that()
        .haveSimpleName("LifeLikeConfiguration")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("javafx..")
        .because(
            "LifeLikeConfiguration is an immutable configuration model and must remain headless"
                + " without JavaFX dependencies");
  }

  public static ArchRule lifeLikeStateMachineDoesNotDependOnJavaFxRule() {
    return noClasses()
        .that()
        .haveSimpleName("LifeLikeStateMachine")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("javafx..")
        .because(
            "LifeLikeStateMachine manages lifecycle states without JavaFX dependencies so it is"
                + " independently testable");
  }

  public static ArchRule imageDoesNotDependOnUiOrJavaFxRule() {
    return noClasses()
        .that()
        .resideInAPackage("com.cellularautomata.image..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("com.cellularautomata.ui..", "javafx..")
        .because(
            "Image utilities must remain headless and strictly independent of UI components and configurations");
  }

  // --- Production Verification Tests ---

  @Test
  public void testEngineDoesNotDependOnUiOrJavaFx() {
    engineDoesNotDependOnUiOrJavaFxRule().check(productionClasses);
  }

  @Test
  public void testEngineDoesNotDependOnImage() {
    engineDoesNotDependOnImageRule().check(productionClasses);
  }

  @Test
  public void testEngineRulesDoNotDependOnPresets() {
    engineRulesDoNotDependOnPresetsRule().check(productionClasses);
  }

  @Test
  public void testEngineRulesDoNotDependOnAutomataOrGenerator() {
    engineRulesDoNotDependOnAutomataOrGeneratorRule().check(productionClasses);
  }

  @Test
  public void testLifeLikeRuleSelectionModelDoesNotDependOnJavaFx() {
    lifeLikeRuleSelectionModelDoesNotDependOnJavaFxRule().check(productionClasses);
  }

  @Test
  public void testWorkspaceConfigDoesNotDependOnJavaFx() {
    workspaceConfigDoesNotDependOnJavaFxRule().check(productionClasses);
  }

  @Test
  public void testLifeLikeConfigurationDoesNotDependOnJavaFx() {
    lifeLikeConfigurationDoesNotDependOnJavaFxRule().check(productionClasses);
  }

  @Test
  public void testLifeLikeStateMachineDoesNotDependOnJavaFx() {
    lifeLikeStateMachineDoesNotDependOnJavaFxRule().check(productionClasses);
  }

  @Test
  public void testImageDoesNotDependOnUiOrJavaFx() {
    imageDoesNotDependOnUiOrJavaFxRule().check(productionClasses);
  }

  // --- Deliberate Violation Detection Tests ---

  @Test
  public void testEngineDoesNotDependOnUiOrJavaFxDetectsViolation() {
    ArchRule rule = engineDoesNotDependOnUiOrJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter().importClasses(EngineArchViolationFixture.ViolatesEngineToUi.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("ViolatesEngineToUi"));
    Assert.assertTrue(error.getMessage().contains("com.cellularautomata.ui.DialogService"));
  }

  @Test
  public void testEngineDoesNotDependOnImageDetectsViolation() {
    ArchRule rule = engineDoesNotDependOnImageRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(EngineArchViolationFixture.ViolatesEngineToImage.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("ViolatesEngineToImage"));
    Assert.assertTrue(error.getMessage().contains("com.cellularautomata.image.AutomatonImage"));
  }

  @Test
  public void testEngineRulesDoNotDependOnPresetsDetectsViolation() {
    ArchRule rule = engineRulesDoNotDependOnPresetsRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(RulesArchViolationFixture.ViolatesRulesToPresets.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("ViolatesRulesToPresets"));
    Assert.assertTrue(
        error.getMessage().contains("com.cellularautomata.engine.presets.LifeLikePreset"));
  }

  @Test
  public void testEngineRulesDoNotDependOnAutomataOrGeneratorDetectsViolation() {
    ArchRule rule = engineRulesDoNotDependOnAutomataOrGeneratorRule();

    JavaClasses violatingAutomata =
        new ClassFileImporter()
            .importClasses(RulesArchViolationFixture.ViolatesRulesToAutomata.class);
    AssertionError error1 = assertThrows(AssertionError.class, () -> rule.check(violatingAutomata));
    Assert.assertTrue(error1.getMessage().contains("ViolatesRulesToAutomata"));
    Assert.assertTrue(
        error1.getMessage().contains("com.cellularautomata.engine.automata.LifeLikeSimulation"));

    JavaClasses violatingGenerator =
        new ClassFileImporter()
            .importClasses(RulesArchViolationFixture.ViolatesRulesToGenerator.class);
    AssertionError error2 =
        assertThrows(AssertionError.class, () -> rule.check(violatingGenerator));
    Assert.assertTrue(error2.getMessage().contains("ViolatesRulesToGenerator"));
    Assert.assertTrue(error2.getMessage().contains("com.cellularautomata.engine.Generator"));
  }

  @Test
  public void testLifeLikeRuleSelectionModelDoesNotDependOnJavaFxDetectsViolation() {
    ArchRule rule = lifeLikeRuleSelectionModelDoesNotDependOnJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(SelectionModelArchViolationFixture.LifeLikeRuleSelectionModel.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("LifeLikeRuleSelectionModel"));
    Assert.assertTrue(error.getMessage().contains("javafx.scene.control.Button"));
  }

  @Test
  public void testWorkspaceConfigDoesNotDependOnJavaFxDetectsViolation() {
    ArchRule rule = workspaceConfigDoesNotDependOnJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(SelectionModelArchViolationFixture.WorkspaceConfig.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("WorkspaceConfig"));
    Assert.assertTrue(error.getMessage().contains("javafx.scene.control.Button"));
  }

  @Test
  public void testLifeLikeConfigurationDoesNotDependOnJavaFxDetectsViolation() {
    ArchRule rule = lifeLikeConfigurationDoesNotDependOnJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(SelectionModelArchViolationFixture.LifeLikeConfiguration.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("LifeLikeConfiguration"));
    Assert.assertTrue(error.getMessage().contains("javafx.scene.control.Button"));
  }

  @Test
  public void testLifeLikeStateMachineDoesNotDependOnJavaFxDetectsViolation() {
    ArchRule rule = lifeLikeStateMachineDoesNotDependOnJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(SelectionModelArchViolationFixture.LifeLikeStateMachine.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("LifeLikeStateMachine"));
    Assert.assertTrue(error.getMessage().contains("javafx.scene.control.Button"));
  }

  @Test
  public void testImageDoesNotDependOnUiOrJavaFxDetectsJavaFxViolation() {
    ArchRule rule = imageDoesNotDependOnUiOrJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(ImageArchViolationFixture.ViolatesImageToJavaFx.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("ViolatesImageToJavaFx"));
    Assert.assertTrue(error.getMessage().contains("javafx.scene.Node"));
  }

  @Test
  public void testImageDoesNotDependOnUiOrJavaFxDetectsUiPresentationViolation() {
    ArchRule rule = imageDoesNotDependOnUiOrJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(ImageArchViolationFixture.ViolatesImageToUiPresentation.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("ViolatesImageToUiPresentation"));
    Assert.assertTrue(
        error.getMessage().contains("com.cellularautomata.ui.panels.ParametersPanel"));
  }

  @Test
  public void testImageDoesNotDependOnUiOrJavaFxDetectsWorkspaceConfigViolation() {
    ArchRule rule = imageDoesNotDependOnUiOrJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(ImageArchViolationFixture.ViolatesImageToWorkspaceConfig.class);
    AssertionError error = assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
    Assert.assertTrue(error.getMessage().contains("ViolatesImageToWorkspaceConfig"));
    Assert.assertTrue(error.getMessage().contains("com.cellularautomata.ui.WorkspaceConfig"));
  }

  private static <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable executable) {
    try {
      executable.run();
    } catch (Throwable actual) {
      if (expectedType.isInstance(actual)) {
        return expectedType.cast(actual);
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
