package com.cellularautomata;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
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

  /**
   * Prevents domain rules, mathematical validation, and simulation logic from coupling to JavaFX
   * controls or presentation-only restrictions (such as the Life-like UI's two-toggle selection
   * cap), keeping the Engine headless and independently testable.
   */
  @Test
  public void testEngineDoesNotDependOnUiOrJavaFx() {
    noClasses()
        .that()
        .resideInAPackage("..engine..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..ui..", "javafx..")
        .because(
            "Engine owns mathematical rules, validation, and simulation state and must remain"
                + " independent of JavaFX presentation and UI-specific restrictions")
        .check(productionClasses);
  }

  /**
   * Prevents configuration metadata (such as {@code WorkspaceConfig}) from importing or
   * instantiating concrete Engine rule implementations, which would turn shared configuration into
   * a brittle facade that must be modified whenever rule families or domain constraints evolve.
   */
  @Test
  public void testConfigDoesNotDependOnEngine() {
    noClasses()
        .that()
        .resideInAPackage("..config..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..engine..")
        .because(
            "Config owns workspace metadata and capabilities and must not construct or depend on"
                + " concrete Engine rule implementations")
        .check(productionClasses);
  }

  /**
   * Prevents shared configuration and workspace metadata from pulling in JavaFX or UI components,
   * ensuring configuration types remain lightweight, portable across layers, and usable without a
   * running UI toolkit.
   */
  @Test
  public void testConfigDoesNotDependOnUiOrJavaFx() {
    noClasses()
        .that()
        .resideInAPackage("..config..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..ui..", "javafx..")
        .because(
            "Config must remain a pure metadata and capability layer usable without UI or JavaFX"
                + " dependencies")
        .check(productionClasses);
  }

  /**
   * Keeps mathematical rules, presets, and simulation primitives self-contained so domain logic
   * does not depend on workspace routing, window titles, or UI module selection metadata.
   */
  @Test
  public void testEngineDoesNotDependOnConfig() {
    noClasses()
        .that()
        .resideInAPackage("..engine..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..config..")
        .because(
            "Engine domain rules and mathematical presets should remain self-contained and"
                + " uncoupled from workspace metadata in Config")
        .check(productionClasses);
  }

  /**
   * Ensures the Life-like selection model remains a headless, pure Java state container that can be
   * tested and manipulated without requiring the JavaFX toolkit.
   */
  @Test
  public void testLifeLikeRuleSelectionModelDoesNotDependOnJavaFx() {
    noClasses()
        .that()
        .haveSimpleName("LifeLikeRuleSelectionModel")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("javafx..")
        .because(
            "LifeLikeRuleSelectionModel must manage selection state and preset synchronization"
                + " without JavaFX dependencies so it is independently testable")
        .check(productionClasses);
  }

  /**
   * Verifies that ArchUnit rules actively inspect compiled bytecode and fail the build with an
   * {@link AssertionError} when an architectural boundary is violated.
   */
  @Test(expected = AssertionError.class)
  public void testArchitecturalViolationsCauseRuleFailure() {
    // Deliberately assert that UI does not depend on Engine. Because UI legitimately coordinates
    // Engine, this rule violation MUST fail ArchUnit verification with an AssertionError.
    noClasses()
        .that()
        .resideInAPackage("..ui..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..engine..")
        .check(productionClasses);
  }
}
