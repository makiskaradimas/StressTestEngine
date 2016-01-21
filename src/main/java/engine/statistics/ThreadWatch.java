package engine.statistics;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ekaradimas
 * @since 19/2/2015
 */
@Service("ThreadWatch")
@Scope("prototype")
public class ThreadWatch {

	/**
	 * Limit of the number of time fractions to keep in memory
	 */
	private static final int LIMIT = 1000;

	/**
	 * The elapsedTime of all running threads
	 */
	private long elapsedTimeMillis = 0;

	/**
	 * Average time of timeFractions as they are recorded into ThreadWatch
	 */
	private long averageTimeMillis = 0;

	/**
	 * Total time (sum of all timeFractions)
	 */
	private long totalTimeMillis = 0;

	/**
	 * Number of timeFractions counted
	 */
	private long timeFractionsCount = 0;

	/**
	 * holds the last end time after calling appendTimeNoOverlaps. This is done
	 * to hold the time the last thread has finished in order not to measure
	 * extra time for the upcoming threads that have started but not finished at
	 * the time of the appendTimeNoOverlaps invocation
	 */
	private long lastEndTimeMillis = 0;

	/**
	 * Map which holds all time fractions from running threads
	 */
	private final Map<String, TimeFraction> timeFractions = new HashMap<String, TimeFraction>();

	/**
	 * Data update control lock
	 */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	/**
	 * Mark the start time for the execution of the current thread. If start is
	 * invoked more than one time, only the first call will have an effect
	 */
	public void start() {
		// outside of execute in order to get the most accurate time and then we
		// wait for the lock
		final long startTime = System.currentTimeMillis();
		executeWithWriteLock(new ExecutionCallback<String>() {
			public String execute() {
				String key = getCurrentExecutionThreadKey();
				TimeFraction p = timeFractions.get(key);
				// We do not want to fill timeFractions for ever, or else memory
				// leak
				// If a thread with the same name exists in the timeFractions
				// then this means an idle thread has been reused (thread pool)
				// therefore clear it out by calling appendTimeNoOverlaps and
				// continue
				if (timeFractions.size() >= LIMIT || p != null) {
					appendTimeNoOverlaps();
				}
				// if still the key exists then start has been called for more
				// than one time
				if (timeFractions.get(key) == null) {
					// put the time fraction with the relevant start time
					timeFractions.put(key, new TimeFraction(key, startTime));
				}
				return key;
			}
		});
	}

	/**
	 * Mark the end time of the current threads execution time. If stop is
	 * invoked more than one time by the same thread only the first call will be
	 * effective
	 */
	public void stop() {
		final long stopTime = System.currentTimeMillis();
		executeWithWriteLock(new ExecutionCallback<String>() {
			public String execute() {
				String key = getCurrentExecutionThreadKey();
				TimeFraction fraction = timeFractions.get(key);
				// if end has already been set then probably stop has been
				// called for
				// more than one time.
				if (fraction != null && fraction.end == 0) {
					fraction.end = stopTime;
				}
				return key;
			}
		});
	}

	/**
	 * Get the elapsed time of all running threads so far.
	 *
	 * @return the time in milliseconds
	 */
	public long getElapsedTime() {
		executeWithWriteLock(new ExecutionCallback<Object>() {
			public Object execute() {
				appendTimeNoOverlaps();
				return "";
			}
		});
		return executeWithReadLock(new ExecutionCallback<Long>() {
			public Long execute() {
				return elapsedTimeMillis;
			}
		});
	}

	public long getAverageTime() {
		executeWithWriteLock(new ExecutionCallback<Object>() {
			public Object execute() {
				appendTimeNoOverlaps();
				return "";
			}
		});
		return executeWithReadLock(new ExecutionCallback<Long>() {
			public Long execute() {
				return averageTimeMillis;
			}
		});
	}

	/**
	 * Set elapsed time to zero, reset this ThreadWatch instance
	 */
	public void reset() {
		executeWithWriteLock(new ExecutionCallback<Object>() {
			public Object execute() {

				elapsedTimeMillis = 0;
				averageTimeMillis = 0;

				totalTimeMillis = 0;
				timeFractionsCount = 0;

				timeFractions.clear();
				lastEndTimeMillis = 0;

				return "";
			}
		});
	}

	private String getCurrentExecutionThreadKey() {
		return Thread.currentThread().getId() + Thread.currentThread().getName();
	}

	/**
	 * This is a private method to calculate the overall time but without any
	 * overlaps from thread execution. The result is added to total elapsed time
	 */
	private void appendTimeNoOverlaps() {
		if (timeFractions.isEmpty())
			return;

		// Sort by start time
		List<TimeFraction> sortedTimeFractions = new ArrayList<TimeFraction>(timeFractions.values());
		Collections.sort(sortedTimeFractions, new Comparator<TimeFraction>() {
			public int compare(TimeFraction t1, TimeFraction t2) {
				return t1.compareTo(t2);
			}
		});

		long start = 0, end = 0;
		Set<String> removeFinishedKeys = new HashSet<String>();

		for (TimeFraction tp : sortedTimeFractions) {
			if (tp.start != 0 && tp.end != 0) { // start, stop has been called

				timeFractionsCount++;
				totalTimeMillis += tp.getDurationMillis();

				removeFinishedKeys.add(tp.uuid);

				// setup start
				if (start == 0 || end < tp.start) {
					start = tp.start;
				} else {
					start = end;
				}
				if (start < lastEndTimeMillis) {
					start = lastEndTimeMillis;
				}

				// setup end
				if (end == 0 || end < tp.end) {
					end = tp.end;
				} else {
					// end stays as is
				}
				if (end < lastEndTimeMillis) {
					end = lastEndTimeMillis;
				}

				if (end - start < 0) {
					throw new IllegalStateException("cannot be negative");
				}

				// sum
				elapsedTimeMillis += end - start;
			}
		}
		lastEndTimeMillis = end;
		for (String k : removeFinishedKeys) {
			timeFractions.remove(k);
		}

		if (timeFractionsCount > 0) {
			averageTimeMillis = totalTimeMillis / timeFractionsCount;
		}
	}

	/**
	 * This internal class holds the time of a fraction
	 */
	class TimeFraction implements Comparable<TimeFraction> {

		String uuid;
		long start = 0, end = 0;

		public TimeFraction(String uuid, long start) {
			this.uuid = uuid;
			this.start = start;
		}

		public int compareTo(TimeFraction other) {
			if (this.start < other.start) {
				return -1;
			} else if (this.start > other.start) {
				return 1;
			} else {
				return 0;
			}
		}

		public long getDurationMillis() {
			return end - start;
		}

		public String toString() {
			return "TimePeriod{" + "uuid='" + uuid + '\'' + ", start=" + start + ", end=" + end + '}';
		}
	}

	/**
	 * This interface will perform a callback withing the context of a
	 * read/write lock
	 */
	private interface ExecutionCallback<T> {
		T execute();
	}

	private <T> T executeWithReadLock(ExecutionCallback<T> callback) {
		try {
			lock.readLock().lock();
			return callback.execute();
		} finally {
			lock.readLock().unlock();
		}
	}

	private <T> T executeWithWriteLock(ExecutionCallback<T> callback) {
		try {
			lock.writeLock().lock();
			return callback.execute();
		} finally {
			lock.writeLock().unlock();
		}
	}
}