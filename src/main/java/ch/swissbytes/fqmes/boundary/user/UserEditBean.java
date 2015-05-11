package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.StatusEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Encode;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
public class UserEditBean implements Serializable {

    private static final Logger log = Logger.getLogger(UserListBean.class.getName());

    @Inject
    private UserService userService;
    @Inject
    private EnumService enumService;

    private StatusEnum statusEnum;
    private RoleEnum roleEnum;

    private UserEntity userSelected;
    private Long userId;
    private Integer roleId;
    private Integer statusId;
    private String password;


    @PostConstruct
    public void init(){
        log.info("create UserEditBean");
    }

    @PreDestroy
    public void destroy(){
        log.info("destroy UserEditBean");
    }

    public String loadUserSelected(){
        log.info("Loading User EntityTbl....");
        if(userId != null){
            userSelected = userService.findUserById(userId);
            password = userSelected.getPassword();
            statusId = userSelected.getStatus().getId();
            statusEnum = StatusEnum.valueOf(userSelected.getStatus().getId());
            roleEnum = RoleEnum.valueOf(userSelected.getRoleEntity().getId());
        }else{
            return "list.jsf?faces-redirect=true";
        }
        return "";
    }

    public String doSave(){
        log.info("trying to edit user");
        if(dataValidate()){
            RoleEntity roleEntity = enumService.getFindRoleByRoleEnumId(roleEnum.getId());
            StatusEntity statusEntity = enumService.getFindRoleByStatusEnumId(statusEnum.getId());
            if(roleEntity != null || statusEntity != null){
                userSelected.setRoleEntity(roleEntity);
                userSelected.setStatus(statusEntity);
                if(!password.equals(userSelected.getPassword())){
                    userSelected.setPassword(getEncodePass(userSelected.getPassword()));
                }
                userService.doUpdate(userSelected);
                Messages.addFlashGlobalInfo("User was updated!");
                return "list?faces-redirect=true";
            }else{
                Messages.addGlobalError("There are data invalid! ");
                return "";
            }
        }
        return "";
    }

    private boolean dataValidate() {
        boolean result = true;
        if(userService.validateDuplicityEmail(userSelected.getEmail(),userSelected.getId())){
            //TODO get Message from MessagesProvider
            Messages.addError("userEditForm:inputEmail", "Email was already registered!");
            result = false;
        }
        if(userService.validateDuplicityUsername(userSelected.getUsername(),userSelected.getId())){
            //TODO get Message from MessagesProvider
            Messages.addError("userEditForm:inputUsername", "Username was already registered!");
            result = false;
        }
        return result;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public RoleEnum getRoleEnum() {
        return roleEnum;
    }

    public void setRoleEnum(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }
}
