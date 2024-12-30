package com.github.caster.shared.memoization;

import lombok.NoArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToLongBiFunction;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public sealed class Memoizer<K, V> permits ToLongBiFunctionMemoizer {

    private final Map<K, V> cache = new HashMap<>();

    public static <K1, K2> ToLongBiFunctionMemoizer<K1, K2> cache(final ToLongBiFunction<K1, K2> function) {
        return new ToLongBiFunctionMemoizer<>(function);
    }

    protected V getFromOrUpdateCache(final K key, final Supplier<V> valueSupplier) {
        val cachedResult = cache.get(key);
        if (cachedResult != null)  return cachedResult;

        val result = valueSupplier.get();
        cache.put(key, result);
        return result;
    }

}
