package dtos;

import java.util.Date;

/**
 * Created by Robertson_Laptop on 6/6/2016.
 */
public class CalendarDto {
    private String dayOfMonth;
    private double hours;

    public String getDayOfMonth() {
        return dayOfMonth;
    }
    public int getIntDayOfMonth()
    {
        return Integer.parseInt(dayOfMonth);
    }
    public double getHours() {
        return hours;
    }

    public void setDayOfMonth(String dayOfMonth) {

        this.dayOfMonth = dayOfMonth;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }


}
