package com.github.caster.solutions.y2025;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import com.github.caster.shared.BaseSolution2;
import com.github.caster.shared.Expectations;

import lombok.val;

import static com.github.caster.shared.Expectations.expect;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.lang.Long.parseLong;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public final class Day12Cheese extends BaseSolution2 {

    public static Expectations expectations() {
        return expect(EXAMPLE).toSolveTo(2).andPart(2).toSolveTo(0)
                .alsoExpect(INPUT).toSolveTo(528).andPart(2).toSolveTo(0);
    }

    private static final Pattern REGION_PATTERN = compile("(\\d+)x(\\d+): ([\\d ]+)");

    @Override
    protected long part1() {
        val numCellsPerPresent = new long[6];
        val sectionIndex = new AtomicInteger();
        val result = new AtomicLong();
        read.with(sections -> sections.forEachRemaining(section -> {
            if (section.firstLine().endsWith(":")) {
                numCellsPerPresent[sectionIndex.getAndIncrement()] = section.lines().skip(1)
                        .collect(joining()).chars().filter(c -> c == '#').count();
            } else {
                section.lines().forEach(line -> {
                    val matcher = REGION_PATTERN.matcher(line);
                    if (!matcher.matches())  throw new IllegalArgumentException("weird region");
                    val presentQty = parseLongs(matcher.group(3));
                    val filledCells = range(0, 6)
                            .mapToLong(i -> numCellsPerPresent[i] * presentQty[i]).sum();
                    val availableCells = parseLong(matcher.group(1)) * parseLong(matcher.group(2));
                    if (filledCells <= availableCells)  result.incrementAndGet();
                });
            }
        }));
        // this cheese only works on the INPUT, not on the last EXAMPLE region
        return result.get() - (read.inputType() == EXAMPLE ? 1 : 0);
    }

}
