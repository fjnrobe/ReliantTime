package managers;

import com.mongodb.client.MongoDatabase;
import common.MonthYear;
import daos.InvoiceDao;
import daos.LogDao;
import daos.PurchaseOrderDao;
import daos.SIRPCRDao;
import dtos.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.bson.types.ObjectId;
import spark.Response;
import utilities.DateTimeUtils;
import utilities.NumberUtils;
import utilities.SortUtils;

import java.awt.*;
import java.io.*;

import java.util.*;
import java.util.List;

/**
 * Created by Robertson_Laptop on 9/7/2016.
 */
public class ReportsManager {

    private final LogDao logDao;
    private final SIRPCRDao sirPcrDao;
    private final InvoiceDao invoiceDao;
    private final PurchaseOrderDao purchaseOrderDao;
    private LogManager logManager;

    public ReportsManager(MongoDatabase reliantDb) {

        logDao = new LogDao(reliantDb);
        sirPcrDao = new SIRPCRDao(reliantDb);
        invoiceDao = new InvoiceDao(reliantDb);
        purchaseOrderDao= new PurchaseOrderDao(reliantDb);

    }

    public void setLogManager(LogManager logManager)
    {
        this.logManager = logManager;
    }

    public List<LogDto> getInnotasHours(String startDate)
    {
        return this.logDao.getInnotasHours(startDate);
    }

    public byte [] createInvoicePackage(String invoiceNumber)
    {
        byte[] outArray = new byte[0];
        HSSFWorkbook workbook = new HSSFWorkbook();

        try {

            FinancialSearchCriteriaDto searchCriteriaDto = new FinancialSearchCriteriaDto();
            searchCriteriaDto.setInvoiceNumber(invoiceNumber);
            List<InvoiceDto> invoiceDtos = this.invoiceDao.getInvoicesByCriteria(searchCriteriaDto);
            InvoiceDto invoiceDto = invoiceDtos.get(0);

            this.createMonthlyTimesheet(workbook, invoiceDto.getMonthYear());
            this.createMonthlyInvoice(workbook, invoiceNumber);

            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            workbook.write(outByteStream);
            workbook.close();
            outArray = outByteStream.toByteArray();
        }
        catch (Exception e)
        {

        }
        return outArray;

    }

    public void createMonthlyInvoice(HSSFWorkbook workbook, String invoiceNumber)
    {
        FinancialSearchCriteriaDto searchCriteriaDto = new FinancialSearchCriteriaDto();
        searchCriteriaDto.setInvoiceNumber(invoiceNumber);
        List<InvoiceDto> invoiceDtos = this.invoiceDao.getInvoicesByCriteria(searchCriteriaDto);
        InvoiceDto invoiceDto = invoiceDtos.get(0);

        PurchaseOrderDto purchaseOrderDto =
                    this.purchaseOrderDao.getPurchaseOrderByPONumber(invoiceDto.getPoNumber());

      //  byte [] outArray = new byte[0];
      //  HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Invoice " + invoiceDto.getMonthYear().getMonthName() + " " + invoiceDto.getMonthYear().getYearName() +
                                            " " + invoiceDto.getPoNumber());
        int rowIdx = 0;
        int colIdx = 0;
        String line = "";


        HSSFCellStyle styleCurrencyFormat = workbook.createCellStyle();
        styleCurrencyFormat.setDataFormat((short)8);

        HSSFCellStyle styleNumberFormat = workbook.createCellStyle();
        styleNumberFormat.setDataFormat((short) 4);

        try {

            //Create a new row in current sheet
            Row row = sheet.createRow(rowIdx++);

            Cell cell = row.createCell(0);
            //write the header row
            cell.setCellValue("Reliant Software, LLC");
            cell.setCellStyle(this.createStyle(workbook, true, false,false,false));

            cell = row.createCell(4);
            cell.setCellValue("RS-LLC");
            cell.setCellStyle(this.createStyle(workbook, true, false,false,false));

            row = sheet.createRow(rowIdx++);
            row.setHeight((short) (41*20));
            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue(DateTimeUtils.DateToString(new Date(), DateTimeUtils.DateFormats.MMMDDYYYY, false));

            row = sheet.createRow(rowIdx++);
            row.setHeight((short) (45*20));
            row = sheet.createRow(rowIdx++);

            cell = row.createCell(0);
            cell.setCellValue("Ernesto Belmonte");

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Belmonte Enterprises, LLC");

            row = sheet.createRow(rowIdx++);
            row.setHeight((short) (55*20));
            row = sheet.createRow(rowIdx++);

            line = "Invoice for Consulting Services - " +
                    invoiceDto.getMonthYear().getMonthName() + " - " +
                    invoiceDto.getMonthYear().getYearName();
            cell = row.createCell(1);
            cell.setCellValue(line);
            cell.setCellStyle(this.createStyle(workbook, true , true, false, false));
            //need to backup 1 row becuase after the row was added above, we bumped the row value
            sheet.addMergedRegion(new CellRangeAddress(
                    rowIdx-1, //first row (0-based)
                    rowIdx-1, //last row (0-based)
                    1, //first column (0-based)
                    4 //last column (0-based)
            ));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("Purchase Order Title:");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_DOUBLE,
                                HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN,
                                HSSFCellStyle.BORDER_DOUBLE));

            cell = row.createCell(2);
            cell.setCellValue("");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE));

            cell = row.createCell(3);
            HSSFCellStyle style = workbook.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            cell.setCellStyle(this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN));
            cell.setCellStyle(style);
            cell.setCellValue("PO #");

            cell = row.createCell(4);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE));
            cell.setCellValue(invoiceDto.getPoNumber());

            row = sheet.createRow(rowIdx++);
            row.setHeight((short) (28*20));
            style = workbook.createCellStyle();
            style.setWrapText(true);
            cell = row.createCell(1);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE);
            cell.setCellValue(purchaseOrderDto.getPoTitle());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN);
            cell.setCellValue("");
            cell.setCellStyle(style);

            cell = row.createCell(3);
            style = workbook.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            cell.setCellStyle(style);
            cell.setCellValue("Invoice ID #");
            cell = row.createCell(4);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE));
            cell.setCellValue(invoiceDto.getInvoiceNumber());

            sheet.addMergedRegion(new CellRangeAddress(
                    rowIdx-1, //first row (0-based)
                    rowIdx-1, //last row (0-based)
                    1, //first column (0-based)
                    2 //last column (0-based)
            ));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell = row.createCell(2);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE));
            cell = row.createCell(4);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell = row.createCell(2);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE));
            cell = row.createCell(4);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell = row.createCell(2);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE));
            cell = row.createCell(4);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("Summary");
            style = this.createStyle(workbook,true,true,false,false);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE);
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue("");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE));

            cell = row.createCell(3);
            cell.setCellValue("");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE));

            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE));

            sheet.addMergedRegion(new CellRangeAddress(
                    rowIdx-1, //first row (0-based)
                    rowIdx-1, //last row (0-based)
                    1, //first column (0-based)
                    4 //last column (0-based)
            ));
            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            style = this.createStyle(workbook, false, true, false, false);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE);

            cell.setCellValue("Employee");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("No. of Hours");
            cell.setCellStyle(this.createStyle(workbook, false, true, true, false));
            cell = row.createCell(3);
            cell.setCellValue("Hourly Rate");
            cell.setCellStyle(this.createStyle(workbook, false, true, true, false));
            cell = row.createCell(4);
            cell.setCellValue("Total Amount");
            style = this.createStyle(workbook, false, true, true, false);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN);
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            row.setHeight((short) (28*20));
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell.setCellValue("Jeff Robertson");

            cell = row.createCell(2);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN));
            cell.setCellValue(invoiceDto.getHours());

            cell = row.createCell(3);
            cell.setCellValue(purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate());
            style = workbook.createCellStyle();
            cell.setCellStyle(this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN));
            style.setDataFormat((short) 8);
            cell.setCellStyle(style);

            cell = row.createCell(4);
            double netGross = invoiceDto.getTotalGross() - (purchaseOrderDto.getPassthruRate() * invoiceDto.getHours());

            cell.setCellValue(netGross);
            style = workbook.createCellStyle();
            cell.setCellStyle(this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN));
            style.setDataFormat((short) 8);
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell = row.createCell(2);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE));

            cell = row.createCell(3);
            cell.setCellValue("Total Invoice Amount");
            style = this.createStyle(workbook, true, false, true,false);
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE);

            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(netGross);

            style = this.createStyle(workbook, true,false,true, false);
            style.setDataFormat((short)8);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE);
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            row = sheet.createRow(rowIdx++);
            sheet.addMergedRegion(new CellRangeAddress(
                    rowIdx-1, //first row (0-based)
                    rowIdx-1, //last row (0-based)
                    1, //first column (0-based)
                    3 //last column (0-based)
            ));
            cell = row.createCell(1);
            cell.setCellValue("Purchase Order Recap");
            style = this.createStyle(workbook, true, true,false,false);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE);

            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE));
            cell = row.createCell(3);
            cell.setCellValue("");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell = row.createCell(2);
            cell.setCellValue("Hours");
            cell.setCellStyle(this.createStyle(workbook, true, true,true,false));
            cell = row.createCell(3);
            cell.setCellValue("Cost");
            style = this.createStyle(workbook, true, true,true,false);
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_THIN);
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell.setCellValue("Original P.O. Amount");

            cell = row.createCell(2);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE);
            style.setDataFormat((short) 4);
            cell.setCellValue(purchaseOrderDto.getTotalHours());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE);
            style.setDataFormat((short) 8);

            cell.setCellValue(purchaseOrderDto.getTotalHours() * purchaseOrderDto.getHourlyRate());
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("Invoice Balance Remaining");
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE));

            cell = row.createCell(2);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE);
            style.setDataFormat((short) 4);
            cell.setCellValue(invoiceDto.getPriorHoursRemaining());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE);
            cell.setCellValue(invoiceDto.getPriorHoursRemaining() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));
            style.setDataFormat((short) 8);
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell.setCellValue("Amount of current invoice");

            cell = row.createCell(2);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE);
            style.setDataFormat((short) 4);
            cell.setCellValue(invoiceDto.getHours());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_THIN,
                    HSSFCellStyle.BORDER_NONE);

            cell.setCellValue(invoiceDto.getHours() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));
            style.setDataFormat((short) 8);
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellStyle(this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_DOUBLE));
            cell.setCellValue("Balance remaining");

            cell = row.createCell(2);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE);
            style.setDataFormat((short) 4);
            cell.setCellValue(invoiceDto.getHoursRemaining());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            style = workbook.createCellStyle();
            this.createBorderStyle(workbook, style, HSSFCellStyle.BORDER_NONE,
                    HSSFCellStyle.BORDER_DOUBLE, HSSFCellStyle.BORDER_DOUBLE,
                    HSSFCellStyle.BORDER_NONE);
            style.setDataFormat((short) 8);
            cell.setCellValue(invoiceDto.getHoursRemaining() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            row.setHeight((short) (58*20));
            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Please make check payable to Reliant Software, LLC.  Invoice is due and payable upon receipt.");

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Please send payment to the attention of:");

            row = sheet.createRow(rowIdx++);
            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("Jeff Robertson");
            cell.setCellStyle(this.createStyle(workbook, true,false,false,false));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("Reliant Software, LLC.");
            cell.setCellStyle(this.createStyle(workbook, true,false,false,false));

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("9026 La Valencia Ct");
            cell.setCellStyle(this.createStyle(workbook, true,false,false,false));


            row = sheet.createRow(rowIdx++);
            cell = row.createCell(1);
            cell.setCellValue("Elk Grove, CA 95624");
            cell.setCellStyle(this.createStyle(workbook, true,false,false,false));

            sheet.setColumnWidth(0, 7*256);
            sheet.setColumnWidth(1, 25*256);
            sheet.setColumnWidth(2, 12*256);
            sheet.setColumnWidth(3, 17*256);
            sheet.setColumnWidth(4, 14*256);

//            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
//            workbook.write(outByteStream);
//            workbook.close();
//            outArray = outByteStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        return outArray;
    }

    public void createMonthlyTimesheet(HSSFWorkbook workbook, MonthYear monthYear)
    {

        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        List<CalendarDto> data = logManager.getCalendarHoursByMonth(monthYear);
        List<TimesheetDto> timeSheetDtos = this.generateHoursCalendar(monthYear, data);

        PurchaseOrderDto purchaseOrderDto = this.purchaseOrderDao.getPurchaseOrderByMonthYear(monthYear);

//        byte [] outArray = new byte[0];
//        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("timesheet " + monthYear.getMonthName() + " " +
                monthYear.getYearName() +
                " " + purchaseOrderDto.getPoNumber());

        int rowIdx = 0;
        int colIdx = 0;
        String line = "";


//        HSSFCellStyle styleCurrencyFormat = workbook.createCellStyle();
//        styleCurrencyFormat.setDataFormat((short)8);
//
//        HSSFCellStyle styleNumberFormat = workbook.createCellStyle();
//        styleNumberFormat.setDataFormat((short) 4);

        HSSFCellStyle borderCell = this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN);
        borderCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        HSSFCellStyle headerCell = this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_THIN,
                HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_THIN);
        headerCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerCell.setFont(boldFont);
        headerCell.setWrapText(true);

        HSSFCellStyle underLineCell = this.createBorderStyle(workbook, null, HSSFCellStyle.BORDER_NONE,
                HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_THIN, HSSFCellStyle.BORDER_NONE);
        underLineCell.setFont(boldFont);

        try {

            //Create a new row in current sheet
            Row row = sheet.createRow(rowIdx++);

            Cell cell = row.createCell(0);
            //write the header row
            cell.setCellValue("Belmonte Enterprises, LLC");
            cell.setCellStyle(this.createStyle(workbook, true, false,false,false));

            cell = row.createCell(8);
            cell.setCellValue("Monthly Time Sheet");
            HSSFCellStyle style = this.createStyle(workbook, true, false,false,false);
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            cell.setCellStyle(style);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("1315 39th Street");

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Sacramento, Ca 95816");

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("(916) 804-5479 : Mobile");

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("(916) 452-7702 : Fax");

            cell = row.createCell(6);
            cell.setCellValue(monthYear.getMonthName() + " " + monthYear.getYearName());

            row = sheet.createRow(rowIdx++);
            for (int x = 0; x <= 8; x++)
            {
                cell = row.createCell(x);
                cell.setCellStyle(underLineCell);
            }
            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Project: " + purchaseOrderDto.getPoTitle());
            cell.setCellStyle(underLineCell);
            for (int x = 1; x <= 5; x++)
            {
                cell = row.createCell(x);
                cell.setCellStyle(underLineCell);
            }
            cell = row.createCell(6);
            cell.setCellValue("PO Number: " + purchaseOrderDto.getPoNumber());
            cell.setCellStyle(underLineCell);
            for (int x = 7; x <= 8; x++)
            {
                cell = row.createCell(x);
                cell.setCellStyle(underLineCell);
            }

            //-------------------------------------------------------------------------

            Row row1 = sheet.getRow(0); //header row
            Row row2  = sheet.getRow(0); // Jeff Robertson
            Row row3 = sheet.getRow(0); //blank row
            Row row4 = sheet.getRow(0); //total row
            Cell cell1; //header row cell
            Cell cell2; //Jeff Robertson/hours
            Cell cell3; //blank
            Cell cell4; //total cell

            int prevWeek = 0;

            double weekTotalHours = 0.0;
            double monthTotalHours = 0.0;
            boolean firstRow = true;


            for (TimesheetDto timesheetDto : timeSheetDtos)
            {

                Calendar cal = DateTimeUtils.convertMonthYear(monthYear, timesheetDto.getDayOfMonth());
                String dateString = DateTimeUtils.DateToString(cal, DateTimeUtils.DateFormats.MMMDDYYYY, true);

                //if this is a break on week, add the row and move to the next week
                if (timesheetDto.getWeekOfMonth() > prevWeek)
                {
                    if (prevWeek > 0)
                    {
                        cell1 = row1.createCell(8);
                        cell1.setCellStyle(borderCell);

                        cell2 = row2.createCell(8);
                        cell2.setCellStyle(borderCell);
                        cell2.setCellValue(weekTotalHours);
                        cell3 = row3.createCell(8);
                        cell3.setCellStyle(borderCell);
                        cell4 = row4.createCell(8);
                        cell4.setCellStyle(borderCell);
                        cell4.setCellValue(weekTotalHours);
                    }

                    prevWeek = timesheetDto.getWeekOfMonth();
                    weekTotalHours = 0.0;
                    colIdx = 1;

                    //consultant name column
                    sheet.createRow(rowIdx++);
                    sheet.createRow(rowIdx++);
                    row1 = sheet.createRow(rowIdx++);
                    row2 = sheet.createRow(rowIdx++);
                    row3 = sheet.createRow(rowIdx++);
                    row4 = sheet.createRow(rowIdx++);

                    cell1 = row1.createCell(0);
                    cell1.setCellStyle(headerCell);
                    cell1.setCellValue("Consultant Name");
                    cell2 = row2.createCell(0);
                    cell2.setCellStyle(borderCell);
                    cell2.setCellValue("Jeff Robertson");
                    cell3 = row3.createCell(0);
                    cell3.setCellStyle(borderCell);
                    cell4 = row4.createCell(0);
                    cell4.setCellStyle(borderCell);

                    //when adding the first row to the calendar, we need to add enough columns so that the
                    // first day of the month starts on the right day
                    //configured so that Sunday is the first day of the week
                    //also - if the first working day is monday - then we need to skip sunday
                    if (firstRow) {

                        firstRow = false;
                        int daysFromLastMonth = timesheetDto.getDayOfWeek() - 1;  //ex. if the first day of the month is on a thursday - this would return (4) - meaning we need 3 blank days (Sun, Mon, Tues, Wed)
                        for (int dayIdx = 1; dayIdx <= daysFromLastMonth; dayIdx++) {

                            //day of week
                            colIdx = dayIdx;
                            cell1 = row1.createCell(dayIdx);
                            cell1.setCellStyle(headerCell);

                            cell1.setCellValue(daysOfWeek[dayIdx - 1]);

                            cell2 = row2.createCell(dayIdx);
                            cell2.setCellStyle(borderCell);
                            cell3 = row3.createCell(dayIdx);
                            cell3.setCellStyle(borderCell);
                            cell4 = row4.createCell(dayIdx);
                            cell4.setCellStyle(borderCell);
                        }
                        colIdx++;
                    }

                }

                String dayOfMonth = "";
                if (timesheetDto.getDayOfMonth() < 10)
                {
                    dayOfMonth = "0" + timesheetDto.getDayOfMonth();
                }
                else
                {
                    dayOfMonth =String.valueOf(timesheetDto.getDayOfMonth());
                }

                cell1 = row1.createCell(colIdx++);
                cell1.setCellStyle(headerCell);
                cell1.setCellValue(daysOfWeek[colIdx - 2]  + "\n" + dateString);

                cell2 = row2.createCell(colIdx-1);
                cell2.setCellStyle(borderCell);
                cell2.setCellValue(timesheetDto.getHoursForDay());
                cell3 = row3.createCell(colIdx-1);
                cell3.setCellStyle(borderCell);
                cell4 = row4.createCell(colIdx-1);
                cell4.setCellStyle(borderCell);
                cell4.setCellValue(timesheetDto.getHoursForDay());

                weekTotalHours += timesheetDto.getHoursForDay();
                monthTotalHours += timesheetDto.getHoursForDay();

            }
            //the last week of the month most often won't end on Saturday - so need to add blank days to fill out the week to seven days
            if (prevWeek > 0)
            {
                //get the last day in the report data, to get what day in the week the day is. then we add the needed blank days to
                //get 7 days in the week
                TimesheetDto lastDayInMonth = timeSheetDtos.get(timeSheetDtos.size() - 1);

                int daysLeftInMonth = 7 - lastDayInMonth.getDayOfWeek();
                for (int dayIdx = 1; dayIdx <= daysLeftInMonth; dayIdx++ )
                {
                    //day of week

                    cell1 = row1.createCell(colIdx++);
                    cell1.setCellStyle(headerCell);
                    cell1.setCellValue(daysOfWeek[colIdx - 2]);

                    cell2 = row2.createCell(colIdx-1);
                    cell2.setCellStyle(borderCell);
                    cell3 = row3.createCell(colIdx-1);
                    cell3.setCellStyle(borderCell);
                    cell4 = row4.createCell(colIdx-1);
                    cell4.setCellStyle(borderCell);
                }

                cell1 = row1.createCell(colIdx++);
                cell1.setCellStyle(headerCell);

                cell2 = row2.createCell(colIdx-1);
                cell2.setCellStyle(borderCell);
                cell2.setCellValue(weekTotalHours);
                cell3 = row3.createCell(colIdx-1);
                cell3.setCellStyle(borderCell);
                cell4 = row4.createCell(colIdx-1);
                cell4.setCellStyle(borderCell);
                cell4.setCellValue(weekTotalHours);
            }

            row = sheet.createRow(rowIdx++);
            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Jeff Robertson");
            for (int i = 1; i < 4; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(underLineCell);
            }
            cell = row.createCell(4);
            cell.setCellValue("Date: " + DateTimeUtils.getTodayAsString(DateTimeUtils.DateFormats.MMMDDYYYY, true));

            cell = row.createCell(7);
            cell.setCellValue("Monthly Total");

            cell = row.createCell(8);
            cell.setCellValue(monthTotalHours);
            style = workbook.createCellStyle();
            style.setFont(boldFont);
            cell.setCellStyle(style);

            //-------------------------------------------------------------------------
            sheet.setColumnWidth(0, 17*256);
            sheet.setColumnWidth(1, 11*256);
            sheet.setColumnWidth(2, 11*256);
            sheet.setColumnWidth(3, 11*256);
            sheet.setColumnWidth(4, 12*256);
            sheet.setColumnWidth(5, 11*256);
            sheet.setColumnWidth(6, 11*256);
            sheet.setColumnWidth(7, 11*256);
            sheet.setColumnWidth(8, 9*256);

//            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
//            workbook.write(outByteStream);
//            workbook.close();
//            outArray = outByteStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        return outArray;
    }

    public List<TimesheetDto> generateHoursCalendar(MonthYear monthYear,
                                                    List<CalendarDto> dayData)
    {
        //the key will be the day of the month
        Map<Integer, TimesheetDto> timeSheetMap = new HashMap<Integer,TimesheetDto>();

        List<TimesheetDto> timesheetDtos = new ArrayList<TimesheetDto>();

        Calendar cal = new GregorianCalendar();
        for (CalendarDto day : dayData)
        {

            cal.set(monthYear.getYear(), monthYear.getMonth() - 1, day.getIntDayOfMonth());

            TimesheetDto timesheetDto = timeSheetMap.get(new Integer(cal.get(Calendar.DAY_OF_MONTH)));

            //if we haven't added one yet, add it now
            if (timesheetDto == null)
            {
                timesheetDto = new TimesheetDto();
                timesheetDto.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
                timesheetDto.setDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
                timesheetDto.setWeekOfMonth(cal.get(Calendar.WEEK_OF_MONTH));

                timeSheetMap.put(new Integer(day.getIntDayOfMonth()), timesheetDto);
            }

            timesheetDto.setHoursForDay(timesheetDto.getHoursForDay() + day.getHours());
        }

        cal.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);

        //fill in any days without bill hours so the month is complete
        //get the entry in the report set for the sir
        for (int dayIdx  = 1; dayIdx <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); dayIdx++)
        {
            TimesheetDto timesheetDto = timeSheetMap.get(new Integer(dayIdx));
            //if we haven't added one yet, add it now
            if (timesheetDto == null)
            {
                timesheetDto = new TimesheetDto();

                Calendar missingDay = Calendar.getInstance();
                missingDay.set(monthYear.getYear(), monthYear.getMonth() - 1, dayIdx);

                timesheetDto.setDayOfMonth(missingDay.get(Calendar.DAY_OF_MONTH));
                timesheetDto.setDayOfWeek(missingDay.get(Calendar.DAY_OF_WEEK));
                timesheetDto.setWeekOfMonth(missingDay.get(Calendar.WEEK_OF_MONTH));
                timesheetDto.setHoursForDay(0);
                timeSheetMap.put(new Integer(dayIdx), timesheetDto);
            }
        }


        Iterator iterator =  timeSheetMap.values().iterator();
        while (iterator.hasNext())
        {
            TimesheetDto record = (TimesheetDto) iterator.next();
            record.setHoursForDay(NumberUtils.roundHours(record.getHoursForDay()));
            timesheetDtos.add(record);

        }

        return SortUtils.sortHoursCalendarByWeekAndDay(timesheetDtos);
    }

    public byte [] createMonthlyStatus(MonthYear monthYear)
    {
        byte [] outArray = new byte[0];

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Status Report");
        int rowIdx = 0;
        int colIdx = 0;
        String line = "";
       // String fileName = "Monthly_Status_" + monthYear.getMonthName() + "_" + monthYear.getYearName() + ".html";

        String statusDesc = "";
        String estCompleteDate = "";
        String sirNumber = "";

        int prevWeek = -1;
        double monthTotalHours = 0.0;
        List<ReportRecordDto> reportData =this.generateMonthAtAGlanceReport(monthYear);
        Map<ObjectId, SirPcrViewDto> sirSummary = this.logDao.getSirSummaryRangeInfo();


        try {

            //Create a new row in current sheet
            Row row = sheet.createRow(rowIdx++);
            //Create a new cell in current row
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row (0-based)
                    0, //first column (0-based)
                    6 //last column (0-based)
            ));
            Cell cell = row.createCell(0);
            //write the header row
            cell.setCellValue("Jeff Robertson's Monthly Status Report For " +
                                monthYear.getMonthName() + " " +
                                monthYear.getYearName());
            cell.setCellStyle(this.createStyle(workbook, true, true,true,true));


            for (ReportRecordDto data : reportData)
            {

                //put out the start date / end date for the current sir
                SirPcrViewDto sirData = sirSummary.get(data.getSirPcrViewDto().getSirPcrDto().getId());

                //if this is a break on week, insert a week header row
                if (data.getFirstDayOfWeek() > prevWeek)
                {

                    row = sheet.createRow(rowIdx++);
                    row = sheet.createRow(rowIdx++);

                    //format as Report Date: MonthName dd - dd
                   GregorianCalendar calStart =
                           DateTimeUtils.stringToGregorianCalendar( data.getSirPcrViewDto().getLogDto().getLogDate(), DateTimeUtils.DateFormats.YYYYMMDD);

                    line = "Report Date: " +
                            calStart.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " +
                            String.valueOf(data.getFirstDayOfWeek()) + " - " +
                            String.valueOf(data.getLastDayOfWeek());

                    cell = row.createCell(0);
                    cell.setCellValue(line);
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    colIdx = 0;
                    row = sheet.createRow(rowIdx++);
                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Activity Type");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Status");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Est Compl. Date");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Total LOE Hrs");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Sub Process/Activity");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("SCR / SIR / PCR");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    cell = row.createCell(colIdx++);
                    cell.setCellValue("Activity Reference and Description of Accomplishments/Tasks");
                    cell.setCellStyle(this.createStyle(workbook, true, false,true,true));

                    row = sheet.createRow(rowIdx++);

                    prevWeek = data.getFirstDayOfWeek();

                }

                //if the sir is a sir/ pcr - then set the status / est completion dates
                if (!data.getSirPcrViewDto().getSirPcrDto().getSirType().equals("OTHER"))
                {

                    //initialize a calendar to the last day of the week
                    Calendar week = Calendar.getInstance();
                    week.set(monthYear.getYear(), monthYear.getMonth() - 1, data.getLastDayOfWeek());

                    //if the activity is still active after the end of the current week
                    Calendar completionDay = null;
                    if (DateTimeUtils.stringToGregorianCalendar(
                            sirData.getLatestLogDate(), DateTimeUtils.DateFormats.MMDDYYYY).after(week))
                    {
                        //set the status to ongoing
                        statusDesc = "Ongoing";

                        //if the sir is currently done, set the completion date to the friday of the week it was completed
                        if (data.getSirPcrViewDto().getSirPcrDto().getCompletedInd() == true)
                        {
                            //set the completion day to the friday in the week that the activity finished
                            completionDay = DateTimeUtils.getLastFridayOfWeek(
                                    DateTimeUtils.stringToGregorianCalendar(sirData.getLatestLogDate(), DateTimeUtils.DateFormats.YYYYMMDD));
                        }
                        //if still ongoing, set to the friday of the next week (we are in)
                        else
                        {
                            //set the completion day to the friday in the next week
                            completionDay = DateTimeUtils.getLastFridayOfNextWeek(monthYear, data.getWeekOfMonth());
                        }
                    }
                    //if it is done, set to the friday of the week in which it ends
                    else
                    {

                        //if the sir is currently done, set the completion date to the friday of the week it was completed
                        if (data.getSirPcrViewDto().getSirPcrDto().getCompletedInd() == false)
                        {
                            statusDesc = "Done";
                            //set the completion day to the friday in the week that the activity finished
                            completionDay = DateTimeUtils.getLastFridayOfWeek(
                                    DateTimeUtils.stringToGregorianCalendar(sirData.getLatestLogDate(), DateTimeUtils.DateFormats.YYYYMMDD));
                        }
                        //if still ongoing, set to the friday of the next week (we are in)
                        else
                        {
                            //set the status to ongoing
                            statusDesc = "Ongoing";

                            //set the completion day to the friday in the next week
                            completionDay = DateTimeUtils.getLastFridayOfNextWeek(monthYear, data.getWeekOfMonth());
                        }

                    }

                    estCompleteDate = DateTimeUtils.DateToString(completionDay, DateTimeUtils.DateFormats.MMDDYYYY, true);
                }
                else
                {
                    statusDesc = "Ongoing";
                    estCompleteDate = "NA";
                }

                if (Integer.parseInt(data.getSirPcrViewDto().getSirPcrDto().getSirPcrNumber()) <= 0)
                {
                    sirNumber = "";
                }
                else
                {
                    sirNumber = data.getSirPcrViewDto().getSirPcrDto().getSirPcrNumber();
                }

                colIdx = 0;
                row = sheet.createRow(rowIdx++);
                cell = row.createCell(colIdx++);
                cell.setCellValue(data.getSirPcrViewDto().getLogDto().getActivityDesc());
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(statusDesc);
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(estCompleteDate);
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(String.valueOf(NumberUtils.roundHours(data.getHoursWithinWeek())));
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(data.getSirPcrViewDto().getSirPcrDto().getSubProcessDesc());
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(sirNumber);
                cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

                cell = row.createCell(colIdx++);
                cell.setCellValue(data.getSirPcrViewDto().getSirPcrDto().getSirDesc());
                HSSFCellStyle style = this.createStyle(workbook, false, false, true, false);
                style.setWrapText(true);
                cell.setCellStyle(style);

                monthTotalHours += data.getHoursWithinWeek();

            }

            row = sheet.createRow(rowIdx++);
            row = sheet.createRow(rowIdx++);

            row = sheet.createRow(rowIdx++);
            cell = row.createCell(0);
            cell.setCellValue("Total Monthly Hours: " + String.valueOf(NumberUtils.roundHours(monthTotalHours)));
            cell.setCellStyle(this.createStyle(workbook, false, false, true, false));

            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            workbook.write(outByteStream);
            workbook.close();
            outArray = outByteStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

       return outArray;
    }

    private HSSFCellStyle createBorderStyle(HSSFWorkbook workbook,
                                            HSSFCellStyle style,
                                            short borderTop, short borderRight,
                                            short borderBottom, short borderLeft)
    {
        if (style == null)
        {
            style = workbook.createCellStyle();
        }
        style.setBorderBottom(borderBottom);
        style.setBorderLeft(borderLeft);
        style.setBorderRight(borderRight);
        style.setBorderTop(borderTop);

        return style;
    }

    private HSSFCellStyle createStyle(HSSFWorkbook workbook,
                                      boolean bold, boolean centerAlign, boolean withBorder, boolean withBackground)
    {
        HSSFFont font = workbook.createFont();
        HSSFCellStyle style = workbook.createCellStyle();

        if (bold) {
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
        }

        if (centerAlign)
        {
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        }

        if (withBorder)
        {
            style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
            style.setBottomBorderColor(
                    IndexedColors.BLACK.getIndex());
            style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            style.setLeftBorderColor(
                    IndexedColors.BLACK.getIndex());
            style.setBorderRight(XSSFCellStyle.BORDER_THIN);
            style.setRightBorderColor(
                    IndexedColors.BLACK.getIndex());
            style.setBorderTop(XSSFCellStyle.BORDER_THIN);
            style.setTopBorderColor(
                    IndexedColors.BLACK.getIndex());
        }

        if (withBackground)
        {
            style.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);
            style.setFillPattern(HSSFCellStyle.FINE_DOTS);
            style.setAlignment(HSSFCellStyle.ALIGN_FILL);
        }

        return style;
    }


    public List<ReportRecordDto> generateMonthAtAGlanceReport(MonthYear monthYear)
    {
        //the key will be the week number and the sir number
        Map<String, ReportRecordDto> reportRecordMap = new HashMap<String,ReportRecordDto>();

        List<ReportRecordDto> reportRecords = new ArrayList<ReportRecordDto>();

        //get a summary of the sirs for the incoming month/year
        Calendar startDate = Calendar.getInstance();
        startDate.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(monthYear.getYear(), monthYear.getMonth() - 1, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        List<LogDto> logsInMonth = this.logDao.getLogsByDateRange(
                DateTimeUtils.DateToString(startDate, DateTimeUtils.DateFormats.YYYYMMDD, false),
                DateTimeUtils.DateToString(endDate, DateTimeUtils.DateFormats.YYYYMMDD, false));

        for (LogDto logInMonth : logsInMonth)
        {
            SirPcrViewDto viewDto = new SirPcrViewDto();
            viewDto.setLogDto(logInMonth);
            viewDto.setSirPcrDto(this.sirPcrDao.getSirById(logInMonth.getSirPcrId()));

            //get the entry in the report set for the current sir in the current week
            Calendar cal = DateTimeUtils.isValidDate(logInMonth.getLogDate(),
                                            DateTimeUtils.DateFormats.YYYYMMDD);

            int weekInMonth = cal.get(Calendar.WEEK_OF_MONTH);
            String entryKey = this.reportRecordEntryKey(weekInMonth, logInMonth.getSirPcrId().toString());

            ReportRecordDto reportRecord = reportRecordMap.get(entryKey);
            //if we haven't added one yet, add it now
            if (reportRecord == null)
            {
                reportRecord = new ReportRecordDto();
                reportRecord.setSirPcrViewDto(viewDto);
                reportRecord.setWeekOfMonth(cal.get(Calendar.WEEK_OF_MONTH));

                //initialize a calendar to the current month being processed
                Calendar week = Calendar.getInstance();
               // week.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                week.set(monthYear.getYear(), monthYear.getMonth() - 1, 1);

                //to get the first day of the week - create a calendar and then set the week to the sir's week in the month
                //and set the day of the week to Sunday - the first day of each week

                //need to capture the last day of month - below when adding 6 days to get the end of the
                //week, we could go into the next month
                int lastDayOfMonth =  week.getActualMaximum(Calendar.DAY_OF_MONTH);
                week.set(Calendar.WEEK_OF_MONTH, cal.get(Calendar.WEEK_OF_MONTH));
                week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                reportRecord.setFirstDayOfWeek(week.get(Calendar.DATE));

                //if the day of week is greater than current day, we went back to the prior month - need to back
                //up to the first of the month (which is always 1)
                if (reportRecord.getFirstDayOfWeek() > cal.get(Calendar.DAY_OF_MONTH))
                {
                    reportRecord.setFirstDayOfWeek(1);
                }

                //to get the end of the week, add 6 days to the calendar
                week.add(Calendar.DAY_OF_WEEK, 6);
                reportRecord.setLastDayOfWeek(week.get(Calendar.DATE));

                //if the last day of the week is before the first day of the week, we've past into the next month.
                //in this case, set the last day of the week to the last day of the month
                if (reportRecord.getLastDayOfWeek() < reportRecord.getFirstDayOfWeek() )
                {
                    reportRecord.setLastDayOfWeek(lastDayOfMonth);
                }

                reportRecordMap.put(entryKey, reportRecord);
            }

            //finally, we need to tally the number of hours of the current sir that fall within the current week
            reportRecord.setHoursWithinWeek(reportRecord.getHoursWithinWeek() + viewDto.getLogDto().getHours());
        }
        Iterator iterator =  reportRecordMap.values().iterator();
        while (iterator.hasNext())
        {
            ReportRecordDto record = (ReportRecordDto) iterator.next();
            record.setHoursWithinWeek(NumberUtils.roundHours(record.getHoursWithinWeek()));
            reportRecords.add(record);

        }

        return SortUtils.sortMonthlyReportByWeek(reportRecords);
    }


    private String reportRecordEntryKey(int weekNumber, String sirNumber)
    {
        return String.valueOf(weekNumber) + "|" + sirNumber;
    }

    public List<MonthYear> getAllMonthsWithLoggedPeriods( boolean ascending)
    {
        List<LogDto> allLogs = logDao.getLogsByDateRange(DateTimeUtils.LOW_DATE,
                DateTimeUtils.HIGH_DATE);

        //build a set of unique start months
        SortedSet<MonthYear> monthYears = new TreeSet<MonthYear>();

        for (LogDto allLog : allLogs)
        {

            MonthYear monthYear = new MonthYear(allLog.getLogDate());

            GregorianCalendar calDate = DateTimeUtils.stringToGregorianCalendar(allLog.getLogDate(), DateTimeUtils.DateFormats.YYYYMMDD);

            monthYear.setMonthName(calDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
            monthYear.setYearName(String.valueOf(calDate.get(Calendar.YEAR)));

            String monthNumber = "";
            int month = calDate.get(Calendar.MONTH) + 1;

            monthYear.setMonth(month);
            monthYear.setYear(calDate.get(Calendar.YEAR));

            if (month < 10)
            {
                monthNumber = "0" + String.valueOf(month);
            }
            else
            {
                monthNumber = String.valueOf(month);
            }
            String sortKey = monthYear.getYearName() + monthNumber;

            monthYear.setSortKey(sortKey);

            monthYears.add(monthYear);
        }

        ArrayList<MonthYear> aList = new ArrayList<MonthYear>();
        for (MonthYear monthYear : monthYears)
        {
            aList.add(monthYear);
        }

        return SortUtils.sortMonthYear(aList, ascending);
    }

}
