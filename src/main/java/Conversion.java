import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import common.CollectionConstants;

import common.FieldConstants;
import common.LogTime;
import dtos.ErrorDto;
import dtos.LogDto;
import dtos.SirPcrDto;
import managers.SirPcrManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import utilities.DateTimeUtils;
import utilities.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * Created by Robertson_Laptop on 6/11/2016.
 */
public class Conversion {
    public static void main (String[] args)
    {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("reliant");
        SirPcrManager sirPcrManager = new SirPcrManager(database);

//        loadSirs(database);
//        loadLogs(database);
//        loadLogPeriods(database);
//        loadActivityTypes(database);
//        loadSubprocessTypes(database);
//          loadPrimaveraTypes(database);
//        loadDeductionTypes(database);
//        loadDeductions(database);
//        loadFinancialSummary(database);
//          convertLogDatesToString(database);
//          stripNewLineChars();

//        linkSirAndLogs(database);
        importDataFromExcel(sirPcrManager);
    }

    private static void importDataFromExcel(SirPcrManager sirPcrManager)
    {
        SirPcrDto sirPcrDto;
        String csvFile = "c:\\reliantData\\Oct2016Pipe.csv";
        String newFile = "c:\\reliantData\\OctCONVERTED.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\|";
        int loadCnt = 0;
        int readCnt = 0;

        stripNewLineChars(csvFile, newFile);

        try {

            br = new BufferedReader(new FileReader(newFile));
            while ((line = br.readLine()) != null) {

                readCnt++;
                System.out.println("Read: " + readCnt);

                // use comma as separator
                //Activity Type,SubProcess,Primavera Activity,Note,SIR/PCR/Other,Date,Start Time,End Time

                String[] sir = line.split(cvsSplitBy);

                if (sir.length < 10) {
                    System.out.println("Skipped " + line);
                } else {
                    String activityType = sir[0];
                    String subProcess = sir[1];
                    String primaVera = sir[2];
                    String note = sir[3];
                    String sirNumber = sir[4];
                    String sirType = sir[5];
                    String description = sir[6];
                    String date = sir[7];
                    String startTime = sir[8];
                    String endTime = sir[9];

                    //if there is no SIR number, we will create a new SIR where the SIR Nickname and desc
                    //will be set to the subprocess type. The sir type will be 'OTHER'

                    if (StringUtils.isEmpty(sirNumber))
                    {
                        //see if we already created an entryby nickanem
                        sirPcrDto = sirPcrManager.findSirByNickName(subProcess);
                        if (sirPcrDto == null) {
                            sirPcrDto = new SirPcrDto();
                            sirPcrDto.setNickName(subProcess);
                            sirPcrDto.setSirPcrNumber(sirPcrManager.getNextSirIdForOtherSirType());
                            sirPcrDto.setSirDesc(subProcess);
                            sirPcrDto.setCompletedInd(false);
                            sirPcrDto.setSirType("OTHER");
                            sirPcrDto.setSubProcessDesc(subProcess);
                            sirPcrDto.setCreateDate(DateTimeUtils.getSystemDate());
                        }
                    }
                    else
                    {
                        //first see if we already have thSe sir in the system
                        sirPcrDto = sirPcrManager.getSirByNumber(sirNumber);
                        if (sirPcrDto == null) {
                            sirPcrDto = new SirPcrDto();
                            sirPcrDto.setNickName(sirNumber);
                            sirPcrDto.setSirPcrNumber(sirNumber);
                            sirPcrDto.setSirDesc(description);
                            sirPcrDto.setCompletedInd(false);
                            sirPcrDto.setSirType(sirType);
                            sirPcrDto.setSubProcessDesc(subProcess);
                            sirPcrDto.setCreateDate(DateTimeUtils.getSystemDate());
                        }
                    }

                    LogDto logDto = new LogDto();
                    logDto.setCreateDate(DateTimeUtils.getSystemDate());
                    logDto.setLogDate(DateTimeUtils.reformatDate(date, DateTimeUtils.DateFormats.MMDDYYYY,true,
                                      DateTimeUtils.DateFormats.YYYYMMDD,false));
                    logDto.setActivityDesc(activityType);
                    logDto.setPrimaveraDesc(primaVera);
                    logDto.setStartTime(convertTime(startTime));
                    logDto.setEndTime(convertTime(endTime));
                    logDto.setNote(note);

                    sirPcrDto.getLogs().add(logDto);
                    List<ErrorDto> errors = sirPcrManager.validateAndSaveSir(sirPcrDto);
                    if (errors.isEmpty()) {
                        loadCnt++;
                    }
                    else
                    {
                        System.out.println("errors for line: " + line);
                        for (ErrorDto error : errors)
                        {
                            System.out.println(error.getErrorMessage());
                        }
                    }
                }
            }

            System.out.print("loaded" + loadCnt);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            System.out.println("error on " + line);
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Load Count: " + loadCnt);
        System.out.println("Done");

    }

    /*
     the incoming format is hh:mm. when hh is < 10, we need to append a '0' to the front
     */
    private static String convertTime(String xlsTime)
    {
        String[] parts = xlsTime.split(":");
        String outValue;

        if (Integer.parseInt(parts[0]) <= 9)
        {
            outValue = '0' + parts[0] + parts[1];
        }
        else
        {
            outValue =  parts[0] + parts[1];
        }

        return outValue;
    }

    private static void linkSirAndLogs(MongoDatabase db) {
        MongoCollection sirCollection = db.getCollection(CollectionConstants.SIR_PCR);
        MongoCollection logCollection = db.getCollection(CollectionConstants.LOG);

        MongoCursor cursor = sirCollection.find().iterator();

        //cycle through all SIRs and
        while (cursor.hasNext()) {
            Document doc = (Document) cursor.next();

            //add a column to refer to the sir/pcr object id key
            logCollection.updateMany( new Document(FieldConstants.LEGACY_SIR_ID,
                    doc.getString(FieldConstants.LEGACY_SIR_ID)),
                    Updates.set(FieldConstants.SIR_PCR_ID, doc.getObjectId("_id")));

        }
    }

    private static void stripNewLineChars(String csvFile, String newFile)
    {

        String line = "";
        int intch;
        String hexNumber;
        boolean foundTheOD = false;
        Reader r = null;
        Writer w = null;

        try {
            InputStream in = new FileInputStream(csvFile);
            r = new InputStreamReader(in, "US-ASCII");
            OutputStream out = new FileOutputStream(newFile);
            w = new OutputStreamWriter(out);

            while ((intch = r.read()) != -1) {
               hexNumber = Integer.toHexString(intch);
                if (hexNumber.equals("a")) {
                    if (!foundTheOD) {
                        intch = 32;
                    }
                } else if (hexNumber.equals("d")) {
                    foundTheOD = true;
                } else {
                    foundTheOD = false;
                }

                w.write(intch);

            }    // ...
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    private static void loadSirs(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\SIR_PCR.csv";
        String newFile = "c:\\reliantData\\SIR_PCR_CONVERTED.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";

        stripNewLineChars(csvFile, newFile);

        MongoCollection sirCollection = db.getCollection(CollectionConstants.SIR_PCR);
        try {

            br = new BufferedReader(new FileReader(newFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, type, number, descriptino, completed_ind, nickname, subprocess_id
                String[] sir = line.split(cvsSplitBy);
//                if (sir[0].equals("306"))
//                {
//                    int x = 0;
//                }
                if (sir.length < 5)
                {
                    System.out.println("Skipped " + line);
                }
                else {
                    Document document = new Document();
                    document.put(FieldConstants.LEGACY_SIR_ID, sir[0]);
                    document.put(FieldConstants.SIR_TYPE, sir[1]);
                    document.put(FieldConstants.SIR_PCR_NUMBER, sir[2]);
                    document.put(FieldConstants.SIR_DESC, sir[3]);
                    document.put(FieldConstants.COMPLETED_IND, sir[4]);
                    //if there are 5 fields, then the last field could be
                    //the nickname or the subprocess id
                    if (sir.length == 6) {
                        try {
                            Integer.parseInt(sir[5]);
                            document.put(FieldConstants.LEGACY_SUBPROCESS_ID, sir[5]);

                        } catch (Exception e) {
                            document.put(FieldConstants.NICKNAME, sir[5]);
                        }

                    } else if (sir.length == 7) {
                        document.put(FieldConstants.NICKNAME, sir[5]);
                        document.put(FieldConstants.LEGACY_SUBPROCESS_ID, sir[6]);
                    }
                    sirCollection.insertOne(document);
                }
            }

            System.out.print("loaded" + sirCollection.count());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void loadLogs(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\SIR_PCR_LOG.csv";
        String newFile = "c:\\reliantData\\SIR_PCR_LOG_CONVERTED.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";

        stripNewLineChars(csvFile, newFile);

        MongoCollection logCollection = db.getCollection(CollectionConstants.LOG);
        logCollection.drop();
        try {

            br = new BufferedReader(new FileReader(newFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, legacySirId, billable_ind, log_date, start_time, end_time, log_note,
                //primavera_id, activity_id
                String[] log = line.split(cvsSplitBy);
                if (log.length < 9)
                {
                    System.out.println("Skipped " + line);
                }
                else {
                    Document document = new Document();
                    document.put(FieldConstants.LEGACY_LOG_ID, log[0]);
                    document.put(FieldConstants.LEGACY_SIR_ID, log[1]);
                    document.put(FieldConstants.BILLABLE_IND, log[2]);
                    document.put(FieldConstants.LOG_DATE, DateTimeUtils.stringToDate(log[3], DateTimeUtils.DateFormats.YYYYMMDD));
                    document.put(FieldConstants.START_TIME, log[4]);
                    document.put(FieldConstants.END_TIME, log[5]);
                    document.put(FieldConstants.NOTE, log[6]);
                    document.put(FieldConstants.LEGACY_PRIMAVERA_ID, log[7]);
                    document.put(FieldConstants.PRIMAVERA_DESC, "");
                    document.put(FieldConstants.LEGACY_ACTIVITY_ID, log[8]);
                    document.put(FieldConstants.ACTIVITY_DESC, "");
                    document.put(FieldConstants.HOURS, 0.0);

//                    //if there are 5 fields, then the last field could be
//                    //the nickname or the subprocess id
//                    if (sir.length == 6) {
//                        try {
//                            Integer.parseInt(sir[5]);
//                            document.put(Constants.LEGACY_SUBPROCESS_ID, sir[5]);
//
//                        } catch (Exception e) {
//                            document.put(Constants.NICKNAME, sir[5]);
//                        }
//
//                    } else if (sir.length == 7) {
//                        document.put(Constants.NICKNAME, sir[5]);
//                        document.put(Constants.LEGACY_SUBPROCESS_ID, sir[6]);
//                    }
                    logCollection.insertOne(document);
                }
            }

            System.out.print("loaded" + logCollection.count());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void loadLogPeriods(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\LOG_PERIODS.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";

        MongoCollection logCollection = db.getCollection(CollectionConstants.LOG);
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, legacyLogId, log_date, start_time, end_time, percent of time
                String[] log = line.split(cvsSplitBy);
                if (log.length < 6)
                {
                    System.out.println("Skipped " + line);
                }
                else {
                    Document logDoc = (Document) logCollection.find(
                            Filters.eq(FieldConstants.LEGACY_LOG_ID, log[1])).first();

                    if (logDoc != null) {
                        double percentOfTime = Double.parseDouble(log[5]);
                        double hours = DateTimeUtils.
                                getHoursBetweenTimeRange(new LogTime(log[3]),
                                        new LogTime(log[4])) * percentOfTime;


                        logCollection.updateOne(Filters.eq("_id", logDoc.getObjectId("_id")),
                                Updates.inc(FieldConstants.HOURS, hours));


                    }
                }
            }

            System.out.print("loaded" + logCollection.count());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void loadActivityTypes(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\LOV_ACTIVITY_TYPE.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";

        MongoCollection lovCollection = db.getCollection(CollectionConstants.LOV);
        MongoCollection logCollection = db.getCollection(CollectionConstants.LOG);
        //lovCollection.drop();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, description
                String[] lov = line.split(cvsSplitBy);
                if (lov.length < 2)
                {
                    System.out.println("Skipped " + line);
                }
                else
                {
                    //add the value to the lov table
                    Document lovDoc = new Document(FieldConstants.LOV_DESC, lov[1]);
                    lovDoc.put(FieldConstants.LOV_CODE, FieldConstants.LOV_CODE_ACITITY_TYPE);

                    lovCollection.insertOne(lovDoc);

                     //and then update all logs with the descriptions
                    UpdateResult results = logCollection.updateMany(Filters.eq(FieldConstants.LEGACY_ACTIVITY_ID,
                            lov[0]),
                            Updates.set(FieldConstants.ACTIVITY_DESC, lov[1]));
                    System.out.println("For: " + lov[1] + " updated: " + results.getMatchedCount() + " logs.");

                }
            }

            System.out.print("loaded" + lovCollection.count());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }
    private static void loadSubprocessTypes(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\LOV_SUBPROCESS_TYPE.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";

        MongoCollection lovCollection = db.getCollection(CollectionConstants.LOV);
        MongoCollection sirCollection = db.getCollection(CollectionConstants.SIR_PCR);
      //  lovCollection.drop();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, description
                String[] lov = line.split(cvsSplitBy);
                if (lov.length < 2)
                {
                    System.out.println("Skipped " + line);
                }
                else
                {
                    //add the value to the lov table
                    //add the value to the lov table
                    Document lovDoc = new Document(FieldConstants.LOV_DESC, lov[1]);
                    lovDoc.put(FieldConstants.LOV_CODE, FieldConstants.LOV_CODE_SUBPROCESS_TYPE);

                    lovCollection.insertOne(lovDoc);

                    //and then update all logs with the descriptions
                    UpdateResult results = sirCollection.updateMany(Filters.eq(FieldConstants.LEGACY_SUBPROCESS_ID,
                            lov[0]),
                            Updates.set(FieldConstants.SUBPROCESS_DESC, lov[1]));

                    System.out.println("For: " + lov[1] + " updated: " + results.getMatchedCount() + " sirs.");

                }
            }

            System.out.print("loaded" + lovCollection.count());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }
    private static void loadPrimaveraTypes(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\LOV_PRIMAVERA_ACTIVITY.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";

        MongoCollection lovCollection = db.getCollection(CollectionConstants.LOV);
        MongoCollection logCollection = db.getCollection(CollectionConstants.LOG);
        //  lovCollection.drop();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, primavera code, description
                String[] lov = line.split(cvsSplitBy);
                if (lov.length < 3)
                {
                    System.out.println("Skipped " + line);
                }
                else
                {
                    //add the value to the lov table
                    Document lovDoc = new Document(FieldConstants.LOV_DESC, lov[2]);
                    lovDoc.put(FieldConstants.LOV_CODE, FieldConstants.LOV_CODE_PRIMAVERA_TYPE);
                    lovDoc.put(FieldConstants.PRIMAVERA_CODE, lov[1]);

                    lovCollection.insertOne(lovDoc);

                    //and then update all logs with the descriptions
                    UpdateResult results = logCollection.updateMany(
                            Filters.eq(FieldConstants.LEGACY_PRIMAVERA_ID,
                            lov[0]),
                            Updates.set(FieldConstants.PRIMAVERA_DESC, lov[2]));

                    System.out.println("For: " + lov[2] + " updated: " + results.getMatchedCount() + " sirs.");

                }
            }

            System.out.print("loaded" + lovCollection.count());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void loadDeductionCategories(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\LOV_DEDUCTION_CATEGORY.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";
        int insertCount = 0;
        MongoCollection lovCollection = db.getCollection(CollectionConstants.LOV);

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, deduction category code, description
                String[] lov = line.split(cvsSplitBy);
                if (lov.length != 3)
                {
                    System.out.println("Skipped " + line);
                }
                else
                {
                    //add the value to the lov table
                    Document lovDoc = new Document(FieldConstants.LOV_DESC, lov[2]);
                    lovDoc.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, lov[1]);
                    lovDoc.put(FieldConstants.LOV_CODE, FieldConstants.LOV_CODE_DEDUCTION_CATEGORY);

                    lovCollection.insertOne(lovDoc);
                    insertCount++;
                }
            }

            System.out.print("loaded" + insertCount);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void loadDeductionTypes(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\LOV_DEDUCTION_TYPE.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";
        int insertCount = 0;
        MongoCollection lovCollection = db.getCollection(CollectionConstants.LOV);

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, deduction category code, deduction type, description, gross_ded_ind
                String[] lov = line.split(cvsSplitBy);
                if (lov.length != 5)
                {
                    System.out.println("Skipped " + line);
                }
                else
                {
                    //add the value to the lov table
                    Document lovDoc = new Document(FieldConstants.LOV_DESC, lov[3]);
                    lovDoc.put(FieldConstants.LOV_CODE, FieldConstants.LOV_CODE_DEDUCTION_TYPE);
                    lovDoc.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, lov[1]);
                    lovDoc.put(FieldConstants.DEDUCTION_TYPE_CODE, lov[2]);
                    lovDoc.put(FieldConstants.DEDUCTION_GROSS_DED_IND, lov[4]);


                    lovCollection.insertOne(lovDoc);
                    insertCount++;
                }
            }

            System.out.print("loaded" + insertCount);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void loadDeductions(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\GROSS_DEDUCTIONS.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";
        int insertCount = 0;
        boolean firstRow = true;
        MongoCollection dedCollection = db.getCollection(CollectionConstants.DEDUCTIONS);

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, deduction category code, deduction type, post date, amount, note
                String[] ded = line.split(cvsSplitBy);
                if ((ded.length < 5) || (firstRow))
                {
                    System.out.println("Skipped " + line);
                    if (firstRow)
                    {
                        firstRow = false;
                    }
                }
                else
                {
                    //add the value to the lov table
                    Document dedDoc = new Document();
                    dedDoc.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, ded[1]);
                    dedDoc.put(FieldConstants.DEDUCTION_TYPE_CODE, ded[2]);
                    dedDoc.put(FieldConstants.POST_DATE, DateTimeUtils.stringToDate(ded[3], DateTimeUtils.DateFormats.YYYYMMDD));
                    dedDoc.put(FieldConstants.DEDUCTION_AMT, Double.parseDouble(ded[4]));
                    if (ded.length == 6) {
                        dedDoc.put(FieldConstants.NOTE, ded[5]);
                    }

                    dedCollection.insertOne(dedDoc);
                    insertCount++;
                }
            }

            System.out.print("loaded" + insertCount);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void loadFinancialSummary(MongoDatabase db) {

        String csvFile = "c:\\reliantData\\FINANCIAL_SUMMARY.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\^";
        int insertCount = 0;
        boolean firstRow = true;
        MongoCollection finCollection = db.getCollection(CollectionConstants.INVOICES);

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //id, month_year, hours, total gross, total adjusted gross, received_ind,
                //invoice date, received_date
                String[] fin = line.split(cvsSplitBy);
                if ((fin.length != 8) || (firstRow))
                {
                    System.out.println("Skipped " + line);
                    if (firstRow)
                    {
                        firstRow = false;
                    }
                }
                else
                {
                    //add the value to the lov table
                    Document dedDoc = new Document();
                    dedDoc.put(FieldConstants.MONTH_YEAR, fin[1]);
                    dedDoc.put(FieldConstants.HOURS, fin[2]);
                    dedDoc.put(FieldConstants.TOTAL_GROSS, Double.parseDouble(fin[3]));
                  //  dedDoc.put(FieldConstants.TOTAL_ADJ_GROSS, Double.parseDouble(fin[4]));
                    dedDoc.put(FieldConstants.RECEIVED_IND, fin[5]);
                    dedDoc.put(FieldConstants.INVOICE_DATE, DateTimeUtils.stringToDate(fin[6], DateTimeUtils.DateFormats.YYYYMMDD));
                    dedDoc.put(FieldConstants.RECEIVED_DATE, DateTimeUtils.stringToDate(fin[7], DateTimeUtils.DateFormats.YYYYMMDD));

                    finCollection.insertOne(dedDoc);
                    insertCount++;
                }
            }

            System.out.print("loaded" + insertCount);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("error on " + line);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    private static void convertLogDatesToString(MongoDatabase db)
    {
        MongoCollection logCollection = db.getCollection(CollectionConstants.LOG);

        MongoCursor cursor = logCollection.find().iterator();

        while (cursor.hasNext())
        {
            Document doc = (Document) cursor.next();

            Date dateValue = doc.getDate(FieldConstants.LOG_DATE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateValue);
            String dateString = DateTimeUtils.DateToString(calendar, DateTimeUtils.DateFormats.YYYYMMDD,false);

            UpdateResult result = logCollection.updateOne(Filters.eq("_id", doc.getObjectId("_id")),
                    Updates.set(FieldConstants.LOG_DATE, dateString));

            System.out.println(result.toString());
        }
    }
 }
