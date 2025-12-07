package com.github.caster.solutions.y2025;

import com.github.caster.shared.BaseSolution;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Integer.parseInt;

public final class Day1 extends BaseSolution {

    private static final int DIAL_SIZE = 100;
    private static final int START = 50;

    private final Dial dial;

    public Day1() {
        read.from(INPUT);
        dial = new Dial();
        read.lines().forEach(dial::rotate);
    }

    private static final class Dial {

        private int position = START;
        private int stoppedAtZeroCount = 0;
        private int passedZeroCount = 0;

        void rotate(final String rotation) {
            val absRot = parseInt(rotation.substring(1));
            passedZeroCount += absRot / DIAL_SIZE;
            val rot = (rotation.charAt(0) == 'R' ? 1 : -1) * absRot % DIAL_SIZE;
            if (position > 0 && (-rot >= position || position + rot >= DIAL_SIZE)) {
                passedZeroCount++;
            }
            position = (position + rot + DIAL_SIZE) % DIAL_SIZE;
            if (position == 0) {
                stoppedAtZeroCount++;
            }
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

