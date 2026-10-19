# Ubiquitous Language

This document defines the terminology used throughout the cellular-automata project.

The goal is to ensure consistent language across:

- Documentation
- GitHub issues
- Pull requests
- Code
- User interface text

## Cellular Automaton (CA)

A mathematical model consisting of cells arranged on a lattice that evolve over time according to a rule.

Examples:

- Elementary cellular automata
- Totalistic cellular automata
- Conway's Game of Life

## Module

A top-level category of cellular automata exposed to users.

A module defines:

- Supported rule families
- Available workspaces
- User interface controls
- Data models
- Presets

Examples:

- Elementary Module
- Totalistic Module
- Life-Like Module

Users select a module when launching the application.

## Workspace

The complete user interface associated with a module.

A workspace contains:

- Controls
- Visualization panels
- Rule configuration
- Statistics
- Presets
- Module-specific tools

Examples:

- Elementary Workspace
- Totalistic Workspace
- Life-Like Workspace

A module owns exactly one workspace.

## Rule Family

A class of cellular automata that share a common rule representation.

Examples:

- Elementary rules
- Totalistic rules
- Outer-totalistic rules
- Generations rules

A module may support one or more rule families.

## Rule

The mathematical definition that determines how cells evolve between generations.

A rule is not necessarily identified by a number. Its representation depends on the rule family.

Examples:

- Rule 30
- Rule 90
- Rule 110
- Life-Like rule B3/S23 (Conway's Game of Life)

Rules belong to a rule family.

## Simulation Engine

The shared backend system responsible for evolving automata.

Responsibilities include:

- Computing next generations
- Maintaining simulation state
- Stepping simulations
- Running simulations continuously
- Resetting simulations

The simulation engine is independent of any specific module or workspace.

## Automaton

A specific simulation instance.

An automaton contains:

- Cell state data
- Rule configuration
- Current generation number
- Metadata

Examples:

- A running Rule 30 simulation
- A paused Totalistic simulation

## Lattice

The arrangement of cells in space.

Examples:

- One-dimensional lattice
- Two-dimensional lattice
- Three-dimensional lattice

The lattice determines neighborhood relationships.

## Cell

The smallest unit of state in an automaton.

Each cell stores a state value.

Examples:

- Dead or alive
- Integer state values
- Continuous values

## Cell State

The value stored by a cell.

Examples:

- 0 or 1
- Integer values
- Floating-point values

Supported state values depend on the module.

## Neighborhood

The set of cells considered when determining the next state of a cell.

Examples:

- Radius 1 neighborhood
- Moore neighborhood
- von Neumann neighborhood

## Neighborhood Radius

The distance from a cell used to determine its neighborhood.

Examples:

- Radius 1
- Radius 2
- Radius 3

Elementary cellular automata use radius 1 neighborhoods.

## Generation

A single timestep in a simulation.

Generation 0 refers to the initial state.

Generation N refers to the result after N simulation steps.

## Step

The operation of advancing a simulation by one generation.

## Preset

A predefined configuration provided by the application.

A preset may include:

- Rule
- Initial state
- Colors
- Simulation settings

Examples:

- Rule 30
- Rule 90
- Rule 110

## Initial State

The starting arrangement of cells before simulation begins.

Examples:

- Single center cell
- Random initialization
- Custom pattern

## Dimension

The number of spatial dimensions in a lattice.

Examples:

- 1D
- 2D
- 3D

## Visualization

The graphical representation of an automaton.

Examples:

- Raster view
- Heatmap
- Three-dimensional rendering

## Module Selection Screen

The application entry point that allows users to choose a module.

The application launches into this screen.

## Application Shell

The top-level application responsible for:

- Managing module selection
- Creating workspaces
- Navigation between screens

The application shell exists above all modules and workspaces.
