package ch.swissbytes.Service.business.poitem;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.PoItemEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by christian on 9/10/14.
 */

public class PoItemDao extends GenericDao<PoItemEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(PoItemDao.class.getName());


    public void doSave(PoItemEntity entity){
        super.save(entity);
    }

    public void doUpdate(PoItemEntity detachedEntity){
       super.update(detachedEntity);
    }

    public List<PoItemEntity> findAll() {
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM PoItemEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
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
