package DocumentDtoMappers;

import common.FieldConstants;
import common.MonthYear;
import dtos.BaseDto;
import dtos.InvoiceDto;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.bson.Document;

import java.lang.reflect.Field;

/**
 * Created by Robertson_Laptop on 10/25/2016.
 */
public class InvoiceMapper extends BaseMapper {

    public BaseDto mapFromDocument (Document doc)
    {
        InvoiceDto dto = new InvoiceDto();

        dto.setId(doc.getObjectId(FieldConstants.COLLECTION_ID));
        dto.setMonthYear(new MonthYear(doc.getString(FieldConstants.MONTH_YEAR)));
        dto.setHours(doc.getDouble(FieldConstants.HOURS));
        dto.setTotalGross(doc.getDouble(FieldConstants.TOTAL_GROSS));
        dto.setInvoiceDate(doc.getDate(FieldConstants.INVOICE_DATE));
        dto.setReceivedDate(doc.getDate(FieldConstants.RECEIVED_DATE));
        dto.setCreateDate(doc.getDate(FieldConstants.CREATE_DATE));
        dto.setUpdateDate(doc.getDate(FieldConstants.UPDATE_DATE));
        dto.setPriorHoursRemaining(doc.getDouble(FieldConstants.PRIOR_HOURS_REMAINING));
        dto.setPriorAmtRemaining(doc.getDouble(FieldConstants.PRIOR_AMT_REMAINING));
        dto.setHoursRemaining(doc.getDouble(FieldConstants.HOURS_REMAINING));
        dto.setAmtRemaining(doc.getDouble(FieldConstants.AMT_REMAINING));
        dto.setPoNumber(doc.getString(FieldConstants.PO_NUMBER));
        dto.setInvoiceNumber(doc.getString(FieldConstants.INVOICE_NUMBER));

        return dto;
    }

    public <T> Document mapToDocument(T documentDto)
    {
        InvoiceDto dto = (InvoiceDto) documentDto;
        Document doc = new Document();

        if (dto.getId() != null)
        {
            doc.put(FieldConstants.COLLECTION_ID, dto.getId());
        }
        doc.put(FieldConstants.MONTH_YEAR, dto.getMonthYear().getSortKey());
        doc.put(FieldConstants.HOURS, dto.getHours());
        doc.put(FieldConstants.TOTAL_GROSS, dto.getTotalGross());
        doc.put(FieldConstants.INVOICE_DATE, dto.getInvoiceDate());
        doc.put(FieldConstants.RECEIVED_DATE, dto.getReceivedDate());
        doc.put(FieldConstants.PO_NUMBER, dto.getPoNumber());
        doc.put(FieldConstants.INVOICE_NUMBER, dto.getInvoiceNumber());
        doc.put(FieldConstants.PRIOR_HOURS_REMAINING, dto.getPriorHoursRemaining());
        doc.put(FieldConstants.PRIOR_AMT_REMAINING, dto.getPriorAmtRemaining());
        doc.put(FieldConstants.HOURS_REMAINING, dto.getHoursRemaining());
        doc.put(FieldConstants.AMT_REMAINING, dto.getAmtRemaining());

        return doc;
    }

}
