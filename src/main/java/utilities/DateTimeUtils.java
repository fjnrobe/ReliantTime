package utilities;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import common.LogTime;
import common.MonthYear;
import common.Year;


import dtos.EffectiveInterval;
import dtos.LogDto;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.Document;
import sun.util.resources.cldr.ak.CalendarData_ak_GH;

public class DateTimeUtils {

	public enum DateFormats
	{
		MMDDYYYY,
		YYYYMMDD,
		YYYYMM,
		ISO,
		MMMDDYYYY
	}
	
	public static String LOW_DATE = "18000101";
	public static String HIGH_DATE = "25001231";
	public static String NULL_DATE = "26001231";

	//time is stored as military - 0000 to 2359
	public static LogTime LOW_TIME = new LogTime("0000");
	public static LogTime HIGH_TIME = new LogTime("2359");
	public static LogTime LUNCH_STARTS = new LogTime("1200");
	public static LogTime LUNCH_ENDS = new LogTime("1300");

	public static boolean isDateBetween(Date targetDate, Date fromDate, Date toDate)
	{
		return ((targetDate.compareTo(fromDate) >= 0) &&
				(targetDate.compareTo(toDate) <= 0));
	}

	public static boolean isNullDate(Date date)

	{
		return DateTimeUtils.getNullDate().compareTo(date) == 0;
	}

	public static Date getNullDate()
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date retDate = null;

		try {
			retDate = df.parse("2600-12-31");
		} catch (Exception e)
		{
			//
		}

		return  retDate;
	}

	public static MonthYear getCurrentMonthYear() {
		String today =
				DateTimeUtils.DateToString(DateTimeUtils.getToday(), DateFormats.YYYYMMDD, false);
		return new MonthYear(today);
	}

	public static MonthYear getMonthYear(Date date)
	{
		String today =
				DateTimeUtils.DateToString(date, DateFormats.YYYYMMDD, false);
		return new MonthYear(today);
	}

	public static MonthYear getLastMonthYear(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);

		return getMonthYear(calendar.getTime());
	}

	public static String getMonthYearDisplayNameText(MonthYear monthYear)
	{
		return monthYear.getMonthName() + "/" + monthYear.getYearName();
	}
	
	public static Calendar convertMonthYear(MonthYear monthYear, int day)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(monthYear.getYear(), monthYear.getMonth() - 1, day);
		
		return calendar;
	}

	public static Date convertMonthYear(MonthYear monthYear)
	{
		return convertMonthYear(monthYear,1).getTime();

	}

	public static boolean isLowDate(Calendar date1)
	{
		boolean isLowDate = false;
		
		if (date1.get(Calendar.YEAR) == 1800)
		{
			isLowDate = true;
		}
	
		return isLowDate;
	}
	
	public static String getMonthYearDisplayNameNumeric(MonthYear monthYear)
	{
		return monthYear.getSortKey().substring(4, 6) + "/" + monthYear.getSortKey().substring(0,4);
	}
	
	public static LogTime getStartOfWorkDay()
	{
		return new LogTime("0600");
	}
	
	/**
	 * the end of the work day is 5:15 pm. however, if the current time is past 5:15 pm, then return minute
	 * 
	 * @return the end of the current day's work day
	 */
	public static LogTime getEndOfWorkDay()
	{
		Calendar day = new GregorianCalendar();
		LogTime time = null;
			
		//should the current time be past 5:15pm, then set to midnight
		if ((day.get(Calendar.HOUR_OF_DAY) > 17) && 
				(day.get(Calendar.MINUTE) > 15))
		{
			time = HIGH_TIME;			
		}
		else
		{
			time = new LogTime("1700");
		}
		
		return time;
	}

	public static int getMonthFromDate(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal.get(Calendar.MONTH);
	}

	public static String DateToString(Date date, DateFormats format, boolean includeSeparator) {

		String retDate = null;

		if (format == DateFormats.MMMDDYYYY)
		{
			if (date != null) {
				DateFormat df = new SimpleDateFormat("MMM dd,yyyy");
				retDate = df.format(date);
			}
		}
		else if (format == DateFormats.ISO)
		{
			if (date != null) {
				DateFormat df1 = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
				//DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				retDate = df1.format(date);
			}
		}
		else {
			if (date == null) {
				retDate = DateTimeUtils.NULL_DATE;
			} else {
				Calendar calendar = Calendar.getInstance();

				calendar.setTime(date);

				retDate = DateTimeUtils.DateToString(calendar, format, includeSeparator);
			}
		}

		return retDate;
	}

	/**
	 * this method converts the incoming date to a string format. Of special note, the month portion of the calendar will be adjusted
	 * to account for the 0 based month scheme of the calendar. January is 0 in the calendar by '1' to the normal person.
	 * 
	 * @param date - to to format
	 * @param format - format specifier - see DateFormats
	 * @param includeSeparator - to include the '/' or not
	 * @return formatted date in specified format
	 */
	public static String DateToString(Calendar date, DateFormats format, boolean includeSeparator)
	{
		String stringDate = "";


		//pad all portions of date to full digit size
		String year = "0000".substring(0, 4 - String.valueOf(date.get(Calendar.YEAR)).length()) + String.valueOf(date.get(Calendar.YEAR));
		String month = "00".substring(0, 2 - String.valueOf(date.get(Calendar.MONTH) + 1).length()) + String.valueOf(date.get(Calendar.MONTH) + 1);
		String day =  "00".substring(0, 2 - String.valueOf(date.get(Calendar.DAY_OF_MONTH)).length()) + String.valueOf(date.get(Calendar.DAY_OF_MONTH)); 
		
		if (format == DateFormats.YYYYMMDD)
		{
			if (includeSeparator)
			{
				stringDate = year + "/" + month + "/" + day;
			}
			else
			{
				stringDate = year + month + day;
			}
		}
		else
		{
			if (includeSeparator)
			{
				stringDate = month + "/" + day + "/" + year;
			}
			else
			{
				stringDate = month + day + year;
			} 
		}
		
		return stringDate;
		
	}


    public static Date stringToDate(String date, DateFormats format)
    {
        Date retDate = null;
		if (DateFormats.ISO == format)
		{

			DateFormat df1 = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
			try {
				retDate = df1.parse(date);
			} catch (Exception e)
			{
				//do nothing
			}
		}
		else {
			Calendar newDate = StringToCalendar(date, format);
			if (newDate != null) {
				retDate = newDate.getTime();
			}
		}

        return retDate;
    }

	public static GregorianCalendar stringToGregorianCalendar(String date, DateFormats format)
	{
		Calendar cal = StringToCalendar(date, format);
		GregorianCalendar greg = (GregorianCalendar) cal;
		return greg;
	}

	public static Calendar StringToCalendar(String date, DateFormats format)
	{
		
		GregorianCalendar newDate = null;
		
		try {
			newDate = isValidDate(date, format);		
		} catch (Exception e)
		{
			
		}
	
		return newDate;
	}
	
	public static boolean isValidYearMonth(String yearMonth)
	{
		boolean isValid = false;
		String testDate = yearMonth + "01";
		if (DateTimeUtils.isValidDate(testDate, DateFormats.YYYYMMDD) != null)
		{
			isValid = true;
		}

		return isValid;
	}

	public static GregorianCalendar isValidDate(String date,DateFormats format)
	{
		
		boolean isValid = true;
		GregorianCalendar tmpCalendar = null;
		
		if (date == null)
		{
			isValid = false;
		}
		else
		{
			String tmpDate = date.trim();
			tmpDate = tmpDate.replace(".", "");
			tmpDate = tmpDate.replace("-", "");
			tmpDate = tmpDate.replace("/", "");
			
			if ((tmpDate.length() != 8))
			{
				isValid = false;
			}
			else
			{				
				try
				{
					int month = 0;
					int day = 0;
					int year = 0;
					
					switch (format)
					{
						case MMDDYYYY:						
							month = Integer.parseInt(tmpDate.substring(0,2)) - 1;
							day = Integer.parseInt(tmpDate.substring(2,4));
							year = Integer.parseInt(tmpDate.substring(4, 8));
							break;
						case YYYYMMDD:
							month = Integer.parseInt(tmpDate.substring(4,6)) - 1;
							day = Integer.parseInt(tmpDate.substring(6,8));
							year = Integer.parseInt(tmpDate.substring(0, 4));
							break;
					}
					
					tmpCalendar = new GregorianCalendar(year,month,day,0,0,0);
				} catch (Exception e)
				{
					isValid = false;
				}
				
			}
		}
		
		return tmpCalendar;
		
	}

    public static boolean isValidTime(String time) {

        boolean validTime = true;
        try {
            LogTime.validateTime(time);
        } catch (Exception e)
        {
            validTime = false;
        }

        return validTime;
    }

	/**
	 * this method checks to see if the existing log overlaps with the incoming date, and start/end time
	 * 
	 * @param existingLog
	 * @param logDateToCheck
	 * @param startTimeToCheck
	 * @param endTimeToCheck
	 * @return
	 */
	public static boolean dateWithinDateRange(LogDto existingLog, String logDateToCheck,
											  LogTime startTimeToCheck, LogTime endTimeToCheck)
	{
		boolean overlaps = false;
		
		//if the date is the same and the time range overlaps, it is an overlap
		if (existingLog.getLogDate().equals(logDateToCheck))
		{
			if ((new LogTime(existingLog.getStartTime()).getNumericTime() <
                    endTimeToCheck.getNumericTime()) &&
					(new LogTime(existingLog.getEndTime()).getNumericTime() >
                            startTimeToCheck.getNumericTime()))
			{
				overlaps = true;
			}
		}
		
		return overlaps;
	}
	
	public static LogTime StringToTime(String time)
	{
			
		return new LogTime(time);
	}
	
	
	public static String getTodayAsString(DateFormats format, boolean includeSeparator)
	{
		GregorianCalendar today = new GregorianCalendar();
		
		return DateToString(today, format, includeSeparator);
		
	}
	
	public static Calendar getToday()
	{
		return new GregorianCalendar();
	}

	public static Date getSystemDate()
	{
		GregorianCalendar today = new GregorianCalendar();
		return today.getTime();
	}

	public static int getCurrentMonth()
	{
		GregorianCalendar today = new GregorianCalendar();
		
		return today.get(Calendar.MONTH) + 1;
		
	}

	//this method returns the Monday for the incoming date
	//if the day goes into the next month, then the returned date will be
	//the first day of the month
	public static String getFirstDayOfWeek(String startDate)
	{
		GregorianCalendar startDay =
				new GregorianCalendar(Integer.parseInt(startDate.substring(0,4)),
								Integer.parseInt(startDate.substring(4,6)) - 1,
								Integer.parseInt(startDate.substring(6)),0,0,0);

        //set the completion week to the current month, and then set the last day to the month
        Calendar monday = Calendar.getInstance();
        monday.setTime(startDay.getTime());

        //now, we cycle backwards from the incoming date until the day is Monday
        // or we are before the 1st of the month
        while ((monday.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY ) &&
                (monday.get(Calendar.DAY_OF_MONTH) > 1))
        {
            monday.set(Calendar.DAY_OF_MONTH, monday.get(Calendar.DAY_OF_MONTH) - 1);
        }

		if (monday.get(Calendar.DAY_OF_MONTH) < 1)
       // if (monday.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY )
        {
            monday.add(Calendar.DAY_OF_MONTH, 1);
        }

		return DateTimeUtils.DateToString(monday, DateFormats.YYYYMMDD, false);
	}

	//this method returns the Sunday for the incoming date (which should be a Monday)
	//if the day goes into the next month, then the returned date will be
	//the last day of the month
	public static String getLastDayOfWeek(String startDate)
	{
		GregorianCalendar startDay =
				new GregorianCalendar(Integer.parseInt(startDate.substring(0,4)),
						Integer.parseInt(startDate.substring(4,6)) - 1,
						Integer.parseInt(startDate.substring(6)),0,0,0);

		Calendar nextSunday = getEndOfWeekAsSunday(startDay);

		return DateTimeUtils.DateToString(nextSunday, DateFormats.YYYYMMDD, false);
	}

	public static int getCurrentYear()
	{
		GregorianCalendar today = new GregorianCalendar();
		
		return today.get(Calendar.YEAR);
	}

	public static int getDayOfWeek(String date)
	{
		GregorianCalendar dateAsDate = stringToGregorianCalendar(date, DateFormats.YYYYMMDD);
		return dateAsDate.get(Calendar.DAY_OF_WEEK);
	}

	public static int getCurrentDay()
	{
		GregorianCalendar today = new GregorianCalendar();
		
		return today.get(Calendar.DAY_OF_MONTH);
	}

	public Date getFirstDayOfMonth(MonthYear monthYear)
	{
		Calendar startDate = Calendar.getInstance();
		startDate.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);

		return startDate.getTime();
	}

	public static EffectiveInterval getMonthInterval(MonthYear monthYear)
	{
		EffectiveInterval interval = new EffectiveInterval();
		interval.setMonthInterval(monthYear);

		return interval;
	}

	public static String reformatDate(String dateToReformat, DateFormats originalFormat, boolean separatorInOriginal, DateFormats newFormat, boolean separatorInNew)
	{
		String newDate = "";


		if (originalFormat == DateFormats.MMDDYYYY)
		{
			String m;
			String d;
			String y;

			if (separatorInOriginal)
			{
				String[] parts = dateToReformat.split("/");
				m = "0" + parts[0];
				m = m.substring(m.length() - 2);

				d = "0" + parts[1];
				d = d.substring(d.length() - 2);

				y = parts[2];
			}
			else
			{
				y = dateToReformat.substring(4, 8);
				m = dateToReformat.substring(0,2);
				d = dateToReformat.substring(2,4);
			}
			
			if (newFormat == DateFormats.YYYYMMDD)
			{
				if (separatorInNew)
				{
					newDate = y + "/" +
							m + "/" +
							d;
				}
				else
				{
					newDate = y + m + d;
				}
				
				
			}
		} 
		else
		{
			if (separatorInOriginal)
			{
				//need code to remove separator
			}
			
			if (newFormat == DateFormats.MMDDYYYY)
			{
				if (separatorInNew)
				{
					newDate = dateToReformat.substring(4, 6) + "/" +
							dateToReformat.substring(6,8) + "/" +
							dateToReformat.substring(0,4);
				}
				else
				{
					newDate = dateToReformat.substring(4, 6) +
							dateToReformat.substring(6,8) +
							dateToReformat.substring(0,4);
				}
			}
		}
		
		return newDate;
	}
	
	/**
	 * this method returns the number of minutes between the two times WITH THE ASSUMPTION that the 
	 * these are two times on the same day
	 * 
	 * @param startTime
	 * @param endTime
	 * @return - number of minutes - should be in 15 minute increment
	 */
	public static int getMinutesBetweenTimeOnSameDay(LogTime startTime, LogTime endTime)
	{
		int startTimeInMinutes = (startTime.getNumericHour() * 60) + startTime.getNumericMinute();
		int endTimeInMinutes = (endTime.getNumericHour() * 60) + endTime.getNumericMinute();
		
		return endTimeInMinutes - startTimeInMinutes;
		
	}
	
	public static double getHoursBetweenTimeRange (LogTime startTime, LogTime endTime)
	{
	
		//get the number of minutes in the end time - multiple by 60 to get minutes in the end hour
		double endTimeMinutes = ( endTime.getNumericHour() * 60 ) + endTime.getNumericMinute();
		double startTimeMinutes = ( startTime.getNumericHour() * 60 ) + startTime.getNumericMinute();
		
		double hours = (endTimeMinutes - startTimeMinutes) / 60;   
				
		return hours;
	}


//	public static  List<Year> getAllYearsWithLoggedPeriods(Context context, boolean ascending)
//	{
//
//		LogManager logManager = new LogManager(context);
//
//		List<SIRPCRLogBo> allLogs = logManager.getLogsByDateRange(LOW_DATE, HIGH_DATE, Constants.LOW_SIR_NUMBER);
//
//		//build a set of unique start years
//		SortedSet<Year> years = new TreeSet<Year>();
//
//		for (SIRPCRLogBo allLog : allLogs)
//		{
//
//			Year year = new Year();
//			year.setYearName(String.valueOf(allLog.getLogDate().get(Calendar.YEAR)));
//
//			year.setYear(allLog.getLogDate().get(Calendar.YEAR));
//
//			String sortKey = year.getYearName();
//
//			year.setSortKey(sortKey);
//
//			years.add(year);
//		}
//
//		return SortUtils.sortYear(years, ascending);
//	}

	
	public static Calendar getLastFridayOfWeek(Calendar date )
	{
		//set the completion week to the current month, and then set the last day to the month
		Calendar lastFridayOfWeek = Calendar.getInstance();
		lastFridayOfWeek.setTime(date.getTime());
		
		String x = DateTimeUtils.DateToString(date, DateFormats.MMDDYYYY, true);
		
		//now, we cycle forward from the incoming date until the day is Friday or we are have past the last day of the month
		while ((lastFridayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY ) &&
				(lastFridayOfWeek.getActualMaximum(Calendar.DAY_OF_MONTH) > lastFridayOfWeek.get(Calendar.DAY_OF_MONTH) ))
		{
			lastFridayOfWeek.set(Calendar.DAY_OF_MONTH, lastFridayOfWeek.get(Calendar.DAY_OF_MONTH) + 1);
		}
		
		if (lastFridayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY )
		{
			lastFridayOfWeek.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		return lastFridayOfWeek;
	}

	public static Calendar getEndOfWeekAsSunday(Calendar date )
	{
		//set the completion week to the current month, and then set the last day to the month
		Calendar lastSundayOfWeek = Calendar.getInstance();
		lastSundayOfWeek.setTime(date.getTime());

		String x = DateTimeUtils.DateToString(date, DateFormats.MMDDYYYY, true);

		//now, we cycle forward from the incoming date until the day is Sunday
		// or we are have past the last day of the month
		while ((lastSundayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ) &&
				(lastSundayOfWeek.getActualMaximum(Calendar.DAY_OF_MONTH) > lastSundayOfWeek.get(Calendar.DAY_OF_MONTH) ))
		{
			lastSundayOfWeek.set(Calendar.DAY_OF_MONTH, lastSundayOfWeek.get(Calendar.DAY_OF_MONTH) + 1);
		}

//		int a = lastSundayOfWeek.getActualMaximum(Calendar.DAY_OF_MONTH);
//		int b = lastSundayOfWeek.get(Calendar.DAY_OF_MONTH);
//
//		//if ((lastSundayOfWeek.getActualMaximum(Calendar.DAY_OF_MONTH) > lastSundayOfWeek.get(Calendar.DAY_OF_MONTH) ))
//		if (lastSundayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY )
//		{
//			lastSundayOfWeek.add(Calendar.DAY_OF_MONTH, -1);
//		}

		return lastSundayOfWeek;
	}

	public static int getLastDayOfMonth(MonthYear monthYear)
	{
		
		Calendar date = Calendar.getInstance();
		date.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);
		int lastDay = date.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		return lastDay;
	}
	
	/**
	 * this method returns the date that corresponds to the Friday following the incoming week
	 * 
	 * @param monthYear - month / year of current date
	 * @param weekOfMonth - week in current date
	 * @return - the date that is the friday following the incoming date values
	 */
	public static Calendar getLastFridayOfNextWeek(MonthYear monthYear, int weekOfMonth)
	{
		String x;
		
		//initialize a calendar to the first of the month
		Calendar lastFridayOfWeek = Calendar.getInstance();
		lastFridayOfWeek.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);
		x = DateTimeUtils.DateToString(lastFridayOfWeek, DateFormats.MMDDYYYY,true);
		//set to the first day of the week
		lastFridayOfWeek.set(Calendar.WEEK_OF_MONTH, weekOfMonth);
		x = DateTimeUtils.DateToString(lastFridayOfWeek, DateFormats.MMDDYYYY,true);
		lastFridayOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		x = DateTimeUtils.DateToString(lastFridayOfWeek, DateFormats.MMDDYYYY,true);
		
		//add 7 days - (this could take us into the following month
		lastFridayOfWeek.add(Calendar.DAY_OF_MONTH, 7);
	
		x = DateTimeUtils.DateToString(lastFridayOfWeek, DateFormats.MMDDYYYY,true);
		
		//now, we cycle forward from the incoming date until the day is Friday
		while ((lastFridayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY ) )
//				&&
//				(lastFridayOfWeek.getActualMaximum(Calendar.DAY_OF_MONTH) > lastFridayOfWeek.get(Calendar.DAY_OF_MONTH) ))
		{
			lastFridayOfWeek.set(Calendar.DAY_OF_MONTH, lastFridayOfWeek.get(Calendar.DAY_OF_MONTH) + 1);
		}
		
//		//if we didn't hit a Friday (then we hit the next month) - back up one day
//		if (lastFridayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY )
//		{
//			lastFridayOfWeek.add(Calendar.DAY_OF_MONTH, -1);
//		}
		
		return lastFridayOfWeek;
	}
}
