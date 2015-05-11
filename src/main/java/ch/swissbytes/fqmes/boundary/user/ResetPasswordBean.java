package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.fqmes.control.user.VerificationTokenService;
import ch.swissbytes.fqmes.control.user.UserService;
import ch.swissbytes.domain.repository.entities.UserEntity;
import ch.swissbytes.domain.repository.entities.VerificationTokenEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Encode;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ResetPasswordBean implements Serializable{

    private static final Logger log = Logger.getLogger(ResetPasswordBean.class.getName());


    @Inject
    private UserService userService;

    @Inject
    private Configuration configuration;

    private String newPassword;
    private String token;
    private UserEntity user;
    private boolean tokenActive;
    private VerificationTokenEntity tokenEntity;

    @Inject
    private VerificationTokenService tokenService;


    @PostConstruct
    public void init(){

    }

    @PreDestroy
    public void destroy(){

    }


    public String resetPass(){
        log.info("resetting password....");
        String newPassEncoded = Encode.encode(newPassword);
        tokenEntity.setVerified(true);
        tokenService.updateToken(tokenEntity);
        user.setPassword(newPassEncoded);
        userService.doUpdate(user);
        Messages.addFlashGlobalInfo("Your password has been changed successfully");
        return "login?faces-redirect=true";
    }

    public String verifyToken() {
        log.info("String verifying token...");
        tokenEntity = tokenService.getActiveVerificationToken(token);
        if (tokenEntity != null){
            log.info("TokenEntity is not null");
            if(!tokenEntity.isVerified()){
                log.info("tokenEntity is not verify");
                if(!hasExpired(tokenEntity)){
                    log.info("tokenEntity is not expired");
                    this.user = tokenEntity.getUser();
                    tokenActive = !tokenEntity.isVerified();
                    return "success";
                }else{
                    Messages.addGlobalError(configuration.getMessage("reset.expired"));
                }
            }else{
                Messages.addGlobalError(configuration.getMessage("reset.token.used"));
            }
        }
        return "failed";
    }

    private boolean hasExpired(VerificationTokenEntity entity) {
        Date tokenDate = new Date();
        return tokenDate.after(entity.getExpirationDate());
    }

    @Size(min = 6, message = "Length must be greater than or equal to 6")
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean isTokenActive() {
        return tokenActive;
    }

    public void setTokenActive(boolean tokenActive) {
        this.tokenActive = tokenActive;
    }
}
