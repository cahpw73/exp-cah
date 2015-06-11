package ch.swissbytes.Service.business.purchase;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.comment.CommentDao;
import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.item.ItemService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.requisition.RequisitionDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.SupplierDao;
import ch.swissbytes.Service.business.tdp.TransitDeliveryPointService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.PurchaseOrderStatusEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Purchase;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
public class PurchaseOrderService extends Service implements Serializable {


    public static final Logger log = Logger.getLogger(PurchaseOrderService.class.getName());

    @Inject
    private PurchaseOrderDao dao;

    @Inject
    private SupplierDao supplierDao;

    @Inject
    private CommentDao commentDao;

    @Inject
    private ScopeSupplyDao scopeSupplyDao;

    @Inject
    private EnumService enumService;

    @Inject
    private TransitDeliveryPointService transitDeliveryPointService;

    @Inject
    private ItemService itemService;

    @Inject
    private RequisitionDao requisitionDao;

    @Inject
    private DeliverableDao deliverableDao;

    @Inject
    private CashflowService cashflowService;

    //@Inject
    //private

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
        return hashCode;
    }

    public List<PurchaseOrderEntity> purchaseListByProject(final Long projectId){
        return dao.findPOByProject(projectId);
    }


    @Transactional
    public PurchaseOrderEntity savePOOnProcurement(PurchaseOrderEntity purchaseOrderEntity){
        POEntity po=dao.savePOEntity(purchaseOrderEntity.getPoEntity());
        collectLists(po,purchaseOrderEntity);
        purchaseOrderEntity.setPoEntity(po);
        purchaseOrderEntity.setPo(purchaseOrderEntity.getPoEntity().getOrderNumber());
        purchaseOrderEntity.setLastUpdate(new Date());
        purchaseOrderEntity.setStatus(enumService.getStatusEnumEnable());
        purchaseOrderEntity.setPurchaseOrderStatus(PurchaseOrderStatusEnum.ISSUED);
        dao.save(purchaseOrderEntity);
        //requisition daos
        requisitionDao.doSave(purchaseOrderEntity.getPoEntity(),po.getRequisitions());
        //items
        itemService.doSave(po.getItemList(),po);
        //deliverable
        deliverableDao.doSave(purchaseOrderEntity.getPoEntity(),po.getDeliverables());
        //CashFlow
        cashflowService.doSave(purchaseOrderEntity.getPoEntity().getCashflow(),po);
        //Text


        return purchaseOrderEntity;
    }

    @Transactional
    public PurchaseOrderEntity updatePOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        POEntity po = dao.updatePOEntity(purchaseOrderEntity.getPoEntity());
        collectLists(po,purchaseOrderEntity);
        purchaseOrderEntity.setPoEntity(po);
        purchaseOrderEntity.setPo(po.getOrderNumber());
        purchaseOrderEntity.setLastUpdate(new Date());
        dao.update(purchaseOrderEntity);
        //requisition daos
        requisitionDao.doUpdate(purchaseOrderEntity.getPoEntity(), po.getRequisitions());
        //items
        itemService.doUpdate(po.getItemList(), po);
        //deliverable
        deliverableDao.doUpdate(purchaseOrderEntity.getPoEntity(), po.getDeliverables());
        //cashFlow
        cashflowService.doUpdate(purchaseOrderEntity.getPoEntity().getCashflow(),po);

        return purchaseOrderEntity;
    }

    @Transactional
    public PurchaseOrderEntity updateOnlyPOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        dao.updatePOEntity(purchaseOrderEntity.getPoEntity());
        return purchaseOrderEntity;
    }
    private void collectLists(POEntity po,PurchaseOrderEntity poe){
        po.getItemList().clear();
        po.getItemList().addAll(poe.getPoEntity().getItemList());
        po.getDeliverables().clear();
        po.getDeliverables().addAll(poe.getPoEntity().getDeliverables());
        po.getRequisitions().clear();
        po.getRequisitions().addAll(poe.getPoEntity().getRequisitions());
        po.setCashflow(poe.getPoEntity().getCashflow());
    }
    public PurchaseOrderEntity findById(Long id){
        List<PurchaseOrderEntity>list=dao.findById(PurchaseOrderEntity.class, id != null ? id : 0L);
        PurchaseOrderEntity po=list.isEmpty()?null:list.get(0);
        if(po!= null) {
            po.getProjectEntity().getCurrencies().addAll(projectService.findProjectCurrencyByProjectId(po.getProjectEntity().getId()));
            po.getPoEntity().getRequisitions().addAll(requisitionDao.findRequisitionByPurchaseOrder(po.getPoEntity().getId()));
            po.getPoEntity().getDeliverables().addAll(deliverableDao.findDeliverableByPurchaseOrder(po.getPoEntity().getId()));
        }
        return list.isEmpty()?null:list.get(0);
    }
    @Inject
    private ProjectService projectService;
}
