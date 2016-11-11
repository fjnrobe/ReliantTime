package DocumentDtoMappers;

import common.FieldConstants;
import common.LOVEnum;
import dtos.*;
import org.bson.Document;

/**
 * Created by Robertson_Laptop on 9/2/2016.
 */
public class LOVMapper extends BaseMapper {

    public BaseDto mapFromDocument (Document doc)
    {
        LovBaseDto lov = null;

        LOVEnum lovEnum = LOVEnum.fromString(doc.getString(FieldConstants.LOV_CODE));
        switch (lovEnum)
        {
            case DEDUCTION_CATEGORY:
                lov = new DeductionCategory(doc.getString(FieldConstants.LOV_CODE),
                    doc.getString(FieldConstants.LOV_DESC),
                    doc.getString(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE));

                break;
            case DEDUCTION_TYPE:

                    lov = new DeductionTypeDto(doc.getString(FieldConstants.LOV_CODE),
                            doc.getString(FieldConstants.LOV_DESC),
                            doc.getString(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE),
                            doc.getString(FieldConstants.DEDUCTION_TYPE_CODE),
                            doc.getString(FieldConstants.DEDUCTION_GROSS_DED_IND));

                break;
            case PRIMAVERA_TYPE:

                lov = new PrimaveraType(doc.getString(FieldConstants.LOV_CODE),
                            doc.getString(FieldConstants.LOV_DESC),
                            doc.getString(FieldConstants.PRIMAVERA_CODE));

                break;
            case SUB_PROCESS_TYPE:
            case ACTIVITY_TYPE:

                lov = new SubProcessType(doc.getString(FieldConstants.LOV_CODE),
                            doc.getString(FieldConstants.LOV_DESC));
                break;
        }
        return lov;
    }

    public <T> Document mapToDocument(T lovBaseDto)
    {
        LovBaseDto lov = (LovBaseDto) lovBaseDto;
        Document doc = new Document();
        doc.put("lovCode", lov.getLovCode());
        doc.put("lovDesc", lov.getLovDescription());
        doc.put("createDate", lov.getCreateDate());
        doc.put("updateDate", lov.getUpdateDate());

        LOVEnum lovEnum = LOVEnum.fromString(lov.getLovCode());
        switch (lovEnum)
        {
            case DEDUCTION_CATEGORY:
                DeductionCategory dedLov = (DeductionCategory) lov;
                doc.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, dedLov.getDeductionCode());

                break;
            case DEDUCTION_TYPE:
                DeductionTypeDto dedType = (DeductionTypeDto) lov;
                doc.put(FieldConstants.LOV_DEDUCTION_CATEGORY_CODE, dedType.getDeductionCode());
                doc.put(FieldConstants.DEDUCTION_TYPE_CODE, dedType.getDeductionTypeCode());
                doc.put(FieldConstants.DEDUCTION_GROSS_DED_IND, dedType.getGrossDedInd());

                break;
            case PRIMAVERA_TYPE:
                PrimaveraType primaType = (PrimaveraType) lov;
                doc.put(FieldConstants.PRIMAVERA_CODE, ((PrimaveraType) lov).getPrimaveraCode());
                break;
        }

        return doc;
    }
}
