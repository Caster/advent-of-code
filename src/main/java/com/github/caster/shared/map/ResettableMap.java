package com.github.caster.shared.map;

import com.github.caster.shared.math.Vector;
import lombok.val;

import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.IntStream.range;

public final class ResettableMap {

    @FunctionalInterface
    public interface CellVisitor {

        boolean continueVisit(final char value, final int y, final int x);

    }

    public record Cell(char value, int x, int y) {}

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

    public boolean contains(final Vector position) {
        val x = position.get(0);
        val y = position.get(1);
        return 0 <= x && x < numColumns && 0 <= y && y < numRows;
    }

    public void forEachCell(final CellVisitor visitor) {
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                if (!visitor.continueVisit(get(y, x), y, x)) {
                    return;
                }
            }
        }
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
