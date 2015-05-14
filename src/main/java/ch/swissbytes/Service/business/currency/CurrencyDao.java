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

    public List<CurrencyEntity> getLogoList(){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM CurrencyEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("ORDER BY x.name ");
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
