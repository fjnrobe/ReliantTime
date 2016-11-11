package dtos;

import java.util.ArrayList;

/**
 * Created by Robertson_Laptop on 10/27/2016.
 */
public class RevenueSummaryDto {
    private String year;
    private double billedHours;
    private double billedGross;
    private double receivedHours;
    private double receivedGross;
    private double dueHours;
    private double dueGross;

    private double totHours;
    private double totGross;

    private ArrayList<DeductionDto> deductions;

    private double totDeductions;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public double getTotHours() {
        return totHours;
    }

    public void setTotHours(double totHours) {
        this.totHours = totHours;
    }

    public double getTotGross() {
        return totGross;
    }

    public void setTotGross(double totGross) {
        this.totGross = totGross;
    }

    public ArrayList<DeductionDto> getDeductions() {
        return deductions;
    }

    public void setDeductions(ArrayList<DeductionDto> deductions) {
        this.deductions = deductions;
    }

    public double getTotDeductions() {
        return totDeductions;
    }

    public void setTotDeductions(double totDeductions) {
        this.totDeductions = totDeductions;
    }



}
