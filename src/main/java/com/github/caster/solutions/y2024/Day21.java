package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Position;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

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

    record ButtonChange(char from, char to) {}

    private static final Map<ButtonChange, String> NUMERIC_MOVES = findAllMovesOnKeyPad(NUMERIC_KEYPAD_POSITIONS);
    private static final Map<ButtonChange, String> DIRECTIONAL_MOVES = findAllMovesOnKeyPad(DIRECTIONAL_KEYPAD_POSITIONS);

    static Map<ButtonChange, String> findAllMovesOnKeyPad(final Map<Character, Position> keyPad) {
        return keyPad.keySet().stream()
                .filter(key -> key != ' ')
                .flatMap(from -> keyPad.keySet().stream()
                        .filter(key -> key != ' ' && key != from)
                        .map(to -> new ButtonChange(from, to)))
                .collect(toMap(identity(), buttonChange -> findSequenceToType(buttonChange, keyPad) + 'A'));
    }

    static String findSequenceToType(final ButtonChange buttonChange, final Map<Character, Position> keyPad) {
        val curPos = keyPad.get(buttonChange.from());
        val targetPos = keyPad.get(buttonChange.to());
        val xDiff = targetPos.x() - curPos.x();
        val h = (xDiff < 0 ? "<" : ">").repeat(abs(xDiff));
        val yDiff = targetPos.y() - curPos.y();
        val v = (yDiff < 0 ? "^" : "v").repeat(abs(yDiff));
        if (xDiff == 0)  return v;
        if (yDiff == 0)  return h;
        val verticalFirstCorner = new Position(curPos.x(), targetPos.y());
        val horizontalFirstCorner = new Position(targetPos.x(), curPos.y());
        val canGoVerticalFirst = !keyPad.get(' ').equals(verticalFirstCorner);
        val canGoHorizontalFirst = !keyPad.get(' ').equals(horizontalFirstCorner);
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

    record MemoKey(String sequence, int numDirectionalRobots) {}

    private static final Map<MemoKey, Long> DIRECTIONAL_SEQUENCE_CACHE = new HashMap<>();

    @Override
    protected void part1() {
        System.out.println(
                read.lines()
                        .mapToLong(code -> determineLineComplexity(code, 2))
                        .sum()
        );
    }

    long determineLineComplexity(final String code, final int numDirectionalRobots) {
        val sequence = findSequenceToType(code, NUMERIC_MOVES);
        return parseLong(code.substring(0, 3)) * findSequenceLengthToTypeDirectional(sequence, numDirectionalRobots);
    }

    String findSequenceToType(final String code, final Map<ButtonChange, String> keyPadMoves) {
        val sequence = new StringBuilder();
        var fromButton = 'A';
        for (var toButton : code.toCharArray()) {
            // the moves maps do not store sequences to stay on the same button, so default to 'A'ctivating
            sequence.append(keyPadMoves.getOrDefault(new ButtonChange(fromButton, toButton), "A"));
            fromButton = toButton;
        }
        return sequence.toString();
    }

    long findSequenceLengthToTypeDirectional(final String sequence, final int numDirectionalRobots) {
        if (numDirectionalRobots == 0)  return sequence.length();
        val memoKey = new MemoKey(sequence, numDirectionalRobots);
        val cachedSequenceLength = DIRECTIONAL_SEQUENCE_CACHE.get(memoKey);
        if (cachedSequenceLength != null)  return cachedSequenceLength;
        val sequenceLength = stream(sequence.split("(?<=A)"))
                .map(move -> findSequenceToType(move, DIRECTIONAL_MOVES))
                .mapToLong(moveSequence -> findSequenceLengthToTypeDirectional(moveSequence, numDirectionalRobots - 1))
                .sum();
        DIRECTIONAL_SEQUENCE_CACHE.put(memoKey, sequenceLength);
        return sequenceLength;
    }

    @Override
    protected void part2() {
        System.out.println(
                read.lines()
                        .mapToLong(code -> determineLineComplexity(code, 25))
                        .sum()
        );
    }

}
