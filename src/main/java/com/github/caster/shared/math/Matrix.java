package com.github.caster.shared.math;

import lombok.val;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.IntStream.iterate;

public final class Matrix {

    private final long[][] columns;

    public Matrix(final int numberOfColumns, final int numberOfRows, final Stream<LongStream> values) {
        this.columns = new long[numberOfColumns][numberOfRows];
        val rowCount = new AtomicInteger(0);
        val colCount = new AtomicInteger(0);
        values.forEachOrdered(row -> {
            row.forEachOrdered(column -> columns[colCount.getAndIncrement()][rowCount.get()] = column);
            rowCount.incrementAndGet();
            colCount.set(0);
        });
    }

    public Vector column(final int index) {
        return new Vector(this.columns[index]);
    }

    public void sortColumns() {
        Arrays.stream(columns).forEach(Arrays::sort);
    }

    public Stream<LongStream> stream() {
        return iterate(0, i -> i < columns[0].length, i -> i + 1)
                .mapToObj(rowIndex -> iterate(0, i -> i < columns.length, i -> i + 1)
                        .mapToLong(columnIndex -> columns[columnIndex][rowIndex]));
    }

    public Stream<LongStream> streamTransposed() {
        return iterate(0, i -> i < columns.length, i -> i + 1)
                .mapToObj(columnIndex -> iterate(0, i -> i < columns[0].length, i -> i + 1)
                        .mapToLong(rowIndex -> columns[columnIndex][rowIndex]));
    }

    public Matrix transposed() {
        return new Matrix(columns[0].length, columns.length, streamTransposed());
    }

    @Override
    public String toString() {
        val result = new StringBuilder("[");
        val maxNumberOfDigitsPerColumn = streamTransposed()
                .map(LongStream::max)
                .mapToLong(maxValue -> (long) Math.ceil(Math.log10(maxValue.orElse(0))))
                .toArray();
        maxNumberOfDigitsPerColumn[0]--; // for the first column, we don't need a space
        val rowCount = new AtomicInteger(0);
        val colCount = new AtomicInteger(0);
        stream().forEachOrdered(row -> {
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
