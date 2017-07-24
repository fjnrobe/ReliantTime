package managers;

import com.mongodb.client.MongoDatabase;
import common.LOVEnum;
import common.MonthYear;
import daos.LogDao;
import dtos.CalendarDto;
import dtos.SirLogDto;
import org.bson.types.ObjectId;
import utilities.DateTimeUtils;
import utilities.NumberUtils;


import java.util.List;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public class LogManager {

    private final LogDao logDao;

    public LogManager(MongoDatabase reliantDb) {

        logDao = new LogDao(reliantDb);
    }

    public List<CalendarDto> getCalendarHoursByMonth(MonthYear monthYear) {

        List<CalendarDto> hours = this.logDao.getCalendarHoursByMonth(monthYear);
        for (CalendarDto hour : hours)
        {
            hour.setHours(NumberUtils.roundHoursFourDecimals(hour.getHours()));
        }

        return hours;
    }

    public List<String> getYearsWithLoggedPeriods()
    {
        return this.logDao.getYearsWithLoggedPeriods();
    }

    //this method gets all the month/years with actual logged hours.
    //if the incoming value for 'includeSysDate' is true, it will insert the current
    //month/year if not already included
    public List<MonthYear> getMonthsWithLoggedPeriods(boolean includeSysDate) {
        List<MonthYear> monthsWithHours = this.logDao.getMonthsWithLoggedPeriods();

        if (includeSysDate)
        {
            boolean exists = false;
            MonthYear sysMonthYear = DateTimeUtils.getCurrentMonthYear();
            for (MonthYear entry : monthsWithHours) {

                if (entry.compareTo(sysMonthYear) == 0)
                {
                    exists = true;
                    break;
                }
            }
            if (!exists)
            {
                monthsWithHours.add(sysMonthYear);
            }
        }

        return monthsWithHours;
    }


    public Long getUsageCount(String columnName, String columnValue)
    {
        return this.logDao.getUsageCount(columnName, columnValue);
    }

    public long updateLovEntry(LOVEnum lovEnum, String oldValue, String newValue)
    {
        return this.logDao.updateLovValue(lovEnum, oldValue, newValue);
    }

    public List<SirLogDto> getLogsByActivityTypeAndDate(String activityDesc,
                                                        String fromDate, String toDate)
    {
        return this.logDao.getLogsByActivityTypeAndDate(activityDesc, fromDate, toDate);
    }
}
