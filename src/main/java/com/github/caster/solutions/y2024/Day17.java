package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.input.InputLoader.parseLongs;
import static java.lang.Integer.parseInt;
import static java.lang.Math.min;
import static java.util.OptionalInt.empty;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public final class Day17 extends BaseSolution {

    public static final long[] registers = {0, 0, 0};
    public static final List<Long> outputList = new ArrayList<>();

    public long[] instructions;

    public Day17() {
        read.from(INPUT);
        read.with(sections -> {
            val registerInstructions = sections.next().lines().toArray(String[]::new);
            for (var i = 0; i < registers.length; i++) {
                registers[i] = parseInt(registerInstructions[i]
                        .substring(registerInstructions[i].lastIndexOf(':') + 2));
            }

            val programInstructions = sections.next().firstLine();
            instructions = parseLongs(programInstructions.substring(
                    programInstructions.lastIndexOf(':') + 2), ",");
        });
    }

    @RequiredArgsConstructor
    enum Instruction {
        adv(operand -> {
            registers[0] = registers[0] >> combo(operand);
            return empty();
        }),
        bxl(operand -> {
            registers[1] = registers[1] ^ operand;
            return empty();
        }),
        bst(operand -> {
            registers[1] = combo(operand) & 0b111;
            return empty();
        }),
        jnz(operand -> {
            if (registers[0] == 0)  return empty();
            return OptionalInt.of((int) operand);
        }),
        bxc(_ -> {
            registers[1] = registers[1] ^ registers[2];
            return empty();
        }),
        out(operand -> {
            outputList.add(combo(operand) & 0b111);
            return empty();
        }),
        bdv(operand -> {
            registers[1] = registers[0] >> combo(operand);
            return empty();
        }),
        cdv(operand -> {
            registers[2] = registers[0] >> combo(operand);
            return empty();
        });

        @Delegate
        private final InstructionExecutor executor;

        static long combo(final long operand) {
            return operand > 3 ? registers[(int) (operand - 4)] : operand;
        }

        static Instruction getByOpcode(final long opcode) {
            return values()[(int) opcode];
        }

    }

    @FunctionalInterface
    interface InstructionExecutor {
        OptionalInt execute(final long operand);
    }

    @Override
    protected void part1() {
        runProgram();

        System.out.println(outputList.stream().map(value -> Long.toString(value)).collect(joining(",")));
    }

    void runProgram() {
        var instructionPointer = 0;
        while (instructionPointer < instructions.length) {
            val instruction = instructions[instructionPointer];
            val operand = instructions[instructionPointer + 1];
            val jump = Instruction.getByOpcode(instruction).execute(operand);
            if (jump.isPresent()) {
                instructionPointer = jump.getAsInt();
            } else {
                instructionPointer += 2;
            }
        }
    }

    @Override
    protected void part2() {
        /* Analyzing the program shows the following:
         *  1. The value of register A is the only value that matters. B and C are set based on A.
         *  2. The program loops until A is 0.
         *  3. Every iteration, the 3 least significant bits are shifted from A.
         *  4. Every iteration outputs exactly 1 number, which is determined based on the value of A.
         * This means that we can determine which value of A would lead to printing the program itself by starting from
         * the back and working our way to the front, constantly shifting A by 3 bits and trying all options for the
         * next part. That is relatively fast.
         */
        System.out.println(backtrack(0, instructions.length - 1, Long.MAX_VALUE));
    }

    long backtrack(final long currentA, final int pos, final long lowestA) {
        return range(0, 8)
                .mapToLong(i -> {
                    val newA = (currentA << 3) + i;
                    resetProgramAndATo(newA);
                    runProgram();
                    return newA;
                })
                .filter(_ -> outputList.getFirst() == instructions[pos])
                .map(newA -> {
                    if (pos == 0) {
                        return min(lowestA, newA);
                    }
                    return min(lowestA, backtrack(newA, pos - 1, lowestA));
                })
                .min()
                .orElse(lowestA);
    }

    void resetProgramAndATo(final long a) {
        outputList.clear();
        registers[0] = a;
        registers[1] = 0;
        registers[2] = 0;
    }

}
