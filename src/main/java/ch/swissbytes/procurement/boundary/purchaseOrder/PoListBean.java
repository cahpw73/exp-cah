package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderDao;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ProcurementStatus;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.boundary.purchase.PurchaseOrderTbl;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.procurement.report.ReportProcBean;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
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

    @Inject
    private ScopeSupplyService scopeSupplyService;

    @Inject
    private TextService textService;
    @Inject
    private PurchaseOrderDao dao;
    @Inject
    private SpreadsheetService exporter;


    private String projectId;

    private ProjectEntity project;

    private PurchaseOrderEntity currentPurchaseOrder;

    private List<PurchaseOrderEntity> pOrderList;

    private List<PurchaseOrderEntity> maxVariationsList;

    private String newVariationNumber;

    private PurchaseOrderEntity purchaseOrderToVariation;

    private PurchaseOrderTbl poList;

    private FilterPO filter;


    @PostConstruct
    public void create() {
        log.info("Created POListBean");
        Date start = new Date();

        currentPurchaseOrder = new PurchaseOrderEntity();
        purchaseOrderToVariation = new PurchaseOrderEntity();
        pOrderList = new ArrayList<>();
        maxVariationsList = new ArrayList<>();

        Date end = new Date();
        log.info("creating list Bean takes " + (end.getTime() - start.getTime()));
    }

    public void load() {
        Date start = new Date();
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
            //list = service.purchaseListByProject(Long.parseLong(projectId));
            maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
            filter = new FilterPO();
            filter.setProjectId(project.getId());
            poList = new PurchaseOrderTbl(dao, filter);
        } else {
            throw new IllegalArgumentException("project Id invalid");
        }
        Date end = new Date();
        log.info("loading list Bean takes " + (end.getTime() - start.getTime()));
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroyed POListBean");
    }

    public void doSavePOO() {
        log.info("do save POO with variation");
        service.savePOOnProcurement(purchaseOrderToVariation);
        sortPurchaseListByVariationAndDoUpdate();
        maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
        //list = service.purchaseListByProject(Long.parseLong(projectId));
    }

    public void doSavePOONewVariation() {
        log.info("do save POO with variation");
        service.savePOOnProcurementNewVariation(purchaseOrderToVariation);
        sortPurchaseListByVariationAndDoUpdate();
        maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
        //list = service.purchaseListByProject(Long.parseLong(projectId));
    }

    private void sortPurchaseListByVariationAndDoUpdate() {
        List<PurchaseOrderEntity> poList = service.findByProjectIdAndPo(purchaseOrderToVariation.getProjectEntity().getId(), purchaseOrderToVariation.getPo());
        sortBean.sortPurchaseOrderEntity(poList);
        int index = 1;
        for (PurchaseOrderEntity po : poList) {
            po.setOrderedVariation(index);
            service.doUpdatePurchaseOrder(po);
            index++;
        }
    }

    public void createVarNumberToPO(PurchaseOrderEntity entity) {
        log.info("loading current purchase order");
        currentPurchaseOrder = entity;
        pOrderList = service.findByProjectIdAndPo(project.getId(), entity.getPo());
        sortBean.sortPurchaseOrderEntity(pOrderList);
        String lastVarNumber = pOrderList.get(pOrderList.size() - 1).getVariation();
        generateVariationNumber(lastVarNumber);
        purchaseOrderToVariation = service.findPOToCreateVariation(entity.getId());
        prepareToSaveWithNewVariation(purchaseOrderToVariation);

    }

    private void prepareToSaveWithNewVariation(PurchaseOrderEntity purchaseOrderToVariation) {
        purchaseOrderToVariation.setId(null);
        purchaseOrderToVariation.getPurchaseOrderProcurementEntity().setId(null);
        purchaseOrderToVariation.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
        purchaseOrderToVariation.setVariation(newVariationNumber);
        //purchaseOrderToVariation.getPoEntity().getTextEntity().setId(null);
        if (purchaseOrderToVariation.getPurchaseOrderProcurementEntity().getCashflow() != null) {
            purchaseOrderToVariation.getPurchaseOrderProcurementEntity().getCashflow().setId(null);
            for (CashflowDetailEntity entity : purchaseOrderToVariation.getPurchaseOrderProcurementEntity().getCashflow().getCashflowDetailList()) {
                entity.setId(null);
            }
        }

        for (DeliverableEntity entity : purchaseOrderToVariation.getPurchaseOrderProcurementEntity().getDeliverables()) {
            entity.setId(null);
        }
        for (RequisitionEntity entity : purchaseOrderToVariation.getPurchaseOrderProcurementEntity().getRequisitions()) {
            entity.setId(null);
        }
        log.info("");
    }

    private void generateVariationNumber(String lastVarNumber) {
        String[] number = lastVarNumber.split("\\.");
        Integer lastNumber;
        if (number.length == 0) {
            lastNumber = Integer.valueOf(lastVarNumber);
            lastNumber++;
            newVariationNumber = String.valueOf(lastNumber);
        } else if (number.length > 1) {
            lastNumber = Integer.valueOf(number[number.length - 1]);
            lastNumber++;
            String varNo = "";
            for (int i = 0; i < number.length - 1; i++) {
                varNo = varNo + number[i] + ".";
            }
            newVariationNumber = varNo + String.valueOf(lastNumber);
        } else if (number.length == 1) {
            lastNumber = Integer.valueOf(lastVarNumber);
            lastNumber++;
            newVariationNumber = String.valueOf(lastNumber);
        }
    }

    public void doCommitPo() {
        if (currentPurchaseOrder != null) {
            currentPurchaseOrder = service.findById(currentPurchaseOrder.getId());
            if(validate()){
                currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.COMMITTED);
                currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);
                maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
            }
        }
    }

    public void doFinalise() {
        if (currentPurchaseOrder != null) {
            currentPurchaseOrder = service.findById(currentPurchaseOrder.getId());
            if(validate()){
                currentPurchaseOrder = service.findById(currentPurchaseOrder.getId());
                currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.FINAL);
                currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);
            }
        }
    }

    private boolean validate(){
        boolean validate = true;
        if(StringUtils.isEmpty(currentPurchaseOrder.getPo())){
            validate = false;
        }
        if(StringUtils.isEmpty(currentPurchaseOrder.getPoTitle())){
            validate = false;
        }
        if(StringUtils.isEmpty(currentPurchaseOrder.getVariation())){
            validate = false;
        }
        if(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getSupplier()==null) {
            validate = false;
        }
        if(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getClazz()==null){
            validate = false;
        }
        if(currentPurchaseOrder.getPoDeliveryDate()==null){
            validate = false;
        }
        if(StringUtils.isEmpty(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoint())){
            validate = false;
        }
        if(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() != null){
            if(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity() == null){
                validate = false;
            }
        }
        if(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getScopeSupplyList().isEmpty()){
           validate = false;
        }
        if(!validateRetention(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow())){
            validate = false;
        }
        if(!validateSecurityDeposit(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow())){
            validate = false;
        }
        if(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow()!=null){
            if(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms()==null){
                validate = false;
            }
        }

        if(!validate){
            Messages.addFlashGlobalError("Not all fields required have been entered on this PO. Please make sure to review before committing");
        }
        return validate;
    }

    public boolean validateRetention(final CashflowEntity cashflow) {
        boolean validate = true;
        if(cashflow!=null){
            boolean applyRetentionSelected = cashflow.getApplyRetention() != null && cashflow.getApplyRetention().booleanValue();

            if (applyRetentionSelected) {
                if (StringUtils.isEmpty(cashflow.getForm())){
                    validate = false;
                }
                if(cashflow.getPercentage() == null){
                    validate = false;
                }
                if (cashflow.getExpDate() == null){
                    validate = false;
                }
                if(cashflow.getProjectCurrency() == null) {
                    validate = false;
                }
            }
        }

        return validate;
    }

    public boolean validateSecurityDeposit(final CashflowEntity cashflow) {
        boolean validate = true;
        if(cashflow!=null){
            boolean applySecurityDeposit = cashflow.getApplyRetentionSecurityDeposit() != null && cashflow.getApplyRetentionSecurityDeposit().booleanValue();
            if (applySecurityDeposit) {
                if (StringUtils.isEmpty(cashflow.getFormSecurityDeposit())){
                    validate = false;
                }
                if(cashflow.getPercentageSecurityDeposit() == null){
                    validate = false;
                }
                if(cashflow.getExpirationDateSecurityDeposit() == null){
                    validate = false;
                }
                if(cashflow.getCurrencySecurityDeposit() == null) {
                    validate = false;
                }
            }
        }
        return validate;
    }

    public void doReleasePo() {
        log.info("do release purchase order");
        currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
        currentPurchaseOrder = service.updateOnlyPOOnProcurement((currentPurchaseOrder));
    }

    public void doDeletePo(){
        log.info("do delete purchase order");
        service.doDelete(currentPurchaseOrder.getId());
    }

    public boolean canView(PurchaseOrderEntity entity) {
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null&&(entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.READY.ordinal())
                || (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.COMMITTED.ordinal()
                ||entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.FINAL.ordinal());
    }

    public boolean canEdit(PurchaseOrderEntity entity) {
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null&&(entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.READY.ordinal())
                || (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.ON_HOLD.ordinal()
        ||entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.INCOMPLETE.ordinal());
    }

    public boolean isPossibleCreateVariation(PurchaseOrderEntity entity) {
        return canCreateVariation(entity);
    }

    private boolean canCreateVariation(PurchaseOrderEntity entity) {
        if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null && entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.COMMITTED.ordinal()) {
            for (Object po : maxVariationsList) {
                Object[] values = (Object[]) po;
                PurchaseOrderEntity poe = new PurchaseOrderEntity();
                poe.setPo((String) values[0]);
                poe.setOrderedVariation((Integer) values[1]);
                if (entity.getOrderedVariation() != null && entity.getPo().equals(poe.getPo()) &&
                        entity.getOrderedVariation().intValue() == poe.getOrderedVariation().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canCommitt(PurchaseOrderEntity entity) {
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null &&
                (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.FINAL.ordinal()||
        entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.INCOMPLETE.ordinal());
    }

    public boolean canDelete(PurchaseOrderEntity entity){
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null &&
                entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() != ProcurementStatus.COMMITTED.ordinal();
    }

    public boolean actionForReady(PurchaseOrderEntity entity) {
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null && entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.READY.ordinal();
    }

    public boolean canRelease(PurchaseOrderEntity entity) {
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null&&entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.FINAL.ordinal();
    }

    public void loadCurrentPo(final PurchaseOrderEntity po) {
        log.info("loading current po");
        currentPurchaseOrder = po;
    }

    public boolean hasCurrentPOStatusFinal() {
        if (currentPurchaseOrder != null && currentPurchaseOrder.getPurchaseOrderProcurementEntity() != null) {
            return currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.FINAL.ordinal();
        }
        return false;
    }

    public void printPOFinal() {
        log.info("printing po final");
        if (currentPurchaseOrder != null && currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() != ProcurementStatus.COMMITTED.ordinal()
                &&currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal()!=ProcurementStatus.INCOMPLETE.ordinal()) {
            doFinalise();
        }
        printPo(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus(), false);
    }


    public void printPODraft() {
        log.info("printing po draft");
        printPo(null, true);
    }

    private void printPo(ProcurementStatus procurementStatus, boolean draft) {
        List<ScopeSupplyEntity> scopeSupplyEntities = scopeSupplyService.scopeSupplyListByPOOId(currentPurchaseOrder.getId());
        TextEntity textEntity = textService.findByPoId(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getId());
        String preamble = textEntity != null ? textEntity.getPreamble() : "";
        List<ClausesEntity> clausesEntityList = getClausesEntitiesByPOid(textEntity);
        reportProcBean.printPurchaseOrder(currentPurchaseOrder, scopeSupplyEntities, preamble, clausesEntityList, draft);
    }

    private List<ClausesEntity> getClausesEntitiesByPOid(TextEntity textEntity) {
        List<ClausesEntity> clausesEntities = new ArrayList<>();
        if (textEntity != null) {
            clausesEntities = textService.findClausesByTextId(textEntity.getId());
        }
        return clausesEntities;
    }

    public String addPrefixToVariation(PurchaseOrderEntity po) {
        service.addPrefixToVariation(po);
        return po.getVariation();
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

    public PurchaseOrderTbl getPoList() {
        return poList;
    }

    public boolean canExportCMS(PurchaseOrderEntity entity){
        boolean exported=entity.getPurchaseOrderProcurementEntity().getCmsExported()==null?false:entity.getPurchaseOrderProcurementEntity().getCmsExported();
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal()==ProcurementStatus.COMMITTED.ordinal()
                &&!exported;
    }
    public boolean canExportJDE(PurchaseOrderEntity entity){
        return entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal()==ProcurementStatus.COMMITTED.ordinal();
    }

    public StreamedContent exportCMS() throws FileNotFoundException {
        log.info("StreamedContent");
        StreamedContent content=null;
        if(currentPurchaseOrder!=null&&currentPurchaseOrder.getId()!=null) {
            List<PurchaseOrderEntity> list=new ArrayList<>();
            list.add(service.findById(currentPurchaseOrder.getId()));
            InputStream is=exporter.generateWorkbook(list);
            service.markCMSAsExported(currentPurchaseOrder);
            content=new DefaultStreamedContent(is,"application/xls",service.generateName(currentPurchaseOrder)+".xlsx");
        }
        return content;
    }
    public void exportJDE(){

    }
}
