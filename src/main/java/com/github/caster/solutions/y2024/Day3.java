package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.regex.Pattern;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Long.parseLong;

public final class Day3 extends BaseSolution {

    public Day3() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        val input = read.string();
        val mulMatcher = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)").matcher(input);

        var result = 0L;
        while (mulMatcher.find()) {
            result += parseLong(mulMatcher.group(1)) * parseLong(mulMatcher.group(2));
        }
        System.out.println(result);
    }

    @Override
    protected void part2() {
        val input = read.string();
        val mulMatcher = Pattern.compile("do\\(\\)|don't\\(\\)|" +
                "mul\\((\\d{1,3}),(\\d{1,3})\\)").matcher(input);

        var mulEnabled = true;
        var result = 0L;
        while (mulMatcher.find()) {
            switch (mulMatcher.group()) {
                case "do()" -> mulEnabled = true;
                case "don't()" -> mulEnabled = false;
                default -> {
                    if (mulEnabled) {
                        result += parseLong(mulMatcher.group(1)) * parseLong(mulMatcher.group(2));
                    }
                }
            }
        }
        System.out.println(result);
    }
}
