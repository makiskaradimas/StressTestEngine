package engine.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import engine.thread.controllers.StatisticsController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * @author ekaradimas
 * @since 12/2/2015
 */
@Service("MultiActionTask")
@Scope("prototype")
public class MultiActionTask extends TaskProxy {

	// TODO: Multiaction task with latch

	private List<TaskProxy> taskProxies;

	@Autowired
	StatisticsController statisticsController;

	public MultiActionTask() {
	}

	public MultiActionTask(CyclicBarrier barrier) {
		super("Multiple Task", barrier);
		taskProxies = new ArrayList<TaskProxy>();
	}

	public MultiActionTask(MultiActionTask multiActionTask) {
		super("Multiple Task", multiActionTask.getBarrier());
		taskProxies = new ArrayList<TaskProxy>();
		for (TaskProxy taskProxy : multiActionTask.taskProxies) {
			addTask(taskProxy);
		}
	}

	public void addTask(TaskProxy task) {
		taskProxies.add(task);
	}

	public void doInit() throws Exception {
		for (TaskProxy taskProxy : taskProxies) {
			taskProxy.doInit();
		}
	}

	@Override
	public void doTask() throws Exception {
		int i = 0;
		for (TaskProxy taskProxy : taskProxies) {
			taskProxy.doTask();
			i++;
			if (i < taskProxies.size())
				Thread.sleep(config.getDelayBetweenActions());
		}
	}

	public void doFinalize() throws Exception {
		for (TaskProxy taskProxy : taskProxies) {
			taskProxy.doFinalize();
		}
	}
}
