package com.github.caster.shared.math;

import java.util.Arrays;
import java.util.stream.LongStream;

import com.github.caster.shared.stream.ZippingGatherer.Pair;

import lombok.EqualsAndHashCode;
import lombok.val;

import static com.github.caster.shared.stream.StreamUtils.iterateIndicesOf;
import static com.github.caster.shared.stream.ZippingGatherer.zipWith;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.subtractExact;

@EqualsAndHashCode
public final class Vector {

    private final long[] components;

    public Vector(final long... components) {
        this.components = components;
    }

    public long absoluteSum() {
        return stream().map(Math::abs).reduce(Long::sum).orElse(0L);
    }

    public long squaredEuclideanDistanceTo(final Vector that) {
        if (this.components.length != that.components.length) {
            throw new IllegalArgumentException("Incompatible number of components");
        }

        return stream().boxed().gather(zipWith(that.stream().boxed()))
                .mapToLong(this::squaredDifference)
                .sum();
    }

    private long squaredDifference(final Pair<Long, Long> components) {
        val diff = subtractExact(components.t1(), components.t2());
        return multiplyExact(diff, diff);
    }

    public long get(final int index) {
        return components[index];
    }

    public Vector minus(final Vector that) {
        if (this.components.length != that.components.length) {
            throw new IllegalArgumentException("Incompatible number of components");
        }

        return new Vector(
                iterateIndicesOf(components)
                        .mapToLong(i -> this.components[i] - that.components[i])
                        .toArray()
        );
    }

    public Vector plus(final Vector that) {
        if (this.components.length != that.components.length) {
            throw new IllegalArgumentException("Incompatible number of components");
        }

        return new Vector(
                iterateIndicesOf(components)
                        .mapToLong(i -> this.components[i] + that.components[i])
                        .toArray()
        );
    }

    public LongStream stream() {
        return Arrays.stream(components);
    }

    @Override
    public String toString() {
        return Arrays.toString(components);
    }
}
