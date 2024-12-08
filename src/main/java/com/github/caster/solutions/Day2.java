package com.github.caster.solutions;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.Arrays;
import java.util.stream.LongStream;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.iterate;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static com.github.caster.shared.input.InputLoader.toColumns;
import static com.github.caster.shared.StreamUtils.iterateIndicesOf;
import static com.github.caster.shared.StreamUtils.streamWithoutIndex;

public final class Day2 extends BaseSolution {

    public Day2() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        System.out.println(
                read.lines()
                        .map(toColumns().andThen(parseLongs()).andThen(LongStream::toArray))
                        .filter(Day2::isSafeReport)
                        .count() +
                " reports are safe");
    }

    private static boolean isSafeReport(final long[] report) {
        // check being sorted
        val reportCopySortedAscending = stream(report).sorted().toArray();
        val reportCopySortedDescending = stream(report).map(i -> -i).sorted().map(i -> -i).toArray();
        if (!Arrays.equals(report, reportCopySortedAscending) && !Arrays.equals(report, reportCopySortedDescending)) {
            return false;
        }

        // check step size
        for (int i = 0; i < report.length - 1; i++) {
            val diff = reportCopySortedAscending[i + 1] - reportCopySortedAscending[i];
            if (diff < 1 || 3 < diff) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void part2() {
        System.out.println(
                read.lines()
                        .map(toColumns().andThen(parseLongs()).andThen(LongStream::toArray))
                        .filter(Day2::isTolerablySafeReport)
                        .count() +
                        " reports are tolerably safe");
    }

    private static boolean isTolerablySafeReport(final long[] report) {
        if (isSafeReport(report)) {
            return true;
        }

        return iterateIndicesOf(report)
                .filter(indexToRemove -> {
                    val reportWithoutIndexI = streamWithoutIndex(report, indexToRemove).toArray();
                    return isSafeReport(reportWithoutIndexI);
                })
                .findFirst()
                .isPresent();
    }

}
