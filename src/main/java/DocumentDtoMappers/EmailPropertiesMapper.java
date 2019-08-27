package DocumentDtoMappers;

import common.FieldConstants;
import dtos.BaseDto;
import dtos.EmailPropertiesDto;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.bson.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Robertson_Laptop on 11/29/2016.
 */
public class EmailPropertiesMapper extends BaseMapper {


    public BaseDto mapFromDocument(Document doc) {

        EmailPropertiesDto dto = new EmailPropertiesDto();
        dto.setId(doc.getObjectId(FieldConstants.COLLECTION_ID));
        dto.setSmtpAuth(doc.getString(FieldConstants.SMTP_AUTH));
        dto.setSmtpHost(doc.getString(FieldConstants.SMTP_HOST));
        dto.setSmtpPort(doc.getString(FieldConstants.SMTP_PORT));
        dto.setSourceEmail(doc.getString(FieldConstants.SOURCE_EMAIL));
        dto.setSmtpUserName(doc.getString(FieldConstants.SMTP_USERNAME));
        dto.setSmtpPassword(doc.getString(FieldConstants.SMTP_PASSWORD));
        dto.setToEmails((ArrayList<String>) doc.get(FieldConstants.TO_EMAILS));
        dto.setSourcePersonal(doc.getString(FieldConstants.SOURCE_PERSONAL));

        return dto;
    }

    public <T> Document mapToDocument(T dto) {
        Document doc = new Document();
        EmailPropertiesDto documentDto = (EmailPropertiesDto) dto;

        if (documentDto.getId() != null)
        {
            doc.put(FieldConstants.COLLECTION_ID, documentDto.getId());
        }
        doc.put(FieldConstants.SMTP_AUTH, documentDto.getSmtpAuth());
        doc.put(FieldConstants.SMTP_PORT, documentDto.getSmtpPort());
        doc.put(FieldConstants.SMTP_HOST, documentDto.getSmtpHost());
        doc.put(FieldConstants.SOURCE_EMAIL, documentDto.getSourceEmail());
        doc.put(FieldConstants.SMTP_USERNAME, documentDto.getSmtpUserName());
        doc.put(FieldConstants.SMTP_PASSWORD, documentDto.getSmtpPassword());
        doc.put(FieldConstants.TO_EMAILS, documentDto. getToEmails());
        doc.put(FieldConstants.SOURCE_PERSONAL, documentDto.getSourcePersonal());

        return doc;
    }
}
