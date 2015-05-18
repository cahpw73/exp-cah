package ch.swissbytes.procurement.boundary.User;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.role.RoleDao;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.model.entities.RoleEntity;
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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 15/05/2015.
 */
@Named
@ViewScoped
public class UserBean implements Serializable {

    public static final Logger log = Logger.getLogger(UserBean.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private EnumService enumService;

    @Inject
    private RoleDao roleDao;

    private UserEntity userEntity;

    private UserEntity editUser;

    private List<ModuleGrantedAccessEntity> moduleGrantedAccessList;

    private List<UserRoleEntity> userRoleList;

    private boolean isCreateUser;

    private boolean userActive;


    @PostConstruct
    public void init (){
        log.info("UserBean created");
        userEntity = new UserEntity();
        moduleGrantedAccessList = new ArrayList<>();
        userRoleList  = new ArrayList<>();
    }

    @PreDestroy
    public void destroy(){
        log.info("UserBean destroying");
    }

    public void loadActionCrud(){
        log.info("load action crud");
        log.info("parameter isCreateUser: " + isCreateUser);
        if(isCreateUser){
            ModuleGrantedAccessEntity moduleExpediting = new ModuleGrantedAccessEntity();
            ModuleGrantedAccessEntity moduleProcurement = new ModuleGrantedAccessEntity();
            UserRoleEntity userExpediting = new UserRoleEntity();
            UserRoleEntity userProcurement = new UserRoleEntity();
            moduleGrantedAccessList.add(moduleExpediting);
            moduleGrantedAccessList.add(moduleProcurement);
            userRoleList.add(userExpediting);
            userRoleList.add(userProcurement);
        }else{

        }
    }

    public String doSave(){
        log.info("do save");
        userEntity.setFirstName("");
        if(isUserActive())
            userEntity.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.ENABLE.getId()));
        else if (!isUserActive())
            userEntity.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.DISABLED.getId()));

        getModuleProcurement().setModuleSystem(ModuleSystemEnum.PROCUREMENT);
        getModuleExpediting().setModuleSystem(ModuleSystemEnum.EXPEDITING);
        getUserProcurement().setModuleSystem(ModuleSystemEnum.PROCUREMENT);
        getUserExpediting().setModuleSystem(ModuleSystemEnum.EXPEDITING);

        userService.doSaveUser(userEntity,moduleGrantedAccessList,userRoleList);
        return "list?faces-redirect=true";
    }

    public List<RoleEntity> getProcurementRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        List<RoleEntity> procurementRoles = new ArrayList<>();
        for(RoleEnum r : roles){
            switch (r){
                case FULL_WITH_COMMIT:
                    procurementRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                case FULL:
                    procurementRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                case RESTRICTED:
                    procurementRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                case READ_ONLY:
                    procurementRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
            }
        }
        return procurementRoles;
    }

    public List<RoleEntity> getExpeditingRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        List<RoleEntity> expeditingRoles = new ArrayList<>();
        for(RoleEnum r : roles){
            switch (r){
                case SENIOR:
                    expeditingRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                case JUNIOR:
                    expeditingRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
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

    public String getRoleName(final Integer roleId){
        return RoleEnum.valueOf(roleId).name();
    }

    public ModuleGrantedAccessEntity getModuleExpediting(){
        return moduleGrantedAccessList.get(0);
    }

    public ModuleGrantedAccessEntity getModuleProcurement(){
        return moduleGrantedAccessList.get(1);
    }

    public UserRoleEntity getUserExpediting(){
        return userRoleList.get(0);
    }

    public UserRoleEntity getUserProcurement(){
        return userRoleList.get(1);
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public boolean isCreateUser() {
        return isCreateUser;
    }

    public void setCreateUser(boolean isCreateUser) {
        this.isCreateUser = isCreateUser;
    }

    public boolean isUserActive() {
        return userActive;
    }

    public void setUserActive(boolean userActive) {
        this.userActive = userActive;
    }
}
