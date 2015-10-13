package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ProcurementStatus;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.procurement.boundary.Bean;
import ch.swissbytes.procurement.boundary.supplierProc.ContactBean;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcBean;
import ch.swissbytes.procurement.boundary.supplierProc.SupplierProcList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
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
    private CashflowService cashflowService;

    @Inject
    private Configuration configuration;


    @Inject
    private PoListBean listBean;


    private boolean supplierHeaderMode = false;
    private boolean supplierMode = false;
    private boolean loaded = false;


    private void initializeNewPurchaseOrder(ProjectEntity projectEntity) {
        List<ProjectCurrencyEntity> projectCurrencyList = projectService.findProjectCurrencyByProjectId(projectEntity.getId());
        purchaseOrder.setProjectEntity(projectEntity);
        purchaseOrder.setProject(projectEntity.getProjectNumber());
        purchaseOrder.setPurchaseOrderProcurementEntity(new PurchaseOrderProcurementEntity());
        purchaseOrder.getPurchaseOrderProcurementEntity().setOrderDate(new Date());
        purchaseOrder.getPurchaseOrderProcurementEntity().setDeliveryInstruction(projectEntity.getDeliveryInstructions() != null ? projectEntity.getDeliveryInstructions() : "");
        putModeCreation();
    }

    private void loadPurchaseOrder() {
        purchaseOrder = service.findById(Long.valueOf(poId));
        itemBean.loadItemList(purchaseOrder.getId());
        cashflowBean.loadCashflow(purchaseOrder.getPurchaseOrderProcurementEntity().getId());
        poTextBean.loadText(purchaseOrder.getPurchaseOrderProcurementEntity(), purchaseOrder.getProjectEntity().getId());
        if (purchaseOrder == null) {
            throw new IllegalArgumentException("It is not a purchase order valid");
        }
        requisitionBean.getList().clear();
        deliverableBean.getList().clear();
        requisitionBean.getList().addAll(purchaseOrder.getPurchaseOrderProcurementEntity().getRequisitions());
        deliverableBean.getList().addAll(purchaseOrder.getPurchaseOrderProcurementEntity().getDeliverables());
        if (modeView == null) {
            log.info("mode edition");
            putModeEdition();
            service.removePrefixIfAny(purchaseOrder);
        } else if (modeView) {
            log.info("mode view");
            putModeView();
        }
    }

    private void initializeValuesBooleanInHeaderSection() {
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getVendorDrawingData() == null) {
            purchaseOrder.getPurchaseOrderProcurementEntity().setVendorDrawingData(false);
        }
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getExchangeRateVariation() == null) {
            purchaseOrder.getPurchaseOrderProcurementEntity().setExchangeRateVariation(false);
        }
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getSecurityDeposit() == null) {
            purchaseOrder.getPurchaseOrderProcurementEntity().setSecurityDeposit(false);
        }
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getLiquidatedDamagesApplicable() == null) {
            purchaseOrder.getPurchaseOrderProcurementEntity().setLiquidatedDamagesApplicable(false);
        }
    }

    public void load() {
        if (!loaded) {
            if (projectId != null) {
                try {
                    ProjectEntity projectEntity = projectService.findProjectById(Long.parseLong(projectId));
                    if (projectEntity != null) {
                        initializeNewPurchaseOrder(projectEntity);
                    } else {
                        throw new IllegalArgumentException(" It is not a project valid");
                    }
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("It is not a project valid");
                }
            } else if (poId != null) {
                try {
                    loadPurchaseOrder();
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("It is not a purchase Order valid");
                }
            } else {
                    throw new IllegalArgumentException("it is neither a project valid nor a purchase order valid");
            }
            initializeValuesBooleanInHeaderSection();
        }
        loaded = true;
    }

    public void canEditPo() {
        if (poId != null) {
            purchaseOrder = service.findById(Long.valueOf(poId));
            ProcurementStatus status = purchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus();
            if (status != null) {
                if ((modeView==null|| (modeView!=null&&!modeView))
                        &&status.ordinal() == ProcurementStatus.COMMITTED.ordinal() || status.ordinal() == ProcurementStatus.FINAL.ordinal()) {
                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect("list.jsf?projectId="+purchaseOrder.getProjectEntity().getId());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void initialize() {
        purchaseOrder = new PurchaseOrderEntity();
        loaded = false;
    }


    public void doSave() {
        log.info("trying to save purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
            purchaseOrder = service.savePOOnProcurement(purchaseOrder);
            doLastOperationsOverPO(true);
            poId = purchaseOrder.getId().toString();
            projectId = null;
            loaded = false;
            loadPurchaseOrder();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("restartChanges();");
        }

    }

    public String doSaveAndClose() {
        log.info("trying to save and close purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
            purchaseOrder = service.savePOOnProcurement(purchaseOrder);
            return doLastOperationsOverPO(false);
        }
        return null;
    }

    public void doUpdate() {
        log.info("trying to update purchase order on procurement module");
        if (validate()) {
            collectData();
            if(purchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus()==null){
                purchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
            }
            if (purchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() != ProcurementStatus.INCOMPLETE.ordinal()) {
                purchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
            }
            purchaseOrder = service.updatePOOnProcurement(purchaseOrder);
            doLastOperationsOverPO(true);
            loaded = false;
            loadPurchaseOrder();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("restartChanges();");
        }
    }

    public String doUpdateAndClose() {
        log.info("trying to update purchase order on procurement module");
        if (validate()) {
            collectData();
            if (purchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() != ProcurementStatus.INCOMPLETE.ordinal()) {
                purchaseOrder.getPurchaseOrderProcurementEntity().setPoProcStatus(ProcurementStatus.READY);
            }
            purchaseOrder = service.updatePOOnProcurement(purchaseOrder);
            String link = doLastOperationsOverPO(false);
            return link;
        }
        return null;
    }

    private String doLastOperationsOverPO(boolean edit) {
        log.info("purchase order updated [" + purchaseOrder.getId() + "]");
        sortPurchaseListByVariationAndDoUpdate();
        sortScopeSupplyAndDoUpdate();
        sortCashflowDetailAndDoUpdate();
        Messages.addFlashGlobalInfo("The Purchase Order " + purchaseOrder.getPoTitle() + " has been saved.", null);
        return !edit ? backToList() : "edit?faces-redirect=true&poId=" + purchaseOrder.getId() + "";
    }

    public String doSaveView() {
        log.info("trying to saveView purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder = service.savePOOnProcurement(purchaseOrder);
            log.info("purchase order created [" + purchaseOrder.getId() + "]");
            doLastOperationsOverPO(true);
            listBean.setCurrentPurchaseOrder(purchaseOrder);
            poId = purchaseOrder.getId().toString();
            loadPurchaseOrder();
            log.info("saved po "+poId);
            RequestContext context = RequestContext.getCurrentInstance();
            log.info("restarting changes!");
            context.execute("restartChanges();");
            log.info("printing draft!");
            context.execute("printDraft();");
        }
        return null;
    }

    public String doUpdateView() {
        log.info("trying to updateView purchase order on procurement module");
        if (validate()) {
            collectData();
            purchaseOrder = service.updatePOOnProcurement(purchaseOrder);
            log.info("purchase order created [" + purchaseOrder.getId() + "]");
            doLastOperationsOverPO(true);
            listBean.setCurrentPurchaseOrder(purchaseOrder);
            loadPurchaseOrder();
            log.info("saved po "+poId);
            RequestContext context = RequestContext.getCurrentInstance();
            log.info("restarting changes!");
            context.execute("restartChanges();");
            log.info("printing draft!");
            context.execute("printDraft();");
        }
        return null;
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

    private void sortCashflowDetailAndDoUpdate() {
        List<CashflowEntity> cList = cashflowService.findByPoId(purchaseOrder.getPurchaseOrderProcurementEntity().getId());
        if (!cList.isEmpty()) {
            CashflowEntity entity = cList.get(0);
            List<CashflowDetailEntity> cashflowList = cashflowService.findDetailByCashflowId(entity.getId());
            sortBean.sortCashFlowDetailEntity(cashflowList);
            int index = 1;
            for (CashflowDetailEntity cde : cashflowList) {
                cde.setOrdered(index);
                index++;
            }
            cashflowService.doUpdateDetail(cashflowList);
        }

    }

    private boolean validate() {
        boolean validated = true;
        if (cashflowBean.getPaymentTerms() == null) {
            Messages.addFlashGlobalError("Please enter Payment Terms");
            validated = false;
        }
        if (!cashflowBean.validateRetention()) {
            validated = false;
        }
        if (cashflowBean.getCashflow().getPaymentTerms() == null) {
            Messages.addFlashGlobalError("Enter a valid Payment Terms in Cashflow");
            validated = false;
        }
        if (service.isVarNumberUsed(purchaseOrder)) {
            Messages.addFlashGlobalError("The variation number is already being used");
            validated = false;
        }
        if(purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() == null){
            Messages.addFlashGlobalError("Enter a valid Supplier in Header");
            validated = false;
        }
        if(purchaseOrder.getPurchaseOrderProcurementEntity().getClazz() == null){
            Messages.addFlashGlobalError("Enter a valid Class in Header");
            validated = false;
        }
        if(purchaseOrder.getPoDeliveryDate() == null){
            Messages.addFlashGlobalError("Enter a valid Delivery Date in Header");
            validated = false;
        }
        if(StringUtils.isEmpty(purchaseOrder.getPurchaseOrderProcurementEntity().getPoint())){
            Messages.addFlashGlobalError("Enter a valid Delivery Point in Header");
            validated = false;
        }
        return validated;
    }

    public void validateAll() {
        boolean validate = true;
        if (StringUtils.isEmpty(purchaseOrder.getPo())) {
            Messages.addFlashGlobalError("Enter a valid PO Number");
            validate = false;
        }
        if (StringUtils.isEmpty(purchaseOrder.getPoTitle())) {
            Messages.addFlashGlobalError("Enter a valid PO Title");
            validate = false;
        }
        if (StringUtils.isEmpty(purchaseOrder.getVariation())) {
            Messages.addFlashGlobalError("Enter a valid Var Number");
            validate = false;
        }
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() == null) {
            Messages.addFlashGlobalError("Enter a valid Supplier");
            validate = false;
        }
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getClazz() == null) {
            Messages.addFlashGlobalError("Enter a valid Class");
            validate = false;
        }
        if (purchaseOrder.getPoDeliveryDate() == null) {
            Messages.addFlashGlobalError("Enter a valid Delivery Date");
            validate = false;
        }
        if (StringUtils.isEmpty(purchaseOrder.getPurchaseOrderProcurementEntity().getPoint())) {
            Messages.addFlashGlobalError("Enter a valid Delivery Point");
            validate = false;
        }
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() != null) {
            if (purchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity() == null) {
                Messages.addFlashGlobalError("Enter a valid Contact for Supplier");
                validate = false;
            }
        }
        if (itemBean.getScopeSupplyList().isEmpty()) {
            Messages.addFlashGlobalError("You must add at least one Item");
            validate = false;
        }
        if (!validateRetention(cashflowBean.getCashflow())) {
            validate = false;
        }
        if (!validateSecurityDeposit(cashflowBean.getCashflow())) {
            validate = false;
        }
        if (cashflowBean.getCashflow().getPaymentTerms() == null) {
            Messages.addFlashGlobalError("Enter a valid Payment Terms");
            validate = false;
        }
        if(!balanceEqualZero()){
            Messages.addFlashGlobalError("The Balance must be zero");
            validate = false;
        }
        if(validate){
            Messages.addFlashGlobalInfo("This PO has been validated");
        }
    }

    private boolean balanceEqualZero(){
        if(!itemBean.getScopeSupplyList().isEmpty()){
            if(cashflowBean.getCashflow()!=null){
                if(!cashflowBean.getCashflowDetailList().isEmpty()){
                    Map<ProjectCurrencyEntity, BigDecimal> balances = service.getBalanceByCurrency(itemBean.getScopeSupplyList(), cashflowBean.getCashflowDetailList());
                    for (ProjectCurrencyEntity currency : balances.keySet()) {
                        if(balances.get(currency).doubleValue() != 0d){
                            return false;
                        }
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
        return true;
    }

    private boolean validateSecurityDeposit(final CashflowEntity cashflow) {
        boolean validate = true;
        if(cashflow!=null){
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

    private boolean validateRetention(final CashflowEntity cashflow) {
        boolean validate = true;
        if(cashflow!=null){
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

    private void collectData() {
        purchaseOrder.getPurchaseOrderProcurementEntity().getScopeSupplyList().addAll(itemBean.getScopeSupplyList());
        purchaseOrder.getPurchaseOrderProcurementEntity().getRequisitions().addAll(requisitionBean.getList());
        purchaseOrder.getPurchaseOrderProcurementEntity().getDeliverables().addAll(deliverableBean.getList());
        purchaseOrder.getPurchaseOrderProcurementEntity().setCashflow(cashflowBean.getCashflow());
        purchaseOrder.getPurchaseOrderProcurementEntity().getCashflow().getCashflowDetailList().addAll(cashflowBean.getCashflowDetailList());
        purchaseOrder.getPurchaseOrderProcurementEntity().setTextEntity(poTextBean.getTextEntity());
        purchaseOrder.getPurchaseOrderProcurementEntity().getTextEntity().getClausesList().addAll(poTextBean.getDroppedTextSnippetList());
    }

    public String phoneContact() {
        String phone = "";
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity() != null && purchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity().getId() != null) {
            phone = purchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity().getPhone();
            if (StringUtils.isEmpty(phone)) {
                String phoneSupplier = purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier().getPhone();
                if (purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() != null && StringUtils.isNotEmpty(phoneSupplier))
                    return phoneSupplier;
            }
        }
        return phone;
    }

    public String faxContact() {
        String fax = "";
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity() != null && purchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity().getId() != null) {
            fax = purchaseOrder.getPurchaseOrderProcurementEntity().getContactEntity().getFax();
            if (StringUtils.isEmpty(fax)) {
                String faxSupplier = purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier().getFax();
                if (purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier() != null && StringUtils.isNotEmpty((faxSupplier))) {
                    return faxSupplier;
                }
            }
        }
        return fax;
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
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getOrderDate() != null) {
            deliverable.setRequiredDate(DateUtils.addDays(purchaseOrder.getPurchaseOrderProcurementEntity().getOrderDate(), deliverable.getNoDays()));
        }

    }

    public void updateNoDays(DeliverableEntity deliverable) {
        if (purchaseOrder.getPurchaseOrderProcurementEntity().getOrderDate() != null && deliverable.getRequiredDate() != null) {
            long diff = deliverable.getRequiredDate().getTime() - purchaseOrder.getPurchaseOrderProcurementEntity().getOrderDate().getTime();
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
            purchaseOrder.getPurchaseOrderProcurementEntity().setContactEntity(contact);
            purchaseOrder.getPurchaseOrderProcurementEntity().getSupplier().getContacts().add(contact);
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
            purchaseOrder.getPurchaseOrderProcurementEntity().setSupplier(supplierProcEntity);
            purchaseOrder.getPurchaseOrderProcurementEntity().setContactEntity(null);
            list.updateSupplierList();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('supplierModal').hide();");
        }
    }

    public BigDecimal calculateProjectValue() {
        return service.calculateProjectValue(itemBean.getScopeSupplyList(), purchaseOrder.getPurchaseOrderProcurementEntity().getCurrency());
    }

    public String calculateTotalValues() {
        Map<ProjectCurrencyEntity, BigDecimal> totals = service.getTotalValuesByCurrency(itemBean.getScopeSupplyList());
        return toString(totals);
    }

    public String calculateAmountForCashflow(BigDecimal percentage) {
        Map<ProjectCurrencyEntity, BigDecimal> retention = cashflowBean.calculateAmount(service.getTotalValuesByCurrency(itemBean.getScopeSupplyList()), percentage);
        return toString(retention);
    }

    public void calculatePaymentValue(CashflowDetailEntity detailEntity) {
        log.info("calculatePaymentValue...");
        if (detailEntity.getProjectCurrency() != null && detailEntity.getPercentage() != null) {
            Map<ProjectCurrencyEntity, BigDecimal> totals = service.getTotalValuesByCurrency(itemBean.getScopeSupplyList());
            final Iterator<ProjectCurrencyEntity> it = totals.keySet().iterator();
            ProjectCurrencyEntity n;
            while (it.hasNext()) {
                n = it.next();
                if (n.getId().longValue() == detailEntity.getProjectCurrency().getId().longValue()) {
                    detailEntity.setOrderAmt(calculateBasedPercentageAndTotalValue(detailEntity.getPercentage(), totals.get(detailEntity.getProjectCurrency())));
                    break;
                } else {
                    detailEntity.setOrderAmt(null);
                }
            }
            if (detailEntity.getOrderAmt() != null) {
                detailEntity.setProjectAmt(calculateProjectValueByPaymentValueAndCurrency(detailEntity.getProjectCurrency().getCurrencyFactor(), detailEntity.getOrderAmt()));
            } else {
                detailEntity.setProjectAmt(null);
            }
        }
    }

    private BigDecimal calculateBasedPercentageAndTotalValue(final BigDecimal iPercentage, final BigDecimal totalValue) {
        BigDecimal paymentValueBig = totalValue.multiply(iPercentage).divide(new BigDecimal(100));
        return paymentValueBig;
    }

    private BigDecimal calculateProjectValueByPaymentValueAndCurrency(final BigDecimal currencyFactor, final BigDecimal paymentValue) {
        BigDecimal projectValue = paymentValue.divide(currencyFactor, BigDecimal.ROUND_HALF_UP);
        return projectValue;
    }

    private String toString(Map<ProjectCurrencyEntity, BigDecimal> map) {
        StringBuilder sb = new StringBuilder();
        for (ProjectCurrencyEntity currency : map.keySet()) {
            String symbol = StringUtils.isNotEmpty(currency.getCurrency().getSymbol()) && StringUtils.isNotBlank(currency.getCurrency().getSymbol()) ? currency.getCurrency().getSymbol() : currency.getCurrency().getCode();
            sb.append(symbol);
            sb.append(" ");
            sb.append(configuration.format(configuration.getPatternDecimal(), map.get(currency)));
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String calculateBalanceValue() {
        Map<ProjectCurrencyEntity, BigDecimal> balances = service.getBalanceByCurrency(itemBean.getScopeSupplyList(), cashflowBean.getCashflowDetailList());
        return toString(balances);
    }

    public boolean poIsOriginal() {
        if (purchaseOrder.getOrderedVariation() == null || purchaseOrder.getOrderedVariation().intValue() == 1 ||
                purchaseOrder.getPurchaseOrderProcurementEntity().getPoProcStatus().ordinal() == ProcurementStatus.INCOMPLETE.ordinal()) {
            return true;
        }
        return false;
    }

    public void resetContact() {
        log.info("reset contact");
        purchaseOrder.getPurchaseOrderProcurementEntity().setContactEntity(new ContactEntity());
    }

    public void loadSecurityDeposit(boolean applyRetention) {
        purchaseOrder.getPurchaseOrderProcurementEntity().setSecurityDeposit(applyRetention);
    }

    public String verifyAndGetProjectId(){
        if(purchaseOrder!=null && purchaseOrder.getProjectEntity()!=null){
            return purchaseOrder.getProjectEntity().getId().toString();
        }
        return "";
    }

}
