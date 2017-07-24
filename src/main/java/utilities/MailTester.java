package utilities;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import managers.EmailManager;
import managers.SirPcrManager;

/**
 * Created by Robertson_Laptop on 11/27/2016.
 */
public class MailTester {

    public static void main(String[] args)
    {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("reliant");
        EmailManager manager = new EmailManager(database);

        manager.sendEmail("fjnrobe@yahoo.com", "test test", "Hello World", null);


        // EmailManager.TLSAuthEmailSetup();
        //EmailManager.SSLAuthEmailSetup();
    }
}
