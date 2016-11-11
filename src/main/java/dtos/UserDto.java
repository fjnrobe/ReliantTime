package dtos;

/**
 * Created by Robertson_Laptop on 10/17/2016.
 */
public class UserDto extends BaseDto {

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    private String session;
    private long lastLoginTime;

}
