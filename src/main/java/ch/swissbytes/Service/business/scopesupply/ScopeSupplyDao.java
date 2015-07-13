package ch.swissbytes.Service.business.scopesupply;

import ch.swissbytes.Service.business.AttachmentScopeSupply.AttachmentScopeSupplyDao;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.business.tdp.TransitDeliveryPointService;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.AttachmentScopeSupply;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.model.entities.TransitDeliveryPointEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alvaro on 9/12/14.
 */

public class ScopeSupplyDao extends GenericDao<ScopeSupplyEntity> implements Serializable {

    @Inject
    private TransitDeliveryPointService tdpService;
    @Inject
    private AttachmentScopeSupplyDao attachmentDao;

    public void persist(List<ScopeSupplyEntity> list,PurchaseOrderEntity purchaseOrderEntity){
        for(ScopeSupplyEntity sse: list){
            sse.setPurchaseOrder(purchaseOrderEntity);
            super.save(sse);
            for (TransitDeliveryPointEntity tdp: sse.getTdpList()){
                tdp.setId(null);
                tdp.setScopeSupply(sse);
                tdpService.save(tdp);
            }
            for (AttachmentScopeSupply attachment: sse.getAttachments()){
                attachment.setId(null);
                attachment.setScopeSupply(sse);
                attachmentDao.save(attachment);
            }
        }
    }

    public ScopeSupplyEntity load(final Long id){
        return super.load(ScopeSupplyEntity.class,id);
    }

    public List<ScopeSupplyEntity> findByPurchaseOrder(final Long purchaseOrderId){
        String hql = "SELECT ss FROM ScopeSupplyEntity ss where ss.purchaseOrder.id=:purchase_id AND  ss.status.id<>:DELETED ORDER BY ss.id" ;
        TypedQuery<ScopeSupplyEntity> query = this.entityManager.createQuery(
                hql, ScopeSupplyEntity.class);
        query.setParameter("purchase_id", purchaseOrderId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        return query.getResultList();

    }
    public void update(List<ScopeSupplyEntity>scopeSupplyEntities,PurchaseOrderEntity purchaseOrderEntity){
        for(ScopeSupplyEntity scopeSupplyEntity:scopeSupplyEntities){
            if(scopeSupplyEntity.getId()==null){//new one
                List<TransitDeliveryPointEntity> tdps=scopeSupplyEntity.getTdpList();
                scopeSupplyEntity.setPurchaseOrder(purchaseOrderEntity);
                super.save(scopeSupplyEntity);
                for(TransitDeliveryPointEntity tdp:tdps){
                    tdp.setId(null);
                    tdp.setScopeSupply(scopeSupplyEntity);
                    tdpService.save(tdp);
                }
                for (AttachmentScopeSupply attachment: scopeSupplyEntity.getAttachments()){
                    attachment.setId(null);
                    attachment.setScopeSupply(scopeSupplyEntity);
                    attachment.setLastUpdate(new Date());
                    attachmentDao.save(attachment);
                }
            }else{
                List<TransitDeliveryPointEntity> tdps=scopeSupplyEntity.getTdpList();
                super.update(scopeSupplyEntity);
                for(TransitDeliveryPointEntity tdp:tdps){
                    tdp.setScopeSupply(scopeSupplyEntity);
                    if(tdp.getId()==null||tdp.getId().longValue()<=0){
                        tdp.setId(null);
                        tdpService.save(tdp);
                    }else{
                        tdpService.update(tdp);
                    }
                }
                for (AttachmentScopeSupply attachment: scopeSupplyEntity.getAttachments()){
                    attachment.setLastUpdate(new Date());
                    attachment.setScopeSupply(scopeSupplyEntity);
                    if(attachment.getId()==null||attachment.getId().longValue()<=0){
                        attachment.setId(null);
                        attachmentDao.save(attachment);
                    }else{
                        if(StatusEnum.DELETED.getId().intValue()==attachment.getStatus().getId().intValue()){
                            AttachmentScopeSupply at=attachmentDao.load(attachment.getId());
                            at.setStatus(attachment.getStatus());
                            attachmentDao.update(attachment);
                        }
                    }
                }
            }

        }
    }

    public void doUpdate(ScopeSupplyEntity detachedEntity){
        ScopeSupplyEntity entity = super.merge(detachedEntity);
        super.update(entity);
    }

    public List<ScopeSupplyEntity> findByPOOId(Long poEntityId) {
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
