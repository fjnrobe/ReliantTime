package dtos;

/**
 * Created by Robertson_Laptop on 9/10/2016.
 */

public class ReportRecordDto {

    public SirPcrViewDto getSirPcrViewDto() {
        return sirPcrViewDto;
    }

    public void setSirPcrViewDto(SirPcrViewDto sirPcrViewDto) {
        this.sirPcrViewDto = sirPcrViewDto;
    }

    private SirPcrViewDto sirPcrViewDto;
    private int weekOfMonth;
    private int dayOfMonth;
    private int firstDayOfWeek;
    private int lastDayOfWeek;
    private int dayOfWeek;
    private double hoursWithinWeek;
    private double hoursForDay;

    /**
     * @return the weekOfMonth
     */
    public int getWeekOfMonth() {
        return weekOfMonth;
    }

    /**
     * @param weekOfMonth the weekOfMonth to set
     */
    public void setWeekOfMonth(int weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    /**
     * @return the firstDayOfWeek
     */
    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /**
     * @param firstDayOfWeek the firstDayOfWeek to set
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    /**
     * @return the lastDayOfWeek
     */
    public int getLastDayOfWeek() {
        return lastDayOfWeek;
    }

    /**
     * @param lastDayOfWeek the lastDayOfWeek to set
     */
    public void setLastDayOfWeek(int lastDayOfWeek) {
        this.lastDayOfWeek = lastDayOfWeek;
    }

    /**
     * @return the hoursWithinWeek
     */
    public double getHoursWithinWeek() {
        return hoursWithinWeek;
    }

    /**
     * @param hoursWithinWeek the hoursWithinWeek to set
     */
    public void setHoursWithinWeek(double hoursWithinWeek) {
        this.hoursWithinWeek = hoursWithinWeek;
    }


    /**
     * @return the hoursForDay
     */
    public double getHoursForDay() {
        return hoursForDay;
    }

    /**
     * @param hoursForDay the hoursForDay to set
     */
    public void setHoursForDay(double hoursForDay) {
        this.hoursForDay = hoursForDay;
    }

    /**
     * @return the dayOfMonth
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * @param dayOfMonth the dayOfMonth to set
     */
    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * @return the dayOfWeek
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


}

