# Advent of Code

This repository contains my solutions to [Advent of Code](https://adventofcode.com/), as far as I
have them of course. Language of choice is Java, with [Project Lombok](https://projectlombok.org/)
sprinkled on top. Check out the `pom.xml` for more details, such as minimum Java version. Although
this project uses Maven, I run each solution individually in
[IntelliJ IDEA](https://www.jetbrains.com/idea/), so running from CLI will require some work.

Issues with ideas or suggestions are welcome, but no spoilers please.

## 2025

Same as last year, using a lot of Java Streams plus the all-new Gatherer API. Also switched to
Java 25 to explore some of the new language features.

  - **Day 10.** Part 2 took 641 seconds to solve. Could probably do with some optimization...

## 2024

My goal this year is to use Java Streams as much as possible, to get to known them in depth. This
indirectly means doing a light version of functional programming. I also strive to make as many
variables as possible `final`, both for readability and also for use in Lambdas.

  - **Day 18.** Part 2 takes about 2,5 seconds on my laptop and should be optimised. The BFS for
    part 1 already takes half a second, so optimizing that should help. It would also be nice if
    we could run a partial BFS in part 2, starting from the last unblocked cell. Or, maybe not use
    BFS but something more efficient, like Dijkstra's algorithm.
