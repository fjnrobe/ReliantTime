package dtos;

import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by Robertson_Laptop on 6/27/2016.
 */
public class BaseDto {
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public ObjectId getId() {
        return id;
    }
    public String getIdAsString()
    {
        if (this.id != null) {
            return this.id.toString();
        }
        else
        {
            return "";
        }
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    private ObjectId id;
    private Date createDate;
    private Date updateDate;
}
