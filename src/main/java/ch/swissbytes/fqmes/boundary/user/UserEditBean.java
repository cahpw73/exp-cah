package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.role.RoleDao;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ModuleSystemEnum;
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

    @Inject
    private RoleDao roleDao;

    @Inject
    private ModuleGrantedAccessService moduleGrantedAccessService;

    @Inject
    private UserRoleService userRoleService;

    private RoleEntity roleExpediting;

    private List<ModuleGrantedAccessEntity> moduleGrantedAccessList;

    private List<UserRoleEntity> userRoleList;

    private StatusEnum statusEnum;
    private RoleEnum roleEnum;

    private UserEntity userSelected;
    private Long userId;
    private Integer roleId;
    private Integer statusId;
    private String password;

    private String oldPassword;
    private String confirmPass;

    private boolean moduleAccessExpediting;


    @PostConstruct
    public void init() {
        log.info("create UserEditBean");
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy UserEditBean");
    }

    public String loadUserSelected() {
        log.info("Loading User EntityTbl....");
        if (userId != null) {
            userSelected = userService.findUserById(userId);
            statusEnum = StatusEnum.valueOf(userSelected.getStatus().getId());
            oldPassword = userSelected.getPassword();
            confirmPass = userSelected.getPassword();

            moduleGrantedAccessList = moduleGrantedAccessService.findListByUserId(userId);
            userRoleList = userRoleService.findListByUserId(userId);
            ModuleGrantedAccessEntity mga = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING);
            if (mga != null && mga.getModuleAccess() != null) {
                moduleAccessExpediting = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING).getModuleAccess();
            }
            roleExpediting = userRoleService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING).getRole();

        } else {
            return "list.jsf?faces-redirect=true";
        }
        return "";
    }

    public String doSave() {
        log.info("trying to edit user");
        if (dataValidate()) {
            if (statusEnum.ordinal() == StatusEnum.ENABLE.ordinal())
                userSelected.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.ENABLE.getId()));
            else if (statusEnum.ordinal() == StatusEnum.DISABLED.ordinal())
                userSelected.setStatus(enumService.getStatusEntityByStatusEnumId(StatusEnum.DISABLED.getId()));

            if (!oldPassword.equals(userSelected.getPassword())) {
                userSelected.setPassword(getEncodePass(userSelected.getPassword()));
            }
           /* if(!moduleGrantedAccessList.isEmpty()) {
                getModuleExpediting().setModuleAccess(moduleAccessExpediting);
            }*/
            if (roleExpediting != null) {
                getUserExpediting().setRole(roleExpediting);
                getModuleExpediting().setModuleAccess(true);
            }

            userService.doUpdateUser(userSelected, moduleGrantedAccessList, userRoleList);

            return "list?faces-redirect=true";

        } else {
            Messages.addGlobalError("There are data invalid! ");
            return "";
        }
    }

    private boolean dataValidate() {
        boolean result = true;
        if (userService.validateDuplicityEmail(userSelected.getEmail(), userSelected.getId())) {
            Messages.addError("userEditForm:inputEmail", "Email was already registered!");
            result = false;
        }
        if (userService.validateDuplicityUsername(userSelected.getUsername(), userSelected.getId())) {
            Messages.addError("userEditForm:inputUsername", "Username was already registered!");
            result = false;
        }
        return result;
    }

    private String getEncodePass(String pass) {
        return Encode.encode(pass);
    }

    public List<RoleEnum> getRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        return roles;
    }

    public List<StatusEnum> getStatuses() {
        List<StatusEnum> statuses = new ArrayList<>();
        for (StatusEnum s : StatusEnum.values()) {
            if (!s.equalsTo(StatusEnum.DELETED)) {
                statuses.add(s);
            }
        }
        return statuses;
    }

    public List<RoleEntity> getExpeditingRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        List<RoleEntity> expeditingRoles = new ArrayList<>();
        for (RoleEnum r : roles) {
            switch (r) {
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

    public RoleEntity getRoleExpediting() {
        return roleExpediting;
    }

    public void setRoleExpediting(RoleEntity roleExpediting) {
        this.roleExpediting = roleExpediting;
    }

    public String roleName(final Integer roleId) {
        return RoleEnum.valueOf(roleId).getLabel();
    }

    public ModuleGrantedAccessEntity getModuleExpediting() {
        return moduleGrantedAccessList.get(0);
    }

    public UserRoleEntity getUserExpediting() {
        return userRoleList.get(0);
    }
}
