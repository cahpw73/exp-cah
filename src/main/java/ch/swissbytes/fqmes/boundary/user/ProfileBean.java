package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.fqmes.control.user.UserService;
import ch.swissbytes.fqmes.model.entities.UserEntity;
import org.omnifaces.util.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/19/14.
 */

@Named
@ViewScoped
public class ProfileBean implements Serializable {

    public static final Logger log = Logger.getLogger(ProfileBean.class.getName());

    @Inject
    private UserService service;

    @Inject
    private Identity identity;


    private UserEntity profile;

    @PostConstruct
    public void create(){
        log.info("create bean");
    }

    public void load(){
        User account=(User)identity.getAccount();
        profile =service.getByUserName(account.getLoginName());
    }
    private boolean dataValidate() {
        boolean result = true;
        if(service.validateDuplicityEmail(profile.getEmail(),profile.getId())){
            Messages.addError("userEditForm:inputEmail", "Email was already registered!");
            result = false;
        }
        if(service.validateDuplicityUsername(profile.getUsername(),profile.getId())){
            Messages.addError("userEditForm:inputUsername", "Username was already registered!");
            result = false;
        }
        return result;
    }
    @PreDestroy
    public void destroy(){
        log.info("destroying bean");
    }

    public String doUpdate(){
        if(dataValidate()){
            profile.setLastUpdate(new Date());
            service.doUpdate(profile);
            updateSessionData();
            return "/home?faces-redirect=true";
        }
        return "";
    }
    private void updateSessionData(){
        User account=(User)identity.getAccount();
        account.setLoginName(profile.getUsername());
        account.setFirstName(profile.getFirstName());
        account.setLastName(profile.getName());
        account.setEmail(profile.getEmail());
    }
    @Produces
    @Named
    @ViewScoped
    public UserEntity getProfile() {
        return profile;
    }
}
