package enums;

/**
 * Created by Robertson_Laptop on 8/24/2016.
 */
public enum SIR_Status {
    ACTIVE("N"),
    COMPLETED("Y"),
    All("A");

    private String status;

    SIR_Status(String status) {
        this.status = status;
    }

    public String status() {
        return status;

    }
}
