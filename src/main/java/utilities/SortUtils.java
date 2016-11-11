package utilities;

import common.MonthYear;
import dtos.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;



/**
 * Created by Robertson_Laptop on 6/5/2016.
 */
public class SortUtils {

    public enum SortBy {
        SIR_NUMBER,
        LOG_DATE
    }

    private static class LogComparator implements Comparator<LogDto>
    {
        public int compare (LogDto dto1, LogDto dto2)
        {
            return dto1.getLogDate().compareTo(dto2.getLogDate());
        }
    }

    private static class MonthlyReportComparator implements Comparator<ReportRecordDto> {

        public int compare (ReportRecordDto object1, ReportRecordDto object2)
        {
            int retValue = 0;

            if (object1.getWeekOfMonth() < object2.getWeekOfMonth())
            {
                retValue = -1;
            }
            else if (object1.getWeekOfMonth() > object2.getWeekOfMonth())
            {
                retValue = 1;
            }
            //when in the same week, sort by hours descending
            else
            {
                if (object1.getHoursWithinWeek() > object2.getHoursWithinWeek())
                {
                    return -1;
                }
                else if (object1.getHoursWithinWeek() < object2.getHoursWithinWeek())
                {
                    return 1;
                }
            }

            return retValue;
        }
    }

    private static class MonthYearComparator implements Comparator<MonthYear> {

        public int compare (MonthYear object1, MonthYear object2)
        {

            return object1.getSortKey().compareTo(object2.getSortKey());
        }
    }

    private static class DtoSirDocComparator implements Comparator<SirPcrDto> {

        public int compare(SirPcrDto dto1, SirPcrDto dto2) {

            return dto1.getSirPcrNumber().compareTo(dto2.getSirPcrNumber());

        }
    }

    private static class LovComparator implements Comparator<LovBaseDto> {

        public int compare(LovBaseDto dto1, LovBaseDto dto2)
        {
            return dto1.getLovDescription().compareTo(dto2.getLovDescription());
        }
    }

    private static class SIRUIDtoComparator implements Comparator<ExistingSIRUIDto> {

        public int compare(ExistingSIRUIDto dto1, ExistingSIRUIDto dto2) {
            return dto1.getDescription().compareTo(dto2.getDescription());
        }
    }

    private static class InnotasRowUiDtoComparator implements Comparator<InnotasRowUiDto> {

        public int compare(InnotasRowUiDto dto1, InnotasRowUiDto dto2) {
            return dto1.getDescription().compareTo(dto2.getDescription());
        }
    }

    /**
     * sort deductions by type description and posting date
     */
    private static class DeductionUiDtoComparator implements Comparator<DeductionUiDto> {
        public int compare (DeductionUiDto dto1, DeductionUiDto dto2) {

            if (dto1.getDeductionCategoryDesc().compareTo(dto2.getDeductionCategoryDesc()) == 0)
            {
                if (dto1.getDeductionTypeDesc().compareTo(dto2.getDeductionTypeDesc()) == 0)
                {
                    return dto1.getDeductionDto().getPostDate().compareTo(dto2.getDeductionDto().getPostDate());
                }
                else
                {
                    return dto1.getDeductionTypeDesc().compareTo(dto2.getDeductionTypeDesc());
                }
            }
            else {
                return dto1.getDeductionCategoryDesc().compareTo(dto2.getDeductionCategoryDesc());
            }
        }
    }

    private static class FinancialComparator implements Comparator<InvoiceDto> {
        public int compare(InvoiceDto dto1, InvoiceDto dto2) {
            return dto1.getMonthYear().compareTo(dto2.getMonthYear());
        }
    }

    private static class FinancialSummaryByYearComparator implements Comparator<FinancialSummaryByYearUIDto>
    {
        public int compare (FinancialSummaryByYearUIDto dto1,
                            FinancialSummaryByYearUIDto dto2) {
            return dto1.getYear().compareTo(dto2.getYear());
        }
    }

    public static List<FinancialSummaryByYearUIDto> sortFinancialSummaryByYearUIDto(List<FinancialSummaryByYearUIDto> dtos, boolean ascending)
    {
        FinancialSummaryByYearComparator comparator = new FinancialSummaryByYearComparator();

        Collections.sort(dtos, comparator);

        if (!ascending)
        {
            Collections.reverse(dtos);
        }

        return dtos;
    }

    public static List<InvoiceDto> sortFinancialDto(List<InvoiceDto> dtos, boolean ascending)
    {
        FinancialComparator comparator = new FinancialComparator();

        Collections.sort(dtos, comparator);

        if (!ascending)
        {
            Collections.reverse(dtos);
        }

        return dtos;
    }

    public static List<DeductionUiDto> sortDeductionUiDto(List<DeductionUiDto> dtos, boolean ascending)
    {
        DeductionUiDtoComparator comparator = new DeductionUiDtoComparator();

        Collections.sort(dtos, comparator);

        if (!ascending)
        {
            Collections.reverse(dtos);
        }

        return dtos;
    }

    public static List<InnotasRowUiDto> sortInnotasRowUiDto(List<InnotasRowUiDto> dtos, boolean ascending)
    {
        InnotasRowUiDtoComparator innotasRowUiDtoComparator = new InnotasRowUiDtoComparator();

        Collections.sort(dtos, innotasRowUiDtoComparator);

        if (!ascending)
        {
            Collections.reverse(dtos);
        }

        return dtos;
    }

    public static List<LogDto> sortLogs(List<LogDto> dtos, boolean ascending)
    {
        LogComparator comparator = new LogComparator();

        Collections.sort(dtos, comparator);

        if (!ascending)
        {
            Collections.reverse(dtos);
        }

        return dtos;
    }

    public static List<SirPcrDto> sortBySirNumber(List<SirPcrDto> dtos) {
        DtoSirDocComparator dtoSirDocComparator = new DtoSirDocComparator();

        Collections.sort(dtos, dtoSirDocComparator);

        return dtos;
    }

    public static List<MonthYear> sortMonthYear(List<MonthYear> monthYears, boolean ascending)
    {
        Collections.sort(monthYears, new MonthYearComparator());

        //by default the comparator sorts ascending, if the user wants descending, then reverse order
        if (!ascending)
        {
            Collections.reverse(monthYears);
        }

        return monthYears;
    }

    public static List<LovBaseDto> sortLovValues(List<LovBaseDto> lov, boolean ascending)
    {
        Collections.sort(lov, new LovComparator());

        if (!ascending)
        {
            Collections.reverse(lov);
        }

        return lov;
    }

    public static List<ExistingSIRUIDto> sortSIRUiDtos(List<ExistingSIRUIDto> dtos, boolean ascending)
    {
        Collections.sort(dtos, new SIRUIDtoComparator());

        if (!ascending)
        {
            Collections.reverse(dtos);
        }

        return dtos;
    }

    public static List<ReportRecordDto> sortMonthlyReportByWeek(List<ReportRecordDto> reportRecords)
    {
        MonthlyReportComparator comparator = new MonthlyReportComparator();

        Collections.sort(reportRecords, comparator);

        return reportRecords;
    }

}
