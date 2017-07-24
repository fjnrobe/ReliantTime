package dtos;

/**
 * Created by Robertson_Laptop on 1/16/2017.
 */
public class SirHoursSummaryDto {

    SirPcrDto sirPcrDto;
    String activityDesc;
    double hours;

    public SirPcrDto getSirPcrDto() {
        return sirPcrDto;
    }

    public void setSirPcrDto(SirPcrDto sirPcrDto) {
        this.sirPcrDto = sirPcrDto;
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
