package ch.swissbytes.Service.business.enumService;

import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.StatusEntity;
import ch.swissbytes.domain.types.StatusEnum;

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
