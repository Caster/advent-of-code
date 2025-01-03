package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import com.github.caster.shared.input.Section;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.stream.StreamUtils.stream;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public final class Day25 extends BaseSolution {

    private final List<Lock> locks = new ArrayList<>();
    private final List<Key> keys = new ArrayList<>();

    public Day25() {
        read.from(INPUT);
        read.with(sections ->
                stream(sections).map(Section::lines).forEach(this::parseLockOrKeyHeights));
    }

    record Lock(int[] heights) {
        boolean fits(final Key key) {
            return range(0, heights.length).allMatch(i -> heights[i] + key.heights[i] <= 5);
        }
    }
    record Key(int[] heights) {}

    void parseLockOrKeyHeights(final Stream<String> schematic) {
        val lines = schematic.map(String::toCharArray).toArray(char[][]::new);
        val isKey = lines[0][0] == '.';
        val heights = range(0, lines[0].length)
                .map(p -> (int) rangeClosed(1, 5)
                        .map(l -> isKey ? 6 - l : l)
                        .filter(l -> lines[l][p] == '#')
                        .count())
                .toArray();

        if (isKey) {
            keys.add(new Key(heights));
        } else {
            locks.add(new Lock(heights));
        }
    }

    @Override
    protected void part1() {
        System.out.println(
                locks.stream()
                        .mapToLong(lock -> keys.stream().filter(lock::fits).count())
                        .sum()
        );
    }

    @Override
    protected void part2() {
        System.out.println("Not a puzzle, still a beautiful conclusion. Thank you, Eric et al.!");
    }

}
