package com.github.caster.shared;

import com.github.caster.shared.input.InputLoader;

import java.time.Duration;
import java.time.Instant;

import static java.time.Instant.now;

public abstract class BaseSolution {

    protected final InputLoader read = new InputLoader();

    protected abstract void part1();

    protected void part2() {
        System.out.println("TO DO");
    }

    public static void main(final String[] args) throws Exception {
        final BaseSolution solution = (BaseSolution) Class.forName(System.getProperty("sun.java.command"))
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
