package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.domain.repository.GenericDao;
import ch.swissbytes.domain.repository.Filter;
import ch.swissbytes.domain.repository.entities.*;
import ch.swissbytes.fqmes.types.StatusEnum;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alvaro on 9/15/14.
 */
public class AttachmentCommentDao extends GenericDao<AttachmentComment> implements Serializable {



    public List<AttachmentComment> findByComment(final Long commentId){
        String hql = "SELECT ass FROM AttachmentComment ass where ass.comment.id=:comment_id AND  ass.status.id<>:DELETED ORDER BY ass.fileName" ;
        TypedQuery<AttachmentComment> query = this.entityManager.createQuery(
                hql, AttachmentComment.class);
        query.setParameter("comment_id", commentId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        return query.getResultList();
    }

    public List<AttachmentComment> findByCommentLazy(final Long commentId){
        String hql = "SELECT ass.id,ass.fileName,ass.mimeType,ass.path,ass.comment,ass.status,ass.lastUpdate FROM AttachmentComment ass where ass.comment.id=:comment_id AND ass.status.id<>:DELETED ORDER BY ass.lastUpdate" ;
        Query query = this.entityManager.createQuery( hql);
        query.setParameter("comment_id", commentId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        List<Object>list=query.getResultList();
        List<AttachmentComment>attachments=new ArrayList<>();
        for(Object record:list){
            Object []values=(Object [])record;
            AttachmentComment entity=new AttachmentComment();
            entity.setId((Long)values[0]);
            entity.setFileName((String) values[1]);
            entity.setMimeType((String) values[2]);
            entity.setPath(values[3]!=null?(String)values[3]:null);
            entity.setComment((CommentEntity) values[4]);
            entity.setStatus((StatusEntity) values[5]);
            entity.setLastUpdate((Date) values[6]);
            attachments.add(entity);
        }
        return attachments;
    }




    public AttachmentComment load(final Long id){
        return super.load(AttachmentComment.class,id);
    }

    protected  void applyCriteriaValues(Query query,Filter filter){
    }
    protected String getEntity(){
        return AttachmentComment.class.getSimpleName();
    }

    protected  String addCriteria(Filter filter){
        return null;
    }


}
