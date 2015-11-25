package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.Service.business.VerificationToken.VerificationTokenService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.VerificationTokenEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.CreateEmailSender;
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

    @Inject
    private CreateEmailSender createEmailSender;

    private UserEntity userEntity;

    private String mail;


    public String sendLostPasswordToken()   {
        log.info("Sending token to email without parameter email");
        if(isValidEmail(mail)){
            log.info("user is not null");
            VerificationTokenEntity tokenEntity = verificationTokenService.getVerificationTokenGenerated(userEntity);
            log.info("token is not null");
            if (tokenEntity != null) {
                createEmailSender.createEmailAndSend(userEntity,tokenEntity);
                return "login?faces-redirect=true";
            }
        }
        return "failed";
    }

    public String sendLostPasswordToken(final String email)   {
        log.info("Sending token to email with parameter email: " + email);
        if(isValidEmail(email)){
            log.info("user is not null");
            VerificationTokenEntity tokenEntity = verificationTokenService.getVerificationTokenGenerated(userEntity);
            log.info("token is not null");
            if (tokenEntity != null) {
                createEmailSender.createEmailAndSend(userEntity,tokenEntity);
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


    private UserEntity getUserByEmail(String email) {
        return userService.findUserByEmail(email).get(0);
    }

    private String getEmailMessage(){
        return "Reset password";
    }

    private String getEmailGreetings(){
        return "Reset Password";
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
