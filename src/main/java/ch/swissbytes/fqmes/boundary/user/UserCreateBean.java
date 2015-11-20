package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.role.RoleDao;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.StatusEnum;
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

    @Inject
    private RoleDao roleDao;

    private UserEntity userEntity;

    private Integer statusId;

    private RoleEntity roleExpediting;

    private List<ModuleGrantedAccessEntity> moduleGrantedAccessList;

    private List<UserRoleEntity> userRoleList;

    @PostConstruct
    private void init(){
        userEntity = new UserEntity();
        moduleGrantedAccessList = new ArrayList<>();
        userRoleList  = new ArrayList<>();
        ModuleGrantedAccessEntity moduleExpediting = new ModuleGrantedAccessEntity();
        UserRoleEntity userExpediting = new UserRoleEntity();

        moduleGrantedAccessList.add(moduleExpediting);
        userRoleList.add(userExpediting);
    }

    @PreDestroy
    private void destroy(){

    }

    public String doSave(){
        log.info("trying to create user");
        if(dataValidate()){
            userEntity.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.ENABLE.getId()));
            userEntity.setPassword(getEncodePass(userEntity.getPassword()));
            getModuleExpediting().setModuleSystem(ModuleSystemEnum.EXPEDITING);
            getModuleExpediting().setModuleAccess(true);
            getUserExpediting().setModuleSystem(ModuleSystemEnum.EXPEDITING);
            getUserExpediting().setRole(roleExpediting);
            userService.doSaveUser(userEntity, moduleGrantedAccessList, userRoleList);
            return "list?faces-redirect=true";
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

    public List<RoleEntity> getExpeditingRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        List<RoleEntity> expeditingRoles = new ArrayList<>();
        for(RoleEnum r : roles){
            switch (r){
                case SENIOR:
                    expeditingRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                /*case JUNIOR:
                    expeditingRoles.add(roleDao.findById(r.getId()).get(0));
                    break;*/
                case VISITOR:
                    expeditingRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                case ADMINISTRATOR:
                    expeditingRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
            }
        }
        return expeditingRoles;
    }

    public ModuleGrantedAccessEntity getModuleExpediting(){
        return moduleGrantedAccessList.get(0);
    }

    public UserRoleEntity getUserExpediting(){
        return userRoleList.get(0);
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

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public RoleEntity getRoleExpediting() {
        return roleExpediting;
    }

    public void setRoleExpediting(RoleEntity roleExpediting) {
        this.roleExpediting = roleExpediting;
    }

    public String roleName(final Integer roleId){
        return RoleEnum.valueOf(roleId).getLabel();
    }
}
