package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparing;

public final class Day24 extends BaseSolution {

    private static final Pattern GATE_PATTERN =
            Pattern.compile("([a-z0-9]{3}) (AND|X?OR) ([a-z0-9]{3}) -> ([a-z0-9]{3})");

    private final Map<String, Wire> wires = new HashMap<>();

    public Day24() {
        read.from(INPUT);

        read.with(sections -> {
            sections.next().lines().forEach(line -> {
                val wireAndInitialValue = line.split(": ");
                val wire = new Wire(wireAndInitialValue[0]);
                wire.value = "1".equals(wireAndInitialValue[1]);
                wires.put(wire.name, wire);
            });

            sections.next().lines()
                    .map(GATE_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .forEach(matcher ->
                            wires.computeIfAbsent(matcher.group(4), Wire::new).dependency = new WireDependency(
                                            wires.computeIfAbsent(matcher.group(1), Wire::new),
                                            wires.computeIfAbsent(matcher.group(3), Wire::new),
                                            Operation.valueOf(matcher.group(2)))
                    );
        });
    }

    @RequiredArgsConstructor
    enum Operation {
        AND((wire1, wire2) -> wire1.value && wire2.value),
        OR((wire1, wire2) -> wire1.value || wire2.value),
        XOR((wire1, wire2) -> wire1.value ^ wire2.value);

        @Delegate
        private final BiPredicate<Wire, Wire> operator;
    }

    record WireDependency(Wire wire1, Wire wire2, Operation operation) {}

    @RequiredArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString
    static class Wire {

        @EqualsAndHashCode.Include
        private final String name;

        private boolean value;
        private WireDependency dependency;

        long computeValue() {
            if (dependency != null) {
                dependency.wire1.computeValue();
                dependency.wire2.computeValue();
                value = dependency.operation.test(dependency.wire1, dependency.wire2);
            }
            if (!name.startsWith("z"))  return 0L;
            return (value ? 1L : 0L) << parseInt(name.substring(1));
        }

        String name() {
            return name;
        }

    }

    @Override
    protected void part1() {
        System.out.println(
                wires.values().stream()
                        .filter(wire -> wire.name.startsWith("z"))
                        .sorted(comparing(Wire::name).reversed())
                        .mapToLong(Wire::computeValue)
                        .sum()
        );
    }

}
