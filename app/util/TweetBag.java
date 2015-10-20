package util;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author James Cooper
 */
public final class TweetBag {

    // -------------------- Statics --------------------

    private static final long EXPIRATION = 5 * 60_000;

    // -------------------- Variables --------------------

    private final Map<String, Counter> map = new ConcurrentHashMap<>();
    private int mutationCounter = 0;

    // -------------------- Public --------------------

    public final void addTag(String tag) {
        map.compute(tag, (k, v) -> {
            if (v == null) {
                v = new Counter(k);
            }
            v.inc();
            return v;
        });
        if (++mutationCounter % 5000 == 0) {
            map.values().removeIf(Counter::trim);
        }
    }

    public final Map<String, Integer> tweetCounts(int skip, int limit) {
        AtomicInteger position = new AtomicInteger(skip + 1);
        return map.values().stream()
            .map(Counter::toTag)
            .sorted(Comparator.comparingInt(HashTag::count).reversed())
            .skip(skip)
            .limit(limit)
            .collect(Collectors.toMap(HashTag::tag, HashTag::count, (l, r) -> l, LinkedHashMap::new));
    }

    // -------------------- Private --------------------

    private static final class Counter {
        private final String tag;
        private final Deque<Long> expirationTimes = new ArrayDeque<>();

        public Counter(String tag) {
            this.tag = tag;
        }

        final synchronized void inc() {
            expirationTimes.add(System.currentTimeMillis() + EXPIRATION);
        }

        final HashTag toTag() {
            return new HashTag(tag, count());
        }

        final boolean trim() {
            return count() == 0;
        }

        private synchronized int count() {
            long now = System.currentTimeMillis();
            Long expiration;
            while ((expiration = expirationTimes.peekFirst()) != null) {
                if (now > expiration) {
                    expirationTimes.removeFirst();
                } else {
                    break;
                }
            }
            return expirationTimes.size();
        }
    }

    private static final class HashTag {
        private final String tag;
        private final int count;

        HashTag(String tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        public String tag() {
            return tag;
        }

        public int count() {
            return count;
        }
    }
}
