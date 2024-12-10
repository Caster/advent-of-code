package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.ResettableMap;

import java.util.Set;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.util.stream.Collectors.toSet;

public final class Day10 extends BaseSolution {

    private final ResettableMap heights;

    public Day10() {
        read.from(INPUT);
        heights = read.map();
    }

    @Override
    protected void part1() {
        System.out.println(heights.stream()
                .filter(cell -> cell.value() == '0')
                .map(this::reachableTrailEnds)
                .mapToLong(Set::size)
                .sum());
    }

    private Set<ResettableMap.Cell> reachableTrailEnds(final ResettableMap.Cell trailHead) {
        if (trailHead.value() == '9') {
            return Set.of(trailHead);
        }
        return trailHead.neighbors(heights)
                .filter(cell -> cell.value() == (trailHead.value() + 1))
                .map(this::reachableTrailEnds)
                .flatMap(Set::stream)
                .collect(toSet());
    }

    @Override
    protected void part2() {
        System.out.println(heights.stream()
                .filter(cell -> cell.value() == '0')
                .mapToLong(this::trailHeadRating)
                .sum());
    }

    private long trailHeadRating(final ResettableMap.Cell trailHead) {
        if (trailHead.value() == '9') {
            return 1;
        }
        return trailHead.neighbors(heights)
                .filter(cell -> cell.value() == (trailHead.value() + 1))
                .mapToLong(this::trailHeadRating)
                .sum();
    }

}
