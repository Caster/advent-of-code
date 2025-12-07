package com.github.caster.solutions.y2025;

import com.github.caster.shared.BaseSolution;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Integer.parseInt;
import static java.util.stream.Gatherers.fold;

public final class Day1 extends BaseSolution {

    private static final int DIAL_SIZE = 100;
    private static final int START = 50;

    private final Dial dial;

    public Day1() {
        read.from(INPUT);
        dial = read.lines()
                .map(Rotation::new)
                .gather(fold(Dial::new, Dial::apply))
                .findFirst().orElseThrow();
    }

    private record Rotation(
            int offset,
            int fullRotations
    ) {
        private Rotation(final String rotation) {
            val direction = rotation.charAt(0) == 'R' ? 1 : -1;
            val absoluteRotation = parseInt(rotation.substring(1));
            this(
                    (direction * absoluteRotation) % DIAL_SIZE,
                    absoluteRotation / DIAL_SIZE
            );
        }
    }

    private record Dial(
            int position,
            int stoppedAtZeroCount,
            int passedZeroCount
    ) {
        private Dial() {
            this(START, 0, 0);
        }

        Dial apply(final Rotation rotation) {
            val newPositionRaw = position + rotation.offset;
            val newPosition = (newPositionRaw + DIAL_SIZE) % DIAL_SIZE;
            return new Dial(
                    newPosition,
                    stoppedAtZeroCount + (newPosition == 0 ? 1 : 0),
                    passedZeroCount + rotation.fullRotations + ((position > 0
                            && (newPositionRaw <= 0 || DIAL_SIZE <= newPositionRaw)) ? 1 : 0)
            );
        }
    }

    @Override
    protected void part1() {
        IO.println(dial.stoppedAtZeroCount);
    }

    @Override
    protected void part2() {
        IO.println(dial.passedZeroCount);
    }

}
