package engine.tasks;

import engine.stepdefs.tools.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import engine.thread.controllers.StatisticsController;
import engine.thread.controllers.ThroughputController;
import engine.thread.executors.AsyncTimedTasksExecutor;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author ekaradimas
 * @since 19/2/2015
 */
@Service("TaskProxy")
@Scope("prototype")
public class TaskProxy extends Thread {

	private Object executorMethod;
	private String actionName;
	private boolean actionSuccessful;

	@Autowired
	Config config;

	@Autowired
	ThroughputController throughputController;

	@Autowired
	StatisticsController statisticsController;

	@Autowired
	AsyncTimedTasksExecutor asyncTimedTasksExecutor;

	public TaskProxy() {
	}

	public TaskProxy(String actionName) {
		super();
		this.actionName = actionName;
		this.actionSuccessful = true;
	}

	public TaskProxy(String actionName, Object executorMethod) {
		super();
		this.executorMethod = executorMethod;
		this.actionName = actionName;
		this.actionSuccessful = true;
	}

	@Override
	public void run() {

		try {
			doInit();
		} catch (Exception e) {
			actionSuccessful = false;
			e.printStackTrace(System.err);
		}

		statisticsController.requestWatcherStart();

		try {
			statisticsController.taskWatcherStart();
			// Thread.sleep(1000);
			doTask();
			statisticsController.plusOneIteration();
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			actionSuccessful = false;
		} finally {
			statisticsController.taskWatcherStop();

			try {
				doFinalize();
			} catch (Exception e) {
				actionSuccessful = false;
				e.printStackTrace(System.err);
			}

			if (executorMethod instanceof CountDownLatch) {
				CountDownLatch latch = (CountDownLatch) executorMethod;
				latch.countDown();
			} else if (executorMethod instanceof CyclicBarrier) {
				CyclicBarrier barrier = (CyclicBarrier) executorMethod;
				try {
					barrier.await();
				} catch (InterruptedException ex) {
					actionSuccessful = false;
					ex.printStackTrace(System.err);
				} catch (BrokenBarrierException ex) {
					actionSuccessful = false;
					ex.printStackTrace(System.err);
				}
			}
		}
		// measure time of iteration
		statisticsController.requestWatcherStop();
	}

	public CyclicBarrier getBarrier() {
		if (executorMethod instanceof CyclicBarrier)
			return (CyclicBarrier) executorMethod;
		else
			return null;
	}

	public String getActionName() {
		return actionName;
	}

	public boolean isActionSuccessful() {
		return actionSuccessful;
	}

	private Method doInit;
	private Method doTask;
	private Method doFinalize;
	private Object instance;

	private final Set<Method> asyncTasksList = new LinkedHashSet<Method>();

	/**
	 * Method to perform the asynchronous tasks configured
	 *
	 * @throws Exception
	 */
	public void doAsyncTask() throws Exception {
		if (asyncTasksList.isEmpty()) {

			return;
		}
		for (Method m : asyncTasksList) {
			m.invoke(instance);
		}
	}

	/**
	 * Method to perform initialization tasks during task execution, this method
	 * is invoked once when the task starts
	 *
	 * @throws Exception
	 */
	public void doInit() throws Exception {
		if (doInit == null) {

			return;
		}
		doInit.invoke(instance);
	}

	/**
	 * Performs the actual task. This method is executed in its own thread. The
	 * rate of invoking this method controls the rate of the blaster
	 *
	 * @throws IllegalStateException
	 *             if task is null
	 * @throws Exception
	 */
	public void doTask() throws Exception {
		if (doTask == null) {
			throw new IllegalStateException(String
					.format("Task is not found for '%s', you should have a method annotated with @DoTask", actionName));
		}
		doTask.invoke(instance);
	}

	/**
	 * Do finalization operations for task, this method is useful if for
	 * instance you want to printout a summarization of the task, or store the
	 * results of the task etc.
	 *
	 * @throws Exception
	 */
	public void doFinalize() throws Exception {
		if (doFinalize == null) {

			return;
		}
		doFinalize.invoke(instance);
	}

	public String toString() {
		return "BlasterTaskProxy{" + "name='" + actionName + '\'' + ", doInit=" + doInit + ", doTask=" + doTask
				+ ", doFinalize=" + doFinalize +
				// ", instance=" + instance +
				"} " + super.toString();
	}

	public void setDoInit(Method doInit) {
		this.doInit = doInit;
	}

	public Method getDoInit() {
		return doInit;
	}

	public Method getDoTask() {
		return doTask;
	}

	public void setDoTask(Method doTask) {
		this.doTask = doTask;
	}

	public Method getDoFinalize() {
		return doFinalize;
	}

	public void setDoFinalize(Method doFinalize) {
		this.doFinalize = doFinalize;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Set<Method> getAsyncTasksList() {
		return asyncTasksList;
	}

	public void addAsyncTasks(Method async) {
		asyncTasksList.add(async);
	}
}
