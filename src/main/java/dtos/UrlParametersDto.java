package dtos;

import utilities.JSONUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robertson_Laptop on 11/21/2016.
 */
public class UrlParametersDto {

    private String priorPage;
    private HashMap<String, String> pageParms;

    public String getPriorPage() {
        return priorPage;
    }

    public void setPriorPage(String priorPage) {
        this.priorPage = priorPage;
    }

    public HashMap<String, String> getPageParms() {
        return pageParms;
    }

    public void setPageParms(HashMap<String, String> pageParms) {
        this.pageParms = pageParms;
    }

    public String toJSON()
    {
        return JSONUtils.convertToJSON(this);
    }
}
