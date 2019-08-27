package dtos;

import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by Robertson_Laptop on 6/27/2016.
 */
public class LogDto extends BaseDto{

    private ObjectId sirPcrId;
    private String legacySirId;
    private boolean billableInd;
    private String logDate;
    private String startTime;
    private String endTime;
    private String note;
    private String primaveraDesc;
    private String activityDesc;
    private double hours;
    private String sirPcrNumber;

    public String getSirPcrNumber() {
        return sirPcrNumber;
    }

    public void setSirPcrNumber(String sirPcrNumber) {
        this.sirPcrNumber = sirPcrNumber;
    }

    public String getSirNickName() {
        return sirNickName;
    }

    public void setSirNickName(String sirNickName) {
        this.sirNickName = sirNickName;
    }

    private String sirNickName;

    public String getLegacySirId() {
        return legacySirId;
    }

    public void setLegacySirId(String legacySirId) {
        this.legacySirId = legacySirId;
    }

    public boolean getBillableInd() {

        return billableInd;
    }

    public void setBillableInd(boolean billableInd) {
        this.billableInd = billableInd;
    }

    public String getLogDate() {
        return logDate;
    }


    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPrimaveraDesc() {
        return primaveraDesc;
    }

    public void setPrimaveraDesc(String primaveraDesc) {
        this.primaveraDesc = primaveraDesc;
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

    public ObjectId getSirPcrId() {
        return sirPcrId;
    }

    public void setSirPcrId(ObjectId sirPcrId) {
        this.sirPcrId = sirPcrId;
    }


}
