package ch.swissbytes.fqmes.control.purchase;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.control.tdp.TransitDeliveryPointService;
import ch.swissbytes.fqmes.model.dao.*;
import ch.swissbytes.fqmes.model.entities.*;
import ch.swissbytes.fqmes.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by alvaro on 9/15/14.
 */
public class PurchaseOrderService extends Service implements Serializable {

    @Inject
    private PurchaseOrderDao dao;

    @Inject
    private SupplierDao supplierDao;

    @Inject
    private CommentDao commentDao;

    @Inject
    private ScopeSupplyDao scopeSupplyDao;


    @Inject
    private TransitDeliveryPointService transitDeliveryPointService;

    public PurchaseOrderService() {
        super.initialize(dao);
    }


    public PurchaseOrderEntity load(Long id) {
        return dao.load(id);
    }



    @Transactional
    public void doSave(PurchaseOrderEntity newPurchaseOrder, List<CommentEntity> comments, List<ScopeSupplyEntity> scopeSupplies, SupplierEntity supplier) {
        dao.save(newPurchaseOrder);
        supplier.setPurchaseOrder(newPurchaseOrder);
        supplierDao.persist(supplier);
        commentDao.persist(comments, newPurchaseOrder);
        scopeSupplyDao.persist(scopeSupplies, newPurchaseOrder);
    }

    @Transactional
    public PurchaseOrderEntity doUpdate(PurchaseOrderEntity por, SupplierEntity supplierEntity, List<CommentEntity> commentEntities, List<ScopeSupplyEntity> scopeSupplyEntities) {
        PurchaseOrderEntity entity = dao.update(por);
        supplierDao.update(supplierEntity);
        commentDao.update(commentEntities, entity);
        scopeSupplyDao.update(scopeSupplyEntities, entity);
       // attachmentDao.update(attachmentEntities, entity);
        return por;
    }

    /**
     * Delete the purchase order and its dependencies
     * @param purchaseOrderId
     */
    @Transactional
    public void doDelete(final Long purchaseOrderId){

        List<PurchaseOrderEntity> list = dao.findById(PurchaseOrderEntity.class, purchaseOrderId);
        if(!list.isEmpty()){
            PurchaseOrderEntity entity = list.get(0);
            entity.setStatus(getStatusDelete());
            dao.update(entity);
            
         //   deleteAttachment(purchaseOrderId);
            deleteComment(purchaseOrderId);
            deleteScopeSupply(purchaseOrderId);
            deleteSupplier(purchaseOrderId);
        }
    }



    private void deleteScopeSupply(final Long purchaseOrderId){
        List<ScopeSupplyEntity> scopeSupplyList = scopeSupplyDao.findByPurchaseOrder(purchaseOrderId);
        if(!scopeSupplyList.isEmpty()){
            ScopeSupplyEntity scopeSupply = scopeSupplyList.get(0);
            scopeSupply.setStatus(getStatusDelete());
            scopeSupplyDao.update(scopeSupply);
        }
    }

    private void deleteSupplier(final Long purchaseOrderId){
        SupplierEntity supplier = supplierDao.findByPurchaseOrder(purchaseOrderId);
        if(supplier != null){
            supplier.setStatus(getStatusDelete());
            supplierDao.update(supplier);
        }
    }

    private void deleteComment(final Long purchaseOrderId){
        List<CommentEntity> commentList =  commentDao.findByPurchaseOrder(purchaseOrderId);
        if(!commentList.isEmpty()){
            CommentEntity comment = commentList.get(0);
            comment.setStatus(getStatusDelete());
            commentDao.update(comment);
        }
    }
    private StatusEntity getStatusDelete(){
        return dao.findById(StatusEntity.class,StatusEnum.DELETED.getId()).get(0);
    }

    public Integer getAbsoluteHashcode(final Long purchaseOrderId){
        Integer hashCode=-1;
        PurchaseOrderEntity entity=dao.load(purchaseOrderId);
        if(entity!=null){
            hashCode=entity.hashCode();
            SupplierEntity supplierEntity=supplierDao.findByPurchaseOrder(purchaseOrderId);
            if(supplierEntity!=null){
                hashCode+=supplierEntity.hashCode();
            }
            for(CommentEntity commentEntity: commentDao.findByPurchaseOrder(purchaseOrderId)){
                hashCode+=commentEntity.hashCode();
            }
            for(ScopeSupplyEntity scopeSupplyEntity: scopeSupplyDao.findByPurchaseOrder(purchaseOrderId)){
                   hashCode+=scopeSupplyEntity.hashCode();
                    for (TransitDeliveryPointEntity tdp: transitDeliveryPointService.findByScopeSupply(scopeSupplyEntity.getId())){
                        hashCode+=tdp.hashCode();
                    }

            }
        }
        return hashCode;
    }

    public Integer getAbsoluteHashcode(final PurchaseOrderEntity purchaseOrderEntity, List<CommentEntity> commentEntityList, List<ScopeSupplyEntity> scopeSupplyEntityList, SupplierEntity supplierEntity){
        Integer hashCode=-1;
        //PurchaseOrderEntity entity=dao.load(purchaseOrderEntity.getId());
        //if(entity!=null){
            hashCode=purchaseOrderEntity.hashCode();
            //SupplierEntity supplierEntity=supplierDao.findByPurchaseOrder(purchaseOrderId);
            if(supplierEntity!=null){
                hashCode+=supplierEntity.hashCode();
            }
            for(CommentEntity commentEntity: commentEntityList){
                hashCode+=commentEntity.hashCode();
            }
            for(ScopeSupplyEntity scopeSupplyEntity: scopeSupplyEntityList){
                hashCode+=scopeSupplyEntity.hashCode();
                for (TransitDeliveryPointEntity tdp: scopeSupplyEntity.getTdpList()){
                    hashCode+=tdp.hashCode();
                }

            }
        //}
        return hashCode;
    }
}
