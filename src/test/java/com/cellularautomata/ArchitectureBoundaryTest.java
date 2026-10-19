package com.cellularautomata;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.cellularautomata.config.ConfigArchViolationFixture;
import com.cellularautomata.engine.EngineArchViolationFixture;
import com.cellularautomata.engine.rules.RulesArchViolationFixture;
import com.cellularautomata.image.ImageArchViolationFixture;
import com.cellularautomata.ui.panels.SelectionModelArchViolationFixture;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ArchUnit dependency boundary tests verifying the architectural contracts documented in {@code
 * docs/ARCHITECTURE.md}.
 *
 * <p>These tests intentionally enforce high-level package ownership boundaries ({@code UI -> Config
 * <- Engine}) while allowing legitimate application composition and coordination in {@code ui} (for
 * example, {@code MainController} coordinating {@code config}, {@code engine}, and {@code image})
 * without constraining internal class structure.
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
                + " independent of JavaFX presentation and UI-specific restrictions");
  }

  public static ArchRule configDoesNotDependOnEngineRule() {
    return noClasses()
        .that()
        .resideInAPackage("..config..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..engine..")
        .because(
            "Config owns workspace metadata and capabilities and must not construct or depend on"
                + " concrete Engine rule implementations");
  }

  public static ArchRule configDoesNotDependOnUiOrJavaFxRule() {
    return noClasses()
        .that()
        .resideInAPackage("..config..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..ui..", "javafx..")
        .because(
            "Config must remain a pure metadata and capability layer usable without UI or JavaFX"
                + " dependencies");
  }

  public static ArchRule engineDoesNotDependOnConfigRule() {
    return noClasses()
        .that()
        .resideInAPackage("..engine..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..config..")
        .because(
            "Engine domain rules and mathematical presets should remain self-contained and"
                + " uncoupled from workspace metadata in Config");
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

  public static ArchRule engineDoesNotDependOnImageRule() {
    return noClasses()
        .that()
        .resideInAPackage("com.cellularautomata.engine..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("com.cellularautomata.image..")
        .because("Engine must remain self-contained and not depend on image utilities");
  }

  public static ArchRule configDoesNotDependOnImageRule() {
    return noClasses()
        .that()
        .resideInAPackage("com.cellularautomata.config..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("com.cellularautomata.image..")
        .because("Config must remain a pure metadata layer and not depend on image utilities");
  }

  public static ArchRule imageDoesNotDependOnUiOrJavaFxRule() {
    return noClasses()
        .that()
        .resideInAPackage("com.cellularautomata.image..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..ui..", "javafx..")
        .because(
            "Image package owns image rendering and file I/O and must remain headless without"
                + " UI or JavaFX dependencies");
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

  // --- Production Verification Tests ---

  @Test
  public void testEngineDoesNotDependOnUiOrJavaFx() {
    engineDoesNotDependOnUiOrJavaFxRule().check(productionClasses);
  }

  @Test
  public void testConfigDoesNotDependOnEngine() {
    configDoesNotDependOnEngineRule().check(productionClasses);
  }

  @Test
  public void testConfigDoesNotDependOnUiOrJavaFx() {
    configDoesNotDependOnUiOrJavaFxRule().check(productionClasses);
  }

  @Test
  public void testEngineDoesNotDependOnConfig() {
    engineDoesNotDependOnConfigRule().check(productionClasses);
  }

  @Test
  public void testEngineRulesDoNotDependOnPresets() {
    engineRulesDoNotDependOnPresetsRule().check(productionClasses);
  }

  @Test
  public void testLifeLikeRuleSelectionModelDoesNotDependOnJavaFx() {
    lifeLikeRuleSelectionModelDoesNotDependOnJavaFxRule().check(productionClasses);
  }

  @Test
  public void testEngineDoesNotDependOnImage() {
    engineDoesNotDependOnImageRule().check(productionClasses);
  }

  @Test
  public void testConfigDoesNotDependOnImage() {
    configDoesNotDependOnImageRule().check(productionClasses);
  }

  @Test
  public void testImageDoesNotDependOnUiOrJavaFx() {
    imageDoesNotDependOnUiOrJavaFxRule().check(productionClasses);
  }

  @Test
  public void testEngineRulesDoNotDependOnAutomataOrGenerator() {
    engineRulesDoNotDependOnAutomataOrGeneratorRule().check(productionClasses);
  }

  // --- Deliberate Violation Detection Tests ---

  @Test
  public void testEngineDoesNotDependOnUiOrJavaFxDetectsViolation() {
    ArchRule rule = engineDoesNotDependOnUiOrJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter().importClasses(EngineArchViolationFixture.ViolatesEngineToUi.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testConfigDoesNotDependOnEngineDetectsViolation() {
    ArchRule rule = configDoesNotDependOnEngineRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(ConfigArchViolationFixture.ViolatesConfigToEngine.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testConfigDoesNotDependOnUiOrJavaFxDetectsViolation() {
    ArchRule rule = configDoesNotDependOnUiOrJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter().importClasses(ConfigArchViolationFixture.ViolatesConfigToUi.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testEngineDoesNotDependOnConfigDetectsViolation() {
    ArchRule rule = engineDoesNotDependOnConfigRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(EngineArchViolationFixture.ViolatesEngineToConfig.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testEngineRulesDoNotDependOnPresetsDetectsViolation() {
    ArchRule rule = engineRulesDoNotDependOnPresetsRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(RulesArchViolationFixture.ViolatesRulesToPresets.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testLifeLikeRuleSelectionModelDoesNotDependOnJavaFxDetectsViolation() {
    ArchRule rule = lifeLikeRuleSelectionModelDoesNotDependOnJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(SelectionModelArchViolationFixture.LifeLikeRuleSelectionModel.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testEngineDoesNotDependOnImageDetectsViolation() {
    ArchRule rule = engineDoesNotDependOnImageRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(EngineArchViolationFixture.ViolatesEngineToImage.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testConfigDoesNotDependOnImageDetectsViolation() {
    ArchRule rule = configDoesNotDependOnImageRule();
    JavaClasses violatingClasses =
        new ClassFileImporter()
            .importClasses(ConfigArchViolationFixture.ViolatesConfigToImage.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testImageDoesNotDependOnUiOrJavaFxDetectsViolation() {
    ArchRule rule = imageDoesNotDependOnUiOrJavaFxRule();
    JavaClasses violatingClasses =
        new ClassFileImporter().importClasses(ImageArchViolationFixture.ViolatesImageToUi.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingClasses));
  }

  @Test
  public void testEngineRulesDoNotDependOnAutomataOrGeneratorDetectsViolation() {
    ArchRule rule = engineRulesDoNotDependOnAutomataOrGeneratorRule();
    JavaClasses violatingAutomata =
        new ClassFileImporter()
            .importClasses(RulesArchViolationFixture.ViolatesRulesToAutomata.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingAutomata));

    JavaClasses violatingGenerator =
        new ClassFileImporter()
            .importClasses(RulesArchViolationFixture.ViolatesRulesToGenerator.class);
    assertThrows(AssertionError.class, () -> rule.check(violatingGenerator));
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
