package com.github.caster.solutions;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.List;
import java.util.regex.Pattern;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

public final class Day4 extends BaseSolution {

    public Day4() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        val numCols = read.firstLine().length();

        val horizontal = "XMAS";
        val latnoziroh = "SAMX";

        val nextRow = ".{%d}".formatted(numCols); // don't forget the newlines!
        val forwardFormatString = join("%1$s", horizontal.split(""));
        val vertical = forwardFormatString.formatted(nextRow);
        val backwardFormatString = join("%1$s", latnoziroh.split(""));
        val lacitrev = backwardFormatString.formatted(nextRow);

        val nextRowDiagonal = ".{%d}".formatted(numCols + 1);
        val diagonal = forwardFormatString.formatted(nextRowDiagonal);
        val lanogaid = backwardFormatString.formatted(nextRowDiagonal);

        val nextRowDiagonal2 = ".{%d}".formatted(numCols - 1);
        val diagonal2 = forwardFormatString.formatted(nextRowDiagonal2);
        val lanogaid2 = backwardFormatString.formatted(nextRowDiagonal2);

        checkPatternsAndPrintMatches(
                horizontal, latnoziroh,
                vertical, lacitrev,
                diagonal, lanogaid,
                diagonal2, lanogaid2
        );
    }

    private void checkPatternsAndPrintMatches(final String... patterns) {
        val input = read.lines().collect(joining("|"));
        var numMatches = 0L;
        for (val pattern : List.of(patterns)) {
            val matcher = Pattern.compile("(?=(" + pattern + ")).").matcher(input);
            val numMatchesIntermediate = matcher.results().count();
            numMatches += numMatchesIntermediate;
        }
        System.out.println(numMatches);
    }

    @Override
    protected void part2() {
        val numCols = read.firstLine().length();
        val nextRow = ".{%d}".formatted(numCols - 2);

        val xmas1 = join("%1$s", "M.S", ".A.", "M.S").formatted(nextRow);
        val xmas2 = join("%1$s", "M.M", ".A.", "S.S").formatted(nextRow);
        val xmas3 = join("%1$s", "S.M", ".A.", "S.M").formatted(nextRow);
        val xmas4 = join("%1$s", "S.S", ".A.", "M.M").formatted(nextRow);

        checkPatternsAndPrintMatches(xmas1, xmas2, xmas3, xmas4);
    }

}
