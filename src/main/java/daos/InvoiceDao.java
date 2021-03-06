package daos;

import DocumentDtoMappers.InvoiceMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import common.CollectionConstants;
import common.FieldConstants;
import dtos.BaseDto;
import dtos.FinancialSearchCriteriaDto;
import dtos.InvoiceDto;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robertson_Laptop on 10/25/2016.
 */
public class InvoiceDao extends BaseDao{

    public InvoiceDao(MongoDatabase reliantDb)
    {

        super(reliantDb, CollectionConstants.INVOICES, new InvoiceMapper());
    }

    public List<InvoiceDto> getAllInvoices()
    {
        return super.getList();
    }

    public List<InvoiceDto> getInvoicesByCriteria(FinancialSearchCriteriaDto searchDto)
    {
        List<InvoiceDto> invoiceDtos = new ArrayList<InvoiceDto>();
        Bson whereFilter = null;
        Bson poFilter = null;
        Bson invoiceFilter = null;
        Bson yearFilter = null;
        if (!StringUtils.isEmpty(searchDto.getPoNumber()))
        {
            poFilter = Filters.eq(FieldConstants.PO_NUMBER, searchDto.getPoNumber());
        }

        if (!StringUtils.isEmpty(searchDto.getInvoiceNumber()))
        {
            invoiceFilter = Filters.eq(FieldConstants.INVOICE_NUMBER, searchDto.getInvoiceNumber());
        }

        if (!StringUtils.isEmpty(searchDto.getYear()))
        {
            yearFilter = Filters.regex( FieldConstants.MONTH_YEAR, "^" + searchDto.getYear());

        }

        if (poFilter != null)
        {
            whereFilter = poFilter;
        }
        if (invoiceFilter != null)
        {
            if (whereFilter == null)
            {
                whereFilter = invoiceFilter;
            }
            else
            {
                whereFilter = Filters.and(whereFilter, invoiceFilter);
            }
        }
        if (yearFilter != null)
        {
            if (whereFilter== null)
            {
                whereFilter = yearFilter;
            }
            else
            {
                whereFilter = Filters.and(whereFilter, yearFilter);
            }
        }

        if (whereFilter != null) {
            invoiceDtos =super.getList(whereFilter);
        }

        return invoiceDtos;
    }

    public InvoiceDto getInvoiceById(ObjectId id)
    {
        Bson filter = Filters.eq(FieldConstants.COLLECTION_ID, id);

        return (InvoiceDto) super.getOne(filter);
    }

    public ObjectId addInvoice(InvoiceDto dto)
    {
        return super.insertOne(dto);
    }

    public void updateInvoice(InvoiceDto dto)
    {
        List<BaseDto> dtos = new ArrayList<BaseDto>();
        dtos.add(dto);

        super.updateList(dtos);
    }

    public long deleteInvoice(ObjectId key)
    {
        Bson filter = Filters.eq(FieldConstants.COLLECTION_ID, key);
        return super.deleteByFilter(filter);
    }
}
