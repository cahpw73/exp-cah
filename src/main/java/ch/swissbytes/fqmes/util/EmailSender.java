package ch.swissbytes.fqmes.util;

import com.sun.mail.util.MailConnectException;
import com.sun.mail.util.SocketConnectException;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.logging.Logger;

@Named
@RequestScoped
public class EmailSender implements Serializable{

    private static final Logger log = Logger.getLogger(EmailSender.class.getName());

    @Resource(mappedName = "java:jboss/mail/Default")

    private Session mailSession;
    private String to;
    private String from;
    private String subject;
    private String body;
    private Multipart content;

    public void send() throws MailConnectException, ConnectException, MessagingException, SocketConnectException {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            Address toAddress = new InternetAddress(to);
            message.addRecipient(Message.RecipientType.TO, toAddress);
            message.setSubject(subject);
            message.setContent(content);
            Transport.send(message);
            log.info(String.format("Your e-mail has been sent"));

        } catch (MailConnectException ce) {
            ce.printStackTrace();
            throw new MessagingException(ce.toString());
        } catch (MessagingException ce) {
            ce.printStackTrace();
            throw new MessagingException(ce.toString());

        }
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Multipart getContent() {
        return content;
    }

    public void setContent(Multipart content) {
        this.content = content;
    }
}
