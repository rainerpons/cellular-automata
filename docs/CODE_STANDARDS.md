# Code Standards

## Enums

- Prefer enums for finite sets of named constants.
- Avoid scattering raw string values throughout the codebase for known states.

## Control Flow

- Always use early returns whenever possible to reduce nesting and improve readability.

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
