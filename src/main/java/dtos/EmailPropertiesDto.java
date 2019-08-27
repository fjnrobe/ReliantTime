package dtos;

import java.util.ArrayList;

/**
 * Created by Robertson_Laptop on 11/29/2016.
 */
public class EmailPropertiesDto extends BaseDto {

    private String smtpHost;
    private String smtpPort;
    private String smtpAuth;
    private String smtpUserName;

    private String sourceEmail;
    private String sourcePersonal;
    private String smtpPassword;
    private ArrayList<String> toEmails;

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(String smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public String getSourceEmail() {
        return sourceEmail;
    }

    public void setSourceEmail(String sourceEmail) {
        this.sourceEmail = sourceEmail;
    }

    public ArrayList<String> getToEmails() {
        return toEmails;
    }

    public void setToEmails(ArrayList<String> toEmails) {
        this.toEmails = toEmails;
    }

    public String getSourcePersonal() {
        return sourcePersonal;
    }

    public void setSourcePersonal(String sourcePersonal) {
        this.sourcePersonal = sourcePersonal;
    }

    public String getSmtpUserName() {
        return smtpUserName;
    }

    public void setSmtpUserName(String smtpUserName) {
        this.smtpUserName = smtpUserName;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }
}
