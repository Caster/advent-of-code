package com.github.caster.shared.map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
    UP('^'),
    RIGHT('>'),
    DOWN('v'),
    LEFT('<');

    public final char representation;

    public static boolean isDirectionRepresentation(final char value) {
        return value == UP.representation || value == RIGHT.representation
                || value == DOWN.representation || value == LEFT.representation;
    }

    public Direction turnCounterclockwise() {
        return values()[(this.ordinal() + 3) % 4];
    }

    public Direction turnClockwise() {
        return values()[(this.ordinal() + 1) % 4];
    }
}
