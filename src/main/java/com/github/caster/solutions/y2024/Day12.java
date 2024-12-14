package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Direction;
import com.github.caster.shared.map.Position;
import com.github.caster.shared.map.ResettableMap;
import lombok.val;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

public final class Day12 extends BaseSolution {

    private final ResettableMap map;
    private final Set<Region> regions;

    public Day12() {
        read.from(INPUT);
        map = read.map();

        val regionMap = new HashMap<Character, Set<Region>>();
        val processedPlots = new HashSet<ResettableMap.Cell>();
        map.stream().forEach(plot -> findAndAddRegion(plot, regionMap, processedPlots));
        regions = regionMap.values().stream().flatMap(Set::stream).collect(toSet());
    }

    private final class Region {

        private final char plant;
        private final Set<ResettableMap.Cell> plots;

        private Region(final char plant) {
            this.plant = plant;
            this.plots = new HashSet<>();
        }

        private void add(final ResettableMap.Cell plot) {
            this.plots.add(plot);
        }

        private long area() {
            return plots.size();
        }

        private long perimeter() {
            return plots.stream()
                    .mapToLong(plot -> 4 - plot.neighbors(map).filter(plots::contains).count())
                    .sum();
        }

        private long sides() {
            if (plots.size() == 1) {
                return 4;
            }

            // start at a corner
            val possibleStartPlots = plots.stream()
                    .filter(plot -> {
                        val neighbors = plot.neighbors(map).filter(plots::contains).collect(toSet());
                        if (neighbors.size() >= 3)  return false;
                        if (neighbors.size() == 1)  return true;
                        val nIt = neighbors.iterator();
                        val n1 = nIt.next();
                        val n2 = nIt.next();
                        return n1.x() != n2.x() && n1.y() != n2.y();
                    })
                    .toList();
            val visitedPlots = new HashSet<Position>();
            return possibleStartPlots.stream().mapToLong(startPlot -> {
                var currPos = new Position(startPlot.x(), startPlot.y());
                if (!visitedPlots.add(currPos))  return 0L;
                val startPos = currPos;

                // walk in a direction along the perimeter
                Direction currDir = null;
                for (val direction : Direction.values()) {
                    val movedPos = currPos.moved(direction);
                    if (!map.contains(movedPos)) continue;
                    if (plant != map.get(movedPos)) continue;

                    val movedCcwPos = currPos.moved(direction.turnCounterclockwise());
                    if (map.contains(movedCcwPos) && map.get(movedCcwPos) == plant) continue;

                    currDir = direction;
                    break;
                }
                if (currDir == null) throw new IllegalStateException("Could not find start direction");
                val startDir = currDir;

                var numSides = 0L;
                currPos = currPos.moved(currDir);
                do {
                    visitedPlots.add(currPos);

                    // can move CCW? then we ended a side, move there
                    val ccwPos = currPos.moved(currDir.turnCounterclockwise());
                    if (map.contains(ccwPos) && map.get(ccwPos) == plant) {
                        numSides++;
                        currDir = currDir.turnCounterclockwise();
                        currPos = ccwPos;
                        continue;
                    }

                    // can move forward? then keep doing so
                    val fwPos = currPos.moved(currDir);
                    if (map.contains(fwPos) && map.get(fwPos) == plant) {
                        currPos = fwPos;
                        continue;
                    }

                    // we found a side, turn CW
                    numSides++;
                    currDir = currDir.turnClockwise();
                } while (!currPos.equals(startPos) || currDir != startDir);

                return numSides;
            }).sum();
        }

    }

    private void findAndAddRegion(
            final ResettableMap.Cell plot,
            final Map<Character, Set<Region>> regions,
            final Set<ResettableMap.Cell> processedPlots
    ) {
        if (processedPlots.contains(plot))  return;

        val newRegion = new Region(plot.value());
        findAndAddRegion(plot, newRegion, processedPlots);
        regions.computeIfAbsent(plot.value(), _ -> new HashSet<>())
                .add(newRegion);
    }

    private void findAndAddRegion(
            final ResettableMap.Cell plot,
            final Region region,
            final Set<ResettableMap.Cell> processedPlots
    ) {
        region.add(plot);
        processedPlots.add(plot);

        plot.neighbors(map)
                .filter(neighbor -> neighbor.value() == plot.value())
                .filter(not(processedPlots::contains))
                .forEach(neighbor -> findAndAddRegion(neighbor, region, processedPlots));
    }

    @Override
    protected void part1() {
        System.out.println(regions.stream().mapToLong(region -> region.area() * region.perimeter()).sum());
    }

    @Override
    protected void part2() {
        System.out.println(regions.stream()
                .mapToLong(region -> region.area() * region.sides())
                .sum());
    }

}
