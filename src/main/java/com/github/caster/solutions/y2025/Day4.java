package com.github.caster.solutions.y2025;

import java.util.List;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.map.ResettableMap.Cell;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.map.ResettableMap.Cell.cellValueIs;
import static java.util.function.Predicate.not;
import static java.util.stream.Stream.generate;

public final class Day4 extends BaseSolution {

    private final ResettableMap paperRollMap;

    public Day4() {
        read.from(INPUT);
        paperRollMap = read.map();
    }

    @Override
    protected void part1() {
        IO.println(accessiblePaperRolls().size());
    }

    private List<Cell> accessiblePaperRolls() {
        return paperRollMap.stream()
                .filter(cellValueIs('@')) // find paper rolls...
                .filter(cell -> cell.octoNeighbors(paperRollMap)
                        .filter(not(cell::equals))
                        .filter(cellValueIs('@'))
                        .count() < 4) // ...with at most 3 neighboring rolls
                .toList();
    }

    @Override
    protected void part2() {
        IO.println(
                generate(this::accessiblePaperRolls)
                        .mapToLong(removableRolls -> {
                            removableRolls.forEach(roll -> paperRollMap.set(roll.position(), '.'));
                            return removableRolls.size();
                        })
                        .takeWhile(removedRolls -> removedRolls > 0)
                        .sum()
        );
    }

}
