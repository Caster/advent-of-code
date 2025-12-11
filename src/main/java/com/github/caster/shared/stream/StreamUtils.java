package com.github.caster.shared.stream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.IntStream.range;

@UtilityClass
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

    public static <T> IntStream iterateIndicesOf(final T[] array) {
        return range(0, array.length);
    }

    public static Stream<Character> stream(final char[] array) {
        return iterateIndicesOf(array).mapToObj(i -> array[i]);
    }

    public static <T> Stream<T> stream(final Iterator<T> iterator) {
        return StreamSupport.stream(spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    public static <T> Stream<T> streamReversed(final T[] array) {
        return Arrays.asList(array).reversed().stream();
    }

    public static LongStream streamWithoutIndex(final long[] array, final int indexToRemove) {
        return iterateIndicesOf(array)
                .filter(i -> i != indexToRemove)
                .mapToLong(i -> array[i]);
    }

}
