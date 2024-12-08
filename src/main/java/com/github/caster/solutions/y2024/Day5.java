package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static java.lang.Long.parseLong;
import static java.util.List.copyOf;
import static java.util.function.Predicate.not;

public final class Day5 extends BaseSolution {

    private final List<OrderingRule> orderingRules = new ArrayList<>();
    private final List<Update> updates = new ArrayList<>();

    public Day5() {
        read.from(INPUT);
        readRulesAndUpdates();
    }

    private void readRulesAndUpdates() {
        read.lines().forEach(line -> {
            if (line.indexOf('|') >= 0) {
                val split = line.split("\\|");
                orderingRules.add(new OrderingRule(parseLong(split[0]), parseLong(split[1])));
            } else {
                updates.add(new Update(Arrays.stream(line.split(",")).map(Long::parseLong).toList()));
            }
        });
    }

    record OrderingRule(long firstPage, long secondPage) {
        boolean isSatisfied(final List<Long> pageNumbers, final int index) {
            val value = pageNumbers.get(index);
            if (value == firstPage) {
                return !pageNumbers.subList(0, index).contains(secondPage);
            }
            if (value == secondPage) {
                return !pageNumbers.subList(index + 1, pageNumbers.size()).contains(firstPage);
            }
            return true;
        }

        List<Long> correctIfNotSatisfied(final List<Long> pageNumbers, final int index) {
            val value = pageNumbers.get(index);
            if (value == firstPage) {
                val otherIndex = pageNumbers.subList(0, index).indexOf(secondPage);
                if (otherIndex >= 0) {
                    return copyWithSwapped(pageNumbers, index, otherIndex);
                }
            }
            if (value == secondPage) {
                val otherIndex = pageNumbers.subList(index + 1, pageNumbers.size()).indexOf(firstPage);
                if (otherIndex >= 0) {
                    return copyWithSwapped(pageNumbers, index, otherIndex + index + 1);
                }
            }
            return null;
        }
    }

    record Update(List<Long> pageNumbers) {
        boolean isCorrectlyOrdered(final List<OrderingRule> orderingRules) {
            for (int i = 0; i < pageNumbers.size(); i++) {
                for (val rule : orderingRules) {
                    if (!rule.isSatisfied(pageNumbers, i)) {
                        return false;
                    }
                }
            }
            return true;
        }

        Long middlePageNumber() {
            return pageNumbers.get(pageNumbers.size() / 2);
        }

        Update corrected(final List<OrderingRule> orderingRules) {
            var correctedPageNumbers = copyOf(pageNumbers);
            for (int i = 0; i < correctedPageNumbers.size(); i++) {
                for (val rule : orderingRules) {
                    val correction = rule.correctIfNotSatisfied(correctedPageNumbers, i);
                    if (correction != null) {
                        correctedPageNumbers = correction;
                        i = 0;
                        break;
                    }
                }
            }
            return new Update(correctedPageNumbers);
        }
    }

    static <T> List<T> copyWithSwapped(final List<T> list, final int indexA, final int indexB) {
        return Stream.iterate(0, i -> i < list.size(), i -> i + 1)
                .map(i -> {
                    if (i == indexA)  return list.get(indexB);
                    if (i == indexB)  return list.get(indexA);
                    return list.get(i);
                })
                .toList();
    }

    @Override
    protected void part1() {
        System.out.println(updates.stream()
                .filter(update -> update.isCorrectlyOrdered(orderingRules))
                .mapToLong(Update::middlePageNumber)
                .sum());
    }

    @Override
    protected void part2() {
        System.out.println(updates.stream()
                .filter(not(update -> update.isCorrectlyOrdered(orderingRules)))
                .map(update -> update.corrected(orderingRules))
                .mapToLong(Update::middlePageNumber)
                .sum());

    }

}
