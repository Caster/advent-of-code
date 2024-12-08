package com.github.caster.shared.map;

public record Position(
        int x,
        int y
) {

    public Position moved(final Direction direction) {
        return switch (direction) {
            case UP -> new Position(x, y - 1);
            case RIGHT -> new Position(x + 1, y);
            case DOWN -> new Position(x, y + 1);
            case LEFT -> new Position(x - 1, y);
        };
    }

}
