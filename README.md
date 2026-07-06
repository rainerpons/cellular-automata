<div align="center">
  <img src="site/assets/cellular-automata-icon.svg" alt="Cellular Automata Logo" width="120" />

  <h1>Cellular Automata</h1>

  <p>
    <strong>A high-performance Java application for exploring elementary cellular automata.</strong>
  </p>

  [![CI](https://github.com/rainerpons/cellular-automata/actions/workflows/ci.yml/badge.svg)](https://github.com/rainerpons/cellular-automata/actions/workflows/ci.yml)
</div>

<br />

<!-- Hero Image Placeholder -->
<div align="center">
  <img src="https://i.redd.it/ypf6h1jjnhb31.gif" alt="Cellular Automata Example" />
  <!-- <em>[ Hero image / GIF of the application running will go here ]</em> -->
  <p><i>A demonstration of how simple computational models produce incredibly complex behavior.</i></p>
</div>

## Overview

Cellular Automata is an interactive Java desktop application that simulates and visualizes elementary cellular automata. It allows users to experiment with different configurations and observe how simple mathematical rules can produce surprisingly complex and often beautiful patterns.

Designed as a modern, polished software engineering project, it features a clean graphical user interface, robust build tooling, and adherence to modern Java coding standards.

**[🌟 Visit the Landing Page for gallery images, rule examples, and downloads](https://www.rainerpons.com/cellular-automata)**

## Features & Highlights

- **Interactive Visualization**: Real-time rendering of elementary cellular automata rules (0-255).
- **Dynamic Controls**: Adjust grid sizes, generation speeds, and initial seeds on the fly.
- **Image Export**: Save the current state of the simulation directly to your machine as a JPEG image.
- **Optimized Rendering**: Efficient drawing routines ensure smooth performance even for large grid sizes.

## Project History

This project was originally developed in 2018 as a way to explore Java and the fascinating behavior of elementary cellular automata. Rather than replacing it with a brand new implementation years later, it was intentionally revisited as a legacy modernization project.

The goal was to preserve the original functionality while improving the overall quality of the software through better architecture, automated testing, continuous integration, static analysis, code formatting, and a more polished user experience. The result is a project that showcases not only the application itself, but also the engineering practices used to evolve and maintain an existing codebase over time.

## Tech Stack

- **Language**: Java 8+
- **UI Framework**: Java Swing
- **Build Tool**: Maven
- **Testing**: JUnit 4
- **Static Analysis**: Checkstyle, SpotBugs
- **Formatting**: Google Java Format
- **CI/CD**: GitHub Actions

## Running Locally

To run the application on your local machine, ensure you have Java and Maven installed.

```bash
# Clone the repository
git clone https://github.com/rainerpons/cellular-automata.git
cd cellular-automata

# Build the project
mvn install

# Run the application
java -jar target/cellular-automata-2.0.jar
```

## Development Commands

- **Run tests**: `mvn test`
- **Format code**: `mvn fmt:format`
- **Install git pre-commit hooks** (formats code automatically): `./scripts/install-hooks.sh`
- **Generate Javadoc**: `mvn javadoc:javadoc` (available in `target/site/apidocs`)

## License

This project is licensed under the GNU General Public License. See the [LICENSE.md](LICENSE.md) file for details.