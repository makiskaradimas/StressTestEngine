package engine.stepdefs.tasks;

import engine.stepdefs.tasks.annotations.*;

/**
 * @author ekaradimas
 * @since 19/2/2015
 */
@WhenStressTask("Redeem Code")
public class RedeemCode {
	@DoInit
	public void doInit() {
		System.out.println("Starting Redeem Code");
	}

	@DoTask
	public void doTask() {
		System.out.println("Redeem Code");
	}

	@DoFinalize
	public void doFinalize() {
		System.out.println("Finishing Redeem Code");
	}

	@AsyncTask
	public void doAsyncTask1() {
		System.out.println("Redeem Code Async Task 1");
	}
}
