package managers;

import com.mongodb.client.MongoDatabase;
import daos.LogDao;
import daos.SIRPCRDao;
import daos.UserDao;
import dtos.UserDto;

/**
 * Created by Robertson_Laptop on 10/17/2016.
 */
public class UserManager {

    private final UserDao userDao;

    public UserManager(MongoDatabase reliantDb) {

        userDao = new UserDao(reliantDb);
    }

    public boolean isLoggedIn(String sessionId)
    {
        return this.userDao.getLoginBySessionId(sessionId) != null;

    }

    public void logInSession(String sessionId, long loginTime)
    {
        UserDto userDto = new UserDto();
        userDto.setLastLoginTime(loginTime);
        userDto.setSession(sessionId);

        this.userDao.saveLogin(userDto);
    }

    public void logOutSession(String sessionId)
    {
        this.userDao.deleteLogin(sessionId);
    }

}
