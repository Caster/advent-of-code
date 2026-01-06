package com.github.caster.shared.map;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.caster.shared.math.Vector;

import lombok.val;

import static java.util.function.Function.identity;
import static java.util.stream.IntStream.range;

public final class ResettableMap {

    @FunctionalInterface
    public interface CellVisitor {

        boolean continueVisit(final char value, final int y, final int x);

    }

    public record Cell(char value, int x, int y) {

        public static Predicate<Cell> cellValueIs(final char value) {
            return cell -> cell.value == value;
        }

        public Stream<Cell> neighbors(final ResettableMap map) {
            val position = new Position(x, y);
            return Arrays.stream(Direction.values())
                    .map(position::moved)
                    .filter(map::contains)
                    .map(p -> new Cell(map.get(p), p.x(), p.y()));
        }

        public Stream<Cell> octoNeighbors(final ResettableMap map) {
            return range(0, 9).mapToObj(i -> new Position(
                    x - 1 + i % 3,
                    y - 1 + i / 3
            )).filter(map::contains).map(p -> new Cell(map.get(p), p.x(), p.y()));
        }

        public Position position() {
            return new Position(x, y);
        }

        public long corners(final ResettableMap map) {
            val hasSameValue = range(-1, 2)
                    .mapToObj(dx -> range(-1, 2)
                            .mapToObj(dy -> {
                                val pos = new Position(x + dx, y + dy);
                                if (!map.contains(pos))  return false;
                                return map.get(pos) == value;
                            })
                            .toArray(Boolean[]::new)
                    )
                    .toArray(Boolean[][]::new);
            return (
                // "inner" corners
                toInt(hasSameValue[1][0] && hasSameValue[0][1] && !hasSameValue[0][0]) +
                toInt(hasSameValue[0][1] && hasSameValue[1][2] && !hasSameValue[0][2]) +
                toInt(hasSameValue[1][2] && hasSameValue[2][1] && !hasSameValue[2][2]) +
                toInt(hasSameValue[2][1] && hasSameValue[1][0] && !hasSameValue[2][0]) +
                // "outer" corners
                toInt(!(hasSameValue[0][1] || hasSameValue[1][0])) +
                toInt(!(hasSameValue[0][1] || hasSameValue[1][2])) +
                toInt(!(hasSameValue[2][1] || hasSameValue[1][0])) +
                toInt(!(hasSameValue[2][1] || hasSameValue[1][2]))
            );
        }

        private int toInt(final boolean b) {
            return b ? 1 : 0;
        }

    }

    public static ResettableMap empty(final int width, final int height) {
        return new ResettableMap(
                range(0, height)
                        .mapToObj(_ -> ".".repeat(width).toCharArray())
                        .toArray(char[][]::new)
        );
    }

    public final int numRows;
    public final int numColumns;

    private final char[][] map;
    private final char[][] mapOverlay;
    private final short[][] setInVersion;

    private short version = 1;

    public ResettableMap(final char[][] map) {
        this.map = map;
        this.numRows = map.length;
        this.numColumns = map[0].length;
        this.mapOverlay = new char[numRows][numColumns];
        this.setInVersion = new short[numRows][numColumns];
    }

    public boolean contains(final Position position) {
        val x = position.x();
        val y = position.y();
        return 0 <= x && x < numColumns && 0 <= y && y < numRows;
    }

    public boolean contains(final Vector position) {
        val x = position.get(0);
        val y = position.get(1);
        return 0 <= x && x < numColumns && 0 <= y && y < numRows;
    }

    public Stream<Cell> stream() {
        return range(0, numRows)
                .mapToObj(y -> range(0, numColumns).mapToObj(x -> new Cell(get(y, x), x, y)))
                .flatMap(identity());
    }

    public char get(final int y, final int x) {
        return setInVersion[y][x] == version ? mapOverlay[y][x] : map[y][x];
    }

    public char get(final Position position) {
        return get(position.y(), position.x());
    }

    public Cell getCell(final Position position) {
        return new Cell(get(position.y(), position.x()), position.x(), position.y());
    }

    public void set(final int y, final int x, final char value) {
        mapOverlay[y][x] = value;
        setInVersion[y][x] = version;
    }

    public void set(final Position position, final char value) {
        set(position.y(), position.x(), value);
    }

    public void set(final Vector position, final char value) {
        set((int) position.get(1), (int) position.get(0), value);
    }

    public void reset() {
        version++;
    }

    @Override
    public String toString() {
        val result = new StringBuilder();
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                result.append(get(y, x));
            }
            result.append("\n");
        }
        return result.toString();
    }
}
