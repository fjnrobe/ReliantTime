package daos;

import DocumentDtoMappers.BaseMapper;
import DocumentDtoMappers.SirPcrMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import common.CollectionConstants;
import common.FieldConstants;
import common.LOVEnum;
import dtos.SirPcrDto;
import enums.SIR_Status;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utilities.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Robertson_Laptop on 6/4/2016.
 */
public class SIRPCRDao extends BaseDao{


    public SIRPCRDao(final MongoDatabase reliantDb) {

        super(reliantDb,CollectionConstants.SIR_PCR, new SirPcrMapper());

    }

    public ObjectId insert(SirPcrDto dto)
    {
        Document document = this.getMapper().mapToDocument(dto);
        document.put(FieldConstants.CREATE_DATE, new Date());
        document.put(FieldConstants.UPDATE_DATE, null);
        this.getCollection().insertOne(document);

        return document.getObjectId("_id");
    }

    public void update(SirPcrDto dto)
    {
        Document document = this.getMapper().mapToDocument(dto);
        document.put(FieldConstants.UPDATE_DATE, new Date());
        Bson filter = Filters.eq(FieldConstants.COLLECTION_ID, dto.getId());
        this.getCollection().replaceOne(filter, document);

    }

    public List<SirPcrDto> getSirsByCriteria(Boolean completedInd, String sirType, String sirNumber)
    {
        Bson filter = null;
        if (StringUtils.isEmpty(sirNumber)) {
            if (completedInd != null) {
                filter = Filters.eq(FieldConstants.COMPLETED_IND, completedInd.booleanValue());
            }
            if (!sirType.equalsIgnoreCase("All")) {
                if (filter == null)
                {
                    filter = Filters.eq(FieldConstants.SIR_TYPE, sirType);
                }
                else {
                    filter = Filters.and(filter, Filters.eq(FieldConstants.SIR_TYPE, sirType));
                }
            }
        }
        else
        {
            filter = Filters.eq(FieldConstants.SIR_PCR_NUMBER, sirNumber);
        }

        if (filter != null) {
            return super.getList(filter);
        }
        else {
            return super.getList();
        }

    }

    public String getNextSirIdForOtherSirType()
    {
        int nextSirPcrNumber = 0;

        Bson filter = Filters.lt(FieldConstants.SIR_PCR_NUMBER, "0");
        Bson orderBy = Sorts.descending(FieldConstants.SIR_PCR_NUMBER);
        int limitBy = 1;

        List<SirPcrDto> items = super.getList(filter, orderBy, limitBy);
        for (SirPcrDto item : items)
        {
            nextSirPcrNumber = Integer.valueOf(item.getSirPcrNumber()).intValue() - 1;
            break;
        }

        return String.valueOf(nextSirPcrNumber);

    }

    public List<SirPcrDto> getAllSIRs() {

        return  this.getList();

    }

    public SirPcrDto getSirById(ObjectId id) {

        return (SirPcrDto) super.getOne(new Document(FieldConstants.COLLECTION_ID, id));
    }

    public SirPcrDto getSirByLegacyId(String id) {

        return (SirPcrDto) super.getOne(new Document(FieldConstants.LEGACY_SIR_ID, id));
    }

    public SirPcrDto getSirByNumber(String sirNumber) {

        return (SirPcrDto) super.getOne(new Document(FieldConstants.SIR_PCR_NUMBER, sirNumber));
    }

    public SirPcrDto getSirByNickName(String nickName)
    {
        return (SirPcrDto) super.getOne(new Document(FieldConstants.NICKNAME, nickName));
    }

    public List<SirPcrDto> getSirsAndLogsByDateRange(Date fromDate, Date toDate)
    {
        return null;
    }

    //get sirs that are active 'N',
    public List<SirPcrDto> getSirsByStatus(SIR_Status sirStatus) {

        List<SirPcrDto> sirs = null;
        if (sirStatus.equals(SIR_Status.All))
        {
            sirs = this.getAllSIRs();
        }
        else
        {
            //default to completed = false;
            boolean status = false;
            if (SIR_Status.COMPLETED.equals(sirStatus))
            {
                status = true;
            }
            Bson filter = Filters.eq(FieldConstants.COMPLETED_IND, status);
            sirs = super.getList(filter);
        }

        return sirs;
    }

    public long deleteBySirId(ObjectId logId)
    {
        Bson deleteFilter = Filters.eq(FieldConstants.COLLECTION_ID, logId);
        return super.deleteByFilter(deleteFilter);
    }

    public Long getUsageCount(String columnName, String columnValue)
    {
        return super.getCount(columnName, columnValue);
    }

    public long updateLovValue(LOVEnum lovEnum, String oldValue, String newValue)
    {
        Bson filter = null;
        Bson update = null;
        switch (lovEnum)
        {
            case SUB_PROCESS_TYPE:
                filter = Filters.eq(FieldConstants.SUBPROCESS_DESC, oldValue);
                update = Updates.set(FieldConstants.SUBPROCESS_DESC, newValue);
                break;
        }

        return this.getCollection().updateMany(filter, update).getModifiedCount();

    }
}
