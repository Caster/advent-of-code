package com.github.caster.shared.math;

import lombok.val;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;

public final class Matrix {

    private final long[][] columns;

    public Matrix(final Stream<long[]> columns) {
        this.columns = columns.toArray(long[][]::new);
    }

    public Vector column(final int index) {
        return new Vector(columns[index]);
    }

    public void sortColumns() {
        Arrays.stream(columns).forEach(Arrays::sort);
    }

    public Stream<LongStream> stream() {
        return Arrays.stream(columns).map(Arrays::stream);
    }

    public Stream<LongStream> streamTransposed() {
        return range(0, columns[0].length)
                .mapToObj(rowIndex -> Arrays.stream(columns).mapToLong(column -> column[rowIndex]));
    }

    public Matrix transposed() {
        return new Matrix(streamTransposed().map(LongStream::toArray));
    }

    @Override
    public String toString() {
        val result = new StringBuilder("[");
        val maxNumberOfDigitsPerColumn = stream()
                .map(LongStream::max)
                .mapToLong(maxValue -> (long) Math.ceil(Math.log10(maxValue.orElse(0))))
                .toArray();
        maxNumberOfDigitsPerColumn[0]--; // for the first column, we don't need a space
        val rowCount = new AtomicInteger(0);
        val colCount = new AtomicInteger(0);
        streamTransposed().forEachOrdered(row -> {
            row.forEachOrdered(value -> {
                result.append("%%%dd".formatted(maxNumberOfDigitsPerColumn[colCount.getAndIncrement()] + 1)
                        .formatted(value));
            });
            if (rowCount.incrementAndGet() < columns[0].length) {
                result.append("\n ");
            }
            colCount.set(0);
        });
        return result.append("]").toString();
    }
}
