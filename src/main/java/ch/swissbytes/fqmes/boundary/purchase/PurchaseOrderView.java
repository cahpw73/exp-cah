package ch.swissbytes.fqmes.boundary.purchase;


import ch.swissbytes.Service.business.AttachmentComment.AttachmentCommentService;
import ch.swissbytes.Service.business.comment.CommentService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.AttachmentScopeSupply.AttachmentScopeSupplyService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.fqmes.util.SortBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */
@Named
@ViewScoped
public class PurchaseOrderView implements Serializable {

    @Inject
    private PurchaseOrderService service;

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

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    private String purchaseOrderId;

    private PurchaseOrderEntity purchaseOrder;

    private List<CommentEntity> comments;

    private List<ScopeSupplyEntity> scopeSupplies;

    private Long currentSelected = -1L;

    private String anchor;

    private String expeditingStatuses = "";

    private static final Logger log = Logger.getLogger(PurchaseOrderView.class.getName());

    @PostConstruct
    public void create() {
        log.log(Level.INFO, String.format("creating bean [%s]", this.getClass().toString()));
    }

    @PreDestroy
    public void destroy() {
        log.log(Level.INFO, String.format("bean destroyed [%s]", this.getClass().toString()));
    }

    public void load() {
        log.info("loading....");
        purchaseOrder = service.load(Long.parseLong(purchaseOrderId));
        if (purchaseOrder != null) {
            comments = commentService.findByPurchaseOrder(purchaseOrder.getId());
            scopeSupplies = scopeSupplyService.findByPurchaseOrder(purchaseOrder.getId());
            if (scopeSupplies != null && !scopeSupplies.isEmpty()) {
                List<ScopeSupplyEntity> scopeActives = new ArrayList<>();
                for (ScopeSupplyEntity s : scopeSupplies) {
                    if (s.getExcludeFromExpediting() == null || !s.getExcludeFromExpediting()) {
                        scopeActives.add(s);
                    }
                }
                scopeSupplies.clear();
                scopeSupplies.addAll(scopeActives);
                sortScopeSupply.sortScopeSupplyEntity(scopeSupplies);
            }
            loadPurchaseOrderStatuses();
        }
    }

    private void loadPurchaseOrderStatuses() {
        List<ExpeditingStatusEntity> expeditingStatusList = service.findExpeditingStatusByPOid(Long.parseLong(purchaseOrderId));
        for (ExpeditingStatusEntity ex : expeditingStatusList) {
            expeditingStatuses = expeditingStatuses + ex.getPurchaseOrderStatus().ordinal() + ",";
        }
        if (expeditingStatuses.length() > 0) {
            expeditingStatuses = expeditingStatuses.substring(0, expeditingStatuses.length() - 1);
        }
        String[] ids = expeditingStatuses.split(",");
        String expStatuses="";
        for (int i = 0; i < ids.length; i++) {
            String exStatus = bundle.getString("postatus." + ExpeditingStatusEnum.getEnum(Integer.valueOf(ids[i]).intValue()).name());
            expStatuses=expStatuses + exStatus+", ";
        }
        log.info("Expediting statuses 1 : " + expeditingStatuses);
        expeditingStatuses = expStatuses;
        expeditingStatuses = expeditingStatuses.substring(0, expeditingStatuses.length() - 2);
        log.info("Expediting statuses 2 : " + expeditingStatuses);
    }

    public ScopeSupplyEntity currentScopeSupplyForAttachment() {
        Integer index = currentSelected != null ? scopeSupplyService.getIndexById(currentSelected, scopeSupplies) : -1;
        return index >= 0 ? scopeSupplies.get(index) : null;
    }

    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
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

    public void downloadAttachedFileOnComment(final long attachmentId) {
        attachmentCommentService.download(attachmentId);
    }

    public void downloadAttachedFileOnScopeSupply(final long attachmentId) {
        attachmentScopeSuplyService.download(attachmentId);
    }

    public List<AttachmentScopeSupply> getAttachmentsScopeSupply() {
        int index = scopeSupplyService.getIndexById(currentSelected, scopeSupplies);
        if (index >= 0) {
            return scopeSupplies.get(index).getAttachments();
        }
        return new ArrayList<>();
    }

    public List<AttachmentComment> getAttachmentsComment() {
        int index = commentService.getIndexById(currentSelected, comments);
        if (index >= 0) {
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

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getExpeditingStatuses() {
        return expeditingStatuses;
    }

    public void setExpeditingStatuses(String expeditingStatuses) {
        this.expeditingStatuses = expeditingStatuses;
    }
}
