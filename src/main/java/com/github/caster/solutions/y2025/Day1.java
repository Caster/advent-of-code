package com.github.caster.solutions.y2025;

import com.github.caster.shared.BaseSolution;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public final class Day1 extends BaseSolution {

    private static final int DIAL_SIZE = 100;

    public Day1() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        val start = 50;
        val input = read.lines().mapToInt(rotation ->
                        (rotation.charAt(0) == 'R' ? 1 : -1) *
                                parseInt(rotation.substring(1)))
                .toArray();
        int zeroCount = 0;
        int position = start;
        for (int rotation : input) {
            position = (position + rotation + DIAL_SIZE) % DIAL_SIZE;
            if (position == 0) {
                zeroCount++;
            }
        }
        System.out.println(zeroCount);
    }

    @Override
    protected void part2() {
        val start = 50;
        val input = read.lines().mapToInt(rotation ->
                        (rotation.charAt(0) == 'R' ? 1 : -1) *
                                parseInt(rotation.substring(1)))
                .toArray();
        int zeroCount = 0;
        int position = start;
        for (int rotation : input) {
            zeroCount += abs(rotation) / DIAL_SIZE;
            val oldPosition = position;
            position += rotation % DIAL_SIZE;
            if (oldPosition > 0 && (position <= 0 || position >= DIAL_SIZE)) {
                zeroCount++;
            }
            position = (position + DIAL_SIZE) % DIAL_SIZE;
        }
        System.out.println(zeroCount);
    }

}
