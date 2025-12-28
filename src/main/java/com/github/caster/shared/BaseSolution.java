package com.github.caster.shared;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import com.github.caster.shared.input.InputLoader;

import lombok.SneakyThrows;
import lombok.val;

import static java.time.Instant.now;

public abstract class BaseSolution {

    protected final InputLoader read = new InputLoader();

    protected abstract void part1();

    protected void part2() {
        IO.println("TO DO");
    }

    static void main() {
        val solutionReference = new AtomicReference<BaseSolution>();
        val setupTime = time(() -> load(solutionReference));
        val solution = solutionReference.get();
        IO.println("Setup for solving [%s] done in about %d ms"
                .formatted(solution.read.inputType(), setupTime));

        runAndTimePart(1, solution::part1);
        runAndTimePart(2, solution::part2);
    }

    private static long time(final Runnable runnable) {
        val start = now();
        runnable.run();
        val stop = now();
        return Duration.between(start, stop).toMillis();
    }

    @SneakyThrows
    private static void load(final AtomicReference<BaseSolution> solutionReference) {
        solutionReference.set(
                (BaseSolution) Class.forName(System.getProperty("sun.java.command"))
                        .getConstructor().newInstance()
        );
    }

    private static void runAndTimePart(final int part, final Runnable runnable) {
        IO.println();
        IO.println("--- PART %d ---".formatted(part));
        final long solveDurationInMs = time(runnable);
        IO.println("--- Done in about %d ms".formatted(solveDurationInMs));
    }

}
