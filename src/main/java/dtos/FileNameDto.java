package dtos;

import common.MonthYear;

/**
 * Created by Robertson_Laptop on 11/30/2016.
 */
public class FileNameDto {

    private String fileName;
    private String fileNameWithPath;
    private MonthYear monthYear;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNameWithPath() {
        return fileNameWithPath;
    }

    public void setFileNameWithPath(String fileNameWithPath) {
        this.fileNameWithPath = fileNameWithPath;
    }

    public MonthYear getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(MonthYear monthYear) {
        this.monthYear = monthYear;
    }
}
