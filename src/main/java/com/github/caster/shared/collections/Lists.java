package com.github.caster.shared.collections;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public final class Lists {

    public static <T> List<T> copyListWithout(final List<T> list, final T elementToIgnoreOnce) {
        val ignoredOnce = new AtomicBoolean();
        return list.stream()
                .filter(element -> {
                    if (!ignoredOnce.get() && element.equals(elementToIgnoreOnce)) {
                        ignoredOnce.set(true);
                        return false;
                    }
                    return true;
                })
                .toList();
    }

}
