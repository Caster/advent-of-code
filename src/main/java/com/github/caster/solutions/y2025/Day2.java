package com.github.caster.solutions.y2025;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import com.github.caster.shared.BaseSolution;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.util.Arrays.stream;
import static java.util.stream.LongStream.rangeClosed;

public final class Day2 extends BaseSolution {

    public Day2() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        IO.println(sumIdsInRangesFilteringBy(id -> isRepeatsSequence(id, 2)));
    }

    private long sumIdsInRangesFilteringBy(final Predicate<String> isInvalidId) {
        return stream(read.firstLine().split(","))
                .flatMapToLong(rangeStr -> {
                    val range = parseLongs(rangeStr, "-");
                    return rangeClosed(range[0], range[1]);
                })
                .mapToObj(Long::toString)
                .filter(isInvalidId)
                .mapToLong(Long::parseLong)
                .sum();
    }

    private boolean isRepeatsSequence(final String id, final int numRepeats) {
        val length = id.length();
        if (length % numRepeats != 0) {
            return false;
        }
        val seqLength = length / numRepeats;
        val seq = id.substring(0, seqLength);
        return IntStream.range(1, numRepeats).map(i -> i * seqLength)
                .mapToObj(startIndex -> id.substring(startIndex, startIndex + seqLength))
                .allMatch(seq::equals);
    }

    @Override
    protected void part2() {
        IO.println(
                sumIdsInRangesFilteringBy(id -> IntStream.rangeClosed(2, id.length())
                        .anyMatch(numRepeats -> isRepeatsSequence(id, numRepeats)))
        );
    }

}
