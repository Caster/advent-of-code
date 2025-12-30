package com.github.caster.shared;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import com.github.caster.shared.input.InputLoader;
import com.github.caster.shared.input.InputLoader.InputType;

import lombok.SneakyThrows;
import lombok.val;

import static com.github.caster.shared.input.InputLoader.formatYearDay;
import static java.lang.Math.abs;
import static java.time.Instant.now;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public abstract class BaseSolution2 {

    private static final Iterator<InputType> INPUT_TYPE_ITERATOR =
            Arrays.stream(InputType.values()).iterator();

    protected final InputLoader read;

    protected BaseSolution2() {
        read = new InputLoader();
        InputType inputType;
        do {
            inputType = INPUT_TYPE_ITERATOR.next();
        } while (!read.exists(inputType));
        read.from(inputType);
    }

    protected abstract long part1();

    protected long part2() {
        return 0;
    }

    static void main() {
        val day = formatYearDay("%2$s").andThen(dayPart -> dayPart.substring(3))
                .apply(System.getProperty("sun.java.command"));
        val resultsTable = new ArrayList<List<String>>();
        resultsTable.add(List.of(
                "Day " + day, "Setup", "Part 1", "Part 1", "Part 2", "Part 2"
        ));

        IO.println();

        while (INPUT_TYPE_ITERATOR.hasNext()) {
            val solutionReference = new AtomicReference<BaseSolution2>();
            val setupTime = time(() -> load(solutionReference)).findFirst().orElseThrow();
            val solution = solutionReference.get();
            val tableRow = new ArrayList<String>();
            tableRow.add(solution.read.inputType().name());
            tableRow.add(setupTime);

            time(solution::part1, solution::part2).forEach(tableRow::add);

            resultsTable.add(tableRow);
        }

        printTable(resultsTable);
    }

    @SafeVarargs
    private static Stream<String> time(final Callable<Long>... callables) {
        return Arrays.stream(callables).map(TimingResult::of).flatMap(TimingResult::stream);
    }

    private record TimingResult(long timeInMs, long result) {

        private static TimingResult of(final Callable<Long> callable) {
            val start = now();
            val result = sneakilyCall(callable);
            val stop = now();
            return new TimingResult(Duration.between(start, stop).toMillis(), result);
        }

        private Stream<String> stream() {
            return Stream.of("%d".formatted(result), "%,d ms".formatted(timeInMs));
        }

    }

    @SneakyThrows
    private static <T> T sneakilyCall(final Callable<T> callable) {
        return callable.call();
    }

    @SneakyThrows
    private static long load(final AtomicReference<BaseSolution2> solutionReference) {
        solutionReference.set(
                (BaseSolution2) Class.forName(System.getProperty("sun.java.command"))
                        .getConstructor().newInstance()
        );
        return 0;
    }

    private static void printTable(final List<List<String>> table) {
        // determine column widths
        val numCols = table.getFirst().size();
        val columnWidths = range(0, numCols)
                .mapToObj(columnIndex -> table.stream().map(row -> row.get(columnIndex)))
                .mapToInt(columnValues -> columnValues.mapToInt(String::length).max().orElse(0))
                .toArray();
        columnWidths[0] *= -1; // left-align first column


        // print headers
        val rowIterator = table.iterator();
        val headers = rowIterator.next();
        printWithWidth(headers.getFirst(), columnWidths[0]);
        for (int i = 1; i < numCols; i++) {
            if (headers.get(i - 1).equals(headers.get(i))) {
                IO.print("   ");
                printWithWidth("", -columnWidths[i]);
            } else {
                IO.print(" | ");
                printWithWidth(headers.get(i), -columnWidths[i]);
            }
        }
        IO.println();

        // print separator
        for (int i = 0; i < numCols; i++) {
            IO.print("-".repeat(abs(columnWidths[i]) + (i == 0 ? 1 : 2)));
            if (i == numCols - 1) {
                IO.println();
            } else {
                IO.print("+");
            }
        }

        // print data rows
        val format = Arrays.stream(columnWidths)
                .mapToObj(width -> "%" + width + "s")
                .collect(joining(" | "));
        while (rowIterator.hasNext()) {
            IO.println(format.formatted(rowIterator.next().toArray()));
        }
    }

    private static void printWithWidth(final String column, final int width) {
        IO.print(String.format("%" + width + "s", column));
    }

}
