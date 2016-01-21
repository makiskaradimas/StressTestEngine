package engine.statistics;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ekaradimas
 * @since 13/2/2015
 */
public class Statistics implements Serializable {

	private static final long serialVersionUID = 8092760848212584396L;

	/** Time */
	private Timestamp time;
	/** Total iterations */
	private long iterations;
	/** Total Failed iterations */
	private long failed;
	/** Throughput */
	private Double throughput = 0.0;
	/** Average response time in milliseconds */
	private long responseTime = 0;
	/** Progress */
	private Double progress = 0.0;
	/** JMX info if available */
	private Map<String, JmxInfo> jmxInfo = new LinkedHashMap<String, JmxInfo>();
	/** Extra information */
	private Map<String, Object> extra = new LinkedHashMap<String, Object>();
	/** Running time */
	private long startedTime = 0;

	public Statistics() {

	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public Double getThroughput() {
		return throughput;
	}

	public void setThroughput(Double throughput) {
		this.throughput = throughput;
	}

	public Double getProgress() {
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public long getIterations() {
		return iterations;
	}

	public long getFailed() {
		return failed;
	}

	public void addIterations(long iterations) {
		this.iterations += iterations;
	}

	public void addFailed(long failed) {
		this.failed += failed;
	}

	public Map<String, JmxInfo> getJmxInfo() {
		return jmxInfo;
	}

	public void setJmxInfo(Map<String, JmxInfo> jmxInfo) {
		this.jmxInfo = jmxInfo;
	}

	public String toString() {
		return "Statistics{" + "iterations=" + iterations + ", failed=" + failed + ", throughput=" + throughput
				+ ", responseTime=" + responseTime + ", progress=" + progress + ", jmxInfo=" + jmxInfo + ", extra='"
				+ extra + "'\'}";
	}

	public void putAllExtra(Map<String, Object> extra) {
		this.extra.putAll(extra);
	}

	public long getStartedTime() {
		return startedTime;
	}

	public void setStartedTime(long time) {
		this.startedTime = time;
	}
}