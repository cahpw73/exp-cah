package ch.swissbytes.Service.business.text;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.TextEntity;
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

public class TextDao1 extends GenericDao<TextEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(TextDao1.class.getName());


    public void doSave(TextEntity entity){
        super.save(entity);
    }

    public void doUpdate(TextEntity detachedEntity){
       super.update(detachedEntity);
    }

    public List<TextEntity> findAll() {
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM TextEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }

    public List<TextEntity> findByPoId(Long poEntityId) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM TextEntity x ");
        sb.append(" WHERE x.status = :ENABLED ");
        sb.append(" AND x.po.id = :PO_ID ");
        sb.append(" ORDER BY x.id ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("PO_ID", poEntityId);
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
