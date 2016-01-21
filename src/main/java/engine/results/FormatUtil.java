package engine.results;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author ekaradimas
 * @since 13/2/2015
 */
@Component
public class FormatUtil {

	final static Random random = new Random();

	public static final FormatUtil INSTANCE = new FormatUtil();

	public String getOverallExecutionTime(long durationMillis) {
		Duration duration = new Duration(durationMillis);
		PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix(" Day(s) ").appendHours()
				.appendSuffix(" Hour(s) ").appendMinutes().appendSuffix(" Minute(s) ").appendSecondsWithOptionalMillis()
				.appendSuffix(" Second(s) ").toFormatter();
		return formatter.print(duration.toPeriod()).trim();
	}

	public void printOutProgress(String prefix, long done, long total) {
		StringBuilder bar = new StringBuilder();
		bar.append(prefix).append(" [");
		int percent = (int) ((done * 100) / total);
		for (int i = 0; i < 50; i++) {
			if (i < (percent / 2)) {
				bar.append("=");
			} else if (i == (percent / 2)) {
				bar.append(">");
			} else {
				bar.append(" ");
			}
		}
		bar.append("]   ").append(percent).append("%     ");
		System.out.print("\r" + bar.toString());
	}

	/**
	 * Method to convert the given time in milliseconds to a readable date
	 * format
	 *
	 * @param milliseconds
	 *            the time in milliseconds from the start of time 1/1/1970
	 *
	 * @return a string representing the given time in a readable format
	 */
	public String millis2Time(long milliseconds) {
		DateTime dt = new DateTime(milliseconds);
		return dt.toString(DateTimeFormat.shortDateTime());
	}
}
