package com.github.caster.solutions.y2025;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Gatherer;

import com.github.caster.shared.BaseSolution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.util.stream.Gatherer.ofSequential;

public final class Day5 extends BaseSolution {

    private List<Interval> freshIngredientIdIntervals;
    private List<Long> availableIngredients;

    public Day5() {
        read.from(INPUT);
        read.with(sections -> {
            freshIngredientIdIntervals = sections.next().lines()
                    .map(Interval::fromString).toList();
            availableIngredients = sections.next().lines()
                    .mapToLong(Long::parseLong).boxed().toList();
        });
    }

    @Getter
    @AllArgsConstructor
    private static final class Interval {

        private long start;
        private long end;

        static Interval empty() {
            return new Interval(0, -1);
        }

        static Interval fromString(final String intervalString) {
            val ends = parseLongs(intervalString, "-");
            return new Interval(ends[0], ends[1]);
        }

        boolean contains(final long value) {
            return start <= value && value <= end;
        }

        boolean contains(final Interval interval) {
            return start <= interval.start && interval.end <= end;
        }

        long size() {
            return end - start + 1;
        }
    }

    @Override
    protected void part1() {
        IO.println(availableIngredients.stream().filter(this::isFresh).count());
    }

    private boolean isFresh(final long ingredientId) {
        return freshIngredientIdIntervals.stream()
                .anyMatch(interval -> interval.contains(ingredientId));
    }

    @Override
    protected void part2() {
        val freshIngredientCount = freshIngredientIdIntervals.stream()
                .sorted(Comparator.comparingLong(Interval::getStart))
                .gather(ofSequential(
                        Interval::empty,
                        Gatherer.Integrator.<Interval, Interval, Interval>of((prevInterval, currInterval, downstream) -> {
                            if (prevInterval.contains(currInterval)) {
                                return true;
                            }
                            if (prevInterval.contains(currInterval.start)) {
                                prevInterval.end = currInterval.end;
                                return true;
                            }
                            downstream.push(prevInterval);
                            prevInterval.start = currInterval.start;
                            prevInterval.end = currInterval.end;
                            return true;
                        }),
                        (prevInterval, downstream) -> downstream.push(prevInterval)
                ))
                .mapToLong(Interval::size)
                .sum();
        IO.println(freshIngredientCount);
    }
}
