package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.math.Vector;
import lombok.val;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;
import static java.lang.Math.round;

public final class Day13 extends BaseSolution {

    private static final double EPSILON = 0.001;
    private static final Pattern X_PATTERN = Pattern.compile("X[+=](\\d+)");
    private static final Pattern Y_PATTERN = Pattern.compile("Y[+=](\\d+)");

    public Day13() {
        read.from(INPUT);
    }

    record Button(long moveX, long moveY, long cost) {

        private Button(final String input) {
            this(
                    parse(X_PATTERN, input),
                    parse(Y_PATTERN, input),
                    input.charAt(7) == 'A' ? 3L : 1L
            );
        }

        private Vector pressTimes(int presses) {
            return new Vector(
                    presses * moveX,
                    presses * moveY
            );
        }

    }

    @Override
    protected void part1() {
        solve(0L);
    }

    private void solve(final long prizeOffset) {
        read.with(sections -> {
            var spendTotal = 0L;
            while (sections.hasNext()) {
                val sectionLines = sections.next().lines().toList();
                val buttonA = new Button(sectionLines.get(0));
                val buttonB = new Button(sectionLines.get(1));
                val prize = new Vector(
                        prizeOffset + parse(X_PATTERN, sectionLines.getLast()),
                        prizeOffset + parse(Y_PATTERN, sectionLines.getLast())
                );

                val solutionOptional = calculateIntersectionPoint(
                        (double) -buttonB.moveX() / buttonA.moveX(),
                        (double) prize.get(0) / buttonA.moveX(),
                        (double) -buttonB.moveY() / buttonA.moveY(),
                        (double) prize.get(1) / buttonA.moveY()
                );
                if (solutionOptional.isPresent()) {
                    val solution = solutionOptional.get();
                    spendTotal += solution.get(1) * buttonA.cost() + solution.get(0) * buttonB.cost();
                }
            }
            System.out.printf("Spend %d in total%n", spendTotal);
        });
    }

    private static long parse(final Pattern pattern, final String input) {
        val matcher = pattern.matcher(input);
        if (matcher.find()) {
            return parseLong(matcher.group(1));
        }
        throw new RuntimeException("Parse error");
    }

    private Optional<Vector> calculateIntersectionPoint(
            double m1,
            double b1,
            double m2,
            double b2
    ) {

        if (m1 == m2) {
            return Optional.empty();
        }

        double x = (b2 - b1) / (m1 - m2);
        double y = m1 * x + b1;

        if (abs(x - round(x)) < EPSILON && abs(y - round(y)) < EPSILON) {
            return Optional.of(new Vector(round(x), round(y)));
        }
        return Optional.empty();
    }

    @Override
    protected void part2() {
        solve(10000000000000L);
    }

}
