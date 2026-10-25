# Architecture Overview

This document defines the architectural decisions, dependency boundaries, and ownership responsibilities of the Cellular Automata application.

The goal is to maintain a modular, extensible, and maintainable architecture as new cellular automata modules and capabilities are introduced.

## Stack

- **Maven**: Dependency management and build automation
- **JUnit**: Unit testing
- **ArchUnit**: Automated architectural boundary verification
- **Google Java Format**: Code formatting
- **Checkstyle**: Static style analysis
- **SpotBugs**: Static bug analysis

## Dependency Direction

The application separates domain logic, image utilities, and presentation into distinct packages with explicit dependency boundaries:

```text
UI ──→ Image ──→ Engine
 │                 ↑
 └─────────────────┘
```

The arrows indicate permitted dependencies.

- **Engine** (`com.cellularautomata.engine`): Owns cellular automata rules, mathematical constraints, validation, simulation state, and generation logic. Independent of presentation and workspace metadata.
- **Image** (`com.cellularautomata.image`): Owns automaton image rendering, resizing, PNG file writing, and initial save directory resolution. May depend on Engine data structures (`Vector`), but must never depend on UI (`com.cellularautomata.ui..`) or JavaFX (`javafx..`).
- **UI** (`com.cellularautomata.ui`): Owns JavaFX presentation, workspace configuration metadata (`WorkspaceConfig`), user interactions, rendering, controllers, and default export filename formatting (`AutomatonFileNameFormatter`). May depend on Engine and Image to coordinate application behavior.

### Dependency Rules

- Engine is self-contained and must never depend on UI or Image.
- Image may depend on Engine, but must never depend on UI (`com.cellularautomata.ui..`) or JavaFX (`javafx..`).
- UI and application entry points (`com.cellularautomata.app`) may depend on Engine and Image.
- Circular dependencies are prohibited.
- Dependencies must follow ownership boundaries, not merely package accessibility.
- Introducing a dependency must not duplicate responsibilities already owned by another layer.

### Why These Boundaries Exist

- **Decoupling workspace metadata from rule families**: Keeping `WorkspaceConfig` free of `Engine` imports prevents workspace metadata from becoming a God enum that must know how to construct every rule family or throw `UnsupportedOperationException` for non-numeric rule families (such as `LIFE_LIKE`).
- **Separating mathematical rules from UI restrictions**: Domain classes (`LifeLikeRule`, `LifeLikePreset`, `TotalisticRule`, `ElementaryRule`) enforce mathematical invariants (such as Moore neighborhood counts `0..8`), while UI presentation models (`LifeLikeRuleSelectionModel`) own product-specific interaction caps (such as limiting toggle selection to at most two counts per set).
- **Headless testability**: Keeping `Engine`, `Image`, and JavaFX-free presentation models (`LifeLikeRuleSelectionModel`, `WorkspaceConfig`, `AutomatonFileNameFormatter`) free of JavaFX types allows core domain logic, export naming, and selection state transitions to be tested deterministically without spinning up the JavaFX toolkit.

### Automated Boundary Enforcement (Why ArchUnit Is Used)

Architectural dependency rules are enforced automatically during `mvn test` / `mvn verify` using **ArchUnit** rather than ad-hoc source-file import scanning:

- **Bytecode-level accuracy**: ArchUnit inspects compiled `.class` bytecode rather than raw `.java` text, catching fully qualified class references, static member accesses, method return/parameter types, and synthetic references that regex or `import`-line checks miss.
- **Executable architectural contract**: Encoding layer boundaries in `ArchitectureBoundaryTest` ensures that accidental reverse dependencies (for example, `engine` importing `javafx`, or `LifeLikeRuleSelectionModel` depending on JavaFX controls) fail the build immediately with descriptive violation messages.

## Responsibility and Ownership

Every responsibility should have a clear owner. New functionality should extend existing architectural patterns rather than create competing implementations or sources of truth.

### Engine Responsibilities

The Engine owns:

- Cellular automata rules and rule families
- Mathematical constraints and domain validation
- Rule construction and interpretation
- Mathematical rule presets (`LifeLikePreset` in `engine.presets`)
- Initial lattice seed types (`SeedType`) and generation algorithms (`Generator`)
- Simulation state and generation
- Neighborhood and lattice behavior

Engine logic must remain independent of JavaFX, UI controls, and presentation-specific restrictions.

### Image Responsibilities

The Image package owns:

- Rendering automaton generation maps into `BufferedImage` instances (`AutomatonImage`)
- Resizing rendered images and writing PNG files to disk (`AutomatonImage`)
- Initial save directory resolution (`AutomatonImageExport`)

Separating `AutomatonImage` (pixel rendering, resizing, and PNG I/O) from `AutomatonImageExport` (user directory resolution) keeps rendering primitives and filesystem destination resolution independent of UI-level workspace naming policy.

### UI Responsibilities

The UI owns:

- JavaFX controls, layouts, and styling
- Module and workspace metadata (`WorkspaceConfig`)
- User interaction and navigation
- Presentation state (`LifeLikeRuleSelectionModel`)
- Default export filename formatting for workspace configurations (`AutomatonFileNameFormatter`)
- UI-specific restrictions and interaction behavior
- Displaying validation feedback and simulation results

UI-specific restrictions must not be treated as mathematical constraints.

The UI may reference authoritative domain constraints where necessary, but must not independently redefine or duplicate mathematical rules.

### Application Composition and Coordination

Application composition and coordination are responsible for connecting the UI, Image utilities, and Engine.

- Application composition (`MainApp`) constructs and connects components.
- Controllers (`MainController`, `LifeLikeController`) coordinate user actions and application operations.
- Controllers should delegate domain logic, generation, and specialized operations to their appropriate owners.
- Controllers should not accumulate unrelated responsibilities.
- New services, interfaces, or abstractions should be introduced only when they provide meaningful separation or reuse.

### Architectural Contracts vs. Implementation Preferences

To keep reviews and maintenance focused, the project distinguishes between **architectural contracts** and **implementation preferences**:

- **Architectural contracts (invariants)**:
  - Permitted package dependency directions (enforced by ArchUnit).
  - Single source of truth for mathematical domain constraints in `engine.rules`.
  - Separation of UI-specific interaction caps (`LifeLikeRuleSelectionModel.MAX_SELECTED_COUNTS_PER_SET`) from mathematical domain bounds (`LifeLikeRule.MIN_NEIGHBOR_COUNT..MAX_NEIGHBOR_COUNT`).
  - JavaFX-free `engine`, `image`, and presentation state models (`LifeLikeRuleSelectionModel`, `WorkspaceConfig`, `AutomatonFileNameFormatter`).
- **Implementation preferences (local design choices)**:
  - Keeping numeric-rule lookup and concrete `Rule` instantiation (`ElementaryRule`, `TotalisticRule`) inside `MainController` coordination methods rather than introducing a separate rule factory class while only `MainController` consumes numeric rules.
  - Representing the shared 1D parameter contract with the narrow `NumericRuleParametersPanel` interface rather than a deep panel inheritance hierarchy.

## Core Components

- **`WorkspaceConfig`**: Owns workspace metadata and capabilities in `ui`, including routing information, window titles, and supported configuration options, without depending on Engine classes or constructing Engine rules.
- **`LifeLikePreset`**: Owns mathematical birth and survival rule presets for Life-like cellular automata within `engine.presets`, depending on `LifeLikeRule` without reverse dependencies.
- **`LifeLikeRule`**: Owns constants, domain validation, notation formatting, and state evaluation for Life-like cellular automata rules within `engine.rules`.
- **`SeedType`**: Defines strongly typed initial seed generation strategies (`DENSITY`, `UNIFORM`, `SPARSE`, `ALTERNATING`) in `engine` with module-specific compatibility rules.
- **`Grid2D`**: Represents an immutable two-dimensional finite lattice of binary cell states in `engine`, providing boundary-safe Moore neighborhood counting and next generation evolution.
- **`Generator`**: Single owner of 1D and 2D initial state generation algorithms in `engine`.
- **`LifeLikeSimulation`**: Manages the simulation lifecycle, stepping, and reset state for 2D Life-like cellular automata in `engine.automata`.
- **`MainApp`**: Application composition root and navigation layer. Assembles workspaces, initializes UI components, and connects controllers.
- **`MainController`**: Coordinates UI actions for numeric-rule workspaces and delegates validation, rule construction, generation, and image export operations to their appropriate owners.
- **`LifeLikeController`**: Coordinates UI actions for the Life-like workspace, managing parameter capture, simulation lifecycle, playback speed synchronization, image rendering, and image export.
- **`LifeLikeStateMachine`**: Manages JavaFX-free simulation lifecycle state transitions (`UNINITIALIZED`, `RUNNING`, `PAUSED`) for the Life-like workspace in `ui`.
- **`LifeLikeConfiguration`**: Captures an immutable, JavaFX-free snapshot of Life-like simulation parameters in `ui`.
- **`LifeLikeRuleSelectionModel`**: Manages JavaFX-free preset and birth/survival selection state for the Life-like parameters panel and enforces the UI-level two-selection cap per set separately from `LifeLikeRule`'s `0..8` neighbor count domain bounds.
- **`RuleValidator`**: Provides rule validation functionality without introducing presentation dependencies.
- **`AutomataEngine`**: Responsible for cellular automata generation and simulation orchestration.
- **`AutomatonImage`**: Handles automaton image rendering, resizing, and PNG file writing independently of UI controls and workspace naming policy.
- **`AutomatonImageExport`**: Resolves initial save directories for saving automaton images.
- **`AutomatonFileNameFormatter`**: Formats default export filenames based on workspace configuration, rule number or Life-like B/S notation, size, and states in `ui`.

## Architectural Principles

### Single Source of Truth

- Domain constraints should be defined by their authoritative domain owner.
- Avoid duplicating constraints, validation logic, or configuration values across layers.
- Prefer named constants for meaningful domain values rather than unexplained numeric literals.

### Separation of Concerns

- Separate domain behavior from presentation behavior.
- Separate mathematical constraints from product or UI restrictions.
- Keep components focused on cohesive responsibilities.
- Avoid combining unrelated functionality simply because it is used by the same workflow.

### Reuse Before Reinvention

- Examine existing components, utilities, abstractions, and dependencies before implementing new functionality.
- Extend established patterns when they remain appropriate.
- Prefer proven solutions over unnecessary custom implementations.
- Avoid introducing abstractions or dependencies whose complexity outweighs their benefits.

### Architectural Evolution

- Existing patterns should be respected but not followed blindly.
- When an existing pattern conflicts with architectural principles, identify the inconsistency rather than reproducing it.
- Architectural changes should be deliberate and discussed before implementation.
- Prefer incremental improvements over speculative frameworks or premature generalization.
- Do not introduce generalized solutions for hypothetical future requirements without a demonstrated need.

## Folder Structure

```text
cellular-automata/
├── docs/                   # Project documentation
├── src/
│   ├── main/java/com/cellularautomata/
│   │   ├── app/            # Application entry point
│   │   ├── engine/         # Domain logic and simulation
│   │   ├── image/          # Image rendering and file export utilities
│   │   └── ui/             # JavaFX presentation and coordination
│   └── test/               # Tests
```

Supporting packages, such as image processing, should maintain cohesive responsibilities and follow the same dependency principles.

## Architectural Anti-Patterns

Avoid:

- Introducing dependencies that violate the permitted dependency direction
- Repeating domain logic or mathematical constraints across layers
- Placing mathematical rule behavior inside UI components
- Treating presentation restrictions as domain constraints
- Allowing controllers or UI panels to accumulate unrelated responsibilities
- Creating competing abstractions for functionality already supported by the application
- Introducing unnecessary interfaces, factories, services, or frameworks
- Coupling otherwise independent components through shared implementation details

The goal is not to maximize abstraction or minimize class size. It is to maintain clear ownership, appropriate dependencies, and code that remains understandable and extensible as the application grows.
