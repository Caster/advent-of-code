package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;

public final class Day19 extends BaseSolution {

    public Day19() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        read.with(sections -> {
            val towels = sections.next().firstLine().split(", ");
            val pattern = Pattern.compile("^(%s)+$".formatted(String.join("|", towels)));

            System.out.println(
                    sections.next().lines()
                        .map(pattern::matcher)
                        .filter(Matcher::matches)
                        .count()
            );
        });
    }

    @Override
    protected void part2() {
        read.with(sections -> {
            val towels = sections.next().firstLine().split(", ");
            val memorizedPatterns = new HashMap<String, Long>();

            System.out.println(
                    sections.next().lines()
                            .mapToLong(line -> findNumberOfWaysToDesign(line, towels, memorizedPatterns))
                            .sum()
            );
        });
    }

    long findNumberOfWaysToDesign(
            final String pattern, final String[] towels, final Map<String, Long> memorizedPatterns
    ) {
        val memorized = memorizedPatterns.get(pattern);
        if (memorized != null) {
            return memorized;
        }
        val numberOfWays = Arrays.stream(towels)
                .filter(pattern::startsWith)
                .mapToLong(towel -> {
                    if (pattern.length() == towel.length()) {
                        return 1;
                    }
                    return findNumberOfWaysToDesign(pattern.substring(towel.length()), towels, memorizedPatterns);
                })
                .sum();
        memorizedPatterns.put(pattern, numberOfWays);
        return numberOfWays;
    }

}
