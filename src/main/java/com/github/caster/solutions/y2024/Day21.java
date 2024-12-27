package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Position;
import lombok.val;

import java.util.Map;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;
import static java.util.Map.entry;

public final class Day21 extends BaseSolution {

    public Day21() {
        read.from(INPUT);
    }

    /*
        +---+---+---+
        | 7 | 8 | 9 |
        +---+---+---+
        | 4 | 5 | 6 |
        +---+---+---+
        | 1 | 2 | 3 |
        +---+---+---+
            | 0 | A |
            +---+---+
    */
    private static final Map<Character, Position> NUMERIC_KEYPAD_POSITIONS = Map.ofEntries(
        entry('7', new Position(0, 0)),
        entry('8', new Position(1, 0)),
        entry('9', new Position(2, 0)),
        entry('4', new Position(0, 1)),
        entry('5', new Position(1, 1)),
        entry('6', new Position(2, 1)),
        entry('1', new Position(0, 2)),
        entry('2', new Position(1, 2)),
        entry('3', new Position(2, 2)),
        entry(' ', new Position(0, 3)),
        entry('0', new Position(1, 3)),
        entry('A', new Position(2, 3))
    );

    /*
            +---+---+
            | ^ | A |
        +---+---+---+
        | < | v | > |
        +---+---+---+
    */
    private static final Map<Character, Position> DIRECTIONAL_KEYPAD_POSITIONS = Map.ofEntries(
            entry(' ', new Position(0, 0)),
            entry('^', new Position(1, 0)),
            entry('A', new Position(2, 0)),
            entry('<', new Position(0, 1)),
            entry('v', new Position(1, 1)),
            entry('>', new Position(2, 1))
    );

    @Override
    protected void part1() {
        System.out.println(read.lines().mapToLong(code -> {
            var sequence = findSequenceToType(code, NUMERIC_KEYPAD_POSITIONS);
            for (var i = 0; i < 2; i++) {
                sequence = findSequenceToType(sequence, DIRECTIONAL_KEYPAD_POSITIONS);
            }
            System.out.printf("%s: %-74s (%d * %d)%n", code, sequence, sequence.length(), parseLong(code.substring(0, 3)));
            return parseLong(code.substring(0, 3)) * sequence.length();
        }).sum());
    }

    String findSequenceToType(final String code, final Map<Character, Position> positionMap) {
        val sequence = new StringBuilder();
        var curPos = positionMap.get('A');
        for (val buttonToPress : code.toCharArray()) {
            var targetPos = positionMap.get(buttonToPress);
            val xDiff = targetPos.x() - curPos.x();
            val yDiff = targetPos.y() - curPos.y();
            val h = (xDiff < 0 ? "<" : ">").repeat(abs(xDiff));
            val v = (yDiff < 0 ? "^" : "v").repeat(abs(yDiff));
            val verticalFirstCorner = new Position(curPos.x(), targetPos.y());
            val horizontalFirstCorner = new Position(targetPos.x(), curPos.y());
            val canGoVerticalFirst = !positionMap.get(' ').equals(verticalFirstCorner);
            val canGoHorizontalFirst = !positionMap.get(' ').equals(horizontalFirstCorner);
            sequence.append(whatToAppend(xDiff, yDiff, h, v, canGoVerticalFirst, canGoHorizontalFirst));
            sequence.append('A');
            curPos = targetPos;
        }
        return sequence.toString();
    }

    String whatToAppend(
            final int xDiff, final int yDiff,
            final String h, final String v,
            final boolean canGoVerticalFirst, final boolean canGoHorizontalFirst
    ) {
        if (xDiff == 0)  return v;
        if (yDiff == 0)  return h;
        if (!canGoVerticalFirst)  return h + v;
        if (!canGoHorizontalFirst)  return v + h;
        if (xDiff < 0 && yDiff < 0)
            return h + v; // prefer <v over v<
        if (xDiff > 0 && yDiff < 0)
            return v + h; // prefer v> over >v
        if (xDiff < 0)
            return h + v; // prefer <^ over ^<
        return v + h;     // prefer ^> over >^
    }

    // TODO: some kind of memoization?
    record MemoKey(Position curPos, Position endPos) {}

    @Override
    protected void part2() {
        System.out.println(read.lines().mapToLong(code -> {
            var sequence = findSequenceToType(code, NUMERIC_KEYPAD_POSITIONS);
            for (var i = 0; i < 2; i++) {
                sequence = findSequenceToType(sequence, DIRECTIONAL_KEYPAD_POSITIONS);
            }
            System.out.printf("%s: %-74s (%d * %d)%n", code, sequence, sequence.length(), parseLong(code.substring(0, 3)));
            return parseLong(code.substring(0, 3)) * sequence.length();
        }).sum());
    }


}
