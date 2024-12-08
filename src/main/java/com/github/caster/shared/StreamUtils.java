package com.github.caster.shared;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

public final class StreamUtils {

    public static IntStream iterateFromTo(final int startInclusive, final int endExclusive) {
        return IntStream.iterate(startInclusive, i -> i < endExclusive, i -> i + 1);
    }

    public static IntStream iterateIndicesOf(final char[] array) {
        return iterateFromTo(0, array.length);
    }

    public static IntStream iterateIndicesOf(final long[] array) {
        return iterateFromTo(0, array.length);
    }

    public static LongStream streamWithoutIndex(final long[] array, final int indexToRemove) {
        return iterateIndicesOf(array)
                .filter(i -> i != indexToRemove)
                .mapToLong(i -> array[i]);
    }

}
