package ch.swissbytes.Service.business.role;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 18/05/15.
 */

public class RoleDao extends GenericDao<BrandEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(RoleDao.class.getName());

    public List<RoleEntity> findById(final Integer id){
        return super.findById(RoleEntity.class, id);
    }

    public List<RoleEntity> findByName(final String name){
        log.info("findByName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT r ");
        sb.append(" FROM RoleEntity r ");
        sb.append(" WHERE LOWER(r.name) = :NAME ");
        Map<String,Object> params = new HashMap<>();
        params.put("NAME", name.toLowerCase().trim());
        return super.findBy(sb.toString(),params);
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
