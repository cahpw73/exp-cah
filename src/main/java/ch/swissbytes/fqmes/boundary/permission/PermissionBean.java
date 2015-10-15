package ch.swissbytes.fqmes.boundary.permission;


import ch.swissbytes.Service.business.permission.PermissionService;
import ch.swissbytes.Service.business.permission.PermissionDao;
import ch.swissbytes.Service.business.user.UserDao;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.PermissionGrantedEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.UserPermissionGrantedEntity;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


/**
 * @author Christian
 */
@Named
@ViewScoped
public class PermissionBean implements Serializable {

    @Inject
    private Identity identity;

    @Inject
    private PermissionDao dao;

    @Inject
    private PermissionService service;

    @Inject
    private UserDao userDao;

    private Logger log = Logger.getLogger(PermissionBean.class.getName());

    private List<PermissionGrantedEntity> permissions;

    private List<UserPermissionGrantedEntity> userPermissions;


    private Date initialDate;

    @PostConstruct
    public void init() {
        initialDate = new Date();
        User user = (User) identity.getAccount();
        List<UserEntity> list = userDao.findUserByUserName(user.getLoginName());
        List<RoleEntity> currentRoles = service.getRolesFor(list);
        permissions = service.getPermissions(collectIds(currentRoles));
        userPermissions = service.getUserPermissions(list.get(0).getId());
    }

    public boolean hasPermission(Integer option) {
        boolean granted = false;
        for (PermissionGrantedEntity permission : permissions) {
            if (permission.getOptions().getId().intValue() == option.intValue()) {
                granted = true;
                break;
            }
        }
        return granted;
    }

    public boolean hasUserPermission(Integer option){
        boolean granted = false;
        for (UserPermissionGrantedEntity permission : userPermissions) {
            if (permission.getOptions().getId().intValue() == option.intValue()) {
                granted = true;
                break;
            }
        }
        return granted;
    }

    public String canAccess(Integer option) {
        return hasPermission(option) ? "GRANTED" : "ACCESS_DENIED";
    }

    private List<Integer> collectIds(final List<RoleEntity> roles) {
        List<Integer> ids = new ArrayList<>();
        for (RoleEntity role : roles) {
            ids.add(role.getId());
        }
        return ids;
    }


    @PreDestroy
    public void destroy() {
        Date endDate = new Date();
        Long diff = endDate.getTime() - initialDate.getTime();
        log.info(String.format("duration time for permission loading [%s]", diff.longValue()));
    }


}
