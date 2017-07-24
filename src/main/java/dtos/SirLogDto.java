package dtos;

/**
 * Created by Robertson_Laptop on 7/23/2017.
 */
public class SirLogDto {

    LogDto logDto;
    SirPcrDto sirPcrDto;

    public SirLogDto ()
    {
        this.logDto = new LogDto();
        this.sirPcrDto = new SirPcrDto();
    }
    public LogDto getLogDto() {
        return logDto;
    }

    public void setLogDto(LogDto logDto) {
        this.logDto = logDto;
    }

    public SirPcrDto getSirPcrDto() {
        return sirPcrDto;
    }

    public void setSirPcrDto(SirPcrDto sirpcrDto) {
        this.sirPcrDto = sirpcrDto;
    }
}
