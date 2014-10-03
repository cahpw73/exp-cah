package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.CommentEntity;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.types.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 9/10/14.
 */

public class CommentDao  extends GenericDao<CommentEntity> implements Serializable {


    public CommentEntity persist(CommentEntity commentEntity){
        super.save(commentEntity);
        return commentEntity;
    }


    public CommentEntity load(final Long id){
        List<CommentEntity> list=findById(CommentEntity.class,id);
        return list.isEmpty()?null:list.get(0);
    }

    public void persist(List<CommentEntity>comments,PurchaseOrderEntity purchaseOrderEntity){
        for(CommentEntity comment:comments){
            comment.setPurchaseOrder(purchaseOrderEntity);
            persist(comment);
        }
    }

    public List<CommentEntity> findByPurchaseOrder(final Long purchaseOrderId){
        String hql = "SELECT cs FROM CommentEntity cs where cs.purchaseOrder.id=:purchase_id AND cs.status.id<>:DELETED ORDER BY cs.id DESC" ;
        TypedQuery<CommentEntity> query = this.entityManager.createQuery(
                hql, CommentEntity.class);
        query.setParameter("purchase_id", purchaseOrderId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        return query.getResultList();

    }

    public void update(List<CommentEntity>commentEntities,PurchaseOrderEntity purchaseOrderEntity){
        for(CommentEntity commentEntity:commentEntities){
            if(commentEntity.getId()==null){//new one{
                commentEntity.setPurchaseOrder(purchaseOrderEntity);
                super.save(commentEntity);
            }else{
                super.update(commentEntity);
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
