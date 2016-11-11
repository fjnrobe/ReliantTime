package dtos;

/**
 * Created by Robertson_Laptop on 8/8/2016.
 */
public class ErrorDto {

    private String errorMessage;

    public ErrorDto()
    {

    }

    public ErrorDto(String errMsg)
    {
        this.errorMessage = errMsg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


}
