package com.github.caster.shared.math;

import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.stream.LongStream;

import static com.github.caster.shared.stream.StreamUtils.iterateIndicesOf;

@EqualsAndHashCode
public final class Vector {

    private final long[] components;

    public Vector(final long... components) {
        this.components = components;
    }

    public long absoluteSum() {
        return stream().map(Math::abs).reduce(Long::sum).orElse(0L);
    }

    public long get(final int index) {
        return components[index];
    }

    public Vector minus(final Vector that) {
        if (this.components.length != that.components.length) {
            throw new RuntimeException("Incompatible number of components");
        }

        return new Vector(
                iterateIndicesOf(components)
                        .mapToLong(i -> this.components[i] - that.components[i])
                        .toArray()
        );
    }

    public Vector plus(final Vector that) {
        if (this.components.length != that.components.length) {
            throw new RuntimeException("Incompatible number of components");
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
