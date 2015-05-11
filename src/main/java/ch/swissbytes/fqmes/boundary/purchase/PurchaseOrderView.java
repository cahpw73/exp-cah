package ch.swissbytes.fqmes.boundary.purchase;


import ch.swissbytes.Service.business.AttachmentComment.AttachmentCommentService;
import ch.swissbytes.Service.business.comment.CommentService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.AttachmentScopeSupply.AttachmentScopeSupplyService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.Service.business.scopesupply.SupplierDao;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.fqmes.util.SortBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */
@Named
@ViewScoped
public class PurchaseOrderView implements Serializable{

    @Inject
    private PurchaseOrderService service;

    @Inject
    private SupplierDao supplierDao;

    @Inject
    private CommentService commentService;

    @Inject
    private ScopeSupplyService scopeSupplyService;

    @Inject
    private SortBean sortScopeSupply;

    @Inject
    private AttachmentCommentService attachmentCommentService;

    @Inject
    private AttachmentScopeSupplyService attachmentScopeSuplyService;

    private String purchaseOrderId;

    private PurchaseOrderEntity purchaseOrder;

    private SupplierEntity supplierView;

    private List<CommentEntity> comments;

    private List<ScopeSupplyEntity>scopeSupplies;

    private Long currentSelected=-1L;

    private static final Logger log = Logger.getLogger(PurchaseOrderView.class.getName());

    @PostConstruct
    public void create(){
        log.log(Level.INFO,String.format("creating bean [%s]",this.getClass().toString()));
    }
    @PreDestroy
    public void destroy(){
        log.log(Level.INFO,String.format("bean destroyed [%s]", this.getClass().toString()));
    }

    public void load(){
        log.info("loading....");
        purchaseOrder=service.load(Long.parseLong(purchaseOrderId));
        if(purchaseOrder!=null){
            supplierView=supplierDao.findByPurchaseOrder(purchaseOrder.getId());
            comments=commentService.findByPurchaseOrder(purchaseOrder.getId());
            scopeSupplies=scopeSupplyService.findByPurchaseOrder(purchaseOrder.getId());
            if(scopeSupplies != null && !scopeSupplies.isEmpty()){
                sortScopeSupply.sortScopeSupplyEntity(scopeSupplies);
            }
        }
    }
    public ScopeSupplyEntity currentScopeSupplyForAttachment(){
        Integer index=currentSelected!=null?scopeSupplyService.getIndexById(currentSelected,scopeSupplies):-1;
        return index>=0?scopeSupplies.get(index):null;
    }

    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }


    public SupplierEntity getSupplierView() {
        return supplierView;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public List<ScopeSupplyEntity> getScopeSupplies() {
        return scopeSupplies;
    }

    public void downloadAttachedFileOnComment(final long attachmentId){
        attachmentCommentService.download(attachmentId);
    }
    public void downloadAttachedFileOnScopeSupply(final long attachmentId){
        attachmentScopeSuplyService.download(attachmentId);
    }

    public List<AttachmentScopeSupply> getAttachmentsScopeSupply(){
        int index=scopeSupplyService.getIndexById(currentSelected,scopeSupplies);
        if(index>=0){
            return scopeSupplies.get(index).getAttachments();
        }
        return new ArrayList<>();
    }

    public List<AttachmentComment> getAttachmentsComment(){
        int index=commentService.getIndexById(currentSelected,comments);
        if(index>=0){
            return comments.get(index).getAttachments();
        }
        return new ArrayList<>();
    }

    public Long getCurrentSelected() {
        return currentSelected;
    }

    public void setCurrentSelected(Long currentSelected) {
        this.currentSelected = currentSelected;
    }
}
