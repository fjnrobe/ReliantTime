package dtos;

import java.util.ArrayList;

/**
 * Created by Robertson_Laptop on 11/29/2016.
 */
public class EmailPropertiesDto extends BaseDto {

    private String smtpHost;
    private String smtpPort;
    private String smtpAuth;
    private String sourceEmail;
    private String sourcePersonal;
    private String sourcePassword;
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

    public String getSourcePassword() {
        return sourcePassword;
    }

    public void setSourcePassword(String sourcePassword) {
        this.sourcePassword = sourcePassword;
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
}
