package ch.swissbytes.Service.business.comment;

import ch.swissbytes.Service.business.AttachmentComment.AttachmentCommentDao;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.AttachmentComment;
import ch.swissbytes.domain.model.entities.CommentEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.StatusEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class CommentDao  extends GenericDao<CommentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(CommentDao.class.getName());

    @Inject
    private AttachmentCommentDao attachmentDao;

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
            comment= persist(comment);
            for(AttachmentComment ac:comment.getAttachments()){
                ac.setLastUpdate(new Date());
                ac.setComment(comment);
                attachmentDao.save(ac);
            }
        }
    }

    public List<CommentEntity> findByPurchaseOrder(final Long purchaseOrderId){
        String hql = "SELECT cs.id,cs.name,cs.subject,cs.description,cs.lastUpdate,cs.purchaseOrder,cs.status, cs.to,cs.commentDate " +
                "FROM CommentEntity cs " +
                "where cs.purchaseOrder.id=:purchase_id AND cs.status.id<>:DELETED ORDER BY cs.commentDate" ;
        Query query = this.entityManager.createQuery( hql);
        query.setParameter("purchase_id", purchaseOrderId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        List<Object>list=query.getResultList();
        List<CommentEntity>comments=new ArrayList<>();
        for(Object record:list){
            Object []values=(Object [])record;
            CommentEntity entity=new CommentEntity();
            entity.setId((Long)values[0]);
            entity.setName((String) values[1]);
            entity.setSubject((String) values[2]);
            entity.setDescription((String) values[3]);
            entity.setLastUpdate((Date) values[4]);
            entity.setPurchaseOrder((PurchaseOrderEntity) values[5]);
            entity.setStatus((StatusEntity) values[6]);
            entity.setTo((String) values[7]);
            entity.setCommentDate((Date) values[8]);
            entity.setPreviousHascode(entity.hashCode());
            comments.add(entity);
        }
       return comments;
    }

    public void update(List<CommentEntity>commentEntities,PurchaseOrderEntity purchaseOrderEntity){
        Date now =new Date();
        for(CommentEntity commentEntity:commentEntities){
            if(commentEntity.getId()==null){//new one{
                commentEntity.setPurchaseOrder(purchaseOrderEntity);
               // log.info("persisting new content for ["+commentEntity.getFileName()+"] with size ["+ (commentEntity.getFile()!=null?commentEntity.getFile().length:"0")+"]");
                super.save(commentEntity);
            }else{
                super.update(commentEntity);
            }
            for(AttachmentComment ac:commentEntity.getAttachments()){
                ac.setComment(commentEntity);
                ac.setLastUpdate(now);
                if(ac.getId()!=null&&ac.getId()>0&&ac.getStatus().getId().intValue()==StatusEnum.DELETED.getId().intValue()){
                        attachmentDao.update(ac);
                }else{
                    if(ac.getId()==null||(ac.getId()!=null&&ac.getId()<0)){
                        ac.setId(null);
                        attachmentDao.save(ac);
                    }
                }
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
