package com.github.caster.solutions.y2025;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.github.caster.shared.BaseSolution2;
import com.github.caster.shared.Expectations;
import com.github.caster.shared.math.Vector;
import com.github.caster.shared.stream.ZippingGatherer.Pair;

import lombok.val;

import static com.github.caster.shared.Expectations.expect;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static com.github.caster.shared.stream.StreamUtils.streamUniquePairs;
import static java.lang.Long.max;
import static java.lang.Long.min;
import static java.util.stream.Gatherers.windowSliding;
import static java.util.stream.Stream.concat;

public final class Day9 extends BaseSolution2 {

    public static Expectations expectations() {
        return expect(EXAMPLE).toSolveTo(50).andPart(2).toSolveTo(24)
                .alsoExpect(INPUT).toSolveTo(4759420470L).andPart(2).toSolveTo(1603439684L);
    }

    private final Vector[] redTilePositions;

    public Day9() {
        redTilePositions = read.lines()
                .map(line -> new Vector(parseLongs(line, ",")))
                .toArray(Vector[]::new);
    }

    @Override
    protected long part1() {
        return streamUniquePairs(redTilePositions)
                .map(Rectangle::from)
                .mapToLong(Rectangle::area)
                .max().orElseThrow();
    }

    private record Rectangle(long x, long y, long w, long h) {

        static Rectangle from(final Pair<Vector, Vector> corners) {
            val size = corners.t1().minus(corners.t2()).abs();
            return new Rectangle(
                    min(corners.t1().get(0), corners.t2().get(0)),
                    min(corners.t1().get(1), corners.t2().get(1)),
                    size.get(0) + 1,
                    size.get(1) + 1
            );
        }

        long area() {
            return w * h;
        }

        boolean intersects(final Interval interval) {
            return switch (interval) {
                case HorizontalInterval(long iy, long ix1, long ix2) ->
                    y < iy && iy < y + h - 1
                        && (x < ix2 && ix1 < x + w - 1);
                case VerticalInterval(long ix, long iy1, long iy2) ->
                    x < ix && ix < x + w - 1
                        && (y < iy2 && iy1 < y + h - 1);
            };
        }

    }

    @Override
    protected long part2() {
        val lastRedTilePosition = redTilePositions[redTilePositions.length - 1];
        val redGreenBorder = concat(Stream.of(lastRedTilePosition), Arrays.stream(redTilePositions))
                .gather(windowSliding(2))
                .map(this::toInterval)
                .toList();
        return streamUniquePairs(redTilePositions).parallel()
                .map(Rectangle::from)
                .filter(rectangle -> redGreenBorder.stream().noneMatch(rectangle::intersects))
                .mapToLong(Rectangle::area)
                .max().orElseThrow();
    }

    private Interval toInterval(final List<Vector> endpoints) {
        val p1 = endpoints.getFirst();
        val p2 = endpoints.getLast();
        if (p1.get(0) == p2.get(0)) {
            return new VerticalInterval(p1.get(0), p1.get(1), p2.get(1));
        }
        return new HorizontalInterval(p1.get(1), p1.get(0), p2.get(0));
    }

    private sealed interface Interval permits HorizontalInterval, VerticalInterval {}

    private record HorizontalInterval(long y, long x1, long x2) implements Interval {
        HorizontalInterval {
            val minX = min(x1, x2);
            val maxX = max(x1, x2);
            x1 = minX;
            x2 = maxX;
        }
    }

    private record VerticalInterval(long x, long y1, long y2) implements Interval {
        VerticalInterval {
            val minY = min(y1, y2);
            val maxY = max(y1, y2);
            y1 = minY;
            y2 = maxY;
        }
    }

}
