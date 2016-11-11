package dtos;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public class PrimaveraType extends LovBaseDto {
    private String primaveraCode;

    public PrimaveraType(String code, String desc, String primaveraCode)
    {
        super(code, desc);
        this.primaveraCode = primaveraCode;
    }

    public String getPrimaveraCode() {
        return primaveraCode;
    }

    public void setPrimaveraCode(String primaveraCode) {
        this.primaveraCode = primaveraCode;
    }
}
