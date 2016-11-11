package dtos;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public class LovBaseDto extends BaseDto{
    private String lovDescription;
    private String lovCode;

    public LovBaseDto(String code, String desc)
    {
        this.lovCode = code;
        this.lovDescription = desc;
    }

    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }


    public String getLovDescription() {
        return lovDescription;
    }

    public void setLovDescription(String lovDescription) {
        this.lovDescription = lovDescription;
    }



}
