package com.github.caster.shared.input;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.Delegate;
import lombok.val;

import static java.util.Arrays.stream;

public final class InputLoader {

    @SuppressWarnings("unused")
    public enum InputType {
        EXAMPLE,
        EXAMPLE2,
        INPUT
    }

    private static final Pattern YEAR_PART = Pattern.compile("y(\\d{4})");

    private InputType inputType;

    @Delegate
    private Section section;

    public void from(final InputType inputToLoad) {
        final String directory = stream(Thread.currentThread().getStackTrace())
                .filter(el -> el.getClassName().startsWith("com.github.caster.solutions"))
                .findFirst()
                .map(StackTraceElement::getClassName)
                .map(fqcn -> {
                    val yearPart = stream(fqcn.split("\\."))
                            .map(YEAR_PART::matcher)
                            .filter(Matcher::matches)
                            .map(matcher -> matcher.group(1))
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalStateException("Could not find year part in FQCN [%s]".formatted(fqcn)));
                    val dayPart = fqcn.substring(fqcn.lastIndexOf('.') + 1).toLowerCase();
                    return yearPart + "/" + dayPart;
                })
                .orElseThrow();
        final String resourceBaseName = inputToLoad.name().toLowerCase();

        final URL resourceUrl = InputLoader.class.getResource(
                "/%s/%s.txt".formatted(directory, resourceBaseName)
        );
        if (resourceUrl == null) {
            throw new RuntimeException("Unknown resource [%s]".formatted(resourceBaseName));
        }
        try (val inputLinesStream = Files.lines(Path.of(resourceUrl.toURI()))) {
            inputType = inputToLoad;
            section = new Section(inputLinesStream.toList());
        } catch (final IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public InputType inputType() {
        return inputType;
    }

    public static List<Long> asList(final long[] array) {
        return stream(array).boxed().toList();
    }

    public static long[] parseLongs(final String input) {
        return stream(parseColumns(input)).mapToLong(Long::parseLong).toArray();
    }

    public static long[] parseLongs(final String input, final String splitByRegex) {
        return stream(parseColumns(input, splitByRegex)).mapToLong(Long::parseLong).toArray();
    }

    public static String[] parseColumns(final String input) {
        return parseColumns(input, "\\s+");
    }

    public static String[] parseColumns(final String input, final String splitByRegex) {
        return input.strip().split(splitByRegex);
    }

}
