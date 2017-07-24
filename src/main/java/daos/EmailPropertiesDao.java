package daos;

import DocumentDtoMappers.EmailPropertiesMapper;
import com.mongodb.client.MongoDatabase;
import common.CollectionConstants;
import dtos.EmailPropertiesDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robertson_Laptop on 11/29/2016.
 */
public class EmailPropertiesDao extends BaseDao {

    public EmailPropertiesDao(MongoDatabase reliantDb)
    {

        super(reliantDb, CollectionConstants.EMAIL_PROPERTIES,
                new EmailPropertiesMapper());
    }

    public void update(EmailPropertiesDto dto)
    {
        List<EmailPropertiesDto> dtos = new ArrayList<EmailPropertiesDto>();
        dtos.add(dto);

        super.updateList(dtos);
    }

    public EmailPropertiesDto load()
    {
        List<EmailPropertiesDto> dtos = super.getList();

        return dtos.get(0);
    }
}
