package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.AttachmentEntity;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.types.StatusEnum;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 9/15/14.
 */
public class AttachmentDao extends GenericDao<AttachmentEntity> implements Serializable {

    public void save(List<AttachmentEntity>attachmentEntities,PurchaseOrderEntity purchaseOrderEntity){

        for(AttachmentEntity attachmentEntity:attachmentEntities){
            attachmentEntity.setPurchaseOrder(purchaseOrderEntity);
            save(attachmentEntity);
        }
    }

    public List<AttachmentEntity> findByPurchaseOrder(final Long purchaseOrderId){
        String hql = "SELECT at FROM AttachmentEntity at where at.purchaseOrder.id=:purchase_id AND  at.status.id<>:DELETED" ;
        TypedQuery<AttachmentEntity> query = this.entityManager.createQuery(
                hql, AttachmentEntity.class);
        query.setParameter("purchase_id", purchaseOrderId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        return query.getResultList();

    }

    public void update(List<AttachmentEntity>attachmentEntities,PurchaseOrderEntity purchaseOrderEntity){

        for(AttachmentEntity attachmentEntity:attachmentEntities){
            if(attachmentEntity.getId()!=null){
                super.update(attachmentEntity);
            }else{
                attachmentEntity.setPurchaseOrder(purchaseOrderEntity);
                save(attachmentEntity);
            }

        }
    }



    public AttachmentEntity load(final Long id){
        return super.load(AttachmentEntity.class,id);
    }

    protected  void applyCriteriaValues(Query query,Filter filter){
    }
    protected String getEntity(){
        return AttachmentEntity.class.getSimpleName();
    }

    protected  String addCriteria(Filter filter){
        return null;
    }


}
