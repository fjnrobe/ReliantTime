package reports;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by Robertson_Laptop on 9/1/2016.
 */
public class ExcelManager {

   XSSFWorkbook workbook = new XSSFWorkbook();

    XSSFSheet spreadsheet = workbook.createSheet("Sheet Name");
}
