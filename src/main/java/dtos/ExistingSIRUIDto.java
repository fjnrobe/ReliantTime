package dtos;

import org.bson.types.ObjectId;

/**
 * Created by Robertson_Laptop on 8/24/2016.
 */
public class ExistingSIRUIDto {

    private String id;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
