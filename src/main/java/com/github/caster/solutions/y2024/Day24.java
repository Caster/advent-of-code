package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.*;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Integer.max;
import static java.lang.Integer.parseInt;
import static java.lang.Long.toBinaryString;
import static java.lang.Math.min;
import static java.util.stream.Collectors.*;

public final class Day24 extends BaseSolution {

    private static final Pattern GATE_PATTERN =
            Pattern.compile("([a-z0-9]{3}) (AND|X?OR) ([a-z0-9]{3}) -> ([a-z0-9]{3})");

    private final Map<String, Wire> wires = new HashMap<>();
    private final List<String> swaps = new ArrayList<>();

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

    enum WireCategory { UNCATEGORIZED, CARRY, SUM, CARRIED_OVER_CARRY, CARRY_NOW_OR_CARRIED_OVER, ADDER }

    record WireDependency(Wire wire1, Wire wire2, Operation operation) {}

    @RequiredArgsConstructor
    enum Operation {
        AND((wire1, wire2) -> wire1.value && wire2.value),
        OR((wire1, wire2) -> wire1.value || wire2.value),
        XOR((wire1, wire2) -> wire1.value ^ wire2.value);

        @Delegate
        private final BiPredicate<Wire, Wire> operator;
    }

    @ToString
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    static class Wire {

        @EqualsAndHashCode.Include
        private final String name;

        @Getter
        private WireCategory category;
        private int ioIndexNumber;
        private boolean value;
        private WireDependency dependency;

        Wire(final String name) {
            this.name = name;
            this.category = WireCategory.UNCATEGORIZED;
            if (name.matches("[xyz]\\d{2}")) {
                this.ioIndexNumber = parseInt(name.substring(1));
            }
        }

        void computeValue() {
            if (dependency != null) {
                dependency.wire1.computeValue();
                dependency.wire2.computeValue();
                value = dependency.operation.test(dependency.wire1, dependency.wire2);
            }
        }

        long bitAsLongValue() {
            return (value ? 1L : 0L) << ioIndexNumber;
        }

    }

    @Override
    protected void part1() {
        System.out.println(getRegisterValue("z"));
    }

    long getRegisterValue(final String namePrefix) {
        return wires.values().stream()
                .filter(wire -> wire.name.startsWith(namePrefix))
                .peek(Wire::computeValue)
                .mapToLong(Wire::bitAsLongValue)
                .sum();
    }

    @Override
    protected void part2() {
        // perform the swaps we believe are necessary
        swapDependencies("z11", "vkq");
        swapDependencies("z24", "mmk");
        swapDependencies("qdq", "pvb");
        swapDependencies("z38", "hqh");

        val x = getRegisterValue("x");
        val y = getRegisterValue("y");
        val z = getRegisterValue("z");
        System.out.printf("x = %46s (%d)%ny = %46s (%d) +%nz = %s (%d)%n",
                toBinaryString(x), x, toBinaryString(y), y, toBinaryString(z), z);

        // find sum and carry wires
        wires.values().stream()
                .filter(this::dependsOnTwoInputs)
                .forEach(wire -> {
                    wire.category = switch (wire.dependency.operation) {
                        case XOR -> WireCategory.SUM;
                        case AND -> WireCategory.CARRY;
                        default -> null;
                    };
                    wire.ioIndexNumber = wire.dependency.wire1.ioIndexNumber;
                });
        // special case for first bit wire
        val wireZ00 = wires.get("z00");
        if (wireZ00.category == WireCategory.SUM && wireZ00.ioIndexNumber == 0) {
            wireZ00.category = WireCategory.ADDER;
        }
        // special case for second bit wire
        val wireZ01 = wires.get("z01");
        if (wireZ01.dependency != null && wireZ01.dependency.operation == Operation.XOR
                && wireZ01.dependency.wire1.category == WireCategory.SUM
                && wireZ01.dependency.wire1.ioIndexNumber == 1
                && wireZ01.dependency.wire2.category == WireCategory.CARRY
                && wireZ01.dependency.wire2.ioIndexNumber == 0) {
            wireZ01.category = WireCategory.ADDER;
        }
        // find other adders by following outputs in order
        for (int i = 2; i < 46; i++) {
            val wireZX = wires.get("z%02d".formatted(i));
            categorize(wireZX);
        }

        val categoryCounts = wires.values().stream().collect(groupingBy(Wire::getCategory, counting()));
        System.out.printf("%nThere are %d wires:%n", wires.size());
        categoryCounts.entrySet().stream().filter(entry -> entry.getKey() != WireCategory.UNCATEGORIZED)
                .forEach(entry -> System.out.printf("  %d %s wires;%n", entry.getValue(),
                        entry.getKey().name().toLowerCase().replaceAll("_", "-")));
        System.out.printf("  %d uncategorized wires.%n", categoryCounts.get(WireCategory.UNCATEGORIZED));

        //System.out.println("sum wire 28 = " + wires.values().stream()
        //        .filter(wire -> wire.category == WireCategory.SUM && wire.ioIndexNumber == 28)
        //        .findFirst().orElseThrow().name);

        System.out.println();
        for (int i = 0; i < 45; i++) {
            val wireZX = wires.get("z%02d".formatted(i));
            if (wireZX.category != WireCategory.ADDER) {
                printWireWithDependencies(wireZX, 0);
                break;
            }
        }
        if (wires.get("z45").category != WireCategory.CARRY_NOW_OR_CARRIED_OVER) {
            printWireWithDependencies(wires.get("z45"), 0);
            return;
        }

        System.out.println("All good! Swaps: " + swaps.stream().sorted().collect(joining(",")));
    }

    void swapDependencies(final String wire1Name, final String wire2Name) {
        val wire1 = wires.get(wire1Name);
        val wire2 = wires.get(wire2Name);
        val dependency1 = wire1.dependency;
        wire1.dependency = wire2.dependency;
        wire2.dependency = dependency1;

        swaps.add(wire1Name);
        swaps.add(wire2Name);
    }

    boolean dependsOnTwoInputs(final Wire wire) {
        return wire.dependency != null
                && wire.dependency.wire1.name.matches("[xy]..")
                && wire.dependency.wire2.name.matches("[xy]..")
                && wire.dependency.wire1.ioIndexNumber == wire.dependency.wire2.ioIndexNumber;
    }

    void categorize(final Wire wire) {
        if (wire.category != WireCategory.UNCATEGORIZED || wire.dependency == null)  return;
        categorize(wire.dependency.wire1);
        categorize(wire.dependency.wire2);

        if (isCarriedOverCarry(wire)) {
            wire.category = WireCategory.CARRIED_OVER_CARRY;
            wire.ioIndexNumber = min(wire.dependency.wire1.ioIndexNumber, wire.dependency.wire2.ioIndexNumber);
            return;
        }
        if (isCarryNowOrCarriedOver(wire)) {
            wire.category = WireCategory.CARRY_NOW_OR_CARRIED_OVER;
            wire.ioIndexNumber = max(wire.dependency.wire1.ioIndexNumber, wire.dependency.wire2.ioIndexNumber);
        }
        if (isAdder(wire)) {
            wire.category = WireCategory.ADDER;
        }
    }

    boolean isCarriedOverCarry(final Wire wire) {
        return wire.dependency.operation == Operation.AND
                && (hasDependencyWith1IndexNumberDifferenceAndCategories(wire, WireCategory.CARRY, WireCategory.SUM)
                    || hasDependencyWith1IndexNumberDifferenceAndCategories(wire, WireCategory.CARRY_NOW_OR_CARRIED_OVER, WireCategory.SUM));
    }

    boolean hasDependencyWith1IndexNumberDifferenceAndCategories(
            final Wire wire, final WireCategory expectedCategory1, final WireCategory expectedCategory2
    ) {
        val dep1 = wire.dependency.wire1;
        val dep2 = wire.dependency.wire2;
        return (dep1.category == expectedCategory1 && dep2.category == expectedCategory2
                    && dep1.ioIndexNumber == dep2.ioIndexNumber - 1) ||
                (dep2.category == expectedCategory1 && dep1.category == expectedCategory2
                    && dep2.ioIndexNumber == dep1.ioIndexNumber - 1);
    }

    boolean isCarryNowOrCarriedOver(final Wire wire) {
        return wire.dependency.operation == Operation.OR
                && hasDependencyWith1IndexNumberDifferenceAndCategories(wire, WireCategory.CARRIED_OVER_CARRY, WireCategory.CARRY);
    }

    boolean isAdder(final Wire wire) {
        val couldBeAdder = wire.dependency.operation == Operation.XOR
                && hasDependencyWith1IndexNumberDifferenceAndCategories(wire, WireCategory.CARRY_NOW_OR_CARRIED_OVER, WireCategory.SUM);
        val isOutputWire = wire.ioIndexNumber > 0;
        if (couldBeAdder && !isOutputWire) {
            System.out.printf("WARN: wire [%s] could have been an adder for output bit [%d]%n", wire.name,
                    max(wire.dependency.wire1.ioIndexNumber, wire.dependency.wire2.ioIndexNumber));
        }
        return couldBeAdder && isOutputWire;
    }

    void printWireWithDependencies(final Wire wire, final int depth) {
        if (wire.category != WireCategory.UNCATEGORIZED) {
            System.out.printf(
                    "%s%s wire %02d [%s]%n",
                    "  ".repeat(depth),
                    wire.category.name().toLowerCase().replaceAll("_", "-"),
                    wire.ioIndexNumber,
                    wire.name
            );
            return;
        }

        System.out.println("  ".repeat(depth) + wire.name + (wire.dependency == null ? "" : " (" + wire.dependency.operation + ")"));
        if (wire.dependency != null) {
            printWireWithDependencies(wire.dependency.wire1, depth + 1);
            printWireWithDependencies(wire.dependency.wire2, depth + 1);
        }
    }

}
