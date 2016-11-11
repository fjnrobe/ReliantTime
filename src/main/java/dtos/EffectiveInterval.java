package dtos;

import common.MonthYear;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Robertson_Laptop on 6/6/2016.
 */
public class EffectiveInterval {
    private Date startDate;
    private Date endDate;

    public EffectiveInterval()
    {

    }

    public EffectiveInterval(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setInterval(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setMonthInterval(MonthYear monthYear)
    {
        Calendar startDate = Calendar.getInstance();
        startDate.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(monthYear.getYear(), monthYear.getMonth() - 1, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        this.startDate  = startDate.getTime();
        this.endDate = endDate.getTime();

    }

    public boolean isDateBetween(Date date)
    {
        return (((date.equals(this.startDate) || date.after(this.startDate))) &&
                (date.equals(this.endDate) || date.before(this.endDate)));
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }



}
