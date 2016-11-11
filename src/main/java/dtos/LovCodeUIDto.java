package dtos;

/**
 * Created by Robertson_Laptop on 9/2/2016.
 */
public class LovCodeUIDto {
    LovBaseDto lovBaseDto;

    public LovBaseDto getLovBaseDto() {
        return lovBaseDto;
    }

    public void setLovBaseDto(LovBaseDto lovBaseDto) {
        this.lovBaseDto = lovBaseDto;
    }

    public boolean isCanBeDeleted() {
        return canBeDeleted;
    }

    public void setCanBeDeleted(boolean canBeDeleted) {
        this.canBeDeleted = canBeDeleted;
    }

    public boolean isBeingEdited() {
        return isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }

    boolean canBeDeleted;
    boolean isBeingEdited;
}
