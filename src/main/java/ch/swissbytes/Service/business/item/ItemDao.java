package ch.swissbytes.Service.business.item;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
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

public class ItemDao extends GenericDao<ScopeSupplyEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ItemDao.class.getName());



    public void doSave(ScopeSupplyEntity entity){
        super.saveAndFlush(entity);
    }

    public void doUpdate(ScopeSupplyEntity detachedEntity){
        super.update(detachedEntity);
    }

    public List<ItemEntity> findAll() {
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM ItemEntity x ");
        sb.append("WHERE x.statusEnum=:ENABLED ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }

    public List<ScopeSupplyEntity> findByPoId(Long poEntityId) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM ScopeSupplyEntity x ");
        sb.append(" WHERE x.status.id = :ENABLED ");
        sb.append(" AND x.purchaseOrder.id = :PO_ID ");
        sb.append(" ORDER BY x.id ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
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
