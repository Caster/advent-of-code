package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.lang.Long.parseLong;
import static java.util.Arrays.stream;

public final class Day11 extends BaseSolution {

    private final long[] stones;
    private final Map<MemoKey, Long> cache = new HashMap<>();

    record MemoKey(long stone, long blinksToGo) {};

    public Day11() {
        read.from(INPUT);
        stones = parseLongs(read.firstLine());
    }

    @Override
    protected void part1() {
        System.out.println(stream(stones).map(stone -> determineNumStonesAfterBlinks(stone, 25)).sum());
    }

    private long determineNumStonesAfterBlinks(final long stone, final int blinksToGo) {
        val key = new MemoKey(stone, blinksToGo);
        val cacheResult = cache.get(key);
        if (cacheResult != null) {
            return cacheResult;
        }

        val blinkResult = LongStream.of(stone).mapMulti(this::blinkSingleStone).toArray();
        if (blinksToGo == 1) {
            cache.put(key, (long) blinkResult.length);
            return blinkResult.length;
        }

        val numStones = Arrays.stream(blinkResult)
                .map(resultStone -> determineNumStonesAfterBlinks(resultStone, blinksToGo - 1))
                .sum();
        cache.put(key, numStones);
        return numStones;
    }

    private void blinkSingleStone(final long stone, final LongConsumer downstream) {
        if (stone == 0) {
            downstream.accept(1L);
            return;
        }
        val stoneString = Long.toString(stone);
        val numDigits = stoneString.length();
        if (numDigits % 2 == 0) {
            downstream.accept(parseLong(stoneString.substring(0, numDigits / 2)));
            downstream.accept(parseLong(stoneString.substring(numDigits / 2)));
            return;
        }
        downstream.accept(stone * 2024L);
    }

    @Override
    protected void part2() {
        System.out.println(stream(stones).map(stone -> determineNumStonesAfterBlinks(stone, 75)).sum());
    }

}
