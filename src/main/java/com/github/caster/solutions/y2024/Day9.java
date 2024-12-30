package com.github.caster.solutions.y2024;

import com.github.caster.shared.BaseSolution;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.caster.shared.input.InputLoader.InputType.INPUT;
import static com.github.caster.shared.stream.StreamUtils.iterateIndicesOf;
import static java.util.stream.IntStream.range;

public final class Day9 extends BaseSolution {

    public Day9() {
        read.from(INPUT);
    }

    @Override
    protected void part1() {
        val input = read.firstLine().chars().map(c -> c - '0').toArray();
        val totalDiskSize = Arrays.stream(input).sum();
        val disk = new char[totalDiskSize];
        val currDiskIndex = new AtomicInteger();
        for (var i = 0; i < input.length; i++) {
            val finalI = i;
            range(0, input[i]).forEach(_ ->
                    disk[currDiskIndex.getAndIncrement()] = finalI % 2 == 0 ? (char) ('0' + (finalI / 2)) : '.'
            );
        }

        int l = 0, r = disk.length - 1;
        while (l < r) {
            if (disk[l] != '.') {
                l++;
                continue;
            }
            if (disk[r] == '.') {
                r--;
                continue;
            }
            disk[l] = disk[r];
            disk[r] = '.';
            l++;
            r--;
        }

        var sum = 0L;
        for (var i = 1; i < disk.length; i++) {
            if (disk[i] == '.')  continue;
            sum += (long) i * (disk[i] - '0');
        }
        System.out.println(sum);
    }

    interface SpaceOnDisk {
        int size();
        int start();
        long checksum();
    }

    record FileOnDisk(long id, int size, int start) implements SpaceOnDisk {

        @Override
        public long checksum() {
            return range(start, start + size).mapToLong(i -> id * i).sum();
        }

    }

    record GapOnDisk(int size, int start) implements SpaceOnDisk {

        @Override
        public long checksum() {
            return 0L;
        }

    }

    @Override
    protected void part2() {
        val input = read.firstLine().chars().map(c -> c - '0').toArray();
        val spaces = new ArrayList<SpaceOnDisk>();
        var lastBlockStart = 0;
        for (var i = 0; i < input.length; i++) {
            if (input[i] == 0)  continue;
            if (i % 2 == 0) {
                spaces.add(new FileOnDisk(i / 2, input[i], lastBlockStart));
            } else {
                spaces.add(new GapOnDisk(input[i], lastBlockStart));
            }
            lastBlockStart += input[i];
        }

        range(0, input.length / 2 + 1).map(i -> input.length / 2 - i).forEach(fileId -> {
            // find file on disk
            val fileIndex = iterateIndicesOf(spaces)
                    .filter(i -> spaces.get(i) instanceof final FileOnDisk f && f.id() == fileId)
                    .findFirst()
                    .orElseThrow();
            val file = (FileOnDisk) spaces.get(fileIndex);

            // find space to move into
            val fittingGapIndexOptional = iterateIndicesOf(spaces)
                    .filter(i -> spaces.get(i) instanceof final GapOnDisk g
                            && g.size >= file.size()
                            && g.start() < file.start())
                    .findFirst();
            if (fittingGapIndexOptional.isEmpty())  return;
            val fittingGapIndex = fittingGapIndexOptional.getAsInt();
            val fittingGap = (GapOnDisk) spaces.get(fittingGapIndex);

            // remove file from disk
            spaces.remove(fileIndex);
            if (spaces.get(fileIndex - 1) instanceof final GapOnDisk gap) {
                if (spaces.get(fileIndex) instanceof final GapOnDisk gap2) {
                    spaces.remove(fileIndex);
                    spaces.remove(fileIndex - 1);
                    spaces.add(fileIndex - 1,
                            new GapOnDisk(gap.size() + gap2.size() + file.size(), gap.start()));
                } else {
                    spaces.remove(fileIndex - 1);
                    spaces.add(fileIndex - 1,
                            new GapOnDisk(gap.size() + file.size(), gap.start()));
                }
            } else {
                spaces.add(fileIndex, new GapOnDisk(file.size(), file.start()));
            }

            // move file into gap
            spaces.remove(fittingGapIndex);
            spaces.add(fittingGapIndex, new FileOnDisk(file.id(), file.size(), fittingGap.start()));
            if (file.size() < fittingGap.size()) {
                spaces.add(fittingGapIndex + 1, new GapOnDisk(fittingGap.size() - file.size(), fittingGap.start() + file.size()));
            }
        });

        System.out.println(spaces.stream().mapToLong(SpaceOnDisk::checksum).sum());
    }

}
