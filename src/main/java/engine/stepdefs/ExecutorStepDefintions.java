package engine.stepdefs;

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import engine.results.FormatUtil;
import engine.results.Result;
import engine.statistics.Statistics;
import engine.tasks.EndOfCycleTask;
import engine.tasks.MultiActionTask;
import engine.tasks.TaskFactory;
import engine.tasks.TaskProxy;
import engine.thread.controllers.StatisticsController;
import engine.thread.executors.AsyncTimedTasksExecutor;
import engine.thread.executors.CountDownLatchExecutor;
import engine.thread.executors.CountDownMetaLatchExecutor;
import engine.thread.executors.CyclicBarrierExecutor;
import engine.stepdefs.tasks.annotations.AsyncTask;
import engine.stepdefs.tasks.annotations.DoFinalize;
import engine.stepdefs.tasks.annotations.DoInit;
import engine.stepdefs.tasks.annotations.DoTask;
import engine.stepdefs.tools.Config;
import engine.stepdefs.tools.StepDefinitionTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import cucumber.api.Scenario;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

/**
 * @author ekaradimas
 * @since 9/2/2015
 */
public class ExecutorStepDefintions {

	private Scenario scenario;

	@Autowired
	FormatUtil formatUtil;

	@Autowired
	Config config;

	@Autowired
	StepDefinitionTools stepDefinitionTools;

	@Autowired
	private AsyncTimedTasksExecutor asyncTimedTasksExecutor;

	@Autowired
	StatisticsController statisticsController;

	@Autowired
	Result result;

	@Autowired
	TaskFactory taskFactory;

	@Lookup
	public MultiActionTask getMultiActionTaskForCyclicBarrier(CyclicBarrier cyclicBarrier) {
		return null;
	}

	@Lookup
	public EndOfCycleTask getEndOfCycleTask() {
		return null;
	}

	@Lookup
	public CyclicBarrierExecutor getCyclicBarrierExecutor(CyclicBarrier cyclicBarrier) {
		return null;
	}

	@Lookup("TaskProxy")
	public TaskProxy getTaskProxy(String taskName, Object executorMethod) {
		return null;
	}

	@Lookup("TaskProxy")
	public TaskProxy getTaskProxy(String taskName) {
		return null;
	}

	@Lookup
	public CountDownLatchExecutor getCountDownLatchExecutor(int threads) {
		return null;
	}

	@Lookup
	public CountDownMetaLatchExecutor getCountDownMetaLatchExecutor(List<CountDownLatchExecutor> countDowns) {
		return null;
	}

	@Before
	public void before(Scenario scenario) {
		this.scenario = scenario;
	}

	@Given("^Start a new test$")
	public void clearSystemStateBeforeTest() {
		System.out.println("#################### NEW SCENARIO START #################");
	}

	@And("^The following configuration is used:$")
	public void Configuration_is_used(DataTable dt) {
		if (stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Cycle users") < dt.getGherkinRows().size()) {
			config.setCycleUsers(Integer.parseInt(dt.getGherkinRows()
					.get(stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Cycle users")).getCells().get(1)));
		}
		if (stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Cycles") < dt.getGherkinRows().size()) {
			config.setCycles(Integer.parseInt(dt.getGherkinRows()
					.get(stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Cycles")).getCells().get(1)));
		}
		if (stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Warm up cycles") < dt.getGherkinRows().size()) {
			config.setWarmupIterations(Integer.parseInt(dt.getGherkinRows()
					.get(stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Warm up cycles")).getCells().get(1)));
		}
		if (stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Failed cycles threshold") < dt.getGherkinRows()
				.size()) {
			config.setFailedThreshold(Integer.parseInt(dt.getGherkinRows()
					.get(stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Failed cycles threshold")).getCells()
					.get(1)));
		}
		if (stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Delay between cycles") < dt.getGherkinRows().size()) {
			config.setDelayBetweenCycles(Long.parseLong(
					dt.getGherkinRows().get(stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Delay between cycles"))
							.getCells().get(1)));
		}
		if (stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Delay between actions") < dt.getGherkinRows().size()) {
			config.setDelayBetweenActions(Long.parseLong(dt.getGherkinRows()
					.get(stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Delay between actions")).getCells()
					.get(1)));
		}
		if (stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Async task delay") < dt.getGherkinRows().size()) {
			asyncTimedTasksExecutor.setReloadConfigRate(dt.getGherkinRows()
					.get(stepDefinitionTools.getPositionOfKeyInDataTable(dt, "Async task delay")).getCells().get(1));
		}

	}

	@When("^Run async tasks in the background continuously$")
	public void Run_Async_Task(DataTable dt) throws Throwable {
		final Map<String, String> annotated = taskFactory.getAnnotated();
		for (int i = 0; i < dt.getGherkinRows().size(); i++) {

			final Object taskInstance = Class
					.forName(annotated.get(dt.getGherkinRows().get(i).getCells().get(0).toString())).newInstance();
			TaskProxy newTask = getTaskProxy(dt.getGherkinRows().get(i).getCells().get(0).toString());
			newTask.setInstance(taskInstance);

			Method[] methods = taskInstance.getClass().getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(AsyncTask.class)) {
					newTask.addAsyncTasks(method);
				}
			}

			if (newTask.getAsyncTasksList().size() > 0) {
				asyncTimedTasksExecutor.register(newTask);
			}

		}
		asyncTimedTasksExecutor.initTimer();
	}

	@When("^Reschedule async tasks$")
	public void Reschedule_Async_Tasks() {
		asyncTimedTasksExecutor.reschedule();
	}

	@When("^Pause async tasks$")
	public void Stop_Async_Tasks() {
		asyncTimedTasksExecutor.stop();
	}

	@When("^Create a cyclic stress test containing the following actions in this order:$")
	public void Cyclic_stress_test_following_actions(DataTable dt) throws Throwable {
		final Map<String, String> annotated = taskFactory.getAnnotated();

		final CyclicBarrier cyclicBarrier = new CyclicBarrier(config.getCycleUsers(), getEndOfCycleTask());

		final MultiActionTask multiActionTask = getMultiActionTaskForCyclicBarrier(cyclicBarrier);

		scenario.write(
				String.format("* Scenario Started at %s \n", formatUtil.millis2Time(System.currentTimeMillis())));

		for (int i = 0; i < dt.getGherkinRows().size(); i++) {
			if (annotated.get(dt.getGherkinRows().get(i).getCells().get(0).toString()) == null) {
				throw new Exception("Task with name " + dt.getGherkinRows().get(i).getCells().get(0).toString()
						+ " not found! Please annotate the relevant task class with " + "@WhenStressTask(\""
						+ dt.getGherkinRows().get(i).getCells().get(0).toString() + "\")");
			} else {
				final Object taskInstance = Class
						.forName(annotated.get(dt.getGherkinRows().get(i).getCells().get(0).toString())).newInstance();
				String taskName = dt.getGherkinRows().get(i).getCells().get(0).toString();
				TaskProxy newTask = (TaskProxy) getTaskProxy(taskName, cyclicBarrier);

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

				multiActionTask.addTask(newTask);
			}
		}

		final CyclicBarrierExecutor cyclicBarrierExecutor = getCyclicBarrierExecutor(cyclicBarrier);
		cyclicBarrierExecutor.setTask(multiActionTask);

		for (int k = 1; k <= config.getCycles() + config.getWarmupIterations(); k++) {
			if (k == config.getWarmupIterations() + 1) {
				statisticsController.reset();
				statisticsController.init();
			}

			cyclicBarrierExecutor.executeThreads();

			if (statisticsController.getTotalFailed() >= config.getFailedThreshold())
				break;

			if (k < config.getCycles() + config.getWarmupIterations())
				Thread.sleep(config.getDelayBetweenCycles());
		}

		// Statistic results
		Statistics finalStats = statisticsController.provide();

		scenario.write(result.giveOutProgress(finalStats));

		result.setStatistics(finalStats);
		result.setEnded(System.currentTimeMillis());

		statisticsController.reset();

		scenario.write(String.format("* Scenario Ended at %s \n", formatUtil.millis2Time(System.currentTimeMillis())));
	}

	@When("^Create a stress test containing the following actions:$")
	public void Stress_test_following_actions(DataTable dt) throws Throwable {
		// TODO: Validate that all tasks names exist before starting threads for
		// all of them

		scenario.write(
				String.format("* Scenario Started at %s \n", formatUtil.millis2Time(System.currentTimeMillis())));

		for (int k = 0; k < config.getCycles() + config.getWarmupIterations(); k++) {
			if (k == config.getWarmupIterations()) {
				statisticsController.reset();
				statisticsController.init();
			}

			List<CountDownLatchExecutor> countDowns = new ArrayList<CountDownLatchExecutor>();
			for (int i = 1; i < dt.getGherkinRows().size(); i++) {

				int threads = Integer.parseInt(dt.getGherkinRows().get(i).getCells().get(1).toString());

				final CountDownLatchExecutor countDownLatchExecutor = getCountDownLatchExecutor(threads);

				countDowns.add(countDownLatchExecutor);

				for (int j = 0; j < threads; j++) {
					countDownLatchExecutor.addTask(dt.getGherkinRows().get(i).getCells().get(0).toString());
				}
			}

			final CountDownMetaLatchExecutor countDownMetaLatch = getCountDownMetaLatchExecutor(countDowns);

			countDownMetaLatch.executeThreads();

			if (k >= config.getWarmupIterations() && !countDownMetaLatch.executionSuccessful())
				statisticsController.plusOneFailed();

			if (statisticsController.getTotalFailed() >= config.getFailedThreshold())
				break;

			Thread.sleep(config.getDelayBetweenCycles());

		}

		// Statistic results
		Statistics finalStats = statisticsController.provide();

		scenario.write(result.giveOutProgress(finalStats));

		result.setStatistics(finalStats);
		result.setEnded(System.currentTimeMillis());

		statisticsController.reset();

		scenario.write(String.format("* Scenario Ended at %s \n", formatUtil.millis2Time(System.currentTimeMillis())));
	}

}