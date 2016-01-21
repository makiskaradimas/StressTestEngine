package engine.thread.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import engine.statistics.Statistics;
import engine.statistics.StatisticsCalculator;
import engine.statistics.StatisticsHolder;
import engine.statistics.ThreadWatch;

/**
 * @author ekaradimas
 * @since 12/2/2015
 */
@Component("StatisticsController")
public class StatisticsController {

	/** This holder keeps the statistics for the specific executor */
	private final StatisticsHolder holder = new StatisticsHolder();

	@Autowired
	private ThreadWatch requestWatcher;
	@Autowired
	private ThreadWatch taskWatcher;
	@Autowired
	private StatisticsCalculator statsCalculator;

	public void plusOneIteration() {
		this.holder.plusOneIteration();
	}

	public void plusOneFailed() {
		this.holder.plusOneFailed();
	}

	public long getTotalFailed() {
		return this.holder.getFailed();
	}

	public long getTotalIterations() {
		return this.holder.getIterations();
	}

	public void requestWatcherStart() {
		requestWatcher.start();
	}

	public void requestWatcherStop() {
		requestWatcher.stop();
	}

	// provide statistics
	public Statistics provide() {
		Statistics statistics;

		// add throughputWatcher results
		holder.setElapsedTime(requestWatcher.getElapsedTime());

		// average time of task execution
		holder.setAverageTime(taskWatcher.getAverageTime());

		statistics = statsCalculator.calculate(holder);

		return statistics;
	}

	public void taskWatcherStart() {
		taskWatcher.start();
	}

	public void taskWatcherStop() {
		taskWatcher.stop();
	}

	public void reset() {
		requestWatcher.reset();
		taskWatcher.reset();
		holder.reset();
	}

	public void init() {
		this.holder.setStartedTime(System.currentTimeMillis());
	}

	public long getStartTime() {
		return this.holder.getStartedTime();
	}
}