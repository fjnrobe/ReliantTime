package daos;

import DocumentDtoMappers.SirPcrMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import common.CollectionConstants;
import dtos.SirPcrViewDto;
import org.bson.Document;
import utilities.DateTimeUtils;

import java.util.List;

/**
 * Created by Robertson_Laptop on 9/10/2016.
 */
public class ReportsDao extends BaseDao{

    private MongoCollection<Document> sirPcr = null;
    public ReportsDao(final MongoDatabase reliantDb) {

        super(reliantDb, CollectionConstants.LOG, new SirPcrMapper());
        sirPcr = reliantDb.getCollection(CollectionConstants.SIR_PCR);
    }


}
