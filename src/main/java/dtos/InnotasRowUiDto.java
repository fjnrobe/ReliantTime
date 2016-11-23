package dtos;

/**
 * Created by Robertson_Laptop on 9/7/2016.
 */
public class InnotasRowUiDto {

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public double[] getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(double[] hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    private String description;
    private double[] hoursPerDay = new double[7];
    private String[] dayOfMonth = new String[7];

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    private String logDate;

    public String[] getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String[] dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
}
