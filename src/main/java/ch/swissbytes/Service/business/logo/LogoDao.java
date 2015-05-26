package ch.swissbytes.Service.business.logo;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.LogoEntity;
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

public class LogoDao extends GenericDao<LogoEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(LogoDao.class.getName());


    public void doSave(LogoEntity brandEntity){
        super.save(brandEntity);
    }

    public void doUpdate(LogoEntity detachedEntity){
       super.update(detachedEntity);
    }

    public List<LogoEntity> getLogoList(){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM LogoEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("ORDER BY x.description ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }

   /* public List<BrandEntity> findByName(final String name){
        log.info("findByName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b ");
        sb.append(" FROM Brand b ");
        sb.append(" WHERE LOWER(b.name) like :NAME ");
        Map<String,Object> params = new HashMap<>();
        params.put("NAME", "%"+name.toLowerCase().trim()+"%");
        return super.findBy(sb.toString(),params);
    }*/

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


    public List<LogoEntity> findByFileName(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT l ");
        sb.append(" FROM LogoEntity l ");
        sb.append(" WHERE l.status = :ENABLE ");
        sb.append(" AND LOWER(l.fileName) = :FILE_NAME ");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("FILE_NAME", fileName.trim().toLowerCase());
        return super.findBy(sb.toString(),params);
    }

    public List<LogoEntity> findAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT l ");
        sb.append(" FROM LogoEntity l ");
        sb.append(" WHERE l.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }
}
