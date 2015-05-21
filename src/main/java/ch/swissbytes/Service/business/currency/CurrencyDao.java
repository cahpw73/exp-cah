package ch.swissbytes.Service.business.currency;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
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

public class CurrencyDao extends GenericDao<CurrencyEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(CurrencyDao.class.getName());


    public void doSave(CurrencyEntity brandEntity){
        super.save(brandEntity);
    }

    public void doUpdate(CurrencyEntity detachedEntity){
       super.update(detachedEntity);
    }

    public List<CurrencyEntity> getCurrencyList(){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM CurrencyEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("ORDER BY x.name ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }

    public List<CurrencyEntity> findByNameButWithNoId(String name, Long id){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM CurrencyEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("AND trim(lower(x.name))=:NAME ");
        sb.append("AND NOT x.id=:CURRENCY_ID ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("NAME", name.toLowerCase().trim());
        map.put("CURRENCY_ID", id!=null?id:0L);
        return super.findBy(sb.toString(),map);
    }
    public List<CurrencyEntity> findByCodeButWithNoId(String code, Long id){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM CurrencyEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("AND trim(lower(x.code))=:CODE ");
        sb.append("AND NOT x.id=:CURRENCY_ID ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("CODE", code.toLowerCase().trim());
        map.put("CURRENCY_ID", id!=null?id:0L);
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
