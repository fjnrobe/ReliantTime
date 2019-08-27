package daos;

import DocumentDtoMappers.EmailHistoryMapper;
import com.mongodb.client.MongoDatabase;
import common.CollectionConstants;
import dtos.BaseDto;
import dtos.EmailMessageDto;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class EmailHistoryDao extends BaseDao {

    public EmailHistoryDao(MongoDatabase reliantDb)
    {

        super(reliantDb, CollectionConstants.EMAIL_HISTORY, new EmailHistoryMapper());
    }

    public ObjectId addEmailHistory(EmailMessageDto dto)
    {
        return super.insertOne(dto);
    }

    public void updateEmailHistory(EmailMessageDto dto)
    {
        List<BaseDto> dtos = new ArrayList<BaseDto>();
        dtos.add(dto);

        super.updateList(dtos);
    }

    public List<EmailMessageDto> getEmailHistory()
    {
        return super.<EmailMessageDto>getList();
    }

}
