package daos;

import DocumentDtoMappers.DeductionMapper;
import DocumentDtoMappers.LOVMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import common.CollectionConstants;
import common.FieldConstants;
import common.LOVEnum;
import dtos.BaseDto;
import dtos.DeductionDto;
import dtos.LovBaseDto;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utilities.DateTimeUtils;
import utilities.StringUtils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Robertson_Laptop on 10/16/2016.
 */
public class DeductionDao extends BaseDao {

    public DeductionDao(MongoDatabase reliantDb)
    {

        super(reliantDb, CollectionConstants.DEDUCTIONS, new DeductionMapper());
    }


    public List<DeductionDto> getDeductionsByCriteria(String deductionCategory,
                                                      String deductionType,
                                                      String startDate,
                                                      String endDate)
    {
        Date sDate = null;
        Date eDate = null;
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            sDate =  df.parse(startDate);
        } catch (Exception e)

        {
            sDate = null;
        }
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            eDate =  df.parse(endDate);
        } catch (Exception e)

        {
            eDate = null;
        }

        Bson searchFilter = null;
        Bson categoryFilter = Filters.eq(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, deductionCategory);
        Bson typeFilter = Filters.eq(FieldConstants.DEDUCTION_TYPE_CODE, deductionType);
        Bson startFilter = Filters.gte(FieldConstants.POST_DATE, sDate);
        Bson endFilter = Filters.lte(FieldConstants.POST_DATE, eDate);

        if (!StringUtils.isEmpty(deductionCategory))
        {
            searchFilter = categoryFilter;
        }
        if (!StringUtils.isEmpty(deductionType))
        {
            if (searchFilter != null)
            {
                searchFilter = Filters.and(searchFilter, typeFilter );
            }
            else
            {
                searchFilter = typeFilter;
            }
        }
        if (sDate != null)
        {
            if (searchFilter != null)
            {
                searchFilter = Filters.and(searchFilter, startFilter );
            }
            else
            {
                searchFilter = startFilter;
            }
        }
        if (eDate != null)
        {
            if (searchFilter != null)
            {
                searchFilter = Filters.and(searchFilter, endFilter );
            }
            else
            {
                searchFilter = endFilter;
            }
        }

        if (searchFilter != null) {
            return super.getList(searchFilter);
        }
        else {
            return super.getList();
        }

    }

    public List<DeductionDto> getDeductionsByCategory(String deductionCategory)
    {
        return super.getList(new Document(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, deductionCategory));
    }

    public DeductionDto getDeductionById(ObjectId id)
    {
        Bson filter = Filters.eq(FieldConstants.COLLECTION_ID, id);

        return (DeductionDto) super.getOne(filter);
    }

    public ObjectId addDeduction(DeductionDto dto)
    {
        return super.insertOne(dto);
    }

    public void updateDeduction(DeductionDto dto)
    {
        List<BaseDto> dtos = new ArrayList<BaseDto>();
        dtos.add(dto);

        super.updateList(dtos);
    }

    public long deleteDeduction(ObjectId key)
    {
        Bson filter = Filters.eq(FieldConstants.COLLECTION_ID, key);
        return super.deleteByFilter(filter);
    }
}
