package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.input.InputLoader;
import lombok.val;

import java.util.Arrays;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.stream.StreamUtils.iterateIndicesOf;
import static com.github.caster.shared.stream.StreamUtils.streamWithoutIndex;
import static java.util.Arrays.stream;

public final class Day2 extends BaseSolution {

    public Day2() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        System.out.println(
                read.lines().map(InputLoader::parseLongs).filter(Day2::isSafeReport).count()
                        + " reports are safe"
        );
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
                read.lines().map(InputLoader::parseLongs).filter(Day2::isTolerablySafeReport).count()
                        + " reports are tolerably safe");
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
