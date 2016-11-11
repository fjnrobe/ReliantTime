package dtos;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public class DeductionCategory extends LovBaseDto {

    private String deductionCode;

    public DeductionCategory(String code, String desc, String deductionCode)
    {
        super(code, desc);
        this.deductionCode = deductionCode;

    }

    public String getDeductionCode() {
        return deductionCode;
    }

    public void setDeductionCode(String deductionCode) {
        this.deductionCode = deductionCode;
    }
}
