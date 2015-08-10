package ch.swissbytes.Service.business.text;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ClausesEntity;
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

public class TextClausesDao extends GenericDao<ClausesEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(TextClausesDao.class.getName());


    public void doSave(ClausesEntity entity){
        super.save(entity);
    }

    public void doUpdate(ClausesEntity detachedEntity){
       super.update(detachedEntity);
    }

    public void doDelete(ClausesEntity entity){
        super.delete(entity);
    }

    public List<ClausesEntity> findClausesByTextId(Long id) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM ClausesEntity x ");
        sb.append(" WHERE x.text.id = :TEXT_ID ");
        sb.append(" AND x.status=:ENABLED");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("TEXT_ID", id);
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }

    public List<ClausesEntity> findAll() {
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM TextEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }

    public List<ClausesEntity> findByTextId(Long textEntityId) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM ClausesEntity x ");
        sb.append(" WHERE x.text.id = :TEXT_ID ");
        sb.append(" ORDER BY x.id ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("TEXT_ID", textEntityId);
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
