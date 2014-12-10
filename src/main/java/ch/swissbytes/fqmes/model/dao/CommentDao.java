package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.CommentEntity;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.model.entities.StatusEntity;
import ch.swissbytes.fqmes.types.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

      /*  private String name;
        private String reason;
        private String description;
        private String fileName;
        private String mimeType;
        private byte[]  file;
        private Date lastUpdate;
        private PurchaseOrderEntity purchaseOrder;
        private StatusEntity status;*/

        String hql = "SELECT cs.id,cs.name,cs.reason,cs.description,cs.fileName,cs.mimeType,cs.lastUpdate,cs.purchaseOrder,cs.status FROM CommentEntity cs where cs.purchaseOrder.id=:purchase_id AND cs.status.id<>:DELETED ORDER BY cs.id" ;
        Query query = this.entityManager.createQuery( hql);
        query.setParameter("purchase_id", purchaseOrderId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        List<Object>list=query.getResultList();
        List<CommentEntity>comments=new ArrayList<>();
        for(Object record:list){
            Object []values=(Object [])record;
            CommentEntity entity=new CommentEntity();
            entity.setId((Long)values[0]);
            entity.setName((String)values[1]);
            entity.setReason((String)values[2]);
            entity.setDescription((String)values[3]);
            entity.setFileName((String)values[4]);
            entity.setMimeType((String)values[5]);
            entity.setLastUpdate((Date)values[6]);
            entity.setPurchaseOrder((PurchaseOrderEntity)values[7]);
            entity.setStatus((StatusEntity)values[8]);
            comments.add(entity);
        }
       return comments;
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
