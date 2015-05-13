package ch.swissbytes.Service.business.brand;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class BrandDao extends GenericDao<BrandEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(BrandDao.class.getName());


    public void doSave(BrandEntity brandEntity){
        super.save(brandEntity);
    }

    public void doUpdate(BrandEntity detachedEntity){
        BrandEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public List<BrandEntity> getBrandList(){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b ");
        sb.append(" FROM BrandEntity b ");
        sb.append(" WHERE b.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<BrandEntity> findByName(final String name){
        log.info("findByName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b ");
        sb.append(" FROM BrandEntity b ");
        sb.append(" WHERE LOWER(b.name) = :NAME ");
        sb.append(" AND b.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("NAME", name.toLowerCase().trim());
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<BrandEntity> findByLikeName(final String name){
        log.info("findByName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b ");
        sb.append(" FROM BrandEntity b ");
        sb.append(" WHERE LOWER(b.name) like :NAME ");
        sb.append(" AND b.status = :ENABLE ");
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
