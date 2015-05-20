package ch.swissbytes.Service.business.enumService;

import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.StatusEntity;

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

    public StatusEntity getStatusEntityByStatusEnumId(final Integer id){
        return entityEnumDao.getStatusEntityByEnumId(id).get(0);
    }

    public StatusEntity getStatusEnumEnable() {
        return entityEnumDao.getStatusEntityEnable().get(0);
    }
    public StatusEntity getStatusEnumDeleted() {
        return entityEnumDao.getStatusDeleted();
    }
}
