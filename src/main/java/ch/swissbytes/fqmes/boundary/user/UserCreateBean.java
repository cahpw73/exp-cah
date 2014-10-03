package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.fqmes.control.enumService.EnumService;
import ch.swissbytes.fqmes.control.user.UserService;
import ch.swissbytes.fqmes.model.entities.RoleEntity;
import ch.swissbytes.fqmes.model.entities.StatusEntity;
import ch.swissbytes.fqmes.model.entities.UserEntity;
import ch.swissbytes.fqmes.types.RoleEnum;
import ch.swissbytes.fqmes.types.StatusEnum;
import ch.swissbytes.fqmes.util.Encode;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 12/09/14.
 */
@Named
@RequestScoped
public class UserCreateBean implements Serializable {

    public static final Logger log = Logger.getLogger(UserCreateBean.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private EnumService enumService;

    private UserEntity userEntity;

    private Integer roleId;

    private String roleName;

    private Integer statusId;

    @PostConstruct
    private void init(){
        userEntity = new UserEntity();

    }

    @PreDestroy
    private void destroy(){

    }

    public String doSave(){
        log.info("trying to create user");
        if(dataValidate()){
            RoleEntity roleEntity = enumService.getFindRoleByRoleEnumId(RoleEnum.valueOf(roleName).getId());
            StatusEntity statusEntity = enumService.getFindRoleByStatusEnumId(statusId);
            if(roleEntity != null){
                userEntity.setRoleEntity(roleEntity);
                userEntity.setStatus(statusEntity);
                userEntity.setPassword(getEncodePass(userEntity.getPassword()));
                userService.doSave(userEntity);
                return "list?faces-redirect=true";
            }else{
                Messages.addGlobalError("RoleName not exist");
                return "";
            }
        }
        return "";
    }

    private boolean dataValidate() {
        boolean result = true;
        if(userService.existsEmail(userEntity.getEmail())){
            //TODO get Message from MessagesProvider
            Messages.addError("userCreateForm:inputEmail", "Email was already registered!");
            result = false;
        }
        if(userService.existsUsername(userEntity.getUsername())){
            //TODO get Message from MessagesProvider
            Messages.addError("userCreateForm:inputUsername", "Username was already registered!");
            result = false;
        }
        return result;
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

    private String getEncodePass(String pass){
        return Encode.encode(pass);
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
}
