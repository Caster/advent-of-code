package com.github.caster.shared.stream;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.caster.shared.stream.ZippingGatherer.Pair;

import static java.util.stream.Gatherer.Integrator.ofGreedy;

public record ZippingGatherer<T1, T2>(
        Stream<T2> other
) implements Gatherer<T1, Iterator<T2>, Pair<T1, T2>> {

    public record Pair<T1, T2>(T1 t1, T2 t2) {}

    public static <T1> ZippingGatherer<T1, Integer> zipWith(final IntStream other) {
        return zipWith(other.boxed());
    }

    public static <T1, T2> ZippingGatherer<T1, T2> zipWith(final Stream<T2> other) {
        return new ZippingGatherer<>(other);
    }

    @Override
    public Supplier<Iterator<T2>> initializer() {
        return other::iterator;
    }

    @Override
    public Integrator<Iterator<T2>, T1, Pair<T1, T2>> integrator() {
        return ofGreedy((state, element, downstream) -> {
            if (!state.hasNext())  return false;
            return downstream.push(new Pair<>(element, state.next()));
        });
    }

}
