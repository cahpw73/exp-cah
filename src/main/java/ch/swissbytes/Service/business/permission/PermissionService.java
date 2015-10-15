package ch.swissbytes.Service.business.permission;

import ch.swissbytes.Service.business.user.UserDao;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.PermissionGrantedEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.UserPermissionGrantedEntity;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Alvaro on 27/09/14.
 */
public class PermissionService implements Serializable {

    @Inject
    private PermissionDao dao;

    @Inject
    private UserDao userDao;

    private static final Logger log = Logger.getLogger(PermissionService.class.getName());

    public List<PermissionGrantedEntity> getPermissions(final List<Integer>roles){
      return  dao.getPermissionFor(roles);
    }

    public List<UserPermissionGrantedEntity> getUserPermissions(final Long userId){
        return dao.getUserPermissionFor(userId);
    }

    public List<RoleEntity> getRolesFor(final List<UserEntity> list){
        if(!list.isEmpty()){
            return dao.getRolesAssignedBy(list.get(0).getId());
        }
        return null;
    }

}
