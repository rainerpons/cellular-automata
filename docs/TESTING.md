# Manual Testing

## Overview

This document defines the manual regression testing checklist for the Cellular Automata application.

The goal is to verify that user-facing functionality works correctly, identify regressions, and maintain confidence in the application as new features are introduced.

Testing should prioritize:

- Common user workflows and realistic interactions.
- Meaningful boundary conditions and validation failures.
- Transitions between application and simulation states.
- Integration between components and modules.
- Previously discovered regressions.

Exhaustively testing every combination of parameters is neither necessary nor practical. Use representative cases that provide meaningful coverage, and expand the checklist when new functionality or defects justify it.

This document complements the automated testing standards in `docs/CODE_STANDARDS.md`. Deterministic mathematical correctness, internal invariants, and lifecycle behavior should primarily be verified through automated tests.

## 1. Application and Navigation

- [ ] Application launches successfully without errors.
- [ ] Module selection screen displays all available modules correctly.
- [ ] Module titles, descriptions, and selection indicators are correct.
- [ ] Continue is disabled until a module is selected.
- [ ] Selecting a different module updates the selection correctly.
- [ ] Continue opens the selected workspace.
- [ ] Each workspace displays the correct title, controls, and layout.
- [ ] Back navigation returns to module selection.
- [ ] Navigating repeatedly between modules works correctly.
- [ ] Reopening a workspace initializes its expected default state.
- [ ] Navigating away from an active simulation stops its playback.
- [ ] Closing the application terminates cleanly.

## 2. Elementary Cellular Automata

### Configuration

- [ ] Default parameter values are correct.
- [ ] Size slider updates its displayed value.
- [ ] Minimum and maximum supported sizes are selectable.
- [ ] Rule number accepts valid values, including 0 and 255.
- [ ] Rule number rejects values outside the supported range.
- [ ] Empty or malformed rule input is handled appropriately.
- [ ] Rule spinner controls respect their boundaries.
- [ ] All supported seed types are selectable.

### Generation and Rendering

- [ ] Generate produces an automaton using the selected parameters.
- [ ] Representative rules, including 0, 30, 90, 110, and 255, produce expected patterns.
- [ ] Uniform, Sparse, and Alternating seeds behave correctly.
- [ ] Generation works at minimum and maximum sizes.
- [ ] Changing the rule produces the expected new output.
- [ ] Changing the size produces output with the correct dimensions.
- [ ] Changing the seed affects initial conditions appropriately.
- [ ] Repeated generation works without stale results or unexpected errors.
- [ ] Invalid input displays appropriate feedback and allows recovery.
- [ ] Generated automata are displayed correctly.

## 3. Totalistic Cellular Automata

### Configuration

- [ ] Default parameter values are correct.
- [ ] Size slider updates correctly across its supported range.
- [ ] State counts from 2 through 5 are accepted.
- [ ] State counts outside the supported range are rejected.
- [ ] Empty or malformed state input is handled appropriately.
- [ ] Rule number limits update correctly when the state count changes.
- [ ] Minimum and maximum valid rule numbers are accepted for representative state counts.
- [ ] Rule numbers outside the current valid range are rejected.
- [ ] Changing the state count after entering a rule is handled correctly.
- [ ] Spinner controls respect their current boundaries.
- [ ] All supported seed types are selectable.

### Generation and Rendering

- [ ] Generate produces an automaton using the selected parameters.
- [ ] Generation works with 2, 3, 4, and 5 states.
- [ ] Representative rules produce expected results.
- [ ] Uniform, Sparse, and Alternating seeds behave correctly.
- [ ] Different cell states are visually distinguishable.
- [ ] Generation works at minimum and maximum sizes.
- [ ] Changing the rule, size, state count, or seed updates the generated output appropriately.
- [ ] Repeated generation works without stale results or unexpected errors.
- [ ] Invalid input displays appropriate feedback and allows recovery.
- [ ] Generated automata are displayed correctly.

## 4. Life-Like Cellular Automata

### Configuration and Presets

- [ ] Default parameters match Conway's Game of Life.
- [ ] Grid size slider displays the selected dimensions correctly.
- [ ] Minimum and maximum supported grid sizes work.
- [ ] Conway's Game of Life selects B3/S23.
- [ ] HighLife selects B36/S23.
- [ ] Seeds selects B2/S.
- [ ] Selecting a preset updates Birth and Survival controls correctly.
- [ ] Modifying preset values switches the displayed selection to Custom when appropriate.
- [ ] Restoring a known preset's exact configuration recognizes that preset.
- [ ] Switching repeatedly between presets and custom rules works correctly.

### Birth and Survival Rules

- [ ] Birth and Survival counts can be selected independently.
- [ ] Neighbor counts from 0 through 8 are selectable.
- [ ] Empty Birth and Survival sets are supported.
- [ ] One or two selections per set work correctly.
- [ ] Additional selections are prevented when a set reaches its two-selection limit.
- [ ] Removing a selected count makes additional selections available.
- [ ] Reaching the limit in one set does not restrict the other.
- [ ] Selected toggles remain visually consistent with the active rule.
- [ ] Rapid or repeated interactions do not produce inconsistent selections.

### Initial Conditions

- [ ] Density, Sparse, and Alternating seed types are selectable.
- [ ] Density is selected by default.
- [ ] Density slider appears only when Density is selected.
- [ ] Switching seed types preserves appropriate layout and control behavior.
- [ ] Density values of 0%, 50%, and 100% produce expected initial conditions.
- [ ] Sparse produces a single living cell.
- [ ] Alternating produces the expected initial pattern.
- [ ] Initial conditions work with representative small and large grid sizes.
- [ ] Changing seed configuration before initialization affects the next simulation.

### Simulation Correctness

- [ ] Conway's Game of Life produces expected behavior for representative known patterns.
- [ ] Stable patterns remain unchanged.
- [ ] Oscillating patterns evolve correctly.
- [ ] HighLife differs from Conway's Game of Life where the additional birth rule applies.
- [ ] Seeds produces births and deaths according to B2/S.
- [ ] Custom Birth and Survival selections affect evolution correctly.
- [ ] Empty Birth and Survival sets behave according to the selected rule.
- [ ] Neighbor counts of 0 and 8 are handled correctly.
- [ ] Corner, edge, and interior cells use the correct neighborhoods.
- [ ] Cells do not wrap around finite grid boundaries.
- [ ] Generated cells remain within the supported binary states.

Use deterministic automated tests as the primary evidence for mathematical correctness. Manual verification should confirm that the UI correctly integrates and displays the underlying simulation.

### Playback and Simulation Controls

- [ ] Initial Play, Step, Reset, and Save availability is correct.
- [ ] Play initializes the simulation and starts automatic generation.
- [ ] Pause stops automatic generation.
- [ ] Resume continues from the paused simulation state.
- [ ] Step advances exactly one generation.
- [ ] Step is unavailable during automatic playback.
- [ ] Reset restores the original simulation grid.
- [ ] Reset works while running and paused.
- [ ] Playback can restart after Reset.
- [ ] Fixed points stop automatic playback.
- [ ] Play and Step become unavailable at a fixed point.
- [ ] Reset remains available at a fixed point.
- [ ] Oscillating patterns are not incorrectly classified as fixed points.
- [ ] Control icons, tooltips, and enabled states reflect the simulation state.
- [ ] Repeated Play, Pause, Step, and Reset interactions behave consistently.

### Configuration Changes During Simulation

- [ ] Parameter changes before initialization are used when starting the simulation.
- [ ] Parameter changes while paused are applied when starting a new simulation as appropriate.
- [ ] Parameter changes during playback do not unexpectedly alter the active simulation.
- [ ] Reset behaves consistently after parameters have been edited.
- [ ] Changing parameters after reaching a fixed point respects the expected Reset workflow.
- [ ] Repeated configuration changes do not produce stale frames or inconsistent control states.

### Playback Speed

- [ ] Default playback speed is 1.00x.
- [ ] Minimum and maximum speeds are 0.25x and 2.00x.
- [ ] Playback speed changes in 0.25x increments.
- [ ] Spinner arrows respect minimum and maximum values.
- [ ] Playback speed is displayed consistently with two decimal places.
- [ ] Increasing or decreasing speed changes the rate of playback.
- [ ] Speed changes take effect during active playback.
- [ ] Speed changes while paused apply after resuming.
- [ ] Changing playback speed does not reset the simulation.
- [ ] Rapid speed changes do not cause duplicate playback or inconsistent behavior.

## 5. Image Saving

### All Modules

- [ ] Save is unavailable before an image has been generated or initialized.
- [ ] Save becomes available when an image can be exported.
- [ ] Save dialog opens correctly.
- [ ] Suggested filename reflects the active automaton configuration.
- [ ] Saving produces a valid PNG file.
- [ ] Saved image matches the intended generated or displayed result.
- [ ] Saved image dimensions and visual appearance are correct.
- [ ] Canceling the save dialog does not create a file.
- [ ] Saving to an existing filename follows the expected overwrite behavior.
- [ ] Invalid or unwritable destinations produce appropriate feedback.
- [ ] Saving different results does not reuse stale image data.
- [ ] Repeated saving works correctly.

### Life-Like Playback Integration

- [ ] Saving while paused captures the current generation.
- [ ] Saving while running temporarily stops generation advancement.
- [ ] Playback resumes after a successful save when previously running.
- [ ] Playback resumes after canceling a save when previously running.
- [ ] Playback resumes appropriately after a failed save.
- [ ] Saving at a fixed point does not restart playback.
- [ ] Saving after Reset captures the reset grid.
- [ ] Saving after editing parameters captures the active simulation rather than unapplied changes.

## 6. User Interface and Usability

- [ ] Module workspaces have consistent visual styling.
- [ ] Labels, controls, and buttons are correctly aligned.
- [ ] Text is readable without unintended clipping or overflow.
- [ ] Tooltips display correctly.
- [ ] Disabled controls are visually distinguishable.
- [ ] Generated images are centered and correctly scaled.
- [ ] Automaton patterns are rendered without visible distortion.
- [ ] Error messages are understandable and identify the relevant problem.
- [ ] Invalid input can be corrected without restarting the application.
- [ ] Keyboard navigation works for applicable controls.
- [ ] Application remains visually usable on representative supported display resolutions and scaling settings.
- [ ] Navigation and repeated interactions do not introduce layout problems.

## 7. Reliability and Regression

- [ ] Maximum-size generation completes successfully for each module.
- [ ] Life-Like playback remains responsive at large grid sizes.
- [ ] Repeated generation does not cause errors or unexpected degradation.
- [ ] Repeated navigation between modules remains stable.
- [ ] Rapid control interactions do not freeze the UI.
- [ ] Long-running playback remains stable.
- [ ] Navigating away from playback releases the active simulation appropriately.
- [ ] Previously generated output does not leak into another workspace.
- [ ] Repeated generation, navigation, and saving do not show sustained unexpected resource growth.
- [ ] Error recovery leaves the application in a usable state.
- [ ] Previously fixed regressions remain resolved.

## 8. Release Readiness

These checks supplement the manual scenarios above and should be performed as part of a milestone's final verification.

- [ ] Required unit and integration tests pass.
- [ ] JavaFX tests pass.
- [ ] ArchUnit architectural boundary tests pass.
- [ ] Formatting checks pass.
- [ ] Checkstyle passes.
- [ ] SpotBugs passes.
- [ ] JavaDoc checks pass.
- [ ] Maven build succeeds.
- [ ] CI checks pass.
- [ ] Release packaging succeeds for supported platforms.
- [ ] Packaged applications launch successfully.
- [ ] Application resources, fonts, icons, and stylesheets are included correctly.
- [ ] Release-blocking defects are resolved.
- [ ] Remaining known issues are documented.
- [ ] Final regression testing passes against the intended release build.

## Maintaining This Checklist

Update this document when:

- A new user-facing feature introduces meaningful workflows.
- An existing workflow changes.
- A discovered regression reveals a missing test scenario.
- A significant edge case warrants recurring verification.
- A scenario becomes obsolete because functionality is removed or replaced.

Prefer adding targeted scenarios over expanding the checklist with every possible parameter combination.

Remove redundant or obsolete scenarios as the application evolves. Keep the checklist focused on meaningful confidence in the user experience rather than checklist size.
