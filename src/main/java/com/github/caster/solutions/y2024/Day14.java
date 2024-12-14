package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.math.Vector;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

import java.util.Map;
import java.util.regex.Pattern;

import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public final class Day14 extends BaseSolution {

    private static final Pattern POS_PATTERN = Pattern.compile("(-?\\d+),(-?\\d+)");

    private final long mapWidth;
    private final long mapHeight;

    public Day14() {
        read.from(INPUT);
        if (read.inputType() == EXAMPLE) {
            mapWidth = 11;
            mapHeight = 7;
        } else {
            mapWidth = 101;
            mapHeight = 103;
        }
    }

    @RequiredArgsConstructor
    @ToString
    private final class Robot {

        private final Vector position;
        private final Vector velocity;

        Robot(final String input) {
            val matcher = POS_PATTERN.matcher(input);
            if (!matcher.find())  throw new IllegalArgumentException("no position found");
            val position = new Vector(parseLong(matcher.group(1)), parseLong(matcher.group(2)));
            if (!matcher.find())  throw new IllegalArgumentException("no velocity found");
            val velocity = new Vector(parseLong(matcher.group(1)), parseLong(matcher.group(2)));
            this(position, velocity);
        }

        Robot moved(final long numSteps) {
            return new Robot(
                    new Vector(
                            ((position.get(0) + velocity.get(0) * numSteps) % mapWidth + mapWidth) % mapWidth,
                            ((position.get(1) + velocity.get(1) * numSteps) % mapHeight + mapHeight) % mapHeight
                    ),
                    velocity
            );
        }

        int quadrant() {
            val x = position.get(0);
            val y = position.get(1);
            val midX = mapWidth / 2;
            val midY = mapHeight / 2;
            if (x == midX || y == midY)  return -1;
            return (y < midY ? 0 : 2) + (x < midX ? 0 : 1);
        }

    }

    @Override
    protected void part1() {
        val safetyFactor = read.lines()
                .map(Robot::new)
                .map(robot -> robot.moved(100))
                .collect(groupingBy(Robot::quadrant, counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() > -1)
                .mapToLong(Map.Entry::getValue)
                .reduce(1, (a, b) -> a * b);
        System.out.println(safetyFactor);
    }

}
