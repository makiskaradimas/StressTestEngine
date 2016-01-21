package engine.thread.executors;

import engine.stepdefs.tasks.annotations.DoFinalize;
import engine.stepdefs.tasks.annotations.DoInit;
import engine.stepdefs.tasks.annotations.DoTask;
import engine.stepdefs.tools.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import engine.tasks.TaskFactory;
import engine.tasks.TaskProxy;
import engine.thread.controllers.StatisticsController;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author ekaradimas
 * @since 6/2/2015
 */
@Service("CountDownLatchExecutor")
@Scope("prototype")
public class CountDownLatchExecutor {

	private CountDownLatch latch;

	@Autowired
	Config config;

	@Autowired
	StatisticsController statisticsController;

	@Autowired
	TaskFactory taskFactory;

	@Autowired
	AsyncTimedTasksExecutor asyncTimedTasksExecutor;

	@Lookup("TaskProxy")
	public TaskProxy getTaskProxyForCountDownLatch(String taskName, CountDownLatch latch) {
		return null;
	}

	public CountDownLatchExecutor(int threads) {
		latch = new CountDownLatch(threads);
		taskProxies = new ArrayList<TaskProxy>();
	}

	private List<TaskProxy> taskProxies;

	public void executeThreads() {
		final ExecutorService executor = Executors.newFixedThreadPool(taskProxies.size());

		for (final TaskProxy v : taskProxies) {
			executor.execute(v);
		}
	}

	public void addTask(String task) {
		try {
			final Map<String, String> annotated = taskFactory.getAnnotated();

			if (annotated.get(task) == null) {
				throw new Exception(
						"Task with name " + task + " not found! Please annotate the relevant task class with "
								+ "@WhenStressTask(\"" + task + "\")");
			}

			final Object taskInstance = Class.forName(annotated.get(task)).newInstance();

			TaskProxy newTask = getTaskProxyForCountDownLatch(task, latch);
			newTask.setInstance(taskInstance);

			Method[] methods = taskInstance.getClass().getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(DoInit.class))
					newTask.setDoInit(method);
				else if (method.isAnnotationPresent(DoTask.class))
					newTask.setDoTask(method);
				else if (method.isAnnotationPresent(DoFinalize.class))
					newTask.setDoFinalize(method);
			}

			taskProxies.add(newTask);
		} catch (Exception e) {
			System.out.println("addTask(): " + e);
		}
	}

	public void latchAwait() throws Exception {
		latch.await();
	}

	public boolean latchAwait(long timeout, TimeUnit unit) throws Exception {
		return latch.await(timeout, unit);
	}

	public boolean threadsSuccessful() {
		for (final TaskProxy v : taskProxies) {
			if (!v.isActionSuccessful()) {
				return false;
			}
		}
		return true;
	}
}
