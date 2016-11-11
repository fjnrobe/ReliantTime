package DocumentDtoMappers;

import common.FieldConstants;
import common.LOVEnum;
import dtos.*;
import org.bson.Document;

/**
 * Created by Robertson_Laptop on 10/16/2016.
 */
public class DeductionMapper extends BaseMapper {

    public BaseDto mapFromDocument (Document doc)
    {
        DeductionDto dto = new DeductionDto();

        dto.setNote(doc.getString(FieldConstants.NOTE));
        dto.setAmount(doc.getDouble(FieldConstants.DEDUCTION_AMT));
        dto.setDeductionCategory(doc.getString(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE));
        dto.setDeductionType(doc.getString(FieldConstants.DEDUCTION_TYPE_CODE));
        dto.setPostDate(doc.getDate(FieldConstants.POST_DATE));
        dto.setId(doc.getObjectId(FieldConstants.COLLECTION_ID));

        return dto;
    }

    public <T> Document mapToDocument(T dto)
    {
        DeductionDto deductionDto = (DeductionDto) dto;
        Document doc = new Document();

        if (deductionDto.getId() != null)
        {
            doc.put(FieldConstants.COLLECTION_ID, deductionDto.getId());
        }
        doc.put(FieldConstants.NOTE, deductionDto.getNote());
        doc.put(FieldConstants.DEDUCTION_AMT, deductionDto.getAmount());
        doc.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, deductionDto.getDeductionCategory());
        doc.put(FieldConstants.DEDUCTION_TYPE_CODE, deductionDto.getDeductionType());
        doc.put(FieldConstants.POST_DATE, deductionDto.getPostDate());

        return doc;
    }

}
