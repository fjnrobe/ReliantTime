package dtos;

import org.bson.types.ObjectId;

/**
 * Created by Robertson_Laptop on 1/16/2017.
 */
public class SirHoursSummarySearchDto {

    ObjectId sirPcrId;
    String startDate;
    String endDate;

    public ObjectId getSirPcrId() {
        return sirPcrId;
    }

    public void setSirPcrId(ObjectId sirPcrId) {
        this.sirPcrId = sirPcrId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
