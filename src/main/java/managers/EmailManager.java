package managers;

import com.mongodb.client.MongoDatabase;
import common.SystemConstants;
import daos.DeductionDao;
import daos.EmailHistoryDao;
import daos.EmailPropertiesDao;
import dtos.EmailMessageDto;
import dtos.EmailPropertiesDto;
import utilities.DateTimeUtils;

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
    private final EmailHistoryDao emailHistoryDao;

    public EmailManager(MongoDatabase reliantDb) {

        emailPropertiesDao = new EmailPropertiesDao(reliantDb);
        emailHistoryDao = new EmailHistoryDao(reliantDb);
    }

    public List<EmailMessageDto> getEmailHistory()
    {
        return this.emailHistoryDao.getEmailHistory();
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
            // Create a transport.
            Transport transport = session.getTransport();

            // Send the message.
            try
            {
                System.out.println("Sending...");

                // Connect to Amazon SES using the SMTP username and password you specified above.
                transport.connect(propertiesDto.getSmtpHost(), propertiesDto.getSmtpUserName(), propertiesDto.getSmtpPassword());

                // Send the email.
                transport.sendMessage(msg, msg.getAllRecipients());
                System.out.println("Email sent!");
            }
            catch (Exception ex) {
                System.out.println("The email was not sent.");
                System.out.println("Error message: " + ex.getMessage());
            }
            finally
            {
                // Close and terminate the connection.
                transport.close();
            }
  //          Transport.send(msg);

            this.addReceipientEmail(toEmail);

            //log the email transmission
            EmailMessageDto mailDto = new EmailMessageDto();
            mailDto.setToEmail(toEmail);
            mailDto.setFromEmail(propertiesDto.getSourceEmail());
            mailDto.setSubject(subjectText);
            mailDto.setBody(bodyText);
            mailDto.setSendDate(DateTimeUtils.getSystemDate());
            mailDto.setAttachmentName(attachmentFileName);
            this.emailHistoryDao.addEmailHistory(mailDto);

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
        props.put("mail.transport.protocol", "smtp");
       // props.put("mail.smtp.host", propertiesDto.getSmtpHost());
        props.put("mail.smtp.port", propertiesDto.getSmtpPort()); //TLS Port
        props.put("mail.smtp.auth", propertiesDto.getSmtpAuth()); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

//        //create Authenticator object to pass in Session.getInstance argument
//        Authenticator auth = new Authenticator() {
//            //override the getPasswordAuthentication method
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(propertiesDto.getSourceEmail(),
//                                                    propertiesDto.getSourcePassword());
//            }
//        };

//        return Session.getInstance(props, auth);
           return Session.getDefaultInstance(props);
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

}
