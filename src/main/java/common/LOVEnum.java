package common;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public enum LOVEnum {
    SUB_PROCESS_TYPE ("subprocessType"),
    ACTIVITY_TYPE ("activityType"),
    PRIMAVERA_TYPE ("primaveratype"),
    DEDUCTION_CATEGORY ("deductionCategory"),
    DEDUCTION_TYPE("deductionType");

    private String value;

    private LOVEnum (String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    public static LOVEnum fromString(String value) {
        for (LOVEnum b : LOVEnum .values()) {
            if (b.getValue().equals(value)) { return b; }
        }
        return null;
    }
}
