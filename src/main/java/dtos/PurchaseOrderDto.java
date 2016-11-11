package dtos;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Robertson_Laptop on 11/4/2016.
 */
public class PurchaseOrderDto extends BaseDto{

    private String poNumber;

    private String poTitle;
    private double totalHours;
    private double totalHoursBilled;
    private double hourlyRate;
    private double passthruRate;
    private Date startDate;
    private Date endDate;
    ArrayList<InvoiceDto> invoices;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }




    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }


    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getPassthruRate() {
        return passthruRate;
    }

    public void setPassthruRate(double passthruRate) {
        this.passthruRate = passthruRate;
    }

    public double getTotalHoursBilled() {
        return totalHoursBilled;
    }

    public void setTotalHoursBilled(double totalHoursBilled) {
        this.totalHoursBilled = totalHoursBilled;
    }

    public ArrayList<InvoiceDto> getInvoices() {
        return invoices;
    }

    public void setInvoiceMonths(ArrayList<InvoiceDto> invoices) {
        this.invoices = invoices;
    }




}
