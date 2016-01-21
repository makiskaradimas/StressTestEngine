package engine.thread.executors;

import engine.stepdefs.tools.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import engine.tasks.MultiActionTask;
import engine.thread.controllers.StatisticsController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * @author ekaradimas
 * @since 17/2/2015
 */
@Service("CyclicBarrierExecutor")
@Scope("prototype")
public class CyclicBarrierExecutor {

	private MultiActionTask task;
	private CyclicBarrier barrier;

	@Autowired
	Config config;

	@Autowired
	StatisticsController statisticsController;

	@Lookup
	public MultiActionTask getMultiActionTask(MultiActionTask task) {
		return null;
	}

	public CyclicBarrierExecutor(CyclicBarrier barrier) {
		this.barrier = barrier;
	}

	public void setTask(MultiActionTask task) {
		this.task = task;
	}

	public void executeThreads() throws Throwable {
		List<MultiActionTask> tasks = new ArrayList<MultiActionTask>();
		for (int i = 0; i < barrier.getParties(); i++) {

			final MultiActionTask newTask = getMultiActionTask(task);

			tasks.add(newTask);

			newTask.start();
		}

		// wait at barrier
		for (MultiActionTask task : tasks)
			task.join();

		for (MultiActionTask task : tasks) {
			if (!task.isActionSuccessful()) {
				statisticsController.plusOneFailed();
				break;
			}
		}
	}
}
