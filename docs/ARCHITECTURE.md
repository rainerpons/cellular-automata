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
- **Image** (`com.cellularautomata.image`): Owns automaton image rendering, resizing, and PNG file writing. May depend on Engine data structures (`Vector`), but must never depend on UI (`com.cellularautomata.ui..`) or JavaFX (`javafx..`).
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

- Rendering automaton generation maps into `BufferedImage` instances (`AutomatonImageUtils`)
- Resizing rendered images and writing PNG files to disk (`AutomatonImageUtils`)

Keeping `AutomatonImageUtils` focused on pixel rendering, resizing, and PNG I/O ensures image utilities remain headless, self-contained, and independent of UI-level saving coordination and workspace naming policy.

### UI Responsibilities

The UI owns:

- JavaFX controls, layouts, and styling
- Module and workspace metadata (`WorkspaceConfig`)
- User interaction and navigation
- Presentation state (`LifeLikeRuleSelectionModel`)
- Default export filename formatting for cellular automata modules (`AutomatonFileNameFormatter`)
- Export destination file selection and saving coordination (`AutomatonImageService` in `ui.services`)
- Native and modal JavaFX dialog abstraction (`DialogService` in `ui.services`)
- UI-specific restrictions and interaction behavior
- Displaying validation feedback and simulation results

UI-specific restrictions must not be treated as mathematical constraints.

The UI may reference authoritative domain constraints where necessary, but must not independently redefine or duplicate mathematical rules.

### Application Composition and Coordination

Application composition and coordination are responsible for connecting the UI, Image utilities, and Engine.

- Application composition (`MainApp`) constructs and connects components.
- Controllers (`ElementaryController`, `TotalisticController`, `LifeLikeController`) coordinate user actions and application operations.
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
  - Giving each module its own dedicated controller (`ElementaryController`, `TotalisticController`, `LifeLikeController`) wired directly to its concrete parameters panel (`ElementaryParametersPanel`, `TotalisticParametersPanel`, `LifeLikeParametersPanel`) rather than multiplexing across `WorkspaceConfig` in a shared controller or forcing panels behind a common parameter interface.
  - Keeping concrete 1D `Rule` instantiation (`ElementaryRule`, `TotalisticRule`) directly inside `ElementaryController` and `TotalisticController` rather than introducing a separate rule factory class or 1D controller base class.

## Core Components

- **`WorkspaceConfig`**: Owns workspace metadata in `ui`, including routing information and window titles, without depending on Engine classes or constructing Engine rules.
- **`LifeLikePreset`**: Owns mathematical birth and survival rule presets for Life-like cellular automata within `engine.presets`, depending on `LifeLikeRule` without reverse dependencies.
- **`LifeLikeRule`**: Owns constants, domain validation, notation formatting, and state evaluation for Life-like cellular automata rules within `engine.rules`.
- **`SeedType`**: Defines strongly typed initial seed generation strategies (`DENSITY`, `UNIFORM`, `SPARSE`, `ALTERNATING`) in `engine` with module-specific compatibility rules.
- **`Grid2D`**: Represents an immutable two-dimensional finite lattice of binary cell states in `engine`, providing boundary-safe Moore neighborhood counting and next generation evolution.
- **`Generator`**: Single owner of 1D and 2D initial state generation algorithms in `engine`.
- **`LifeLikeSimulation`**: Manages the simulation lifecycle, stepping, and reset state for 2D Life-like cellular automata in `engine.automata`.
- **`MainApp`**: Application composition root and navigation layer in `ui`. Assembles workspaces, initializes UI components, and connects controllers.
- **`ElementaryController`**: Coordinates UI actions for the Elementary workspace in `ui.controllers`, delegating fixed-bound rule validation, `ElementaryRule` construction, 2-state generation, rendering, and image export operations to their appropriate owners.
- **`TotalisticController`**: Coordinates UI actions for the Totalistic workspace in `ui.controllers`, delegating state count validation, dynamic-bound rule validation, `TotalisticRule` construction, multi-state generation, rendering, and image export operations to their appropriate owners.
- **`LifeLikeController`**: Coordinates UI actions for the Life-like workspace in `ui.controllers`, managing parameter capture, simulation lifecycle, fixed-point detection, playback speed synchronization, image rendering, and image export.
- **`DialogService`**: Owns the boundary for native and modal JavaFX dialogs (`FileChooser` and `Alert`) in `ui.services` so controllers and services remain deterministically testable without opening OS dialogs.
- **`AutomatonImageService`**: Coordinates saving rendered automaton images to disk in `ui.services`, resolving initial save directories, prompting the user via `DialogService`, delegating image writing to `AutomatonImageUtils::saveImage`, and displaying error feedback.
- **`LifeLikeStateMachine`**: Manages JavaFX-free simulation lifecycle state transitions (`UNINITIALIZED`, `RUNNING`, `PAUSED`, `FIXED_POINT`) for the Life-like workspace in `ui.simulation`.
- **`LifeLikeConfiguration`**: Captures an immutable, JavaFX-free snapshot of Life-like simulation parameters in `ui.simulation`.
- **`SimulationScheduler` / `TimelineScheduler`**: Defines the simulation tick scheduling contract, playback speed bounds (`0.25x..2.00x`), and JavaFX `Timeline` implementation in `ui.simulation`.
- **`LifeLikeRuleSelectionModel`**: Manages JavaFX-free preset and birth/survival selection state in `ui` for the Life-like parameters panel and enforces the UI-level two-selection cap per set separately from `LifeLikeRule`'s `0..8` neighbor count domain bounds.
- **`RuleValidator`**: Provides rule validation functionality without introducing presentation dependencies.
- **`AutomataEngine`**: Responsible for cellular automata generation and simulation orchestration.
- **`AutomatonImageUtils`**: Handles automaton image rendering from generation maps and grids, image resizing, and PNG file writing independently of UI controls and workspace naming policy.
- **`AutomatonFileNameFormatter`**: Formats default export filenames from strongly typed module rules (`ElementaryRule`, `TotalisticRule`, `LifeLikeRule`) and automaton parameters in `ui`.

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
│   │   ├── image/          # Image rendering and PNG file writing utilities
│   │   └── ui/             # Application shell, navigation screens, workspace metadata, export naming
│   │       ├── components/ # Reusable self-contained UI controls (ModuleCard, LifeLikeToolbar, LabeledSlider)
│   │       ├── controllers/# UI action and simulation coordination (ElementaryController, TotalisticController, LifeLikeController)
│   │       ├── panels/     # Major workspace layout regions (DisplayPanel, SidebarPanel, ParametersPanel, etc.)
│   │       ├── services/   # Application dialogs and image save coordination (DialogService, AutomatonImageService)
│   │       ├── shared/     # Shared layout geometry, CSS class constants, and font registration (UiStyles, UiFonts)
│   │       └── simulation/ # Simulation lifecycle state, configuration, and scheduling (LifeLikeStateMachine, etc.)
│   └── test/               # Tests mirroring production package structure
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
