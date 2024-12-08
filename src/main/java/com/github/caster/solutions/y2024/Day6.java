package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Direction;
import com.github.caster.shared.map.Position;
import com.github.caster.shared.map.ResettableMap;
import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.map.Direction.UP;
import static com.github.caster.shared.map.Direction.isDirectionRepresentation;
import static java.util.stream.Collectors.toSet;

public final class Day6 extends BaseSolution {

    private final ResettableMap map;
    private final Position startPos;
    private final Direction startDir;

    private Direction curDir = UP;
    private Position curPos = null;

    public Day6() {
        read.from(INPUT);
        map = read.map();
        findCurrentGuardPosition();
        startPos = curPos;
        startDir = curDir;
    }

    private void findCurrentGuardPosition() {
        val guardCell = map.stream().filter(cell -> cell.value() == '^').findFirst().orElseThrow();
        curPos = new Position(guardCell.x(), guardCell.y());
    }

    @Override
    protected void part1() {
        leavesMapWhenWalkingAround();

        System.out.println(map.stream().filter(cell -> cell.value() != '.' && cell.value() != '#').count());
    }

    private boolean leavesMapWhenWalkingAround() {
        try {
            do {
                map.set(curPos, curDir.representation);
                while (map.get(curPos.moved(curDir)) == '#') {
                    curDir = curDir.turnRight();
                }
                curPos = curPos.moved(curDir);
            } while (map.get(curPos) != curDir.representation);
            return false;
        } catch (final ArrayIndexOutOfBoundsException e) {
            // apparently we left the map :)
            return true;
        }
    }

    @Override
    protected void part2() {
        // first collect all positions the guard will visit, those can be blocked by us (except starting pos)
        val guardPositions = map.stream()
                .filter(cell -> isDirectionRepresentation(cell.value()))
                .map(cell -> new Position(cell.x(), cell.y()))
                .filter(position -> !position.equals(startPos))
                .collect(toSet());
        resetMap();

        // now, for each guard position place an obstacle and check if he will loop
        System.out.println(
                guardPositions.stream()
                        .filter(position -> {
                            map.set(position, '#');
                            val guardWillLoop = !leavesMapWhenWalkingAround();
                            resetMap();
                            return guardWillLoop;
                        })
                        .count()
        );
    }

    private void resetMap() {
        map.reset();
        curPos = startPos;
        curDir = startDir;
    }

}
