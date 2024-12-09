package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.val;

import java.util.function.LongBinaryOperator;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.lang.Long.parseLong;

public final class Day7 extends BaseSolution {

    public Day7() {
        read.from(INPUT);
    }

    record CalibrationEquation(
            long expected,
            long[] operands
    ) {
        static CalibrationEquation parse(final String line) {
            val split = line.split(": ");
            val expected = parseLong(split[0]);
            val operands = parseLongs(split[1]);
            return new CalibrationEquation(expected, operands);
        }

        long resultIfCouldPossiblyBeTrue(final Operation[] allowedOperations) {
            return resultIfCouldPossiblyBeTrue(allowedOperations, operands[0], 0);
        }

        private long resultIfCouldPossiblyBeTrue(
                final Operation[] allowedOperations,
                final long intermediateResult,
                final int index
        ) {
            if (index == operands.length - 1) {
                return intermediateResult;
            }
            for (val operation : allowedOperations) {
                val result = resultIfCouldPossiblyBeTrue(
                        allowedOperations,
                        operation.applyAsLong(intermediateResult, operands[index + 1]),
                        index + 1
                );
                if (result == expected) {
                    return result;
                }
            }
            return 0;
        }
    }

    @RequiredArgsConstructor
    enum Operation {
        ADD(Long::sum),
        MULTIPLY((a, b) -> a * b),
        CONCATENATE((a, b) -> parseLong(a + Long.toString(b)));

        @Delegate
        private final LongBinaryOperator operator;
    }

    @Override
    protected void part1() {
        solveAndPrint(new Operation[]{Operation.ADD, Operation.MULTIPLY});
    }

    private void solveAndPrint(final Operation[] allowedOperations) {
        System.out.println(read.lines()
                .map(CalibrationEquation::parse)
                .mapToLong(eq -> eq.resultIfCouldPossiblyBeTrue(allowedOperations))
                .sum());
    }

    @Override
    protected void part2() {
        solveAndPrint(Operation.values());
    }

}
