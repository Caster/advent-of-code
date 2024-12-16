package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Direction;
import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.map.ResettableMap.Cell;
import lombok.val;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.map.Direction.RIGHT;
import static com.github.caster.shared.map.ResettableMap.Cell.cellValueIs;
import static java.util.function.Predicate.not;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toSet;

public final class Day16 extends BaseSolution {

    private static final long TURNING_COST = 1000L;

    private final ResettableMap maze;
    private final Set<MazeSearchEntry> bestPaths;

    public Day16() {
        read.from(INPUT);
        maze = read.map();
        bestPaths = new HashSet<>();
    }

    record MazeSearchEntry(
            Cell cell,
            Direction direction,
            long score,
            Set<Cell> path
    ) implements Comparable<MazeSearchEntry> {

        @Override
        public int compareTo(final MazeSearchEntry that) {
            return Long.compare(this.score, that.score);
        }

    }

    @Override
    protected void part1() {
        val startPos = maze.stream().filter(cellValueIs('S')).findFirst().orElseThrow();
        val endPos = maze.stream().filter(cellValueIs('E')).findFirst().orElseThrow();
        findBestPaths(startPos, endPos);

        System.out.println(bestPaths.iterator().next().score);
    }

    void findBestPaths(final Cell source, final Cell sink) {
        val alreadyVisitedWithScore = new HashMap<Cell, Long>();
        val searchEntries = new PriorityQueue<MazeSearchEntry>();
        searchEntries.add(new MazeSearchEntry(source, RIGHT, 0, Set.of(source)));
        var bestPathScore = -1L;

        while (!searchEntries.isEmpty()) {
            val searchEntry = searchEntries.poll();
            val previouslyVisited = alreadyVisitedWithScore.get(searchEntry.cell());
            if (previouslyVisited != null && previouslyVisited < searchEntry.score() - TURNING_COST)  continue;
            if (previouslyVisited == null || previouslyVisited > searchEntry.score()) {
                alreadyVisitedWithScore.put(searchEntry.cell(), searchEntry.score());
            }
            for (val neighbor : searchEntry.cell().neighbors(maze).filter(not(cellValueIs('#'))).toList()) {
                val direction = Direction.between(searchEntry.cell().position(), neighbor.position());
                val turningCost = TURNING_COST * Direction.difference(searchEntry.direction(), direction);
                val neighborEntry = new MazeSearchEntry(
                        neighbor,
                        direction,
                        searchEntry.score() + turningCost + 1L,
                        Stream.of(searchEntry.path().stream(), Stream.of(neighbor)).flatMap(identity()).collect(toSet())
                );
                val neighborVisited = alreadyVisitedWithScore.get(neighbor);
                if (neighborVisited != null && neighborVisited < neighborEntry.score() - TURNING_COST)  continue;

                if (neighbor.equals(sink) && (bestPaths.isEmpty() || bestPathScore == neighborEntry.score())) {
                    bestPaths.add(neighborEntry);
                    bestPathScore = neighborEntry.score();
                } else {
                    searchEntries.add(neighborEntry);
                }
            }
        }
    }

    @Override
    protected void part2() {
        val seatTiles = bestPaths.stream()
                .flatMap(entry -> entry.path().stream())
                .collect(toSet());
        System.out.println(seatTiles.size());
    }

}
