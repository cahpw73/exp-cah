package ch.swissbytes.fqmes.control.enumService;

import ch.swissbytes.fqmes.model.dao.EntityEnumDao;
import ch.swissbytes.fqmes.model.entities.RoleEntity;
import ch.swissbytes.fqmes.model.entities.StatusEntity;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by christian on 15/09/14.
 */
public class EnumService implements Serializable {

    @Inject
    private EntityEnumDao entityEnumDao;

    public RoleEntity getFindRoleByRoleEnumId(final Integer id){
        return entityEnumDao.getRoleEntityByEnumId(id).get(0);
    }

    public StatusEntity getFindRoleByStatusEnumId(final Integer id){
        return entityEnumDao.getStatusEntityByEnumId(id).get(0);
    }

    public StatusEntity getStatusEnumEnable() {
        return entityEnumDao.getStatusEntityEnable().get(0);
    }
    public StatusEntity getStatusEnumDeleted() {
        return entityEnumDao.getStatusDeleted();
    }
}
