package ch.swissbytes.fqmes.control.permission;

import ch.swissbytes.fqmes.model.dao.PermissionDao;
import ch.swissbytes.fqmes.model.dao.UserDao;
import ch.swissbytes.domain.repository.entities.PermissionGrantedEntity;
import ch.swissbytes.domain.repository.entities.RoleEntity;
import ch.swissbytes.domain.repository.entities.UserEntity;

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

    public List<RoleEntity> getRolesFor(final String userName){
        log.info("public List<RoleEntity> getRolesFor(final String userName="+userName+")");
        List<UserEntity> list=userDao.findUserByUserName(userName);
        if(!list.isEmpty()){
            return dao.getRolesAssignedBy(list.get(0).getId());
        }
        return null;
    }

}
