package engine.statistics;

import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author ekaradimas
 * @since 19/2/2015
 */
@Component("StatisticsHolder")
public class StatisticsHolder implements Serializable {

	private static final long serialVersionUID = -794865055280979190L;

	private volatile long iterations = 0;
	private volatile long failed = 0;

	private long startedTime;
	private long endedTime;

	private long elapsedTime;
	private long averageTime;

	public long getIterations() {
		return iterations;
	}

	public long getFailed() {
		return failed;
	}

	public void plusOneIteration() {
		this.iterations++;
	}

	public void plusOneFailed() {
		this.failed++;
	}

	public long getAverageTime() {
		return averageTime;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public void setAverageTime(long averageTime) {
		this.averageTime = averageTime;
	}

	public long getStartedTime() {
		return startedTime;
	}

	public void setStartedTime(long startedTime) {
		this.startedTime = startedTime;
	}

	public long getEndedTime() {
		return endedTime;
	}

	public void setEndedTime(long endedTime) {
		this.endedTime = endedTime;
	}

	public void reset() {

		this.iterations = 0;
		this.failed = 0;

		this.startedTime = 0;
		this.endedTime = 0;

		this.elapsedTime = 0;
		this.averageTime = 0;
	}
}
