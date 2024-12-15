package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.math.Vector;
import lombok.val;

import java.util.List;
import java.util.Map;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.map.ResettableMap.Cell.cellValueIs;
import static java.util.stream.Collectors.*;

public final class Day8 extends BaseSolution {

    private final ResettableMap map;
    private final Map<Character, List<Vector>> antennaPositions;

    public Day8() {
        read.from(INPUT);
        map = read.map();
        antennaPositions = map.stream()
                .filter(cell -> cell.value() != '.')
                .collect(groupingBy(
                        ResettableMap.Cell::value,
                        collectingAndThen(toSet(),
                                cells -> cells.stream().map(cell -> new Vector(cell.x(), cell.y())).toList()
                        )
                ));
    }

    @Override
    protected void part1() {
        for (val positions : antennaPositions.values()) {
            for (val posA : positions) {
                for (val posB : positions) {
                    if (posA.equals(posB))  continue;

                    val diff = posB.minus(posA);
                    for (val possibleAntiNodePos : List.of(posA.minus(diff), posB.plus(diff))) {
                        if (map.contains(possibleAntiNodePos)) {
                            map.set(possibleAntiNodePos, '#');
                        }
                    }
                }
            }
        }

        System.out.println(map.stream().filter(cellValueIs('#')).count());
    }

    @Override
    protected void part2() {
        for (val positions : antennaPositions.values()) {
            for (val posA : positions) {
                for (val posB : positions) {
                    if (posA.equals(posB))  continue;

                    map.set(posA, '#');
                    map.set(posB, '#');
                    val diff = posB.minus(posA);
                    var checkingPos = posA.minus(diff);
                    while (map.contains(checkingPos)) {
                        map.set(checkingPos, '#');
                        checkingPos = checkingPos.minus(diff);
                    }

                    checkingPos = posB.plus(diff);
                    while (map.contains(checkingPos)) {
                        map.set(checkingPos, '#');
                        checkingPos = checkingPos.plus(diff);
                    }
                }
            }
        }

        System.out.println(map.stream().filter(cellValueIs('#')).count());
    }

}
