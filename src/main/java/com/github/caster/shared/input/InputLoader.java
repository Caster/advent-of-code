package com.github.caster.shared.input;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import lombok.experimental.Delegate;
import lombok.val;

import static java.util.Arrays.stream;

public final class InputLoader {

    public static boolean exists(final InputType inputToCheck) {
        return determineUrl(inputToCheck) != null;
    }

    private static URL determineUrl(final InputType inputType) {
        val directory = formatYearDay("%s/%s").apply(getSolutionClassName());
        val resourceBaseName = inputType.name().toLowerCase();
        return InputLoader.class.getResource("/%s/%s.txt".formatted(directory, resourceBaseName));
    }

    public static String getSolutionClassName() {
        return System.getProperty("sun.java.command");
    }

    public static UnaryOperator<String> formatYearDay(final String format) {
        return fqcn -> {
            val yearMatcher = YEAR_PART.matcher(fqcn);
            if (!yearMatcher.find()) {
                throw new IllegalStateException(
                        "Could not find year part in FQCN [%s]".formatted(fqcn)
                );
            }
            val yearPart = yearMatcher.group(1);
            val dayPart = fqcn.substring(fqcn.lastIndexOf('.') + 1).toLowerCase();
            return format.formatted(yearPart, dayPart);
        };
    }

    public enum InputType {
        EXAMPLE,
        EXAMPLE2,
        INPUT

    }

    private static final Pattern YEAR_PART = Pattern.compile("\\.y(\\d{4})\\.");

    private InputType inputType;

    @Delegate
    private Section section;

    public void from(final InputType inputToLoad) {
        val resourceUrl = determineUrl(inputToLoad);
        if (resourceUrl == null) {
            throw new RuntimeException("Unknown resource [%s]"
                    .formatted(inputToLoad.name().toLowerCase()));
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

    public static int[] parseInts(final String input) {
        return stream(parseColumns(input)).mapToInt(Integer::parseInt).toArray();
    }

    public static int[] parseInts(final String input, final String splitByRegex) {
        return stream(parseColumns(input, splitByRegex)).mapToInt(Integer::parseInt).toArray();
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
