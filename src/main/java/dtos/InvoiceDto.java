package dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.MonthYear;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by Robertson_Laptop on 10/25/2016.
 */
public class InvoiceDto extends BaseDto {

    private String poNumber;
    private String invoiceNumber;
    private MonthYear monthYear;
    private double hours;
    private double totalGross;
    private Date invoiceDate;
    private Date receivedDate;
    private double priorHoursRemaining;
    private double priorAmtRemaining;
    private double hoursRemaining;
    private double amtRemaining;

    @JsonIgnore
    public MonthYear getMonthYear() {
        return monthYear;
    }
    public String getMonthYearAsString()
    {
        return monthYear.getSortKey();
    }
    public void setMonthYear(MonthYear monthYear) {
        this.monthYear = monthYear;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getTotalGross() {
        return totalGross;
    }

    public void setTotalGross(double totalGross) {
        this.totalGross = totalGross;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

       public double getPriorHoursRemaining() {
        return priorHoursRemaining;
    }

    public void setPriorHoursRemaining(double priorHoursRemaining) {
        this.priorHoursRemaining = priorHoursRemaining;
    }

    public double getPriorAmtRemaining() {
        return priorAmtRemaining;
    }

    public void setPriorAmtRemaining(double priorAmtRemaining) {
        this.priorAmtRemaining = priorAmtRemaining;
    }

    public double getHoursRemaining() {
        return hoursRemaining;
    }

    public void setHoursRemaining(double hoursRemaining) {
        this.hoursRemaining = hoursRemaining;
    }

    public double getAmtRemaining() {
        return amtRemaining;
    }

    public void setAmtRemaining(double amtRemaining) {
        this.amtRemaining = amtRemaining;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
}
