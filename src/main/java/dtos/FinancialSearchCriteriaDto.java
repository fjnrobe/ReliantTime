package dtos;

/**
 * Created by Robertson_Laptop on 11/5/2016.
 */
public class FinancialSearchCriteriaDto {
    private String poNumber;
    private String invoiceNumber;
    private String year;

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
