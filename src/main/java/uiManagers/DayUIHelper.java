package uiManagers;

import common.LogTime;
import dtos.DayEntryMatrixRowuiDto;
import dtos.LogDto;
import dtos.SirPcrDto;
import utilities.DateTimeUtils;

import java.util.*;

/**
 * Created by Robertson_Laptop on 8/17/2016.
 */
public class DayUIHelper {

    //this method will construct the parameters for use on the day.ftl.
    public HashMap<String, Object> constructDailyMatrix(String dayToShow, List<SirPcrDto> sirEntries) {

        HashMap<String, Object> parms = new HashMap<String, Object>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeUtils.stringToDate(dayToShow, DateTimeUtils.DateFormats.YYYYMMDD));
        parms.put("currentDay", dayToShow);
        parms.put("currentYearMonth", dayToShow.substring(0,6));

        calendar.add(Calendar.DATE, 1);
        parms.put("nextDay", DateTimeUtils.DateToString(calendar.getTime(), DateTimeUtils.DateFormats.YYYYMMDD, false));

        calendar.add(Calendar.DATE, -2);
        parms.put("prevDay", DateTimeUtils.DateToString(calendar.getTime(), DateTimeUtils.DateFormats.YYYYMMDD, false));

        List<DayEntryMatrixRowuiDto> dayEntries = new ArrayList<DayEntryMatrixRowuiDto>();

        double totHours = 0.0;

        for (SirPcrDto sirEntry : sirEntries) {

            LogTime currentTimePoint = DateTimeUtils.getStartOfWorkDay();

            DayEntryMatrixRowuiDto uiDto = new DayEntryMatrixRowuiDto();
            uiDto.setSirNickname(sirEntry.getNickName());
            uiDto.setId(sirEntry.getId());

            //for each sir, we will denote the log entry for a given start/end time with a button'
            for (LogDto logEntry : sirEntry.getLogs()) {

                //we want to determine how much space to put before each button - all time is in 15 minute increments'
                LogTime logStartTime = new LogTime(logEntry.getStartTime());
                LogTime logEndTime = new LogTime(logEntry.getEndTime());

                uiDto.getLogId().add(logEntry.getId());
                if (logStartTime.compareTo(currentTimePoint) == 1) {
                    uiDto.getSpaceBeforeEntry().add(DateTimeUtils.getMinutesBetweenTimeOnSameDay(
                            currentTimePoint, logStartTime) / 15);
                } else {
                    uiDto.getSpaceBeforeEntry().add(0);
                }

                //set the width of the button bsaed on the time in minutes / 15 minutes - each column on the grid will be 15 minutes'
                uiDto.getEntryWidth().add(
                        DateTimeUtils.getMinutesBetweenTimeOnSameDay(logStartTime, logEndTime) / 15);

                //the label will be the hours based on the log_periods'
                uiDto.getEntryLabel().add(String.valueOf(logEntry.getHours()));
                totHours += logEntry.getHours();

                //move the time to the end of the current log'
                currentTimePoint = logEndTime;
            }

            dayEntries.add(uiDto);

        }
        parms.put("matrixConfig", dayEntries);
        parms.put("totalHours", totHours);

        return parms;
    }
}
