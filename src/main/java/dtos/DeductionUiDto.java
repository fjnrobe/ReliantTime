package dtos;

/**
 * Created by Robertson_Laptop on 10/18/2016.
 */
public class DeductionUiDto {

    private DeductionDto deductionDto;
    private String deductionCategoryDesc;
    private String deductionTypeDesc;

    public DeductionUiDto()
    {
        deductionDto = new DeductionDto();
    }

    public String getDeductionTypeDesc() {
        return deductionTypeDesc;
    }

    public void setDeductionTypeDesc(String deductionTypeDesc) {
        this.deductionTypeDesc = deductionTypeDesc;
    }

    public DeductionDto getDeductionDto() {
        return deductionDto;
    }

    public void setDeductionDto(DeductionDto deductionDto) {
        this.deductionDto = deductionDto;
    }

    public String getDeductionCategoryDesc() {
        return deductionCategoryDesc;
    }

    public void setDeductionCategoryDesc(String deductionCategoryDesc) {
        this.deductionCategoryDesc = deductionCategoryDesc;
    }



}
