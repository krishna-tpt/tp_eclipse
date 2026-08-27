package org.michelin.filemanager.tools.util;

import java.util.List;
import java.util.Random;

public class Rng {

    private final Random rnd;

    public Rng(long seed) {
        this.rnd = new Random(seed);
    }

    public int nextInt(int bound) {
        return rnd.nextInt(bound);
    }

    public int nextIntBetween(int minInclusive, int maxInclusive) {
        if (maxInclusive < minInclusive) {
            throw new IllegalArgumentException("max < min");
        }
        return minInclusive + rnd.nextInt(maxInclusive - minInclusive + 1);
    }

    public double nextDouble() {
        return rnd.nextDouble();
    }

    public <T> T pick(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("cannot pick from empty list");
        }
        return items.get(rnd.nextInt(items.size()));
    }

    /** Returns a fresh Random seeded from this Rng. Useful for deterministic sub-streams. */
    public Random childRandom() {
        return new Random(rnd.nextLong());
    }
}
