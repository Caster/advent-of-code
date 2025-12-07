package com.github.caster.solutions.y2025;

import com.github.caster.shared.BaseSolution;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Math.pow;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.rangeClosed;
import static java.util.stream.LongStream.range;

public final class Day3 extends BaseSolution {

    private static final long[] POWERS_OF_TEN = range(0, 16).map(e -> (long) pow(10, e)).toArray();

    public Day3() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        IO.println(read.lines().mapToLong(bank -> maximumJoltage(bank, 2)).sum());
    }

    private long maximumJoltage(final String bank, final int batteriesToTurnOn) {
        val batteries = stream(bank.split("")).mapToInt(Integer::parseInt).toArray();
        return maximumJoltage(batteries, batteriesToTurnOn, 0);
    }

    private long maximumJoltage(
            final int[] batteries,
            final int batteriesToTurnOn,
            final int startIndex
    ) {
        val maxIndex = rangeClosed(startIndex, batteries.length - batteriesToTurnOn).boxed()
                .max(comparing(i  -> batteries[i])).orElseThrow();
        val joltage = POWERS_OF_TEN[batteriesToTurnOn - 1] * batteries[maxIndex];
        if (batteriesToTurnOn == 1) {
            return joltage;
        }
        return joltage + maximumJoltage(batteries, batteriesToTurnOn - 1, maxIndex + 1);
    }

    @Override
    protected void part2() {
        IO.println(read.lines().mapToLong(bank -> maximumJoltage(bank, 12)).sum());
    }

}
