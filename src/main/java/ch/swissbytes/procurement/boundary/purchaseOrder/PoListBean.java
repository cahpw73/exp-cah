package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJDECsvService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJDEService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetService;
import ch.swissbytes.Service.business.poDocument.PODocumentService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderDao;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.requisition.RequisitionDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ProcurementStatus;
import ch.swissbytes.fqm.boundary.UserSession;
import ch.swissbytes.fqmes.boundary.purchase.PurchaseOrderTbl;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.procurement.report.ReportProcBean;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Ajax;
import org.omnifaces.util.Messages;
import org.primefaces.component.api.UIData;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Inject
    private SpreadsheetJDEService exporterToJDE;

    @Inject
    private SpreadsheetJDECsvService jdeCsvService;

    @Inject
    private POManagerTable poManagerTable;

    @Inject
    private PoListManagerTable managerTable;

    @Inject
    private PODocumentService poDocumentService;

    @Inject
    private RequisitionDao requisitionDao;

    @Inject
    private UserSession userSession;

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");


    private String projectId;

    private ProjectEntity project;

    private PurchaseOrderEntity currentPurchaseOrder;

    private List<PurchaseOrderEntity> pOrderList;

    private List<PurchaseOrderEntity> allPurchaseOrders;

    private List<PurchaseOrderEntity> maxVariationsList;

    private List<PurchaseOrderEntity> purchaseOrders;

    private String newVariationNumber;

    private PurchaseOrderEntity purchaseOrderToVariation;

    private PurchaseOrderTbl poList;

    private FilterPO filter;
    private boolean showButtons = false;
    private Date start;
    private Date end;
    private String scrollTop = "0";

    private boolean canPrintFinalPdf;

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public void renderButtons() {
        log.info("renderButtons");
        setShowButtons(true);
        log.info("showButtons: " + isShowButtons());
    }

    @PostConstruct
    public void create() {
        log.info("Created POListBean");
        currentPurchaseOrder = new PurchaseOrderEntity();
        purchaseOrderToVariation = new PurchaseOrderEntity();
        pOrderList = new ArrayList<>();
        allPurchaseOrders = new ArrayList<>();
        maxVariationsList = new ArrayList<>();
        purchaseOrders = new ArrayList<>();
    }

    public void load() {
        log.info("loading PO List Bean");
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
            Date d1 = new Date();
            maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
            //allPurchaseOrders = service.findAllPOs(Long.parseLong(projectId));
            Date d2 = new Date();
            log.info("getting all max variations takes [" + (d2.getTime() - d1.getTime()) + "]ms");
            ((FilterPO) poManagerTable.getFilter()).setProjectId(project.getId());
            findPOs();

        } else {
            throw new IllegalArgumentException("project Id invalid");
        }
        Date end = new Date();
        log.info("loading list Bean takes [" + (end.getTime() - start.getTime()) + "]ms");

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
    }

    public void doSavePOONewVariation() {
        log.info("do save POO with variation");
        variationPOModifyValidationButtons(purchaseOrderToVariation.getPurchaseOrderProcurementEntity());
        variationCurrentPOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
        service.savePOOnProcurementNewVariation(purchaseOrderToVariation);
        currentPurchaseOrder = service.updatePOStatus(currentPurchaseOrder);
        sortPurchaseListByVariationAndDoUpdate();
        maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
        findPOs();
        allPurchaseOrders.clear();
        allPurchaseOrders = service.findAllPOs(Long.parseLong(projectId));
        log.info("Process on ["+new Date()+"] by user["+userSession.getCurrentUser().getUsername()+"]");
        log.info("PO["+currentPurchaseOrder.getProject()+", "+currentPurchaseOrder.getPo()+","+ currentPurchaseOrder.getVariation()+"]");
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
        if (entity.getId() != null) {
            currentPurchaseOrder = entity;
            pOrderList = service.findByProjectIdAndPo(project.getId(), entity.getPo());
            sortBean.sortPurchaseOrderEntity(pOrderList);
            String lastVarNumber = entity.getVariation();
            if (generateVariationNumber(lastVarNumber)) {
                purchaseOrderToVariation = service.findPOToCreateVariation(entity.getId());
                prepareToSaveWithNewVariation(purchaseOrderToVariation);
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('variationModal').show();");
            } else {
                Messages.addFlashGlobalError("The format variation is invalid");
            }
        }
    }

    private void prepareToSaveWithNewVariation(PurchaseOrderEntity purchaseOrderToVariation) {
        purchaseOrderToVariation.setId(null);

        purchaseOrderToVariation.getPurchaseOrderProcurementEntity().setId(null);
        purchaseOrderToVariation.getPurchaseOrderProcurementEntity().setDeliveryInstruction(project.getDeliveryInstructions());
        purchaseOrderToVariation.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
        purchaseOrderToVariation.setVariation(newVariationNumber);
        purchaseOrderToVariation.setResponsibleExpediting("");
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

    private boolean generateVariationNumber(String lastVarNumber) {
        String[] number = lastVarNumber.split("\\.");
        Integer lastNumber;
        boolean generateVar;
        try {
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
            generateVar = true;
        } catch (NumberFormatException nfe) {
            generateVar = false;
        }
        return generateVar;
    }

    public void doCommitPo() {
        log.info("doCommitPo()");
        if (currentPurchaseOrder != null) {
            Date startDoCommit = new Date();
            Date startFindCurrentPo = new Date();
            currentPurchaseOrder = service.findPOForCommitByPoId(currentPurchaseOrder.getId());
            Date endFindCurrentPo = new Date();
            log.info("time between startFindCurrentPo - endFindCurrentPo = " +(endFindCurrentPo.getTime() - startFindCurrentPo.getTime()));

            if (validate()) {
                currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.COMMITTED);
                commitPOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
                Date startPopulateFromPreviousRevision = new Date();
                populateFromPreviousRevision(currentPurchaseOrder);
                Date endPopulateFromPreviousRevision = new Date();
                log.info("time between populateFromPreviousRevision = " + (endPopulateFromPreviousRevision.getTime() - startPopulateFromPreviousRevision.getTime()));

                Date startUpdateROSDateInExpediting = new Date();
                List<ScopeSupplyEntity> supplyList = updateROSDateInExpediting();
                Date endUpdateROSDateInExpediting = new Date();
                log.info("time between updateROSDateInExpediting= "+(endUpdateROSDateInExpediting.getTime()-startUpdateROSDateInExpediting.getTime()));

                Date startUpdateOnlyPOOnProcurement = new Date();
                currentPurchaseOrder = service.updatePOAfterCommitted(currentPurchaseOrder);

               // currentPurchaseOrder.getPurchaseOrderProcurementEntity().setCanVariation(canCreateVariation(currentPurchaseOrder));

                Date endUpdateOnlyPOOnProcurement = new Date();
                log.info("time between UpdateOnlyPOOnProcurement = " + (endUpdateOnlyPOOnProcurement.getTime()-startUpdateOnlyPOOnProcurement.getTime()));

                Date startUpdateScopeSuppliesAfterCommitted = new Date();
                maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
                updateScopeSuppliesAfterCommitted(supplyList);
                Date endUpdateScopeSuppliesAfterCommitted = new Date();
                log.info("time between UpdateScopeSuppliesAfterCommitted = "+ (endUpdateScopeSuppliesAfterCommitted.getTime()-startUpdateScopeSuppliesAfterCommitted.getTime()));

                log.info("Process on ["+new Date()+"] by user["+userSession.getCurrentUser().getUsername()+"]");

                Date endDoCommit = new Date();
                log.info("time for doCommitPO = "+ (endDoCommit.getTime()-startDoCommit.getTime()));
                log.info("PurchaseOrderEntity values after doSave: " + currentPurchaseOrder.getPurchaseOrderProcurementEntity().toString());
            }
        }
        findPOs();
    }

    public void doCommitPOUpdateRow(UIData table, int index) {
        log.info("doCommitPOUpdateRow(UIData table, int index["+index+"])");
        /*long seed = System.nanoTime();
        Collections.shuffle(getPurchaseOrders(), new Random(seed));*/
        doCommitPo();
        Ajax.updateRow(table, index);
    }

    private void updateScopeSuppliesAfterCommitted(List<ScopeSupplyEntity> list) {
        log.info("updateScopeSuppliesAfterCommitted(List<ScopeSupplyEntity> list size : " + list.size());
        for (ScopeSupplyEntity s : list) {
            scopeSupplyService.doUpdate(s);
        }
    }

    private List<ScopeSupplyEntity> updateROSDateInExpediting() {
        log.info("private List<ScopeSupplyEntity> updateROSDateInExpediting()");
        List<ScopeSupplyEntity> scopeSupplyList = new ArrayList<>();
        //List<RequisitionEntity> requisitionList = requisitionDao.findRequisitionByPurchaseOrder(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getId());
        List<RequisitionEntity> requisitionList = new ArrayList<>();
        requisitionList.addAll(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getRequisitions());
        boolean wasUpdateRosMin = false;
        Date rosMin = null;
        for (RequisitionEntity r : requisitionList) {
            if(rosMin == null) {
                rosMin = r.getRequiredOnSiteDate();
                wasUpdateRosMin = true;
            }else if (r.getRequiredOnSiteDate() != null && r.getRequiredOnSiteDate().before(rosMin)) {
                rosMin = r.getRequiredOnSiteDate();
                wasUpdateRosMin = true;
            }
        }
        if (!requisitionList.isEmpty() && wasUpdateRosMin) {
            //scopeSupplyList = scopeSupplyService.findByPoId(currentPurchaseOrder.getId());
            scopeSupplyList.addAll(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getScopeSupplyEntities());
            for (ScopeSupplyEntity s : scopeSupplyList) {
                s.setRequiredSiteDate(rosMin);
            }
        }
        if(rosMin!=null && wasUpdateRosMin) {
            currentPurchaseOrder.setRequiredDate(rosMin);
        }
        return scopeSupplyList;
    }


    private void populateFromPreviousRevision(PurchaseOrderEntity currentPurchaseOrder) {
        if (currentPurchaseOrder.getOrderedVariation() > 0) {
            Long projectId = currentPurchaseOrder.getProjectEntity().getId();
            String po = currentPurchaseOrder.getPo();
            Integer previousOrderedVariation = currentPurchaseOrder.getOrderedVariation() - 1;
            PurchaseOrderEntity previousRevision = service.findPOPreviousRevision(projectId, po, previousOrderedVariation);
            if (previousRevision != null) {
                currentPurchaseOrder.setNextKeyDate(previousRevision.getNextKeyDate() != null ? previousRevision.getNextKeyDate() : null);
                currentPurchaseOrder.setNextKeyDateComment(StringUtils.isNotEmpty(previousRevision.getNextKeyDateComment()) ? previousRevision.getNextKeyDateComment() : null);
                currentPurchaseOrder.setGeneralComment(StringUtils.isNotEmpty(previousRevision.getGeneralComment()) ? previousRevision.getGeneralComment() : null);
            }
        }
    }

    public void doUncommit() {
        log.info("doUncommit()");
        Date d1 = new Date();
        if (currentPurchaseOrder != null) {
            currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
            unCommitPOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
            currentPurchaseOrder = service.updatePOStatus(currentPurchaseOrder);
            log.info("Process on ["+d1+"] by user["+userSession.getCurrentUser().getUsername() + "]");
            log.info("PurchaseOrderEntity values: " + currentPurchaseOrder.getPurchaseOrderProcurementEntity().toString());
        }
        Date d2 = new Date();
        log.info("time process doUncommit= " + (d2.getTime() - d1.getTime()));
    }

    public void doFinalise() {
        log.info("doFinalise()");
        Date d1 = new Date();
        if (currentPurchaseOrder != null) {
            currentPurchaseOrder = service.findById(currentPurchaseOrder.getId());
            if (validate()) {
                currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.FINAL);
                finalisePOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
                currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);
                log.info("Process on ["+ d1 + "] by user[" + userSession.getCurrentUser().getUsername() + "]");
                log.info("PurchaseOrderEntity values: " + currentPurchaseOrder.getPurchaseOrderProcurementEntity().toString());
            }
        }
        findPOs();
        Date d2 = new Date();
        log.info("time process doFinalise= " + (d2.getTime() - d1.getTime()));
    }

    public boolean doFinaliseAndPrint() {
        log.info("doFinalise()");
        Date d1 = new Date();
        boolean result = false;
        if (currentPurchaseOrder != null) {
            currentPurchaseOrder = service.findById(currentPurchaseOrder.getId());
            result = validate();
            if (result) {
                currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.FINAL);
                finalisePOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
                currentPurchaseOrder = service.updateOnlyPOOnProcurement(currentPurchaseOrder);
                log.info("Process on ["+ d1 + "] by user[" + userSession.getCurrentUser().getUsername() + "]");
                log.info("PurchaseOrderEntity values: " + currentPurchaseOrder.getPurchaseOrderProcurementEntity().toString());
            }
        }
        findPOs();
        Date d2 = new Date();
        log.info("time process doFinalise= " + (d2.getTime() - d1.getTime()));
        return result;
    }

    public void doReleasePo() {
        log.info("do release purchase order");
        Date d1 = new Date();
        currentPurchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
        releasePOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
        currentPurchaseOrder = service.updatePOStatus(currentPurchaseOrder);
        log.info("Process on [" + d1 + "] by user[" + userSession.getCurrentUser().getUsername() + "]");
        log.info("PurchaseOrderEntity values: " + currentPurchaseOrder.getPurchaseOrderProcurementEntity().toString());
        Date d2 = new Date();
        log.info("time process doReleasePo= " + (d2.getTime() - d1.getTime()));
    }

    public void doDeletePo() {
        log.info("do delete purchase order");
        deletePOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
        service.doDelete(currentPurchaseOrder.getId());
        log.info("Process on ["+new Date()+"] by user["+userSession.getCurrentUser().getUsername()+"]");
        log.info("PO["+currentPurchaseOrder.getProject()+", "+currentPurchaseOrder.getPo()+","+ currentPurchaseOrder.getVariation()+"]");
        PurchaseOrderEntity previousPO = service.findPOPreviousRevision(currentPurchaseOrder.getProjectEntity().getId(),currentPurchaseOrder.getPo(),currentPurchaseOrder.getOrderedVariation()-1);
        afterDeletePOModifyValidationButtons(previousPO.getPurchaseOrderProcurementEntity());
        service.updatePOStatus(previousPO);
        refreshListPo();
        log.info("PurchaseOrderEntity values: " + currentPurchaseOrder.getPurchaseOrderProcurementEntity().toString());
    }

    public boolean canView(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null) {
                return (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.READY.ordinal())
                        || (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.COMMITTED.ordinal()
                        || entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.FINAL.ordinal());
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean canEdit(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null) {
                return
                        (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.READY.ordinal())
                                || (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.ON_HOLD.ordinal()
                                || entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.INCOMPLETE.ordinal());
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isPossibleCreateVariation(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            return canCreateVariation(entity);
        }
        return false;
    }

    private boolean canCreateVariation(PurchaseOrderEntity entity) {
        log.info("canCreateVariation(PurchaseOrderEntity entity)");
        boolean canCreateVar = false;
        boolean poCommitted = false;
        if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null) {
            poCommitted = entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.COMMITTED.ordinal();
        }
        boolean isLastVariationAndCanCreateVariation = isLastPoWithStatusCommitted(entity, poCommitted);
        boolean existsLastVariationAndHasStatusIncomplete = verifyMaxVariationWithStatusIncomplete(entity);
        boolean existNextVariation = verifyNextPOVariationExists(entity);


        if (isLastVariationAndCanCreateVariation || (existsLastVariationAndHasStatusIncomplete && !existNextVariation && poCommitted)) {
            canCreateVar = true;
        }
        return canCreateVar;
    }

    private boolean isLastPoWithStatusCommitted(final PurchaseOrderEntity entity, final boolean poCommitted) {
        if (poCommitted) {
            for (Object po : maxVariationsList) {
                Object[] values = (Object[]) po;
                PurchaseOrderEntity poe = new PurchaseOrderEntity();
                poe.setPo((String) values[0]);
                poe.setOrderedVariation((Integer) values[1]);
                if (StringUtils.equals(entity.getPo(), poe.getPo()) && entity.getOrderedVariation() != null &&
                        entity.getOrderedVariation().intValue() == poe.getOrderedVariation().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verifyMaxVariationWithStatusIncomplete(final PurchaseOrderEntity entity) {
        PurchaseOrderEntity po = entity;
        for (PurchaseOrderEntity p : allPurchaseOrders) {
            if (po.getOrderedVariation() != null && p.getOrderedVariation() != null) {
                if (StringUtils.equals(entity.getPo(), p.getPo()) && p.getOrderedVariation().intValue() > po.getOrderedVariation().intValue()) {
                    po = p;
                }
            }
        }
        if (po.getOrderedVariation() != null && entity.getOrderedVariation() != null) {
            if (po.getPurchaseOrderProcurementEntity().getPoProcStatus() != null) {
                if (po.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.INCOMPLETE.ordinal() && po.getOrderedVariation().intValue() > entity.getOrderedVariation().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verifyNextPOVariationExists(final PurchaseOrderEntity entity) {
        String nextVariationStr = "";
        boolean nextVariationBool = false;
        try {
            if (StringUtils.isNotEmpty(entity.getVariation())) {
                Integer nextVariation = Integer.valueOf(entity.getVariation()) + 1;
                nextVariationStr = nextVariation.toString();
            }
            for (PurchaseOrderEntity p : allPurchaseOrders) {
                if (StringUtils.equals(p.getPo(), entity.getPo()) && StringUtils.equals(p.getVariation(), nextVariationStr)) {
                    nextVariationBool = true;
                    break;
                }
            }
        } catch (NumberFormatException nef) {

        }

        return nextVariationBool;
    }

    public boolean canCommitt(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null &&
                    (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.FINAL.ordinal() ||
                            entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.INCOMPLETE.ordinal());
        }
        return false;
    }

    public boolean canUncommit(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            boolean canUncommit = false;
            if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null) {
                if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.COMMITTED.ordinal()) {
                    if (entity.getPurchaseOrderProcurementEntity().getCmsExported() != null && entity.getPurchaseOrderProcurementEntity().getCmsExported()) {
                        return false;
                    }
                    if (entity.getPurchaseOrderProcurementEntity().getJdeExported() != null && entity.getPurchaseOrderProcurementEntity().getJdeExported()) {
                        return false;
                    }
                    canUncommit = true;
                }
            }
            return canUncommit;
        }
        return false;
    }

    public boolean canDelete(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null &&
                    entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() != ProcurementStatus.COMMITTED.ordinal();
        }
        return false;

    }

    public boolean actionForReady(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null && entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.READY.ordinal();
        }
        return false;
    }

    public boolean canRelease(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            return entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null && entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.FINAL.ordinal();
        }
        return false;
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
        log.info("printing PO  print final PDF");
        if (currentPurchaseOrder != null && currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.READY.ordinal()) {
            log.info("PO is ready status, trying doFinalize PO");
            //canPrintFinalPdf = doFinaliseAndPrint();
            if(doFinaliseAndPrint()){
                log.info("Passed validations before doFinalize");
                //printPo(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus(), false);
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("printFinalPdf();");
            }
        }else{
            log.info("PO is not ready status, printing final PO");
            //canPrintFinalPdf = true;
            //printPo(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus(), false);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("printFinalPdf();");
        }
        log.info("canPrintFinalPdf =  " + canPrintFinalPdf);
    }

    public void printFinalPOPDF(){
        log.info("printFinalPOPDF()");
        printPo(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus(), false);
    }


    public void printPODraft() {
        log.info("printing po draft");
        printPo(null, true);
    }

    private void printPo(ProcurementStatus procurementStatus, boolean draft) {
        List<ItemEntity> itemEntities = scopeSupplyService.getItemsBYPOId(currentPurchaseOrder.getId());
        TextEntity textEntity = textService.findByPoId(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getId());
        String preamble = textEntity != null ? textEntity.getPreamble() : "";
        List<ClausesEntity> clausesEntityList = getClausesEntitiesByPOid(textEntity);
        List<PODocumentEntity> poDocumentList = poDocumentService.findByPOId(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getId());
        reportProcBean.printPurchaseOrder(currentPurchaseOrder, itemEntities, preamble, clausesEntityList, draft, poDocumentList);
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

    public boolean canExportCMS(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null) {
                boolean exported = entity.getPurchaseOrderProcurementEntity().getCmsExported() == null ? false : entity.getPurchaseOrderProcurementEntity().getCmsExported();
                return entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.COMMITTED.ordinal()
                        && !exported;
            }
            return false;
        }
        return false;
    }

    public boolean canExportJDE(PurchaseOrderEntity entity) {
        if (entity.getId() != null) {
            if (entity.getPurchaseOrderProcurementEntity().getPoProcStatus() != null) {
                boolean exported = entity.getPurchaseOrderProcurementEntity().getJdeExported() == null ? false : entity.getPurchaseOrderProcurementEntity().getJdeExported();
                return entity.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.COMMITTED.ordinal()
                        && !exported;
            }
            return false;
        }
        return false;
    }

    public boolean canExportJDECsv(PurchaseOrderEntity entity){
        return entity.getId() != null ? true: false;
    }

    @Transactional
    public StreamedContent exportCMS() throws FileNotFoundException {
        log.info("exportCMS");
        StreamedContent content = null;
        if (currentPurchaseOrder != null && currentPurchaseOrder.getId() != null) {
            List<PurchaseOrderEntity> list = new ArrayList<>();
            list.add(service.findById(currentPurchaseOrder.getId()));
            InputStream is = exporter.generateWorkbook(list);
            //service.markCMSAsExported(currentPurchaseOrder);
            exportCsvPOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
            content = new DefaultStreamedContent(is, "application/xls", service.generateName(currentPurchaseOrder) + ".xlsx");
        }
        return content;
    }

    @Transactional
    public StreamedContent exportJDE() {
        log.info("exportJDE");
        StreamedContent content = null;
        if (currentPurchaseOrder != null && currentPurchaseOrder.getId() != null) {
            List<PurchaseOrderEntity> list = new ArrayList<>();
            list.add(service.findById(currentPurchaseOrder.getId()));
            InputStream is = exporterToJDE.generateWorkbook(list);
            exportJdePOModifyValidationButtons(currentPurchaseOrder.getPurchaseOrderProcurementEntity());
            //service.markJDEAsExported(currentPurchaseOrder);
            content = new DefaultStreamedContent(is, "application/xls", service.generateName(currentPurchaseOrder) + ".xlsx");
        }
        return content;
    }

    public void downloadJDECsvFile() throws IOException {
        log.info("Download Zip File...");
        List<String> pathCSVFiles = new ArrayList<>();
        try {
            if (currentPurchaseOrder != null && currentPurchaseOrder.getId() != null) {
                List<PurchaseOrderEntity> list = new ArrayList<>();
                list.add(service.findById(currentPurchaseOrder.getId()));
                String fName = StringUtils.isNotEmpty(project.getFolderName()) ? project.getFolderName() : project.getProjectNumber() + " " + project.getTitle();
                pathCSVFiles.addAll(jdeCsvService.generateJDECsvFileAndGetPaths(list,fName));
            }
        }catch (Exception e) {

        }

        List<String> pathCsv = new ArrayList<>();
        for (String path : pathCSVFiles){
            String[] str = path.split("\\.");
            pathCsv.add(str[0]+".csv");
        }

        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "inline; filename=JDECsv" + ".zip;");

        ServletOutputStream outputStream = null;
        ZipOutputStream zip = null;
        String packageHeaderInformation = "PkgHdr";
        String packageScheduleInformation = "PkgScInf";
        try {
            outputStream = response.getOutputStream();
            zip = new ZipOutputStream(outputStream);
            int i = 1 ;
            for (String path : pathCsv) {
                if(i==1) {
                    addFilesToZip(zip, path, packageHeaderInformation + ".csv");
                }else if(i==2){
                    addFilesToZip(zip, path, packageScheduleInformation + ".csv");
                }
                i++;

            }
        } finally {
            IOUtils.closeQuietly(zip);
            IOUtils.closeQuietly(outputStream);
        }
        jdeCsvService.deleteTemporalFiles(pathCSVFiles);
    }

    public void exportJdeCsv(){
        log.info("Exporting JDE Csv from list of PO");
        List<String> pathCSVFiles = new ArrayList<>();
        try {
            if (currentPurchaseOrder != null && currentPurchaseOrder.getId() != null) {
                List<PurchaseOrderEntity> list = new ArrayList<>();
                list.add(service.findById(currentPurchaseOrder.getId()));
                String fName = project.getProjectNumber();
                pathCSVFiles.addAll(jdeCsvService.generateJDECsvFileAndGetPaths(list,fName));
                jdeCsvService.deleteTemporalFiles(pathCSVFiles);
            }
        }catch (Exception e) {

        }
    }

    private void addFilesToZip(ZipOutputStream zip, String path, String fileName) throws IOException {
        FileInputStream file = null;
        try {
            zip.putNextEntry(new ZipEntry(fileName));
            file = new FileInputStream(path);
            IOUtils.copy(file, zip);
            zip.closeEntry();
        } finally {
            IOUtils.closeQuietly(file);
        }
    }

    private boolean validate() {
        Date startFindCurrentPo = new Date();
        boolean validate = true;
        if (StringUtils.isEmpty(currentPurchaseOrder.getPo())) {
            Messages.addFlashGlobalError("Enter a valid PO Number");
            validate = false;
        }
        if (StringUtils.isEmpty(currentPurchaseOrder.getPoTitle())) {
            Messages.addFlashGlobalError("Enter a valid PO Title");
            validate = false;
        }
        if (StringUtils.isEmpty(currentPurchaseOrder.getVariation())) {
            Messages.addFlashGlobalError("Enter a valid Var Number");
            validate = false;
        }
        if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() == null) {
            Messages.addFlashGlobalError("Enter a valid Supplier");
            validate = false;
        }
        if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getClazz() == null) {
            Messages.addFlashGlobalError("Enter a valid Class");
            validate = false;
        }
        if (currentPurchaseOrder.getPoDeliveryDate() == null) {
            Messages.addFlashGlobalError("Enter a valid Delivery Date");
            validate = false;
        }
        if (StringUtils.isEmpty(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getPoint())) {
            Messages.addFlashGlobalError("Enter a valid Delivery Point");
            validate = false;
        }
        if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() != null) {
            if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity() == null) {
                Messages.addFlashGlobalError("Enter a valid Contact for Supplier");
                validate = false;
            }
        }
        if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getScopeSupplyList().isEmpty()) {
            Messages.addFlashGlobalError("You must add at least one Item");
            validate = false;
        }
        if (!validateRetention(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow())) {
            validate = false;
        }
        if (!validateSecurityDeposit(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow())) {
            validate = false;
        }
        if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow() != null) {
            if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms() == null) {
                Messages.addFlashGlobalError("Enter a valid Payment Terms");
                validate = false;
            }
        }
        if (!balanceEqualZero()) {
            Messages.addFlashGlobalError("The Balance must be zero");
            validate = false;
        }
        Date endFindCurrentPo = new Date();
        log.info("time between startValidatePO - endValidatePO = " +(endFindCurrentPo.getTime() - startFindCurrentPo.getTime()));
        return validate;
    }

    private boolean balanceEqualZero() {
        if (!currentPurchaseOrder.getPurchaseOrderProcurementEntity().getScopeSupplyList().isEmpty()) {
            if (currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow() != null) {
                if (!currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow().getCashflowDetailList().isEmpty()) {
                    Map<ProjectCurrencyEntity, BigDecimal> balances = service.getBalanceByCurrency(currentPurchaseOrder.getPurchaseOrderProcurementEntity().getScopeSupplyList(), currentPurchaseOrder.getPurchaseOrderProcurementEntity().getCashflow().getCashflowDetailList());
                    for (ProjectCurrencyEntity currency : balances.keySet()) {
                        double roundOff = (double) Math.round(balances.get(currency).doubleValue() * 100) / 100;
                        if (roundOff != 0d) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean validateRetention(final CashflowEntity cashflow) {
        boolean validate = true;
        if (cashflow != null) {
            boolean applyRetentionSelected = cashflow.getApplyRetention() != null && cashflow.getApplyRetention().booleanValue();
            if (applyRetentionSelected) {
                if (StringUtils.isEmpty(cashflow.getForm())) {
                    Messages.addFlashGlobalError("Enter a valid Retention Form");
                    validate = false;
                }
                if (cashflow.getPercentage() == null) {
                    Messages.addFlashGlobalError("Enter a valid Retention Percentage");
                    validate = false;
                }
                if (cashflow.getExpDate() == null) {
                    Messages.addFlashGlobalError("Enter a valid Retention Exp Date");
                    validate = false;
                }
                if (cashflow.getProjectCurrency() == null) {
                    Messages.addFlashGlobalError("Enter a valid Retention Currency");
                    validate = false;
                }
            }
        }
        return validate;
    }

    private boolean validateSecurityDeposit(final CashflowEntity cashflow) {
        boolean validate = true;
        if (cashflow != null) {
            boolean applySecurityDeposit = cashflow.getApplyRetentionSecurityDeposit() != null && cashflow.getApplyRetentionSecurityDeposit().booleanValue();
            if (applySecurityDeposit) {
                if (StringUtils.isEmpty(cashflow.getFormSecurityDeposit())) {
                    Messages.addFlashGlobalError("Enter a valid Security Deposit Form");
                    validate = false;
                }
                if (cashflow.getPercentageSecurityDeposit() == null) {
                    Messages.addFlashGlobalError("Enter a valid Security Deposit Percentage");
                    validate = false;
                }
                if (cashflow.getExpirationDateSecurityDeposit() == null) {
                    Messages.addFlashGlobalError("Enter a valid Security Deposit Exp Date");
                    validate = false;
                }
                if (cashflow.getCurrencySecurityDeposit() == null) {
                    Messages.addFlashGlobalError("Enter a valid Security Deposit Currency");
                    validate = false;
                }
            }
        }
        return validate;
    }

    public FilterPO getFilter() {

        return (FilterPO) poManagerTable.getFilter();
    }


    public void findPOs() {
        log.info("PoListBean  findPOs");
        purchaseOrders.clear();
        List<PurchaseOrderEntity> list = service.findPosBy(getFilter());
        purchaseOrders.addAll(list);
        allPurchaseOrders.addAll(purchaseOrders);
    }

    public List<PurchaseOrderEntity> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrderEntity> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public String getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(String scrollTop) {
        this.scrollTop = scrollTop;
    }

    public String redirectToView() {
        return "edit.xhtml?faces-redirect=true&poId=" + currentPurchaseOrder.getId() + "&modeView=true&anchor=" + scrollTop;
    }

    public String redirectToEdit() {
        return "edit.xhtml?faces-redirect=true&poId=" + currentPurchaseOrder.getId() + "&anchor=" + scrollTop;
    }

    public DataTable getDataTable() {
        return managerTable.getDataTable();
    }

    public void setDataTable(DataTable datatable) {
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        if (managerTable.isSortInitialized()) {
            log.info("sorting by " + managerTable.getSortBy());
            datatable.setValueExpression("sortBy", expressionFactory.createValueExpression(elContext, managerTable.getSortBy(), Object.class));
            datatable.setSortOrder(managerTable.getDirection());
        }
        managerTable.setDataTable(datatable);
    }

    public void sortListener(SortEvent event) {
        managerTable.setSortBy(event.getSortColumn().getValueExpression("sortBy").getExpressionString());
        managerTable.setDirection(event.isAscending() ? "ascending" : "descending");
        managerTable.setSortInitialized(true);
        for (PurchaseOrderEntity po : purchaseOrders) {

        }
    }

    public void refreshListPo(){
        log.info("refreshListPo()");
        maxVariationsList = service.findPOMaxVariations(Long.parseLong(projectId));
        ((FilterPO) poManagerTable.getFilter()).setProjectId(project.getId());
        findPOs();
    }


    public void finalisePOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(false);
        po.setCanView(true);
        po.setCanVariation(false);
        po.setCanCommit(true);
        po.setCanUncommit(false);
        po.setCanExportCsv(false);
        po.setCanExportJde(false);
        po.setCanFinalise(false);
        po.setCanRelease(true);
        po.setCanDelete(true);
    }

    public void releasePOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(true);
        po.setCanView(true);
        po.setCanVariation(false);
        po.setCanCommit(false);
        po.setCanUncommit(false);
        po.setCanExportCsv(false);
        po.setCanExportJde(false);
        po.setCanFinalise(true);
        po.setCanRelease(false);
        po.setCanDelete(true);
    }

    public void commitPOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(false);
        po.setCanView(true);
        po.setCanVariation(true);
        po.setCanCommit(false);
        po.setCanUncommit(true);
        po.setCanExportCsv(true);
        po.setCanExportJde(true);
        po.setCanFinalise(false);
        po.setCanRelease(false);
        po.setCanDelete(false);
    }

    public void afterDeletePOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        log.info("afterDeletePOModifyValidationButtons(PurchaseOrderProcurementEntity po)");
        po.setCanEdit(false);
        po.setCanView(true);
        po.setCanVariation(true);
        po.setCanCommit(false);
        po.setCanUncommit(po.getCanUncommit());
        po.setCanExportCsv(po.getCanExportCsv());
        po.setCanExportJde(po.getCanExportJde());
        po.setCanFinalise(false);
        po.setCanRelease(false);
        po.setCanDelete(false);
    }

    public void unCommitPOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(true);
        po.setCanView(true);
        po.setCanVariation(false);
        po.setCanCommit(false);
        po.setCanUncommit(false);
        po.setCanExportCsv(false);
        po.setCanExportJde(false);
        po.setCanFinalise(true);
        po.setCanRelease(false);
        po.setCanDelete(true);
    }

    public void exportCsvPOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(false);
        po.setCanView(true);
        po.setCanVariation(true);
        po.setCanCommit(false);
        po.setCanUncommit(false);
        po.setCanExportCsv(false);
        po.setCanExportJde(po.getCanExportJde());
        po.setCanFinalise(false);
        po.setCanRelease(false);
        po.setCanDelete(false);
    }

    public void exportJdePOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(false);
        po.setCanView(true);
        po.setCanVariation(true);
        po.setCanCommit(false);
        po.setCanUncommit(false);
        po.setCanExportCsv(po.getCanExportCsv());
        po.setCanExportJde(false);
        po.setCanFinalise(false);
        po.setCanRelease(false);
        po.setCanDelete(false);
    }

    public void variationPOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(true);
        po.setCanView(true);
        po.setCanVariation(false);
        po.setCanCommit(false);
        po.setCanUncommit(false);
        po.setCanExportCsv(false);
        po.setCanExportJde(false);
        po.setCanFinalise(true);
        po.setCanRelease(false);
        po.setCanDelete(true);
    }

    public void variationCurrentPOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(false);
        po.setCanView(true);
        po.setCanVariation(false);
        po.setCanCommit(false);
        po.setCanUncommit(false);
        po.setCanExportCsv(po.getCanExportCsv());
        po.setCanExportJde(po.getCanExportJde());
        po.setCanFinalise(false);
        po.setCanRelease(false);
        po.setCanDelete(false);
    }

    public void deletePOModifyValidationButtons(PurchaseOrderProcurementEntity po){
        po.setCanEdit(false);
        po.setCanView(false);
        po.setCanVariation(false);
        po.setCanCommit(false);
        po.setCanUncommit(false);
        po.setCanExportCsv(false);
        po.setCanExportJde(false);
        po.setCanFinalise(false);
        po.setCanRelease(false);
        po.setCanDelete(false);
    }

    public boolean isCanPrintFinalPdf() {
        return canPrintFinalPdf;
    }

    public void setCanPrintFinalPdf(boolean canPrintFinalPdf) {
        this.canPrintFinalPdf = canPrintFinalPdf;
    }
}
