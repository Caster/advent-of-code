package com.github.caster.solutions.y2025;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.caster.shared.BaseSolution2;
import com.github.caster.shared.Expectations;
import com.github.caster.shared.map.Position;
import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.stream.ZippingGatherer.Pair;

import lombok.val;

import static com.github.caster.shared.Expectations.expect;
import static com.github.caster.shared.collections.Lists.copyListWithout;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseInts;
import static com.github.caster.shared.map.Direction.RIGHT;
import static com.github.caster.shared.stream.ZippingGatherer.zipWith;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.generate;

public final class Day12 extends BaseSolution2 {

    public static Expectations expectations() {
        return expect(EXAMPLE).toSolveTo(2).andPart(2).toSolveTo(0)
                .alsoExpect(INPUT).toSolveTo(528).andPart(2).toSolveTo(0);
    }

    private final List<Present> presents;
    private final List<Region> regions;

    private record Present(String shape, int coveredCells) {

        static Present from(final Stream<String> lines) {
            val shape = lines.collect(joining());
            if (shape.length() != 9) {
                throw new IllegalStateException("unexpectedly found a present that is not 3x3");
            }
            return new Present(shape);
        }

        Present(final String shape) {
            this(shape, (int) shape.chars().filter(c -> c == '#').count());
        }

        boolean fitsIn(final ResettableMap region, final Position from) {
            for (int p = 0; p < 9; p++) {
                if (shape.charAt(p) != '#')  continue;
                if (region.get(getPosition(from, p)) == '#')  return false;
            }

            for (int p = 0; p < 9; p++) {
                if (shape.charAt(p) != '#')  continue;
                region.set(getPosition(from, p), '#');
            }
            return true;
        }

        private Position getPosition(final Position from, final int p) {
            return new Position(from.x() + p % 3, from.y() + p / 3);
        }

        void removeFrom(final ResettableMap region, final Position from) {
            for (int p = 0; p < 9; p++) {
                if (shape.charAt(p) == '#') {
                    region.set(getPosition(from, p), '.');
                }
            }
        }

        @Override
        public String toString() {
            return "Present[%n  %s%n  %s%n  %s%n]".formatted(
                    shape.substring(0, 3), shape.substring(3, 6), shape.substring(6)
            );
        }

        Set<Present> uniqueOrientations() {
            val c = shape.toCharArray();
            final char[][] orientations = {
                    {c[6], c[3], c[0], c[7], c[4], c[1], c[8], c[5], c[2]}, // rot. cw  90 deg
                    {c[8], c[7], c[6], c[5], c[4], c[3], c[2], c[1], c[0]}, // rot. cw 180 deg
                    {c[2], c[5], c[8], c[1], c[4], c[7], c[0], c[3], c[6]}, // rot. cw 270 deg
                    {c[6], c[7], c[8], c[3], c[4], c[5], c[0], c[1], c[2]}, // flipped vertically
                    {c[0], c[3], c[6], c[1], c[4], c[7], c[2], c[5], c[8]}, // fv; rot. cw.  90 deg
                    {c[2], c[1], c[0], c[5], c[4], c[3], c[8], c[7], c[6]}, // fv; rot. cw. 180 deg
                                                                        // (= flipped horizontally)
                    {c[8], c[5], c[2], c[7], c[4], c[1], c[6], c[3], c[0]}, // fv; rot. cw. 270 deg
            };
            return concat(
                    Stream.of(this),
                    stream(orientations).map(String::new).map(Present::new)
            ).collect(toSet());
        }

    }

    private record Region(int id, ResettableMap region, List<Present> presentsToFit) {

        private static final Pattern REGION_PATTERN = compile("(\\d+)x(\\d+): ([\\d ]+)");

        static Region from(final int id, final String line, final List<Present> presents) {
            val matcher = REGION_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("cannot parse Region from [%s]".formatted(line));
            }

            val presentQuantities = parseInts(matcher.group(3));
            return new Region(
                    id,
                    ResettableMap.empty(parseInt(matcher.group(1)), parseInt(matcher.group(2))),
                    stream(presentQuantities).boxed()
                            .gather(zipWith(presents.stream()))
                            .<Present>mapMulti((presentQty, downstream) ->
                                generate(presentQty::t2).limit(presentQty.t1()).forEach(downstream))
                            .toList()
            );
        }

        boolean fitsAllPresents(final int numRegions) {
            IO.print("checking if region %d / %d fits all presents ...\r".formatted(id, numRegions));
            val cellsToFit = presentsToFit.stream().mapToInt(Present::coveredCells).sum();
            val openCells = region.numRows * region.numColumns;
            val fits = fitsAllOf(presentsToFit, new Position(0, 0), cellsToFit, openCells);
            region.reset();
            return fits;
        }

        private boolean fitsAllOf(
                final List<Present> presents,
                final Position from,
                final int cellsToFit,
                final int openCells
        ) {
            if (cellsToFit > openCells)  return false;
            if (presents.isEmpty())  return true;

            val nextPosAndSkippedOpenCells = nextPosition(from);
            val nextPos = nextPosAndSkippedOpenCells.t1();
            val skippedOpenCells = nextPosAndSkippedOpenCells.t2();
            Present lastPresent = null;
            for (val present : presents) {
                if (present.equals(lastPresent)) {
                    continue; // skip checking more quantity of same present at same position
                }
                for (val orientation : present.uniqueOrientations()) {
                    if (orientation.fitsIn(region, from)) {
                        if (nextPos.isEmpty()) {
                            if (presents.size() > 1) {
                                orientation.removeFrom(region, from);
                            }
                            return presents.size() == 1;
                        }
                        if (fitsAllOf(
                                copyListWithout(presents, present),
                                nextPos.get(),
                                cellsToFit - orientation.coveredCells(),
                                openCells - skippedOpenCells
                        )) {
                            return true;
                        }
                        orientation.removeFrom(region, from);
                    }
                }
                lastPresent = present;
            }
            return nextPos.map(next ->
                            fitsAllOf(presents, next, cellsToFit, openCells - skippedOpenCells))
                    .orElse(false);
        }

        ///
        /// Returns one position to the right of `from`, or if too close to the right border then
        /// the first position on the next row, or if that's too close to the bottom border then
        /// an empty [Optional].
        ///
        /// The second member of the returned [Pair] is the number of open cells that is skipped by
        /// moving to this next position.
        ///
        private Pair<Optional<Position>, Integer> nextPosition(final Position from) {
            if (from.x() < region.numColumns - 3) {
                return new Pair<>(Optional.of(from.moved(RIGHT)), region.get(from) == '.' ? 1 : 0);
            }
            if (from.y() < region.numRows - 3) {
                final int skipped = (int) range(from.x(), region.numColumns)
                        .mapToObj(x -> region.get(from.y(), x))
                        .filter(c -> c == '.')
                        .count();
                return new Pair<>(Optional.of(new Position(0, from.y() + 1)), skipped);
            }
            return new Pair<>(Optional.empty(), 0);
        }

    }

    public Day12() {
        presents = new ArrayList<>();
        regions = new ArrayList<>();

        val regionId = new AtomicInteger();
        read.with(sections -> sections.forEachRemaining(section -> {
            if (section.firstLine().endsWith(":")) {
                presents.add(Present.from(section.lines().skip(1)));
            } else {
                section.lines().forEach(line ->
                        regions.add(Region.from(regionId.incrementAndGet(), line, presents)));
            }
        }));
    }

    @Override
    protected long part1() {
        return regions.stream().filter(region -> region.fitsAllPresents(regions.size())).count();
    }

}
