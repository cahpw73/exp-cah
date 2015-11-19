package ch.swissbytes.Service.business.cashflow;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.CashflowDetailEntity;
import ch.swissbytes.domain.model.entities.CashflowEntity;
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

public class CashflowDetailDao extends GenericDao<CashflowDetailEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(CashflowDetailDao.class.getName());


    public void doSave(CashflowDetailEntity entity){
        super.save(entity);
    }

    public void doUpdate(CashflowDetailEntity detachedEntity){
       super.update(detachedEntity);
    }

    public List<CashflowDetailEntity> findAll() {
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM CashflowDetailEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }

    public List<CashflowDetailEntity> findByCashflowId(Long cashId) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM CashflowDetailEntity x ");
        sb.append(" WHERE x.cashflowEntity.id = :CASH_ID ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("CASH_ID", cashId);
        return super.findBy(sb.toString(),map);
    }

    public List<CashflowDetailEntity> findOrderedByCurrencyAndItem(Long cashId) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM CashflowDetailEntity x ");
        sb.append(" WHERE x.cashflowEntity.id = :CASH_ID ");
        sb.append(" ORDER BY x.projectCurrency.id,x.ordered ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("CASH_ID", cashId);
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
