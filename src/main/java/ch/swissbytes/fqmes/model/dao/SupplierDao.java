package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.domain.repository.GenericDao;
import ch.swissbytes.domain.repository.Filter;
import ch.swissbytes.domain.repository.entities.SupplierEntity;
import ch.swissbytes.fqmes.types.StatusEnum;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 9/8/14.
 */


public class SupplierDao extends GenericDao<SupplierEntity> implements Serializable {



    public SupplierEntity load(Long id){
        List<SupplierEntity> list=findById(SupplierEntity.class,id);
        return list.isEmpty()?null:list.get(0);
    }

    public SupplierEntity persist(SupplierEntity supplierEntity){
        super.save(supplierEntity);
        return supplierEntity;
    }

    public SupplierEntity findByPurchaseOrder(final Long purchaseOrderId){
        String hql = "SELECT s FROM SupplierEntity s where s.purchaseOrder.id=:purchase_id AND s.status.id<>:DELETED" ;
        TypedQuery<SupplierEntity> query = this.entityManager.createQuery(
                hql, SupplierEntity.class);
        query.setParameter("purchase_id", purchaseOrderId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        List<SupplierEntity> list = query.getResultList();
        return !list.isEmpty()?list.get(0):null;
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
