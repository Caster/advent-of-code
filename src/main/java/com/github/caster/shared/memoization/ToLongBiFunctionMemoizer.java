package com.github.caster.shared.memoization;

import lombok.RequiredArgsConstructor;

import java.util.function.ToLongBiFunction;

@RequiredArgsConstructor
public final class ToLongBiFunctionMemoizer<K1, K2>
        extends Memoizer<ToLongBiFunctionMemoizer.MemoKey<K1, K2>, Long>
        implements ToLongBiFunction<K1, K2> {

    record MemoKey<K1, K2>(K1 k1, K2 k2) {}

    private final ToLongBiFunction<K1, K2> function;

    @Override
    public long applyAsLong(final K1 k1, final K2 k2) {
        return getFromOrUpdateCache(new MemoKey<>(k1, k2), () -> function.applyAsLong(k1, k2));
    }
}
