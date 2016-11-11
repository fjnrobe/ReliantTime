package daos;

import DocumentDtoMappers.LOVMapper;
import DocumentDtoMappers.LogMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import common.CollectionConstants;
import common.FieldConstants;
import common.LOVEnum;
import dtos.BaseDto;
import dtos.LovBaseDto;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public class LovDao  extends BaseDao {

    public LovDao(MongoDatabase reliantDb)
    {

        super(reliantDb,CollectionConstants.LOV, new LOVMapper());
    }

    //return a unique list of all the lov entries
    public List<String> getLovList()
    {
        List<String> lovCodes = super.getDistinct(FieldConstants.LOV_CODE);

        Collections.sort(lovCodes);

        return lovCodes;

    }

    public List<LovBaseDto> getLovEntries(LOVEnum lovCode)
    {
        return super.getList(new Document(FieldConstants.LOV_CODE, lovCode.getValue()));
    }

    public List<LovBaseDto> getLovEntriesByDeductionCode(String deductionCode)
    {
        Bson filter = Filters.and(new Document(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, deductionCode),
                                  new Document(FieldConstants.LOV_CODE, "deductionType"));
        return super.getList(filter);
    }

    public Long getUsageCount(String columnName, String columnValue)
    {
        return super.getCount(columnName, columnValue);
    }

    public void addLovEntry(LOVEnum lovCode, String newDesc)
    {
        Document newLov = new Document();
        newLov.put(FieldConstants.LOV_CODE, lovCode.getValue());
        newLov.put(FieldConstants.LOV_DESC, newDesc);

        switch (lovCode)
        {
            case DEDUCTION_CATEGORY:
                newLov.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, "");
                break;
            case DEDUCTION_TYPE:
                newLov.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, "");
                newLov.put(FieldConstants.DEDUCTION_TYPE_CODE, "");
                newLov.put(FieldConstants.DEDUCTION_GROSS_DED_IND, "");
                break;

        }
        this.getCollection().insertOne(newLov);
    }

    public long updateLovEntry(LOVEnum lovCode, String oldDesc, String newDesc)
    {
        Bson filter = Filters.and(Filters.eq(FieldConstants.LOV_CODE, lovCode.getValue()),
                                  Filters.eq(FieldConstants.LOV_DESC, oldDesc));

        Bson update = Updates.set(FieldConstants.LOV_DESC, newDesc);

        return this.getCollection().updateOne(filter, update).getModifiedCount();

    }

    public void deleteLovEntry(LOVEnum lovCode, String oldDesc)
    {
        Bson filter = Filters.and(Filters.eq(FieldConstants.LOV_CODE, lovCode.getValue()),
                Filters.eq(FieldConstants.LOV_DESC, oldDesc));


        this.getCollection().deleteOne(filter);
    }
}
