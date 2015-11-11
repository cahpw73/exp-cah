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
public class UserDeleteBean implements Serializable {

    private static final Logger log = Logger.getLogger(UserDeleteBean.class.getName());

    @Inject
    private UserService userService;
    @Inject
    private EnumService enumService;

    private StatusEnum statusEnum;
    private RoleEnum roleEnum;

    private UserEntity userSelected;
    private Integer userId;
    private Integer roleId;
    private Integer statusId;
    private String password;


    @PostConstruct
    public void init() {
        log.info("create UserDeleteBean");
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy UserDeleteBean");
    }

    public void loadUserSelected(final Long id) {
        log.info("Loading User EntityTbl....");
        userSelected = userService.findUserById(id);
        password = userSelected.getPassword();
        userId = userSelected.getStatus().getId();
        statusEnum = StatusEnum.valueOf(userSelected.getStatus().getId());
        log.info("UserSelected: " + userSelected.getFirstName());
    }

    public String doDelete() {
        log.info("trying to delete user");
        StatusEntity statusEntity = enumService.getFindRoleByStatusEnumId(StatusEnum.DELETED.getId());
        if (statusEntity != null) {
            userSelected.setStatus(statusEntity);
            userService.doUpdate(userSelected);
            Messages.addFlashGlobalInfo("User was delete!");
            return "list?faces-redirect=true";
        } else {
            Messages.addGlobalError("There are data invalid! ");
            return "";
        }
    }

    public List<RoleEnum> getRoles() {
        List<RoleEnum> roles = Arrays.asList(RoleEnum.values());
        return roles;
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
