package edu.uic.cloud_computing;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * Created by gabe on 9/11/16.
 */
public class MessageContent {
    private String subject;
    private Date date;

    public String getSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }
    public String getDateAsString(){
        return date.toString();
    }

    private MessageContent(){}


    public MessageContent(MimeMessage message) throws MessagingException{
        this.subject = message.getSubject();
        this.date = message.getSentDate();
    }

    @Override
    public String toString() {
        return "SUBJECT: " + subject + "\t" + "DATE: " + date.toString();
    }
}
