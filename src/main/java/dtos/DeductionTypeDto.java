package dtos;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public class DeductionTypeDto extends LovBaseDto {

    private String deductionCode;
    private String deductionTypeCode;
    private String grossDedInd;



    public DeductionTypeDto(String code, String desc, String deductionCode, String deductionTypeCode, String grossInd )
    {
        super(code, desc);
        this.deductionCode = deductionCode;
        this.deductionTypeCode = deductionTypeCode;
        this.grossDedInd = grossInd;
    }

    public String getGrossDedInd() {
        return grossDedInd;
    }

    public void setGrossDedInd(String grossDedInd) {
        this.grossDedInd = grossDedInd;
    }

    public String getDeductionTypeCode() {
        return deductionTypeCode;
    }

    public void setDeductionTypeCode(String deductionTypeCode) {
        this.deductionTypeCode = deductionTypeCode;
    }

    public String getDeductionCode() {
        return deductionCode;
    }

    public void setDeductionCode(String deductionCode) {
        this.deductionCode = deductionCode;
    }


}
