package ch.swissbytes.procurement.boundary.User;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.model.entities.StatusEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 19/05/2015.
 */
@Named
@ViewScoped
public class UsersBean implements Serializable {

    public static final Logger log = Logger.getLogger(UsersBean.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private UserRoleService userRoleService;

    @Inject
    private ModuleGrantedAccessService moduleGrantedAccessService;

    @Inject
    private EnumService enumService;

    private List<UserEntity> userList;

    @PostConstruct
    public void init (){
        log.info("UserListBean was created");
        userList = new ArrayList<>();
        loadUsers();
    }

    @PreDestroy
    public void destroy(){
        log.info("UserListBean destroying");
    }

    public void loadUsers(){
        userList = userService.findAllUser();
    }

    public boolean getActiveUser(final StatusEntity status){
        return (StatusEnum.ENABLE.getId().intValue() == status.getId().intValue());
    }

    public boolean getAccessModuleProcurement(final Long userId){
        ModuleGrantedAccessEntity moduleProcurement = moduleGrantedAccessService.findByUserId(userId,ModuleSystemEnum.PROCUREMENT);
        boolean hasAccess = false;
        if(moduleProcurement != null){
            hasAccess = moduleProcurement.getModuleAccess();
        }
        return hasAccess;
    }

    public boolean getAccessModuleExpediting(final Long userId){
        ModuleGrantedAccessEntity moduleProcurement = moduleGrantedAccessService.findByUserId(userId,ModuleSystemEnum.EXPEDITING);
        boolean hasAccess = false;
        if(moduleProcurement != null){
            hasAccess = moduleProcurement.getModuleAccess();
        }
        return hasAccess;
    }

    public String getAccessLevelProcurement(final Long userId){
        UserRoleEntity userRole = userRoleService.findByUserId(userId, ModuleSystemEnum.PROCUREMENT);
        if(userRole != null) {
            RoleEnum roleEnum = RoleEnum.of(userRole.getRole());
            return roleEnum.getLabel();
        }else{
            return "";
        }
    }

    public String getAccessLevelExpediting(final Long userId){
        UserRoleEntity userRole = userRoleService.findByUserId(userId, ModuleSystemEnum.EXPEDITING);
        if(userRole != null) {
            RoleEnum roleEnum = RoleEnum.of(userRole.getRole());
            return roleEnum.name();
        }else{
            return "";
        }
    }

    public List<UserEntity> getUserList() {
        return userList;
    }

}
