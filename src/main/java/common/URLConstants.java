package common;

/**
 * Created by Robertson_Laptop on 8/8/2016.
 */
public class URLConstants
{
        public static final String HOME = "/";
        public static final String LOGIN = "/login";
        public static final String ERROR_PAGE = "/internal_error";
        //mapped
        public static final String CALENDAR = "/calendar";
        //mapped
        public static final String CALENDAR_DEFAULT = "/calendar/default/:yearMonth";
        //mapped
        public static final String CALENDAR_DATA = "/calendar/data/:yearMonth";
        //mapped
        public static final String CALENDAR_INIT = "/calendar/init";
        public static final String DAY_ENTRY_NO_DATE = "/dayEntry";
        public static final String DAY_ENTRY = "/dayEntry/:date";
        //mapped
        public static final String DAY_DATA = "/dayEntry/data/:status";
        public static final String NEW_SIR = "/newSir/:date";
        public static final String NEW_LOG = "/newLog";
        public static final String SAVE_SIR = "/saveSir";
        public static final String SIR_DETAIL = "/sirDetail/:sirId/:returnPageParms";
        public static final String UPDATE_SIR = "/updateSir";
        public static final String EDIT_LOG = "/editLog/:logId";
        //mapped
        public static final String GET_SIR_AND_LOGS = "/sirGet/:sirId";
        public static final String DELETE_SIR = "/sirDelete/:sirId";
        public static final String DELETE_LOG = "/logDelete/:logId";
        public static final String LOV_MAINT = "/lovTables";
        //mapped
        public static final String LOV_LIST = "/lovTables/lovList/:lovCode";
        public static final String LOV_UPDATE = "/lovTables/update";
        public static final String LOV_DELETE = "/lovTables/delete";
        public static final String LOV_ADD = "/lovTables/add";
        public static final String REPORT_INNOTAS = "/reports/innotas/*";
        public static final String REPORT_MONTHLY_STATUS = "/reports/monthlyStatus";
        public static final String REPORT_MONTHLY_STATUS_XLS = "/reports/monthlyStatus/:yearMonth";
        public static final String REPORT_MONTHLY_INVOICE_XLS = "/reports/invoice/:invoiceNumber";
        public static final String REPORT_MONTHLY_TIMESHEET_XLS = "/reports/timesheet/:yearMonth";
        public static final String REPORT_MONTHLY_INVOICE_GETFILE = "/reports/monthlyStatus/getFile/:fileName";
        public static final String REPORT_MONTHLY_STATUS_EMAILFILE = "/reports/monthlyStatus/emailFile";
        public static final String REPORT_MONTHLY_INVOICE_EMAILFILE = "/reports/invoice/emailFile";
        public static final String REPORT_ACTIVITY_SUMMARY = "/reports/activitySummary";
        public static final String REPORT_ACTIVITY_LIST = "/reports/activityList/:activityDesc/:fromDate/:toDate";

        public static final String MAINT_SIR_LIST ="/sirList";
        public static final String DEDUCTIONS = "/deductions";
        public static final String DEDUCTIONS_UPDATE = "/deductions/update";
        //mapped
        public static final String DEDUCTIONS_CATEGORY_TYPES = "/deductions/categoryTypes/:deductionCategory";
        public static final String DEDUCTIONS_DELETE = "/deductions/delete";
        public static final String REVENUE = "/revenue";
        public static final String REVENUE_UPDATE = "/revenue/update";
        public static final String REVENUE_DELETE = "/revenue/delete";
        //mapped
        public static final String REVENUE_DATA = "revenue/data/:year";
        //mapped
        public static final String REVENUE_NEXT_INVOICE = "revenue/nextInvoice";
        public static final String FINANCIAL_SUMMARY = "/financialSummary";
        //mapped
        public static final String FINANCIAL_SUMMARY_DATA = "/financialSummary/data/:year";
        public static final String PURCHASE_ORDERS = "/purchaseOrders";
        public static final String PURCHASE_ORDER_DATA = "/purchaseOrders/data/:poNumber";
}
