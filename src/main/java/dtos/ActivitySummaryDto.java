package dtos;

/**
 * Created by Robertson_Laptop on 7/12/2017.
 */
public class ActivitySummaryDto {

    private String subprocessDesc;
    private String activityDesc;
    private double hours;

    public String getSubprocessDesc() {
        return subprocessDesc;
    }

    public void setSubprocessDesc(String subprocessDesc) {
        this.subprocessDesc = subprocessDesc;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}
