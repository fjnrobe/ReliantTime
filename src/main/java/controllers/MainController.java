package controllers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import common.*;
import dtos.*;
import enums.SIR_Status;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import managers.*;
import org.bson.types.ObjectId;
import spark.*;
import uiManagers.*;
import utilities.DateTimeUtils;
import utilities.JSONUtils;
import utilities.SortUtils;
import utilities.UrlEncoder;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;


import static spark.Spark.*;

/**
 * Created by Robertson_Laptop on 6/4/2016.
 */
public class MainController {
    private final Configuration cfg;
    private final LogManager logManager;
    private final SirPcrManager sirPcrManager;
    private final LovManager lovManager;
    private final ReportsManager reportManager;
    private final DeductionManager deductionManager;
    private final UserManager userManager;
    private final FinancialManager financialManager;
    private final EmailManager emailManager;

    public static void main (String[] args) throws IOException {

        new MainController();
    }

    public MainController() throws IOException {

        final MongoClient mongoClient = new MongoClient();
        final MongoDatabase reliantDb = mongoClient.getDatabase(CollectionConstants.DATABASE);

        this.logManager = new LogManager(reliantDb);
        this.sirPcrManager = new SirPcrManager(reliantDb);
        this.lovManager = new LovManager(reliantDb);
        this.reportManager = new ReportsManager(reliantDb);
        this.deductionManager = new DeductionManager(reliantDb);
        this.userManager = new UserManager(reliantDb);
        this.financialManager = new FinancialManager(reliantDb);
        this.emailManager = new EmailManager(reliantDb);

        //inject other managers
        this.lovManager.setLogManager(this.logManager);
        this.lovManager.setSirPcrManager(this.sirPcrManager);

        cfg = this.createFreeMarkerConfiguration();
        setPort(80);
        Spark.staticFileLocation("/freemarker");
        this.initializeRoutes();

    }

    private Configuration createFreeMarkerConfiguration() {
        Configuration cfg = new Configuration();
        //this tells spark where to find the template files
        cfg.setClassForTemplateLoading(MainController.class, "/freemarker");
        return cfg;
    }

    abstract class FreemarkerBasedRoute extends Route {
        final Template template;

        protected FreemarkerBasedRoute(final String  path, final String templateName)
                throws IOException {
            super(path);
            template = cfg.getTemplate(templateName);

        }

        @Override
        public Object handle(Request request, Response response) {
            StringWriter writer = new StringWriter();

            try {
                doHandle(request, response, writer);

            } catch (Exception e){
                e.printStackTrace();
                response.redirect(URLConstants.ERROR_PAGE);
            }
            return writer;
        }

        protected abstract void doHandle(final Request request, final Response response, final Writer writer)
                throws IOException, TemplateException;
    }

    public void logRequest(Request request)
    {
        System.out.println("Session Id: " + request.session().id());
        System.out.println("is new: " + request.session().isNew());
        System.out.println("request attributes: " + request.attributes().toString());
        System.out.println("request cookies: " + request.cookies().toString());
        System.out.println("session attributes: " + request.session().attributes().toString());
        System.out.println("session create time: " + request.session().creationTime());
        System.out.println("session last accessed: " + request.session().lastAccessedTime());
        System.out.println("session max inactive interval: " + request.session().maxInactiveInterval());
        System.out.println("request path: " + request.pathInfo());
        System.out.println("query parms: " + request.queryParams().toString());
        System.out.println("request contextpath: " + request.contextPath());
        System.out.println("request ip: " + request.ip());
        System.out.println("request method: " + request.requestMethod());
        System.out.println("request host: " + request.host());

    }

    private void initializeRoutes() throws IOException {

        //make sure the user has logged in
        before(new Filter()  {
            @Override
            public void handle(Request request, Response response)
           {
            //   logRequest(request);
               /// ... check if authenticated
               if (!userManager.isLoggedIn(request.session().id()))
               {
                   if (!URLConstants.LOGIN.equals(request.pathInfo())) {
                       response.redirect(URLConstants.LOGIN);
                   }
               }
            }

        });


        get(new FreemarkerBasedRoute(URLConstants.LOGIN, TemplateConstants.HOME) {

            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                System.out.println("ping GET /login");
                HashMap<String, Object> root = new HashMap<String, Object>();

                List<ErrorDto> errors = new ArrayList<ErrorDto>();

                root.put("errors", errors);
                template.process(root, writer);

            }
        });

        post(new FreemarkerBasedRoute(URLConstants.LOGIN, TemplateConstants.HOME) {

            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                HashMap<String, Object> root = new HashMap<String, Object>();

                System.out.println("ping POST /login");

                if ((request.queryParams("passCode") != null) &&
                     !request.queryParams("passCode").equals("87to16"))
                {
                    ErrorDto error = new ErrorDto();
                    error.setErrorMessage("sorry charlie, but that's not it.");
                    List<ErrorDto> errors = new ArrayList<ErrorDto>();
                    errors.add(error);

                    root.put("errors", errors);

                    template.process(root, writer);
                }
                else
                {
                    userManager.logInSession(request.session().id(), request.session().creationTime());
                    response.redirect(URLConstants.CALENDAR);
                }
            }
        });

        //call to the calendar page with no default date - will set to system date
        get(new FreemarkerBasedRoute(URLConstants.CALENDAR, TemplateConstants.CALENDAR) {

            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                System.out.println("ping GET /calendar");

                String yearMonth = DateTimeUtils.getTodayAsString(DateTimeUtils.DateFormats.YYYYMMDD,false).substring(0,6);

                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("defaultYearMonth", yearMonth);



                template.process(root, writer);

            }
        });

        //this is the calendar page with a defaulted month/year
        get(new FreemarkerBasedRoute(URLConstants.CALENDAR_DEFAULT, TemplateConstants.CALENDAR) {

            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                HashMap<String, Object> root = new HashMap<String, Object>();

                if (DateTimeUtils.isValidYearMonth(request.params(":yearMonth")))
                {
                    root.put("defaultYearMonth", request.params(":yearMonth"));
                }
                List<SirPcrDto> existingSirs = sirPcrManager.getSirsByStatus(SIR_Status.ACTIVE);
                root.put("existingSirs", existingSirs);

                template.process(root, writer);

            }
        });

        //invoked from the calendar page to get the days/hours in the incoming month/year
        get(new Route(URLConstants.CALENDAR_DATA) {
            @Override
            public Object handle(Request request, Response response) {
                MonthYear yearMonth = new MonthYear(request.params(":yearMonth"));

                List<CalendarDto> data = logManager.getCalendarHoursByMonth(yearMonth);


                return JSONUtils.convertToJSON(data);
            }
        });


        //invoked from the calendar page to get any initialization needed
        //1) gets a list of all months/years where data exists
        get (new Route(URLConstants.CALENDAR_INIT) {
            @Override
                public Object handle(Request request, Response response) {
                List<MonthYear> data = logManager.getMonthsWithLoggedPeriods(true);
                data = SortUtils.sortMonthYear(data,false);

                List<MonthYearDto> returnData = new ArrayList<MonthYearDto>();
                for (MonthYear item : data)
                {
                   MonthYearDto dto = new MonthYearDto();
                    dto.setMonth(item.getMonth());
                    dto.setYear(item.getYear());
                    dto.setMonthName(item.getMonthName());
                    dto.setYearName(item.getYearName());
                    dto.setSortKey((item.getSortKey()));
                    returnData.add(dto);
                }

                return JSONUtils.convertToJSON(returnData);
            }
        });

        get (new FreemarkerBasedRoute(URLConstants.DAY_ENTRY, TemplateConstants.DAY) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                //the incoming date is of the form yyyymmdd
                HashMap<String, Object> root = sirPcrManager.getDayEntryMatrix(request.params(":date"));

                List<ExistingSIRUIDto> existingSirs =
                        sirPcrManager.getExistingSirs(SIR_Status.ACTIVE);
                root.put("activeOnly", true);
                root.put("existingSirs", existingSirs);

                template.process(root, writer);
            }

        });

        //loads the day SIR page
        get (new FreemarkerBasedRoute(URLConstants.DAY_ENTRY_NO_DATE, TemplateConstants.DAY) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                String today = "";
                if (request.queryParams("date") == null) {
                    today = DateTimeUtils.getTodayAsString(DateTimeUtils.DateFormats.YYYYMMDD, false);
                }
                else
                {
                    today = request.queryParams("date");
                }

                HashMap<String, Object> root = sirPcrManager.getDayEntryMatrix(today);

                List<ExistingSIRUIDto> existingSirs =
                        sirPcrManager.getExistingSirs(SIR_Status.ACTIVE);
                root.put("activeOnly", true);
                root.put("existingSirs", existingSirs);

                template.process(root, writer);
            }

        });

        //REST invoked from the day entry to load the existing sir list
        get(new Route(URLConstants.DAY_DATA) {
            @Override
            public Object handle(Request request, Response response) {

                SIR_Status status = null;

                if (request.params(":status").equals("N"))
                {
                    status = SIR_Status.ACTIVE;
                }
                else
                {
                    status = SIR_Status.All;
                }

                List<ExistingSIRUIDto> data = sirPcrManager.getExistingSirs(status);
                return JSONUtils.convertToJSON(data);

            }
        });

        //REST invoked to retrieve a Sir and all logs by sir id
        //returns SirPCRDto
        get(new Route(URLConstants.GET_SIR_AND_LOGS) {
            @Override
            public Object handle(Request request, Response response) {

                SirPcrDto sirPcrDto = sirPcrManager.getSirAndLogsBySirId(request.params(":sirId"));
                return JSONUtils.convertToJSON(sirPcrDto);

            }
        });

        //REST - invoked to delete a SIR
        get(new Route(URLConstants.DELETE_SIR) {
            @Override
            public Object handle(Request request, Response response) {

                sirPcrManager.deleteSir(request.params(":sirId"));
                return "";
            }
        });

        //REST - invoked to delete a Log
        get(new Route(URLConstants.DELETE_LOG) {
            @Override
            public Object handle(Request request, Response response) {

                sirPcrManager.deleteLog(request.params(":logId"));
                return "";
            }
        });

        //called to load the sir page for a new sir
        get (new FreemarkerBasedRoute(URLConstants.NEW_SIR, TemplateConstants.NEW_SIR) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                List<LovBaseDto> subProcessTypes = lovManager.getLovEntries(LOVEnum.SUB_PROCESS_TYPE);
                List<LovBaseDto> primaVeraTypes = lovManager.getLovEntries(LOVEnum.PRIMAVERA_TYPE);
                List<LovBaseDto> activityTypes = lovManager.getLovEntries(LOVEnum.ACTIVITY_TYPE);

                root.put("date", request.params(":date"));
                root.put("subProcessTypes", subProcessTypes);
                root.put("primaVeraTypes", primaVeraTypes);
                root.put("activityTypes", activityTypes);
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //called to save the sir (for add or edit)
        post (new FreemarkerBasedRoute(URLConstants.SAVE_SIR, TemplateConstants.NEW_SIR) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                //pull the queryParms from request - map to an object and then invoke manager

                SirPcrDto sir = SIRUIHelper.mapSirFromQueryParms(request, sirPcrManager, true);
                List<ErrorDto> errors = sirPcrManager.validateAndSaveSir(sir);
                if (errors.isEmpty())
                {
                    String returnDate = DateTimeUtils.getTodayAsString(DateTimeUtils.DateFormats.YYYYMMDD,false);

                    if (sir.getLogs().size() > 0)
                    {
                        returnDate =sir.getLogs().get(0).getLogDate();
                    }
                    response.redirect("/dayEntry/" + returnDate);
                }
                else
                {

                    //reset the incoming values to go back out
                    HashMap<String, Object> root = new HashMap<String, Object>();

                    root.putAll(SIRUIHelper.mapSirToQueryParams(sir));
                    List<LovBaseDto> subProcessTypes = lovManager.getLovEntries(LOVEnum.SUB_PROCESS_TYPE);
                    List<LovBaseDto> primaVeraTypes = lovManager.getLovEntries(LOVEnum.PRIMAVERA_TYPE);
                    List<LovBaseDto> activityTypes = lovManager.getLovEntries(LOVEnum.ACTIVITY_TYPE);


                    root.put("subProcessTypes", subProcessTypes);
                    root.put("primaVeraTypes", primaVeraTypes);
                    root.put("activityTypes", activityTypes);

                    //add in the errors
                    root.put("errors", errors);

                    template.process(root, writer);

                }
            }

        });

        //this method is called to navigate to the sir/log page where
        //the sir is an existing sir, and the log is new
        post (new FreemarkerBasedRoute(URLConstants.NEW_LOG, TemplateConstants.NEW_SIR) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                //incoming form fields - newLogDate, existingActivity
                SirPcrDto sirPcrDto =
                        sirPcrManager.getSirById(new ObjectId(request.queryParams("existingActivity")));

                LogDto logDto = new LogDto();
                logDto.setLogDate(DateTimeUtils.reformatDate(request.queryParams("newLogDate"), DateTimeUtils.DateFormats.YYYYMMDD,
                        "-", DateTimeUtils.DateFormats.YYYYMMDD, null));
                sirPcrDto.getLogs().add(logDto);

                HashMap<String, Object> root = new HashMap<String, Object>();
                root.putAll(SIRUIHelper.mapSirToQueryParams(sirPcrDto));

                List<LovBaseDto> subProcessTypes = lovManager.getLovEntries(LOVEnum.SUB_PROCESS_TYPE);
                List<LovBaseDto> primaVeraTypes = lovManager.getLovEntries(LOVEnum.PRIMAVERA_TYPE);
                List<LovBaseDto> activityTypes = lovManager.getLovEntries(LOVEnum.ACTIVITY_TYPE);

                root.put("subProcessTypes", subProcessTypes);
                root.put("primaVeraTypes", primaVeraTypes);
                root.put("activityTypes", activityTypes);
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);

            }

        });

        //called to load the sir page - edit existing sir
        get (new FreemarkerBasedRoute(URLConstants.EDIT_LOG, TemplateConstants.NEW_SIR) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                List<LovBaseDto> subProcessTypes = lovManager.getLovEntries(LOVEnum.SUB_PROCESS_TYPE);
                List<LovBaseDto> primaVeraTypes = lovManager.getLovEntries(LOVEnum.PRIMAVERA_TYPE);
                List<LovBaseDto> activityTypes = lovManager.getLovEntries(LOVEnum.ACTIVITY_TYPE);

                ObjectId logId = new ObjectId(request.params(":logId"));
                SirPcrDto sirPcrDto = sirPcrManager.getSirByLogId(logId);
                root.putAll(SIRUIHelper.mapSirToQueryParams(sirPcrDto));

                root.put("subProcessTypes", subProcessTypes);
                root.put("primaVeraTypes", primaVeraTypes);
                root.put("activityTypes", activityTypes);
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //called to load the lov maintenance page - onload
        get (new FreemarkerBasedRoute(URLConstants.LOV_MAINT, TemplateConstants.LOV_CODES) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                List<String> lovList = lovManager.getLovList();
                root.put("lovList", lovList);
                root.put("defaultLovCode", "");
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //REST - called to load all lov entries for a given lov code
        get (new Route(URLConstants.LOV_LIST) {
            @Override
            public Object handle(Request request, Response response){

                HashMap<String, Object> root = new HashMap<String, Object>();
                String lovCode = request.params(":lovCode");
                List<LovBaseDto> lovList = lovManager.getLovEntries(LOVEnum.fromString(lovCode));

                return JSONUtils.convertToJSON(TablesUIHelper.createLovCodeUiDtos(lovList, lovManager));
            }

        });

        //REST - called to add a lov entry
        post (new FreemarkerBasedRoute(URLConstants.LOV_ADD, TemplateConstants.LOV_CODES) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                //incoming values are : lovList, newLovEntry
                LOVEnum lovEnum = LOVEnum.fromString(request.queryParams("lovList"));
                String newDesc = request.queryParams("newLovEntry");
                List<ErrorDto> errors = lovManager.validateAndSaveLovEntry(lovEnum, newDesc);

                List<String> lovList = lovManager.getLovList();
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("lovList", lovList);
                root.put("defaultLovCode", lovEnum.getValue());

                //add in the errors
                root.put("errors", errors);
                if (!errors.isEmpty())
                {
                    root.put("newLovEntry", newDesc);
                }

                template.process(root, writer);

            }
        });

        //REST - called to update a lov entry
        post (new FreemarkerBasedRoute(URLConstants.LOV_UPDATE, TemplateConstants.LOV_CODES) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                //incoming values are : originalDescription, lovCode, newDescription
                LOVEnum lovEnum = LOVEnum.fromString(request.queryParams("lovCode"));
                String oldDesc = request.queryParams("originalDescription");
                String newDesc = request.queryParams("newDescription");
                lovManager.updateLovEntry(lovEnum, oldDesc, newDesc);

                List<String> lovList = lovManager.getLovList();
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("lovList", lovList);
                root.put("defaultLovCode", lovEnum.getValue());
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);

            }
        });

        //REST -called to delete a lov code entry
        post (new FreemarkerBasedRoute(URLConstants.LOV_DELETE, TemplateConstants.LOV_CODES) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                //incoming values are : originalDescription, lovCode
                LOVEnum lovEnum = LOVEnum.fromString(request.queryParams("delLovCode"));
                String oldDesc = request.queryParams("delLovDesc");
                lovManager.deleteLovEntry(lovEnum, oldDesc);

                List<String> lovList = lovManager.getLovList();
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("lovList", lovList);
                root.put("defaultLovCode", lovEnum.getValue());
                root.put("errors", new ArrayList<ErrorDto>());

                template.process(root, writer);

            }
        });

        //called to load the innotas report page
        get (new FreemarkerBasedRoute(URLConstants.REPORT_INNOTAS, TemplateConstants.INNOTAS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();
                String startDate = "";
                if (request.splat().length == 0)
                {
                    startDate = DateTimeUtils.getTodayAsString(DateTimeUtils.DateFormats.YYYYMMDD, false);
                }
                else {
                    startDate = request.splat()[0];
                }

                if (DateTimeUtils.isValidDate(startDate, DateTimeUtils.DateFormats.YYYYMMDD) == null)
                {
                    startDate = DateTimeUtils.getTodayAsString(DateTimeUtils.DateFormats.YYYYMMDD, false);
                }

                List<LogDto> dtos = reportManager.getInnotasHours(startDate);
                List<InnotasRowUiDto> uiDtos =
                        ReportUIHelper.createInnotasReportMatrix(dtos);

                String[] daysOfWeek = new String[7];
                String startOfWeek = DateTimeUtils.getFirstDayOfWeek(startDate);
                String endDate = DateTimeUtils.getLastDayOfWeek(startOfWeek);
                //what day of the week does our week start (usually a Monday, but if at the
                //first week of the month, the first day of the week could be thursday or friday
                //the week starts on a Sunday - but for Innotas - start on Monday
                int startDayIdx = DateTimeUtils.getDayOfWeek(startOfWeek);
                startDayIdx -= 2;
                if (startDayIdx < 0)
                {
                    startDayIdx = 6;
                }
                daysOfWeek[startDayIdx] = startOfWeek.substring(4,6) + "/" +
                                          startOfWeek.substring(6) + "/" +
                                          startOfWeek.substring(0,4);

                String nextDay = startOfWeek;
                for (int x = startDayIdx + 1; x <= 6; x++)
                {
                    nextDay = DateTimeUtils.getNextDay(nextDay);
                    daysOfWeek[x] = nextDay.substring(4,6) + "/" +
                            nextDay.substring(6) + "/" +
                            nextDay.substring(0,4);
                }

                root.put("daysOfWeek", daysOfWeek);
                root.put("date", startDate);
                root.put("innotasHours", uiDtos);

                template.process(root, writer);
            }

        });

        get (new FreemarkerBasedRoute(URLConstants.REPORT_MONTHLY_STATUS, TemplateConstants.MONTHLY_STATUS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();
                List<MonthYear> aList = reportManager.getAllMonthsWithLoggedPeriods(true);
                root.put("monthYears", aList);

                List<FileNameDto> fileList = reportManager.getStatusFileList();
                root.put("fileList", fileList);

                root.put("toAddresses", emailManager.getToEmailAddresses());

                template.process(root, writer);
            }

        });

        post (new FreemarkerBasedRoute(URLConstants.REPORT_MONTHLY_STATUS, TemplateConstants.MONTHLY_STATUS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                MonthYear yearMonth = new MonthYear(request.queryParams("monthYears"));

                String fileName = reportManager.writeMonthlyStatus(yearMonth);

                HashMap<String, Object> root = new HashMap<String, Object>();
                List<MonthYear> aList = reportManager.getAllMonthsWithLoggedPeriods(true);
                root.put("monthYears", aList);

                List<FileNameDto> fileList = reportManager.getStatusFileList();
                root.put("fileList", fileList);

                root.put("toAddresses", emailManager.getToEmailAddresses());

                template.process(root, writer);
            }

        });

        post (new FreemarkerBasedRoute(URLConstants.REPORT_MONTHLY_STATUS_EMAILFILE,
                TemplateConstants.MONTHLY_STATUS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                String fileName = request.queryParams("emailFileName");
                String toAddress = request.queryParams("toEmail");
                String subjectText = request.queryParams("subjectText");
                String bodyText = request.queryParams("bodyText");

                emailManager.sendEmail(toAddress, subjectText, bodyText, fileName);

                HashMap<String, Object> root = new HashMap<String, Object>();
                List<MonthYear> aList = reportManager.getAllMonthsWithLoggedPeriods(true);
                root.put("monthYears", aList);

                List<FileNameDto> fileList = reportManager.getStatusFileList();
                root.put("fileList", fileList);

                root.put("toAddresses", emailManager.getToEmailAddresses());

                template.process(root, writer);

            }
        });

        post (new FreemarkerBasedRoute(URLConstants.REPORT_MONTHLY_INVOICE_EMAILFILE,
                TemplateConstants.REVENUE) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                String invoiceNumber = request.queryParams("emailInvoiceNumber");
                String toAddress = request.queryParams("toEmail");
                String subjectText = request.queryParams("subjectText");
                String bodyText = request.queryParams("bodyText");

                reportManager.setLogManager(logManager);
                String fileName = reportManager.writeInvoicePackage(invoiceNumber);

                emailManager.sendEmail(toAddress, subjectText, bodyText, fileName);

                HashMap<String, Object> root = new HashMap<String, Object>();

                List<InvoiceDto> invoiceDtos =
                        financialManager.getAllInvoices();

                //we want to summarize all revenue
                List<InvoiceDto> summaryDtos = FinancialUIHelper.createRevenueFinancialSummary(invoiceDtos);

                //create the list of years with invoices for the dropdown
                List <String> years = FinancialUIHelper.createYearsWithInvoices(invoiceDtos);

                FinancialSearchCriteriaDto searchDto = new FinancialSearchCriteriaDto();
                searchDto.setInvoiceNumber(invoiceNumber);
                List<InvoiceDto> currInvoiceDtos = financialManager.getInvoicesByCriteria(searchDto);
                int queryYear = currInvoiceDtos.get(0).getMonthYear().getYear();

                searchDto = new FinancialSearchCriteriaDto();
                searchDto.setYear(String.valueOf(queryYear));
                invoiceDtos = financialManager.getInvoicesByCriteria(searchDto);

                SortUtils.sortFinancialDto(invoiceDtos, false);

                root.put("yearList", years);
                root.put("selectedYear", String.valueOf(queryYear));
                root.put("revenueList", invoiceDtos);
                root.put("revenueSummary", summaryDtos);
                root.put("revenueListData",  JSONUtils.convertToJSON(invoiceDtos));
                root.put("toAddresses", emailManager.getToEmailAddresses());

                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);

            }
        });

//        //REST call
//        //invoked from the calendar page to get the days/hours in the incoming month/year
//        get(new Route(URLConstants.REPORT_MONTHLY_STATUS_XLS) {
//            @Override
//            public Object handle(Request request, Response response) {
//                MonthYear yearMonth = new MonthYear(request.params(":yearMonth"));
//
//                response.type("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//                response.header("Content-Disposition", "attachment; filename=filename.xls");
//
//                byte[] xls = reportManager.createMonthlyStatus(yearMonth);
//
//                try {
//
//                    response.raw().getOutputStream().write(xls);
//                    response.raw().flushBuffer();
//
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//        });


        //REST call
        //invoked from the calendar page to get the days/hours in the incoming month/year
        get(new Route(URLConstants.REPORT_MONTHLY_STATUS_XLS) {
            @Override
            public Object handle(Request request, Response response) {
                MonthYear yearMonth = new MonthYear(request.params(":yearMonth"));

                String fileName = reportManager.writeMonthlyStatus(yearMonth);

                try {

                    response.raw().getWriter().println(fileName);
                    response.raw().flushBuffer();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        });

        //REST call
        //invoked from the calendar page to get the days/hours in the incoming month/year
        get(new Route(URLConstants.REPORT_MONTHLY_INVOICE_GETFILE) {
            @Override
            public Object handle(Request request, Response response) {
               String fileName = request.params(":fileName");

                response.type("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.header("Content-Disposition", "attachment; filename=filename.xls");

                byte[] xls = reportManager.getMonthlyStatus(fileName);

                try {

                    response.raw().getOutputStream().write(xls);
                    response.raw().flushBuffer();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        });

        //REST call
        //get the timesheet
        get(new Route(URLConstants.REPORT_MONTHLY_TIMESHEET_XLS) {
            @Override
            public Object handle(Request request, Response response) {
                MonthYear yearMonth = new MonthYear(request.params(":yearMonth"));

                response.type("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.header("Content-Disposition", "attachment; filename=timesheet.xls");
                reportManager.setLogManager(logManager);
            //    byte[] xls = reportManager.createMonthlyTimesheet(yearMonth);

//                try {
//
//                    response.raw().getOutputStream().write(xls);
//                    response.raw().flushBuffer();
//
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                }

                return null;
            }
        });

        //REST call
        //invoked to generate/get the invoice for the incoming invoice number
        get(new Route(URLConstants.REPORT_MONTHLY_INVOICE_XLS) {
            @Override
            public Object handle(Request request, Response response) {
                String invoiceNumber = request.params(":invoiceNumber");

                response.type("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.header("Content-Disposition", "attachment; filename=filename.xls");
                reportManager.setLogManager(logManager);
                byte[] xls = reportManager.getInvoicePackage(invoiceNumber);

                try {

                    response.raw().getOutputStream().write(xls);
                    response.raw().flushBuffer();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        });

//REST call
        //invoked to generate/get the invoice for the incoming invoice number
        get(new Route(URLConstants.REPORT_MONTHLY_INVOICE_XLS) {
            @Override
            public Object handle(Request request, Response response) {
                String invoiceNumber = request.params(":invoiceNumber");

                response.type("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.header("Content-Disposition", "attachment; filename=filename.xls");
                reportManager.setLogManager(logManager);
                byte[] xls = reportManager.getInvoicePackage(invoiceNumber);

                try {

                    response.raw().getOutputStream().write(xls);
                    response.raw().flushBuffer();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        });

        //called to load the SIR list page,
        get (new FreemarkerBasedRoute(URLConstants.MAINT_SIR_LIST, TemplateConstants.SIR_LIST) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                //if the page has incoming parameters, then load then
                if (request.queryParams("sirStatus") != null) {
                    root.putAll(SIRUIHelper.mapSirPcrDtoToUiDto(URLConstants.MAINT_SIR_LIST,
                            request,
                            sirPcrManager.findSirsByCriteria(request.queryParams("sirStatus"),
                                    request.queryParams("sirType"),
                                    request.queryParams("sirNumber"))));

                    root.put("sirStatus", request.queryParams("sirStatus"));
                    root.put("sirType", request.queryParams("sirType"));
                    root.put("sirNumber", request.queryParams("sirNumber"));
                }
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //called to load the sir list page with the incoming search parms
        post (new FreemarkerBasedRoute(URLConstants.MAINT_SIR_LIST, TemplateConstants.SIR_LIST) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                //incoming parms:
                //action : SEARCH, SUBMIT
                //sir status : OPEN, CLOSED, ALL
                //sir Type: SIR, PCR, OTHER
                //sir #

                if (request.queryParams("action").equals("SEARCH"))
                {
                    root.putAll(SIRUIHelper.mapSirPcrDtoToUiDto(URLConstants.MAINT_SIR_LIST,
                                                            request,
                                                sirPcrManager.findSirsByCriteria(request.queryParams("sirStatus"),
                                                 request.queryParams("sirType"),
                                                 request.queryParams("sirNumber"))));
                }
                else
                {
                    //the incoming queryParams have a list if sir numbers - these
                    //are the sirs to close
                    List<String> results = SIRUIHelper.mapSirPcrUiDtoToDto(request);

                    sirPcrManager.closeSirs(results);

                    //after closing the sirs, requery using the current search criteria
                    root.putAll(SIRUIHelper.mapSirPcrDtoToUiDto(URLConstants.MAINT_SIR_LIST,
                            request,
                            sirPcrManager.findSirsByCriteria(request.queryParams("sirStatus"),
                                    request.queryParams("sirType"),
                                    request.queryParams("sirNumber"))));

                }

                root.put("sirStatus", request.queryParams("sirStatus"));
                root.put("sirType", request.queryParams("sirType"));
                root.put("sirNumber", request.queryParams("sirNumber"));
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //REST - load SIRS to match search criteria on sir list page
        get (new Route(URLConstants.LOV_LIST) {
            @Override
            public Object handle(Request request, Response response){

                HashMap<String, Object> root = new HashMap<String, Object>();
                String lovCode = request.params(":lovCode");
                List<LovBaseDto> lovList = lovManager.getLovEntries(LOVEnum.fromString(lovCode));

                return JSONUtils.convertToJSON(TablesUIHelper.createLovCodeUiDtos(lovList, lovManager));
            }

        });

        //called to load the SIR detail page,
        get (new FreemarkerBasedRoute(URLConstants.SIR_DETAIL, TemplateConstants.SIR_DETAIL) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                String sirId = request.params(":sirId");
                String returnPageParms = request.params(":returnPageParms");

                SirPcrDto sirPcrDto = sirPcrManager.getSirAndLogsBySirId(sirId);

                root.putAll(SIRUIHelper.mapSirToQueryParams(sirPcrDto));

                List<LovBaseDto> subProcessTypes = lovManager.getLovEntries(LOVEnum.SUB_PROCESS_TYPE);

                SirHoursSummarySearchDto searchDto = new SirHoursSummarySearchDto();
                searchDto.setSirPcrId(sirPcrDto.getId());

                List<SirHoursSummaryDto> hoursByActivityList =
                        reportManager.getSirHoursByActivity(searchDto);

                root.put("subProcessTypes", subProcessTypes);
                root.put("returnPageParms", returnPageParms);
                root.put("hoursByActivityList", hoursByActivityList);
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //called to save the SIR detail page,
        post (new FreemarkerBasedRoute(URLConstants.UPDATE_SIR, TemplateConstants.SIR_DETAIL) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                //pull the queryParms from request - map to an object and then invoke manager

                SirPcrDto sir = SIRUIHelper.mapSirFromQueryParms(request, sirPcrManager, false);
                String returnPageParms = request.queryParams("returnPageParms");

                List<ErrorDto> errors = sirPcrManager.validateAndSaveSir(sir);
                if (errors.isEmpty())
                {
                    String nextPageParms = "";

                    if (returnPageParms != null) {
                        UrlParametersDto parmDto = null;

                        try {

                            String parms = UrlEncoder.decipher(returnPageParms);

                            parmDto = JSONUtils.convertUrlParameterDtoToObject(parms);

                            Iterator iterator = parmDto.getPageParms().entrySet().iterator();
                            int parmIdx = 0;
                            while (iterator.hasNext())
                            {
                                Map.Entry pair = (Map.Entry)iterator.next();

                                if (parmIdx == 0) {
                                    nextPageParms = "?" + pair.getKey() + "=" + pair.getValue();
                                } else {
                                    nextPageParms += "&" +pair.getKey() + "=" + pair.getValue();
                                }

                                parmIdx++;
                            }

                            nextPageParms = parmDto.getPriorPage() + nextPageParms;

                        } catch (Exception e) {

                        }

                    }
                    else
                    {
                        nextPageParms = URLConstants.MAINT_SIR_LIST;
                    }
                    response.redirect(nextPageParms);
                }
                else
                {
                    //reset the incoming values to go back out
                    HashMap<String, Object> root = new HashMap<String, Object>();
                    root.putAll(SIRUIHelper.mapSirToQueryParams(sir));
                    List<LovBaseDto> subProcessTypes = lovManager.getLovEntries(LOVEnum.SUB_PROCESS_TYPE);

                    root.put("subProcessTypes", subProcessTypes);

                    //add in the errors
                    root.put("errors", errors);

                    template.process(root, writer);

                }
            }

        });

        //called to load the deductions page - onload
        get (new FreemarkerBasedRoute(URLConstants.DEDUCTIONS, TemplateConstants.DEDUCTIONS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                List<LovBaseDto> deductionCatgList =
                        lovManager.getLovEntries(LOVEnum.DEDUCTION_CATEGORY);
                List<LovBaseDto> deductionTypeList =
                        new ArrayList<LovBaseDto>();
                root.put("deductionCategoryList", deductionCatgList);
                root.put("deductionTypeList", deductionTypeList);
                root.put("defaultCategoryCode", "");
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //called to load the deduction pages based on a search
        post (new FreemarkerBasedRoute(URLConstants.DEDUCTIONS, TemplateConstants.DEDUCTIONS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                String deductionCategory = request.queryParams("deductionCategory");
                String deductionType = request.queryParams("deductionType");
                String startDate = request.queryParams("startDate");
                String endDate  = request.queryParams("endDate");

                List<DeductionDto> deductionDtos =
                        deductionManager.getDeductionsByCriteria(deductionCategory, deductionType, startDate, endDate);

                List<DeductionUiDto> uiDtos = DeductionUIHelper.mapDeductionUiDtos(lovManager, deductionDtos);
                List<DeductionUiDto> summaryDtos = DeductionUIHelper.createDeductionSummary(lovManager, deductionDtos);

                List<LovBaseDto> deductionCatgList =
                        lovManager.getLovEntries(LOVEnum.DEDUCTION_CATEGORY);
                List<LovBaseDto> deductionTypeList =
                        lovManager.getLovEntriesByDeductionCode(deductionCategory);

                root.put("deductionList", uiDtos);
                root.put("deductionSummary", summaryDtos);
                root.put("deductionListData",  JSONUtils.convertToJSON(uiDtos));
                root.put("deductionCategoryList", deductionCatgList);
                root.put("deductionTypeList", deductionTypeList);
                root.put("defaultCategoryCode", deductionCategory);
                root.put("defaultDeductionType", deductionType);
                root.put("defaultStartDate", startDate);
                root.put("defaultEndDate", endDate);

                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //called to load the deduction pages based on a search
        post (new FreemarkerBasedRoute(URLConstants.DEDUCTIONS_UPDATE, TemplateConstants.DEDUCTIONS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                DeductionDto dto = DeductionUIHelper.mapDeductionFromQueryParams(request);

                List<ErrorDto> errors = deductionManager.validateAndSaveDeduction(dto);
                if (errors.size() > 0)
                {
                    System.out.println(errors.toString());
                }
                else {
                    //reload the deductions for the new/updated category/type
                    List<DeductionDto> deductionDtos =
                            deductionManager.getDeductionsByCriteria(dto.getDeductionCategory(),
                                    dto.getDeductionType(),
                                    null, null);

                    List<DeductionUiDto> uiDtos = DeductionUIHelper.mapDeductionUiDtos(lovManager, deductionDtos);
                    List<DeductionUiDto> summaryDtos = DeductionUIHelper.createDeductionSummary(lovManager, deductionDtos);


                    List<LovBaseDto> deductionCatgList =
                            lovManager.getLovEntries(LOVEnum.DEDUCTION_CATEGORY);
                    List<LovBaseDto> deductionTypeList =
                            lovManager.getLovEntriesByDeductionCode(dto.getDeductionCategory());

                    root.put("deductionList", uiDtos);
                    root.put("deductionSummary", summaryDtos);
                    root.put("deductionListData", JSONUtils.convertToJSON(uiDtos));
                    root.put("deductionCategoryList", deductionCatgList);
                    root.put("deductionTypeList", deductionTypeList);
                    root.put("defaultCategoryCode", dto.getDeductionCategory());
                    root.put("defaultDeductionType", dto.getDeductionType());
                    root.put("defaultStartDate", "");
                    root.put("defaultEndDate", "");
                }
                root.put("errors", errors);
                template.process(root, writer);
            }

        });

        //called to load the deduction pages based on a search
        post (new FreemarkerBasedRoute(URLConstants.DEDUCTIONS_DELETE, TemplateConstants.DEDUCTIONS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                DeductionDto dto = DeductionUIHelper.mapDeductionFromQueryParams(request);

                DeductionDto origDto = deductionManager.getDeductionById(dto.getId());
                deductionManager.deleteDeduction(dto.getId());

                //reload the deductions for the new/updated category/type
                List<DeductionDto> deductionDtos =
                        deductionManager.getDeductionsByCriteria(origDto.getDeductionCategory(),
                                origDto.getDeductionType(),
                                null, null);

                List<DeductionUiDto> uiDtos = DeductionUIHelper.mapDeductionUiDtos(lovManager, deductionDtos);
                List<DeductionUiDto> summaryDtos = DeductionUIHelper.createDeductionSummary(lovManager, deductionDtos);

                List<LovBaseDto> deductionCatgList =
                        lovManager.getLovEntries(LOVEnum.DEDUCTION_CATEGORY);
                List<LovBaseDto> deductionTypeList =
                        lovManager.getLovEntriesByDeductionCode(origDto.getDeductionCategory());

                root.put("deductionList", uiDtos);
                root.put("deductionSummary", summaryDtos);
                root.put("deductionListData", JSONUtils.convertToJSON(uiDtos));
                root.put("deductionCategoryList", deductionCatgList);
                root.put("deductionTypeList", deductionTypeList);
                root.put("defaultCategoryCode", origDto.getDeductionCategory());
                root.put("defaultDeductionType", origDto.getDeductionType());
                root.put("defaultStartDate", "");
                root.put("defaultEndDate", "");

                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //REST - called to load all lov entries where the deduction type code equals
        //       the incoming deduction category
        get (new Route(URLConstants.DEDUCTIONS_CATEGORY_TYPES) {
            @Override
            public Object handle(Request request, Response response){

                HashMap<String, Object> root = new HashMap<String, Object>();
                String deductionCategory = request.params(":deductionCategory");
                List<LovBaseDto> typeDtos =
                        lovManager.getLovEntriesByDeductionCode(deductionCategory);

                return JSONUtils.convertToJSON(typeDtos);
            }

        });

        //REST - called to add a lov entry
        post (new FreemarkerBasedRoute(URLConstants.LOV_ADD, TemplateConstants.LOV_CODES) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                //incoming values are : lovList, newLovEntry
                LOVEnum lovEnum = LOVEnum.fromString(request.queryParams("lovList"));
                String newDesc = request.queryParams("newLovEntry");
                List<ErrorDto> errors = lovManager.validateAndSaveLovEntry(lovEnum, newDesc);

                List<String> lovList = lovManager.getLovList();
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("lovList", lovList);
                root.put("defaultLovCode", lovEnum.getValue());

                //add in the errors
                root.put("errors", errors);
                if (!errors.isEmpty())
                {
                    root.put("newLovEntry", newDesc);
                }

                template.process(root, writer);

            }
        });

        //REST - called to update a lov entry
        post (new FreemarkerBasedRoute(URLConstants.LOV_UPDATE, TemplateConstants.LOV_CODES) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                //incoming values are : originalDescription, lovCode, newDescription
                LOVEnum lovEnum = LOVEnum.fromString(request.queryParams("lovCode"));
                String oldDesc = request.queryParams("originalDescription");
                String newDesc = request.queryParams("newDescription");
                lovManager.updateLovEntry(lovEnum, oldDesc, newDesc);

                List<String> lovList = lovManager.getLovList();
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("lovList", lovList);
                root.put("defaultLovCode", lovEnum.getValue());
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);

            }
        });

        //REST -called to delete a lov code entry
        post (new FreemarkerBasedRoute(URLConstants.LOV_DELETE, TemplateConstants.LOV_CODES) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                //incoming values are : originalDescription, lovCode
                LOVEnum lovEnum = LOVEnum.fromString(request.queryParams("delLovCode"));
                String oldDesc = request.queryParams("delLovDesc");
                lovManager.deleteLovEntry(lovEnum, oldDesc);

                List<String> lovList = lovManager.getLovList();
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("lovList", lovList);
                root.put("defaultLovCode", lovEnum.getValue());
                root.put("errors", new ArrayList<ErrorDto>());

                template.process(root, writer);

            }
        });

        //called to load the receipts page on load
        get (new FreemarkerBasedRoute(URLConstants.REVENUE, TemplateConstants.REVENUE) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();


                List<InvoiceDto> invoiceDtos =
                        financialManager.getAllInvoices();

                //we want to summarize all revenue
                List<InvoiceDto> summaryDtos = FinancialUIHelper.createRevenueFinancialSummary(invoiceDtos);
                //create the list of years with invoices for the dropdown
                List <String> years = FinancialUIHelper.createYearsWithInvoices(invoiceDtos);

                FinancialSearchCriteriaDto searchDto = new FinancialSearchCriteriaDto();
                searchDto.setYear(String.valueOf(years.get(0)));
                invoiceDtos = financialManager.getInvoicesByCriteria(searchDto);

                SortUtils.sortFinancialDto(invoiceDtos, false);

                root.put("yearList", years);
                root.put("revenueList", invoiceDtos);
                root.put("revenueSummary", summaryDtos);
                root.put("revenueListData",  JSONUtils.convertToJSON(invoiceDtos));
                root.put("toAddresses", emailManager.getToEmailAddresses());

                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        get (new Route(URLConstants.REVENUE_DATA) {
            @Override
            public Object handle(Request request, Response response) {

                String year = request.params(":year");
                FinancialSearchCriteriaDto searchDto = new FinancialSearchCriteriaDto();
                searchDto.setYear(year);

                List<InvoiceDto> invoiceDtos =
                        invoiceDtos = financialManager.getInvoicesByCriteria(searchDto);

                SortUtils.sortFinancialDto(invoiceDtos, false);

                return JSONUtils.convertToJSON(invoiceDtos);
            }
        });

        //get the next invoice info. the invoiceDto is used
        get (new Route(URLConstants.REVENUE_NEXT_INVOICE) {
            @Override
            public Object handle(Request request, Response response) {

                InvoiceDto dto = financialManager.getNextInvoice();
                String json = JSONUtils.convertToJSON(dto);
                return json;
            }
        });

        //called to add/update a revenue entry
        post (new FreemarkerBasedRoute(URLConstants.REVENUE_UPDATE, TemplateConstants.REVENUE) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                InvoiceDto dto = FinancialUIHelper.mapFromQueryParams(request);

                List<ErrorDto> errors = financialManager.validateAndSaveInvoice(dto);
                if (errors.size() > 0)
                {
                    System.out.println(errors.toString());
                }
                else {

                    List<InvoiceDto> invoiceDtos =
                            financialManager.getAllInvoices();

                    List<InvoiceDto> summaryDtos = FinancialUIHelper.createRevenueFinancialSummary(invoiceDtos);

                    //create the list of years with invoices for the dropdown
                    List <String> years = FinancialUIHelper.createYearsWithInvoices(invoiceDtos);

                    FinancialSearchCriteriaDto searchDto = new FinancialSearchCriteriaDto();
                    searchDto.setYear(String.valueOf(dto.getMonthYear().getYear()));

                    invoiceDtos =financialManager.getInvoicesByCriteria(searchDto);
                    SortUtils.sortFinancialDto(invoiceDtos, false);

                    root.put("selectedYear", String.valueOf(dto.getMonthYear().getYear()));
                    root.put("yearList", years);
                    root.put("revenueList", invoiceDtos);
                    root.put("revenueSummary", summaryDtos);
                    root.put("revenueListData",  JSONUtils.convertToJSON(invoiceDtos));
                    root.put("toAddresses", emailManager.getToEmailAddresses());

                }
                root.put("errors", errors);
                template.process(root, writer);
            }

        });

        //called to delete a financial
        post (new FreemarkerBasedRoute(URLConstants.REVENUE_DELETE, TemplateConstants.REVENUE) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{

                HashMap<String, Object> root = new HashMap<String, Object>();

                InvoiceDto dto = FinancialUIHelper.mapFromQueryParams(request);

                financialManager.deleteInvoice(dto.getId());

                List<InvoiceDto> invoiceDtos =
                        financialManager.getAllInvoices();

                List<InvoiceDto> summaryDtos = FinancialUIHelper.createRevenueFinancialSummary(invoiceDtos);

                root.put("revenueList", invoiceDtos);
                root.put("revenueSummary", summaryDtos);
                root.put("revenueListData",  JSONUtils.convertToJSON(invoiceDtos));
                root.put("toAddresses", emailManager.getToEmailAddresses());

                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }

        });

        //called to load the financial summary page on load
        get (new FreemarkerBasedRoute(URLConstants.FINANCIAL_SUMMARY, TemplateConstants.FINANCIAL_SUMMARY) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{


                HashMap<String, Object> root = new HashMap<String, Object>();

              //  List<String> years = logManager.getYearsWithLoggedPeriods();
                List<InvoiceDto> invoiceDtos =
                        financialManager.getAllInvoices();

                //we want to summarize all revenue
                List<InvoiceDto> summaryDtos = FinancialUIHelper.createRevenueFinancialSummary(invoiceDtos);
                //create the list of years with invoices for the dropdown
                List <String> years = FinancialUIHelper.createYearsWithInvoices(invoiceDtos);

                String currentYear = "" + DateTimeUtils.getCurrentYear();

                List<DeductionDto> deductionDtos =
                        deductionManager.getDeductionsByCriteria(null,
                                null,
                                null, null);

                FinancialSummaryDto financialSummaryDto = FinancialUIHelper.createFinancialSummary(lovManager,
                        currentYear,
                        invoiceDtos,
                        deductionDtos);

                root.put("years", years);
                root.put("currentYear", currentYear);
                root.put("summaryDto", financialSummaryDto);
                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }
        });

        //invoked from the financial summary page to get the info for the requested
        //year
        get(new Route(URLConstants.FINANCIAL_SUMMARY_DATA) {
            @Override
            public Object handle(Request request, Response response) {
                String year = request.params(":year");

                List<InvoiceDto> invoiceDtos =
                        financialManager.getAllInvoices();

                List<DeductionDto> deductionDtos =
                        deductionManager.getDeductionsByCriteria(null,
                                null,
                                null, null);

                return JSONUtils.convertToJSON(FinancialUIHelper.createFinancialSummary(lovManager,
                                                                              year,
                        invoiceDtos,
                                                                              deductionDtos));
            }
        });

        //called to load the purchase orders on load
        get (new FreemarkerBasedRoute(URLConstants.PURCHASE_ORDERS, TemplateConstants.PURCHASE_ORDERS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{


                HashMap<String, Object> root = new HashMap<String, Object>();

                List<PurchaseOrderDto> allPos = financialManager.getAllPurchaseOrders();

                root.putAll(FinancialUIHelper.createPoSummary(allPos, financialManager, null));

                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }
        });

        //called to load the purchase orders on load
        get (new FreemarkerBasedRoute(URLConstants.PURCHASE_ORDER_DATA, TemplateConstants.PURCHASE_ORDERS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{


                HashMap<String, Object> root = new HashMap<String, Object>();

                String poNumber = request.params(":poNumber");

                List<PurchaseOrderDto> allPos = financialManager.getAllPurchaseOrders();

                root.putAll(FinancialUIHelper.createPoSummary(allPos, financialManager, poNumber));

                root.put("errors", new ArrayList<ErrorDto>());
                template.process(root, writer);
            }
        });

        //called to save a purchase order
        post (new FreemarkerBasedRoute(URLConstants.PURCHASE_ORDERS, TemplateConstants.PURCHASE_ORDERS) {
            @Override
            public void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException{


                HashMap<String, Object> root = new HashMap<String, Object>();

                PurchaseOrderDto dto = FinancialUIHelper.mapPoFromQueryParams(request);
                List<ErrorDto> errors = financialManager.validateAndSavePO(dto);

                List<PurchaseOrderDto> allPos = financialManager.getAllPurchaseOrders();

                root.putAll(FinancialUIHelper.createPoSummary(allPos, financialManager, dto.getPoNumber()));

                //if there are errors - then we just send back what came in
                if (errors.size() > 0)
                {
                    root.put("poDto", dto);
                }

                root.put("errors", errors);
                template.process(root, writer);
            }
        });
    }



}
