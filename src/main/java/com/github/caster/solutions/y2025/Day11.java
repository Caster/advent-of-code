package com.github.caster.solutions.y2025;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.github.caster.shared.BaseSolution2;
import com.github.caster.shared.Expectations;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.val;

import static com.github.caster.shared.Expectations.expect;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE2;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.util.Arrays.stream;

public final class Day11 extends BaseSolution2 {

    public static Expectations expectations() {
        return expect(EXAMPLE).toSolveTo(5).andPart(2).toSolveTo(0)
                .alsoExpect(EXAMPLE2).toSolveTo(0).andPart(2).toSolveTo(2)
                .alsoExpect(INPUT).toSolveTo(543).andPart(2).toSolveTo(479511112939968L);
    }

    private final Graph<IgnorableVertex, ReadableEdge> serverRack;
    private final Graph<IgnorableVertex, ReadableEdge> serverRackReversed;

    private static final class ReadableEdge extends DefaultEdge {

        @Override
        public IgnorableVertex getSource() {
            return (IgnorableVertex) super.getSource();
        }

        @Override
        public IgnorableVertex getTarget() {
            return (IgnorableVertex) super.getTarget();
        }

    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static final class IgnorableVertex {

        private final String value;

        @EqualsAndHashCode.Exclude
        private boolean ignore = false;

        void ignore() {
            ignore = true;
        }

        boolean isIgnored() {
            return ignore;
        }

    }

    public Day11() {
        serverRack = new SimpleDirectedGraph<>(ReadableEdge.class);
        serverRack.addVertex(new IgnorableVertex("out"));
        read.lines().map(line -> line.split(":")[0])
                .map(IgnorableVertex::new)
                .forEach(serverRack::addVertex);
        read.lines().forEach(line -> {
            val source = new IgnorableVertex(line.split(":")[0]);
            val targets = stream(line.split(": ")[1].split(" "))
                    .map(IgnorableVertex::new).toList();
            targets.forEach(target -> serverRack.addEdge(source, target));
        });

        serverRackReversed = new EdgeReversedGraph<>(serverRack);
    }

    @Override
    protected long part1() {
        if (read.inputType() == EXAMPLE2)  return 0;
        return new AllDirectedPaths<>(serverRack)
                .getAllPaths(new IgnorableVertex("you"), new IgnorableVertex("out"), true, null)
                .size();
    }

    @Override
    protected long part2() {
        if (read.inputType() == EXAMPLE)  return 0;

        // Found in a visualization of the graph that all paths _first_ visit `fft`, _then_ `dac`.
        // We therefore only consider that order - the performance is horrible when trying to
        // consider the `svr-dac-fft-out` order too.
        val svr2fft = getNumberOfPathsFromTo(new IgnorableVertex("svr"), new IgnorableVertex("fft"));
        val fft2dac = getNumberOfPathsFromTo(new IgnorableVertex("fft"), new IgnorableVertex("dac"));
        val dac2out = getNumberOfPathsFromTo(new IgnorableVertex("dac"), new IgnorableVertex("out"));

        return svr2fft * fft2dac * dac2out;
    }

    private long getNumberOfPathsFromTo(final IgnorableVertex from, final IgnorableVertex to) {
        val paths = new AllDirectedPaths<>(serverRackReversed,
                (_, edge) -> !edge.getTarget().isIgnored())
                .getAllPaths(to, from, true, null);
        paths.stream().map(GraphPath::getVertexList).flatMap(List::stream)
                .distinct().forEach(IgnorableVertex::ignore);
        return paths.size();
    }

}
