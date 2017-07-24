package managers;

import com.mongodb.client.MongoDatabase;
import common.SystemConstants;
import daos.DeductionDao;
import daos.EmailPropertiesDao;
import dtos.EmailPropertiesDto;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by Robertson_Laptop on 11/23/2016.
 */
public class EmailManager {

    private final EmailPropertiesDao emailPropertiesDao;

    public EmailManager(MongoDatabase reliantDb) {

        emailPropertiesDao = new EmailPropertiesDao(reliantDb);
    }

    public List<String> getToEmailAddresses()
    {
        EmailPropertiesDto props = this.loadProperties();

        return props.getToEmails();
    }

    public void addReceipientEmail(String email)
    {
        EmailPropertiesDto props = this.loadProperties();

        boolean found = false;
        //see if the incoming email address is new
        for (String toEmail : props.getToEmails())
        {
            if (email.equals(toEmail))
            {
                found = true;
                break;
            }
        }

        if (!found)
        {
            props.getToEmails().add(email);
            this.emailPropertiesDao.update(props);
        }
    }

    public void updateProperties(EmailPropertiesDto dto)
    {
        this.emailPropertiesDao.update(dto);
    }

    public EmailPropertiesDto loadProperties()
    {
        return this.emailPropertiesDao.load();
    }

    public void sendEmail(String toEmail, String subjectText,
                               String bodyText, String attachmentFileName)
    {
        try {

            final EmailPropertiesDto propertiesDto = this.loadProperties();

            Session session = this.initSession(propertiesDto);

            MimeMessage msg =
                    this.packageMessage(propertiesDto, session,toEmail, subjectText);

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(bodyText);

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            if (attachmentFileName != null) {
                messageBodyPart = new MimeBodyPart();
                String directory = SystemManager.loadProperties().getProperty(SystemConstants.FILE_DIRECTORY);
                String filename = directory + "/" + attachmentFileName;
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
            }
            // Send the complete message parts
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);

            this.addReceipientEmail(toEmail);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * setup for TLS authorization
     *
     * @return - Session object for email transport
     */
    private Session initSession(final EmailPropertiesDto propertiesDto)
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", propertiesDto.getSmtpHost());
        props.put("mail.smtp.port", propertiesDto.getSmtpPort()); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(propertiesDto.getSourceEmail(),
                                                    propertiesDto.getSourcePassword());
            }
        };

        return Session.getInstance(props, auth);
    }

    private MimeMessage packageMessage(EmailPropertiesDto propertiesDto,
                                      Session session, String toEmail,
                                      String subjectText)
    {
        MimeMessage msg = new MimeMessage(session);

        try {
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(propertiesDto.getSourceEmail(),
                    propertiesDto.getSourcePersonal()));

            msg.setReplyTo(InternetAddress.parse(toEmail, false));

            msg.setSubject(subjectText, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        } catch (Exception e)
        {

        }
        return msg;
    }

//    public void TLSAuthEmailSetup()
//    {
//        final String fromEmail = "fjnrobe@yahoo.com"; //requires valid gmail id
//        final String password = "P1apple#"; // correct password for gmail id
//        final String toEmail = "fjnrobe@yahoo.com"; // can be any email id
//
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.mail.yahoo.com"); //SMTP Host
//        props.put("mail.smtp.port", "587"); //TLS Port
//        props.put("mail.smtp.auth", "true"); //enable authentication
//        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
//
//        //create Authenticator object to pass in Session.getInstance argument
//        Authenticator auth = new Authenticator() {
//            //override the getPasswordAuthentication method
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(fromEmail, password);
//            }
//        };
//        Session session = Session.getInstance(props, auth);
//
//     //   EmailManager.sendEmail(session, toEmail,"TLSEmail Testing Subject", "TLSEmail Testing Body");
//
//     //   EmailManager.sendAttachmentEmail(session, toEmail,"TLSEmail Testing Subject with Attachment", "SSLEmail Testing Body with Attachment");
//
//        EmailManager.sendImageEmail(session, toEmail,"SSLEmail Testing Subject with Image", "SSLEmail Testing Body with Image");
//
//
//    }

//    public static void SSLAuthEmailSetup()
//    {
//        /**
//         Outgoing Mail (SMTP) Server
//         requires TLS or SSL: smtp.gmail.com (use authentication)
//         Use Authentication: Yes
//         Port for SSL: 465
//         */
//
//        final String fromEmail = "fjnrobe@yahoo.com"; //requires valid gmail id
//        final String password = "P1apple#"; // correct password for gmail id
//        final String toEmail = "fjnrobe@yahoo.com"; // can be any email id
//
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.mail.yahoo.com"); //SMTP Host
//        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
//        props.put("mail.smtp.socketFactory.class",
//                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
//        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
//        props.put("mail.smtp.port", "465"); //SMTP Port
//
//        Authenticator auth = new Authenticator() {
//            //override the getPasswordAuthentication method
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(fromEmail, password);
//            }
//        };
//
//        Session session = Session.getDefaultInstance(props, auth);
//        System.out.println("Session created");
//        EmailManager.sendEmail(session, toEmail,"SSLEmail Testing Subject", "SSLEmail Testing Body");
//
//
//
//    }

//    public static void noAuthEmailSetup()
//    {
//
//        String smtpHostServer = "smtp.mail.yahoo.com";
//        String emailID = "fjnrobe@yahoo.com";
//
//        Properties props = System.getProperties();
//
//        props.put("mail.smtp.host", smtpHostServer);
//
//        Session session = Session.getInstance(props, null);
//
//        EmailManager.sendEmail(session, emailID,"SimpleEmail Testing Subject", "SimpleEmail Testing Body");
//    }

    /**
     * Utility method to send simple HTML email
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     */
//    public static void sendEmail(Session session, String toEmail, String subject, String body){
//        try
//        {
//            MimeMessage msg = new MimeMessage(session);
//            //set message headers
//            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
//            msg.addHeader("format", "flowed");
//            msg.addHeader("Content-Transfer-Encoding", "8bit");
//
//            msg.setFrom(new InternetAddress("fjnrobe@yahoo.com", "fjnrobe"));
//
//            msg.setReplyTo(InternetAddress.parse("fjnrobe@yahoo.com", false));
//
//            msg.setSubject(subject, "UTF-8");
//
//            msg.setText(body, "UTF-8");
//
//            msg.setSentDate(new Date());
//
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
//
//            Transport.send(msg);
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void sendAttachmentEmail(Session session, String toEmail, String subject, String body){
//
//        try{
//            MimeMessage msg = new MimeMessage(session);
//            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
//            msg.addHeader("format", "flowed");
//            msg.addHeader("Content-Transfer-Encoding", "8bit");
//
//            msg.setFrom(new InternetAddress("fjnrobe@yahoo.com", "fjnrobe"));
//
//            msg.setReplyTo(InternetAddress.parse("fjnrobe@yahoo.com", false));
//
//            msg.setSubject(subject, "UTF-8");
//
//            msg.setSentDate(new Date());
//
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
//
//            // Create the message body part
//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            // Fill the message
//            messageBodyPart.setText(body);
//
//            // Create a multipart message for attachment
//            Multipart multipart = new MimeMultipart();
//
//            // Set text message part
//            multipart.addBodyPart(messageBodyPart);
//
//            // Second part is attachment
//            messageBodyPart = new MimeBodyPart();
//            String filename = "C:\\reliantData\\JRobertson_PO_66584_Timesheet_and_Invoice_Oct_2016.xlsx";
//            DataSource source = new FileDataSource(filename);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(filename);
//            multipart.addBodyPart(messageBodyPart);
//
//            // Send the complete message parts
//            msg.setContent(multipart);
//
//            // Send message
//            Transport.send(msg);
//        }catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public static void sendImageEmail(Session session, String toEmail, String subject, String body){
//        try{
//            MimeMessage msg = new MimeMessage(session);
//            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
//            msg.addHeader("format", "flowed");
//            msg.addHeader("Content-Transfer-Encoding", "8bit");
//
//            msg.setFrom(new InternetAddress("fjnrobe@yahoo.com", "fjnrobe"));
//
//            msg.setReplyTo(InternetAddress.parse("fjnrobe@yahoo.com", false));
//
//            msg.setSubject(subject, "UTF-8");
//
//            msg.setSentDate(new Date());
//
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
//
//            // Create the message body part
//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(body);
//
//            // Create a multipart message for attachment
//            Multipart multipart = new MimeMultipart();
//
//            // Set text message part
//            multipart.addBodyPart(messageBodyPart);
//
//            // Second part is image attachment
//            messageBodyPart = new MimeBodyPart();
//            String filename = "c:\\reliantData\\IMG_4287.jpg";
//            DataSource source = new FileDataSource(filename);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(filename);
//            //Trick is to add the content-id header here
//            messageBodyPart.setHeader("Content-ID", "image_id");
//            multipart.addBodyPart(messageBodyPart);
//
//            //third part for displaying image in the email body
//            messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setContent("<h1>Attached Image</h1>" +
//                    "<img src='cid:image_id'>", "text/html");
//            multipart.addBodyPart(messageBodyPart);
//
//            //Set the multipart message to the email message
//            msg.setContent(multipart);
//
//            // Send message
//            Transport.send(msg);
//        }catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void sendImageEmail(Session session, String toEmail, String subject, String body){
//        try{
//            MimeMessage msg = new MimeMessage(session);
//            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
//            msg.addHeader("format", "flowed");
//            msg.addHeader("Content-Transfer-Encoding", "8bit");
//
//            msg.setFrom(new InternetAddress("fjnrobe@yahoo.com", "fjnrobe"));
//
//            msg.setReplyTo(InternetAddress.parse("fjnrobe@yahoo.com", false));
//
//            msg.setSubject(subject, "UTF-8");
//
//            msg.setSentDate(new Date());
//
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
//
//            // Create the message body part
//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(body);
//
//            // Create a multipart message for attachment
//            Multipart multipart = new MimeMultipart();
//
//            // Set text message part
//            multipart.addBodyPart(messageBodyPart);
//
//            // Second part is image attachment
//            messageBodyPart = new MimeBodyPart();
//            String filename = "c:\\reliantData\\IMG_4287.jpg";
//            DataSource source = new FileDataSource(filename);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(filename);
//            //Trick is to add the content-id header here
//            messageBodyPart.setHeader("Content-ID", "image_id");
//            multipart.addBodyPart(messageBodyPart);
//
//            //third part for displaying image in the email body
//            messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setContent("<h1>Attached Image</h1>" +
//                    "<img src='cid:image_id'>", "text/html");
//            multipart.addBodyPart(messageBodyPart);
//
//            //Set the multipart message to the email message
//            msg.setContent(multipart);
//
//            // Send message
//            Transport.send(msg);
//        }catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

}
