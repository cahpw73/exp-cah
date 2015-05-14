package ch.swissbytes.Service.business.category;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.CategoryEntity;
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

public class CategoryDao extends GenericDao<CategoryEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(CategoryDao.class.getName());


    public void doSave(CategoryEntity categoryEntity){
        super.save(categoryEntity);
    }

    public void doUpdate(CategoryEntity detachedEntity){
        CategoryEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public List<CategoryEntity> getBrandList(){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT c ");
        sb.append(" FROM CategoryEntity c ");
        sb.append(" WHERE c.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<CategoryEntity> findByName(final String name){
        log.info("findByName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT c ");
        sb.append(" FROM CategoryEntity c ");
        sb.append(" WHERE LOWER(c.name) = :NAME ");
        sb.append(" AND c.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("NAME", name.toLowerCase().trim());
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<CategoryEntity> findByLikeName(final String name){
        log.info("findByLikeName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT c ");
        sb.append(" FROM CategoryEntity c ");
        sb.append(" WHERE LOWER(c.name) like :NAME ");
        sb.append(" AND c.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("NAME", "%"+name.toLowerCase().trim()+"%");
        params.put("ENABLE", StatusEnum.ENABLE);
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
