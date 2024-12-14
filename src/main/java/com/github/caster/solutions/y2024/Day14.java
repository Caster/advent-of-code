package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.math.Vector;
import lombok.ToString;
import lombok.val;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public final class Day14 extends BaseSolution {

    private static final Pattern POS_PATTERN = Pattern.compile("(-?\\d+),(-?\\d+)");

    private final int mapWidth;
    private final int mapHeight;

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

    @ToString
    private final class Robot {

        private final Vector velocity;

        private Vector position;

        Robot(final String input) {
            val matcher = POS_PATTERN.matcher(input);
            if (!matcher.find())  throw new IllegalArgumentException("no position found");
            this.position = new Vector(parseLong(matcher.group(1)), parseLong(matcher.group(2)));
            if (!matcher.find())  throw new IllegalArgumentException("no velocity found");
            this.velocity = new Vector(parseLong(matcher.group(1)), parseLong(matcher.group(2)));
        }

        void move(final long numSteps) {
            position = new Vector(
                    ((position.get(0) + velocity.get(0) * numSteps) % mapWidth + mapWidth) % mapWidth,
                    ((position.get(1) + velocity.get(1) * numSteps) % mapHeight + mapHeight) % mapHeight
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
                .peek(robot -> robot.move(100))
                .collect(groupingBy(Robot::quadrant, counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() > -1)
                .mapToLong(Map.Entry::getValue)
                .reduce(1, (a, b) -> a * b);
        System.out.println(safetyFactor);
    }

    @Override
    protected void part2() {
        if (read.inputType() != INPUT) {
            System.out.println("only for full input");
            return;
        }

        val robots = read.lines().map(Robot::new).toList();

        val row = ".".repeat(mapWidth).toCharArray();
        val grid = Stream.generate(() -> row).limit(mapHeight).toArray(char[][]::new);
        val map = new ResettableMap(grid);

        val treePattern = Pattern.compile("█{21}");

        long numSteps;
        for (numSteps = 0L; !treePattern.matcher(map.toString()).find(); numSteps++) {
            map.reset();
            robots.forEach(robot -> {
                robot.move(1);
                map.set(robot.position, '█');
            });
        }

        System.out.println(map);
        System.out.printf("found easter egg in [%d] steps%n", numSteps);
    }

}
