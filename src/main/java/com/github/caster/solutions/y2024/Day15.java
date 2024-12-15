package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Direction;
import com.github.caster.shared.map.Position;
import com.github.caster.shared.map.ResettableMap;
import lombok.val;

import java.util.List;
import java.util.stream.Stream;

import static com.github.caster.shared.StreamUtils.stream;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.map.Direction.LEFT;
import static com.github.caster.shared.map.Direction.RIGHT;
import static com.github.caster.shared.map.ResettableMap.Cell.cellValueIs;

public final class Day15 extends BaseSolution {

    private final ResettableMap map;
    private final ResettableMap wideMap;
    private final List<Direction> moveSequence;

    public Day15() {
        read.from(INPUT);

        final Object[] results = new Object[3];
        read.with(sections -> {
            val mapInput = sections.next().lines().toList();
            results[0] = new ResettableMap(mapInput.stream().map(String::toCharArray).toArray(char[][]::new));
            results[1] = new ResettableMap(
                    mapInput.stream()
                            .map(line -> line
                                    .replace("#", "##")
                                    .replace("O", "[]")
                                    .replace(".", "..")
                                    .replace("@", "@.")
                            )
                            .map(String::toCharArray)
                            .toArray(char[][]::new)
            );
            results[2] = sections.next().string().replace("\n", "");
        });
        map = (ResettableMap) results[0];
        wideMap = (ResettableMap) results[1];
        moveSequence = stream(((String) results[2]).toCharArray()).map(Direction::valueOf).toList();
    }

    @Override
    protected void part1() {
        findRobotAndPerformMoveSequenceOn(map);
        printSumOfGpsCoordinatesOf(map.stream().filter(cellValueIs('O')));
    }

    void findRobotAndPerformMoveSequenceOn(final ResettableMap map) {
        val robotCell = map.stream().filter(cellValueIs('@')).findFirst().orElseThrow();
        var robotPos = robotCell.position();
        for (val direction : moveSequence) {
            if (canMove(map, robotPos, direction)) {
                robotPos = doMove(map, robotPos, direction);
            }
        }
    }

    boolean canMove(final ResettableMap map, final Position fromPos, final Direction direction) {
        val toPos = fromPos.moved(direction);
        val toCell = map.getCell(toPos);
        return switch (toCell.value()) {
            case 'O' -> canMove(map, toPos, direction);
            case '[' -> canMove(map, toPos, direction)
                    && (direction == RIGHT || direction == LEFT || canMove(map, toPos.moved(RIGHT), direction));
            case ']' -> canMove(map, toPos, direction)
                    && (direction == LEFT || direction == RIGHT || canMove(map, toPos.moved(LEFT), direction));
            default -> toCell.value() == '.';
        };
    }

    Position doMove(final ResettableMap map, final Position fromPos, final Direction direction) {
        val toPos = fromPos.moved(direction);
        val toCell = map.getCell(toPos);
        switch (toCell.value()) {
            case 'O' -> doMove(map, toPos, direction);
            case '[' -> {
                doMove(map, toPos, direction);
                if (direction != RIGHT && direction != LEFT) {
                    doMove(map, toPos.moved(RIGHT), direction);
                }
            }
            case ']' -> {
                doMove(map, toPos, direction);
                if (direction != LEFT && direction != RIGHT) {
                    doMove(map, toPos.moved(LEFT), direction);
                }
            }
        }
        map.set(toPos, map.get(fromPos));
        map.set(fromPos, '.');
        return toPos;
    }

    void printSumOfGpsCoordinatesOf(final Stream<ResettableMap.Cell> cellStream) {
        System.out.println(cellStream.mapToLong(cell -> 100L * cell.y() + cell.x()).sum());
    }

    @Override
    protected void part2() {
        findRobotAndPerformMoveSequenceOn(wideMap);
        printSumOfGpsCoordinatesOf(wideMap.stream().filter(cellValueIs('[')));
    }
}
