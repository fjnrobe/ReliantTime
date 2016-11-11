package dtos;

/**
 * Created by Robertson_Laptop on 9/10/2016.
 */
public class SirPcrViewDto {

    public SirPcrViewDto()
    {
        this.logDto = new LogDto();
        this.sirPcrDto = new SirPcrDto();
    }

    public SirPcrDto getSirPcrDto() {
        return sirPcrDto;
    }

    public void setSirPcrDto(SirPcrDto sirPcrDto) {
        this.sirPcrDto = sirPcrDto;
    }

    public LogDto getLogDto() {
        return logDto;
    }

    public void setLogDto(LogDto logDto) {
        this.logDto = logDto;
    }

    SirPcrDto sirPcrDto;
    LogDto logDto;

    public String getEarliestLogDate() {
        return earliestLogDate;
    }

    public void setEarliestLogDate(String earliestLogDate) {
        this.earliestLogDate = earliestLogDate;
    }

    public String getLatestLogDate() {
        return latestLogDate;
    }

    public void setLatestLogDate(String latestLogDate) {
        this.latestLogDate = latestLogDate;
    }

    String earliestLogDate;
    String latestLogDate;

}
