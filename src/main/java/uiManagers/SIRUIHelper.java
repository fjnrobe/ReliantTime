package uiManagers;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import common.FieldConstants;
import dtos.LogDto;
import dtos.SirPcrDto;
import dtos.SirPcrUIDto;
import managers.SirPcrManager;
import org.bson.types.ObjectId;
import spark.Request;
import utilities.DateTimeUtils;
import utilities.NumberUtils;
import utilities.SortUtils;
import utilities.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robertson_Laptop on 8/20/2016.
 */
public class SIRUIHelper {

    public static SirPcrDto mapSirFromQueryParms(Request request, SirPcrManager manager, boolean includeLog)
    {
        SirPcrDto sir = new SirPcrDto();
        if (!StringUtils.isEmpty(request.queryParams("sirId"))) {
            sir = manager.getSirById(new ObjectId(request.queryParams("sirId")));
        }
        if (!StringUtils.isEmpty(request.queryParams("subProcess"))) {
            sir.setSubProcessDesc(request.queryParams("subProcess"));
        }
        if (!StringUtils.isEmpty(request.queryParams("sirType"))) {
            sir.setSirType(request.queryParams("sirType"));
        }
        if (!StringUtils.isEmpty(request.queryParams("sirNumber"))) {
            sir.setSirPcrNumber(request.queryParams("sirNumber"));
        }
        if (!StringUtils.isEmpty(request.queryParams("nickName"))) {
            sir.setNickName(request.queryParams("nickName"));
        }
        if (!StringUtils.isEmpty(request.queryParams("description"))) {
            sir.setSirDesc(request.queryParams("description"));
        }
        if (!StringUtils.isEmpty(request.queryParams("completed"))) {
            sir.setCompletedInd(StringUtils.stringToBoolean(request.queryParams("completed")));
        }
        else
        {
            sir.setCompletedInd(false);
        }

        if (includeLog) {
            LogDto log = new LogDto();

            if (!StringUtils.isEmpty(request.queryParams("logId"))) {
                log.setId(new ObjectId(request.queryParams("logId")));
                if (request.queryParams("logCreateDate") != null) {
                    log.setCreateDate(DateTimeUtils.stringToDate(
                            request.queryParams("logCreateDate"), DateTimeUtils.DateFormats.ISO));
                }
                if (request.queryParams("logUpdateDate") != null) {
                    log.setUpdateDate(DateTimeUtils.stringToDate(
                            request.queryParams("logUpdateDate"), DateTimeUtils.DateFormats.ISO));
                }
            }
            log.setSirPcrId(sir.getId());
            log.setLogDate(request.queryParams("date"));
            log.setStartTime(request.queryParams("startTime"));
            log.setEndTime(request.queryParams("endTime"));
            log.setNote(request.queryParams("note"));
            log.setPrimaveraDesc(request.queryParams("primaVeraActivity"));
            log.setActivityDesc(request.queryParams("activity"));
            log.setBillableInd(StringUtils.stringToBoolean(request.queryParams("billable")));

            sir.getLogs().add(log);
        }
        return sir;
    }

    public static HashMap<String, Object> mapSirToQueryParams(SirPcrDto sir)
    {
        HashMap<String, Object> root = new HashMap<String, Object>();

        root.put("sirId", sir.getId());
        root.put("subProcess", sir.getSubProcessDesc());
        root.put("sirType", sir.getSirType());
        root.put("sirNumber", sir.getSirPcrNumber());
        root.put("nickName", sir.getNickName());
        root.put("description", sir.getSirDesc());
        root.put("completed", sir.getCompletedInd());
        root.put("sirCreateDate", DateTimeUtils.DateToString(sir.getCreateDate(), DateTimeUtils.DateFormats.ISO, false));
        root.put("sirUpdateDate", DateTimeUtils.DateToString(sir.getUpdateDate(), DateTimeUtils.DateFormats.ISO, false));

        if (sir.getLogs().size() >= 1) {
            LogDto log = sir.getLogs().get(0);
            root.put("logId", log.getId());
            root.put("logDate", log.getLogDate());
            root.put("date", log.getLogDate());
            root.put("startTime", log.getStartTime());
            root.put("endTime", log.getEndTime());
            root.put("note", log.getNote());
            root.put("primaVeraActivity", log.getPrimaveraDesc());
            root.put("activity", log.getActivityDesc());
            root.put("billable", log.getBillableInd());
            root.put("logCreateDate", DateTimeUtils.DateToString(log.getCreateDate(), DateTimeUtils.DateFormats.ISO, false));
            root.put("logUpdateDate",  DateTimeUtils.DateToString(log.getUpdateDate(), DateTimeUtils.DateFormats.ISO, false));
        }
        double totHours = 0.0;
        for (LogDto dto : sir.getLogs())
        {
            totHours += dto.getHours();
        }

        root.put("totHours", NumberUtils.roundHours(totHours));
        if (sir.getLogs().size() >= 1)
        {

            root.put("logList", SortUtils.sortLogs(sir.getLogs(),false));
        }

        return root;
    }

    public static HashMap<String, Object> mapSirPcrDtoToUiDto(List<SirPcrDto> dtos)
    {
        HashMap<String, Object> root = new HashMap<String, Object>();

        List<SirPcrUIDto> results = new ArrayList<SirPcrUIDto>();
        for (SirPcrDto dto : dtos)
        {
            SirPcrUIDto uiDto = new SirPcrUIDto();
            uiDto.setSirPcrDto(dto);
            uiDto.setAction("");

            results.add(uiDto);
        }

        root.put("sirList", results);
        root.put("sirCount", results.size());
        return root;
    }

    public static List<String> mapSirPcrUiDtoToDto(Request request)
    {
        List<String> sirList = new ArrayList<String>();

        int sirCount =  Integer.parseInt(request.queryParams("sirCount"));

        String parmKey = "sirId";

        for (int x = 1; x <= sirCount; x++)
        {
            parmKey = "sirId" + x;

            String checkValue = request.queryParams(parmKey);
            if (!StringUtils.isEmpty(checkValue))
            {
                sirList.add(checkValue);
            }
        }

        return sirList;

    }
}
