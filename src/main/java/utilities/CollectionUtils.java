package utilities;

import dtos.BaseDto;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    public static <T> List<BaseDto> upCast(List<T> dtos)
    {
        List<BaseDto> newDtos = new ArrayList<BaseDto>();

        for (T dto : dtos)
        {
            newDtos.add((BaseDto) dto);
        }

        return newDtos;
    }
}
