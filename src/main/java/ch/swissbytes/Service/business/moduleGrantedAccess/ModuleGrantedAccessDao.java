package ch.swissbytes.Service.business.moduleGrantedAccess;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
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

public class ModuleGrantedAccessDao extends GenericDao<ModuleGrantedAccessEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ModuleGrantedAccessDao.class.getName());


    public void doSave(ModuleGrantedAccessEntity moduleGrantedAccessEntity){
        super.save(moduleGrantedAccessEntity);
    }

    public void doUpdate(ModuleGrantedAccessEntity detachedEntity){
        ModuleGrantedAccessEntity entity = super.merge(detachedEntity);
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
