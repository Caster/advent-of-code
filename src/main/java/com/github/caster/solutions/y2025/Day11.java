package com.github.caster.solutions.y2025;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.github.caster.shared.BaseSolution2;
import com.github.caster.shared.Expectations;

import lombok.val;

import static com.github.caster.shared.Expectations.expect;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE2;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseColumns;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public final class Day11 extends BaseSolution2 {

    public static Expectations expectations() {
        return expect(EXAMPLE).toSolveTo(5).andPart(2).toSolveTo(0)
                .alsoExpect(EXAMPLE2).toSolveTo(0).andPart(2).toSolveTo(2)
                .alsoExpect(INPUT).toSolveTo(543).andPart(2).toSolveTo(479511112939968L);
    }

    private final Graph<String, ReadableEdge> serverRack;
    private final Graph<String, ReadableEdge> serverRackReversed;

    private static final class ReadableEdge extends DefaultEdge {

        @Override
        public String getSource() {
            return super.getSource().toString();
        }

        @Override
        public String getTarget() {
            return super.getTarget().toString();
        }

    }

    public Day11() {
        serverRack = new SimpleDirectedGraph<>(ReadableEdge.class);
        serverRack.addVertex("out");
        read.lines().map(line -> line.split(":")[0]).forEach(serverRack::addVertex);
        read.lines().forEach(line -> {
            val source = line.split(":")[0];
            val targets = parseColumns(line.split(": ")[1]);
            stream(targets).forEach(target -> serverRack.addEdge(source, target));
        });

        serverRackReversed = new EdgeReversedGraph<>(serverRack);
    }

    @Override
    protected long part1() {
        if (read.inputType() == EXAMPLE2)  return 0;
        return new AllDirectedPaths<>(serverRack).getAllPaths("you", "out", true, null).size();
    }

    @Override
    protected long part2() {
        if (read.inputType() == EXAMPLE)  return 0;

        // Found in a visualization of the graph that all paths _first_ visit `fft`, _then_ `dac`.
        // We therefore only consider that order - the performance is horrible when trying to
        // consider the `svr-dac-fft-out` order too.
        val verticesToIgnore = new TreeSet<String>();
        val svr2fft = getNumberOfPathsFromTo("svr", "fft", verticesToIgnore);
        val fft2dac = getNumberOfPathsFromTo("fft", "dac", verticesToIgnore);
        val dac2out = getNumberOfPathsFromTo("dac", "out", verticesToIgnore);

        return svr2fft * fft2dac * dac2out;
    }

    private long getNumberOfPathsFromTo(
            final String from,
            final String to,
            final Set<String> verticesToIgnore
    ) {
        val paths = new AllDirectedPaths<>(serverRackReversed,
                (_, edge) -> !verticesToIgnore.contains(edge.getTarget()))
                .getAllPaths(to, from, true, null);
        verticesToIgnore.addAll(paths.stream().map(GraphPath::getVertexList)
                .flatMap(List::stream).collect(toSet()));
        return paths.size();
    }

}
