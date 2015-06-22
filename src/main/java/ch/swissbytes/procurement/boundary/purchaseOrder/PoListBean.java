package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.procurement.report.ReportProcBean;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 6/1/2015.
 */

@Named
@ViewScoped
public class PoListBean implements Serializable {

    private static final Logger log = Logger.getLogger(PoListBean.class.getName());

    @Inject
    private PurchaseOrderService service;

    @Inject
    private ProjectService projectService;

    @Inject
    private SortBean sortBean;

    @Inject
    private ReportProcBean reportProcBean;

    private String projectId;

    private ProjectEntity project;

    private PurchaseOrderEntity currentPurchaseOrder;

    private List<PurchaseOrderEntity> list;

    private List<PurchaseOrderEntity> pOrderList;

    private String newVariationNumber;

    private PurchaseOrderEntity purchaseOrderToVariation;

    @PostConstruct
    public void create() {
        log.info("Created POListBean");
        list = new ArrayList<>();
        currentPurchaseOrder = new PurchaseOrderEntity();
        purchaseOrderToVariation = new PurchaseOrderEntity();
        pOrderList = new ArrayList<>();
    }

    public void load() {
        if (StringUtils.isNotEmpty(projectId) && StringUtils.isNotBlank(projectId)) {
            try {
                Long.parseLong(projectId);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("project Id invalid");
            }
            project = projectService.findProjectById(Long.parseLong(projectId));
            if (project == null) {
                throw new IllegalArgumentException("project Id invalid");
            }
            list = service.purchaseListByProject(Long.parseLong(projectId));
        } else {
            throw new IllegalArgumentException("project Id invalid");
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroyed POListBean");
    }

    public void doSavePOO(){
        log.info("do save POO with variation");
        service.savePOOnProcurement(purchaseOrderToVariation);
        list = service.purchaseListByProject(Long.parseLong(projectId));
    }

    public void createVarNumberToPO(PurchaseOrderEntity entity) {
        log.info("loading current purchase order");
        currentPurchaseOrder = entity;
        pOrderList = service.findByProjectIdAndPo(project.getId(),entity.getPo());
        sortBean.sortPurchaseOrderEntity(pOrderList);
        String lastVarNumber = pOrderList.get(pOrderList.size()-1).getVariation();
        generateVariationNumber(lastVarNumber);
        purchaseOrderToVariation = service.findById(entity.getId());
        prepareToSaveWithNewVariation(purchaseOrderToVariation);

    }

    private void prepareToSaveWithNewVariation(PurchaseOrderEntity purchaseOrderToVariation) {
        purchaseOrderToVariation.setId(null);
        purchaseOrderToVariation.getPoEntity().setId(null);
        purchaseOrderToVariation.getPoEntity().setPoProcStatus(POStatusEnum.READY);
        purchaseOrderToVariation.setVariation(newVariationNumber);
        purchaseOrderToVariation.getPoEntity().getTextEntity().setId(null);
        purchaseOrderToVariation.getPoEntity().getCashflow().setId(null);
        for(CashflowDetailEntity entity : purchaseOrderToVariation.getPoEntity().getCashflow().getCashflowDetailList()){
            entity.setId(null);
        }
        for(ItemEntity entity : purchaseOrderToVariation.getPoEntity().getItemList()){
            entity.setId(null);
        }
        for(DeliverableEntity entity : purchaseOrderToVariation.getPoEntity().getDeliverables()){
            entity.setId(null);
        }
        for(RequisitionEntity entity : purchaseOrderToVariation.getPoEntity().getRequisitions()){
            entity.setId(null);
        }
        log.info("");
    }

    private void generateVariationNumber(String lastVarNumber) {
        String[] number = lastVarNumber.split("\\.");
        Integer lastNumber;
        if(number.length == 0){
            lastNumber = Integer.valueOf(lastVarNumber);
            lastNumber++;
            newVariationNumber = String.valueOf(lastNumber);
        }else if(number.length > 1){
            lastNumber = Integer.valueOf(number[number.length-1]);
            lastNumber++;
            String varNo = "";
            for(int i = 0; i < number.length-1; i++){
                varNo = varNo + number[i] + ".";
            }
            newVariationNumber = varNo + String.valueOf(lastNumber);
        }else if(number.length == 1){
            lastNumber = Integer.valueOf(lastVarNumber);
            lastNumber++;
            newVariationNumber = String.valueOf(lastNumber);
        }
    }

    public void doCommitPo() {
        log.info("do commit purchase order");
        currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.COMMITED);
        currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);
    }

    public void doReleasePo() {
        log.info("do release purchase order");
        currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
        currentPurchaseOrder = service.updateOnlyPOOnProcurement((currentPurchaseOrder));
    }

    public boolean actionViewPOO(PurchaseOrderEntity entity) {
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())) {
            return true;
        }
        return false;
    }

    public boolean actionEditPOO(PurchaseOrderEntity entity) {
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.ON_HOLD.ordinal())) {
            return true;
        }
        return false;
    }

    public boolean actionVarationPOO(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal()) {
            return true;
        }
        return false;
    }

    public boolean actionCommitPOO(PurchaseOrderEntity entity) {
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())) {
            return true;
        }
        return false;
    }

    public boolean actionReleasePOO(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal()) {
            return true;
        }
        return false;
    }

    public boolean actionPrintPOO(PurchaseOrderEntity entity) {
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal())) {
            return true;
        }
        return false;
    }

    public void loadCurrentPo(final PurchaseOrderEntity po){
        log.info("loading current po");
        currentPurchaseOrder = po;
    }

    public boolean hasCurrentPOStatusFinal(){
        if(currentPurchaseOrder != null && currentPurchaseOrder.getPoEntity()!=null){
            return currentPurchaseOrder.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal();
        }
        return false;
    }

    public void printPOFinal(){
        log.info("printing po final");
        currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.FINAL);
        currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);
        printPo(currentPurchaseOrder.getPoEntity().getPoProcStatus());
    }

    public void printPODraft(){
        log.info("printing po draft");
        printPo(null);
    }

    private void printPo(POStatusEnum poStatusEnum){
        reportProcBean.printPurchaseOrder(currentPurchaseOrder);
    }

    public List<PurchaseOrderEntity> getList() {
        return list;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public PurchaseOrderEntity getCurrentPurchaseOrder() {
        log.info("set value on current purchase order");
        return currentPurchaseOrder;
    }

    public void setCurrentPurchaseOrder(PurchaseOrderEntity currentPurchaseOrder) {
        this.currentPurchaseOrder = currentPurchaseOrder;
    }

    public String getNewVariationNumber() {
        return newVariationNumber;
    }

    public void setNewVariationNumber(String newVariationNumber) {
        this.newVariationNumber = newVariationNumber;
    }
}
