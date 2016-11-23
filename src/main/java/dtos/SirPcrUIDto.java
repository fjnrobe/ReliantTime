package dtos;

/**
 * Created by Robertson_Laptop on 10/15/2016.
 */
public class SirPcrUIDto {
    private SirPcrDto sirPcrDto;
    private String linkParms;
    private String action;


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public SirPcrDto getSirPcrDto() {
        return sirPcrDto;
    }

    public void setSirPcrDto(SirPcrDto sirPcrDto) {
        this.sirPcrDto = sirPcrDto;
    }

    public String getLinkParms() {
        return linkParms;
    }

    public void setLinkParms(String linkParms) {
        this.linkParms = linkParms;
    }
}
