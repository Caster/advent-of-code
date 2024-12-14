package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
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

        private long corners() {
            return plots.stream()
                    .mapToLong(plot -> plot.corners(map))
                    .sum();
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
                .mapToLong(region -> region.area() * region.corners())
                .sum());
    }

}
