package engine.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import engine.stepdefs.tools.Config;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author ekaradimas
 * @since 13/2/2015
 */
@Service("StatisticsCalculator")
@Scope("prototype")
public class StatisticsCalculator {

	@Autowired
	Config config;

	public Statistics calculate(StatisticsHolder... stats) {
		Statistics result = new Statistics();
		for (StatisticsHolder statisticsHolder : stats) {

			result.addIterations(statisticsHolder.getIterations());
			result.addFailed(statisticsHolder.getFailed());

			// Throughput
			long totalDurationMillis = statisticsHolder.getElapsedTime();
			if (totalDurationMillis > 0) {
				double throughput = new BigDecimal(
						(double) (statisticsHolder.getIterations() * 1000) / (double) totalDurationMillis)
								.setScale(2, RoundingMode.HALF_UP).doubleValue();

				result.setThroughput(result.getThroughput() + throughput);
			}

			// Progress
			long totalIterations = config.getCycleUsers() * config.getCycles();

			double progress = new BigDecimal((double) statisticsHolder.getIterations() / ((double) totalIterations))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();

			result.setProgress(result.getProgress() + progress);

			result.setResponseTime(result.getResponseTime() + statisticsHolder.getAverageTime());
			result.setStartedTime(result.getStartedTime() + statisticsHolder.getStartedTime());
		}

		// average
		result.setThroughput(
				new BigDecimal(result.getThroughput() / stats.length).setScale(2, RoundingMode.HALF_UP).doubleValue());
		result.setProgress(
				new BigDecimal(result.getProgress() / stats.length).setScale(2, RoundingMode.HALF_UP).doubleValue());
		result.setResponseTime(result.getResponseTime() / stats.length);
		result.setStartedTime(result.getStartedTime() / stats.length);
		return result;
	}

}
