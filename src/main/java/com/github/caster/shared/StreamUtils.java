package com.github.caster.shared;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.IntStream.range;

public final class StreamUtils {

    public static IntStream iterateIndicesOf(final char[] array) {
        return range(0, array.length);
    }

    public static IntStream iterateIndicesOf(final long[] array) {
        return range(0, array.length);
    }

    public static <T> IntStream iterateIndicesOf(final List<T> list) {
        return range(0, list.size());
    }

    public static LongStream streamWithoutIndex(final long[] array, final int indexToRemove) {
        return iterateIndicesOf(array)
                .filter(i -> i != indexToRemove)
                .mapToLong(i -> array[i]);
    }

}
