package com.github.caster.solutions.y2025;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.github.caster.shared.BaseSolution2;
import com.github.caster.shared.math.Vector;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.lang.Math.multiplyExact;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.IntStream.range;

public final class Day8 extends BaseSolution2 {

    private final List<Vector> junctionBoxPositions;
    private final List<JunctionBoxesDistance> sqDistances;
    private final int[] boxIndexToCircuitId;
    private final List<Set<Integer>> circuits;

    private int sqDistanceIndex;
    private int numCircuits;

    public Day8() {
        junctionBoxPositions = read.lines()
                .map(line -> new Vector(parseLongs(line, ",")))
                .toList();
        val numJunctionBoxes = junctionBoxPositions.size();
        sqDistances = range(0, numJunctionBoxes).boxed()
                .<JunctionBoxesDistance>mapMulti((i, downstream) ->
                        range(i + 1, numJunctionBoxes).boxed()
                                .map(j -> JunctionBoxesDistance.of(i, j, junctionBoxPositions))
                                .forEach(downstream))
                .sorted().toList();
        boxIndexToCircuitId = range(0, numJunctionBoxes).toArray();
        circuits = range(0, numJunctionBoxes)
                .mapToObj(p -> new TreeSet<>(Set.of(p)))
                .collect(toCollection(ArrayList::new));
        sqDistanceIndex = 0;
        numCircuits = numJunctionBoxes;
    }

    private record JunctionBoxesDistance(int indexI, int indexJ, long sqDistance)
            implements Comparable<JunctionBoxesDistance> {

        static JunctionBoxesDistance of(final int i, final int j, final List<Vector> points) {
            return new JunctionBoxesDistance(i, j,
                    points.get(i).squaredEuclideanDistanceTo(points.get(j)));
        }

        @Override
        public int compareTo(final JunctionBoxesDistance that) {
            return Long.compare(this.sqDistance, that.sqDistance);
        }
    }

    @Override
    protected long part1() {
        range(0, read.inputType() == INPUT ? 1_000 : 10)
                .forEach(_ -> this.connectClosestJunctionBoxes());

        return circuits.stream().filter(Objects::nonNull)
                .map(Set::size)
                .sorted(reverseOrder())
                .limit(3)
                .mapToLong(Long::valueOf)
                .reduce(1, Math::multiplyExact);
    }

    private void connectClosestJunctionBoxes() {
        val closestBoxesDistance = sqDistances.get(sqDistanceIndex++);
        val i = closestBoxesDistance.indexI;
        val j = closestBoxesDistance.indexJ;
        val circuitIdI = boxIndexToCircuitId[i];
        val circuitIdJ = boxIndexToCircuitId[j];
        if (circuitIdI == circuitIdJ)  return;

        val circuitI = circuits.get(circuitIdI);
        val circuitJ = circuits.get(circuitIdJ);
        circuitI.addAll(circuitJ);
        circuitJ.forEach(jNeighbor -> boxIndexToCircuitId[jNeighbor] = circuitIdI);
        circuits.set(circuitIdJ, null);
        numCircuits--;
    }

    @Override
    protected long part2() {
        while (numCircuits > 1) {
            connectClosestJunctionBoxes();
        }

        val lastConnection = sqDistances.get(sqDistanceIndex - 1);
        return multiplyExact(
                junctionBoxPositions.get(lastConnection.indexI).get(0),
                junctionBoxPositions.get(lastConnection.indexJ).get(0)
        );
    }
}
