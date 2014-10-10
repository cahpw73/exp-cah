package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.model.entities.ScopeSupplyEntity;
import ch.swissbytes.fqmes.model.entities.TransitDeliveryPointEntity;
import ch.swissbytes.fqmes.types.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 9/12/14.
 */

public class ScopeSupplyDao extends GenericDao<ScopeSupplyEntity> implements Serializable {

    public void persist(List<ScopeSupplyEntity> list,PurchaseOrderEntity purchaseOrderEntity){
        for(ScopeSupplyEntity sse: list){
            sse.setPurchaseOrder(purchaseOrderEntity);
            super.save(sse);
            for (TransitDeliveryPointEntity tdp: sse.getTdpList()){
                tdp.setScopeSupply(sse);
                entityManager.persist(tdp);
            }
        }
    }

    public ScopeSupplyEntity load(final Long id){
        return super.load(ScopeSupplyEntity.class,id);
    }

    public List<ScopeSupplyEntity> findByPurchaseOrder(final Long purchaseOrderId){
        String hql = "SELECT ss FROM ScopeSupplyEntity ss where ss.purchaseOrder.id=:purchase_id AND  ss.status.id<>:DELETED ORDER BY ss.id DESC" ;
        TypedQuery<ScopeSupplyEntity> query = this.entityManager.createQuery(
                hql, ScopeSupplyEntity.class);
        query.setParameter("purchase_id", purchaseOrderId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        return query.getResultList();

    }
    public void update(List<ScopeSupplyEntity>scopeSupplyEntities,PurchaseOrderEntity purchaseOrderEntity){
        for(ScopeSupplyEntity scopeSupplyEntity:scopeSupplyEntities){
            if(scopeSupplyEntity.getId()==null){//new one{
                scopeSupplyEntity.setPurchaseOrder(purchaseOrderEntity);
                super.save(scopeSupplyEntity);
            }else{
                super.update(scopeSupplyEntity);
            }

        }
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
