package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.fqmes.control.attachment.AttachmentService;
import ch.swissbytes.fqmes.control.comment.CommentService;
import ch.swissbytes.fqmes.control.enumService.EnumService;
import ch.swissbytes.fqmes.control.purchase.PurchaseOrderService;
import ch.swissbytes.fqmes.control.scopesupply.ScopeSupplyService;
import ch.swissbytes.fqmes.model.dao.SupplierDao;
import ch.swissbytes.fqmes.model.entities.*;
import org.apache.commons.io.IOUtils;
import org.omnifaces.util.Messages;
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
import java.io.IOException;
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
public class PurchaseOrderEdit implements Serializable{

    @Inject
    private SupplierDao supplierDao;

    @Inject
    private CommentService commentService;

    @Inject
    private ScopeSupplyService scopeSupplyService;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private AttachmentService attachmentService;

    @Inject
    private EnumService enumService;
@Inject
private Conversation conversation;

    private String purchaseOrderId;

    private PurchaseOrderEntity poEdit;

    private List<CommentEntity> comments;

    private List<AttachmentEntity> attachments;

    private List<ScopeSupplyEntity>scopeSupplies;

    private ScopeSupplyEntity selectedScopeSupply;

    private ScopeSupplyEntity scopeSupplySplit;

    private SupplierEntity supplierEdit;

    private CommentEntity commentEdit;

    private CommentEntity editingComment;

    private Integer commentIndexSelected;

    private ScopeSupplyEntity scopeSupplyEdit;

    private Integer currentOperation=-1;


    private Long relativeCurrentCommentId=0L;
    private Long relativeCurrentAttachmentId=0L;
    private Long relativeCurrentScopeSupplyId =0L;

    private ScopeSupplyEntity scopeSupplyEditing;

    private Integer indexForScopeSupplyEditing=-1;

    private final static Integer SPLIT=2;
    private final static Integer CREATE=0;
    private final static Integer EDIT=1;


    private Integer originalQuantity;

    private static final Logger log = Logger.getLogger(PurchaseOrderEdit.class.getName());




    public void load(){
        log.info("loading...");
        originalQuantity =0;
            poEdit =service.load(Long.parseLong(purchaseOrderId));
            supplierEdit=supplierDao.findByPurchaseOrder(poEdit.getId());

                supplierEdit=supplierDao.findByPurchaseOrder(poEdit.getId());
                if(comments==null||comments.size()==0){
                    comments=commentService.findByPurchaseOrder(poEdit.getId());
                }else{

                }
                if(scopeSupplies==null||scopeSupplies.size()==0){
                    scopeSupplies=scopeSupplyService.findByPurchaseOrder(poEdit.getId());
                }
                if(attachments==null||attachments.size()==0){
                    attachments=attachmentService.findByPurchaseOrder(poEdit.getId());
                }

        commentIndexSelected =-1;
    }

    @PostConstruct
    public void create(){
       // poEdit=new PurchaseOrderEntity();
        comments=new ArrayList<>();
        commentEdit=new CommentEntity();
        attachments=new ArrayList<>();
        //scopeSupplies=new ArrayList<>();
        log.log(Level.INFO,String.format("creating bean [%s]",this.getClass().toString()));
        if(conversation.isTransient()){
            conversation.begin();
        }
    }
    @PreDestroy
    public void destroy(){
        log.log(Level.INFO, String.format("bean destroyed [%s]", this.getClass().toString()));
    }
    public String doUpdate(){
        log.info("upgrading...");
        updateStatusesAndLastUpdate();
        service.doUpdate(poEdit,supplierEdit,comments,scopeSupplies,attachments);
        if(!conversation.isTransient()){
            conversation.end();
        }
        return "view?faces-redirect=true&poId="+poEdit.getId();
    }
    private void updateStatusesAndLastUpdate(){
        PurchaseOrderEntity oldPurchaseOrder=service.load(poEdit.getId());
        Date now =new Date();
        if(!oldPurchaseOrder.equals(poEdit)){
            poEdit.setLastUpdate(now);
        }
       SupplierEntity oldSupplier= supplierDao.load(supplierEdit.getId());
        if(!oldSupplier.equals(supplierEdit)){
            supplierEdit.setLastUpdate(now);
        }
        for(final CommentEntity commentEntity:comments){
            if(commentEntity.getId()<=0){
                commentEntity.setId(null);
                commentEntity.setLastUpdate(now);
            }else{
                CommentEntity oldComment=commentService.load(commentEntity.getId());
                if(!oldComment.equals(commentEntity)){
                    commentEntity.setLastUpdate(now);
                }
            }
        }
        for(final ScopeSupplyEntity scopeSupply:scopeSupplies){
            if(scopeSupply.getId()<=0){
                scopeSupply.setId(null);
                scopeSupply.setLastUpdate(now);
            }else{
                ScopeSupplyEntity oldScopeSupply=scopeSupplyService.load(scopeSupply.getId());
                if(!oldScopeSupply.equals(scopeSupply)){
                    scopeSupply.setLastUpdate(now);
                }
            }
        }
        for(final AttachmentEntity attachment:attachments){
            if(attachment.getId()<=0){
                attachment.setId(null);
                attachment.setLastUpdate(now);
            }else{
                AttachmentEntity oldAttachment=attachmentService.load(attachment.getId());
                if(!oldAttachment.equals(attachment)){
                    attachment.setLastUpdate(now);
                }
            }
        }
    }
    public void addComment(){
        log.log(Level.INFO,"COMMENT NAME    "+commentEdit.getName());
        log.log(Level.INFO,"size    "+comments.size());
        CommentEntity commentEntity=new CommentEntity();
        try{
            commentEntity=(CommentEntity)org.apache.commons.beanutils.BeanUtils.cloneBean(commentEdit);
            relativeCurrentCommentId--;
            commentEntity.setId(relativeCurrentCommentId);
        }catch (Exception ex){

        }
        commentEntity.setStatus(enumService.getStatusEnumEnable());
        comments.add(commentEntity);
    }
    private void registerScopeSupply(){
        log.log(Level.INFO,"SCOPE CODE    "+scopeSupplyEdit.getCode());
        ScopeSupplyEntity scopeSupplyEntity=new ScopeSupplyEntity();
        try{
            scopeSupplyEntity=(ScopeSupplyEntity)org.apache.commons.beanutils.BeanUtils.cloneBean(scopeSupplyEdit);
        }catch (Exception ex){
        }
        scopeSupplyEntity.setStatus(enumService.getStatusEnumEnable());
        relativeCurrentAttachmentId--;
        scopeSupplyEntity.setId(relativeCurrentScopeSupplyId);
        scopeSupplies.add(scopeSupplyEntity);

    }
    public void handleCommentUpload(FileUploadEvent event){
        UploadedFile uf=event.getFile();
        log.info(uf.getFileName());
        if(commentIndexSelected!=null&&commentIndexSelected>=0){
            try{
                editingComment.setFile(IOUtils.toByteArray(uf.getInputstream()));
            }catch (IOException ioe){
            }
            editingComment.setFileName(uf.getFileName());
            editingComment.setMimeType(uf.getContentType());
        }else{
            try{
                commentEdit.setFile(IOUtils.toByteArray(uf.getInputstream()));
            }catch (IOException ioe){
            }
            commentEdit.setFileName(uf.getFileName());
            commentEdit.setMimeType(uf.getContentType());
        }


    }

    public List<AttachmentEntity> getAttachmentActives(){
        return attachmentService.getActives(attachments);
    }

    public void cleanComment(){
        commentEdit=new CommentEntity();
        commentIndexSelected =-1;
    }

    public String cleanScopeSupply(){
        currentOperation=CREATE;//CREATING...
        scopeSupplyEdit=new ScopeSupplyEntity();
        scopeSupplyEdit.setIsForecastSiteDateCalculated(true);
        return "/purchase/modal/EditModalScopeSupply?faces-redirect=true";
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


    public SupplierEntity getSupplierEdit() {
        return supplierEdit;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
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

    public List<CommentEntity> getActiveComments(){
        return commentService.getActives(comments);
    }


    @Produces
    @ViewScoped
    @Named
    public ScopeSupplyEntity getScopeSupplyEdit() {
        return scopeSupplyEdit;
    }

    public void deleteComment(Long id) {
        log.info("deleting commment ");
        Integer index = commentService.getIndexById(id,comments);
        if (index >= 0) {
            CommentEntity comment = comments.get(index);
            if (comment.getId() > 0) {
                comment.setStatus(enumService.getStatusEnumDeleted());
            } else {
                log.info("comments previous "+comments.size());
                comments.remove(index.intValue());
                log.info("comments " + comments.size());
            }
        } else {

        }
    }
    public void editComment(Long id) {
        Integer index = commentService.getIndexById(id,comments);
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

    public void selectComment(final Long id){
        commentIndexSelected =-1;
        Integer index = commentService.getIndexById(id,comments);
        if (index >= 0) {
            commentIndexSelected =index;
            editingComment= commentService.clone(comments.get(index));
        }
    }
    public void doUpdateComment(){
        Integer index = commentService.getIndexById(editingComment.getId(),comments);
        if(index>=0&&index<comments.size()){
            comments.set(index,commentService.clone(editingComment));
        }
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
    }

    public void deleteAttachment(Long id) {
        int index = attachmentService.getIndexById(id, attachments);
        if (index >= 0) {
            AttachmentEntity attachmentEntity = attachments.get(index);
            if (attachmentEntity.getId() != null) {
                attachmentEntity.setStatus(enumService.getStatusEnumDeleted());
            } else {
                attachments.remove(index);
            }
        }
    } 
    public List<ScopeSupplyEntity>getActiveScopeSupplies(){
//        log.info("size about scope supply "+scopeSupplies.size());
       return scopeSupplyService.getActives(scopeSupplies);
    }

    public String addScopeSupply(){
        registerScopeSupply();
        return "/purchase/edit?faces-redirect=true";
    }

    public void addScopeSupplyAndAdd(){
        registerScopeSupply();
        cleanScopeSupply();
    }
    public String cancel(){
        return "/purchase/edit?faces-redirect=true";
    }

    public String split(){
        log.info("split....");
        if(originalQuantity.intValue()>0&& scopeSupplySplit.getQuantity().intValue()<originalQuantity.intValue()){
            //selectedScopeSupply.setQuantity(originalQuantity.intValue()-scopeSupplySplit.getQuantity().intValue());
            relativeCurrentAttachmentId--;
            scopeSupplySplit.setId(relativeCurrentScopeSupplyId);
            scopeSupplySplit.setStatus(enumService.getStatusEnumEnable());
            scopeSupplies.add(scopeSupplyService.clone(scopeSupplySplit));
            final int index=scopeSupplyService.getIndexById(selectedScopeSupply.getId(),scopeSupplies);
            if(index>=0 && index <scopeSupplies.size()){
                scopeSupplies.set(index,scopeSupplyService.clone(selectedScopeSupply));
            }
        }else{
            Messages.addGlobalError("invalid quantity");
        }
        log.info("finished splitting");
        return "/purchase/edit?faces-redirect=true";
    }

    public String doUpdateScopeSupply(){
        if(indexForScopeSupplyEditing>=0){
            scopeSupplies.set(indexForScopeSupplyEditing,scopeSupplyService.clone(scopeSupplyEditing));
        }
        return "/purchase/edit?faces-redirect=true";
    }

    public String selectScopeSupply(final Long idScopeSupply){
        currentOperation=SPLIT;//spliting...
        final int index=scopeSupplyService.getIndexById(idScopeSupply,scopeSupplies);
        if(index>=0){
            selectedScopeSupply=scopeSupplyService.clone(scopeSupplies.get(index));
            originalQuantity=selectedScopeSupply.getQuantity();
            scopeSupplySplit=scopeSupplyService.clone(selectedScopeSupply);
            scopeSupplySplit.setQuantity(0);
        }
        return "/purchase/modal/EditModalSplitScopeSupply?faces-redirect=true&poId="+idScopeSupply;
    }

    public void calculateNewScopeSupplyQuantity(){
        if(selectedScopeSupply.getQuantity().intValue()>=0 && selectedScopeSupply.getQuantity().intValue()<originalQuantity){
            scopeSupplySplit.setQuantity(originalQuantity.intValue()-selectedScopeSupply.getQuantity().intValue());
        }else{
            selectedScopeSupply.setQuantity(originalQuantity);
            scopeSupplySplit.setQuantity(0);
        }
    }
    public void calculateOldScopeSupplyQuantity(){
        if(scopeSupplySplit.getQuantity().intValue()>=0 && scopeSupplySplit.getQuantity().intValue()<originalQuantity){
            selectedScopeSupply.setQuantity(originalQuantity.intValue()-scopeSupplySplit.getQuantity().intValue());
        }else{
            selectedScopeSupply.setQuantity(originalQuantity);
            scopeSupplySplit.setQuantity(0);
        }
    }

    public String selectScopeSuppplyForEditing(Long id){
        currentOperation=EDIT;//EDITING
        final int index=scopeSupplyService.getIndexById(id,scopeSupplies);
        indexForScopeSupplyEditing=-1;
        if(index>=0){
            scopeSupplyEditing=scopeSupplyService.clone(scopeSupplies.get(index));
            indexForScopeSupplyEditing=index;
        }

        return "/purchase/modal/EditModalScopeSupplyEditing?faces-redirect=true&poId="+id;
    }

    public ScopeSupplyEntity getScopeSupplySplit() {
        return scopeSupplySplit;
    }

    public void downloadAttachmentFile(final long id){
        log.info("public void downloadAttachmentFile(final long id)");
        attachmentService.download(id);
    }
    public void downloadAttachedFileOnComment(final long id){
        log.info("public void downloadAttachedFileOnComment(final long id)");
        commentService.download(id);
    }

    public ScopeSupplyEntity getScopeSupplyEditing() {
        return scopeSupplyEditing;
    }

    public CommentEntity getEditingComment() {
        return editingComment;
    }

    public Date calculateDate() {
        Date date=null;
        switch (currentOperation){
            case 0://CREATING
                if(scopeSupplyEdit.getIsForecastSiteDateCalculated()){
                    date=scopeSupplyService.calculateForecastSiteDate(scopeSupplyEdit);
                    scopeSupplyEdit.setSiteDate(date);
                }
                break;
            case 1://EDITING
                if(scopeSupplyEditing.getIsForecastSiteDateCalculated()){
                    date=scopeSupplyService.calculateForecastSiteDate(scopeSupplyEditing);
                    scopeSupplyEditing.setSiteDate(date);
                }
                break;
            case 2://SPLITING
                if(getScopeSupplySplit().getIsForecastSiteDateCalculated()){
                    date=scopeSupplyService.calculateForecastSiteDate(getScopeSupplySplit());
                    getScopeSupplySplit().setSiteDate(date);
                }
                break;
            default:
                log.info("no possible computation");
                break;
        }

        log.info("date calculated "+date);
        return date;
    }
    public void switchModeForecastSiteDate() {
        log.info("public void switchModeForecastSiteDate()");
        switch (currentOperation){
            case 0://CREATING
                if(scopeSupplyEdit.getIsForecastSiteDateCalculated()){
                    scopeSupplyEdit.setSiteDate(null);
                }else{
                    calculateDate();
                }
                break;
            case 1://EDITING
                if(scopeSupplyEditing.getIsForecastSiteDateCalculated()){
                    scopeSupplyEditing.setSiteDate(null);
                }else{
                    calculateDate();
                }
                break;
            case 2://SPLITING
                if(getScopeSupplySplit().getIsForecastSiteDateCalculated()){
                    getScopeSupplySplit().setSiteDate(null);
                }else{
                    calculateDate();
                }
                break;
            default:
                log.info("no possible computation");
                break;
        }
    }

}
