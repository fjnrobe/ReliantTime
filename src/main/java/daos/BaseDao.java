package daos;

import DocumentDtoMappers.BaseMapper;
import DocumentDtoMappers.LogMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import common.CollectionConstants;
import common.FieldConstants;
import dtos.BaseDto;
import dtos.LogDto;
import dtos.SirPcrDto;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Robertson_Laptop on 8/24/2016.
 */
public class BaseDao {

    private MongoDatabase thisDatabase = null;
    private MongoCollection<Document> thisCollection = null;
    private BaseMapper thisMapper = null;


    public BaseDao(MongoDatabase db, String collection, BaseMapper mapper)
    {
        this.thisDatabase = db;
        this.thisCollection =  db.getCollection(collection);
        this.thisMapper = mapper;
    }

    protected List<String> getDistinct(String column) {
        List<String> retValues = new ArrayList<String>();

        MongoIterable list = this.thisCollection.distinct(column, String.class);
        list.into(retValues);

        return retValues;
    }

    protected Long getCount(String columnName, String columnValue)
    {
        Bson filter = Filters.eq(columnName, columnValue );

        return this.thisCollection.count(filter);
    }

    protected <T> List<T> getList(Bson filter, Bson orderBy)
    {

        return getList(filter, orderBy, -1);
    }

    protected <T> List<T> getList(Bson filter, Bson orderBy, int limitTo)
    {
        List<T> results = new ArrayList<T>();

        MongoCursor cursor;
        if (limitTo > 0)
        {
            cursor = this.thisCollection.find(filter).sort(orderBy).limit(limitTo).iterator();
        }
        else
        {
            cursor = this.thisCollection.find(filter).sort(orderBy).iterator();
        }

        while (cursor.hasNext()) {
            Document doc = (Document) cursor.next();
            T dto = (T) this.thisMapper.mapFromDocument(doc);
            results.add(dto);
        }

        return results;
    }

    protected <T> List<T> getList(Bson filter)
    {
        List<T> results = new ArrayList<T>();

        MongoCursor cursor = this.thisCollection.find(filter).iterator();

        while (cursor.hasNext()) {
            Document doc = (Document) cursor.next();
            T dto = (T) this.thisMapper.mapFromDocument(doc);
            results.add(dto);
        }

        return results;
    }

    protected  <T> List<T> getList()
    {
        List<T> results = new ArrayList<T>();

        MongoCursor cursor = this.thisCollection.find().iterator();

        while (cursor.hasNext()) {
            Document doc = (Document) cursor.next();
            T dto = (T) this.thisMapper.mapFromDocument(doc);
            results.add(dto);
        }

        return results;
    }

    protected BaseDto getOne(Bson findBy)
    {
        BaseDto dto = null;
        Document doc = this.thisCollection.find(findBy).first();
        if (doc != null)
        {
            dto = (BaseDto) this.getMapper().mapFromDocument(doc);
        }
        return dto;
    }

    protected <T> ObjectId insertOne(T dto) {

        Document document = this.getMapper().mapToDocument(dto);
        document.put("createDate", new Date());
        document.put("updateDate", null);
        this.getCollection().insertOne(document);

        return document.getObjectId("_id");
    }

    protected <T> void updateList(List<T> dtos) {

        for (T dto : dtos) {
            Document document = this.thisMapper.mapToDocument(dto);

            document.put(FieldConstants.UPDATE_DATE, new Date());

            this.thisCollection.replaceOne(eq(FieldConstants.COLLECTION_ID,
                    document.getObjectId(FieldConstants.COLLECTION_ID)), document);
        }
    }

    protected long deleteByFilter(Bson deleteFilter)
    {
        DeleteResult deleteResult = this.thisCollection.deleteMany(deleteFilter);

        return deleteResult.getDeletedCount();
    }

    protected MongoCollection<Document> getCollection() {
        return this.thisCollection;
    }

    protected BaseMapper getMapper() {
        return this.thisMapper;
    }
}
