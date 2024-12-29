package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.*;
import java.util.function.Consumer;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public final class Day23 extends BaseSolution {

    private final Map<String, Set<String>> nodeNeighbors;

    public Day23() {
        read.from(INPUT);
        nodeNeighbors = new HashMap<>();
        read.lines().forEach(line -> {
            val edge = line.split("-");
            nodeNeighbors.computeIfAbsent(edge[0], _ -> new HashSet<>()).add(edge[1]);
            nodeNeighbors.computeIfAbsent(edge[1], _ -> new HashSet<>()).add(edge[0]);
        });
    }

    @Override
    protected void part1() {
        val cliques = new ArrayList<String>();
        val nodes = nodeNeighbors.keySet().stream().sorted().toList();
        val numNodes = nodes.size();
        for (int i = 0; i < numNodes - 2; i++) {
            val node1 = nodes.get(i);

            for (int j = i + 1; j < numNodes - 1; j++) {
                val node2 = nodes.get(j);
                if (!nodeNeighbors.get(node1).contains(node2))  continue;

                for (int k = j + 1; k < numNodes; k++) {
                    val node3 = nodes.get(k);
                    if (node1.charAt(0) != 't' && node2.charAt(0) != 't' && node3.charAt(0) != 't')  continue;
                    if (!nodeNeighbors.get(node1).contains(node3))  continue;
                    if (!nodeNeighbors.get(node2).contains(node3))  continue;
                    cliques.add("%s,%s,%s".formatted(node1, node2, node3));
                }
            }
        }
        System.out.println(cliques.size());
    }

    @Override
    protected void part2() {
        val cliques = new ArrayList<String>();
        runBronKerbosch2(new HashSet<>(), new HashSet<>(nodeNeighbors.keySet()), new HashSet<>(), cliques::add);
        System.out.println(cliques.stream().max(comparing(String::length)).orElseThrow());
    }

    // https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
    void runBronKerbosch2(
            final Set<String> cliqueSoFar, // R on Wikipedia
            final Set<String> toConsider,  // P on Wikipedia
            final Set<String> toExclude,   // X on Wikipedia
            final Consumer<String> downstream
    ) {
        if (toConsider.isEmpty() && toExclude.isEmpty()) {
            downstream.accept(cliqueSoFar.stream().sorted().collect(joining(",")));
            return;
        }

        val pivot = concat(toConsider.stream(), toExclude.stream()).findAny().orElseThrow();
        val pivotNeighbors = nodeNeighbors.get(pivot);
        val toConsiderIterator = toConsider.iterator();
        while (toConsiderIterator.hasNext()) {
            val vertex = toConsiderIterator.next();
            if (pivotNeighbors.contains(vertex))  continue;
            val vertexNeighbors = nodeNeighbors.get(vertex);
            runBronKerbosch2(
                    concat(cliqueSoFar.stream(), of(vertex)).collect(toSet()),
                    toConsider.stream().filter(vertexNeighbors::contains).collect(toSet()),
                    toExclude.stream().filter(vertexNeighbors::contains).collect(toSet()),
                    downstream
            );
            toConsiderIterator.remove();
            toExclude.add(vertex);
        }
    }

}
