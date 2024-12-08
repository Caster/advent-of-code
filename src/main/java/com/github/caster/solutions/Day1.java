package com.github.caster.solutions;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public final class Day1 extends BaseSolution {

    public Day1() {
        read.from(EXAMPLE);
    }

    @Override
    protected void part1() {
        val input = read.matrix();
        input.sortColumns();
        System.out.println(
                input.column(0)
                        .minus(input.column(1))
                        .absoluteSum()
        );
    }

    @Override
    protected void part2() {
        val input = read.matrix();
        val counts = input.column(1).stream()
                .boxed()
                .collect(groupingBy(identity(), counting()));
        System.out.println(
                input.column(0).stream()
                        .map(number -> number * counts.getOrDefault(number, 0L))
                        .sum()
        );
    }

}
