package ch.swissbytes.procurement.boundary.profile;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.fqmes.util.Encode;
import ch.swissbytes.procurement.boundary.menu.MainMenuBean;
import org.omnifaces.util.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by Christian on 22/05/2015.
 */
@Named
@ViewScoped
public class UserProfileBean implements Serializable {

    public static final Logger log = Logger.getLogger(UserProfileBean.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private EnumService enumService;

    @Inject
    private MainMenuBean mainMenuBean;

    @Inject
    private Identity identity;

    private UserEntity userEntity;

    private Long userId;

    private String oldPassword;

    private String confirmPass;


    @PostConstruct
    public void init (){
        log.info("UserBean created");
        userEntity = new UserEntity();
        loadActionCrud();
    }

    @PreDestroy
    public void destroy(){
        log.info("UserBean destroying");
    }

    public void loadActionCrud(){
        log.info("load action crud");
        User user=(User)identity.getAccount();
        log.info("account: " + user.getLoginName());
        userEntity = userService.findByUsername(user.getLoginName());
        log.info("username: " + userEntity.getUsername());
        oldPassword = userEntity.getPassword();
        confirmPass = userEntity.getPassword();
    }

    public void doUpdate(){
        log.info("do update");
        if(dataValidateToUpdate()) {
            if(!oldPassword.equals(userEntity.getPassword())){
                userEntity.setPassword(getEncodePass(userEntity.getPassword()));
            }
            userService.doUpdate(userEntity);
            Messages.addFlashGlobalInfo("User has been updated.");
        }
    }

    private boolean dataValidateToUpdate() {
        boolean result = true;
        if(userService.validateDuplicityEmail(userEntity.getEmail(), userEntity.getId())){
            Messages.addError("email", "Email is already registered!");
            result = false;
        }
        if(userService.validateDuplicityUsername(userEntity.getUsername(), userEntity.getId())){
            Messages.addError("username", "Username is already registered!");
            result = false;
        }

        if(!userEntity.getPassword().equals(confirmPass)){
            Messages.addFlashError("pwd1", "Password should match with Confirm Password.");
            result = false;
        }
        return result;
    }


    private String getEncodePass(String pass){
        return Encode.encode(pass);
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }
}
