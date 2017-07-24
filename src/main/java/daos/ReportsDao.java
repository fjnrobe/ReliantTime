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

    public List<ActivitySummaryDto> getActivitySummaryBySubprocess(String fromDate, String toDate)
    {
        //the following is equalvant to
        //select sum(hours), subprocessDesc, activityDesc
        //  from sirPcr s
        //   inner join log l
        //   on s._id = log.sirPcrId
        //   where l.logDate between '20170101' and '20170630'
        //    group by subprocessDesc, activityDesc
        //    order by hours desc;

//        db.log.aggregate([{$match: {logDate: {$gte: '20170101', $lte: '20170131'}}},
//        {$lookup: {from: "sirPcr", localField: "sirPcrId", foreignField: "_id", as:"sirPcr"}},
//        {$project: {"_id":0,
//                "sirPcr.subprocessDesc":1,
//                "activityDesc":1,
//                "hours":1}},
//        {$unwind: "$sirPcr"},
//        {$group: { _id: {subprocessDesc: "$sirPcr.subprocessDesc",
//                activityDesc: "$activityDesc"},
//            totalHours: {$sum: "$hours"}}},
//        {$project: { _id:0,
//                subprocessDesc: "$_id.subprocessDesc",
//                activityDesc: "$_id.activityDesc",
//                totalHours:"$totalHours"}},
//
//        {$sort: {"totalHours":-1}}
//        ]
//        );



        List<ActivitySummaryDto> dtos = new ArrayList<ActivitySummaryDto>();

        Bson matchString = Aggregates.match(Filters.and(Filters.gte("logDate", fromDate),
                Filters.lte("logDate", toDate)));

        Bson lookupString = Aggregates.lookup( "sirPcr", "sirPcrId", "_id", "sirPcr");

        Bson firstProjectFields = Projections.fields(new Document("_id",0),
                new Document("sirPcr.subprocessDesc",1),
                new Document("activityDesc",1),
                new Document("hours",1));

        Bson firstProject = Aggregates.project(firstProjectFields);

        Bson unwind = Aggregates.unwind( "$sirPcr");

        Bson groupBy = Aggregates.group(new Document("subprocessDesc","$sirPcr.subprocessDesc").
                             append("activityDesc","$activityDesc"),
                             Accumulators.sum("totalHours", "$hours"));

        Bson secondProjectFields = Projections.fields(new Document("_id",0),
                                                new Document("subprocessDesc", "$_id.subprocessDesc"),
                                                new Document("activityDesc", "$_id.activityDesc"),
                                                new Document("totalHours","$totalHours"));
        Bson secondProject = Aggregates.project(secondProjectFields);

        Bson sortBy = Aggregates.sort(new Document("totalHours",-1));

        MongoCursor iterable =
                this.getCollection().aggregate(asList(matchString, lookupString,
                                                      firstProject, unwind,
                                                        groupBy,
                                                      secondProject, sortBy)).iterator();

        while (iterable.hasNext())
        {
            Document doc = (Document) iterable.next();

            ActivitySummaryDto dto = new ActivitySummaryDto();
            dto.setSubprocessDesc(doc.getString("subprocessDesc"));
            dto.setActivityDesc(doc.getString("activityDesc"));
            dto.setHours(doc.getDouble("totalHours"));

            dtos.add(dto);
        }

        return dtos;
    }

    public List<ActivitySummaryDto> getActivitySummaryByActivity(String fromDate, String toDate)
    {
        //the following is equalvant to
        //select sum(hours), activityDesc
        //  from sirPcr s
        //   inner join log l
        //   on s._id = log.sirPcrId
        //   where l.logDate between '20170101' and '20170630'
        //    group by activityDesc
        //    order by hours desc;

//        db.log.aggregate([{$match: {logDate: {$gte: '20170101', $lte: '20170131'}}},
//        {$project: {"_id":0,
//                "activityDesc":1,
//                "hours":1}},
//        {$group: { _id: { activityDesc: "$activityDesc"},
//            totalHours: {$sum: "$hours"}}},
//        {$project: { _id:0,
//                activityDesc: "$_id.activityDesc",
//                totalHours:"$totalHours"}},
//        {$sort: {"totalHours":-1}}
//        ]
//        );
//
        List<ActivitySummaryDto> dtos = new ArrayList<ActivitySummaryDto>();

        Bson matchString = Aggregates.match(Filters.and(Filters.gte("logDate", fromDate),
                Filters.lte("logDate", toDate)));

        Bson firstProjectFields = Projections.fields(new Document("_id",0),
                                                new Document("activityDesc",1),
                                                new Document("hours",1));

        Bson firstProject = Aggregates.project(firstProjectFields);

        Bson groupBy = Aggregates.group(new Document("activityDesc","$activityDesc"),
                Accumulators.sum("totalHours", "$hours"));

        Bson secondProjectFields = Projections.fields(new Document("_id",0),
                new Document("activityDesc", "$_id.activityDesc"),
                new Document("totalHours","$totalHours"));

        Bson secondProject = Aggregates.project(secondProjectFields);

        Bson sortBy = Aggregates.sort(new Document("totalHours",-1));

        MongoCursor iterable =
                this.getCollection().aggregate(asList(matchString,
                        firstProject, groupBy,
                        secondProject, sortBy)).iterator();

        while (iterable.hasNext())
        {
            Document doc = (Document) iterable.next();

            ActivitySummaryDto dto = new ActivitySummaryDto();
            dto.setActivityDesc(doc.getString("activityDesc"));
            dto.setHours(doc.getDouble("totalHours"));

            dtos.add(dto);
        }

        return dtos;
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
