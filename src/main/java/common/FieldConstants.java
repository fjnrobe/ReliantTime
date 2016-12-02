package common;

/**
 * Created by Robertson_Laptop on 6/4/2016.
 */
public class FieldConstants {


    //system columns
    public static final String CREATE_DATE = "createDate";
    public static final String UPDATE_DATE = "updateDate";

    //these are mongoDb collection fields in COLLECTION_SIR_PCR
    public static final String COLLECTION_ID = "_id";
    public static final String LEGACY_SIR_ID = "legacySirId";
    public static final String SIR_PCR_NUMBER = "sirPcrNumber";
    public static final String SUBPROCESS_DESC = "subprocessDesc";
    public static final String SIR_TYPE = "sirType";
    public static final String SIR_DESC = "sirDesc";
    public static final String NICKNAME = "NickName";
    public static final String COMPLETED_IND = "CompletedInd";
    public static final String LEGACY_SUBPROCESS_ID = "legacySubprocessId";

    //these are the mongoDb collection fields for COLLECTION_LOG
    public static final String LEGACY_LOG_ID = "legacyLogId";
    //public static final String LEGACY_SIR_ID = "legacySirId";
    public static final String SIR_PCR_ID = "sirPcrId"; //ref value in COLLECTION_LOG that refers to the sir
    public static final String LOG_DATE = "logDate";    //stored as Date()
    public static final String START_TIME = "startTime"; //stored as HHMM
    public static final String END_TIME = "endTime";     //stored as HHMM
    public static final String LEGACY_ACTIVITY_ID = "legacyActivityId";
    public static final String LEGACY_PRIMAVERA_ID = "legacyPrimaVeraId";
    public static final String PRIMAVERA_DESC  = "primaveraDesc";
    public static final String ACTIVITY_DESC = "activityDesc";
    public static final String NOTE = "note";
    public static final String BILLABLE_IND = "billableInd";

    //note: hours is derived - and not necessarily matches to the start/end time -
    //if there are multiple sirs whose start/end times overlap, the hours are prorated
    //across the sirs
    public static final String HOURS = "hours";

    public static final String ERROR_MESSAGE = "errorMessage";

    //these are the fields for the LOV Collection

    public static final String LOV_CODE = "lovCode"; //identifies the code type
    public static final String PRIMAVERA_CODE = "primaveraCode"; //for the primavera type only
    public static final String LOV_CODE_PRIMAVERA_TYPE = "primaveratype";
    public static final String LOV_CODE_ACITITY_TYPE = "activityType";
    public static final String LOV_CODE_SUBPROCESS_TYPE = "subprocessType";
    public static final String LOV_DEDUCTION_CATEGORY_CODE = "deductionCode";
    public static final String LOV_CODE_DEDUCTION_CATEGORY = "deductionCategory";
    public static final String LOV_CODE_DEDUCTION_TYPE = "deductionType";
    public static final String LOV_DESC = "lovDesc";
    public static final String DEDUCTION_TYPE_CODE = "deductionTypeCode";
    public static final String DEDUCTION_GROSS_DED_IND = "deductionGrossDedInd";

    //these are the fields for the DEDUCTIONS collection

    //these are DTO document fields
//    public static final String LOV_DEDUCTION_CATEGORY_CODE = "deductionCode";
//    public static final String DEDUCTION_TYPE_CODE = "deductionTypeCode";
    public static final String POST_DATE = "postDate";
    public static final String DEDUCTION_AMT = "deductionAmt";
//    public static final String NOTE = "note";

    //these are the fields for the FINANCIAL_SUMMARY collection
    public static final String MONTH_YEAR = "monthYear";
    //public static final String HOURS = "hours";
    public static final String INVOICE_NUMBER = "invoiceNumber";
    public static final String TOTAL_GROSS = "totalGross";
    public static final String RECEIVED_IND = "receivedInd";
    public static final String INVOICE_DATE = "invoiceDate";
    public static final String RECEIVED_DATE = "receivedDate";
    public static final String PRIOR_HOURS_REMAINING = "priorHoursRemaining";
    public static final String PRIOR_AMT_REMAINING = "priorAmtRemaining";
    public static final String HOURS_REMAINING = "hoursRemaining";
    public static final String AMT_REMAINING = "amtRemaining";

    public static final String SESSION_ID = "sessionId";
    public static final String LAST_LOGIN = "lastLogin";

    //fields for Purchase Order
    public static final String PO_NUMBER = "poNumber";

    public static final String PO_TITLE = "poTitle";
    public static final String TOTAL_HOURS = "totalHours";
    public static final String HOURLY_RATE = "hourlyRate";
    public static final String PASSTHRU_RATE = "passthruRate";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";

    //fields for the Email Properties
    public static final String SMTP_AUTH = "smtpAuth";
    public static final String SMTP_PORT = "smtpPort";
    public static final String SMTP_HOST = "smtpHost";
    public static final String SOURCE_EMAIL = "sourceEmail";
    public static final String SOURCE_PERSONAL = "sourcePersonal";
    public static final String SOURCE_PASSWORD = "sourcePassword";
    public static final String TO_EMAILS = "toEmailAddresses";

}
