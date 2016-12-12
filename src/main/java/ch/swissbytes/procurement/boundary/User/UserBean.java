package ch.swissbytes.procurement.boundary.User;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.role.RoleDao;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Encode;
import ch.swissbytes.procurement.boundary.menu.MainMenuBean;
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

    @Inject
    private ModuleGrantedAccessService moduleGrantedAccessService;

    @Inject
    private UserRoleService userRoleService;

    @Inject
    private MainMenuBean mainMenuBean;

    private UserEntity userEntity;

    private UserEntity editUser;

    private List<ModuleGrantedAccessEntity> moduleGrantedAccessList;

    private List<UserRoleEntity> userRoleList;

    private boolean isCreateUser;

    private boolean userActive = true;

    private Long userId;

    private String oldPassword;

    private RoleEntity roleExpediting;

    private RoleEntity roleProcurement;

    private boolean moduleAccessProcurement;

    private boolean moduleAccessExpediting;

    private String confirmPass;


    @PostConstruct
    public void init() {
        log.info("UserBean created");
        userEntity = new UserEntity();
        moduleGrantedAccessList = new ArrayList<>();
        userRoleList = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.info("UserBean destroying");
    }

    public void loadActionCrud() {
        log.info("load action crud");
        if (isCreateUser) {
            ModuleGrantedAccessEntity moduleExpediting = new ModuleGrantedAccessEntity();
            ModuleGrantedAccessEntity moduleProcurement = new ModuleGrantedAccessEntity();
            UserRoleEntity userExpediting = new UserRoleEntity();
            UserRoleEntity userProcurement = new UserRoleEntity();
            moduleGrantedAccessList.add(moduleExpediting);
            moduleGrantedAccessList.add(moduleProcurement);
            userRoleList.add(userExpediting);
            userRoleList.add(userProcurement);
        } else {
            userEntity = userService.findUserById(userId);
            oldPassword = userEntity.getPassword();
            confirmPass = userEntity.getPassword();

            moduleGrantedAccessList = moduleGrantedAccessService.findListByUserId(userId);
            if (moduleGrantedAccessList.size() == 1) {
                ModuleGrantedAccessEntity moduleProcurement = new ModuleGrantedAccessEntity();
                moduleProcurement.setUserEntity(userEntity);
                if(moduleGrantedAccessList.get(0).getModuleSystem().ordinal()==ModuleSystemEnum.EXPEDITING.ordinal()){
                    moduleProcurement.setModuleSystem(ModuleSystemEnum.PROCUREMENT);
                }else{
                    moduleProcurement.setModuleSystem(ModuleSystemEnum.EXPEDITING);
                }
                moduleGrantedAccessList.add(moduleProcurement);
            }
            userRoleList = userRoleService.findListByUserId(userId);
            if(userRoleList.size() == 1){
                UserRoleEntity userProcurement = new UserRoleEntity();
                userProcurement.setUser(userEntity);
                userProcurement.setUser(userEntity);
                if(userRoleList.get(0).getModuleSystem().ordinal() == ModuleSystemEnum.EXPEDITING.ordinal()){
                    userProcurement.setModuleSystem(ModuleSystemEnum.PROCUREMENT);
                }else{
                    userProcurement.setModuleSystem(ModuleSystemEnum.EXPEDITING);
                }
                userRoleList.add(userProcurement);
            }
            ModuleGrantedAccessEntity mga = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING);
            if (mga != null && mga.getModuleAccess() != null) {
                moduleAccessExpediting = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING).getModuleAccess();
            }
            ModuleGrantedAccessEntity moduleGrantedAccessProcurement = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.PROCUREMENT);
            if (moduleGrantedAccessProcurement != null && moduleGrantedAccessProcurement.getModuleAccess() != null) {
                moduleAccessProcurement = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.PROCUREMENT).getModuleAccess();
            }

            roleExpediting = userRoleService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING).getRole();
            if (userRoleService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.PROCUREMENT).getRole() != null) {
                roleProcurement = userRoleService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.PROCUREMENT).getRole();
            }

            if (StatusEnum.ENABLE.getId().intValue() == userEntity.getStatus().getId().intValue())
                userActive = true;
            else
                userActive = false;
        }
    }

    public String doSave() {
        log.info("do save");
        if (dataValidate()) {
            if (isUserActive())
                userEntity.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.ENABLE.getId()));
            else if (!isUserActive())
                userEntity.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.DISABLED.getId()));

            userEntity.setPassword(getEncodePass(userEntity.getPassword()));

            if (getModuleProcurement() != null) {
                getModuleProcurement().setModuleSystem(ModuleSystemEnum.PROCUREMENT);
            }
            getModuleExpediting().setModuleSystem(ModuleSystemEnum.EXPEDITING);

            if (getModuleProcurement() != null) {
                getModuleProcurement().setModuleAccess(moduleAccessProcurement);
            }
            getModuleExpediting().setModuleAccess(moduleAccessExpediting);

            getUserExpediting().setModuleSystem(ModuleSystemEnum.EXPEDITING);
            if (userRoleList.size() > 1) {
                getUserProcurement().setRole(roleProcurement);
                getUserProcurement().setModuleSystem(ModuleSystemEnum.PROCUREMENT);
            } else {
                UserRoleEntity ure = new UserRoleEntity();
                ure.setModuleSystem(ModuleSystemEnum.PROCUREMENT);
                ure.setRole(roleProcurement);
                ure.setUser(userEntity);
                userRoleList.add(ure);
            }
            getUserExpediting().setRole(roleExpediting);
            userService.doSaveUser(userEntity, moduleGrantedAccessList, userRoleList);
            return "list?faces-redirect=true";
        }
        mainMenuBean.select(2);
        return "";
    }

    public String doUpdate() {
        log.info("do update");
        if (dataValidateToUpdate()) {
            if (isUserActive())
                userEntity.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.ENABLE.getId()));
            else if (!isUserActive())
                userEntity.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.DISABLED.getId()));

            if (!oldPassword.equals(userEntity.getPassword())) {
                userEntity.setPassword(getEncodePass(userEntity.getPassword()));
            }
            updateModuleGrantedAccessListBeforeUpdate();
            updateRoleListBeforeUpdate();
            userService.doUpdateUser(userEntity, moduleGrantedAccessList, userRoleList);
            return "list?faces-redirect=true";
        }
        mainMenuBean.select(2);
        return "";
    }

    public void updateRoleListBeforeUpdate(){
        log.info("void updateRoleListBeforeUpdate()");
        for(UserRoleEntity u : userRoleList){
            if (roleProcurement != null && u.getModuleSystem().ordinal() == roleProcurement.getModuleSystem().ordinal()) {
                u.setRole(roleProcurement);
            }else if(u.getModuleSystem().ordinal() == ModuleSystemEnum.PROCUREMENT.ordinal()){
                u.setRole(null);
            }
            if(roleExpediting != null && u.getModuleSystem().ordinal() == roleExpediting.getModuleSystem().ordinal()){
                u.setRole(roleExpediting);
            }else if(u.getModuleSystem().ordinal() == ModuleSystemEnum.EXPEDITING.ordinal()){
                u.setRole(null);
            }
        }
    }

    public void updateModuleGrantedAccessListBeforeUpdate(){
        log.info("void updateModuleGrantedAccessListBeforeUpdate()");
        for(ModuleGrantedAccessEntity m : moduleGrantedAccessList){
            if (m.getModuleSystem().ordinal() == ModuleSystemEnum.PROCUREMENT.ordinal()) {
                m.setModuleAccess(moduleAccessProcurement);
            }
            if(m.getModuleSystem().ordinal() == ModuleSystemEnum.EXPEDITING.ordinal()){
                m.setModuleAccess(moduleAccessExpediting);
            }
        }
    }

    public List<RoleEntity> getProcurementRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        List<RoleEntity> procurementRoles = new ArrayList<>();
        for (RoleEnum r : roles) {
            switch (r) {
                case FULL_WITH_COMMIT:
                    procurementRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                case FULL:
                    procurementRoles.add(roleDao.findById(r.getId()).get(0));
                    break;
                /*case RESTRICTED:
                    procurementRoles.add(roleDao.findById(r.getId()).get(0));
                    break;*/
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
        for (RoleEnum r : roles) {
            switch (r) {
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

    public void resetRoleProcurement() {
        log.info("resetRoleProcurement()");
        log.info("moduleAccessProcurement = " + moduleAccessProcurement);
        if (!moduleAccessProcurement)
            roleProcurement = null;
    }

    public void resetRoleExpediting() {
        log.info("resetRoleExpediting()");
        log.info("moduleAccessExpediting = " + moduleAccessExpediting);
        if (!moduleAccessExpediting)
            roleExpediting = null;
    }

    private boolean dataValidate() {
        boolean result = true;
        if (userService.existsEmail(userEntity.getEmail())) {
            Messages.addFlashError("email", "Email was already registered!");
            result = false;
        }
        if (userService.existsUsername(userEntity.getUsername())) {
            Messages.addFlashError("username", "Username was already registered!");
            result = false;
        }
        if (roleProcurement == null && roleExpediting == null) {
            Messages.addFlashGlobalError("Must select at least one role");
            result = false;
        }
        if (!userEntity.getPassword().equals(confirmPass)) {
            Messages.addFlashError("pwd1", "Password should match with Confirm Password.");
            result = false;
        }
        return result;
    }

    private boolean dataValidateToUpdate() {
        boolean result = true;
        if (userService.validateDuplicityEmail(userEntity.getEmail(), userEntity.getId())) {
            Messages.addError("email", "Email was already registered!");
            result = false;
        }
        if (userService.validateDuplicityUsername(userEntity.getUsername(), userEntity.getId())) {
            Messages.addError("username", "Username was already registered!");
            result = false;
        }
        if (roleProcurement == null && roleExpediting == null) {
            Messages.addFlashGlobalError("Must select at least one role");
            result = false;
        }

        if (!userEntity.getPassword().equals(confirmPass)) {
            Messages.addFlashError("pwd1", "Password should match with Confirm Password.");
            result = false;
        }
        return result;
    }

    public String roleName(final Integer roleId) {
        return RoleEnum.valueOf(roleId).getLabel();
    }

    private String getEncodePass(String pass) {
        return Encode.encode(pass);
    }

    public String getRoleName(final Integer roleId) {
        return RoleEnum.valueOf(roleId).name();
    }

    public ModuleGrantedAccessEntity getModuleExpediting() {
        return moduleGrantedAccessList.size() > 1 ? moduleGrantedAccessList.get(0) : new ModuleGrantedAccessEntity();
    }

    public ModuleGrantedAccessEntity getModuleProcurement() {
        return moduleGrantedAccessList.size() > 1 ? moduleGrantedAccessList.get(1) : new ModuleGrantedAccessEntity();
    }

    public UserRoleEntity getUserExpediting() {
        return userRoleList.get(0);
    }

    public UserRoleEntity getUserProcurement() {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RoleEntity getRoleExpediting() {
        return roleExpediting;
    }

    public void setRoleExpediting(RoleEntity roleExpediting) {
        this.roleExpediting = roleExpediting;
    }

    public RoleEntity getRoleProcurement() {
        return roleProcurement;
    }

    public void setRoleProcurement(RoleEntity roleProcurement) {
        this.roleProcurement = roleProcurement;
    }

    public boolean isModuleAccessProcurement() {
        return moduleAccessProcurement;
    }

    public void setModuleAccessProcurement(boolean moduleAccessProcurement) {
        this.moduleAccessProcurement = moduleAccessProcurement;
    }

    public boolean isModuleAccessExpediting() {
        return moduleAccessExpediting;
    }

    public void setModuleAccessExpediting(boolean moduleAccessExpediting) {
        this.moduleAccessExpediting = moduleAccessExpediting;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }
}
