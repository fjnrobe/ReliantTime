package dtos;

/**
 * Created by Robertson_Laptop on 11/2/2016.
 */
public class FinancialSummaryByYearUIDto {

    private String year;
    private Double grossHours;
    private Double grossIncome;
    private Double grossDeductions;
    private Double netIncome;

    public FinancialSummaryByYearUIDto()
    {
        year = "";
        grossHours = 0.0;
        grossIncome = 0.0;
        grossDeductions = 0.0;
        netIncome = 0.0;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Double getGrossIncome() {
        return grossIncome;
    }

    public void setGrossIncome(Double grossIncome) {
        this.grossIncome = grossIncome;
    }

    public Double getGrossDeductions() {
        return grossDeductions;
    }

    public void setGrossDeductions(Double grossDeductions) {
        this.grossDeductions = grossDeductions;
    }

    public Double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(Double netIncome) {
        this.netIncome = netIncome;
    }



    public Double getGrossHours() {
        return grossHours;
    }

    public void setGrossHours(Double grossHours) {
        this.grossHours = grossHours;
    }


}
