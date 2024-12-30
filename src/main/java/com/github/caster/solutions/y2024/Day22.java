package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.Map;
import java.util.Set;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.stream.StreamUtils.iterateIndicesOf;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.of;
import static java.util.stream.Stream.iterate;

public final class Day22 extends BaseSolution {

    public Day22() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        System.out.println(
                read.lines()
                        .mapToLong(Long::parseLong)
                        .map(initialSecretNumber ->
                                iterate(initialSecretNumber, Day22::evolve)
                                        .skip(2000)
                                        .findFirst().orElseThrow())
                        .sum()
        );
    }

    static long evolve(final long secretNumber) {
        val r1 = prune(mix(secretNumber, secretNumber << 6)); // * 64 = * 2**6 = << 6
        val r2 = prune(mix(r1, r1 >> 5)); // / 32 = / 2**5 = >> 5
        return prune(mix(r2, r2 << 11)); // * 2048 = * 2^^11 = << 11
    }

    static long mix(final long secretNumber, final long value) {
        return secretNumber ^ value;
    }

    static long prune(final long secretNumber) {
        return secretNumber & ((1 << 24) - 1); // % 16_777_216 = % 2**24 = & 2**24 - 1
    }

    record ChangeSequence(
            int[] changes, // last 4 changes in price
            long secretNumber // current secret number, also determines current price
    ) {
        ChangeSequence(final long secretNumber) {
            val numbers = iterate(secretNumber, Day22::evolve).limit(5).mapToLong(Long::longValue).toArray();
            val changes = iterateIndicesOf(numbers)
                    .skip(1)
                    .map(i -> price(numbers[i]) - price(numbers[i - 1]))
                    .toArray();
            this(changes, numbers[4]);
        }

        static int price(final long secretNumber) {
            return (int) (secretNumber % 10);
        }

        ChangeSequence evolve() {
            val newSecretNumber = Day22.evolve(secretNumber);
            val newPrice = price(newSecretNumber);
            return new ChangeSequence(
                    concat(stream(changes).skip(1), of(newPrice - price())).toArray(),
                    newSecretNumber
            );
        }

        int price() {
            return price(secretNumber);
        }

        String key() {
            return stream(changes).mapToObj(change -> Character.toString(
                            (char) (change >= 0 ? '0' + change : 'A' - change - 1)))
                    .collect(joining());
        }
    }

    @Override
    protected void part2() {
        // first make a map from change sequence to first price per secret number
        val totalBananasPerSequence = read.lines()
                .parallel()
                .mapToLong(Long::parseLong)
                .mapToObj(initialSecretNumber ->
                        iterate(new ChangeSequence(initialSecretNumber), ChangeSequence::evolve)
                                .limit(1996)
                                .collect(toMap(ChangeSequence::key, ChangeSequence::price, (price1, _) -> price1)))
                // now combine the maps, summing the prices of different secret numbers together
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
        // finally determine the sequence with the biggest sum of bananas
        // there are slightly over 40k entries in this map for the input, so stream parallel for speedup
        System.out.println(
                totalBananasPerSequence.entrySet()
                        .parallelStream()
                        .max(Map.Entry.comparingByValue()).orElseThrow()
                        .getValue()
        );
    }

}
