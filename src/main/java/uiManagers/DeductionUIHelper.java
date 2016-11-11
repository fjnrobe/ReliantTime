package uiManagers;

import common.LOVEnum;
import dtos.*;
import managers.LovManager;
import org.bson.types.ObjectId;
import spark.Request;
import utilities.SortUtils;
import utilities.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robertson_Laptop on 10/18/2016.
 */
public class DeductionUIHelper {

    public static List<DeductionUiDto> mapDeductionUiDtos(LovManager lovManager, List<DeductionDto> dtos)
    {
        List<DeductionUiDto> retList = new ArrayList<DeductionUiDto>();

        for (DeductionDto dto : dtos)
        {
            DeductionUiDto uiDto = new DeductionUiDto();
            uiDto.setDeductionDto(dto);

            List<LovBaseDto> categoryList = lovManager.getLovEntries(LOVEnum.DEDUCTION_CATEGORY);
            List<LovBaseDto> typeList = lovManager.getLovEntries(LOVEnum.DEDUCTION_TYPE);

            for (LovBaseDto lovDto : categoryList)
            {
                DeductionCategory deductionCategory = (DeductionCategory) lovDto;

                if (dto.getDeductionCategory().equals(deductionCategory.getDeductionCode()))
                {
                    uiDto.setDeductionCategoryDesc(deductionCategory.getLovDescription());
                    break;
                }
            }

            for (LovBaseDto lovDto : typeList)
            {
                DeductionTypeDto deductionTypeDto = (DeductionTypeDto) lovDto;

                if (dto.getDeductionType().equals(deductionTypeDto.getDeductionTypeCode()))
                {
                    uiDto.setDeductionTypeDesc(deductionTypeDto.getLovDescription());
                }
            }

            retList.add(uiDto);
        }

        SortUtils.sortDeductionUiDto(retList, false);

        return retList;
    }

    public static List<DeductionUiDto> createDeductionSummary(LovManager lovManager, List<DeductionDto> dtos)
    {
        //the key will be category desc/type/year of the posting
        HashMap<String, DeductionUiDto> map = new HashMap<String, DeductionUiDto>();
        List<DeductionUiDto> retList = new ArrayList<DeductionUiDto>();

        List<LovBaseDto> categoryList = lovManager.getLovEntries(LOVEnum.DEDUCTION_CATEGORY);
        List<LovBaseDto> typeList = lovManager.getLovEntries(LOVEnum.DEDUCTION_TYPE);

        SimpleDateFormat simpleDateformatYYYY =new SimpleDateFormat("yyyy");
        SimpleDateFormat simpleDateformatYYYYMMDD =new SimpleDateFormat("yyyyMMdd");

        for (DeductionDto dto : dtos) {

            String key = dto.getDeductionCategory() +
                    dto.getDeductionType() +
                    simpleDateformatYYYY.format(dto.getPostDate());

            DeductionUiDto entry = null;
            if (map.containsKey(key)) {
                entry = map.get(key);
            } else {
                entry = new DeductionUiDto();

                for (LovBaseDto lovDto : categoryList) {
                    DeductionCategory deductionCategory = (DeductionCategory) lovDto;

                    if (dto.getDeductionCategory().equals(deductionCategory.getDeductionCode())) {
                        entry.setDeductionCategoryDesc(deductionCategory.getLovDescription());
                        break;
                    }
                }

                for (LovBaseDto lovDto : typeList) {
                    DeductionTypeDto deductionTypeDto = (DeductionTypeDto) lovDto;

                    if (dto.getDeductionType().equals(deductionTypeDto.getDeductionTypeCode())) {
                        entry.setDeductionTypeDesc(deductionTypeDto.getLovDescription());
                    }
                }

                int year = Integer.parseInt(simpleDateformatYYYY.format(dto.getPostDate()));

                try {
                    entry.getDeductionDto().setPostDate(simpleDateformatYYYYMMDD.parse(year + "0101"));
                } catch (Exception e)
                {
                   //
                }

                entry.getDeductionDto().setAmount(0.0);
                map.put(key, entry);
            }

            entry.getDeductionDto().setAmount(entry.getDeductionDto().getAmount() + dto.getAmount());

        }

        for (String key : map.keySet())
        {
            retList.add(map.get(key));
        }

        SortUtils.sortDeductionUiDto(retList, false);

        return retList;
    }

    public static DeductionDto mapDeductionFromQueryParams(Request request)
    {
        DeductionDto dto = new DeductionDto();
        if (!StringUtils.isEmpty(request.queryParams("deductionId")))
        {
            dto.setId(new ObjectId(request.queryParams("deductionId")));
        }
        else if (!StringUtils.isEmpty(request.queryParams("delDeductionId")))
        {
            dto.setId(new ObjectId(request.queryParams("delDeductionId")));
        }

        dto.setDeductionCategory( request.queryParams("deductionCategoryCode"));
        dto.setDeductionType(request.queryParams("deductionCategoryType"));
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dto.setPostDate(df.parse(request.queryParams("postDate")));
        } catch (Exception e)
        {
            dto.setPostDate(new Date());
        }
        try {
            dto.setAmount(Double.parseDouble(request.queryParams("amount")));
        } catch (Exception e)
        {
            dto.setAmount(0);
        }
        dto.setNote(request.queryParams("note"));

        return dto;
    }
}
