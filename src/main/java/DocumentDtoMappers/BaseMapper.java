package DocumentDtoMappers;

import dtos.BaseDto;
import dtos.LogDto;
import org.bson.Document;

/**
 * Created by Robertson_Laptop on 8/24/2016.
 */
public abstract class BaseMapper {

    public abstract BaseDto mapFromDocument(Document doc);

    public abstract <T> Document mapToDocument(T dto);
}
