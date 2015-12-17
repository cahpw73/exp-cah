package ch.swissbytes.fqmes.util;

import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.VerificationTokenEntity;
import com.sun.mail.util.SocketConnectException;
import org.omnifaces.util.Messages;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Christian on 25/11/2015.
 */

public class CreateEmailSender implements Serializable {

    private static final Logger log = Logger.getLogger(CreateEmailSender.class.getName());

    private static final String sendMailInfo  = System.getProperty("fqmes.send.mail.info");

    private static final String sendToSmacneall  = System.getProperty("fqmes.send.mail.to.smacneall");

    private static final String sendToDevs = System.getProperty("fqmes.send.mail.to.others.recipients");


    @Inject
    private Configuration configuration;

    @Inject
    private EmailSender email;

    public void createEmailAndSend(UserEntity userEntity,VerificationTokenEntity tokenEntity){
        StringBuilder builder = new StringBuilder();
        builder.append(getResetPasswordURL()).append("token=").append(tokenEntity.getToken());
        String link = String.format("<a href=%s>%s</a>", builder.toString(),configuration.getMessage("reset.link"));
        try {
            MimeMultipart multipart = createMimeMultipart(link);
            sendMail(userEntity.getEmail(),multipart);
            Messages.addFlashGlobalInfo("Email was sent successfully ");
            log.info("Token was generated and sent to email");
        } catch (MessagingException e) {
            Messages.addFlashGlobalError("Error occurred while sending the token to your email");
            log.log(Level.SEVERE,"Messaging has error" + e.getMessage());
            e.printStackTrace();
        } catch (SocketConnectException e) {
            Messages.addFlashGlobalError("Error occurred while sending the token to your email");
            e.printStackTrace();
            log.log(Level.SEVERE,"Messaging has error" + e.getCause());
        } catch (ConnectException e) {
            Messages.addFlashGlobalError("Error occurred while sending the token to your email");
            log.log(Level.SEVERE,"Messaging has error" + e.getCause());
            e.printStackTrace();
        }catch(Exception ex){
            Messages.addFlashGlobalError("It cannot send the email to restart the password.");
            log.log(Level.SEVERE,"Messaging has error" + ex.getCause());
            ex.printStackTrace();
        }
    }

    public void createEmailToInfoErrorExportCmsOrJde(String error){
        try {
            MimeMultipart multipart = createMimeMultipartErrorExportCmsOrJde(error);
            sendMailExprtCmsOrJde(sendToSmacneall, multipart,sendToDevs);
            Messages.addFlashGlobalInfo("Email was sent successfully ");
            log.info("Token was generated and sent to email");
        } catch (MessagingException e) {
            Messages.addFlashGlobalError("Error occurred while sending the token to your email");
            log.log(Level.SEVERE,"Messaging has error" + e.getMessage());
            e.printStackTrace();
        } catch (SocketConnectException e) {
            Messages.addFlashGlobalError("Error occurred while sending the token to your email");
            e.printStackTrace();
            log.log(Level.SEVERE,"Messaging has error" + e.getCause());
        } catch (ConnectException e) {
            Messages.addFlashGlobalError("Error occurred while sending the token to your email");
            log.log(Level.SEVERE,"Messaging has error" + e.getCause());
            e.printStackTrace();
        }catch(Exception ex){
            Messages.addFlashGlobalError("It cannot send the email to restart the password.");
            log.log(Level.SEVERE,"Messaging has error" + ex.getCause());
            ex.printStackTrace();
        }
    }

    private void sendMailExprtCmsOrJde(String mail, MimeMultipart multipart,String recipients) throws MessagingException, SocketConnectException, ConnectException,Exception {
        if(email == null){
            email = new EmailSender();
        }
        email.setFrom(sendMailInfo);
        email.setTo(mail);
        email.setToCC(recipients);
        email.setSubject("Error exporting CMS or JDE");
        //email.setBody(message);
        email.setContent(multipart);
        email.sendMultipleRecipients();
    }

    private void sendMail(String mail, MimeMultipart multipart) throws MessagingException, SocketConnectException, ConnectException,Exception {
        if(email == null){
            email = new EmailSender();
        }
        email.setFrom(sendMailInfo);
        email.setTo(mail);
        email.setSubject("Reset Password");
        //email.setBody(message);
        email.setContent(multipart);
        email.send();
    }


    private MimeMultipart createMimeMultipart(final String link) {
        try {
            //Create the html body
            MimeMultipart multipart = new MimeMultipart("related");
            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            StringBuilder sbHtmlText = new StringBuilder();
            sbHtmlText.append("<p> " + configuration.getMessage("reset.message.request") + " </p>\n");
            sbHtmlText.append("<p> " + "Please " + link +  " </p>\n");
            sbHtmlText.append("\t\t\t\t\t<br/>\n");
            sbHtmlText.append("<p> " +configuration.getMessage("reset.message.warning") +  " </p>\n");

            messageBodyPart.setContent(sbHtmlText.toString(), "text/html");
            multipart.addBodyPart(messageBodyPart);

            // add it
            multipart.addBodyPart(messageBodyPart);
            return multipart;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MimeMultipart createMimeMultipartErrorExportCmsOrJde(final String error) {
        try {
            //Create the html body
            MimeMultipart multipart = new MimeMultipart("related");
            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            StringBuilder sbHtmlText = new StringBuilder();
            sbHtmlText.append("<p> " + "Something happened trying to export POs ?" + " </p>\n");
            sbHtmlText.append("<br/>\n");
            sbHtmlText.append("<p> " + "Here you are the stack trace:" + " </p>\n");
            sbHtmlText.append("<p> " +  error +  " </p>\n");

            messageBodyPart.setContent(sbHtmlText.toString(), "text/html");
            multipart.addBodyPart(messageBodyPart);

            // add it
            multipart.addBodyPart(messageBodyPart);
            return multipart;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getResetPasswordURL() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        String domainUrl = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/";
        return domainUrl + "resetpassword.jsf?";
    }
}
