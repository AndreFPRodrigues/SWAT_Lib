package hcim.auric.calendar;

import java.util.Calendar;

public class CalendarManager {
	private static final String[] weekdays = new String[] { "Sun", "Mon",
			"Tue", "Wed", "Thu", "Fri", "Sat" };
	private static final String[] months = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };

	private static final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31,
			30, 31, 30, 31 };

	public static String getMonthAsString(int i) {
		return months[i];
	}

	public static String getWeekDayAsString(int i) {
		return weekdays[i];
	}

	public static int getNumberOfDaysOfMonth(int i) {
		return daysOfMonth[i];
	}

	public static String currentMonthYear() {
		Calendar c = Calendar.getInstance();
		return CalendarManager.getMonthAsString(c.get(Calendar.MONTH)) + " "
				+ c.get(Calendar.YEAR);
	}

	public static String getCurrentDateAndTime() {
		Calendar c = Calendar.getInstance();
		int seconds = c.get(Calendar.SECOND);
		int minutes = c.get(Calendar.MINUTE);
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int day = c.get(Calendar.DAY_OF_MONTH);
		String month = CalendarManager.getMonthAsString(c.get(Calendar.MONTH));
		int year = c.get(Calendar.YEAR);

		StringBuilder s = new StringBuilder();
		s.append(day);
		s.append("-");
		s.append(month);
		s.append("-");
		s.append(year);
		s.append(" ");
		s.append(hours);
		s.append(":");
		s.append(minutes);
		s.append(":");
		s.append(seconds);
		return s.toString();
	}
	
	public static String getDateFormat(String d, String m, String y){
		StringBuilder s = new StringBuilder();
		s.append(d);
		s.append("-");
		s.append(m);
		s.append("-");
		s.append(y);
		return s.toString();
	}
}
