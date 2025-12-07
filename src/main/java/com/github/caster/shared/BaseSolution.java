package com.github.caster.shared;

import java.time.Duration;
import java.time.Instant;

import com.github.caster.shared.input.InputLoader;

import lombok.val;

import static java.time.Instant.now;

public abstract class BaseSolution {

    protected final InputLoader read = new InputLoader();

    protected abstract void part1();

    protected void part2() {
        IO.println("TO DO");
    }

    static void main() throws Exception {
        val solution = (BaseSolution) Class.forName(System.getProperty("sun.java.command"))
                .getConstructor().newInstance();
        System.out.printf("--- PART 1 [%s] ---%n", solution.read.inputType());
        final Instant start1 = now();
        solution.part1();
        final Instant stop1 = now();
        System.out.printf("--- Done in about %d ms%n", Duration.between(start1, stop1).toMillis());

        System.out.printf("\n--- PART 2 [%s] ---%n", solution.read.inputType());
        final Instant start2 = now();
        solution.part2();
        final Instant stop2 = now();
        System.out.printf("--- Done in about %d ms%n", Duration.between(start2, stop2).toMillis());
    }

}
