package engine.thread.executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import engine.results.FormatUtil;
import engine.results.Result;
import engine.statistics.Statistics;
import engine.tasks.TaskProxy;
import engine.thread.controllers.StatisticsController;
import engine.thread.controllers.ThroughputController;

import java.util.*;

/**
 * @author ekaradimas
 * @since 17/2/2015
 */
@Component("AsyncTimedTasksExecutor")
public class AsyncTimedTasksExecutor {
    private final Logger log = LogManager.getLogger(getClass());

    @Value("${reloadConfigRate:1000}")
    private String reloadConfigRate;

    @Autowired
    private FormatUtil formatUtil;

	@Autowired
	StatisticsController statisticsController;

	@Autowired
	ThroughputController throughputController;

	@Autowired
	Result result;

	private final Set<TaskProxy> tasks = new LinkedHashSet<TaskProxy>();

	private final Timer timer = new Timer();

	private TimerTask timerTask;

	public void initTimer() {

		timerTask = new TimerTask() {
			public void run() {
				if (tasks.isEmpty()) {

					return;
				}
				log.trace("Executing timed Task...");
				long start = System.currentTimeMillis();
				for (TaskProxy t : tasks) {
					try {
						t.doAsyncTask();
					} catch (Exception e) {
						System.out.println("Async Tasks initTimer(): " + e.getMessage());
					}
				}
				long end = System.currentTimeMillis();
				log.trace(String.format("Executing timed Task...OK (took %s)", formatUtil.getOverallExecutionTime(end - start)));
			}
		};

		this.timer.schedule(timerTask, new Date(), Long.valueOf(reloadConfigRate));
	}

	public void register(TaskProxy task) {
		tasks.add(task);
	}

	public void stop() {
		timerTask.cancel();
	}

	public void reschedule() {
		timerTask = new TimerTask() {
			public void run() {
				if (tasks.isEmpty()) {

					return;
				}
				log.trace("Executing timed Task...");
				long start = System.currentTimeMillis();
				for (TaskProxy t : tasks) {
					try {
						t.doAsyncTask();
					} catch (Exception e) {
						System.out.println("Async Tasks initTimer(): " + e.getMessage());
					}
				}
				long end = System.currentTimeMillis();
				log.trace(String.format("Executing timed Task...OK (took %s)", formatUtil.getOverallExecutionTime(end - start)));
			}
		};
		timer.schedule(timerTask, new Date(), Long.valueOf(reloadConfigRate));
	}

	/** Method to add actions to be executed per interval on an async manner */
	public void asyncTimedTasks() {

		// Log statistics and print output
		Statistics statistics = statisticsController.provide();

		result.printOutProgress(statistics);
	}

	public String getReloadConfigRate() {
		return reloadConfigRate;
	}

	public void setReloadConfigRate(String reloadConfigRate) {
		this.reloadConfigRate = reloadConfigRate;
	}

}
