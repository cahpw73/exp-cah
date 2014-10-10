package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.fqmes.control.attachment.AttachmentService;
import ch.swissbytes.fqmes.control.comment.CommentService;
import ch.swissbytes.fqmes.control.purchase.PurchaseOrderService;
import ch.swissbytes.fqmes.control.scopesupply.ScopeSupplyService;
import ch.swissbytes.fqmes.model.dao.*;
import ch.swissbytes.fqmes.model.entities.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
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
    private AttachmentService attachmentService;

    private String purchaseOrderId;

    private PurchaseOrderEntity purchaseOrder;

    private SupplierEntity supplierView;

    private List<CommentEntity> comments;

    private List<AttachmentEntity>attachmentEntities;

    private List<ScopeSupplyEntity>scopeSupplies;

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
            attachmentEntities=attachmentService.findByPurchaseOrder(purchaseOrder.getId());
        }
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

    public void downloadAttachedFileOnComment(final long id){
        log.info("public void downloadAttachedFileOnComment(final long id)");
        commentService.download(id);
    }

}
