package com.github.caster.solutions.y2024;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Position;
import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.math.Vector;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static com.github.caster.shared.map.ResettableMap.Cell.cellValueIs;
import static java.util.function.Predicate.not;

public final class Day18 extends BaseSolution {

    private final ResettableMap map;
    private final Map<Position, ParentEntry> parent;
    private final int initiallySimulatedBytes;
    private final Position sourcePosition;
    private final Position sinkPosition;

    public Day18() {
        read.from(INPUT);
        val size = read.inputType() == EXAMPLE ? 7 : 71;
        map = ResettableMap.empty(size, size);
        parent = new HashMap<>();
        initiallySimulatedBytes = read.inputType() == EXAMPLE ? 12 : 1024;
        sourcePosition = new Position(0, 0);
        sinkPosition = new Position(size - 1, size - 1);
        resetParentMap();
    }

    private void resetParentMap() {
        parent.clear();
        parent.put(sourcePosition, new ParentEntry(null, 0));
    }

    record ParentEntry(Position parentPosition, long distanceToSource) {}

    @Override
    protected void part1() {
        read.lines().limit(initiallySimulatedBytes).forEach(line ->
                map.set(new Vector(parseLongs(line, ",")), '#'));

        bfs(map.getCell(sourcePosition));
        System.out.println(parent.get(sinkPosition).distanceToSource());
    }

    void bfs(final ResettableMap.Cell source) {
        val searchQueue = new ArrayDeque<ResettableMap.Cell>();
        searchQueue.push(source);

        while (!searchQueue.isEmpty()) {
            val currentCell = searchQueue.pop();
            val currentPos = currentCell.position();
            val currentDistance = parent.get(currentPos).distanceToSource();
            currentCell.neighbors(map)
                    .filter(not(cellValueIs('#')))
                    .forEach(neighbor -> {
                        val knownParent = parent.get(neighbor.position());
                        if (knownParent == null || knownParent.distanceToSource() > currentDistance + 1) {
                            parent.put(neighbor.position(), new ParentEntry(currentPos, currentDistance + 1));
                            searchQueue.push(neighbor);
                        }
                    });
        }
    }

    @Override
    protected void part2() {
        val bytesToDrop = read.lines().skip(initiallySimulatedBytes).toList();
        for (val byteToDrop : bytesToDrop) {
            map.set(new Vector(parseLongs(byteToDrop, ",")), '#');

            if (getPathFromSourceToSink().stream()
                    .map(map::getCell)
                    .filter(cellValueIs('#'))
                    .findFirst()
                    .isEmpty()) {
                continue; // skip to next byte, path is intact
            }

            resetParentMap();
            bfs(map.getCell(sourcePosition));
            if (!parent.containsKey(sinkPosition)) {
                System.out.println(byteToDrop);
                break;
            }
        }
    }

    List<Position> getPathFromSourceToSink() {
        val path = new ArrayList<Position>();
        path.add(sinkPosition);
        var currentPosition = sinkPosition;
        do {
            currentPosition = parent.get(currentPosition).parentPosition();
            path.add(currentPosition);
        } while (!currentPosition.equals(sourcePosition));
        return path.reversed();
    }

}
