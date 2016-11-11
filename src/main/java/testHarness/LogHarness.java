package testHarness;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import common.CollectionConstants;
import common.MonthYear;
import daos.LogDao;
import daos.SIRPCRDao;
import dtos.CalendarDto;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static java.util.Arrays.asList;

/**
 * Created by Robertson_Laptop on 6/12/2016.
 */
public class LogHarness {

    public static void main (String[] args)
    {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase(CollectionConstants.DATABASE);

        LogDao dao = new LogDao(db);
//        List<CalendarDto> entries = dao.getCalendarHoursByMonth(new MonthYear("201405"));
//        for (CalendarDto entry: entries)
//        {
//            System.out.println("day: " + entry.getDayOfMonth() + ", hours: " + entry.getHours());
//        }

        List<MonthYear> list = dao.getMonthsWithLoggedPeriods();
        for (MonthYear item : list) {
            System.out.println("value: " + item.getMonthAndYearName());
        }
    }


}
