package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderDao;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqmes.boundary.purchase.PurchaseOrderTbl;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.procurement.report.ReportProcBean;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
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
        purchaseOrderToVariation.getPoEntity().setId(null);
        purchaseOrderToVariation.getPoEntity().setPoProcStatus(POStatusEnum.READY);
        purchaseOrderToVariation.setVariation(newVariationNumber);
        //purchaseOrderToVariation.getPoEntity().getTextEntity().setId(null);
        if (purchaseOrderToVariation.getPoEntity().getCashflow() != null) {
            purchaseOrderToVariation.getPoEntity().getCashflow().setId(null);
            for (CashflowDetailEntity entity : purchaseOrderToVariation.getPoEntity().getCashflow().getCashflowDetailList()) {
                entity.setId(null);
            }
        }

        for (DeliverableEntity entity : purchaseOrderToVariation.getPoEntity().getDeliverables()) {
            entity.setId(null);
        }
        for (RequisitionEntity entity : purchaseOrderToVariation.getPoEntity().getRequisitions()) {
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
        log.info("do commit purchase order");
        currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.COMMITED);
        currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);
        //list = service.purchaseListByProject(Long.parseLong(projectId));
        maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
    }
    public void doFinalise(){
        if(currentPurchaseOrder!=null) {
            currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.FINAL);
            currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);

        }
    }

    public void doReleasePo() {
        log.info("do release purchase order");
        currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
        currentPurchaseOrder = service.updateOnlyPOOnProcurement((currentPurchaseOrder));
    }

    public boolean actionViewPOO(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus() != null) {
            if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal())
                    || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean actionEditPOO(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus() != null) {
            if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                    || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.ON_HOLD.ordinal())) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean isPossibleCreateVariation(PurchaseOrderEntity entity) {
        return canCreateVariation(entity);
    }

    private boolean canCreateVariation(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus() != null && entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal()) {
            for (Object po : maxVariationsList) {
                Object[] values = (Object[]) po;
                PurchaseOrderEntity poe = new PurchaseOrderEntity();
                poe.setPo((String) values[0]);
                poe.setOrderedVariation((Integer) values[1]);
                if (entity.getOrderedVariation()!=null&&entity.getPo().equals(poe.getPo()) &&
                        entity.getOrderedVariation().intValue() == poe.getOrderedVariation().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean actionCommitPOO(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus() != null) {
            if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public boolean actionForReady(PurchaseOrderEntity entity){
        return entity.getPoEntity().getPoProcStatus()!=null&&entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal();
    }

    public boolean actionReleasePOO(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus() != null) {
            if (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal()) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public boolean isPossiblePrintPO(PurchaseOrderEntity entity) {
        if (entity.getPoEntity().getPoProcStatus() != null) {
            if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                    || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())
                    || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal())) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public void loadCurrentPo(final PurchaseOrderEntity po) {
        log.info("loading current po");
        currentPurchaseOrder = po;
    }

    public boolean hasCurrentPOStatusFinal() {
        if (currentPurchaseOrder != null && currentPurchaseOrder.getPoEntity() != null) {
            return currentPurchaseOrder.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal();
        }
        return false;
    }

    public void printPOFinal() {
        log.info("printing po final");
        if(currentPurchaseOrder!=null&&currentPurchaseOrder.getPoEntity().getPoProcStatus().ordinal()!=POStatusEnum.COMMITED.ordinal()){
           /* currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.FINAL);
            currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);*/
            doFinalise();
        }
        //RequestContext.getCurrentInstance().update("projectListId");
        printPo(currentPurchaseOrder.getPoEntity().getPoProcStatus());
    }


    public void printPODraft() {
        log.info("printing po draft");
       /* if(currentPurchaseOrder!=null&&currentPurchaseOrder.getPoEntity().getPoProcStatus().ordinal()!=POStatusEnum.COMMITED.ordinal()&&
                currentPurchaseOrder.getPoEntity().getPoProcStatus().ordinal()!=POStatusEnum.FINAL.ordinal()) {*/
            printPo(null);
        //}

    }

    private void printPo(POStatusEnum poStatusEnum) {
        List<ScopeSupplyEntity> scopeSupplyEntities = scopeSupplyService.scopeSupplyListByPOOId(currentPurchaseOrder.getId());
        TextEntity textEntity = textService.findByPoId(currentPurchaseOrder.getPoEntity().getId());
        String preamble = textEntity != null ? textEntity.getPreamble() : "";
        List<ClausesEntity> clausesEntityList = getClausesEntitiesByPOid(textEntity);
        reportProcBean.printPurchaseOrder(currentPurchaseOrder, scopeSupplyEntities, preamble, clausesEntityList);
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
}
