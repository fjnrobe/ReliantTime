package managers;

import com.mongodb.client.MongoDatabase;
import common.MonthYear;
import daos.LogDao;
import daos.SIRPCRDao;
import dtos.LogDto;
import dtos.ReportRecordDto;
import dtos.SirPcrViewDto;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.bson.types.ObjectId;
import spark.Response;
import utilities.DateTimeUtils;
import utilities.NumberUtils;
import utilities.SortUtils;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by Robertson_Laptop on 9/7/2016.
 */
public class ReportsManager {

    private final LogDao logDao;
    private final SIRPCRDao sirPcrDao;

    public ReportsManager(MongoDatabase reliantDb) {

        logDao = new LogDao(reliantDb);
        sirPcrDao = new SIRPCRDao(reliantDb);
    }

    public List<LogDto> getInnotasHours(String startDate)
    {
        return this.logDao.getInnotasHours(startDate);
    }

    public byte [] createMonthlyStatus(MonthYear monthYear)
    {
        byte [] outArray = new byte[0];

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Status Report");
        int rowIdx = 0;
        int colIdx = 0;
        String line = "";
       // String fileName = "Monthly_Status_" + monthYear.getMonthName() + "_" + monthYear.getYearName() + ".html";

        String statusDesc = "";
        String estCompleteDate = "";
        String sirNumber = "";

        int prevWeek = -1;
        double monthTotalHours = 0.0;
        List<ReportRecordDto> reportData =this.generateMonthAtAGlanceReport(monthYear);
        Map<ObjectId, SirPcrViewDto> sirSummary = this.logDao.getSirSummaryRangeInfo();


        try {

            //Create a new row in current sheet
            Row row = sheet.createRow(rowIdx++);
            //Create a new cell in current row
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row (0-based)
                    0, //first column (0-based)
                    6 //last column (0-based)
            ));
            Cell cell = row.createCell(0);
            //write the header row
            cell.setCellValue("Jeff Robertson's Monthly Status Report For " +
                                monthYear.getMonthName() + " " +
                                monthYear.getYearName());
            cell.setCellStyle(this.createStyle(workbook, true, true,true,true));


            for (ReportRecordDto data : reportData)
            {

                //put out the start date / end date for the current sir
                SirPcrViewDto sirData = sirSummary.get(data.getSirPcrViewDto().getSirPcrDto().getId());

                //if this is a break on week, insert a week header row
                if (data.getFirstDayOfWeek() > prevWeek)
                {

                    row = sheet.createRow(rowIdx++);
                    row = sheet.createRow(rowIdx++);

                    //format as Report Date: MonthName dd - dd
                   GregorianCalendar calStart =
                           DateTimeUtils.stringToGregorianCalendar( data.getSirPcrViewDto().getLogDto().getLogDate(), DateTimeUtils.DateFormats.YYYYMMDD);

                    line = "Report Date: " +
                            calStart.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " +
                            String.valueOf(data.getFirstDayOfWeek()) + " - " +
                            String.valueOf(data.getLastDayOfWeek());

                    cell = row.createCell(0);
                    cell.setCellValue(line);
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    colIdx = 0;
                    row = sheet.createRow(rowIdx++);
                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Activity Type");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Status");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Est Compl. Date");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Total LOE Hrs");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Sub Process/Activity");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("SCR / SIR / PCR");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Activity Reference and Description of Accomplishments/Tasks");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    row = sheet.createRow(rowIdx++);

                    prevWeek = data.getFirstDayOfWeek();

                }

                //if the sir is a sir/ pcr - then set the status / est completion dates
                if (!data.getSirPcrViewDto().getSirPcrDto().getSirType().equals("OTHER"))
                {

                    //initialize a calendar to the last day of the week
                    Calendar week = Calendar.getInstance();
                    week.set(monthYear.getYear(), monthYear.getMonth() - 1, data.getLastDayOfWeek());

                    //if the activity is still active after the end of the current week
                    Calendar completionDay = null;
                    if (DateTimeUtils.stringToGregorianCalendar(
                            sirData.getLatestLogDate(), DateTimeUtils.DateFormats.MMDDYYYY).after(week))
                    {
                        //set the status to ongoing
                        statusDesc = "Ongoing";

                        //if the sir is currently done, set the completion date to the friday of the week it was completed
                        if (data.getSirPcrViewDto().getSirPcrDto().getCompletedInd() == true)
                        {
                            //set the completion day to the friday in the week that the activity finished
                            completionDay = DateTimeUtils.getLastFridayOfWeek(
                                    DateTimeUtils.stringToGregorianCalendar(sirData.getLatestLogDate(), DateTimeUtils.DateFormats.YYYYMMDD));
                        }
                        //if still ongoing, set to the friday of the next week (we are in)
                        else
                        {
                            //set the completion day to the friday in the next week
                            completionDay = DateTimeUtils.getLastFridayOfNextWeek(monthYear, data.getWeekOfMonth());
                        }
                    }
                    //if it is done, set to the friday of the week in which it ends
                    else
                    {

                        //if the sir is currently done, set the completion date to the friday of the week it was completed
                        if (data.getSirPcrViewDto().getSirPcrDto().getCompletedInd() == false)
                        {
                            statusDesc = "Done";
                            //set the completion day to the friday in the week that the activity finished
                            completionDay = DateTimeUtils.getLastFridayOfWeek(
                                    DateTimeUtils.stringToGregorianCalendar(sirData.getLatestLogDate(), DateTimeUtils.DateFormats.YYYYMMDD));
                        }
                        //if still ongoing, set to the friday of the next week (we are in)
                        else
                        {
                            //set the status to ongoing
                            statusDesc = "Ongoing";

                            //set the completion day to the friday in the next week
                            completionDay = DateTimeUtils.getLastFridayOfNextWeek(monthYear, data.getWeekOfMonth());
                        }

                    }

                    estCompleteDate = DateTimeUtils.DateToString(completionDay, DateTimeUtils.DateFormats.MMDDYYYY, true);
                }
                else
                {
                    statusDesc = "Ongoing";
                    estCompleteDate = "NA";
                }

                if (Integer.parseInt(data.getSirPcrViewDto().getSirPcrDto().getSirPcrNumber()) <= 0)
                {
                    sirNumber = "";
                }
                else
                {
                    sirNumber = data.getSirPcrViewDto().getSirPcrDto().getSirPcrNumber();
                }

                colIdx = 0;
                row = sheet.createRow(rowIdx++);
                cell = row.createCell(colIdx++);
                cell.setCellValue(data.getSirPcrViewDto().getLogDto().getActivityDesc());
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(statusDesc);
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(estCompleteDate);
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(String.valueOf(NumberUtils.roundHours(data.getHoursWithinWeek())));
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(data.getSirPcrViewDto().getSirPcrDto().getSubProcessDesc());
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(sirNumber);
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(data.getSirPcrViewDto().getSirPcrDto().getSirDesc());
                HSSFCellStyle style = this.createStyle(workbook, false, false, true, false);
                style.setWrapText(true);
                cell.setCellStyle(style);

                monthTotalHours += data.getHoursWithinWeek();

            }

            row = sheet.createRow(rowIdx++);
            row = sheet.createRow(rowIdx++);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Total Monthly Hours: " + String.valueOf(NumberUtils.roundHours(monthTotalHours)));
            cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            workbook.write(outByteStream);
            workbook.close();
            outArray = outByteStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

       return outArray;
    }

    private HSSFCellStyle createStyle(HSSFWorkbook workbook,
                                      boolean bold, boolean centerAlign, boolean withBorder, boolean withBackground)
    {
        HSSFFont font = workbook.createFont();
        HSSFCellStyle style = workbook.createCellStyle();

        if (bold) {
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
        }

        if (centerAlign)
        {
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        }

        if (withBorder)
        {
            style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
            style.setBottomBorderColor(
                    IndexedColors.BLACK.getIndex());
            style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            style.setLeftBorderColor(
                    IndexedColors.BLACK.getIndex());
            style.setBorderRight(XSSFCellStyle.BORDER_THIN);
            style.setRightBorderColor(
                    IndexedColors.BLACK.getIndex());
            style.setBorderTop(XSSFCellStyle.BORDER_THIN);
            style.setTopBorderColor(
                    IndexedColors.BLACK.getIndex());
        }

        if (withBackground)
        {
            style.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);
            style.setFillPattern(HSSFCellStyle.FINE_DOTS);
            style.setAlignment(HSSFCellStyle.ALIGN_FILL);
        }

        return style;
    }


    public List<ReportRecordDto> generateMonthAtAGlanceReport(MonthYear monthYear)
    {
        //the key will be the week number and the sir number
        Map<String, ReportRecordDto> reportRecordMap = new HashMap<String,ReportRecordDto>();

        List<ReportRecordDto> reportRecords = new ArrayList<ReportRecordDto>();

        //get a summary of the sirs for the incoming month/year
        Calendar startDate = Calendar.getInstance();
        startDate.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(monthYear.getYear(), monthYear.getMonth() - 1, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        List<LogDto> logsInMonth = this.logDao.getLogsByDateRange(
                DateTimeUtils.DateToString(startDate, DateTimeUtils.DateFormats.YYYYMMDD, false),
                DateTimeUtils.DateToString(endDate, DateTimeUtils.DateFormats.YYYYMMDD, false));

        for (LogDto logInMonth : logsInMonth)
        {
            SirPcrViewDto viewDto = new SirPcrViewDto();
            viewDto.setLogDto(logInMonth);
            viewDto.setSirPcrDto(this.sirPcrDao.getSirById(logInMonth.getSirPcrId()));

            //get the entry in the report set for the current sir in the current week
            Calendar cal = DateTimeUtils.isValidDate(logInMonth.getLogDate(),
                                            DateTimeUtils.DateFormats.YYYYMMDD);

            int weekInMonth = cal.get(Calendar.WEEK_OF_MONTH);
            String entryKey = this.reportRecordEntryKey(weekInMonth, logInMonth.getSirPcrId().toString());

            ReportRecordDto reportRecord = reportRecordMap.get(entryKey);
            //if we haven't added one yet, add it now
            if (reportRecord == null)
            {
                reportRecord = new ReportRecordDto();
                reportRecord.setSirPcrViewDto(viewDto);
                reportRecord.setWeekOfMonth(cal.get(Calendar.WEEK_OF_MONTH));

                //initialize a calendar to the current month being processed
                Calendar week = Calendar.getInstance();
               // week.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                week.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);

                //to get the first day of the week - create a calendar and then set the week to the sir's week in the month
                //and set the day of the week to Sunday - the first day of each week

                //need to capture the last day of month - below when adding 6 days to get the end of the
                //week, we could go into the next month
                int lastDayOfMonth =  week.getActualMaximum(Calendar.DAY_OF_MONTH);
                week.set(Calendar.WEEK_OF_MONTH, cal.get(Calendar.WEEK_OF_MONTH));
                week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                reportRecord.setFirstDayOfWeek(week.get(Calendar.DATE));

                //if the day of week is greater than current day, we went back to the prior month - need to back
                //up to the first of the month (which is always 1)
                if (reportRecord.getFirstDayOfWeek() > cal.get(Calendar.DAY_OF_MONTH))
                {
                    reportRecord.setFirstDayOfWeek(1);
                }

                //to get the end of the week, add 6 days to the calendar
                week.add(Calendar.DAY_OF_WEEK, 6);
                reportRecord.setLastDayOfWeek(week.get(Calendar.DATE));

                //if the last day of the week is before the first day of the week, we've past into the next month.
                //in this case, set the last day of the week to the last day of the month
                if (reportRecord.getLastDayOfWeek() < reportRecord.getFirstDayOfWeek() )
                {
                    reportRecord.setLastDayOfWeek(lastDayOfMonth);
                }

                reportRecordMap.put(entryKey, reportRecord);
            }

            //finally, we need to tally the number of hours of the current sir that fall within the current week
            reportRecord.setHoursWithinWeek(reportRecord.getHoursWithinWeek() + viewDto.getLogDto().getHours());
        }
        Iterator iterator =  reportRecordMap.values().iterator();
        while (iterator.hasNext())
        {
            ReportRecordDto record = (ReportRecordDto) iterator.next();
            record.setHoursWithinWeek(NumberUtils.roundHours(record.getHoursWithinWeek()));
            reportRecords.add(record);

        }

        return SortUtils.sortMonthlyReportByWeek(reportRecords);
    }


    private String reportRecordEntryKey(int weekNumber, String sirNumber)
    {
        return String.valueOf(weekNumber) + "|" + sirNumber;
    }

    public List<MonthYear> getAllMonthsWithLoggedPeriods( boolean ascending)
    {
        List<LogDto> allLogs = logDao.getLogsByDateRange(DateTimeUtils.LOW_DATE,
                DateTimeUtils.HIGH_DATE);

        //build a set of unique start months
        SortedSet<MonthYear> monthYears = new TreeSet<MonthYear>();

        for (LogDto allLog : allLogs)
        {

            MonthYear monthYear = new MonthYear(allLog.getLogDate());

            GregorianCalendar calDate = DateTimeUtils.stringToGregorianCalendar(allLog.getLogDate(), DateTimeUtils.DateFormats.YYYYMMDD);

            monthYear.setMonthName(calDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
            monthYear.setYearName(String.valueOf(calDate.get(Calendar.YEAR)));

            String monthNumber = "";
            int month = calDate.get(Calendar.MONTH) + 1;

            monthYear.setMonth(month);
            monthYear.setYear(calDate.get(Calendar.YEAR));

            if (month < 10)
            {
                monthNumber = "0" + String.valueOf(month);
            }
            else
            {
                monthNumber = String.valueOf(month);
            }
            String sortKey = monthYear.getYearName() + monthNumber;

            monthYear.setSortKey(sortKey);

            monthYears.add(monthYear);
        }

        ArrayList<MonthYear> aList = new ArrayList<MonthYear>();
        for (MonthYear monthYear : monthYears)
        {
            aList.add(monthYear);
        }

        return SortUtils.sortMonthYear(aList, ascending);
    }

}
