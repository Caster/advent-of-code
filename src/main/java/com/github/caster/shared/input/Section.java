package com.github.caster.shared.input;

import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.math.Matrix;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.caster.shared.StreamUtils.iterateIndicesOf;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public final class Section {

    private final List<String> lines;

    public String firstLine() {
        return lines.getFirst();
    }

    public Stream<String> lines() {
        return lines.stream();
    }

    public ResettableMap map() {
        return new ResettableMap(lines().map(String::toCharArray).toArray(char[][]::new));
    }

    public Matrix matrix() {
        return new Matrix(lines().map(InputLoader::parseLongs)).transposed();
    }

    public String string() {
        return lines().collect(joining("\n"));
    }

    public void with(final Consumer<Iterator<Section>> sectionConsumer) {
        val sectionSeparatorIndices = Stream.of(
                IntStream.of(-1),
                iterateIndicesOf(lines).filter(i -> lines.get(i).isEmpty()),
                IntStream.of(lines.size())
        ).flatMapToInt(identity()).toArray();
        val sections = range(0, sectionSeparatorIndices.length - 1)
                .mapToObj(i -> lines.subList(sectionSeparatorIndices[i] + 1, sectionSeparatorIndices[i + 1]))
                .map(Section::new)
                .toList();
        sectionConsumer.accept(sections.iterator());
    }

}
