package engine.stepdefs.tasks;

import engine.stepdefs.tasks.annotations.*;

/**
 * @author ekaradimas
 * @since 19/2/2015
 */
@WhenStressTask("Send MO")
public class SendMO {
    @DoInit
    public void doInit() {
        System.out.println("Starting Send MO");
    }
    @DoTask
    public void doTask() {
        System.out.println("Send MO");
    }
    @DoFinalize
    public void doFinalize() {
        System.out.println("Finishing Send MO");
    }
    @AsyncTask
    public void doAsyncTask1() { System.out.println("Send MO Async Task 1"); }
    @AsyncTask
    public void doAsyncTask2() { System.out.println("Send MO Async Task 2"); }
}
