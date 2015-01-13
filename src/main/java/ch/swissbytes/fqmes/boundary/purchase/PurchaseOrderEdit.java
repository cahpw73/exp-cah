package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.fqmes.control.attachment.AttachmentService;
import ch.swissbytes.fqmes.control.comment.CommentService;
import ch.swissbytes.fqmes.control.enumService.EnumService;
import ch.swissbytes.fqmes.control.purchase.PurchaseOrderService;
import ch.swissbytes.fqmes.control.scopesupply.ScopeSupplyService;
import ch.swissbytes.fqmes.control.tdp.TransitDeliveryPointService;
import ch.swissbytes.fqmes.model.dao.SupplierDao;
import ch.swissbytes.fqmes.model.entities.*;
import ch.swissbytes.fqmes.types.StatusEnum;
import ch.swissbytes.fqmes.types.TimeMeasurementEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.SortBean;
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
public class PurchaseOrderEdit implements Serializable {

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

    @Inject
    private TransitDeliveryPointService tdpService;

    @Inject
    private Configuration configuration;

    @Inject
    private SortBean sortScopeSupply;

    private String purchaseOrderId;

    private PurchaseOrderEntity poEdit;

    private List<CommentEntity> comments;

    private List<ScopeSupplyEntity> scopeSupplies;

    private ScopeSupplyEntity selectedScopeSupply;

    private ScopeSupplyEntity scopeSupplySplit;

    private SupplierEntity supplierEdit;

    private CommentEntity commentEdit;

    private CommentEntity editingComment;

    private Integer commentIndexSelected;

    private ScopeSupplyEntity scopeSupplyEdit;

    private Integer currentOperation = -1;


    private Long relativeCurrentCommentId = 0L;
    private Long relativeCurrentAttachmentId = 0L;
    private Long relativeCurrentScopeSupplyId = 0L;

    private ScopeSupplyEntity scopeSupplyEditing;

    private Integer indexForScopeSupplyEditing = -1;

    private final static Integer SPLIT = 2;
    private final static Integer CREATE = 0;
    private final static Integer EDIT = 1;


    private Integer originalQuantity;

    private static final Logger log = Logger.getLogger(PurchaseOrderEdit.class.getName());

    private TransitDeliveryPointEntity newTdp;

    private TransitDeliveryPointEntity tdpEdit;

    private Long temporaryIdForTdp=0L;

    private Integer currentIndexForTdp=-1;
    private String fase="0";

    private Integer currentHashCode;

    private List<ScopeSupplyEntity>scopeActives=new ArrayList<>();

    private List<CommentEntity>commentActives=new ArrayList<>();

    private List<TransitDeliveryPointEntity>tdpActives=new ArrayList<>();


    public void load() {
        log.info("loading...");
        if(!fase.equals("1")){
            originalQuantity = 0;
            poEdit = service.load(Long.parseLong(purchaseOrderId));
            supplierEdit = supplierDao.findByPurchaseOrder(poEdit.getId());

            supplierEdit = supplierDao.findByPurchaseOrder(poEdit.getId());
            currentHashCode=service.getAbsoluteHashcode(poEdit.getId());
            log.info(String.format("hashcode starting [%s]",currentHashCode));
            if (comments == null || comments.size() == 0) {
                comments = commentService.findByPurchaseOrder(poEdit.getId());
            } else {

            }
            if (scopeSupplies == null || scopeSupplies.size() == 0) {
                scopeSupplies = scopeSupplyService.findByPurchaseOrder(poEdit.getId());
                scopeActives.addAll(scopeSupplies);
            }

            commentIndexSelected = -1;
        }

        if(scopeSupplies != null && !scopeSupplies.isEmpty()){
            scopeActives.clear();
            scopeActives.addAll(scopeSupplies);
            sortScopeSupply.sortScopeSupplyEntity(scopeActives);
        }
        commentActives=commentService.getActives(comments);
    }

    @PostConstruct
    public void create() {
        log.info("creating Purchase Order Edit...");
        // poEdit=new PurchaseOrderEntity();
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
        Integer hashCode=service.getAbsoluteHashcode(poEdit.getId());
        log.info(String.format("hashCode [%s]",hashCode));
        String url = "";
        if(hashCode.intValue()==currentHashCode.intValue()){
            updateStatusesAndLastUpdate();
            service.doUpdate(poEdit, supplierEdit, comments, scopeSupplies);
            if (!conversation.isTransient()) {
                conversation.end();
            }
            url="view?faces-redirect=true&poId=" + poEdit.getId();

        }else{
            if (!conversation.isTransient()) {
                log.info("Deleting the conversation...");
                conversation.end();
                url="edit?faces-redirect=true&poId=" + poEdit.getId();
            }
            Messages.addFlashGlobalError("Somebody has already updated this purchase order! Please enter your data one more time");
        }
        return url;
    }

    private void updateStatusesAndLastUpdate() {
        PurchaseOrderEntity oldPurchaseOrder = service.load(poEdit.getId());
        Date now = new Date();
        if (!oldPurchaseOrder.equals(poEdit)) {
            poEdit.setLastUpdate(now);
        }
        SupplierEntity oldSupplier = supplierDao.load(supplierEdit.getId());
        if (!oldSupplier.equals(supplierEdit)) {
            supplierEdit.setLastUpdate(now);
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
        int order =1;
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
            if(scopeSupply.getStatus().getId().intValue()!= StatusEnum.DELETED.ordinal()){
                scopeSupply.setOrdered(order);
                order++;
            }else{
                scopeSupply.setOrdered(null);
            }
        }
    }

    public void addComment() {
        CommentEntity commentEntity = new CommentEntity();
        try {
            commentEntity = (CommentEntity) org.apache.commons.beanutils.BeanUtils.cloneBean(commentEdit);
            relativeCurrentCommentId--;
            commentEntity.setId(relativeCurrentCommentId);
        } catch (Exception ex) {

        }
        commentEntity.setLastUpdate(new Date());
        commentEntity.setStatus(enumService.getStatusEnumEnable());
        comments.add(commentEntity);
        commentActives=commentService.getActives(comments);
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
        relativeCurrentAttachmentId--;
        scopeSupplyEntity.setId(relativeCurrentScopeSupplyId);
        scopeSupplies.add(scopeSupplyEntity);

    }

    public void handleCommentUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        log.info(uf.getFileName());
        log.info("size ["+uf.getSize()+"]");
        if (commentIndexSelected != null && commentIndexSelected >= 0) {
            try {
                editingComment.setJustLoaded(true);
                editingComment.setFile(IOUtils.toByteArray(uf.getInputstream()));
                editingComment.setFileName(uf.getFileName());
                editingComment.setMimeType(uf.getContentType());
            } catch (IOException ioe) {
                log.log(Level.SEVERE,"Error uploading file :"+uf.getFileName() +" of "+ uf.getSize()+" byte(s)");
                ioe.printStackTrace();
                Messages.addGlobalError("Error uploading file :"+uf.getFileName() +" of "+ uf.getSize()+" byte(s)");
            }

        } else {
            try {
                commentEdit.setJustLoaded(true);
                commentEdit.setFile(IOUtils.toByteArray(uf.getInputstream()));
                commentEdit.setFileName(uf.getFileName());
                commentEdit.setMimeType(uf.getContentType());
            } catch (IOException ioe) {
                log.log(Level.SEVERE,"Error uploading file :"+uf.getFileName() +" of "+ uf.getSize()+" byte(s)");
                ioe.printStackTrace();
                Messages.addGlobalError("Error uploading file :"+uf.getFileName() +" of "+ uf.getSize()+" byte(s)");
            }

        }


    }

    public void cleanComment() {
        commentEdit = new CommentEntity();
        commentIndexSelected = -1;
    }

    public String cleanScopeSupply() {
        currentOperation = CREATE;//CREATING...
        scopeSupplyEdit = new ScopeSupplyEntity();
        scopeSupplyEdit.setDeliveryDate(poEdit.getPoDeliveryDate());
        scopeSupplyEdit.setResponsibleExpediting(poEdit.getResponsibleExpediting());
        scopeSupplyEdit.setRequiredSiteDate(poEdit.getRequiredDate());
        scopeSupplyEdit.setIsForecastSiteDateManual(false);
        scopeSupplyEdit.setDeliveryLeadTimeMs(TimeMeasurementEnum.DAY);
        return "/purchase/modal/EditModalScopeSupply?faces-redirect=true";
    }

    private boolean isValidDataUpdate() {
        log.info("boolean isValidDataUpdate()");
        boolean isValid = false;
        int inc = 0;
        Integer quantity=scopeSupplyEditing.getQuantity()!=null?scopeSupplyEditing.getQuantity():0;
        Double cost=scopeSupplyEditing.getCost()!=null?scopeSupplyEditing.getCost().doubleValue():0D;
        if(quantity >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Quantity has a invalid data");
        }
        if(cost >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Unit Price has a invalid data");
        }
        if(inc == 2){
            isValid = true;
        }
        return isValid;
    }

    private boolean isValidData() {
        log.info("boolean isValidData()");
        boolean isValid = false;
        int inc = 0;
        Integer quantity=scopeSupplyEdit.getQuantity()!=null?scopeSupplyEdit.getQuantity():0;
        Double cost=scopeSupplyEdit.getCost()!=null?scopeSupplyEdit.getCost().doubleValue():0D;
        if(quantity >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Quantity has a invalid data");
        }
        if(cost >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Unit Price has a invalid data");
        }
        if(inc == 2){
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


    public SupplierEntity getSupplierEdit() {
        return supplierEdit;
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
      //  return commentService.getActives(comments);
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
            commentActives=commentService.getActives(comments);
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
        }
    }

    public void doUpdateComment() {
        Integer index = commentService.getIndexById(editingComment.getId(), comments);
        if (index >= 0 && index < comments.size()) {

            int originalHascode=comments.get(index).hashCode();
            int currentHascode=editingComment.hashCode();
            if(currentHascode!=originalHascode){
                editingComment.setLastUpdate(new Date());
            }
            comments.set(index, commentService.clone(editingComment));
        }


        commentActives=commentService.getActives(comments);
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

    public List<ScopeSupplyEntity> getActiveScopeSupplies() {
        return scopeActives;
    }

    public String addScopeSupply() {
        if(isValidData()){
        registerScopeSupply();
        scopeActives.clear();
        scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
        return "/purchase/edit?faces-redirect=true&fase=1";
        }
        return "";
    }

    public void addScopeSupplyAndAdd() {
        if(isValidData()){
            registerScopeSupply();
            cleanScopeSupply();
        }
        scopeActives.clear();
        scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
    }

    public String cancel() {
        log.info("cancel to edit purchase order...........");
        return "/purchase/edit?faces-redirect=true&cid="+conversation.getId()+"&fase=1";
    }

    public String split() {
        String url="/purchase/edit?faces-redirect=true&fase=1";
        if(validateValuesAboutSplit()){
            relativeCurrentScopeSupplyId--;
            scopeSupplySplit.setId(relativeCurrentScopeSupplyId);
            scopeSupplySplit.setStatus(enumService.getStatusEnumEnable());
            ScopeSupplyEntity ss=scopeSupplyService.clone(scopeSupplySplit);
            ss.getTdpList().clear();

            for(TransitDeliveryPointEntity tdpe:selectedScopeSupply.getTdpList()){
                currentIndexForTdp--;
                TransitDeliveryPointEntity tdp=tdpService.clone(tdpe);
                tdp.setId(Long.parseLong(Integer.toString(currentIndexForTdp)));
                ss.getTdpList().add(tdp);
            }
            scopeSupplies.add(ss);
            final int index = scopeSupplyService.getIndexById(selectedScopeSupply.getId(), scopeSupplies);
            if (index >= 0 && index < scopeSupplies.size()) {
                ScopeSupplyEntity sse=scopeSupplyService.clone(selectedScopeSupply);
                sse.getTdpList().addAll(selectedScopeSupply.getTdpList());
                scopeSupplies.set(index, sse);
            }
            scopeActives.clear();
            scopeActives.addAll(scopeSupplyService.getActives(scopeSupplies));
        } else {
            Messages.addGlobalError("You are not able to make a split with these values");
            url= "";
        }
        return url;
    }
    private boolean validateValuesAboutSplit(){
        int arrivedQuantity=(scopeSupplySplit!=null&&scopeSupplySplit.getQuantity()!=null)?scopeSupplySplit.getQuantity().intValue():0;
        int newQuantity=(selectedScopeSupply!=null&&selectedScopeSupply.getQuantity()!=null)?selectedScopeSupply.getQuantity().intValue():0;
        return originalQuantity!=null&& originalQuantity.intValue()>1&& originalQuantity.intValue()==arrivedQuantity+newQuantity;
    }

    public String doUpdateScopeSupply() {
        if(isValidDataUpdate()){
            if (indexForScopeSupplyEditing >= 0) {
                ScopeSupplyEntity ss=scopeSupplyService.clone(scopeSupplyEditing);
                ss.getTdpList().clear();
                ss.getTdpList().addAll(scopeSupplyEditing.getTdpList());
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
            if( selectedScopeSupply.getActualSiteDate()==null){
                selectedScopeSupply.setActualSiteDate(new Date());
            }
            scopeSupplySplit.setQuantity(0);

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
        updateTdpActives();
        return  "/purchase/modal/EditModalScopeSupplyEditing?faces-redirect=true&poId=" + id;
    }

    public ScopeSupplyEntity getScopeSupplySplit() {
        return scopeSupplySplit;
    }

    public void downloadAttachedFileOnComment(final long id) {
        log.info("public void downloadAttachedFileOnComment(final long id)");
        commentService.download(id);
    }

    public ScopeSupplyEntity getScopeSupplyEditing() {
        return scopeSupplyEditing;
    }

    public List<TransitDeliveryPointEntity> activesTdp(){
        //return tdpService.getActives(scopeSupplyEditing.getTdpList());
        return tdpActives;
    }

    private void updateTdpActives(){
        if(scopeSupplyEditing!=null){
        tdpActives=tdpService.getActives(scopeSupplyEditing.getTdpList());
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
                    scopeSupplyEdit.setSiteDate(date);
                }
                break;
            case 1://EDITING
                if (!scopeSupplyEditing.getIsForecastSiteDateManual()) {
                    date = scopeSupplyService.calculateForecastSiteDate(scopeSupplyEditing);
                    scopeSupplyEditing.setSiteDate(date);
                }
                break;
            case 2://SPLITING
                if (!getScopeSupplySplit().getIsForecastSiteDateManual()) {
                    date = scopeSupplyService.calculateForecastSiteDate(getScopeSupplySplit());
                    getScopeSupplySplit().setSiteDate(date);
                }
                break;
            default:
                log.info("no possible computation");
                break;
        }

        log.info("date calculated " + date);
        return date;
    }

    public void switchModeForecastSiteDate() {
        log.info("public void switchModeForecastSiteDate()");
        switch (currentOperation) {
            case 0://CREATING
                scopeSupplyEdit.setSiteDate(null);
                if (!scopeSupplyEdit.getIsForecastSiteDateManual()) {
                    calculateDate();
                }
                break;
            case 1://EDITING
                scopeSupplyEditing.setSiteDate(null);
                if (!scopeSupplyEditing.getIsForecastSiteDateManual()) {
                    calculateDate();
                }
                break;
            case 2://SPLITING
                getScopeSupplySplit().setSiteDate(null);
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

    public void cleanTransitDeliveryPoint(){
        newTdp=new TransitDeliveryPointEntity();
        newTdp.setIsForecastSiteDateManual(false);
        newTdp.setMeasurementTime(TimeMeasurementEnum.DAY);
        currentIndexForTdp=-1;
    }
    public void selectTdp(Long id){
        int index=tdpService.getIndexById(id,scopeSupplyEdit.getTdpList());
        if(index>=0&&index<scopeSupplyEdit.getTdpList().size()){
            tdpEdit=tdpService.clone(scopeSupplyEdit.getTdpList().get(index));
            currentIndexForTdp=index;
        }
    }
    public void selectTdpEditing(Long id){
        int index=tdpService.getIndexById(id,scopeSupplyEditing.getTdpList());
        if(index>=0&&index<scopeSupplyEditing.getTdpList().size()){
            tdpEdit=tdpService.clone(scopeSupplyEditing.getTdpList().get(index));
            currentIndexForTdp=index;
        }
    }
    public void deleteTdp(Long id){
        Integer index=tdpService.getIndexById(id,scopeSupplyEdit.getTdpList());
        if(index>=0){
            TransitDeliveryPointEntity tde=scopeSupplyEdit.getTdpList().get(index);
            if(tde.getId()!=null&&tde.getId()>0){
                tde.setStatus(enumService.getStatusEnumDeleted());
                tde.setLastUpdate(new Date());
            }else{
                scopeSupplyEdit.getTdpList().remove(index.intValue());
            }
            calculateDate();
        }
    }
    public void deleteTdpOnEdition(Long id){
        Integer index=tdpService.getIndexById(id,scopeSupplyEditing.getTdpList());
        if(index>=0){
            TransitDeliveryPointEntity tde=scopeSupplyEditing.getTdpList().get(index);
            if(tde.getId()!=null&&tde.getId()>0){
                tde.setStatus(enumService.getStatusEnumDeleted());
                tde.setLastUpdate(new Date());
            }else{
                scopeSupplyEditing.getTdpList().remove(index.intValue());
            }
            calculateDate();
        }
    }
    public void doSaveTdp(){
        newTdp.setStatus(enumService.getStatusEnumEnable());
        temporaryIdForTdp--;
        newTdp.setId(temporaryIdForTdp);
        newTdp.setLastUpdate(new Date());
        scopeSupplyEdit.getTdpList().add(tdpService.clone(newTdp));
        calculateDate();
    }
    public void doSaveOnEdition(){
        newTdp.setStatus(enumService.getStatusEnumEnable());
        temporaryIdForTdp--;
        newTdp.setId(temporaryIdForTdp);
        newTdp.setLastUpdate(new Date());
        scopeSupplyEditing.getTdpList().add(tdpService.clone(newTdp));
        calculateDate();
        updateTdpActives();
    }
    public void doUpdateTdp(){
        if(currentIndexForTdp>=0&&currentIndexForTdp<scopeSupplyEdit.getTdpList().size()){
            tdpEdit.setLastUpdate(new Date());
            scopeSupplyEdit.getTdpList().set(currentIndexForTdp,tdpService.clone(tdpEdit));
            calculateDate();
        }
    }
    public void doUpdateTdpOnEdition(){
        if(currentIndexForTdp>=0&&currentIndexForTdp<scopeSupplyEditing.getTdpList().size()){
            tdpEdit.setLastUpdate(new Date());
            scopeSupplyEditing.getTdpList().set(currentIndexForTdp,tdpService.clone(tdpEdit));
            calculateDate();
            updateTdpActives();
        }
    } 
    public String cancelEditPurchaseOrder(){
        if(!conversation.isTransient()){
            log.info("Finish conversation...");
            conversation.end();
        }
        return"/purchase/list?faces-redirect=true";
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public void calulateForecasteDateForTdpCreation(){
        log.info("calulateForecasteDateForTdpCreation....");
      //  newTdp.setForecastDeliveryDate(null);
        if(!newTdp.getIsForecastSiteDateManual()){
            List<TransitDeliveryPointEntity>list=scopeSupplyEdit.getTdpList();
            TransitDeliveryPointEntity tdpPrevious=list.size()>0?list.get(list.size()-1):null;
            newTdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEdit, scopeSupplyEdit.getTdpList().size()==0,tdpPrevious, newTdp));
        }
    }
    public void calulateForecasteDateForTdpEdition(){
        log.info("calulateForecasteDateForTdpEdition....");
      //  tdpEdit.setForecastDeliveryDate(null);
        if(!tdpEdit.getIsForecastSiteDateManual()){
            List<TransitDeliveryPointEntity>list=scopeSupplyEdit.getTdpList();
            TransitDeliveryPointEntity tdpPrevious=list.size()>1&&currentIndexForTdp>0?list.get(currentIndexForTdp-1):null;
            tdpEdit.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEdit, currentIndexForTdp.intValue() == 0, tdpPrevious, tdpEdit));
        }
    }

    public void calulateForecasteDateForTdpCreation1(){
        log.info("calulateForecasteDateForTdpCreation1....");
      //  newTdp.setForecastDeliveryDate(null);
        if(!newTdp.getIsForecastSiteDateManual()){
            List<TransitDeliveryPointEntity>list=scopeSupplyEditing.getTdpList();
            TransitDeliveryPointEntity tdpPrevious=list.size()>0?list.get(list.size()-1):null;
            newTdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEditing, scopeSupplyEditing.getTdpList().size()==0,tdpPrevious, newTdp));
        }
    }
    public void calulateForecasteDateForTdpEdition1(){
        log.info("calulateForecasteDateForTdpEdition1....");
       // tdpEdit.setForecastDeliveryDate(null);
        if(!tdpEdit.getIsForecastSiteDateManual()){
            List<TransitDeliveryPointEntity>list=scopeSupplyEditing.getTdpList();
            TransitDeliveryPointEntity tdpPrevious=list.size()>1&&currentIndexForTdp>0?list.get(currentIndexForTdp-1):null;
            tdpEdit.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(scopeSupplyEditing, currentIndexForTdp.intValue() == 0, tdpPrevious, tdpEdit));
        }
    }

}
