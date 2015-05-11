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
public class AttachmentScopeSupplyDao extends GenericDao<AttachmentScopeSupply> implements Serializable {



    public List<AttachmentScopeSupply> findByScopeSupply(final Long scopeSupplyId){
        String hql = "SELECT ass FROM AttachmentScopeSupply ass where ass.scopeSupply.id=:scope_supply_id AND  ass.status.id<>:DELETED ORDER BY ass.fileName" ;
        TypedQuery<AttachmentScopeSupply> query = this.entityManager.createQuery(
                hql, AttachmentScopeSupply.class);
        query.setParameter("scope_supply_id", scopeSupplyId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        return query.getResultList();
    }

    public List<AttachmentScopeSupply> findByScopeSupplyLazy(final Long scopeSupplyId){
        String hql = "SELECT ass.id,ass.fileName,ass.mimeType,ass.path,ass.scopeSupply,ass.status,ass.lastUpdate FROM AttachmentScopeSupply ass where ass.scopeSupply.id=:scope_supply_id AND ass.status.id<>:DELETED ORDER BY ass.lastUpdate" ;
        Query query = this.entityManager.createQuery( hql);
        query.setParameter("scope_supply_id", scopeSupplyId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        List<Object>list=query.getResultList();
        List<AttachmentScopeSupply>attachments=new ArrayList<>();
        for(Object record:list){
            Object []values=(Object [])record;
            AttachmentScopeSupply entity=new AttachmentScopeSupply();
            entity.setId((Long)values[0]);
            entity.setFileName((String) values[1]);
            entity.setMimeType((String) values[2]);
            entity.setPath(values[3]!=null?(String)values[3]:null);
            entity.setScopeSupply((ScopeSupplyEntity)values[4]);
            entity.setStatus((StatusEntity) values[5]);
            entity.setLastUpdate((Date) values[6]);
            attachments.add(entity);
        }
        return attachments;
    }




    public AttachmentScopeSupply load(final Long id){
        return super.load(AttachmentScopeSupply.class,id);
    }

    protected  void applyCriteriaValues(Query query,Filter filter){
    }
    protected String getEntity(){
        return AttachmentScopeSupply.class.getSimpleName();
    }

    protected  String addCriteria(Filter filter){
        return null;
    }


}
