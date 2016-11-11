package DocumentDtoMappers;

import common.FieldConstants;
import dtos.BaseDto;
import dtos.PurchaseOrderDto;
import org.bson.Document;
import utilities.StringUtils;

/**
 * Created by Robertson_Laptop on 11/4/2016.
 */
public class PurchaseOrderMapper extends BaseMapper{


    public BaseDto mapFromDocument(Document doc) {

        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(doc.getObjectId(FieldConstants.COLLECTION_ID));
        dto.setCreateDate(doc.getDate(FieldConstants.CREATE_DATE));
        dto.setUpdateDate(doc.getDate(FieldConstants.UPDATE_DATE));
        dto.setHourlyRate(doc.getDouble(FieldConstants.HOURLY_RATE));
        dto.setPoNumber(doc.getString(FieldConstants.PO_NUMBER));
        dto.setStartDate(doc.getDate(FieldConstants.START_DATE));
        dto.setEndDate(doc.getDate(FieldConstants.END_DATE));
        dto.setPassthruRate(doc.getDouble(FieldConstants.PASSTHRU_RATE));
        dto.setPoTitle(doc.getString(FieldConstants.PO_TITLE));
        dto.setTotalHours(doc.getDouble(FieldConstants.TOTAL_HOURS));

        return dto;
    }

    public <T> Document mapToDocument(T dto) {

        PurchaseOrderDto invoiceDto = (PurchaseOrderDto) dto;
        Document doc = new Document();
        if (invoiceDto.getId() != null) {
            doc.put(FieldConstants.COLLECTION_ID, invoiceDto.getId());
        }
        doc.put(FieldConstants.HOURLY_RATE, invoiceDto.getHourlyRate());
        doc.put(FieldConstants.PO_NUMBER, invoiceDto.getPoNumber());

        doc.put(FieldConstants.PASSTHRU_RATE, invoiceDto.getPassthruRate());
        doc.put(FieldConstants.PO_TITLE, invoiceDto.getPoTitle());
        doc.put(FieldConstants.TOTAL_HOURS, invoiceDto.getTotalHours());
        doc.put(FieldConstants.START_DATE, invoiceDto.getStartDate());
        doc.put(FieldConstants.END_DATE, invoiceDto.getEndDate());

        return doc;
    }
}
