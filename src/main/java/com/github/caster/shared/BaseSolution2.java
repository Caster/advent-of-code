package com.github.caster.shared;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator.OfInt;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import com.github.caster.shared.Expectations.Input;
import com.github.caster.shared.input.InputLoader;
import com.github.caster.shared.input.InputLoader.InputType;

import lombok.SneakyThrows;
import lombok.val;

import static com.github.caster.shared.input.InputLoader.exists;
import static com.github.caster.shared.input.InputLoader.formatYearDay;
import static com.github.caster.shared.input.InputLoader.getSolutionClassName;
import static java.lang.Character.isDigit;
import static java.lang.Character.isEmoji;
import static java.lang.Math.abs;
import static java.time.Instant.now;
import static java.util.Comparator.comparingInt;
import static java.util.List.of;
import static java.util.stream.IntStream.iterate;
import static java.util.stream.IntStream.range;

public abstract class BaseSolution2 {

    private static final Queue<InputType> INPUT_TYPES = new ArrayDeque<>(of(InputType.values()));
    private static final OfInt PART_ITERATOR = iterate(0, p -> (p + 1) % 3).iterator();
    private static final Expectations EXPECTATIONS;

    private static InputType currentInputType;

    static {
        Expectations expectationsToLoad;
        try {
            val solutionClass = Class.forName(getSolutionClassName());
            val expectationsMethod = solutionClass.getDeclaredMethod("expectations");
            expectationsToLoad = (Expectations) expectationsMethod.invoke(null);
        } catch (final Exception _) {
            expectationsToLoad = Expectations.empty();
        }
        EXPECTATIONS = expectationsToLoad;
    }

    protected final InputLoader read;

    protected BaseSolution2() {
        read = new InputLoader();
        read.from(currentInputType = INPUT_TYPES.poll());
    }

    protected abstract long part1();

    protected long part2() {
        return 0;
    }

    static void main() {
        val day = formatYearDay("%2$s").andThen(dayPart -> dayPart.substring(3))
                .apply(getSolutionClassName());
        val resultsTable = new ArrayList<List<String>>();
        resultsTable.add(List.of("Day " + day, "Setup", "Part 1", "", "Part 2", ""));

        IO.println();

        while (!INPUT_TYPES.isEmpty()) {
            if (!exists(INPUT_TYPES.peek())) {
                INPUT_TYPES.poll();
                continue;
            }

            val solutionReference = new AtomicReference<BaseSolution2>();
            val setupTime = time(() -> load(solutionReference)).skip(1).findFirst().orElseThrow();
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

    private record TimingResult(int part, long timeInMs, long result) {

        private static TimingResult of(final Callable<Long> callable) {
            val start = now();
            val result = sneakilyCall(callable);
            val stop = now();
            return new TimingResult(
                    PART_ITERATOR.next(),
                    Duration.between(start, stop).toMillis(),
                    result
            );
        }

        private Stream<String> stream() {
            val optionalResult = EXPECTATIONS.get(new Input(currentInputType, part));
            val emoji = (optionalResult.isEmpty() ? "❓" :
                    optionalResult.getAsLong() == result ? "✅" : "❌");
            return Stream.of("%d %s".formatted(result, emoji), "%,d ms".formatted(timeInMs));
        }

    }

    @SneakyThrows
    private static <T> T sneakilyCall(final Callable<T> callable) {
        return callable.call();
    }

    @SneakyThrows
    private static long load(final AtomicReference<BaseSolution2> solutionReference) {
        solutionReference.set(
                (BaseSolution2) Class.forName(getSolutionClassName()).getConstructor().newInstance()
        );
        return 0;
    }

    private static void printTable(final List<List<String>> table) {
        // determine column widths
        val numCols = table.getFirst().size();
        val columnWidths = range(0, numCols)
                .mapToObj(columnIndex -> table.stream()
                        .map(row -> row.get(columnIndex))
                        .max(comparingInt(BaseSolution2::stringLengthEmojiAs2)).orElseThrow())
                .mapToInt(BaseSolution2::stringLengthEmojiAs2).toArray();
        columnWidths[0] *= -1; // left-align first column

        // print headers
        val rowIterator = table.iterator();
        val headers = rowIterator.next();
        printWithWidth(headers.getFirst(), columnWidths[0]);
        for (int i = 1; i < numCols; i++) {
            val columnWidth = columnWidths[i];
            if (headers.get(i).isEmpty()) {
                IO.print("   ");
                printWithWidth("", -columnWidth);
            } else {
                IO.print(" | ");
                printWithWidth(headers.get(i), -columnWidth);
            }
        }
        IO.println();

        // print separator
        for (int i = 0; i < numCols; i++) {
            val columnPadding = i == 0 ? 1 : 2;
            IO.print("-".repeat(abs(columnWidths[i]) + columnPadding));
            if (i == numCols - 1) {
                IO.println();
            } else {
                IO.print("+");
            }
        }

        // print data rows
        while (rowIterator.hasNext()) {
            val row = rowIterator.next();
            printWithWidth(row.getFirst(), columnWidths[0]);
            for (int i = 1; i < numCols; i++) {
                val column = row.get(i);
                val columnWidth = columnWidths[i] - (stringLengthEmojiAs2(column) - column.length());
                IO.print(" | ");
                printWithWidth(column, columnWidth);
            }
            IO.println();
        }
    }

    private static int stringLengthEmojiAs2(final String string) {
        return string.codePoints().reduce(0, (s, c) -> s + (isEmoji(c) && !isDigit(c) ? 2 : 1));
    }

    private static void printWithWidth(final String column, final int width) {
        IO.print(String.format("%" + width + "s", column));
    }

}
