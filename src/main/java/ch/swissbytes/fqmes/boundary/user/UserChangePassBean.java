package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.fqmes.control.enumService.EnumService;
import ch.swissbytes.fqmes.control.user.UserService;
import ch.swissbytes.fqmes.model.entities.UserEntity;
import ch.swissbytes.fqmes.types.RoleEnum;
import ch.swissbytes.fqmes.types.StatusEnum;
import ch.swissbytes.fqmes.util.Encode;
import org.omnifaces.util.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 19/09/14.
 */
@Named
@ViewScoped
public class UserChangePassBean implements Serializable {
    private static final Logger log = Logger.getLogger(UserChangePassBean.class.getName());

    @Inject
    private UserService userService;
    @Inject
    private EnumService enumService;
    @Inject
    private Identity identity;

    private UserEntity userSelected;
    private Integer userId;
    private String password;
    private String newPassword;


    @PostConstruct
    public void init(){
        log.info("create UserDeleteBean");
    }

    @PreDestroy
    public void destroy(){
        log.info("destroy UserDeleteBean");
    }

    public void loadUserSelected(){
        log.info("Loading User Entity....");
        User account=(User)identity.getAccount();
        userSelected =userService.getByUserName(account.getLoginName());
        log.info("UserSelected: " + userSelected.getFirstName());
    }

    public String doSave(){
        log.info("trying change password user");
        log.info("userSelected.getPassword(): " + userSelected.getPassword());
        log.info("old pass: " + password);
        log.info("new pass: " + newPassword);
        if(getEncodePass(password).equals(userSelected.getPassword())){
            userSelected.setPassword(getEncodePass(newPassword));
            userService.doUpdate(userSelected);
            Messages.addGlobalInfo("Password was changed!");
        }else{
            Messages.addGlobalInfo("Old password is different!");
        }
        return "";
    }

    private String getEncodePass(String pass){
        return Encode.encode(pass);
    }

    public List<RoleEnum> getRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        return roles;
    }

    public List<StatusEnum> getStatuses(){
        List<StatusEnum> statuses = new ArrayList<>();
        for(StatusEnum s : StatusEnum.values()){
            if(!s.equalsTo(StatusEnum.DELETED)){
                statuses.add(s);
            }
        }
        return statuses;
    }

    public UserEntity getUserSelected() {
        return userSelected;
    }

    public void setUserSelected(UserEntity userSelected) {
        this.userSelected = userSelected;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Size(min = 6, message = "Length must be greater than or equal to 6")
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
