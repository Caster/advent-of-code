package com.github.caster.solutions.y2025;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.LongStream;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.math.Matrix;
import com.github.caster.shared.stream.ZippingGatherer.Pair;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseColumns;
import static com.github.caster.shared.stream.StreamUtils.streamReversed;
import static com.github.caster.shared.stream.ZippingGatherer.zipWith;
import static java.lang.Long.parseLong;
import static java.util.Arrays.stream;
import static java.util.List.copyOf;
import static java.util.stream.IntStream.range;

public final class Day6 extends BaseSolution {

    private Matrix numbers;
    private char[][] numbersMatrix;
    private String[] operations;

    public Day6() {
        read.from(INPUT);
        read.with(sections -> {
            val numbersSection = sections.next();
            numbers = numbersSection.matrix();
            numbersMatrix = numbersSection.lines().map(String::toCharArray).toArray(char[][]::new);
            operations = parseColumns(sections.next().firstLine());
        });
    }

    @Override
    protected void part1() {
        IO.println(
                numbers.stream()
                        .gather(zipWith(stream(operations).map(this::toOperator)))
                        .mapToLong(this::applyOperationToColumn)
                        .sum()
        );
    }

    private LongBinaryOperator toOperator(final String operation) {
        return switch (operation) {
            case "+" -> Long::sum;
            case "*" -> (a, b) -> a * b;
            default -> throw new IllegalArgumentException(
                    "Unknown operation [%s]".formatted(operation));
        };
    }

    private long applyOperationToColumn(
            final Pair<LongStream, LongBinaryOperator> columnAndOperation
    ) {
        val column = columnAndOperation.t1();
        val operation = columnAndOperation.t2();
        return column.reduce(operation).orElseThrow();
    }

    @Override
    protected void part2() {
        val numRows = numbersMatrix.length;
        val numColumns = stream(numbersMatrix).mapToInt(row -> row.length).max().orElse(0);
        val grandTotal = range(0, numColumns).map(i -> numColumns - i - 1)
                .mapToObj(columnIndex -> range(0, numRows)
                        .filter(rowIndex -> columnIndex < numbersMatrix[rowIndex].length)
                        .mapToObj(rowIndex -> numbersMatrix[rowIndex][columnIndex])
                        .filter(character -> character != ' ')
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                        .toString())
                .gather(new StreamNumbersPerBlock())
                .gather(zipWith(streamReversed(operations).map(this::toOperator)))
                .mapToLong(this::applyOperationToColumn)
                .sum();
        IO.println(grandTotal);
    }

    private static class StreamNumbersPerBlock implements Gatherer<String, List<Long>, LongStream> {

        @Override
        public Supplier<List<Long>> initializer() {
            return ArrayList::new;
        }

        @Override
        public Integrator<List<Long>, String, LongStream> integrator() {
            return Integrator.of((blockNumbers, nextNumber, downstream) -> {
                if (nextNumber.isEmpty()) {
                    pushAndClear(blockNumbers, downstream);
                } else {
                    blockNumbers.add(parseLong(nextNumber));
                }
                return true;
            });
        }

        private void pushAndClear(
                final List<Long> longList,
                final Downstream<? super LongStream> downstream
        ) {
            downstream.push(copyOf(longList).stream().mapToLong(Long::longValue));
            longList.clear();
        }

        @Override
        public BiConsumer<List<Long>, Downstream<? super LongStream>> finisher() {
            return this::pushAndClear;
        }

    }

}
