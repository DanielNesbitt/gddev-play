package util;

import java.lang.Integer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
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

	public final void add(Map<String, Integer> tweets) {
		tweets.forEach((k,v) -> push(counts.computeIfAbsent(k, s -> new ArrayDeque<>(nCopies(LIMIT, 0))), v));
		counts.entrySet().removeIf(e -> {
			if (!tweets.containsKey(e.getKey())) {
				push(e.getValue(), 0);
				return !e.getValue().stream().filter(i -> i > 0).findAny().isPresent();
			}
			return false;
		});
	}

	private void push(Deque<Integer> deque, int value) {
		deque.add(value);
		deque.removeFirst();
	}

}
