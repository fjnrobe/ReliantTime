package dtos;

import common.MonthYear;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Robertson_Laptop on 8/10/2016.
 */
public class MonthYearDto {
    private String sortKey;

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private String monthName;
    private String yearName;
    private int month;
    private int year;

}
