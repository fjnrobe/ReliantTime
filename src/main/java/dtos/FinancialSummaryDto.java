package dtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robertson_Laptop on 10/30/2016.
 */
public class FinancialSummaryDto {


    private String currentYear;
    private List<DeductionUiDto>  expenseList;
    private double billedHours;
    private double billedGross;
    private double receivedHours;
    private double receivedGross;
    private double dueHours;

    public List<FinancialSummaryByYearUIDto> getSummaryByYear() {
        return summaryByYear;
    }

    public void setSummaryByYear(List<FinancialSummaryByYearUIDto> summaryByYear) {
        this.summaryByYear = summaryByYear;
    }

    private List<FinancialSummaryByYearUIDto> summaryByYear;

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public double getBilledHours() {
        return billedHours;
    }

    public void setBilledHours(double billedHours) {
        this.billedHours = billedHours;
    }

    public double getBilledGross() {
        return billedGross;
    }

    public void setBilledGross(double billedGross) {
        this.billedGross = billedGross;
    }

    public double getReceivedHours() {
        return receivedHours;
    }

    public void setReceivedHours(double receivedHours) {
        this.receivedHours = receivedHours;
    }

    public double getReceivedGross() {
        return receivedGross;
    }

    public void setReceivedGross(double receivedGross) {
        this.receivedGross = receivedGross;
    }

    public double getDueHours() {
        return dueHours;
    }

    public void setDueHours(double dueHours) {
        this.dueHours = dueHours;
    }

    public double getDueGross() {
        return dueGross;
    }

    public void setDueGross(double dueGross) {
        this.dueGross = dueGross;
    }

    private double dueGross;

    public FinancialSummaryDto()
    {
        this.billedHours = 0;
        this.billedGross = 0;
        this.receivedHours = 0;
        this.receivedGross = 0;
        this.dueHours = 0;

        this.expenseList = new ArrayList<DeductionUiDto>();
        this.summaryByYear = new ArrayList<FinancialSummaryByYearUIDto>();
    }



    public List<DeductionUiDto> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<DeductionUiDto> expenseList) {
        this.expenseList = expenseList;
    }



}
