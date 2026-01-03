package com.github.caster.solutions.y2025;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import com.github.caster.shared.BaseSolution2;
import com.github.caster.shared.Expectations;

import lombok.val;

import static com.github.caster.shared.Expectations.expect;
import static com.github.caster.shared.input.InputLoader.InputType.EXAMPLE;
import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseInts;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static com.github.caster.shared.stream.ZippingGatherer.zipWith;
import static java.lang.Integer.parseUnsignedInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.iterate;
import static org.chocosolver.solver.Model.MINIMIZE;

public final class Day10 extends BaseSolution2 {

    public static Expectations expectations() {
        return expect(EXAMPLE).toSolveTo(7).andPart(2).toSolveTo(33)
                .alsoExpect(INPUT).toSolveTo(438);
    }

    private final List<Machine> machines;
    private final AtomicInteger machineIndex;

    private record Machine(
            int indicatorLightDiagram,
            int[] buttons,
            int[][] buttonToggles,
            List<Integer> joltageLevels
    ) {
        static Machine from(final String line) {
            val parts = line.split(" ");

            // parse indicator light diagram as an int, each bit is an indicator light
            val diagram = new StringBuilder(parts[0]
                    .replace('.', '0')
                    .replace('#', '1'))
                    .deleteCharAt(parts[0].length() - 1)
                    .deleteCharAt(0)
                    .reverse().toString();
            val indicatorLightDiagram = parseUnsignedInt(diagram, 2);

            // parse buttons as bitmasks, lights that are toggled are 1
            val buttons = stream(parts, 1, parts.length - 1)
                    .map(toggles -> toggles.substring(1, toggles.length() - 1))
                    .map(toggles -> parseLongs(toggles, ","))
                    .mapToInt(toggles -> stream(toggles).mapToInt(light -> 1 << light).sum())
                    .toArray();

            // parse buttons as an array of lights they toggle
            val buttonToggles = stream(parts, 1, parts.length - 1)
                    .map(toggles -> toggles.substring(1, toggles.length() - 1))
                    .map(toggles -> parseInts(toggles, ","))
                    .toArray(int[][]::new);

            // parse required joltage levels
            val joltageLevels = stream(new StringBuilder(parts[parts.length - 1])
                    .deleteCharAt(parts[parts.length - 1].length() - 1)
                    .deleteCharAt(0).toString()
                    .split(","))
                    .map(Integer::parseInt)
                    .toList();

            return new Machine(indicatorLightDiagram, buttons, buttonToggles, joltageLevels);
        }
    }

    public Day10() {
        machines = read.lines().map(Machine::from).toList();
        machineIndex = new AtomicInteger();
    }

    @Override
    protected long part1() {
        return machines.stream().mapToLong(this::determineFewestButtonPressesToActivate).sum();
    }

    private long determineFewestButtonPressesToActivate(final Machine machine) {
        return iterate(
                Set.of(0), // initially all lights are off
                currentStates -> currentStates.stream()
                        .noneMatch(state -> state == machine.indicatorLightDiagram),
                inEachStatePushAll(machine.buttons)
        ).count();
    }

    private UnaryOperator<Set<Integer>> inEachStatePushAll(final int[] buttons) {
        return currentStates -> currentStates.stream()
                    .<Integer>mapMulti((currentState, downstream) ->
                            stream(buttons).forEach(
                                    button -> downstream.accept(currentState ^ button)))
                    .collect(toSet());
    }

    @Override
    protected long part2() {
        if (read.inputType() == INPUT) {
            val fewestButtonPresses = machines.stream()
                    .mapToLong(this::determineFewestButtonPressesToConfigureJoltageUsingChoco)
                    .sum();
            IO.println();
            return fewestButtonPresses;
        }

        return machines.stream()
                .mapToLong(this::determineFewestButtonPressesToConfigureJoltage)
                .sum();
    }

    private long determineFewestButtonPressesToConfigureJoltageUsingChoco(final Machine machine) {
        val model = new Model("Joltage levels for machine %d / %d"
                .formatted(machineIndex.incrementAndGet(), machines.size()));
        IO.println("solving %s...".formatted(model.getName()));

        // variables: a bX for every button to count its presses; buttonPresses to count total
        val bXs = model.intVarArray("b", machine.buttons.length, 0, 999_999);
        val buttonPresses = model.intVar("totalButtonPresses", 0, 999);

        // constraints: the sum of all bX toggling a specific jY is equal to jY
        range(0, machine.joltageLevels.size()).forEach(j -> {
            val buttonsIncrementingJ = range(0, machine.buttons.length)
                    .filter(b -> stream(machine.buttonToggles[b]).anyMatch(t -> t == j))
                    .mapToObj(b -> bXs[b])
                    .toArray(IntVar[]::new);
            model.sum(buttonsIncrementingJ, "=", machine.joltageLevels.get(j)).post();
        });

        // constraint: the buttonPresses variable is equal to the sum of all bX
        model.sum(bXs, "=", buttonPresses).post();

        // solve
        val solution = model.getSolver().findOptimalSolution(buttonPresses, MINIMIZE);
        return stream(bXs).mapToInt(solution::getIntVal).sum();
    }

    private long determineFewestButtonPressesToConfigureJoltage(final Machine machine) {
        val initialJoltageLevels = machine.joltageLevels.stream().map(_ -> 0).toList();
        return iterate(
                Set.of(initialJoltageLevels),
                currentStates -> currentStates.stream().noneMatch(machine.joltageLevels::equals),
                inEachJoltageStatePushAllButtonsOn(machine)
        ).count();
    }

    private UnaryOperator<Set<List<Integer>>> inEachJoltageStatePushAllButtonsOn(
            final Machine machine
    ) {
        return currentStates -> currentStates.stream()
                .<List<Integer>>mapMulti((currentState, downstream) ->
                        stream(machine.buttonToggles).forEach(button -> {
                            val newState = press(button, currentState);
                            if (doesNotOvershoot(newState, machine.joltageLevels)) {
                                downstream.accept(newState);
                            }
                        }))
                .collect(toSet());
    }

    private List<Integer> press(final int[] button, final List<Integer> currentState) {
        val result = new ArrayList<>(currentState);
        stream(button).forEach(toggle -> result.set(toggle, result.get(toggle) + 1));
        return result;
    }

    private boolean doesNotOvershoot(final List<Integer> state, final List<Integer> joltageLevels) {
        return state.stream().gather(zipWith(joltageLevels.stream()))
                .allMatch(joltagePair -> joltagePair.t1() <= joltagePair.t2());
    }

}
