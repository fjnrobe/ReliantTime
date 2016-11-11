package DocumentDtoMappers;

import dtos.BaseDto;
import dtos.LogDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import utilities.DateTimeUtils;
import utilities.StringUtils;

/**
 * Created by Robertson_Laptop on 6/27/2016.
 */
public class LogMapper extends BaseMapper {

    public BaseDto mapFromDocument(Document doc)
    {
        LogDto log = new LogDto();
        log.setHours(doc.getDouble("hours"));
        log.setId(doc.getObjectId("_id"));
        log.setActivityDesc(doc.getString("activityDesc"));
        log.setBillableInd(doc.getBoolean("billableInd"));
        log.setStartTime(doc.getString("startTime"));
        log.setEndTime(doc.getString("endTime"));
        log.setLegacySirId(doc.getString("legacySirId"));
        log.setLogDate(doc.getString("logDate"));
        log.setNote(doc.getString("note"));
        log.setPrimaveraDesc(doc.getString("primaveraDesc"));
        try {
            log.setSirPcrId(doc.getObjectId("sirPcrId"));
        } catch( Exception e)
        {
            log.setSirPcrId(new ObjectId(doc.getString("sirPcrId")));
        }
        log.setCreateDate(doc.getDate("createDate"));
        log.setUpdateDate(doc.getDate("updateDate"));

        return log;
    }

    public <T> Document mapToDocument(T dto)
    {
        LogDto logDto = (LogDto) dto;
        Document doc = new Document();
        doc.put("hours", logDto.getHours());
        if (logDto.getId() != null) {
            doc.put("_id", logDto.getId());
        }
        doc.put("activityDesc", logDto.getActivityDesc());
        doc.put("billableInd", logDto.getBillableInd());
        doc.put("startTime", logDto.getStartTime());
        doc.put("endTime", logDto.getEndTime());
        doc.put("legacySirId", logDto.getLegacySirId());
        doc.put("logDate", logDto.getLogDate());
        doc.put("note", logDto.getNote());
        doc.put("primaveraDesc", logDto.getPrimaveraDesc());
        doc.put("sirPcrId", logDto.getSirPcrId());
        doc.put("createDate", logDto.getCreateDate());
        doc.put("updateDate", logDto.getUpdateDate());
        return doc;
    }
}
