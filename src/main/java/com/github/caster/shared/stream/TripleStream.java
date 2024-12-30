package com.github.caster.shared.stream;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@UtilityClass
public final class TripleStream {

    public record Triple<T>(T t1, T t2, T t3) {}

    @FunctionalInterface
    public interface TriConsumer<T> {
        void accept(final T t1, final T t2, final T t3);
    }

    @FunctionalInterface
    public interface TriPredicate<T> {
        boolean test(final T t1, final T t2, final T t3);
    }

    private record TripleIndex<T>(List<T> list, int i, int j, int k) {

        TripleIndex(final List<T> list) {
            this(list, 0, 1, 2);
        }

        boolean hasNext() {
            val n = list.size();
            return k < n - 1 || j < n - 2 || i < n - 3;
        }

        TripleIndex<T> next() {
            val n = list.size();
            if (k < n - 1)  return new TripleIndex<>(list, i, j, k + 1);
            if (j < n - 2)  return new TripleIndex<>(list, i, j + 1, j + 2);
            return new TripleIndex<>(list, i + 1, i + 2, i+ 3);
        }

        Triple<T> getTriple() {
            return new Triple<>(list.get(i), list.get(j), list.get(k));
        }

    }

    public static <T> Stream<Triple<T>> streamTriples(final List<T> list) {
        return Stream.iterate(new TripleIndex<>(list), TripleIndex::hasNext, TripleIndex::next)
                .map(TripleIndex::getTriple);
    }

    public static <T> Consumer<Triple<T>> unpackedTriple(final TriConsumer<T> consumer) {
        return (triple) -> consumer.accept(triple.t1, triple.t2, triple.t3);
    }

    public static <T> Predicate<Triple<T>> unpackedTriple(final TriPredicate<T> predicate) {
        return (triple) -> predicate.test(triple.t1, triple.t2, triple.t3);
    }

}
