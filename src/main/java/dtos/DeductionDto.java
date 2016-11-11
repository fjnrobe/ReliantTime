package dtos;

import java.util.Date;

/**
 * Created by Robertson_Laptop on 10/16/2016.
 */
public class DeductionDto extends BaseDto {

    private String deductionCategory;
    private String deductionType;
    private Date postDate;

    private double amount;
    private String note;

    public String getDeductionCategory() {
        return deductionCategory;
    }

    public void setDeductionCategory(String deductionCategory) {
        this.deductionCategory = deductionCategory;
    }

    public String getDeductionType() {
        return deductionType;
    }

    public void setDeductionType(String deductionType) {
        this.deductionType = deductionType;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
