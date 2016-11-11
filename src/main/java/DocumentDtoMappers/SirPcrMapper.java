package DocumentDtoMappers;

import dtos.BaseDto;
import dtos.SirPcrDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import utilities.DateTimeUtils;
import utilities.StringUtils;

import java.sql.Date;

/**
 * this class contains static methods for mapping the document representation
 * of the Sir and Log data to the Dto version.
 */
public class SirPcrMapper extends BaseMapper {

    public BaseDto mapFromDocument (Document doc)
    {
        SirPcrDto sir = new SirPcrDto();
        sir.setId(doc.getObjectId("_id"));
        sir.setCompletedInd(doc.getBoolean("CompletedInd"));
        sir.setLegacyId(doc.getString("legacySirId"));
        sir.setNickName(doc.getString("NickName"));
        sir.setSirDesc(doc.getString("sirDesc"));
        sir.setSirPcrNumber(doc.getString("sirPcrNumber"));
        sir.setSirType(doc.getString("sirType"));
        sir.setSubProcessDesc(doc.getString("subprocessDesc"));
        sir.setCreateDate(doc.getDate("createDate"));
        if (doc.getDate("updateDate") != null) {
            sir.setUpdateDate(doc.getDate("updateDate"));
        }
        return sir;
    }

    public <T> Document mapToDocument(T sirDto)
    {
        SirPcrDto sir = (SirPcrDto) sirDto;
        Document doc = new Document();
        if (sir.getId() != null) {
            doc.put("_id", sir.getId());
        }
        doc.put("CompletedInd", sir.getCompletedInd());
        doc.put("legacySirId", sir.getLegacyId());
        doc.put("NickName", sir.getNickName());
        doc.put("sirDesc", sir.getSirDesc());
        doc.put("sirPcrNumber", sir.getSirPcrNumber());
        doc.put("sirType", sir.getSirType());
        doc.put("subprocessDesc", sir.getSubProcessDesc());
        doc.put("createDate", sir.getCreateDate());
        doc.put("updateDate", sir.getUpdateDate());

        return doc;
    }

}
