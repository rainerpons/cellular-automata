# Cellular Automata

> Demonstrating how one of the simplest models of computation gives rise to surprisingly complex behavior.

Get an overview of elementary cellular automata [here](https://en.wikipedia.org/wiki/Elementary_cellular_automaton) or learn about the more mathematical side [here](http://mathworld.wolfram.com/ElementaryCellularAutomaton.html). 

![Elementary cellular automata rules 100 through 149](http://mathworld.wolfram.com/images/eps-gif/ElementaryCA3_900.gif)

## Table of contents

* [Cellular Automata](https://github.com/rpons17/cellular-automata#cellular-automata)
  * [Table of contents](https://github.com/rpons17/cellular-automata#table-of-contents)
  * [Getting started](https://github.com/rpons17/cellular-automata#getting-started)
    * [Prerequisites](https://github.com/rpons17/cellular-automata#prerequisites)
    * [Installing](https://github.com/rpons17/cellular-automata#installing)
  * [Running](https://github.com/rpons17/cellular-automata#running)
    * [Testing](https://github.com/rpons17/cellular-automata#testing)
  * [Documentation](https://github.com/rpons17/cellular-automata#documentation)
  * [Built with](https://github.com/rpons17/cellular-automata#built-with)
  * [License](https://github.com/rpons17/cellular-automata#license)

## Getting started

Clone or download the Cellular Automata repository onto your local machine.

### Prerequisites

This program is easiest to run if you already have Maven installed. If you do not then you can learn how to [here](https://maven.apache.org/install.html). If you have Homebrew installed then installation is as simple as entering the following into Terminal.

```bash
$ brew install maven
```

### Installing

Using a command prompt, `cd` into the local repository. Run the install command.

```bash
$ mvn install
```

If the installation is successful then you should see something similar to the following at the end of the results.

```bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 5.474 s
[INFO] Finished at: 2018-04-03T19:23:16-07:00
[INFO] Final Memory: 11M/39M
[INFO] ------------------------------------------------------------------------
```

The JAR file will be available under the `target` folder of the local repository.

## Running

Assuming you are currently in the root directory of the local repository, the following command will execute the program. 

```bash
$ java -jar target/cellular-automata-2.0.jar
```

From here you can look at cellular automata; adjust its grid size, rule number, and seed type; or save the current display to your local machine as a JPEG image.

### Testing

Within the same directory, you can run the test command.

```bash
$ mvn test
```

## Documentation

If you would like to view documentation for Cellular Automata, the Apache Maven Javadoc plugin in `pom.xml` simplifies the process. From the local repository, run the following command.

```bash
$ mvn javadoc:javadoc
```

The Javadoc files will be available under the `target/site/apidocs` folder of the local repository.

## Built with

* [Maven](https://maven.apache.org/) - dependency management
* [JUnit](https://junit.org/junit4/) - unit testing

## License

This project is licensed under the GNU General Public License. See the [LICENSE.md](https://github.com/rpons17/cellular-automata/blob/master/LICENSE.md) file for details.