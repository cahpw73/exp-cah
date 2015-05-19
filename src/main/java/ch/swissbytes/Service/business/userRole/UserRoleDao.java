package ch.swissbytes.Service.business.userRole;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
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

    public List<UserRoleEntity> findByUserIdAndModuleSystem(final Long userId, final ModuleSystemEnum moduleSystem) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ur ");
        sb.append(" FROM UserRoleEntity ur ");
        sb.append(" WHERE ur.user.id = :USER_ID ");
        sb.append(" AND ur.moduleSystem = :MODULE_SYSTEM ");
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("USER_ID",userId);
        parameters.put("MODULE_SYSTEM",moduleSystem);
        return super.findBy(sb.toString(),parameters);
    }

    public List<UserRoleEntity> findByUserId(Long userId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ur ");
        sb.append(" FROM UserRoleEntity ur ");
        sb.append(" WHERE ur.user.id = :USER_ID ");
        sb.append(" ORDER BY ur.user.id ASC ");
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("USER_ID",userId);
        return super.findBy(sb.toString(),parameters);
    }
}
