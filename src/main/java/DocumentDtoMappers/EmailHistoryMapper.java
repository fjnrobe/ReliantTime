package DocumentDtoMappers;

import common.FieldConstants;
import dtos.BaseDto;
import dtos.EmailMessageDto;
import org.bson.Document;

public class EmailHistoryMapper extends BaseMapper {

    public BaseDto mapFromDocument (Document doc)
    {
        EmailMessageDto dto = new EmailMessageDto();

        dto.setFromEmail(doc.getString(FieldConstants.EMAIL_FROM));
        dto.setToEmail(doc.getString(FieldConstants.EMAIL_TO));
        dto.setSubject(doc.getString(FieldConstants.EMAIL_SUBJECT));
        dto.setBody(doc.getString(FieldConstants.EMAIL_BODY));
        dto.setSendDate(doc.getDate(FieldConstants.EMAIL_SENDDATE));
        dto.setId(doc.getObjectId(FieldConstants.COLLECTION_ID));
        dto.setCreateDate(doc.getDate(FieldConstants.CREATE_DATE));

        return dto;
    }

    public <T> Document mapToDocument(T dto)
    {
        EmailMessageDto emailMessageDto = (EmailMessageDto) dto;
        Document doc = new Document();

        if (emailMessageDto.getId() != null)
        {
            doc.put(FieldConstants.COLLECTION_ID, emailMessageDto.getId());
        }
        doc.put(FieldConstants.EMAIL_FROM, emailMessageDto.getFromEmail());
        doc.put(FieldConstants.EMAIL_TO, emailMessageDto.getToEmail());
        doc.put(FieldConstants.EMAIL_SUBJECT, emailMessageDto.getSubject());
        doc.put(FieldConstants.EMAIL_BODY, emailMessageDto.getBody());
        doc.put(FieldConstants.EMAIL_SENDDATE, emailMessageDto.getSendDate());
        doc.put(FieldConstants.CREATE_DATE, emailMessageDto.getCreateDate());

        return doc;
    }
}
