package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Position;
import com.github.caster.shared.map.ResettableMap;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.map.ResettableMap.Cell.cellValueIs;
import static java.util.function.Predicate.not;
import static java.util.stream.IntStream.range;

public final class Day20 extends BaseSolution {

    private final ResettableMap map;
    private final PathFromSource raceTrack;

    public Day20() {
        read.from(INPUT);

        map = read.map();
        val sourcePosition = map.stream().filter(cellValueIs('S')).findFirst().orElseThrow().position();
        val sinkPosition = map.stream().filter(cellValueIs('E')).findFirst().orElseThrow().position();
        raceTrack = findPath(sourcePosition, sinkPosition);
    }

    private final class PathFromSource {

        private final List<Position> positions;

        PathFromSource(final Position sourcePosition) {
            positions = new ArrayList<>();
            positions.add(sourcePosition);
        }

        ResettableMap.Cell head() {
            return map.getCell(positions.getLast());
        }

        Position get(final int index) {
            return positions.get(index);
        }

        int size() {
            return positions.size();
        }

        boolean isNeck(final ResettableMap.Cell cell) {
            return positions.size() >= 2 && positions.get(positions.size() - 2).equals(cell.position());
        }

    }

    PathFromSource findPath(final Position sourcePosition, final Position sinkPosition) {
        val path = new PathFromSource(sourcePosition);
        while (!path.head().position().equals(sinkPosition)) {
            val nextNeighbor = path.head().neighbors(map)
                    .filter(not(cellValueIs('#')))
                    .filter(not(path::isNeck))
                    .findFirst()
                    .orElseThrow();
            path.positions.add(nextNeighbor.position());
        }
        return path;
    }

    @Override
    protected void part1() {
        System.out.println(findNumberOfShortcutSavingsAtLeast100(2));
    }

    long findNumberOfShortcutSavingsAtLeast100(final int maxCheatDuration) {
        return range(0, raceTrack.size()).flatMap(startPositionIndex ->
                        range(startPositionIndex + 3, raceTrack.size()).mapMulti((endPositionIndex, downstream) -> {
                            val startPosition = raceTrack.get(startPositionIndex);
                            val endPosition = raceTrack.get(endPositionIndex);
                            val distance = (int) startPosition.manhattanDistanceTo(endPosition);
                            if (distance <= maxCheatDuration && distance < endPositionIndex - startPositionIndex) {
                                downstream.accept(endPositionIndex - startPositionIndex - distance);
                            }
                        }))
                .filter(saving -> saving >= 100)
                .count();
    }

    @Override
    protected void part2() {
        System.out.println(findNumberOfShortcutSavingsAtLeast100(20));
    }

}
