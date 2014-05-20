package mobi.esys.string_process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;

public class DateStringProcess {
	private transient String processDate;
	private transient String secondDelta;
	private transient String minuteDelta;
	private transient String hourDelta;
	private transient String dayDelta;
	private transient String weeksDelta;
	private transient String monthDelta;
	private transient StringBuilder dateIntervalBuilder;

	public DateStringProcess(String processDate) {
		super();
		this.processDate = processDate;
		dateIntervalBuilder = new StringBuilder();
		getDeltas();
	}

	public String getIntervalStr() {
		build();
		return dateIntervalBuilder.toString();
	}

	private void getDeltas() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());

		DateTime vacDateTime = DateTime.now(DateTimeZone.forID("Etc/GMT-4"));

		try {
			vacDateTime = new DateTime(dateFormat.parse(processDate));
		} catch (ParseException e) {
		}
		DateTime currentDateTime = new LocalDateTime(new Date()).toDateTime();

		this.secondDelta = String.valueOf(Seconds.secondsBetween(vacDateTime,
				currentDateTime).getSeconds());
		this.minuteDelta = String.valueOf(Minutes.minutesBetween(vacDateTime,
				currentDateTime).getMinutes());
		this.hourDelta = String.valueOf(Hours.hoursBetween(vacDateTime,
				currentDateTime).getHours());
		this.dayDelta = String.valueOf(Days.daysBetween(vacDateTime,
				currentDateTime).getDays());
		this.weeksDelta = String.valueOf(Weeks.weeksBetween(vacDateTime,
				currentDateTime).getWeeks());
		this.monthDelta = String.valueOf(Months.monthsBetween(vacDateTime,
				currentDateTime).getMonths());
	}

	private void build() {
		if (minuteDelta.equals("0") && !secondDelta.equals("0")) {
			addSeconds();
		} else if (hourDelta.equals("0") && !minuteDelta.equals("0")) {
			addMinutes();
		} else if (dayDelta.equals("0") && !hourDelta.equals("0")) {
			addHours();
		} else if (weeksDelta.equals("0") && !dayDelta.equals("0")) {
			addDay();
		} else if (!weeksDelta.equals("0") && monthDelta.equals("0")) {
			addWeek();
		} else {
			addMonth();
		}
	}

	private void addSeconds() {
		int secDeltaInt = Integer.parseInt(secondDelta);
		if (secDeltaInt >= 2 && secDeltaInt <= 4
				|| (secondDelta.endsWith("2") && secDeltaInt != 12)
				|| (secondDelta.endsWith("3") && secDeltaInt != 13)
				|| (secondDelta.endsWith("4") && secDeltaInt != 14)) {
			dateIntervalBuilder.append(secondDelta + " " + "секунды назад");
		} else if (secondDelta.endsWith("1") && secDeltaInt != 11) {
			dateIntervalBuilder.append(secondDelta + " " + "секунду назад");
		} else {
			dateIntervalBuilder.append(secondDelta + " " + "секунд назад");
		}
	}

	private void addMinutes() {
		int minDeltaInt = Integer.parseInt(minuteDelta);
		if ((minDeltaInt >= 2 && minDeltaInt <= 4)
				|| (minuteDelta.endsWith("2") && minDeltaInt != 12)
				|| (minuteDelta.endsWith("3") && minDeltaInt != 13)
				|| (minuteDelta.endsWith("4") && minDeltaInt != 14)) {
			dateIntervalBuilder.append(minuteDelta + " " + "минуты назад");
		} else if (minuteDelta.endsWith("1") && minDeltaInt != 11) {
			dateIntervalBuilder.append(minuteDelta + " " + "минуту назад");
		} else {
			dateIntervalBuilder.append(minuteDelta + " " + "минут назад");
		}
	}

	private void addHours() {
		int hourDeltaInt = Integer.parseInt(hourDelta);
		if ((hourDeltaInt >= 2 && hourDeltaInt <= 4)
				|| (hourDelta.endsWith("2") && hourDeltaInt != 12)
				|| (hourDelta.endsWith("3") && hourDeltaInt != 13)
				|| (hourDelta.endsWith("4") && hourDeltaInt != 14)) {
			dateIntervalBuilder.append(hourDelta + " " + "часа назад");
		} else if (hourDelta.endsWith("1") && hourDeltaInt != 11) {
			dateIntervalBuilder.append(hourDelta + " " + "час назад");
		} else {
			dateIntervalBuilder.append(hourDelta + " " + "часов назад");
		}
	}

	private void addDay() {
		int dayDeltaInt = Integer.parseInt(dayDelta);
		if ((dayDeltaInt >= 2 && dayDeltaInt <= 4)
				|| (dayDelta.endsWith("2") && dayDeltaInt != 12)
				|| (dayDelta.endsWith("3") && dayDeltaInt != 13)
				|| (dayDelta.endsWith("4") && dayDeltaInt != 14)) {
			dateIntervalBuilder.append(dayDelta + " " + "дня назад");
		} else if (dayDelta.endsWith("1") && dayDeltaInt != 11) {
			dateIntervalBuilder.append(dayDelta + " " + "день назад");
		} else {
			dateIntervalBuilder.append(dayDelta + " " + "дней назад");
		}
	}

	private void addWeek() {
		int weekDeltaInt = Integer.parseInt(weeksDelta);
		if ((weekDeltaInt >= 2 && weekDeltaInt <= 4)
				|| (weeksDelta.endsWith("2") && weekDeltaInt != 12)
				|| (weeksDelta.endsWith("3") && weekDeltaInt != 13)
				|| (weeksDelta.endsWith("4") && weekDeltaInt != 14)) {
			dateIntervalBuilder.append(weeksDelta + " " + "недели назад");
		} else if (weeksDelta.endsWith("1") && weekDeltaInt != 11) {
			dateIntervalBuilder.append(weeksDelta + " " + "неделю назад");
		} else {
			dateIntervalBuilder.append(weeksDelta + " " + "недель назад");
		}
	}

	private void addMonth() {
		int monthDeltaInt = Integer.parseInt(monthDelta);
		if ((monthDeltaInt >= 2 && monthDeltaInt <= 4)
				|| (monthDelta.endsWith("2") && monthDeltaInt != 12)
				|| (monthDelta.endsWith("3") && monthDeltaInt != 13)
				|| (monthDelta.endsWith("4") && monthDeltaInt != 14)) {
			dateIntervalBuilder.append(monthDelta + " " + "месяца назад");
		} else if (monthDelta.endsWith("1") && monthDeltaInt != 11) {
			dateIntervalBuilder.append(monthDelta + " " + "месяц назад");
		} else {
			dateIntervalBuilder.append(monthDelta + " " + "месяцев назад");
		}
	}

	@Override
	public String toString() {
		return "DateStringProcess [processDate=" + processDate
				+ ", secondDelta=" + secondDelta + ", minuteDelta="
				+ minuteDelta + ", hourDelta=" + hourDelta + ", dayDelta="
				+ dayDelta + ", weeksDelta=" + weeksDelta + ", monthDelta="
				+ monthDelta + ", dateIntervalBuilder=" + dateIntervalBuilder
				+ "]";
	}

	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}

	public String getHourDelta() {
		return hourDelta;
	}

	public void setHourDelta(String hourDelta) {
		this.hourDelta = hourDelta;
	}

	public String getDayDelta() {
		return dayDelta;
	}

	public void setDayDelta(String dayDelta) {
		this.dayDelta = dayDelta;
	}

	public String getWeeksDelta() {
		return weeksDelta;
	}

	public void setWeeksDelta(String weeksDelta) {
		this.weeksDelta = weeksDelta;
	}

	public String getMonthDelta() {
		return monthDelta;
	}

	public void setMonthDelta(String monthDelta) {
		this.monthDelta = monthDelta;
	}

	public StringBuilder getDateIntervalBuilder() {
		return dateIntervalBuilder;
	}

	public void setDateIntervalBuilder(StringBuilder dateIntervalBuilder) {
		this.dateIntervalBuilder = dateIntervalBuilder;
	}

	public String getSecondDelta() {
		return secondDelta;
	}

	public void setSecondDelta(String secondDelta) {
		this.secondDelta = secondDelta;
	}

	public String getMinuteDelta() {
		return minuteDelta;
	}

	public void setMinuteDelta(String minuteDelta) {
		this.minuteDelta = minuteDelta;
	}

}
