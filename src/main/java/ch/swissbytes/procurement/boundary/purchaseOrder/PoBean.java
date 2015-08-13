package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.procurement.boundary.Bean;
import ch.swissbytes.procurement.boundary.supplierProc.ContactBean;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcBean;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcList;
import org.apache.commons.lang.time.DateUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * Created by alvaro on 6/1/2015.
 */

@Named
@ViewScoped
public class PoBean extends Bean {

    private static final Logger log = Logger.getLogger(PoBean.class.getName());

    private PurchaseOrderEntity purchaseOrder;

    private String projectId;

    private String poId;

    private Boolean modeView;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private ProjectService projectService;

    @Inject
    private ItemBean itemBean;

    @Inject
    private RequisitionBean requisitionBean;

    @Inject
    private DeliverableBean deliverableBean;

    @Inject
    private PoTextBean poTextBean;

    @Inject
    private CashflowBean cashflowBean;

    @Inject
    private ContactBean contactBean;

    @Inject
    private SortBean sortBean;

    @Inject
    private SupplierProcBean supplier;

    @Inject
    private SupplierProcList list;

    @Inject
    private ScopeSupplyService scopeSupplyService;

    @Inject
    private Configuration configuration;

    private boolean supplierHeaderMode = false;
    private boolean supplierMode = false;
    private boolean loaded = false;


    public void load() {
        if (!loaded) {
            if (projectId != null) {
                try {
                    ProjectEntity projectEntity = projectService.findProjectById(Long.parseLong(projectId));
                    if (projectEntity != null) {
                        List<ProjectCurrencyEntity> projectCurrencyList = projectService.findProjectCurrencyByProjectId(projectEntity.getId());
                        purchaseOrder.setProjectEntity(projectEntity);
                        purchaseOrder.setProject(projectEntity.getProjectNumber());
                        purchaseOrder.setPoEntity(new POEntity());
                        purchaseOrder.getPoEntity().setOrderDate(new Date());
                        purchaseOrder.getPoEntity().setDeliveryInstruction(projectEntity.getDeliveryInstructions() != null ? projectEntity.getDeliveryInstructions() : "");
                        putModeCreation();
                        poTextBean.loadProjectTextSnippets(purchaseOrder.getProjectEntity().getId());
                        if (projectCurrencyList.size() == 1) {
                            purchaseOrder.getPoEntity().setCurrency(projectCurrencyList.get(0));
                        }
                    } else {
                        throw new IllegalArgumentException(" It is not a project valid");
                    }
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("It is not a project valid");
                }
            } else if (poId != null) {
                try {
                    purchaseOrder = service.findById(Long.valueOf(poId));
                    itemBean.loadItemList(purchaseOrder.getId());
                    cashflowBean.loadCashflow(purchaseOrder.getPoEntity().getId());
                    //poTextBean.loadProjectTextSnippets(purchaseOrder.getProjectEntity().getId());
                    poTextBean.loadText(purchaseOrder.getPoEntity(), purchaseOrder.getProjectEntity().getId());
                    if (purchaseOrder == null) {
                        throw new IllegalArgumentException("It is not a purchase order valid");
                    }
                    requisitionBean.getList().addAll(purchaseOrder.getPoEntity().getRequisitions());
                    deliverableBean.getList().addAll(purchaseOrder.getPoEntity().getDeliverables());
                    if (modeView == null) {
                        log.info("mode edition");
                        putModeEdition();
                    } else if (modeView) {
                        log.info("mode view");
                        putModeView();
                    }

                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("It is not a purchase Order valid");
                }
            } else {
                throw new IllegalArgumentException("it is neither a project valid nor a purchase order valid");
            }
        }
        loaded = true;
    }

    @Override
    protected void initialize() {
        purchaseOrder = new PurchaseOrderEntity();
    }


    public void doSave() {
        log.info("trying to save purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
            purchaseOrder = service.savePOOnProcurement(purchaseOrder);
            doLastOperationsOverPO(true);
            poId = purchaseOrder.getId().toString();
            projectId = null;
            loaded = false;
            load();
        }

    }

    public String doSaveAndClose() {
        log.info("trying to save and close purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
            purchaseOrder = service.savePOOnProcurement(purchaseOrder);
            return doLastOperationsOverPO(false);
        }
        return "";
    }

    public void doUpdate() {
        log.info("trying to update purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
            purchaseOrder = service.updatePOOnProcurement(purchaseOrder);
            doLastOperationsOverPO(true);
            loaded = false;
            load();
        }
    }

    public String doUpdateAndClose() {
        log.info("trying to update purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
            purchaseOrder = service.updatePOOnProcurement(purchaseOrder);
            String link = doLastOperationsOverPO(false);
            System.out.println("url " + link);
            return link;
        }
        return "";
    }

    private String doLastOperationsOverPO(boolean edit) {
        log.info("purchase order updated [" + purchaseOrder.getId() + "]");
        sortPurchaseListByVariationAndDoUpdate();
        sortScopeSupplyAndDoUpdate();
        Messages.addFlashGlobalInfo("The Purchase Order " + purchaseOrder.getPoEntity().getOrderTitle() + " has been saved.", null);
        return !edit ? backToList() : "edit?faces-redirect=true&poId=" + purchaseOrder.getId() + "";
    }

    public String doSaveView() {
        log.info("trying to saveView purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder = service.savePOOnProcurement(purchaseOrder);
            log.info("purchase order created [" + purchaseOrder.getId() + "]");
            sortPurchaseListByVariationAndDoUpdate();
            sortScopeSupplyAndDoUpdate();
            return backToList();
        }
        return "";
    }

    public String doUpdateView() {
        log.info("trying to updateView purchase order on procurement module");
        collectData();
        purchaseOrder = service.updatePOOnProcurement(purchaseOrder);
        log.info("purchase order created [" + purchaseOrder.getId() + "]");
        sortPurchaseListByVariationAndDoUpdate();
        sortScopeSupplyAndDoUpdate();
        return backToList();
    }

    public String backToList() {
        return "list.xhtml?faces-redirect=true&projectId=" + purchaseOrder.getProjectEntity().getId();
    }

    private void sortPurchaseListByVariationAndDoUpdate() {
        List<PurchaseOrderEntity> poList = service.findByProjectIdAndPo(purchaseOrder.getProjectEntity().getId(), purchaseOrder.getPo());
        sortBean.sortPurchaseOrderEntity(poList);
        int index = 1;
        for (PurchaseOrderEntity po : poList) {
            po.setOrderedVariation(index);
            service.doUpdatePurchaseOrder(po);
            index++;
        }
    }

    private void sortScopeSupplyAndDoUpdate() {
        List<ScopeSupplyEntity> sspList = scopeSupplyService.findByPurchaseOrder(purchaseOrder.getId());
        sortBean.sortScopeSupplyEntity(sspList);
        int index = 1;
        for (ScopeSupplyEntity ssp : sspList) {
            ssp.setOrdered(index);
            scopeSupplyService.doUpdate(ssp);
            index++;
        }
    }

    private boolean validate() {
        boolean validated = true;
        if (service.isVarNumberUsed(purchaseOrder)) {
            Messages.addFlashError("poVarNumber", "variation number is already being used");
            validated = false;
        }
        return validated;
    }

    private void collectData() {
        purchaseOrder.getPoEntity().getScopeSupplyList().addAll(itemBean.getScopeSupplyList());
        purchaseOrder.getPoEntity().getRequisitions().addAll(requisitionBean.getList());
        purchaseOrder.getPoEntity().getDeliverables().addAll(deliverableBean.getList());
        purchaseOrder.getPoEntity().setCashflow(cashflowBean.getCashflow());
        purchaseOrder.getPoEntity().getCashflow().getCashflowDetailList().addAll(cashflowBean.getCashflowDetailList());
        purchaseOrder.getPoEntity().setTextEntity(poTextBean.getTextEntity());
        purchaseOrder.getPoEntity().getTextEntity().getClausesList().addAll(poTextBean.getDroppedTextSnippetList());
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Boolean getModeView() {
        return modeView;
    }

    public void setModeView(Boolean modeView) {
        this.modeView = modeView;
    }

    public void updateRequiredDate(DeliverableEntity deliverable) {
        if (purchaseOrder.getPoEntity().getOrderDate() != null) {
            deliverable.setRequiredDate(DateUtils.addDays(purchaseOrder.getPoEntity().getOrderDate(), deliverable.getNoDays()));
        }

    }

    public void updateNoDays(DeliverableEntity deliverable) {
        if (purchaseOrder.getPoEntity().getOrderDate() != null && deliverable.getRequiredDate() != null) {
            long diff = deliverable.getRequiredDate().getTime() - purchaseOrder.getPoEntity().getOrderDate().getTime();
            deliverable.setNoDays(Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toDays(diff))));
        }
    }

    public void updateDeliverables() {
        for (DeliverableEntity deliverable : deliverableBean.getList()) {
            if (deliverable.getNoDays() != null) {
                updateRequiredDate(deliverable);
            } else if (deliverable.getRequiredDate() != null) {
                updateNoDays(deliverable);
            }
        }
    }

    public void doSaveContact() {
        ContactEntity contact = contactBean.doSave();
        if (contact != null) {
            purchaseOrder.getPoEntity().setContactEntity(contact);
            purchaseOrder.getPoEntity().getSupplier().getContacts().add(contact);
        }
    }

    public void addingSupplierHeader() {
        supplier.putModeCreation();
        supplier.start();
        supplierHeaderMode = true;
    }

    public void addingSupplier() {
        supplier.putModeCreation();
        supplier.start();
        supplierMode = true;
    }

    public void saveSupplier() {
        SupplierProcEntity supplierProcEntity = supplier.save();
        if (supplierProcEntity != null) {
            if (supplierHeaderMode) {
                purchaseOrder.getPoEntity().setSupplierHeader(supplierProcEntity);
            } else if (supplierMode) {
                purchaseOrder.getPoEntity().setSupplier(supplierProcEntity);
                purchaseOrder.getPoEntity().setContactEntity(null);
            }
            supplierHeaderMode = supplierMode = false;
            list.updateSupplierList();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('supplierModal').hide();");
        }
    }

    public BigDecimal calculateProjectValue() {
        return service.calculateProjectValue(itemBean.getScopeSupplyList(), purchaseOrder.getPoEntity().getCurrency());
    }

    public String calculateTotalValues() {
        Map<ProjectCurrencyEntity, BigDecimal> totals = service.getTotalValuesByCurrency(purchaseOrder.getPoEntity().getCurrency(), itemBean.getScopeSupplyList());
        return toString(totals);
    }
    private String toString(Map<ProjectCurrencyEntity,BigDecimal>map){
        StringBuilder sb = new StringBuilder();
        for (ProjectCurrencyEntity currency : map.keySet()) {
            sb.append(currency.getCurrency().getCode());
            sb.append(" ");
            sb.append(configuration.format(configuration.getPatternDecimal(),map.get(currency)));
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String calculateBalanceValue() {
        Map<ProjectCurrencyEntity, BigDecimal> balances=service.getBalanceByCurrency(purchaseOrder.getPoEntity().getCurrency(), itemBean.getScopeSupplyList(),cashflowBean.getCashflowDetailList());
        return toString(balances);
    }

}
