package common;

import java.util.Calendar;
import java.util.Locale;

public class MonthYear implements Comparable<MonthYear> {

		private String sortKey;
		private String monthName;
		private String yearName;
		private int month;
		private int year;
		
		public MonthYear(Calendar calendar)
		{
			this.month = calendar.get(Calendar.MONTH) + 1;
			this.year = calendar.get(Calendar.YEAR);

			this.monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
			this.setYearName(String.valueOf(calendar.get(Calendar.YEAR)));
						
		}

		public MonthYear (String yearMonth )
		{
			try {
				this.year = Integer.valueOf(yearMonth.substring(0, 4));
				this.month = Integer.valueOf(yearMonth.substring(4, 6));

				Calendar calendar = Calendar.getInstance();
				calendar.set(this.year, this.month - 1, 1);

				this.monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
				this.yearName = String.valueOf(calendar.get(Calendar.YEAR));
			} catch (Exception e)
			{
				String x = yearMonth;
			}
		}
		
		public MonthYear()
		{
			
		}
		
		public MonthYear getNextMonth()
		{
			MonthYear nextMonth = new MonthYear();
			nextMonth.setMonth(this.month + 1);
			if (nextMonth.getMonth() > 12)
			{
				nextMonth.setMonth(1);
				nextMonth.setYear(this.year + 1);
			}
			else
			{
				nextMonth.setYear(this.year);
			}
			
			return nextMonth;
		}


		/**
		 * @return the sortKey
		 */
		public String getSortKey() {
			
			String monthNumber = "";
			if (this.month < 10)
			{
				monthNumber = "0" + String.valueOf(this.month);
			}
			else
			{
				monthNumber = String.valueOf(this.month);
			}
			String sortKey = String.valueOf(this.getYear()) + monthNumber;
			
			
			return sortKey;
		}

		public void setSortKey(String sortKey)
		{
			this.sortKey = sortKey;
		}

		/**
		 * @return the monthName
		 */
		public String getMonthName() {
			return monthName;
		}
		
		public int getMonth()
		{
			return month;
		}
		
		/**
		 * @param monthName the monthName to set
		 */
		public void setMonthName(String monthName) {
			this.monthName = monthName;
		}
		/**
		 * @return the yearName
		 */
		public String getYearName() {
			return yearName;
		}
		
		public int getYear()
		{
			return year;
		}		
		
		/**
		 * @param yearName the yearName to set
		 */
		public void setYearName(String yearName) {
			this.yearName = yearName;
		}
		
		public String getMonthAndYearName()
		{
			return this.monthName + "/" + this.yearName;
		}
		
		/**
		 * @param month the month to set
		 */
		public void setMonth(int month) {
			this.month = month;
		}
		/**
		 * @param year the year to set
		 */
		public void setYear(int year) {
			this.year = year;
		}
		
		public void setMonthYear(String yearMonth)
		{
			try
			{
				this.year = Integer.valueOf(yearMonth.substring(0, 4));
			} catch (Exception e)
			{
				this.year = 2013;
			}
			try
			{
				this.month = Integer.valueOf(yearMonth.substring(4, 6));
			} catch (Exception e)
			{
				this.month = 0;
			}
		}

		public String getMonthYear() {
			return String.valueOf(this.getYear()) + String.valueOf(this.getMonth());
		}

		public int compareTo(MonthYear another) {
			
			int compareValue = 0;
			
			if (this.year < another.getYear())
			{
				compareValue = -1;
			}
			else if (this.year > another.getYear())
			{
				compareValue = 1;
			}
			else
			{
				if (this.month < another.getMonth())
				{
					compareValue = -1;
				}
				else if (this.month > another.getMonth())
				{
					compareValue = 1;
				}
			}
			
			return compareValue;
		}

}
