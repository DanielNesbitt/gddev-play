package util;

import java.lang.Integer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.nCopies;

/**
 * @author James Cooper
 */
public final class StatBag {

	// -------------------- Statics --------------------

	private static final int LIMIT = 10;

	// -------------------- Variables --------------------

	private final Map<String, Deque<Integer>> counts = new ConcurrentHashMap<>();

	// -------------------- Public --------------------

	public final List<String> add(Map<String, Integer> tweets) {
        List<String> removals = new ArrayList<>();
		tweets.forEach((k,v) -> push(counts.computeIfAbsent(k, s -> new ArrayDeque<>(nCopies(LIMIT, 0))), v));
		counts.entrySet().removeIf(e -> {
			if (!tweets.containsKey(e.getKey())) {
				push(e.getValue(), 0);
                if (!e.getValue().stream().filter(i -> i > 0).findAny().isPresent()) {
                    removals.add(e.getKey());
                    return true;
                }
			}
			return false;
		});
        return removals;
    }

    public final Object[] columns() {
        return counts.entrySet().stream()
            .map(e -> {
                List<Object> column = new ArrayList<>();
                column.add(e.getKey());
                column.addAll(e.getValue());
                return column.toArray();
            }).toArray();
    }

    // -------------------- Private --------------------

    private void push(Deque<Integer> deque, int value) {
		deque.add(value);
		deque.removeFirst();
	}

}
