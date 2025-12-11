package com.github.caster.solutions.y2025;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import com.github.caster.shared.BaseSolution;

import lombok.val;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.util.Comparator.comparingLong;

public final class Day5 extends BaseSolution {

    private List<Interval> freshIngredientIdIntervals;
    private List<Long> availableIngredients;

    public Day5() {
        read.from(INPUT);
        read.with(sections -> {
            freshIngredientIdIntervals = sections.next().lines()
                    .map(Interval::fromString)
                    .sorted(comparingLong(Interval::start))
                    .gather(new MergingIntervalGatherer())
                    .toList();
            availableIngredients = sections.next().lines()
                    .mapToLong(Long::parseLong).boxed().toList();
        });
    }

    private record Interval(long start, long end) {

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

    private static final class MergingIntervalGatherer
            implements Gatherer<Interval, MutableOptional<Interval>, Interval> {

        @Override
        public Supplier<MutableOptional<Interval>> initializer() {
            return MutableOptional::new;
        }

        @Override
        public Integrator<MutableOptional<Interval>, Interval, Interval> integrator() {
            return (state, currInterval, downstream) -> {
                if (state.isEmpty()) {
                    state.set(currInterval);
                    return true;
                }

                val prevInterval = state.get();
                if (prevInterval.contains(currInterval)) {
                    return true;
                }
                if (prevInterval.contains(currInterval.start)) {
                    state.set(new Interval(prevInterval.start, currInterval.end));
                    return true;
                }
                state.ifPresent(downstream::push).set(currInterval);
                return true;
            };
        }

        @Override
        public BiConsumer<MutableOptional<Interval>, Downstream<? super Interval>> finisher() {
            return (state, downstream) -> state.ifPresent(downstream::push);
        }

    }

    private static final class MutableOptional<T> {

        private T value = null;

        T get() {
            return value;
        }

        boolean isEmpty() {
            return value == null;
        }

        MutableOptional<T> ifPresent(final Consumer<T> consumer) {
            if (!isEmpty()) {
                consumer.accept(value);
            }
            return this;
        }

        void set(final T newValue) {
            this.value = newValue;
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
        IO.println(freshIngredientIdIntervals.stream().mapToLong(Interval::size).sum());
    }

}
