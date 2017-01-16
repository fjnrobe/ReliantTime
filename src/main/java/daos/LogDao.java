package daos;

import DocumentDtoMappers.LogMapper;
import DocumentDtoMappers.SirPcrMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Arrays.asList;

import common.CollectionConstants;
import common.FieldConstants;
import common.LOVEnum;
import common.MonthYear;
import dtos.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utilities.DateTimeUtils;

import java.util.*;

/**
 * Created by Robertson_Laptop on 6/4/2016.
 */
public class LogDao extends BaseDao{

    public LogDao(final MongoDatabase reliantDb) {

        super(reliantDb,CollectionConstants.LOG, new LogMapper());

    }

    public ObjectId insert(LogDto dto)
    {
        Document document = super.getMapper().mapToDocument(dto);
        document.put("createDate", new Date());
        document.put("updateDate", null);
        this.getCollection().insertOne(document);

        return document.getObjectId("_id");
    }

    public LogDto getByLogId(ObjectId logId)
    {
        Bson findBy = Filters.eq(FieldConstants.COLLECTION_ID, logId);
        return (LogDto) super.getOne(findBy);
    }

    public void updateLogs(List<LogDto> dtos)
    {
        super.updateList(dtos);
    }

    public long deleteByLogId(ObjectId logId)
    {
        Bson deleteFilter = Filters.eq(FieldConstants.COLLECTION_ID, logId);
        return super.deleteByFilter(deleteFilter);
    }

    public List<LogDto> getLogsBySirObjectId(ObjectId sirObjectId)
    {
        return super.getList( new Document(FieldConstants.SIR_PCR_ID, sirObjectId));

    }

    public List<LogDto> getLogsByDateRange(String startDate, String endDate) {

        BasicDBObject whereClause = new BasicDBObject();

        whereClause.put(FieldConstants.LOG_DATE,
                        new BasicDBObject("$gte", startDate).append("$lte", endDate));
        Bson orderBy = Sorts.ascending("startDate");

        return  super.getList(whereClause, orderBy);

    }

    public List<String> getYearsWithLoggedPeriods()
    {
        List<String> list = new ArrayList<String>();
        //Select distinct substr(logDate,1,6)
        //   from log
        //   order by  1 desc
        //        db.log.aggregate([{$project: {monthYear:{$substr:["$logDate",0,4]}}},
        //        {$group: {_id:"$monthYear"}},
        //        {$sort: {_id:-1} }])

        Bson project = Aggregates.project(new Document("year",Document.parse("{$substr: ['$logDate',0,4]}")));
        Bson groupBy = Aggregates.group("$year");
        Bson sortBy = Aggregates.sort(new Document("_id", -1));

        MongoCursor iterable =
                this.getCollection().aggregate(asList(project, groupBy, sortBy)).iterator();

        while (iterable.hasNext())
        {
            Document doc = (Document) iterable.next();

            list.add(doc.getString("_id"));
        }

        return list;
    }

    public List<MonthYear> getMonthsWithLoggedPeriods()
    {

        List<MonthYear> list = new ArrayList<MonthYear>();
        //Select distinct substr(logDate,1,6)
        //   from log
        //   order by  1 desc
        //        db.log.aggregate([{$project: {monthYear:{$substr:["$logDate",0,6]}}},
        //        {$group: {_id:"$monthYear"}},
        //        {$sort: {_id:-1} }])

        Bson project = Aggregates.project(new Document("monthYear",Document.parse("{$substr: ['$logDate',0,6]}")));
        Bson groupBy = Aggregates.group("$monthYear");
        Bson sortBy = Aggregates.sort(new Document("_id", -1));

        MongoCursor iterable =
                this.getCollection().aggregate(asList(project, groupBy, sortBy)).iterator();

        while (iterable.hasNext())
        {
            Document doc = (Document) iterable.next();

           list.add(new MonthYear((String) doc.get("_id")));
        }

        return list;
    }

    public List<CalendarDto> getCalendarHoursByMonth(MonthYear monthYear)
    {
        List<CalendarDto> entries = new ArrayList<CalendarDto>();

        String startDate = monthYear.getSortKey() + "01";
        String endDate = monthYear.getSortKey() + DateTimeUtils.getLastDayOfMonth(monthYear);

        Bson matchString = Aggregates.match(Filters.and(Filters.gte("logDate", startDate),
                                                        Filters.lte("logDate", endDate),
                                                        Filters.eq("billableInd", true)));
        Bson groupBy = Aggregates.group(Document.parse("{$substr: ['$logDate',6,2]}"),
                Accumulators.sum("TotalHours", "$hours"));
        Bson sortBy = Aggregates.sort(new Document("_id",1));
        MongoCursor iterable =
                this.getCollection().aggregate(asList(matchString, groupBy, sortBy)).iterator();

        while (iterable.hasNext())
        {
            Document doc = (Document) iterable.next();

            CalendarDto dateEntry = new CalendarDto();

            dateEntry.setDayOfMonth(doc.getString("_id"));
            dateEntry.setHours(doc.getDouble("TotalHours"));

            entries.add(dateEntry);
        }

        return entries;
    }

    //the incoming value will be a date (YYYYMMDD) that represents the 1st day of the
    //7 day week. The results will be a summary of log hours by innotas (primavera)
    //description and log date
    public List<LogDto> getInnotasHours(String startDate)
    {
        List<LogDto> entries = new ArrayList<LogDto>();
        String startOfWeek = DateTimeUtils.getFirstDayOfWeek(startDate);
        String endDate = DateTimeUtils.getLastDayOfWeek(startOfWeek);

        Bson matchString = Aggregates.match(Filters.and(Filters.gte("logDate", startOfWeek),
                Filters.lte("logDate", endDate)));
        Bson project = Projections.fields(new Document("primaveraDesc",1),
                                          new Document("logDate",1),
                                          new Document("hours",1));
        Bson groupBy = Aggregates.group(new Document("desc","$primaveraDesc").append("day","$logDate"),
                Accumulators.sum("TotalHours", "$hours"));
        Bson sortBy = Aggregates.sort(new Document("_id",1));
        MongoCursor iterable =
                this.getCollection().aggregate(asList(matchString, groupBy, sortBy)).iterator();


        while (iterable.hasNext())
        {
            Document doc = (Document) iterable.next();

            LogDto dateEntry = new LogDto();
            Document t = (Document) doc.get ("_id");
            dateEntry.setPrimaveraDesc(t.getString("desc"));
            dateEntry.setLogDate(t.getString("day"));
            dateEntry.setHours(doc.getDouble("TotalHours"));

            entries.add(dateEntry);
        }

        return entries;

    }

    public Long getUsageCount(String columnName, String columnValue)
    {
        return super.getCount(columnName, columnValue);
    }

    public long updateLovValue(LOVEnum lovEnum, String oldValue, String newValue)
    {
        Bson filter = null;
        Bson update = null;
        switch (lovEnum)
        {
            case ACTIVITY_TYPE:
                filter = Filters.eq(FieldConstants.ACTIVITY_DESC, oldValue);
                update = Updates.set(FieldConstants.ACTIVITY_DESC, newValue);
                break;
            case PRIMAVERA_TYPE:
                filter = Filters.eq(FieldConstants.PRIMAVERA_DESC, oldValue);
                update = Updates.set(FieldConstants.PRIMAVERA_DESC, newValue);
                break;
        }

        return this.getCollection().updateMany(filter, update).getModifiedCount();

    }

    public Map<ObjectId, SirPcrViewDto> getSirSummaryRangeInfo()
    {

        //we want to get the earlest start date and latest start date for each SIR
//        db.log.aggregate([{$group:{ _id: "$sirPcrId",
//            earliestStartDate: {$min: "$logDate"},
//        latestEndDate: {$max: "$logDate"}}}]);

        Map<ObjectId, SirPcrViewDto> mapDto = new HashMap<ObjectId, SirPcrViewDto>();

        Bson groupBy = Aggregates.group(new Document("sirPcrId","$sirPcrId"),
                Accumulators.min("earliestStartDate", "$logDate"),
                Accumulators.max("latestEndDate", "$logDate"));

        MongoCursor iterable =
                this.getCollection().aggregate(asList(groupBy)).iterator();


        while (iterable.hasNext())
        {
            Document doc = null;
            try {
                doc = (Document) iterable.next();
                SirPcrViewDto dto = new SirPcrViewDto();
                dto.setEarliestLogDate(doc.getString("earliestStartDate"));
                dto.setLatestLogDate(doc.getString("latestEndDate"));
                Document t = (Document) doc.get("_id");
                dto.getSirPcrDto().setId(t.getObjectId("sirPcrId"));
                mapDto.put(dto.getSirPcrDto().getId(), dto);
            } catch (Exception e)
            {
                int x = 1;
            }
        }

        return mapDto;

    }
}
