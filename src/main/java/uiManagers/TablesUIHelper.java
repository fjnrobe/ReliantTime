package uiManagers;

import common.LOVEnum;
import dtos.LovBaseDto;
import dtos.LovCodeUIDto;
import managers.LovManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robertson_Laptop on 9/6/2016.
 */
public class TablesUIHelper {

    public static List<LovCodeUIDto> createLovCodeUiDtos(List<LovBaseDto> dtos, LovManager manager)
    {
        List<LovCodeUIDto> lovCodeUIDtos = new ArrayList<LovCodeUIDto>();
        for (LovBaseDto dto : dtos)
        {
            //we need to tag each lov entry with a flag indicating whether the
            //entry can be deleted
            LovCodeUIDto newUiDto = new LovCodeUIDto();
            newUiDto.setLovBaseDto(dto);
            newUiDto.setBeingEdited(false);
            newUiDto.setCanBeDeleted(!manager.isInUse(LOVEnum.fromString(dto.getLovCode()), dto.getLovDescription()));

            lovCodeUIDtos.add(newUiDto);
        }

        return lovCodeUIDtos;
    }
}
