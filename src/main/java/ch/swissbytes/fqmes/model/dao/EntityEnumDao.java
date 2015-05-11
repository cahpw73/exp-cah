package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.domain.repository.GenericDao;
import ch.swissbytes.domain.repository.Filter;
import ch.swissbytes.domain.repository.entities.RoleEntity;
import ch.swissbytes.domain.repository.entities.StatusEntity;
import ch.swissbytes.domain.repository.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Created by christian on 15/09/14.
 */
public class EntityEnumDao extends GenericDao implements Serializable {

    public List<RoleEntity> getRoleEntityByEnumId(final Integer id){
        return  findById(RoleEntity.class,id);
    }

    public List<StatusEntity> getStatusEntityByEnumId(final Integer id){
        return  findById(StatusEntity.class,id);
    }

    public List<StatusEntity> getStatusEntityEnable() {
        return  findById(StatusEntity.class, StatusEnum.ENABLE.getId());
    }

    public StatusEntity getStatusDeleted(){
        return  (StatusEntity)(findById(StatusEntity.class, StatusEnum.DELETED.getId()).get(0));
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
