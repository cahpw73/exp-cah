package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.fqmes.control.comment.CommentService;
import ch.swissbytes.fqmes.control.enumService.EnumService;
import ch.swissbytes.fqmes.control.purchase.PurchaseOrderService;
import ch.swissbytes.fqmes.control.scopesupply.ScopeSupplyService;
import ch.swissbytes.fqmes.model.entities.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
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
@ViewScoped
public class PurchaseOrderCreate implements Serializable {

    @Inject
    private PurchaseOrderService service;

    @Inject
    private EnumService enumService;

    @Inject
    private CommentService commentService;

    @Inject
    private ScopeSupplyService scopeSupplyService;

    private PurchaseOrderEntity newPurchaseOrder;

    private SupplierEntity newSupplier;

    private ScopeSupplyEntity newScopeSupply;

    private CommentEntity newComment;

    private CommentEntity editComment;

    private Integer indexCommentEditing;

    private List<CommentEntity> commentEntities;

    private List<AttachmentEntity> atachmentEntities;

    private List<ScopeSupplyEntity>scopeSupplies;

    private ScopeSupplyEntity selectedScopeSupply;

    private ScopeSupplyEntity editScopeSupply;

    private Integer indexScopeSupplyEditing;

    private static final Logger log = Logger.getLogger(PurchaseOrderCreate.class.getName());

    @PostConstruct
    public void create(){
        log.log(Level.INFO,String.format("creating bean [%s]",this.getClass().toString()));
        newPurchaseOrder =new PurchaseOrderEntity();
        newSupplier =new SupplierEntity();
        commentEntities=new ArrayList<>();
        newComment=new CommentEntity();
        atachmentEntities =new ArrayList<>();
        scopeSupplies=new ArrayList<>();
        newScopeSupply=new ScopeSupplyEntity();
    }

    public void cleanComment(){
        log.log(Level.INFO,"cleaning comments");
        indexCommentEditing=-1;
        newComment =new CommentEntity();
    }

    public String doSave(){
        savePurchase();
        return "view?faces-redirect=true&poId="+newPurchaseOrder.getId();
    }
    public String doSaveAndAdd(){
        savePurchase();
        return "create?faces-redirect=true";
    }

    private void savePurchase(){
        log.log(Level.INFO, String.format("trying to save purchase order"));
        initializeStatusesAndLastUpdate();
        service.doSave(newPurchaseOrder, getCommentEntities(), scopeSupplies, newSupplier, atachmentEntities);
    }

    private void initializeStatusesAndLastUpdate(){
        Date now=new Date();
        StatusEntity enabled=enumService.getStatusEnumEnable();
        newPurchaseOrder.setStatus(enabled);
        newPurchaseOrder.setLastUpdate(now);
        newSupplier.setStatus(enabled);
        newSupplier.setLastUpdate(now);
        for(AttachmentEntity attachmentEntity:getAtachmentEntities()){
            attachmentEntity.setStatus(enabled);
            attachmentEntity.setLastUpdate(now);
        }
        for(CommentEntity commentEntity:getCommentEntities()){
            commentEntity.setStatus(enabled);
            commentEntity.setLastUpdate(now);
        }
        for(ScopeSupplyEntity scopeSupplyEntity:getScopeSupplies()){
            scopeSupplyEntity.setStatus(enabled);
            scopeSupplyEntity.setLastUpdate(now);
        }
    }


    public String addComment(){
        log.log(Level.INFO,"COMMENT NAME    "+newComment.getName());
        log.log(Level.INFO,"size    "+commentEntities.size());
        CommentEntity commentEntity=new CommentEntity();
        try{
            commentEntity=(CommentEntity)org.apache.commons.beanutils.BeanUtils.cloneBean(newComment);
        }catch (Exception ex){

        }
        commentEntities.add(commentEntity);
        return "";
    }
    public String addScopeSupply(){
        log.info("public String addScopeSupply()");
        registerScopeSupply();
        return "";
    }

    public void doUpdateScopeSupply(){
        if(indexScopeSupplyEditing>=0){
            scopeSupplies.set(indexScopeSupplyEditing,scopeSupplyService.clone(editScopeSupply));
        }
    }
    public String addScopeSupplyAndAdd(){
        registerScopeSupply();
        cleanScopeSupply();
        return "";
    }
    private void registerScopeSupply(){
        log.log(Level.INFO, "SCOPE CODE    " + newScopeSupply.getCode());
        ScopeSupplyEntity scopeSupplyEntity=new ScopeSupplyEntity();
        try{
            scopeSupplyEntity=(ScopeSupplyEntity)org.apache.commons.beanutils.BeanUtils.cloneBean(newScopeSupply);
        }catch (Exception ex){
        }
        scopeSupplies.add(scopeSupplyEntity);
    }
    public void deleteComment(final int index){
        if(index>=0 && index < commentEntities.size()){
            commentEntities.remove(index);
        }
    }
    public void editComment(final int index){
        indexCommentEditing=-1;
        if(index>=0 && index < commentEntities.size()){
            editComment=commentService.clone(commentEntities.get(index));
            indexCommentEditing=index;
        }
    }
    public void updateComment(){
        if(indexCommentEditing>=0&&indexCommentEditing<commentEntities.size()){
            commentEntities.set(indexCommentEditing,editComment);
        }
    }
    public void deleteAttachment(final int index){
        if(index>=0 && index < atachmentEntities.size()){
            atachmentEntities.remove(index);
        }
    }

    public void deleteScopeSupply(final int index){
        if(index>=0 && index < scopeSupplies.size()){
            scopeSupplies.remove(index);
        }
    }

    @Produces
    @Named
    @ViewScoped
    public PurchaseOrderEntity getNewPurchaseOrder(){
        return newPurchaseOrder;
    }

    public void handleCommentUpload(FileUploadEvent event){
        UploadedFile uf=event.getFile();
        log.info(uf.getFileName());
        if(indexCommentEditing>=0){
            try{
                editComment.setFile(IOUtils.toByteArray(uf.getInputstream()));
            }catch (IOException ioe){
            }
            editComment.setFileName(uf.getFileName());
            editComment.setMimeType(uf.getContentType());
        }else{
            try{
                newComment.setFile(IOUtils.toByteArray(uf.getInputstream()));
            }catch (IOException ioe){
            }
            newComment.setFileName(uf.getFileName());
            newComment.setMimeType(uf.getContentType());
        }

    }

    public void handleAttachmentUpload(FileUploadEvent event){
        UploadedFile uf=event.getFile();
        log.info(uf.getFileName());
        AttachmentEntity attachment=new AttachmentEntity();
        try{
            attachment.setFile(IOUtils.toByteArray(uf.getInputstream()));
        }catch (IOException ioe){
        }
        attachment.setFileName(uf.getFileName());
        attachment.setName(attachment.getFileName());
        attachment.setMimeType(uf.getContentType());
        getAtachmentEntities().add(attachment);
    }

    @PreDestroy
    public void destroy(){
        log.log(Level.INFO, String.format("bean destroyed [%s]", this.getClass().toString()));
    }

    @Produces
    @Named
    @ViewScoped
    public SupplierEntity getNewSupplier() {
        return newSupplier;
    }

    @Produces
    @Named
    @RequestScoped
    public CommentEntity getNewComment() {
        return newComment;
    }

    public List<CommentEntity> getCommentEntities() {
        return commentEntities;
    }

    public Date calculateDate() {
        Date date=null;
        if(indexScopeSupplyEditing!=null){
            if(indexScopeSupplyEditing>=0){
                date=scopeSupplyService.calculateForecastSiteDate(editScopeSupply,newPurchaseOrder);
            }else{
                date=scopeSupplyService.calculateForecastSiteDate(newScopeSupply,newPurchaseOrder);
            }
        }else{
            log.info("probably first time");
        }
        log.info("date calculated "+date);
       return date;
    }

    public void cleanScopeSupply(){
        log.info("cleaning scope supply");
        indexScopeSupplyEditing=-1;
        newScopeSupply=new ScopeSupplyEntity();
    }

    public void selectForEditingScopeSuppply(Integer index){
        indexScopeSupplyEditing=-1;
            if(index>=0&&index <=scopeSupplies.size()){
                indexScopeSupplyEditing=index;
                editScopeSupply= scopeSupplyService.clone(scopeSupplies.get(index));
            }
    }


    public CommentEntity getEditComment() {
        return editComment;
    }

    @Produces
    @Named
    @RequestScoped
    public ScopeSupplyEntity getNewScopeSupply() {
        return newScopeSupply;
    }

    public ScopeSupplyEntity getSelectedScopeSupply() {
        return selectedScopeSupply;
    }

    public void setSelectedScopeSupply(ScopeSupplyEntity selectedScopeSupply) {
        this.selectedScopeSupply = selectedScopeSupply;
    }

    public List<ScopeSupplyEntity> getScopeSupplies() {
        return scopeSupplies;
    }

    public void setScopeSupplies(List<ScopeSupplyEntity> scopeSupplies) {
        this.scopeSupplies = scopeSupplies;
    }

    public List<AttachmentEntity> getAtachmentEntities() {
        return atachmentEntities;
    }

    public ScopeSupplyEntity getEditScopeSupply() {
        return editScopeSupply;
    }
}
