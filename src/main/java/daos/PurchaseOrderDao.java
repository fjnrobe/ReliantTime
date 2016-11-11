package daos;

import DocumentDtoMappers.PurchaseOrderMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import common.CollectionConstants;
import common.FieldConstants;
import common.MonthYear;
import dtos.BaseDto;
import dtos.InvoiceDto;
import dtos.PurchaseOrderDto;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utilities.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Robertson_Laptop on 11/4/2016.
 */
public class PurchaseOrderDao extends BaseDao {

    public PurchaseOrderDao(MongoDatabase reliantDb)
    {

        super(reliantDb, CollectionConstants.PURCHASE_ORDER, new PurchaseOrderMapper());
    }

    public List<PurchaseOrderDto> getAll()
    {
        return super.getList();
    }

    public PurchaseOrderDto getPurchaseOrderByPONumber(String poNumber)
    {
        Bson where = Filters.eq(FieldConstants.PO_NUMBER, poNumber);
        return (PurchaseOrderDto) super.getOne(where);
    }

    public PurchaseOrderDto getPurchaseOrderByMonthYear(MonthYear monthYear)
    {
        Date parmDate = DateTimeUtils.stringToDate(monthYear.getSortKey() + "01", DateTimeUtils.DateFormats.YYYYMMDD);
        Bson where = Filters.and(Filters.lte(FieldConstants.START_DATE, parmDate),
                                 Filters.gte(FieldConstants.END_DATE, parmDate));

        return (PurchaseOrderDto) super.getOne(where);

    }

    public ObjectId addPurchaseOrder (PurchaseOrderDto dto)
    {
        return super.insertOne(dto);
    }

    public void updatePurchaseOrder (PurchaseOrderDto dto)
    {
        List<PurchaseOrderDto> dtos = new ArrayList<PurchaseOrderDto>();
        dtos.add(dto);
        super.updateList(dtos);
    }

    public long deletePurchaseOrder (ObjectId id)
    {
        Bson deleteFilter = Filters.eq(FieldConstants.COLLECTION_ID, id);
        return super.deleteByFilter(deleteFilter);
    }



}
