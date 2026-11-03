# Code Standards

## Enums

- Prefer enums for finite sets of named constants.
- Avoid scattering raw string values throughout the codebase for known states.

## Control Flow

- Always use early returns whenever possible to reduce nesting and improve readability.
- Prefer early returns over wrapping a method body in an `if` block.
- Do not nest `try/catch` blocks inside other `try/catch` blocks.
- Keep `try` blocks small and focused on the operation that can fail.
- Extract cohesive helper methods when a method mixes guard checks, state transitions, and failure recovery.
- Do not catch `Exception` when a narrower exception type applies.
- Do not catch `RuntimeException` around internal helper calls purely as control flow.

## UI Styling

- Prefer CSS classes in `style.css` over inline `-fx-...` strings in Java code.
- Use CSS for colors, borders, backgrounds, typography, padding, alignment, and state-based visuals (`:hover`, `:pressed`, `:focused`, `:selected`, `:disabled`).
- Keep Java layout code limited to structural layout and behavior: container types, child composition, spacing/gaps, layout constraints (`HBox.setHgrow`, `GridPane` row/column indices, `Region.USE_PREF_SIZE`), and dynamic properties computed at runtime.
- Do not call `setStyle(...)` in Java unless a style value is computed dynamically at runtime.
- Do not duplicate visual styling in both Java and `style.css`.
- Keep `UiStyles` limited to shared layout geometry constants and shared CSS class name constants.

## Constants

- Prefer appropriately named constants over unexplained numeric literals when the value has domain meaning or is reused.
- Avoid introducing constants for values whose meaning is already obvious from context.

## Reuse and Maintainability

- Review existing components, utilities, patterns, and suitable libraries before implementing equivalent functionality.
- Prefer cohesive responsibilities and straightforward solutions over duplication or unnecessary abstractions.
- Introduce new abstractions when they solve a concrete problem, not solely for hypothetical future reuse.
- Follow the dependency and ownership boundaries defined in `ARCHITECTURE.md` rather than duplicating them here.

## Testing

- Prioritize tests that protect meaningful behavior, contracts, invariants, and regressions.
- Avoid tests coupled to incidental implementation details, internal structure, or a particular algorithm when externally observable behavior is what matters.
- Do not add tests solely to increase coverage metrics; focus on the confidence they provide.
- Keep tests maintainable and update them when intended behavior changes.
- Ensure tests actually exercise the behavior they claim to verify. Manually invoking a scheduled callback, for example, does not prove that the scheduler executes it.
- Prefer deterministic tests with controlled inputs, dependencies, and execution. Do not use arbitrary sleeps, polling delays, or timing-dependent assertions to establish correctness.
- For asynchronous integration tests, bounded timeouts are acceptable to prevent indefinite hangs, but should not substitute for meaningful synchronization.
- Avoid redundant assertions, hardcoded duplicates of production constants, and tests that merely reproduce implementation logic.
- Verify both successful behavior and meaningful failure paths, including relevant lifecycle transitions and edge cases.
- Run relevant tests and verification checks before reporting completion. Clearly distinguish verified results from assumptions or unverified claims.

## Static Analysis

- The project uses Checkstyle and SpotBugs.
- Run the relevant local verification checks before considering work complete; local checks should align with CI requirements.
- **Future Consideration:** We are considering evaluating and adopting Error Prone in a future issue, after the initial Java 25 modernization work is fully complete.

## Documentation

- Remove JavaDocs from trivial getters that simply restate the method name.
- Prefer documenting fields, classes, non-obvious methods, invariants, algorithms, and implementation details that are not immediately obvious from the code.

## String Formatting

- Prefer `String.formatted(...)` over `String.format(...)` or string concatenation of runtime values for parameterized messages, filenames, and debug representations.
- Keep simple static string literals concatenated only when breaking across lines for readability.
