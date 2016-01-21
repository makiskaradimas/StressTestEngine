package engine.results;

import org.springframework.stereotype.Component;

import engine.statistics.JmxInfo;
import engine.statistics.Statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author ekaradimas
 * @since 13/2/2015
 */
@Component("Result")
public class Result {

	private long started;
	private long ended;

	private Statistics statistics;

	private final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");

	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append("** Results ").append("\n").append("\t| ")
				.append(String.format("Started: %s \n", format.format(new Timestamp(started)))).append("\t| ")
				.append(String.format("Finished: %s \n", format.format(new Timestamp(ended)))).append("\t| ")
				.append(String.format("Throughput: %s/sec (sent=%s, failed=%s)\n", statistics.getThroughput(),
						statistics.getIterations(), statistics.getFailed()))

				.append("\t| ")
				.append(String.format("Average Response Time: %s millis\n", statistics.getResponseTime()))
				.append("\t| ").append(String.format("Overall execution: %s \n",
						FormatUtil.INSTANCE.getOverallExecutionTime(ended - started)));

		// Append JMX
		if (!statistics.getJmxInfo().isEmpty()) {
			sb.append("\t| JMX Information\n");
			for (Map.Entry<String, JmxInfo> info : statistics.getJmxInfo().entrySet()) {
				sb.append("\t| ").append("Instance: ").append(info.getValue().getJvmName()).append("\n")
						.append("\t|\t - UPTIME: ")
						.append(FormatUtil.INSTANCE.getOverallExecutionTime(info.getValue().getUpTime())).append("\n")
						.append("\t|\t -    CPU: ").append(info.getValue().getCpuUsage()).append("%\n")
						.append("\t|\t -    MEM: ").append(info.getValue().getMemUsed()).append("%\n");
			}
		}

		return sb.toString();
	}

	public void printOutProgress(Statistics statistics) {
		double responseTime = new BigDecimal((double) statistics.getResponseTime() / (double) 1000)
				.setScale(3, RoundingMode.HALF_EVEN).doubleValue();
		String prefix = String.format("* Throughput: %6s/sec, Response time: %6ssec, Failed cycles: %s ",
				statistics.getThroughput(), responseTime, statistics.getFailed());
		String format = "\r%s | Iterations: %s |\n";
		System.out.printf(format, prefix, statistics.getIterations());
	}

	public String giveOutProgress(Statistics statistics) {
		double responseTime = new BigDecimal((double) statistics.getResponseTime() / (double) 1000)
				.setScale(3, RoundingMode.HALF_EVEN).doubleValue();
		String prefix = String.format("* Throughput: %6s/sec, Response time: %6ssec, Failed cycles: %s ",
				statistics.getThroughput(), responseTime, statistics.getFailed());
		String format = "\r%s | Iterations: %s |\n";
		return String.format(format, prefix, statistics.getIterations());
	}

	public void setStarted(long started) {
		this.started = started;
	}

	public void setEnded(long ended) {
		this.ended = ended;
	}

	public long getStarted() {
		return started;
	}

	public long getEnded() {
		return ended;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
}