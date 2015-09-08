package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqm.boundary.UserSession;
import ch.swissbytes.fqmes.boundary.scopeSupply.ScopeSupplyBean;
import ch.swissbytes.Service.business.comment.CommentService;
import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.PurchaseOrderStatusEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Purchase;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcBean;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcList;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
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
public class PurchaseOrderCreate implements Serializable {

    @Inject
    private PurchaseOrderService service;

    @Inject
    private EnumService enumService;

    @Inject
    private CommentService commentService;

    @Inject
    private ScopeSupplyService scopeSupplyService;

    @Inject
    private ScopeSupplyBean scopeSupplyBean;

    @Inject
    private SortBean sortBean;
    @Inject
    private UserSession userSession;

    @Inject
    private SupplierProcBean supplier;

    @Inject
    private SupplierProcList list;

    private PurchaseOrderEntity newPurchaseOrder;

    private SupplierEntity newSupplier;

    private ScopeSupplyEntity newScopeSupply;

    private CommentEntity newComment;

    private CommentEntity editComment;

    private Integer indexCommentEditing;

    private List<CommentEntity> commentEntities;

    private List<ScopeSupplyEntity> scopeSupplies;

    private ScopeSupplyEntity selectedScopeSupply;

    private ScopeSupplyEntity editScopeSupply;

    private Integer indexScopeSupplyEditing;

    private Integer indexAttachment;

    private static final Logger log = Logger.getLogger(PurchaseOrderCreate.class.getName());

    @Inject
    private Configuration configuration;

    @Inject
    private Conversation conversation;

    @Inject
    private ProjectService projectService;


    @PostConstruct
    public void create() {
        log.log(Level.INFO, String.format("creating bean [%s]", this.getClass().toString()));
        reset();
        if (conversation.isTransient()) {
            conversation.begin();
            conversation.setTimeout(configuration.getTimeOutConversation());
            log.info(String.format("conversation started [%s]", conversation.getTimeout()));
        }
    }

    public void load() {
        log.info("void load()");
        if (scopeSupplies != null && !scopeSupplies.isEmpty()) {
            sortBean.sortScopeSupplyEntity(scopeSupplies);
        }
    }

    private void reset() {
        newPurchaseOrder = new PurchaseOrderEntity();
        newSupplier = new SupplierEntity();
        commentEntities = new ArrayList<>();
        newComment = new CommentEntity();
        scopeSupplies = new ArrayList<>();
        newScopeSupply = new ScopeSupplyEntity();
        newPurchaseOrder.setPurchaseOrderStatus(PurchaseOrderStatusEnum.ISSUED);
        newPurchaseOrder.setPoEntity(new POEntity());
        newPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.COMMITED);
        newPurchaseOrder.setProjectEntity(projectService.findProjectById(1L));
    }

    public void cleanComment() {
        log.log(Level.INFO, "cleaning comments");
        indexCommentEditing = -1;
        newComment = new CommentEntity();
    }

    @Deprecated
    public String doSave() {
        //TODO probably it should be deleted (all this method)
        /*if(ModuleSystemEnum.EXPEDITING.name().equalsIgnoreCase(userSession.getCurrentModule())){
            log.log(Level.SEVERE, "Somebody is trying to save a PO from expediting module and this is not allowed");
            Messages.addGlobalError("you cannot save a PO");
            return "";
        }*/
        savePurchase();
        if (!conversation.isTransient()) {
            conversation.end();
        }
        return "view?faces-redirect=true&poId=" + newPurchaseOrder.getId();
    }


    @Deprecated
    public String doSaveAndAdd() {
        //TODO probably it should be deleted (all this method)
       /* if(ModuleSystemEnum.EXPEDITING.name().equalsIgnoreCase(userSession.getCurrentModule())){
            log.log(Level.SEVERE,"Somebody is trying to save a PO from expediting module and this is not allowed");
            Messages.addGlobalError("you cannot save a PO");
            return "";
        }*/
        savePurchase();
        reset();
        if (!conversation.isTransient()) {
            conversation.end();
        }
        return "create?faces-redirect=true";
    }

    private void savePurchase() {
        log.log(Level.INFO, String.format("trying to save purchase order"));
        initializeStatusesAndLastUpdate();
        service.doSave(newPurchaseOrder, getCommentEntities(), scopeSupplies, newSupplier);
    }

    private void initializeStatusesAndLastUpdate() {
        Date now = new Date();
        StatusEntity enabled = enumService.getStatusEnumEnable();
        newPurchaseOrder.setStatus(enabled);
        newPurchaseOrder.setLastUpdate(now);
        newSupplier.setStatus(enabled);
        newSupplier.setLastUpdate(now);

        sortBean.sortScopeSupplyEntity(scopeSupplies);
        int ordered = 1;
        for (ScopeSupplyEntity scopeSupplyEntity : scopeSupplies) {
            scopeSupplyEntity.setStatus(enabled);
            scopeSupplyEntity.setOrdered(ordered);
            scopeSupplyEntity.setLastUpdate(now);
            for (AttachmentScopeSupply attachment : scopeSupplyEntity.getAttachments()) {
                attachment.setLastUpdate(now);
                attachment.setStatus(enabled);
            }
            ordered++;
        }
    }

    public String addComment() {
        log.log(Level.INFO, "COMMENT NAME    " + newComment.getName());
        log.log(Level.INFO, "size    " + commentEntities.size());
        CommentEntity commentEntity = new CommentEntity();
        try {
            commentEntity = (CommentEntity) org.apache.commons.beanutils.BeanUtils.cloneBean(newComment);
            commentEntity.getAttachments().addAll(newComment.getAttachments());
        } catch (Exception ex) {

        }
        commentEntity.setLastUpdate(new Date());
        commentEntity.setStatus(enumService.getStatusEnumEnable());
        commentEntities.add(commentEntity);
        return "";
    }


    public void doUpdateScopeSupply() {
        if (indexScopeSupplyEditing >= 0) {
            scopeSupplies.set(indexScopeSupplyEditing, scopeSupplyService.clone(editScopeSupply));
        }
    }

    public String addScopeSupplyAndAdd() {
        registerScopeSupply();
        cleanScopeSupply();
        return "";
    }

    private void registerScopeSupply() {
        log.log(Level.INFO, "SCOPE CODE    " + newScopeSupply.getCode());
        ScopeSupplyEntity scopeSupplyEntity = new ScopeSupplyEntity();
        try {
            scopeSupplyEntity = (ScopeSupplyEntity) org.apache.commons.beanutils.BeanUtils.cloneBean(newScopeSupply);
        } catch (Exception ex) {
        }
        scopeSupplies.add(scopeSupplyEntity);
    }

    public void deleteComment(final int index) {
        if (index >= 0 && index < commentEntities.size()) {
            commentEntities.remove(index);
        }
    }

    public void editComment(final int index) {
        log.info("editComment(index[" + index + "])");
        indexCommentEditing = -1;
        log.info("commentEntities.size(): " + commentEntities.size());
        if (index >= 0 && index < commentEntities.size()) {
            editComment = commentService.clone(commentEntities.get(index));
            editComment.getAttachments().addAll(commentEntities.get(index).getAttachments());
            editComment.setLastUpdate(new Date());
            indexCommentEditing = index;
        }
    }

    public void updateComment() {
        if (indexCommentEditing >= 0 && indexCommentEditing < commentEntities.size()) {
            commentEntities.set(indexCommentEditing, editComment);
        }
    }


    public void deleteScopeSupply(final int index) {
        if (index >= 0 && index < scopeSupplies.size()) {
            scopeSupplies.remove(index);
        }
    }

    public void selectingForAttachment(final int index) {
        indexAttachment = index;
        log.info("indexAttachment "+indexAttachment);
    }

    public ScopeSupplyEntity currentScopeSupplyForAttachment(){
        return indexAttachment!=null?scopeSupplies.get(indexAttachment):null;
    }

    public List<AttachmentComment>getAttachmentsForComment(){
        List<AttachmentComment> list=new ArrayList<AttachmentComment>();
        if(indexCommentEditing!=null&&indexCommentEditing==-1&&newComment!=null){
            list=newComment.getAttachments();
        }else if(indexCommentEditing!=null&&indexCommentEditing!=-1&&editComment!=null){
            list=editComment.getAttachments();
        }
        return list;
    }

    public PurchaseOrderEntity getNewPurchaseOrder() {
        return newPurchaseOrder;
    }

    public void handleCommentUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        log.info(uf.getFileName());
        log.info("size [ " + uf.getSize() + "]");
        log.info("indexFileAttachment[ " + indexAttachment + "]");
        AttachmentComment attachmentComment = new AttachmentComment();
        new Util().enterFile(uf, attachmentComment);
        attachmentComment.setStatus(enumService.getStatusEnumEnable());
        if (indexCommentEditing == -1) {
            newComment.getAttachments().add(attachmentComment);
        } else {
            editComment.getAttachments().add(attachmentComment);
        }

    }

    public List<AttachmentScopeSupply> getScopeSupplyAttachments() {
        return (indexAttachment != null && indexAttachment >= 0 && indexAttachment < scopeSupplies.size()) ? scopeSupplies.get(indexAttachment).getAttachments() : new ArrayList<AttachmentScopeSupply>();
    }

    public void deleteAttachmentScopeSupply(int index) {
        if (indexAttachment != null && indexAttachment >= 0 && indexAttachment < scopeSupplies.size()) {
            if (index >= 0 && index < scopeSupplies.get(indexAttachment).getAttachments().size()) {
                scopeSupplies.get(indexAttachment).getAttachments().remove(index);
            }
        }
    }

    public void deleteAttachmentComment(int index) {
        if(indexCommentEditing==-1){
            newComment.getAttachments().remove(index);
        }else{
            editComment.getAttachments().remove(index);
        }
    }

    public void handleAttachmentScopeSupply(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        if (indexAttachment != null && indexAttachment >= 0 && indexAttachment < scopeSupplies.size()) {
            AttachmentScopeSupply attachment = new AttachmentScopeSupply();
            new Util().enterFile(uf, attachment);
            scopeSupplies.get(indexAttachment).getAttachments().add(attachment);
        }
    }

    @PreDestroy
    public void destroy() {
        log.log(Level.INFO, String.format("bean destroyed [%s]", this.getClass().toString()));
    }


    public SupplierEntity getNewSupplier() {
        return newSupplier;
    }


    public CommentEntity getNewComment() {
        return newComment;
    }

    public List<CommentEntity> getCommentEntities() {
        return commentEntities;
    }


    public String cleanScopeSupply() {
        log.info("cleaning scope supply");

        String cid = conversation.getId();
        return "/purchase/modal/CreateModalScopeSupply?faces-redirect=true&cid=" + cid;
    }

    public String selectForEditingScopeSuppply(Integer index) {
        log.info("selectForEditingScopeSupply.................");
        String cid = conversation.getId();
        return "/purchase/modal/CreateModalScopeSupplyEditing?faces-redirect=true&index=" + index + "&cid=" + cid;
    }

    public String cancelCreatePurchaseOrder() {
        if (!conversation.isTransient()) {
            log.info("Finish conversation...");
            conversation.end();
        }
        return "/purchase/list?faces-redirect=true";
    }


    public CommentEntity getEditComment() {
        return editComment;
    }


    public ScopeSupplyEntity getNewScopeSupply() {
        return newScopeSupply;
    }


    public ScopeSupplyEntity getSelectedScopeSupply() {
        return selectedScopeSupply;
    }

    public void setSelectedScopeSupply(ScopeSupplyEntity selectedScopeSupply) {
        this.selectedScopeSupply = selectedScopeSupply;
    }

    @ConversationScoped
    @Named
    @Produces
    public List<ScopeSupplyEntity> getScopeSupplies() {
        log.info("List<ScopeSupplyEntity> getScopeSupplies()");
        return scopeSupplies;
    }

    public ScopeSupplyEntity getEditScopeSupply() {
        return editScopeSupply;
    }


    @Purchase
    @ConversationScoped
    @Named
    @Produces
    public PurchaseOrderEntity getPurchaseOrder() {
        return (PurchaseOrderEntity) service.clone(newPurchaseOrder);
    }

        public void saveSupplier(){
            SupplierProcEntity supplierProcEntity = supplier.save();
            if (supplierProcEntity != null) {
                newPurchaseOrder.getPoEntity().setSupplier(supplierProcEntity);
                newPurchaseOrder.getPoEntity().setContactEntity(null);
                list.updateSupplierList();
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('supplierModal').hide();");
            }

    }

    public void addingSupplier() {
        supplier.putModeCreation();
        supplier.start();
    }
}
