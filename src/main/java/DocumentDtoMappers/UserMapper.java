package DocumentDtoMappers;

import common.FieldConstants;
import dtos.BaseDto;
import dtos.DeductionDto;
import dtos.UserDto;
import org.bson.Document;

/**
 * Created by Robertson_Laptop on 10/17/2016.
 */
public class UserMapper extends BaseMapper {

    public BaseDto mapFromDocument (Document doc)
    {
        UserDto dto = new UserDto();

        dto.setId(doc.getObjectId(FieldConstants.COLLECTION_ID));
        dto.setSession(doc.getString(FieldConstants.SESSION_ID));
        dto.setLastLoginTime(doc.getLong(FieldConstants.LAST_LOGIN));

        return dto;
    }

    public <T> Document mapToDocument(T dto)
    {
        UserDto userDto = (UserDto) dto;
        Document doc = new Document();

        doc.put(FieldConstants.SESSION_ID,  userDto.getSession());
        doc.put(FieldConstants.LAST_LOGIN, userDto.getLastLoginTime());

        return doc;
    }

}
