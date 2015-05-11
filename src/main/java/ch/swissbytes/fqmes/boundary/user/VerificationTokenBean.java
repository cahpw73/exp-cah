package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.Service.business.VerificationToken.VerificationTokenService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.VerificationTokenEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.EmailSender;
import com.sun.mail.util.SocketConnectException;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Messages;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class VerificationTokenBean implements Serializable {

    private static final Logger log = Logger.getLogger(VerificationTokenBean.class.getName());

    @Inject
    private EmailSender email;

    @Inject
    private VerificationTokenService verificationTokenService;

    @Inject
    private UserService userService;

    @Inject
    private Configuration configuration;

    private static final String sendMailInfo  = System.getProperty("fqmes.send.mail.info");

    private UserEntity userEntity;

    private String mail;


    public String sendLostPasswordToken()   {
        log.info("Sending token to email...");
        if(isValidEmail(mail)){
            log.info("user is not null");
            VerificationTokenEntity tokenEntity = verificationTokenService.getVerificationTokenGenerated(userEntity);
            log.info("token is not null");
            if (tokenEntity != null) {
                StringBuilder builder = new StringBuilder();
                builder.append(getResetPasswordURL()).append("token=").append(tokenEntity.getToken());
                ;
                String link = String.format("<a href=%s>%s</a>", builder.toString(),configuration.getMessage("reset.link"));
                try {
                    MimeMultipart multipart = createMimeMultipart(link);
                    sendMail(userEntity.getEmail(),multipart);
                    Messages.addFlashGlobalInfo("Your token generated was sent");
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
                }
                return "login?faces-redirect=true";
            }
        }
        return "failed";
    }

    private boolean isValidEmail(String mail) {
        log.info("boolean validateEmail(String mail["+mail+"])");
        boolean result = true;
        if(mail == null || StringUtils.isEmpty(mail)){
            result = false;
            Messages.addError("forgetPassForm:inputEmail","Email is required!");
        }else{
            List<UserEntity> userList = userService.findUserByEmail(mail);
            if(userList.isEmpty()) {
                result = false;
                Messages.addGlobalError("Does not exist email!");
            }else{
                userEntity = userList.get(0);
            }
        }
        return result;
    }

    private String getResetPasswordURL() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        String domainUrl = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/";
        return domainUrl + "resetpassword.jsf?";
    }

    private UserEntity getUserByEmail(String email) {
        return userService.findUserByEmail(email).get(0);
    }

    private String getEmailMessage(){
        return "Reset password";
    }

    private String getEmailGreetings(){
        return "Reset Password";
    }

    private void sendMail(String mail, MimeMultipart multipart) throws MessagingException, SocketConnectException, ConnectException {

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

    public VerificationTokenEntity getActiveVerificationToken(String token){
        return verificationTokenService.getActiveVerificationToken(token);
    }

    @Pattern(regexp = "\\s*$|^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",message = "Enter a valid email account")
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void updateToken(VerificationTokenEntity tokenEntity) {
        verificationTokenService.updateToken(tokenEntity);
    }
}
