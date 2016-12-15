package dtos;

import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * Created by Robertson_Laptop on 6/27/2016.
 */
public class SirPcrDto extends BaseDto {

    private String legacyId;
    private String sirType;
    private String sirPcrNumber;
    private String sirDesc;
    private boolean completedInd;
    private String nickName;
    private String subProcessDesc;
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private ArrayList<LogDto> logs;

    public SirPcrDto()
    {
        logs = new ArrayList<LogDto>();
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public ArrayList<LogDto> getLogs() {
        return logs;
    }







    public String getSirType() {
        return sirType;
    }

    public void setSirType(String sirType) {
        this.sirType = sirType;
    }

    public String getSirPcrNumber() {
        return sirPcrNumber;
    }

    public void setSirPcrNumber(String sirPcrNumber) {
        this.sirPcrNumber = sirPcrNumber;
    }

    public String getSirDesc() {
        return sirDesc;
    }

    public void setSirDesc(String sirDesc) {
        this.sirDesc = sirDesc;
    }

    public boolean getCompletedInd() {

            return completedInd;

    }

    public void setCompletedInd(boolean completedInd) {
        this.completedInd = completedInd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSubProcessDesc() {
        return subProcessDesc;
    }

    public void setSubProcessDesc(String subProcessDesc) {
        this.subProcessDesc = subProcessDesc;
    }



}
