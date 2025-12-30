package com.github.caster.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

import com.github.caster.shared.input.InputLoader.InputType;

import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public final class Expectations {

    private final Map<Input, Long> expectations = new HashMap<>();

    public record Input(InputType inputType, int part) {}

    private InputType builderInputType;
    private int builderPart;

    public static Expectations empty() {
        return new Expectations(null, 0);
    }

    public static Expectations expect(final InputType inputType) {
        return new Expectations(inputType, 1);
    }

    public Expectations toSolveTo(final long result) {
        expectations.put(new Input(builderInputType, builderPart), result);
        return this;
    }

    public Expectations andPart(final int part) {
        builderPart = part;
        return this;
    }

    public Expectations alsoExpect(final InputType inputType) {
        builderInputType = inputType;
        builderPart = 1;
        return this;
    }

    public OptionalLong get(final Input input) {
        val result = expectations.get(input);
        return result == null ? OptionalLong.empty() : OptionalLong.of(result);
    }

}
