package engine.stepdefs.tools;

import org.springframework.stereotype.Component;

/**
 * @author ekaradimas
 * @since 16/2/2015
 */
@Component
public class Config {
	private int cycleUsers;
	private int step;
	private int iterations;
	private int cycles;
	private int warmupIterations;
	private int failedThreshold;
	private boolean syncMode;
	private long timeout;
	private long delayBetweenCycles;
	private long delayBetweenActions;

	public int getCycleUsers() {
		return cycleUsers;
	}

	public int getWarmupIterations() {
		return warmupIterations;
	}

	public int getFailedThreshold() {
		return failedThreshold;
	}

	public int getStep() {
		return step;
	}

	public int getIterations() {
		return iterations;
	}

	public void setCycles(int cycles) {
		this.cycles = cycles;
	}

	public int getCycles() {
		return cycles;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean isSyncMode() {
		return syncMode;
	}

	public void setSyncMode(boolean syncMode) {
		this.syncMode = syncMode;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public void setCycleUsers(int cycleUsers) {
		this.cycleUsers = cycleUsers;
	}

	public void setWarmupIterations(int warmupIterations) {
		this.warmupIterations = warmupIterations;
	}

	public void setFailedThreshold(int failedThreshold) {
		this.failedThreshold = failedThreshold;
	}

	public long getDelayBetweenCycles() {
		return delayBetweenCycles;
	}

	public long getDelayBetweenActions() {
		return delayBetweenActions;
	}

	public void setDelayBetweenCycles(long delayBetweenCycles) {
		this.delayBetweenCycles = delayBetweenCycles;
	}

	public void setDelayBetweenActions(long delayBetweenActions) {
		this.delayBetweenActions = delayBetweenActions;
	}
}
