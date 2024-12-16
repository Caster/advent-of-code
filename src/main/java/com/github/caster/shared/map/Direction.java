package com.github.caster.shared.map;

import lombok.RequiredArgsConstructor;
import lombok.val;

import static java.lang.Math.min;

@RequiredArgsConstructor
public enum Direction {
    UP('^'),
    RIGHT('>'),
    DOWN('v'),
    LEFT('<');

    public final char representation;

    public static Direction between(final Position posA, final Position posB) {
        val dx = posB.x() - posA.x();
        val dy = posB.y() - posA.y();
        if (dx ==  1 && dy ==  0)  return RIGHT;
        if (dx == -1 && dy ==  0)  return LEFT;
        if (dx ==  0 && dy == -1)  return UP;
        if (dx ==  0 && dy ==  1)  return DOWN;
        throw new IllegalArgumentException(
                "not a one Direction difference between [%s] and [%s]".formatted(posA, posB));
    }

    public static long difference(final Direction dirA, final Direction dirB) {
        return min(
                (dirB.ordinal() - dirA.ordinal() + 4) % 4,
                (dirA.ordinal() - dirB.ordinal() + 4) % 4
        );
    }

    public static boolean isDirectionRepresentation(final char value) {
        return value == UP.representation || value == RIGHT.representation
                || value == DOWN.representation || value == LEFT.representation;
    }

    public static Direction valueOf(final char value) {
        for (val direction : values()) {
            if (direction.representation == value) {
                return direction;
            }
        }
        throw new IllegalArgumentException("unknown direction [%s]".formatted(value));
    }

    public Direction turnCounterclockwise() {
        return values()[(this.ordinal() + 3) % 4];
    }

    public Direction turnClockwise() {
        return values()[(this.ordinal() + 1) % 4];
    }

}
