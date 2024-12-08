package com.github.caster.shared.input;

import com.github.caster.shared.map.ResettableMap;
import com.github.caster.shared.math.Matrix;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;

public final class InputLoader {

    @SuppressWarnings("unused")
    public enum InputType {
        EXAMPLE,
        EXAMPLE2,
        INPUT
    }

    private InputType inputType;
    private Path inputPath;

    public void from(final InputType inputToLoad) {
        final String directory = stream(Thread.currentThread().getStackTrace())
                .filter(el -> el.getClassName().startsWith("com.github.caster.solutions"))
                .findFirst()
                .map(StackTraceElement::getClassName)
                .map(fqcn -> fqcn.substring(fqcn.lastIndexOf('.') + 1).toLowerCase())
                .orElseThrow();
        final String resourceBaseName = inputToLoad.name().toLowerCase();

        final URL resourceUrl = InputLoader.class.getResource(
                "/%s/%s.txt".formatted(directory, resourceBaseName)
        );
        if (resourceUrl == null) {
            throw new RuntimeException("Unknown resource [%s]".formatted(resourceBaseName));
        }
        try {
            inputPath = Path.of(resourceUrl.toURI());
            inputType = inputToLoad;
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public InputType inputType() {
        return inputType;
    }

    public String firstLine() {
        return lines().findFirst().orElseThrow();
    }

    public Stream<String> lines() {
        try {
            return Files.lines(inputPath).filter(not(String::isBlank));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResettableMap map() {
        return new ResettableMap(lines().map(String::toCharArray).toArray(char[][]::new));
    }

    public Matrix matrix() {
        final int numberOfColumns = (int) streamFirstLine().flatMap(toColumns()).count();
        final int numberOfRows = (int) lines().count();
        return new Matrix(
                numberOfColumns,
                numberOfRows,
                lines().map(toColumns().andThen(parseLongs()))
        );
    }

    private Stream<String> streamFirstLine() {
        return lines().limit(1);
    }

    public static Function<String, Stream<String>> toColumns() {
        return line -> stream(line.split("\\s+"));
    }

    public static Function<Stream<String>, LongStream> parseLongs() {
        return strings -> strings.mapToLong(Long::parseLong);
    }

    public String string() {
        try {
            return Files.readString(inputPath);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sections(Consumer<Section>... sections) {
        final AtomicInteger currentSectionIndex = new AtomicInteger();
        lines().forEach(line -> {
            if (line.isBlank()) {
                currentSectionIndex.incrementAndGet();
                return;
            }

//            sections[currentSectionIndex.get()].accept();
        });
    }

}
