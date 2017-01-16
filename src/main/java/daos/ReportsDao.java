package daos;

import DocumentDtoMappers.SirPcrMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import common.CollectionConstants;
import common.FieldConstants;
import dtos.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import utilities.DateTimeUtils;
import utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by Robertson_Laptop on 9/10/2016.
 */
public class ReportsDao extends BaseDao{

    private MongoCollection<Document> sirPcr = null;
    public ReportsDao(final MongoDatabase reliantDb) {

        super(reliantDb, CollectionConstants.LOG, new SirPcrMapper());
        sirPcr = reliantDb.getCollection(CollectionConstants.SIR_PCR);
    }

    //the incoming value will be a date (YYYYMMDD) that represents the 1st day of the
    //7 day week. The results will be a summary of log hours by innotas (primavera)
    //description and log date
    public List<SirHoursSummaryDto> getSirHoursByActivity(SirHoursSummarySearchDto searchDto)
    {
        List<SirHoursSummaryDto> entries = new ArrayList<SirHoursSummaryDto>();

        Bson whereFilter = null;
        Bson sirFilter = null;
        Bson fromFilter = null;
        Bson toFilter = null;

        //search by a given SIR Number or all
        if (searchDto.getSirPcrId() != null) {

            sirFilter = Aggregates.match(Filters.eq(FieldConstants.SIR_PCR_ID, searchDto.getSirPcrId()));
        }

        if (!StringUtils.isEmpty(searchDto.getStartDate()))
        {
            fromFilter = Aggregates.match(Filters.gte(FieldConstants.LOG_DATE, searchDto.getStartDate()));
        }

        if (!StringUtils.isEmpty(searchDto.getEndDate()))
        {
            toFilter = Aggregates.match(Filters.lte(FieldConstants.LOG_DATE, searchDto.getEndDate()));
        }

        if (sirFilter != null)
        {
            whereFilter = sirFilter;
        }
        if (fromFilter != null)
        {
            if (whereFilter == null)
            {
                whereFilter = fromFilter;
            }
            else {
                whereFilter = Filters.and(whereFilter, fromFilter);
            }
        }

        if (toFilter != null)
        {
            if (whereFilter == null)
            {
                whereFilter = toFilter;
            }
            else {
                whereFilter = Filters.and(whereFilter, toFilter);
            }
        }

        Bson project = Projections.fields(new Document(FieldConstants.SIR_PCR_ID,1),
                new Document(FieldConstants.ACTIVITY_DESC,1),
                new Document(FieldConstants.LOG_DATE,1),
                new Document(FieldConstants.HOURS,1));

        Bson groupBy = Aggregates.group(new Document("sirId","$sirPcrId").append("activityId","$activityDesc"),
                Accumulators.sum("totalHours", "$hours"));
        Bson sortBy = Aggregates.sort(new Document("_id",1));

        MongoCursor iterable = null;
        if (whereFilter == null)
        {
            iterable =
                    this.getCollection().aggregate(asList(groupBy, sortBy)).iterator();
        }
        else {

            iterable =
                    this.getCollection().aggregate(asList(whereFilter, groupBy, sortBy)).iterator();
        }

        while (iterable.hasNext())
        {
            Document doc = (Document) iterable.next();

            SirHoursSummaryDto dto = new SirHoursSummaryDto();

            LogDto dateEntry = new LogDto();
            //get the grouping key
            Document t = (Document) doc.get ("_id");

            dto.setSirPcrDto(new SirPcrDto());
            dto.getSirPcrDto().setId(t.getObjectId("sirId"));
            dto.setActivityDesc(t.getString("activityId"));
            dto.setHours(doc.getDouble("totalHours"));

            entries.add(dto);
        }

        return entries;

    }

}
