package engine.statistics;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author ekaradimas
 * @since 13/2/2015
 */
public class JmxInfo implements Serializable {

	private static final long serialVersionUID = -6029573021217319807L;

	// RUNTIME
	private Timestamp time = new Timestamp(0);
	private String jvmName = "";
	private long upTime = 0;

	// MEMORY
	private double memUsed;
	private double memAllocated;

	// CPU
	private double cpuUsage;
	private int cpuProcessors;

	public Timestamp getTime() {
		return time;
	}

	public String getJvmName() {
		return jvmName;
	}

	public long getUpTime() {
		return upTime;
	}

	public double getMemUsed() {
		return memUsed;
	}

	public double getMemAllocated() {
		return memAllocated;
	}

	public double getCpuUsage() {
		return cpuUsage;
	}

	public int getCpuProcessors() {
		return cpuProcessors;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public void setJvmName(String jvmName) {
		this.jvmName = jvmName;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public void setMemUsed(double memUsed) {
		this.memUsed = memUsed;
	}

	public void setMemAllocated(double memAllocated) {
		this.memAllocated = memAllocated;
	}

	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public void setCpuProcessors(int cpuProcessors) {
		this.cpuProcessors = cpuProcessors;
	}

	public String toString() {
		return "JmxInfo{" + "time=" + time + ", jvmName='" + jvmName + '\'' + ", upTime=" + upTime + ", memUsed="
				+ memUsed + ", memAllocated=" + memAllocated + ", cpuUsage=" + cpuUsage + ", cpuProcessors="
				+ cpuProcessors + '}';
	}
}
