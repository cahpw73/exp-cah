package ch.swissbytes.procurement.boundary.User;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.fqmes.boundary.user.UserEditBean;

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
 * Created by Christian on 15/05/2015.
 */
@ViewScoped
@Named
public class UserBean implements Serializable {

    public static final Logger log = Logger.getLogger(UserBean.class.getName());

    @Inject
    private UserService userService;

    private UserEntity newUser;

    private UserEntity editUser;

    private List<ModuleGrantedAccessEntity> moduleGrantedAccessList;

    private List<UserRoleEntity> userRoleList;

    private boolean isCreateUser;

    private boolean activeUser;

    private boolean procurementAccess;

    private boolean expeditingAccess;

    @PostConstruct
    public void init (){
        log.info("UserBean created");
        newUser = new UserEntity();
        moduleGrantedAccessList = new ArrayList<>();
        userRoleList  = new ArrayList<>();
    }

    @PreDestroy
    public void destroy(){
        log.info("UserBean destroying");
    }

    public boolean isCreateUser() {
        return isCreateUser;
    }

    public void setCreateUser(boolean isCreateUser) {
        this.isCreateUser = isCreateUser;
    }
}
