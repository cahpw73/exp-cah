package ch.swissbytes.fqmes.boundary.purchase;


import ch.swissbytes.Service.business.AttachmentComment.AttachmentCommentService;
import ch.swissbytes.Service.business.AttachmentScopeSupply.AttachmentScopeSupplyService;
import ch.swissbytes.Service.business.comment.CommentService;
import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.Service.business.tdp.TransitDeliveryPointService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.domain.types.TimeMeasurementEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.boundary.supplierProc.ContactBean;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcBean;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcList;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */
@Named
@ConversationScoped
public class PurchaseOrderEdit implements Serializable {

    @Inject
    private CommentService commentService;

    @Inject
    private ScopeSupplyService scopeSupplyService;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private EnumService enumService;

    @Inject
    private Conversation conversation;

    @Inject
    private TransitDeliveryPointService tdpService;

    @Inject
    private Configuration configuration;

    @Inject
    private SortBean sortScopeSupply;

    @Inject
    private AttachmentCommentService attachmentCommentService;

    @Inject
    private AttachmentScopeSupplyService attachmentScopeSupplyService;
    @Inject
    private ContactBean contactBean;

    @Inject
    private SupplierProcBean supplier;

    @Inject
    private SupplierProcList list;

    private String purchaseOrderId;

    private PurchaseOrderEntity poEdit;

    private ScopeSupplyEntity bulkScopeSupply;

    private List<CommentEntity> comments;

    private List<ScopeSupplyEntity> scopeSupplies;

    private ScopeSupplyEntity selectedScopeSupply;

    private ScopeSupplyEntity scopeSupplySplit;

    private CommentEntity commentEdit;

    private CommentEntity editingComment;

    private Integer commentIndexSelected;

    private ScopeSupplyEntity scopeSupplyEdit;

    private ScopeSupplyEntity currentScopeSupply;

    private Integer currentOperation = -1;

    private Long relativeCurrentCommentId = 0L;

    private Long relativeCurrentScopeSupplyId = 0L;

    private ScopeSupplyEntity scopeSupplyEditing;

    private Integer indexForScopeSupplyEditing = -1;

    private final static Integer SPLIT = 2;
    private final static Integer CREATE = 0;
    private final static Integer EDIT = 1;


    private BigDecimal originalQuantity;

    private static final Logger log = Logger.getLogger(PurchaseOrderEdit.class.getName());

    private TransitDeliveryPointEntity newTdp;

    private TransitDeliveryPointEntity tdpEdit;

    private Long temporaryIdForTdp = 0L;

    private Integer currentIndexForTdp = -1;
    private String fase = "0";

    private Integer currentHashCode;

    private List<ScopeSupplyEntity> scopeActives = new ArrayList<>();

    private List<CommentEntity> commentActives = new ArrayList<>();

    private List<TransitDeliveryPointEntity> tdpActives = new ArrayList<>();

    private Long idForAttachment;

    private Long temporaryId = -1L;

    private String titleBulkUpdateModal;

    private String expeditingStatuses;

    private boolean hasValueLeadTime;

    private boolean hasValueForecastSiteDate;

    private String anchor;


    public void selectingForAttachment(Long id) {
        idForAttachment = id;
    }

    public ScopeSupplyEntity currentScopeSupplyForAttachment() {
        Integer index = idForAttachment != null ? scopeSupplyService.getIndexById(idForAttachment, scopeActives) : -1;
        return index >= 0 ? scopeActives.get(index) : null;
    }

    public List<AttachmentComment> getAttachmentActives() {
        if (commentIndexSelected != null && commentEdit != null && commentIndexSelected == -1) {
            return new AttachmentCommentService().getActives(commentEdit.getAttachments());
        } else if (commentIndexSelected != null && editingComment != null && commentIndexSelected != -1) {
            return new AttachmentCommentService().getActives(editingComment.getAttachments());
        }
        return new ArrayList<AttachmentComment>();
    }

    public void load() {
        log.info("loading...");
        if (!fase.equals("1")) {
            originalQuantity = new BigDecimal("0");
            poEdit = service.load(Long.parseLong(purchaseOrderId));
            service.removePrefixIfAny(poEdit);
            currentHashCode = service.getAbsoluteHashcode(poEdit.getId());
            log.info(String.format("hashcode starting [%s]", currentHashCode));
            if (comments == null || comments.size() == 0) {
                comments = commentService.findByPurchaseOrder(poEdit.getId());
            }
            if (scopeSupplies == null || scopeSupplies.size() == 0) {
                scopeSupplies = scopeSupplyService.findByPurchaseOrder(poEdit.getId());
                for (ScopeSupplyEntity s : scopeSupplies) {
                    scopeActives.add(s);
                }
            }
            commentIndexSelected = -1;
        }

        if (scopeSupplies != null && !scopeSupplies.isEmpty()) {
            scopeActives.clear();
            for (ScopeSupplyEntity s : scopeSupplies) {
                scopeActives.add(s);
            }
            sortScopeSupply.sortScopeSupplyEntity(scopeActives);
        }
        commentActives = commentService.getActives(comments);
    }

    @PostConstruct
    public void create() {
        log.info("creating Purchase Order Edit...");
        bulkScopeSupply = new ScopeSupplyEntity();
        comments = new ArrayList<>();
        commentEdit = new CommentEntity();
        log.log(Level.INFO, String.format("creating bean [%s]", this.getClass().toString()));
        if (conversation.isTransient()) {
            log.info("Creating conversation...");
            conversation.begin();
            conversation.setTimeout(configuration.getTimeOutConversation());
            log.info(String.format("conversation started [%s]", conversation.getTimeout()));
        }
    }

    @PreDestroy
    public void destroy() {
        log.log(Level.INFO, String.format("bean destroyed [%s]", this.getClass().toString()));
    }

    public void switchModeForecastSiteDateForTdp(boolean editing) {
        switch (currentOperation) {
            case 0://CREATING
                if (!editing) {
                    calulateForecasteDateForTdpCreation();
                } else {
                    calulateForecasteDateForTdpEdition();
                }
                break;
            case 1://EDITING
                if (!editing) {
                    calulateForecasteDateForTdpCreation1();
                } else {
                    calulateForecasteDateForTdpEdition1();
                }
                break;
            default:
                log.info("no possible computation");
                break;
        }

    }

    public String doUpdate() {
        log.info("upgrading...");
        log.info("Expediting Statuses: " + expeditingStatuses);
        Integer hashCode = service.getAbsoluteHashcode(poEdit.getId());
        log.info(String.format("hashCode [%s]", hashCode));
        String url;
        if (hashCode.intValue() == currentHashCode.intValue()) {
            updateStatusesAndLastUpdate();
            service.doUpdate(poEdit, comments, scopeSupplies, expeditingStatuses);
            url = "view?faces-redirect=true&poId=" + poEdit.getId();
        } else {
            url = "edit?faces-redirect=true&poId=" + poEdit.getId();
            Messages.addFlashGlobalError("Somebody has already updated this purchase order! Please enter your data one more time");
        }
        if (!conversation.isTransient()) {
            conversation.end();
        }
        return url;
    }

    public boolean validateFields() {
        if (StringUtils.isEmpty(poEdit.getExpeditingTitle()) || StringUtils.isBlank(poEdit.getExpeditingTitle())) {
            Messages.addFlashError("expeditingTitleId", "Enter a valid Expediting Title");
            return false;
        }
        return true;
    }

    private void updateStatusesAndLastUpdate() {
        PurchaseOrderEntity oldPurchaseOrder = service.load(poEdit.getId());
        Date now = new Date();
        if (!oldPurchaseOrder.equals(poEdit)) {
            poEdit.setLastUpdate(now);
        }
        for (final CommentEntity commentEntity : comments) {
            if (commentEntity.getId() <= 0) {
                commentEntity.setId(null);
                commentEntity.setLastUpdate(now);
            } else {
                CommentEntity oldComment = commentService.load(commentEntity.getId());
                if (!oldComment.equals(commentEntity)) {
                    commentEntity.setLastUpdate(now);
                }
            }
        }
        int order = 1;
        sortScopeSupply.sortScopeSupplyEntity(scopeSupplies);
        for (final ScopeSupplyEntity scopeSupply : scopeSupplies) {
            if (scopeSupply.getId() <= 0) {
                scopeSupply.setId(null);
                scopeSupply.setLastUpdate(now);
            } else {
                ScopeSupplyEntity oldScopeSupply = scopeSupplyService.load(scopeSupply.getId());
                if (!oldScopeSupply.equals(scopeSupply)) {
                    scopeSupply.setLastUpdate(now);
                }
            }
            if (scopeSupply.getStatus().getId().intValue() != StatusEnum.DELETED.ordinal()) {
                scopeSupply.setOrdered(order);
                order++;
            } else {
                scopeSupply.setOrdered(null);
            }
        }
    }

    public void addComment() {
        CommentEntity commentEntity = new CommentEntity();
        try {
            commentEntity = (CommentEntity) org.apache.commons.beanutils.BeanUtils.cloneBean(commentEdit);
            commentEntity.getAttachments().addAll(commentEdit.getAttachments());
            relativeCurrentCommentId--;
            commentEntity.setId(relativeCurrentCommentId);
        } catch (Exception ex) {

        }
        commentEntity.setLastUpdate(new Date());
        commentEntity.setStatus(enumService.getStatusEnumEnable());
        comments.add(commentEntity);
        commentActives = commentService.getActives(comments);
    }

    private void registerScopeSupply() {
        log.log(Level.INFO, "SCOPE CODE    " + scopeSupplyEdit.getCode());
        ScopeSupplyEntity scopeSupplyEntity = new ScopeSupplyEntity();
        try {
            scopeSupplyEntity = (ScopeSupplyEntity) org.apache.commons.beanutils.BeanUtils.cloneBean(scopeSupplyEdit);
            scopeSupplyEntity.getTdpList().clear();
            scopeSupplyEntity.getTdpList().addAll(scopeSupplyEdit.getTdpList());
        } catch (Exception ex) {
        }
        scopeSupplyEntity.setStatus(enumService.getStatusEnumEnable());
        scopeSupplyEntity.setId(relativeCurrentScopeSupplyId);
        scopeSupplies.add(scopeSupplyEntity);

    }


    public void handleCommentUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        log.info("uploading file [" + uf.getFileName() + "]");
        log.info("size [" + uf.getSize() + "]");
        AttachmentComment ac = new AttachmentComment();
        new Util().enterFile(uf, ac);
        ac.setStatus(enumService.getStatusEnumEnable());
        ac.setId(temporaryId);
        temporaryId--;
        if (commentIndexSelected == -1) {
            commentEdit.getAttachments().add(ac);
        } else {
            editingComment.getAttachments().add(ac);
        }
    }

    public void cleanComment() {
        commentEdit = new CommentEntity();
        commentIndexSelected = -1;
    }

    public String cleanScopeSupply() {
        currentOperation = CREATE;//CREATING...
        scopeSupplyEdit = new ScopeSupplyEntity();
        scopeSupplyEdit.setPoDeliveryDate(poEdit.getPoDeliveryDate());
        scopeSupplyEdit.setResponsibleExpediting(poEdit.getResponsibleExpediting());
        scopeSupplyEdit.setRequiredSiteDate(poEdit.getRequiredDate());
        scopeSupplyEdit.setIsForecastSiteDateManual(false);
        scopeSupplyEdit.setDeliveryLeadTimeMs(TimeMeasurementEnum.DAY);
        return "/purchase/modal/EditModalScopeSupply?faces-redirect=true";
    }

    private boolean isValidDataUpdate() {
        log.info("boolean isValidDataUpdate()");
        boolean isValid = false;
        //int inc = 0;
        BigDecimal quantity = scopeSupplyEditing.getQuantity() != null ? scopeSupplyEditing.getQuantity() : new BigDecimal("0");
        //Double cost = scopeSupplyEditing.getCost() != null ? scopeSupplyEditing.getCost().doubleValue() : 0D;
        if (quantity.doubleValue() >= 0) {
            isValid = true;
        } else {
            Messages.addGlobalError("Quantity has a invalid data");
        }
        /*if (cost >= 0) {
            inc++;
        } else {
            Messages.addGlobalError("Unit Price has a invalid data");
        }*/
        /*if (inc == 2) {
            isValid = true;
        }*/
        return isValid;
    }

    private boolean isValidData() {
        log.info("boolean isValidData()");
        boolean isValid = false;
        int inc = 0;
        BigDecimal quantity = scopeSupplyEdit.getQuantity() != null ? scopeSupplyEdit.getQuantity() : new BigDecimal("0");
        Double cost = scopeSupplyEdit.getCost() != null ? scopeSupplyEdit.getCost().doubleValue() : 0D;
        if (quantity.doubleValue() >= 0) {
            inc++;
        } else {
            Messages.addGlobalError("Quantity has a invalid data");
        }
        if (cost >= 0) {
            inc++;
        } else {
            Messages.addGlobalError("Unit Price has a invalid data");
        }
        if (inc == 2) {
            isValid = true;
        }
        return isValid;
    }


    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public ScopeSupplyEntity getSelectedScopeSupply() {
        return selectedScopeSupply;
    }

    public void setSelectedScopeSupply(ScopeSupplyEntity selectedScopeSupply) {
        this.selectedScopeSupply = selectedScopeSupply;
    }


    public PurchaseOrderEntity getPoEdit() {
        return poEdit;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }


    public List<ScopeSupplyEntity> getScopeSupplies() {
        return scopeSupplies;
    }

    @Produces
    @ConversationScoped
    @Named
    public CommentEntity getCommentEdit() {
        return commentEdit;
    }

    public List<CommentEntity> getActiveComments() {
        return commentActives;
    }


    @Produces
    @ViewScoped
    @Named
    public ScopeSupplyEntity getScopeSupplyEdit() {
        return scopeSupplyEdit;
    }

    public void deleteComment(Long id) {
        log.info("deleting commment ");
        Integer index = commentService.getIndexById(id, comments);
        if (index >= 0) {
            CommentEntity comment = comments.get(index);
            if (comment.getId() > 0) {
                comment.setStatus(enumService.getStatusEnumDeleted());
            } else {
                log.info("comments previous " + comments.size());
                comments.remove(index.intValue());
                log.info("comments " + comments.size());
            }
            commentActives = commentService.getActives(comments);
        }
    }

    public void editComment(Long id) {
        Integer index = commentService.getIndexById(id, comments);
        if (index >= 0) {
            CommentEntity comment = comments.get(index);
            if (comment.getId() > 0) {
                comment.setStatus(enumService.getStatusEnumDeleted());
            } else {
                comments.remove(index);
            }
        } else {

        }
    }

    public void selectComment(final Long id) {
        commentIndexSelected = -1;
        Integer index = commentService.getIndexById(id, comments);
        if (index >= 0) {
            commentIndexSelected = index;
            editingComment = commentService.clone(comments.get(index));
            editingComment.getAttachments().addAll(comments.get(index).getAttachments());
        }
    }

    public void resetBulkUpdateModal() {
        bulkScopeSupply = new ScopeSupplyEntity();
        titleBulkUpdateModal = "Bulk update for PO #" + poEdit.getPo();
        hasValueForecastSiteDate = false;
        hasValueLeadTime = false;
    }

    public void doBulkUpdateForPO() {
        log.info("size list scopeActives: " + scopeActives.size());
        for (ScopeSupplyEntity sp : scopeActives) {
            if (sp.getExcludeFromExpediting() == null || sp.getExcludeFromExpediting().booleanValue() == false) {
                sp.setResponsibleExpediting(StringUtils.isNotEmpty(bulkScopeSupply.getResponsibleExpediting()) ? bulkScopeSupply.getResponsibleExpediting() : sp.getResponsibleExpediting());
                sp.setRequiredSiteDate(bulkScopeSupply.getRequiredSiteDate() != null ? bulkScopeSupply.getRequiredSiteDate() : sp.getRequiredSiteDate());
                sp.setForecastExWorkDate(bulkScopeSupply.getForecastExWorkDate() != null ? bulkScopeSupply.getForecastExWorkDate() : sp.getForecastExWorkDate());
                sp.setActualExWorkDate(bulkScopeSupply.getActualExWorkDate() != null ? bulkScopeSupply.getActualExWorkDate() : sp.getActualExWorkDate());
                sp.setActualSiteDate(bulkScopeSupply.getActualSiteDate() != null ? bulkScopeSupply.getActualSiteDate() : sp.getActualSiteDate());
                sp.setSpIncoTerm(StringUtils.isNotEmpty(bulkScopeSupply.getSpIncoTerm()) ? bulkScopeSupply.getSpIncoTerm() : sp.getSpIncoTerm());
                sp.setPoDeliveryDate(bulkScopeSupply.getPoDeliveryDate() != null ? bulkScopeSupply.getPoDeliveryDate() : sp.getPoDeliveryDate());
                sp.setDeliveryLeadTimeQt(bulkScopeSupply.getDeliveryLeadTimeQt() != null ? bulkScopeSupply.getDeliveryLeadTimeQt() : sp.getDeliveryLeadTimeQt());
                sp.setDeliveryLeadTimeMs(bulkScopeSupply.getDeliveryLeadTimeMs() != null ? bulkScopeSupply.getDeliveryLeadTimeMs() : sp.getDeliveryLeadTimeMs());
                sp.setForecastSiteDate(bulkScopeSupply.getForecastSiteDate() != null ? bulkScopeSupply.getForecastSiteDate() : sp.getForecastSiteDate());
                sp.setIsForecastSiteDateManual(bulkScopeSupply.getIsForecastSiteDateManual() != null ? bulkScopeSupply.getIsForecastSiteDateManual() : false);
                if(bulkScopeSupply.getDeliveryLeadTimeMs()!=null && bulkScopeSupply.getDeliveryLeadTimeQt()!=null){
                    calculateDateForecastDateForBulkUpdate(sp);
                }else if(bulkScopeSupply.getForecastSiteDate()!=null){
                    sp.setIsForecastSiteDateManual(true);
                    sp.setDeliveryLeadTimeMs(null);
                    sp.setDeliveryLeadTimeQt(null);
                }

                log.info("algo");
            }
        }
    }

    public void doUpdateComment() {
        Integer index = commentService.getIndexById(editingComment.getId(), comments);
        if (index >= 0 && index < comments.size()) {

            int originalHascode = comments.get(index).hashCode();
            int currentHascode = editingComment.hashCode();
            if (currentHascode != originalHascode) {
                editingComment.setLastUpdate(new Date());
            }
            comments.set(index, commentService.clone(editingComment));
            comments.get(index).getAttachments().addAll(editingComment.getAttachments());
        }


        commentActives = commentService.getActives(comments);
    }


    public void deleteScopeSupply(Long id) {
        final int index = scopeSupplyService.getIndexById(id, scopeSupplies);
        if (index >= 0) {
            ScopeSupplyEntity scopeSupplyEntity = scopeSupplies.get(index);
            if (scopeSupplyEntity.getId() != null) {
                scopeSupplyEntity.setStatus(enumService.getStatusEnumDeleted());
            } else {
                scopeSupplies.remove(index);
            }
        } else {
        }
        scopeActives.clear();
        scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
    }

    public List<AttachmentComment> getActiveAttachmentsForComment() {
        List<AttachmentComment> list = new ArrayList<AttachmentComment>();
        if (commentIndexSelected != null && commentIndexSelected == -1 && commentEdit != null) {
            list = commentEdit.getAttachments();
        } else if (commentIndexSelected != null && commentIndexSelected != -1 && editingComment != null) {
            list = editingComment.getAttachments();
        }
        return list;
    }

    public void deleteAttachmentComment(Long id) {
        if (commentIndexSelected == -1) {
            int index = new AttachmentScopeSupplyService().getIndexById(id, commentEdit.getAttachments());
            commentEdit.getAttachments().remove(index);
        } else {
            int index = new AttachmentScopeSupplyService().getIndexById(id, editingComment.getAttachments());
            if (editingComment.getAttachments().get(index).getId() == null || (editingComment.getAttachments().get(index).getId() != null && editingComment.getAttachments().get(index).getId().intValue() < 0)) {
                editingComment.getAttachments().remove(index);
            } else if (editingComment.getAttachments().get(index).getId() != null && editingComment.getAttachments().get(index).getId().intValue() > 0) {
                editingComment.getAttachments().get(index).setStatus(enumService.getStatusEnumDeleted());
            }
        }

    }

    public List<AttachmentScopeSupply> getActiveAttachmentsForScopeSupply() {
        final int index = idForAttachment != null ? scopeSupplyService.getIndexById(idForAttachment, scopeSupplies) : -1;
        if (index >= 0) {

            return new AttachmentScopeSupplyService().getActives(scopeSupplies.get(index).getAttachments());
        }
        return new ArrayList<>();
    }

    public void deleteAttachmentScopeSupply(Long id) {
        final int index = idForAttachment != null ? scopeSupplyService.getIndexById(idForAttachment, scopeSupplies) : -1;
        if (index >= 0) {
            int index2 = new AttachmentScopeSupplyService().getIndexById(id, scopeSupplies.get(index).getAttachments());
            if (index2 >= 0) {
                if (scopeSupplies.get(index).getAttachments().get(index2).getId() > 0) {
                    scopeSupplies.get(index).getAttachments().get(index2).setStatus(enumService.getStatusEnumDeleted());
                } else {
                    scopeSupplies.get(index).getAttachments().remove(index2);
                }
            }
        }
    }

    public void handleAttachmentScopeSupply(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        if (idForAttachment != null) {
            final int index = idForAttachment != null ? scopeSupplyService.getIndexById(idForAttachment, scopeSupplies) : -1;
            if (index >= 0) {
                AttachmentScopeSupply attachment = new AttachmentScopeSupply();
                new Util().enterFile(uf, attachment);
                attachment.setStatus(enumService.getStatusEnumEnable());
                attachment.setId(temporaryId);
                temporaryId--;
                scopeSupplies.get(index).getAttachments().add(attachment);
            }
        }
    }

    public List<ScopeSupplyEntity> getActiveScopeSupplies() {
        return scopeActives;
    }

    public String addScopeSupply() {
        if (isValidData()) {
            registerScopeSupply();
            scopeActives.clear();
            scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
            return "/purchase/edit?faces-redirect=true&fase=1";
        }
        return "";
    }

    public void addScopeSupplyAndAdd() {
        if (isValidData()) {
            registerScopeSupply();
            cleanScopeSupply();
        }
        scopeActives.clear();
        scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
    }

    public String cancel() {
        log.info("cancel to edit purchase order...........");
        return "/purchase/edit?faces-redirect=true&cid=" + conversation.getId() + "&fase=1";
    }

    public String split() {
        String url = "/purchase/edit?faces-redirect=true&fase=1";
        if (validateValuesAboutSplit()) {
            relativeCurrentScopeSupplyId--;
            scopeSupplySplit.setId(relativeCurrentScopeSupplyId);
            scopeSupplySplit.setStatus(enumService.getStatusEnumEnable());
            ScopeSupplyEntity ss = scopeSupplyService.clone(scopeSupplySplit);
            ss.setTotalCost(ss.getCost().multiply(ss.getQuantity()));
            ss.getTdpList().clear();
            ss.getAttachments().clear();
            for (TransitDeliveryPointEntity tdpe : selectedScopeSupply.getTdpList()) {
                currentIndexForTdp--;
                TransitDeliveryPointEntity tdp = tdpService.clone(tdpe);
                tdp.setId(Long.parseLong(Integer.toString(currentIndexForTdp)));
                ss.getTdpList().add(tdp);
            }
            for (AttachmentScopeSupply acs : selectedScopeSupply.getAttachments()) {
                temporaryId--;
                AttachmentScopeSupply attachmentScopeSupply = new AttachmentScopeSupplyService().clone(acs);
                attachmentScopeSupply.setId(temporaryId);
                attachmentScopeSupply.setLastUpdate(new Date());
                ss.getAttachments().add(attachmentScopeSupply);
            }
            scopeSupplies.add(ss);
            final int index = scopeSupplyService.getIndexById(selectedScopeSupply.getId(), scopeSupplies);
            if (index >= 0 && index < scopeSupplies.size()) {
                ScopeSupplyEntity sse = scopeSupplyService.clone(selectedScopeSupply);
                sse.setTotalCost(ss.getCost().multiply(ss.getQuantity()));
                sse.getTdpList().addAll(selectedScopeSupply.getTdpList());
                sse.getAttachments().addAll(selectedScopeSupply.getAttachments());
                scopeSupplies.set(index, sse);
            }
            scopeActives.clear();
            scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
        } else {
            Messages.addGlobalError("You are not able to split with these values");
            url = "";
        }
        return url;
    }

    private boolean validateValuesAboutSplit() {
        int arrivedQuantity = (scopeSupplySplit != null && scopeSupplySplit.getQuantity() != null) ? scopeSupplySplit.getQuantity().intValue() : 0;
        int newQuantity = (selectedScopeSupply != null && selectedScopeSupply.getQuantity() != null) ? selectedScopeSupply.getQuantity().intValue() : 0;
        return originalQuantity != null && originalQuantity.intValue() > 1 && originalQuantity.intValue() == arrivedQuantity + newQuantity;
    }

    public String doUpdateScopeSupply() {
        if (isValidDataUpdate()) {
            if (indexForScopeSupplyEditing >= 0) {
                ScopeSupplyEntity ss = scopeSupplyService.clone(scopeSupplyEditing);
                ss.getTdpList().clear();
                ss.getTdpList().addAll(scopeSupplyEditing.getTdpList());
                ss.getAttachments().addAll(scopeSupplyEditing.getAttachments());
                scopeSupplies.set(indexForScopeSupplyEditing, ss);
            }
            scopeActives.clear();
            scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
            return "/purchase/edit?faces-redirect=true&fase=1";
        }
        return "";
    }

    public String selectScopeSupply(final Long idScopeSupply) {
        currentOperation = SPLIT;
        final int index = scopeSupplyService.getIndexById(idScopeSupply, scopeSupplies);
        if (index >= 0) {
            selectedScopeSupply = scopeSupplyService.clone(scopeSupplies.get(index));
            originalQuantity = selectedScopeSupply.getQuantity();
            scopeSupplySplit = scopeSupplyService.clone(scopeSupplies.get(index));
            selectedScopeSupply.getTdpList().clear();
            selectedScopeSupply.getTdpList().addAll(scopeSupplies.get(index).getTdpList());
            selectedScopeSupply.getAttachments().clear();
            selectedScopeSupply.getAttachments().addAll(scopeSupplies.get(index).getAttachments());
            if (selectedScopeSupply.getActualSiteDate() == null) {
                selectedScopeSupply.setActualSiteDate(new Date());
            }
            scopeSupplySplit.setQuantity(new BigDecimal("0"));

        }
        return "/purchase/modal/EditModalSplitScopeSupply?faces-redirect=true&poId=" + idScopeSupply;
    }

    public String selectScopeSuppplyForEditing(Long id) {
        log.info("selectScopeSuppplyForEditing(Long id)");
        currentOperation = EDIT;//EDITING
        final int index = scopeSupplyService.getIndexById(id, scopeSupplies);
        indexForScopeSupplyEditing = -1;
        scopeSupplyEditing = scopeSupplyService.clone(scopeSupplies.get(index));
        scopeSupplyEditing.getTdpList().clear();

        indexForScopeSupplyEditing = index;
        scopeSupplyEditing.getTdpList().addAll(scopeSupplies.get(index).getTdpList());
        scopeSupplyEditing.getAttachments().addAll(scopeSupplies.get(index).getAttachments());
        updateTdpActives();
        return "/purchase/modal/EditModalScopeSupplyEditing?faces-redirect=true&poId=" + id;
    }

    public ScopeSupplyEntity getScopeSupplySplit() {
        return scopeSupplySplit;
    }

    public void downloadAttachedFileOnComment(final long id) {
        attachmentCommentService.download(id);
    }

    public void downloadAttachedFileOnScopeSupply(final long id) {
        attachmentScopeSupplyService.download(id);
    }

    public ScopeSupplyEntity getScopeSupplyEditing() {
        return scopeSupplyEditing;
    }

    public List<TransitDeliveryPointEntity> activesTdp() {
        return tdpActives;
    }

    private void updateTdpActives() {
        log.info("updateTdpActives");
        if (scopeSupplyEditing != null) {
            log.info("scopeSupplyEditing is not null");
            log.info("tdpActives size 1 " + tdpActives.size());
            tdpActives = tdpService.getActives(scopeSupplyEditing.getTdpList());
            log.info("tdpActives size 2 " + tdpActives.size());
        }
    }

    public CommentEntity getEditingComment() {
        return editingComment;
    }

    public Date calculateDate() {
        log.info("calculateDate()");
        Date date = null;
        switch (currentOperation) {
            case 0://CREATING
                if (!scopeSupplyEdit.getIsForecastSiteDateManual()) {
                    date = scopeSupplyService.calculateForecastSiteDate(scopeSupplyEdit);
                    scopeSupplyEdit.setForecastSiteDate(date);
                }
                break;
            case 1://EDITING
                if (!scopeSupplyEditing.getIsForecastSiteDateManual()) {
                    date = scopeSupplyService.calculateForecastSiteDate(scopeSupplyEditing);
                    scopeSupplyEditing.setForecastSiteDate(date);
                }
                break;
            case 2://SPLITING
                if (!getScopeSupplySplit().getIsForecastSiteDateManual()) {
                    date = scopeSupplyService.calculateForecastSiteDate(getScopeSupplySplit());
                    getScopeSupplySplit().setForecastSiteDate(date);
                }
                break;
            default:
                log.info("no possible computation");
                break;
        }
        log.info("date calculated " + date);
        return date;
    }

    public void calculateDateForecastDateForBulkUpdate(ScopeSupplyEntity scopeSupply) {
        Date date = scopeSupplyService.calculateForecastSiteDate(scopeSupply);
        scopeSupply.setForecastSiteDate(date);
        scopeSupply.setIsForecastSiteDateManual(false);
    }

    public boolean hasLeadTimeData() {
        boolean res = bulkScopeSupply.getDeliveryLeadTimeQt() != null ? true : false;
        return res;
    }

    public void switchModeForecastSiteDate() {
        log.info("public void switchModeForecastSiteDate()");
        switch (currentOperation) {
            case 0://CREATING
                scopeSupplyEdit.setForecastSiteDate(null);
                if (!scopeSupplyEdit.getIsForecastSiteDateManual()) {
                    calculateDate();
                }
                break;
            case 1://EDITING
                scopeSupplyEditing.setForecastSiteDate(null);
                if (!scopeSupplyEditing.getIsForecastSiteDateManual()) {
                    calculateDate();
                }
                break;
            case 2://SPLITING
                getScopeSupplySplit().setForecastSiteDate(null);
                if (!getScopeSupplySplit().getIsForecastSiteDateManual()) {
                    calculateDate();
                }
                break;
            default:
                log.info("no possible computation");
                break;
        }
    }

    public TransitDeliveryPointEntity getNewTdp() {
        return newTdp;
    }

    public TransitDeliveryPointEntity getTdpEdit() {
        return tdpEdit;
    }

    public void cleanTransitDeliveryPoint() {
        newTdp = new TransitDeliveryPointEntity();
        newTdp.setIsForecastSiteDateManual(false);
        newTdp.setMeasurementTime(TimeMeasurementEnum.DAY);
        currentIndexForTdp = -1;
    }

    public void selectTdp(Long id) {
        int index = tdpService.getIndexById(id, scopeSupplyEdit.getTdpList());
        if (index >= 0 && index < scopeSupplyEdit.getTdpList().size()) {
            tdpEdit = tdpService.clone(scopeSupplyEdit.getTdpList().get(index));
            currentIndexForTdp = index;
        }
    }

    public void selectTdpEditing(Long id) {
        int index = tdpService.getIndexById(id, scopeSupplyEditing.getTdpList());
        if (index >= 0 && index < scopeSupplyEditing.getTdpList().size()) {
            tdpEdit = tdpService.clone(scopeSupplyEditing.getTdpList().get(index));
            currentIndexForTdp = index;
        }
    }

    public void deleteTdp(Long id) {
        Integer index = tdpService.getIndexById(id, scopeSupplyEdit.getTdpList());
        if (index >= 0) {
            TransitDeliveryPointEntity tde = scopeSupplyEdit.getTdpList().get(index);
            if (tde.getId() != null && tde.getId() > 0) {
                tde.setStatus(enumService.getStatusEnumDeleted());
                tde.setLastUpdate(new Date());
            } else {
                scopeSupplyEdit.getTdpList().remove(index.intValue());
            }
            calculateDate();
        }
    }

    public void deleteTdpOnEdition(Long id) {
        log.info("deleting TDP on Edition...");
        Integer index = tdpService.getIndexById(id, scopeSupplyEditing.getTdpList());
        if (index >= 0) {
            TransitDeliveryPointEntity tde = scopeSupplyEditing.getTdpList().get(index);
            if (tde.getId() != null && tde.getId() > 0) {
                tde.setStatus(enumService.getStatusEnumDeleted());
                tde.setLastUpdate(new Date());
            } else {
                scopeSupplyEditing.getTdpList().remove(index.intValue());
            }
            updateTdpActives();
            calculateDate();
        }
    }

    public void doSaveTdp() {
        newTdp.setStatus(enumService.getStatusEnumEnable());
        temporaryIdForTdp--;
        newTdp.setId(temporaryIdForTdp);
        newTdp.setLastUpdate(new Date());
        scopeSupplyEdit.getTdpList().add(tdpService.clone(newTdp));
        calculateDate();
    }

    public void doSaveOnEdition() {
        newTdp.setStatus(enumService.getStatusEnumEnable());
        temporaryIdForTdp--;
        newTdp.setId(temporaryIdForTdp);
        newTdp.setLastUpdate(new Date());
        scopeSupplyEditing.getTdpList().add(tdpService.clone(newTdp));
        calculateDate();
        updateTdpActives();
    }

    public void doUpdateTdp() {
        if (currentIndexForTdp >= 0 && currentIndexForTdp < scopeSupplyEdit.getTdpList().size()) {
            tdpEdit.setLastUpdate(new Date());
            scopeSupplyEdit.getTdpList().set(currentIndexForTdp, tdpService.clone(tdpEdit));
            calculateDate();
        }
    }

    public void doUpdateTdpOnEdition() {
        if (currentIndexForTdp >= 0 && currentIndexForTdp < scopeSupplyEditing.getTdpList().size()) {
            tdpEdit.setLastUpdate(new Date());
            scopeSupplyEditing.getTdpList().set(currentIndexForTdp, tdpService.clone(tdpEdit));
            calculateDate();
            updateTdpActives();
        }
    }

    public void doSaveContact() {
        ContactEntity contact = contactBean.doSave();
        if (contact != null) {
            poEdit.getPurchaseOrderProcurementEntity().setContactExpediting(contact);
            poEdit.getPurchaseOrderProcurementEntity().getSupplier().getContacts().add(contact);
        }
    }

    public String cancelEditPurchaseOrder() {
        if (!conversation.isTransient()) {
            log.info("Finish conversation...");
            conversation.end();
        }
        return "/purchase/list?faces-redirect=true&anchor="+anchor;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public void calulateForecasteDateForTdpCreation() {
        log.info("calulateForecasteDateForTdpCreation....");
        if (!newTdp.getIsForecastSiteDateManual()) {
            List<TransitDeliveryPointEntity> list = scopeSupplyEdit.getTdpList();
            TransitDeliveryPointEntity tdpPrevious = list.size() > 0 ? list.get(list.size() - 1) : null;
            newTdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEdit, scopeSupplyEdit.getTdpList().size() == 0, tdpPrevious, newTdp));
        }
    }

    public void calulateForecasteDateForTdpEdition() {
        log.info("calulateForecasteDateForTdpEdition....");
        if (!tdpEdit.getIsForecastSiteDateManual()) {
            List<TransitDeliveryPointEntity> list = scopeSupplyEdit.getTdpList();
            TransitDeliveryPointEntity tdpPrevious = list.size() > 1 && currentIndexForTdp > 0 ? list.get(currentIndexForTdp - 1) : null;
            tdpEdit.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEdit, currentIndexForTdp.intValue() == 0, tdpPrevious, tdpEdit));
        }
    }

    public void calulateForecasteDateForTdpCreation1() {
        log.info("calulateForecasteDateForTdpCreation1....");
        if (!newTdp.getIsForecastSiteDateManual()) {
            List<TransitDeliveryPointEntity> list = scopeSupplyEditing.getTdpList();
            TransitDeliveryPointEntity tdpPrevious = list.size() > 0 ? list.get(list.size() - 1) : null;
            newTdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEditing, scopeSupplyEditing.getTdpList().size() == 0, tdpPrevious, newTdp));
        }
    }

    public void calulateForecasteDateForTdpEdition1() {
        log.info("calulateForecasteDateForTdpEdition1....");
        if (!tdpEdit.getIsForecastSiteDateManual()) {
            List<TransitDeliveryPointEntity> list = scopeSupplyEditing.getTdpList();
            TransitDeliveryPointEntity tdpPrevious = list.size() > 1 && currentIndexForTdp > 0 ? list.get(currentIndexForTdp - 1) : null;
            tdpEdit.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEditing, currentIndexForTdp.intValue() == 0, tdpPrevious, tdpEdit));
        }
    }

    public void saveSupplier() {
        SupplierProcEntity supplierProcEntity = supplier.save();
        if (supplierProcEntity != null) {
            poEdit.getPurchaseOrderProcurementEntity().setSupplier(supplierProcEntity);
            poEdit.getPurchaseOrderProcurementEntity().setContactEntity(null);
            list.updateSupplierList();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('supplierModal').hide();");
        }
    }

    public void disabledForecastSiteDate() {
        hasValueLeadTime = bulkScopeSupply.getDeliveryLeadTimeQt() != null || bulkScopeSupply.getDeliveryLeadTimeMs() != null;
    }

    public void disabledLeadTime() {
        log.info("disabledLeadTime");
        hasValueForecastSiteDate = bulkScopeSupply.getForecastSiteDate() != null;
    }

    public void updateScopeSupplyExcludeFromExpedite() {
        currentScopeSupply.setExcludeFromExpediting(true);
    }

    public void addItemToExpedite() {
        currentScopeSupply.setExcludeFromExpediting(false);
    }

    public void addingSupplier() {
        supplier.putModeCreation();
        supplier.start();
    }

    public ScopeSupplyEntity getCurrentScopeSupply() {

        return currentScopeSupply;
    }

    public void setCurrentScopeSupply(ScopeSupplyEntity currentScopeSupply) {
        this.currentScopeSupply = currentScopeSupply;
    }

    public ScopeSupplyEntity getBulkScopeSupply() {
        return bulkScopeSupply;
    }

    public void setBulkScopeSupply(ScopeSupplyEntity bulkScopeSupply) {
        this.bulkScopeSupply = bulkScopeSupply;
    }

    public String getTitleBulkUpdateModal() {
        return titleBulkUpdateModal;
    }

    public void setTitleBulkUpdateModal(String titleBulkUpdateModal) {
        this.titleBulkUpdateModal = titleBulkUpdateModal;
    }

    public String getExpeditingStatuses() {
        return expeditingStatuses;
    }

    public void setExpeditingStatuses(String expeditingStatuses) {
        this.expeditingStatuses = expeditingStatuses;
    }

    public boolean isHasValueLeadTime() {
        return hasValueLeadTime;
    }

    public void setHasValueLeadTime(boolean hasValueLeadTime) {
        this.hasValueLeadTime = hasValueLeadTime;
    }

    public boolean isHasValueForecastSiteDate() {
        return hasValueForecastSiteDate;
    }

    public void setHasValueForecastSiteDate(boolean hasValueForecastSiteDate) {
        this.hasValueForecastSiteDate = hasValueForecastSiteDate;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }
}
