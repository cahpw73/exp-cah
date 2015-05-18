package ch.swissbytes.Service.business.userRole;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class UserRoleDao extends GenericDao<UserRoleEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(UserRoleDao.class.getName());


    public void doSave(UserRoleEntity userRoleEntity){
        super.saveAndFlush(userRoleEntity);
    }

    public void doUpdate(UserRoleEntity detachedEntity){
        UserRoleEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }
}
