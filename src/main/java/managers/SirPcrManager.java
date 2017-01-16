package managers;

import com.mongodb.client.MongoDatabase;

import common.LOVEnum;
import common.LogTime;
import daos.LogDao;
import daos.SIRPCRDao;
import dtos.ErrorDto;
import dtos.ExistingSIRUIDto;
import dtos.LogDto;
import dtos.SirPcrDto;
import enums.SIR_Status;
import org.bson.types.ObjectId;
import uiManagers.DayUIHelper;
import utilities.DateTimeUtils;
import utilities.NumberUtils;
import utilities.SortUtils;
import utilities.StringUtils;

import java.util.*;



/**
 * Created by Robertson_Laptop on 6/5/2016.
 */
public class SirPcrManager {
    private final SIRPCRDao sirPcrDao;
    private final LogDao logDao;

    public SirPcrManager(MongoDatabase reliantDb) {

        sirPcrDao = new SIRPCRDao(reliantDb);
        logDao = new LogDao(reliantDb);
    }

    public String getNextSirIdForOtherSirType()
    {
        return this.sirPcrDao.getNextSirIdForOtherSirType();
    }

    public SirPcrDto findSirByNickName(String nickname)
    {
        return this.sirPcrDao.getSirByNickName(nickname);
    }

    public void deleteLog(String logId)
    {
        LogDto logDto = this.logDao.getByLogId(new ObjectId(logId));

        this.logDao.deleteByLogId(new ObjectId(logId));

        //need to compute the actual hours attributable to the log entry
        //given overlaps with other logs
        this.updateLogHours(logDto.getLogDate());

    }

    public List<ErrorDto> validateAndSaveSir(SirPcrDto sirDto) {
        List<ErrorDto> errors = this.validateSir(sirDto);
        if (errors.isEmpty()) {
            ObjectId sirId = null;
            if (sirDto.getId() != null) {
                this.sirPcrDao.update(sirDto);
                sirId = sirDto.getId();
            } else {
                sirId = this.sirPcrDao.insert(sirDto);
            }

            if (sirDto.getLogs().size() == 1) {
                LogDto logDto = sirDto.getLogs().get(0);
                if (logDto.getId() != null) {
                    this.logDao.updateLogs(sirDto.getLogs());
                } else {
                    LogDto newLog = sirDto.getLogs().get(0);
                    newLog.setSirPcrId(sirId);
                    this.logDao.insert(newLog);
                }
                //need to compute the actual hours attributable to the log entry
                //given overlaps with other logs
                this.updateLogHours(sirDto.getLogs().get(0).getLogDate());
            }
        }


        return errors;
    }

    private List<ErrorDto> validateSir(SirPcrDto sirDto) {

        List<ErrorDto> errors = new ArrayList<ErrorDto>();

        if (StringUtils.isEmpty(sirDto.getSirDesc())) {
            errors.add(new ErrorDto("SIR Description is required"));
        }

        if (StringUtils.isEmpty(sirDto.getSubProcessDesc())) {
            errors.add(new ErrorDto("SubProcess is required"));
        }

        if (StringUtils.isEmpty(sirDto.getSirPcrNumber())) {
            errors.add(new ErrorDto("SIR Number is required"));
        } else {
            long sirNo = 0;
            try {
                sirNo = Long.parseLong(sirDto.getSirPcrNumber());

                //verify sir number is unique
                SirPcrDto testSir = this.sirPcrDao.getSirByNumber(sirDto.getSirPcrNumber());

                if ((testSir == null) || (testSir.getId().equals(sirDto.getId()))) {
                    //do nothing - this means the sir number didn't exist, or if it does
                    //exist, it is the same as the incoming sir - we are editing
                } else {
                    errors.add(new ErrorDto("This SIR number already exists"));
                }

            } catch (Exception e) {
                errors.add(new ErrorDto("SIR Number must be numeric"));
            }
        }

        if (StringUtils.isEmpty(sirDto.getSirType())) {
            errors.add(new ErrorDto("SIR type is required"));
        }

        //nickname is optional - but if present, must be unique
        if (!StringUtils.isEmpty(sirDto.getNickName())) {
            SirPcrDto byNickname = (this.sirPcrDao.getSirByNickName(sirDto.getNickName()));
            if (byNickname != null) {
                if (!byNickname.getId().equals(sirDto.getId())) {
                    errors.add(new ErrorDto("Nickname already used"));
                }
            }
        }

        //if there is a log, validate it as well

        if (sirDto.getLogs().size() == 1) {
            errors.addAll(this.validateLog(sirDto));
        }

        return errors;

    }

    private List<ErrorDto> validateLog(SirPcrDto sirlogDto) {
        List<ErrorDto> errors = new ArrayList<ErrorDto>();
        LogDto logDto = sirlogDto.getLogs().get(0);

        if (logDto.getLogDate() != null) {
            if (StringUtils.isEmpty(logDto.getStartTime())) {
                errors.add(new ErrorDto("Start time is missing"));
            } else {
                if (!DateTimeUtils.isValidTime(logDto.getStartTime())) {
                    errors.add(new ErrorDto("invalid formatted start time"));
                }
            }
            if (!StringUtils.isEmpty(logDto.getEndTime())) {
                if (!DateTimeUtils.isValidTime(logDto.getEndTime())) {
                    errors.add(new ErrorDto("invalid formatted end time"));
                }
            } else {
                logDto.setEndTime(DateTimeUtils.getEndOfWorkDay().getTime());
            }

            //make sure start time < end date - but only if no errors at this point
            if (errors.isEmpty()) {
                if (!(new LogTime(logDto.getStartTime()).equals(DateTimeUtils.LOW_TIME))) {
                    if (new LogTime(logDto.getStartTime()).compareTo(
                            new LogTime(logDto.getEndTime())) != -1) {
                        errors.add(new ErrorDto("the end time must be greater than the start time"));
                    }
                }
            }

            //if no errors - validate that the log entry doesn't overlap an existing
            //log entry for this sir
            if (errors.isEmpty()) {

                if (sirlogDto.getId() != null) {
                    List<LogDto> existingLogs = this.logDao.getLogsBySirObjectId(sirlogDto.getId());

                    for (LogDto existingLog : existingLogs) {
                        if (!existingLog.getId().equals(logDto.getId())) {
                            if (DateTimeUtils.dateWithinDateRange(existingLog,
                                    logDto.getLogDate(),
                                    new LogTime(logDto.getStartTime()),
                                    new LogTime(logDto.getEndTime()))) {
                                errors.add(new ErrorDto("this log entriesoverlaps an existing entry and cannot be added"));

                            }
                        }
                    }

                    //if the activity type is meetings - then we can't allow an overlap with any
                    //other meeting activity types
                    if (logDto.getActivityDesc().equals("Meetings")) {
                        List<LogDto> logsOnDate =
                                this.logDao.getLogsByDateRange(
                                        logDto.getLogDate(),
                                        logDto.getLogDate());

                        for (LogDto logOnDate : logsOnDate) {
                            if (logOnDate.getActivityDesc().equals("Meetings") &&
                                    !logOnDate.getId().equals(logDto.getId())) {
                                if (DateTimeUtils.dateWithinDateRange(logOnDate,
                                        logDto.getLogDate(),
                                        new LogTime(logDto.getStartTime()),
                                        new LogTime(logDto.getEndTime()))) {
                                    errors.add(new ErrorDto("cannot add two meetings that overlap in time"));
                                }
                            }
                        }
                    }
                }
            }

            if (StringUtils.isEmpty(logDto.getActivityDesc())) {
                errors.add(new ErrorDto("Activity Type is a required field"));
            }
            if (StringUtils.isEmpty((logDto.getPrimaveraDesc()))) {
                errors.add(new ErrorDto("Primavera Activity Type is a required field"));
            }
        }

        return errors;
    }

    /*
        this method will determine the hours that the log gets based on the presence/overlap
        of other logs for the same date/time
     */
    private void updateLogHours(String logDate) {
//        LogDto logDto = sirPcrDto.getLogs().get(0);

        //get all the logs for the date of the incoming log doc
        //note, the log being processed has already been persisted
        List<LogDto> overlappingLogs = this.logDao.getLogsByDateRange(logDate,
                logDate);

        //create timepoints
        List<LogTime> timePoints = this.createTimePoints(overlappingLogs);

        //chunk each log into periods - note - the lunchtime period will not be included
        List<LogDto> allLogPeriods = new ArrayList<LogDto>();
        for (LogDto log : overlappingLogs) {
            allLogPeriods.addAll(this.createLogPeriods(log, timePoints));
        }

        //stamp each log period with the percentage of time to allocate (ie, if 3 logs have a timeperiod
        //then each log period gets 3s.33 percent of the time
        List<LogDto> allPeriods = new ArrayList<LogDto>();
        for (LogTime timePoint : timePoints) {
            List<LogDto> periodsStartingWithTimePoint = this.getPeriodsThatStartAtTimePoint(allLogPeriods, timePoint);

            //if any of these periods belong to a log for a meeting activity type - then only that log gets the time
            ObjectId logId = this.getMeetingPeriodLogId(periodsStartingWithTimePoint);

            for (LogDto logPeriod : periodsStartingWithTimePoint) {
                if (logId == null) {
                    logPeriod.setHours((1.0 / periodsStartingWithTimePoint.size()));
                } else if (logId.equals(logPeriod.getId())) {
                    logPeriod.setHours(1.0);
                } else {
                    logPeriod.setHours(0.0);
                }
            }

            allPeriods.addAll(periodsStartingWithTimePoint);
        }

        //now refresh the total hours for each log
        for (LogDto overlappingLog : overlappingLogs) {
            //reset the total hours to 0 and then we will tally up the
            //hours on the individual timepoint periods
            double totHours = 0.0;
            for (LogDto logPeriod : allPeriods) {
                if (overlappingLog.getId().equals(logPeriod.getId())) {
                    double hours =
                            DateTimeUtils.getHoursBetweenTimeRange(new LogTime(logPeriod.getStartTime()), new LogTime(logPeriod.getEndTime()));
                    //note - the hours were temporarily set to the percentage of time the period should get
                    //we now multiply that by the true time
                    totHours += (logPeriod.getHours() * hours);
                }
            }
            overlappingLog.setHours(NumberUtils.roundHoursFourDecimals(totHours));

        }

        this.logDao.updateLogs(overlappingLogs);
    }

    private List<LogTime> createTimePoints(List<LogDto> logDtos) {
        SortedSet<LogTime> timePoints = new TreeSet<LogTime>();
        List<LogTime> timePointsList = new ArrayList<LogTime>();

        //build a unique list of start times - the sortedSet will handle the elimination
        //of duplicate start times
        for (LogDto logDto : logDtos) {
            timePoints.add(new LogTime(logDto.getStartTime()));
            timePoints.add(new LogTime(logDto.getEndTime()));
        }

        //insert a time for 12pm and 1 pm - this is the lunch time in which no time is logged
        timePoints.add(DateTimeUtils.LUNCH_STARTS);
        timePoints.add(DateTimeUtils.LUNCH_ENDS);

        //put out in ascending time order
        Iterator iterator = timePoints.iterator();
        while (iterator.hasNext()) {
            timePointsList.add((LogTime) iterator.next());
        }

        return timePointsList;
    }

    public List<LogDto> createLogPeriods(LogDto log, List<LogTime> timePoints) {
        List<LogDto> logPeriods = new ArrayList<LogDto>();

        LogDto logPeriod = null;

        //the timepoints are in ascending time order
        for (LogTime timePoint : timePoints) {
            LogTime time = timePoint;

            //if the current time is before the log starts - do nothing
            if (time.getNumericTime() < new LogTime(log.getStartTime()).getNumericTime()) {
                //do nothing - this is a timepoint earlier than the log begin time
            }
            //we have hit the timepoint that is the start of the current log start time
            else if (time.getNumericTime() == new LogTime(log.getStartTime()).getNumericTime()) {
                logPeriod = new LogDto();
                logPeriod.setId(log.getId());
                logPeriod.setStartTime(time.getTime());

            }
            //when a timeperiod is before the end of the log, then we end the current period, save it
            //and start a new period
            else if (time.getNumericTime() < new LogTime(log.getEndTime()).getNumericTime()) {
                logPeriod.setEndTime(time.getTime());
                //for simplicity, any timeframe that starts/ends on the lunch time frame is not added to the log set
                if (!logPeriod.getStartTime().equals(DateTimeUtils.LUNCH_STARTS.getTime()) &&
                        !logPeriod.getEndTime().equals(DateTimeUtils.LUNCH_ENDS.getTime())) {
                    logPeriods.add(logPeriod);
                }

                logPeriod = new LogDto();
                logPeriod.setId(log.getId());
                logPeriod.setLogDate(log.getLogDate());
                logPeriod.setStartTime(time.getTime());
            }
            //once we hit a timeperiod that is on/after the end of the log, then close off
            //the current period and set the logperiod to null - this will then
            //indicate that any future perios are not needed
            else if ((time.getNumericTime() == new LogTime(log.getEndTime()).getNumericTime()) ||
                    (time.getNumericTime() > new LogTime(log.getEndTime()).getNumericTime())) {
                if (logPeriod != null) {
                    logPeriod.setEndTime(time.getTime());
                    //for simplicity, any timeframe that starts/ends on the lunch time frame is not added to the log set
                    if (!logPeriod.getStartTime().equals(DateTimeUtils.LUNCH_STARTS.getTime()) &&
                            !logPeriod.getEndTime().equals(DateTimeUtils.LUNCH_ENDS.getTime())) {
                        logPeriods.add(logPeriod);
                    }
                    logPeriod = null;
                }
            }
        }

        //stamp the activity for the period

        for (LogDto period : logPeriods) {
            period.setActivityDesc(log.getActivityDesc());
        }

        return logPeriods;
    }

    private List<LogDto> getPeriodsThatStartAtTimePoint(List<LogDto> logPeriods, LogTime timePoint) {
        List<LogDto> matchPeriods = new ArrayList<LogDto>();

        for (LogDto period : logPeriods) {
            if (new LogTime(period.getStartTime()).getNumericTime() ==
                    timePoint.getNumericTime()) {
                matchPeriods.add(period);
            }
        }

        return matchPeriods;
    }

    private ObjectId getMeetingPeriodLogId(List<LogDto> periodsStartingWithTimePoint) {
        ObjectId logId = null;
        for (LogDto period : periodsStartingWithTimePoint) {
            if (period.getActivityDesc().equals("Meetings")) {
                logId = period.getId();
                break;
            }
        }

        return logId;
    }

    /*
        this method returns an entry from COLLECTION_SIR_PCR for the incoming id
     */
    public SirPcrDto getSirById(ObjectId id) {
        return this.sirPcrDao.getSirById(id);

    }

    public SirPcrDto getSirByNumber(String sirNumber)
    {
        return this.sirPcrDao.getSirByNumber(sirNumber);
    }

    public SirPcrDto getSirAndLogsBySirId(String id) {
        ObjectId sirId = new ObjectId(id);
        SirPcrDto sirPcrDto = this.getSirById(sirId);

        List<LogDto> logDtos = this.logDao.getLogsBySirObjectId(sirId);
        for (LogDto logDto : logDtos) {
            logDto.setHours(NumberUtils.roundHoursFourDecimals(logDto.getHours()));
            sirPcrDto.getLogs().add(logDto);
        }

        return sirPcrDto;
    }

    //retrieve the log for the incoming id and the associated SIR info
    public SirPcrDto getSirByLogId(ObjectId logId) {
        LogDto logDto = logDao.getByLogId(logId);

        SirPcrDto sirPcrDto = this.getSirById(logDto.getSirPcrId());

        sirPcrDto.getLogs().add(logDto);

        return sirPcrDto;
    }

    public void deleteSir(String sirId) {
        List<LogDto> logDtos = this.logDao.getLogsBySirObjectId(new ObjectId(sirId));

        for (LogDto logDto : logDtos) {
            this.logDao.deleteByLogId(logDto.getId());
            this.updateLogHours(logDto.getLogDate());
        }

        this.sirPcrDao.deleteBySirId(new ObjectId(sirId));

    }

    /*
        this method returns all entries from COLLECTION_SIR_PCR
     */
    public List<SirPcrDto> getAllSirs(SortUtils.SortBy sortBy) {
        List<SirPcrDto> dtos = this.sirPcrDao.getAllSIRs();

        if (sortBy != null) {
            if (sortBy == SortUtils.SortBy.SIR_NUMBER) {
                SortUtils.sortBySirNumber(dtos, true);
            }
        }

        return dtos;
    }

    /*
        this method returns all sirs and the logs that fall within the date range.

       return a list of SIRs and the logs that fall within the date range
     */
    public List<SirPcrDto> getSirsByDateRange(String startDate, String endDate) {

        List<SirPcrDto> retList = new ArrayList<SirPcrDto>();

        HashMap<String, SirPcrDto> mapCollection = new HashMap<String, SirPcrDto>();

        //get the logs
        List<LogDto> logs = this.logDao.getLogsByDateRange(startDate, endDate);

        //for each log - see if the SIR has already been retrieved
        for (LogDto log : logs) {
            SirPcrDto sirDto;

            //if this log is for a new sir, get the sir detail and
            //add to the map
            if (!mapCollection.containsKey(log.getSirPcrId().toString())) {

                sirDto = this.sirPcrDao.getSirById(log.getSirPcrId());

                mapCollection.put(log.getSirPcrId().toString(), sirDto);
            } else {
                sirDto = mapCollection.get(log.getSirPcrId().toString());
            }

            sirDto.getLogs().add(log);
        }

        //convert the map to a list
        return new ArrayList<SirPcrDto>(mapCollection.values());


    }

    public List<SirPcrDto> getSirsByStatus(SIR_Status sirStatus) {
        return this.sirPcrDao.getSirsByStatus(sirStatus);
    }

    public HashMap<String, Object> getDayEntryMatrix(String dayToShow) {
        List<SirPcrDto> sirs = this.getSirsByDateRange(dayToShow, dayToShow);

        DayUIHelper uiHelper = new DayUIHelper();
        return uiHelper.constructDailyMatrix(dayToShow, sirs);

    }

    public List<ExistingSIRUIDto> getExistingSirs(SIR_Status sirStatus) {
        List<SirPcrDto> sirs = this.getSirsByStatus(sirStatus);

        List<ExistingSIRUIDto> uiDtos = new ArrayList<ExistingSIRUIDto>();

        for (SirPcrDto sir : sirs) {
            String desc;
            if (Integer.parseInt(sir.getSirPcrNumber()) < 0) {
                desc = "(" + sir.getSirType() + ") " + sir.getNickName();
            } else {
                desc = "(" + sir.getSirPcrNumber() + ") " + sir.getNickName();
            }

            ExistingSIRUIDto uiDto = new ExistingSIRUIDto();
            uiDto.setId(sir.getId().toString());
            uiDto.setDescription(desc);

            uiDtos.add(uiDto);
        }

        SortUtils.sortSIRUiDtos(uiDtos, true);

        return uiDtos;
    }

    public Long getUsageCount(String columnName, String columnValue)
    {
        return this.sirPcrDao.getUsageCount(columnName, columnValue);
    }

    public long updateLovEntry(LOVEnum lovEnum, String oldValue, String newValue)
    {
        return this.sirPcrDao.updateLovValue(lovEnum, oldValue, newValue);
    }

    public List<SirPcrDto> findSirsByCriteria(String status, String sirType, String sirNumber)
    {
        Boolean completedInd = null;
        if (!StringUtils.isEmpty(status))
        {
            if (status.equalsIgnoreCase("Open"))
            {
                completedInd = false;
            } else if (status.equalsIgnoreCase("Closed"))
            {
                completedInd = true;
            }
        }

        return this.sirPcrDao.getSirsByCriteria(completedInd, sirType, sirNumber);

    }

    public void closeSirs(List<String> sirNumbers)
    {
        for (String sirNumber : sirNumbers)
        {
            SirPcrDto dto = this.getSirByNumber(sirNumber);
            dto.setCompletedInd(true);
            this.sirPcrDao.update(dto);
        }
    }
}
