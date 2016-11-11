package common;

import utilities.DateTimeUtils;

public class LogTime implements Comparable<LogTime> {

	//time is stored as hhmm
	private String time;

	public LogTime()
	{
		
	}
	
	public LogTime(String time)
	{
		try
		{
			
			this.setTime(time);
		} catch (Exception e)
		{
			
		}
	}
	
	
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time)  {
		
		try {
			time = this.formatTime(time);
			this.validateTime(time);
			this.time = time;
		} catch (Exception e)
		{

		}
		
	}
	
	public String getHour() 
	{
		return time.substring(0,2);
	}
	
	public int getNumericHour()
	{
		return Integer.valueOf(this.getHour());
	}
	
	public String getMinute()
	{
		return time.substring(2, 4);
	}
	
	public int getNumericMinute()
	{
		return Integer.valueOf(this.getMinute());
	}
	
	public int getNumericTime()
	{
		return Integer.parseInt(this.time);
	}

	/**
	 * return time in the format hh:mm am|pm
	 * @return
	 */
	public String getDisplayTime()
	{
		String outTime = "";
		int hour = 0;
		String ampm = "";
		
		if (this.getNumericHour() > 12)
		{
			hour = this.getNumericHour() - 12;
			ampm = "pm";
		}
		else
		{
			hour = this.getNumericHour();
			ampm = "am";
		}
		
		outTime = String.valueOf(hour) + ":" + this.getMinute() + ampm;
		
		return outTime;
	}
	
	private String formatTime(String inTime)
	{
		//"hh:mm"
		
		String returnTime = "0000";
		String tmpTime = inTime;
		boolean validTime = true;
		
		int colonIdx = tmpTime.indexOf(":");
		if (colonIdx >= 0)
		{
			//we have a time such as 5:00 - add a 0 to preceed the 5
			if (colonIdx == 1)
			{
				tmpTime = "0" + tmpTime;
			}
			//we have some 3 digit hour, invalid
			else if (colonIdx > 2)
			{
				validTime = false;
			}
			else
			{
				tmpTime = tmpTime.replace(":", "");
			}
		}
		//in the absense of a colon, then the time must have been set as HHMM - pad to 4 chars if less than 4 chars
		else
		{
			if (tmpTime.length() > 4)
			{
				validTime = false;
			}
			else
			{
				tmpTime = tmpTime.concat("0000".substring(0, 4 - tmpTime.length()));;
			}			
		}
		
		if (validTime)
		{
			if (tmpTime.length() >= 5)
			{
				validTime = false;
			}						
		}
		
		//return a time without formatting
		if (validTime)
		{
			returnTime = tmpTime;
		}
	
		return returnTime;
	}
	
	public static void validateTime(String time) throws Exception
	{
		//HHMM
		
		if (!time.equals("0000"))
		{
			if ((Integer.parseInt(time.substring(0, 2)) < 1) ||  (Integer.parseInt(time.substring(0, 2)) > 23))
			{
				throw new Exception ("the hour must be between 1 and 23");
			}
			if ((Integer.parseInt(time.substring(2,4)) < 0) || (Integer.parseInt(time.substring(2,4)) > 59))
			{
				throw new Exception ("The minute must be between 0 and 59");
			}
		}
					
	}
	
	public int compareTo (LogTime compareToTime)
	{
		int compareValue;
		
		if (this.getNumericTime() < compareToTime.getNumericTime())
		{
			compareValue = -1;
		}
		else if (this.getNumericTime() == compareToTime.getNumericTime())
		{
			compareValue =  0;
		}
		else
		{
			compareValue = 1;
		}
			
		return compareValue;
	}
	
	public boolean equals (LogTime compareToTime)
	{
		boolean isEqual = false;
		
		if (this.getNumericTime() == compareToTime.getNumericTime())
		{
			isEqual = true;
		}
		
		return isEqual;
	}



}
