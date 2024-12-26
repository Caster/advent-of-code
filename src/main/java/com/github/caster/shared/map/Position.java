package com.github.caster.shared.map;

import static java.lang.Math.abs;

public record Position(
        int x,
        int y
) {

    public long manhattanDistanceTo(final Position that) {
        return abs(that.x - this.x) + abs(that.y - this.y);
    }

    public Position moved(final Direction direction) {
        return switch (direction) {
            case UP -> new Position(x, y - 1);
            case RIGHT -> new Position(x + 1, y);
            case DOWN -> new Position(x, y + 1);
            case LEFT -> new Position(x - 1, y);
        };
    }

}
