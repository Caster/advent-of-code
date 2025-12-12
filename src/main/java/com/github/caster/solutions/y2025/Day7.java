package com.github.caster.solutions.y2025;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.map.Direction;
import com.github.caster.shared.map.Position;
import com.github.caster.shared.map.ResettableMap;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.map.Direction.DOWN;
import static com.github.caster.shared.map.Direction.LEFT;
import static com.github.caster.shared.map.Direction.RIGHT;
import static com.github.caster.shared.map.ResettableMap.Cell.cellValueIs;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toSet;

public final class Day7 extends BaseSolution {

    private final ResettableMap map;
    private final AtomicLong weights = new AtomicLong();

    public Day7() {
        read.from(INPUT);
        map = read.map();
    }

    @Override
    protected void part1() {
        val startPos = map.stream().filter(cellValueIs('S')).findFirst().orElseThrow().position();
        var beams = Set.of(Beam.at(startPos));
        val numSplits = new AtomicLong();
        while (!beams.isEmpty()) {
            beams = beams.stream()
                    .<Beam>mapMulti((beam, downstream) ->
                            check(beam.moved(DOWN), numSplits, downstream))
                    .collect(groupingBy(Beam::position, reducing(Beam::addWeight)))
                    .values()
                    .stream()
                    .map(Optional::orElseThrow)
                    .collect(toSet());
        }
        IO.println(numSplits.get());
    }

    private record Beam(Position position, long weight) {

        static Beam at(final Position pos) {
            return new Beam(pos, 1);
        }

        Beam addWeight(final Beam that) {
            if (!this.position.equals(that.position)) {
                throw new IllegalArgumentException("other beam is at different position");
            }
            return new Beam(position, this.weight + that.weight);
        }

        Beam moved(final Direction direction) {
            return new Beam(position.moved(direction), weight);
        }

    }

    private void check(
            final Beam beam,
            final AtomicLong numSplits,
            final Consumer<Beam> downstream
    ) {
        if (!map.contains(beam.position())) {
            weights.addAndGet(beam.weight());
            return;
        }
        val space = map.get(beam.position());
        if (space == '.') {
            map.set(beam.position(), '|');
            downstream.accept(beam);
        }
        if (space == '|') {
            downstream.accept(beam);
        }
        if (space == '^') {
            numSplits.incrementAndGet();
            check(beam.moved(LEFT), numSplits, downstream);
            check(beam.moved(RIGHT), numSplits, downstream);
        }
    }

    @Override
    protected void part2() {
        IO.println(weights.get());
    }

}
