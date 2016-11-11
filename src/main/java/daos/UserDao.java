package daos;

import DocumentDtoMappers.LogMapper;
import DocumentDtoMappers.UserMapper;
import com.mongodb.client.MongoDatabase;
import common.CollectionConstants;
import common.FieldConstants;
import dtos.UserDto;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Created by Robertson_Laptop on 10/17/2016.
 */
public class UserDao extends BaseDao {

    public UserDao(final MongoDatabase reliantDb) {

        super(reliantDb, CollectionConstants.USER, new UserMapper());

    }

    public UserDto getLoginBySessionId(String sessionId)
    {
        return (UserDto) super.getOne(new Document(FieldConstants.SESSION_ID, sessionId));
    }

    public ObjectId saveLogin(UserDto userDto)
    {
        return super.insertOne(userDto);
    }

    public void deleteLogin(String sessionId)
    {
        super.deleteByFilter(new Document(FieldConstants.SESSION_ID, sessionId));
    }
}
