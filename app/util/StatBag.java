package gddev;

import java.lang.Integer;
import java.util.ArrayDeque;
import static java.util.Collections.nCopies;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
		tweets.forEach((k,v) -> counts.computeIfAbsent(k, s -> new ArrayDeque<>(nCopies(LIMIT - 1, 0))).add(v));
		counts.entrySet().removeIf(e -> {
			if (!tweets.containsKey(e.getKey())) {
				Deque<Integer> deque = e.getValue();
				deque.add(0);
				deque.removeFirst();
				return !deque.stream().filter(i -> i > 0).findAny().isPresent();
			}
			return false;
		});
	}

}
