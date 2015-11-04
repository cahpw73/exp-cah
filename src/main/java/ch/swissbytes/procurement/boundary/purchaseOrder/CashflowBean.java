package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.milestone.MilestoneItemService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.CashflowDetailEntity;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.MilestoneItem;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.types.PaymentTermsEnum;
import ch.swissbytes.domain.types.RetentionFormEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;


/**
 * Created by Christian 02/06/2015
 */

@Named
@ViewScoped
public class CashflowBean implements Serializable {

    public static final Logger log = Logger.getLogger(CashflowBean.class.getName());

    @Inject
    private CashflowService cashflowService;

    @Inject
    private SortBean sortBean;

    @Inject
    private Configuration configuration;

    @Inject
    private PurchaseOrderService poService;
    @Inject
    private MilestoneItemService milestoneItemService;

    private CashflowEntity cashflow;

    private List<CashflowDetailEntity> cashflowDetailList;

    private Long preId = -1L;
    private List<MilestoneItem> milestoneItems;

    @PostConstruct
    public void create() {
        log.info("create poTextBean");
        cashflow = new CashflowEntity();
        cashflowDetailList = new ArrayList<>();
        milestoneItems=milestoneItemService.findAllMilestoneItems();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy poTextBean");
    }

    public void loadCashflow(final Long poId) {
        List<CashflowEntity> list = cashflowService.findByPoId(poId);
        if (!list.isEmpty()) {
            cashflow = list.get(0);
            cashflowDetailList = cashflowService.findDetailByCashflowId(cashflow.getId());
            if (!cashflowDetailList.isEmpty()) {
                sortBean.sortCashFlowDetailEntity(cashflowDetailList);
            }
        }
    }

    public boolean validateRetention() {
        boolean validate = true;
        boolean applyRetentionSelected = cashflow.getApplyRetention() != null && cashflow.getApplyRetention().booleanValue();
        if (applyRetentionSelected) {
            if (StringUtils.isEmpty(cashflow.getForm())){
                Messages.addFlashGlobalError("Enter a valid Retention Form");
                validate = false;
            }
            if(cashflow.getPercentage() == null){
                Messages.addFlashGlobalError("Enter a valid Retention Percentage");
                validate = false;
            }
            if (cashflow.getExpDate() == null){
                Messages.addFlashGlobalError("Enter a valid Retention Exp Date");
                validate = false;
            }
            if(cashflow.getProjectCurrency() == null) {
                Messages.addFlashGlobalError("Enter a valid Retention Currency");
                validate = false;
            }
        }
        return validate;
    }

    public boolean validateSecurityDeposit() {
        boolean validate = true;
        boolean applySecurityDeposit = cashflow.getApplyRetentionSecurityDeposit() != null && cashflow.getApplyRetentionSecurityDeposit().booleanValue();
        if (applySecurityDeposit) {
            if (StringUtils.isEmpty(cashflow.getFormSecurityDeposit())){
                Messages.addFlashGlobalError("Enter a valid Security Deposit Form");
                validate = false;
            }
            if(cashflow.getPercentageSecurityDeposit() == null){
                Messages.addFlashGlobalError("Enter a valid Security Deposit Percentage");
                validate = false;
            }
            if(cashflow.getExpirationDateSecurityDeposit() == null){
                Messages.addFlashGlobalError("Enter a valid Security Deposit Exp Date");
                validate = false;
            }
            if(cashflow.getCurrencySecurityDeposit() == null) {
                Messages.addFlashGlobalError("Enter a valid Security Deposit Currency");
                validate = false;
            }
        }
        return validate;
    }

    public void confirmCashflowDetail(CashflowDetailEntity entity) {
        log.info("confirm item");
        if (itemNoIsNotEmpty(entity)) {
            int index = cashflowDetailList.indexOf(entity);
            cashflowDetailList.set(index, entity);
            entity.stopEditing();
        }
    }

    public void deleteCashflowDetail(CashflowDetailEntity entity) {
        log.info("delete item");
        if (entity.getId() < 0L) {
            cashflowDetailList.remove(entity);
        } else {
            entity.setStatus(StatusEnum.DELETED);
        }
        sortBean.sortCashFlowDetailEntity(cashflowDetailList);
    }

    public void editCashflowDetail(CashflowDetailEntity entity) {
        log.info("edit item");
        entity.startEditing();
        entity.storeOldValue(entity);
        sortBean.sortCashFlowDetailEntity(cashflowDetailList);
    }

    public void cancelEditionCashDetail(CashflowDetailEntity entity) {
        log.info("cancel item");
        if (noHasData(entity)) {
            cashflowDetailList.remove(entity);
        } else {
            entity.stopEditing();
            entity = (CashflowDetailEntity) entity.getValueCloned();
        }
        sortBean.sortCashFlowDetailEntity(cashflowDetailList);
    }

    public boolean hasNotStatusDeleted(CashflowDetailEntity entity) {
        if (entity != null && entity.getStatus() != null)
            return StatusEnum.DELETED.getId().intValue() != entity.getStatus().getId().intValue();
        else
            return true;
    }

    public void addItem() {
        log.info("add Item");
        if (lastItemNoIsNotEmpty()) {
            CashflowDetailEntity entity = new CashflowDetailEntity();
            entity.setId(preId);
            entity.startEditing();
            cashflowDetailList.add(entity);
            preId--;
            sortBean.sortCashFlowDetailEntity(cashflowDetailList);
        }
    }

    private boolean lastItemNoIsNotEmpty() {
        int index = cashflowDetailList.size();
        if (index > 0) {
            CashflowDetailEntity lastItem = cashflowDetailList.get(index - 1);
            return itemNoIsNotEmpty(lastItem);
        }
        return true;
    }

    private boolean itemNoIsNotEmpty(CashflowDetailEntity entity) {
        log.info("item is not empty");
        return StringUtils.isNotEmpty(entity.getItem()) && StringUtils.isNotBlank(entity.getItem());
    }

    public List<MilestoneItem> getMilestoneItems() {
        return milestoneItems;
    }

    private boolean noHasData(CashflowDetailEntity entity) {
        if (entity.getPaymentDate() == null && entity.getClaimDate() == null
                && (StringUtils.isEmpty(entity.getItem()) && StringUtils.isBlank(entity.getItem()))
                && (StringUtils.isEmpty(entity.getMilestone()) && StringUtils.isBlank(entity.getMilestone()))
                && entity.getOrderAmt() == null && entity.getProjectAmt() == null
                && entity.getProjectCurrency() == null) {
            return true;
        }
        return false;
    }

    public String formattedPercentage(BigDecimal percentage) {
        if (percentage == null) {
            return "";
        }
        Double percentageN = percentage.doubleValue();
        Integer percentageI = percentageN.intValue();
        return percentageI.toString();
    }

    public List<PaymentTermsEnum> getPaymentTerms() {
        List<PaymentTermsEnum> paymentTerms = new ArrayList<>();
        for (PaymentTermsEnum s : PaymentTermsEnum.values()) {
            paymentTerms.add(s);
        }
        Collections.sort(paymentTerms, new Comparator<PaymentTermsEnum>() {
            @Override
            public int compare(PaymentTermsEnum o1, PaymentTermsEnum o2) {
                return o1.getOrdered() > o2.getOrdered() ? 1 : -1;
            }
        });
        return paymentTerms;
    }

    public List<RetentionFormEnum> getRetentionForms() {
        List<RetentionFormEnum> retentionForms = new ArrayList<>();
        for (RetentionFormEnum s : RetentionFormEnum.values()) {
            retentionForms.add(s);
        }
        return retentionForms;
    }

    public void calculatePaymentDate(CashflowDetailEntity detailEntity) {
        if (detailEntity.getClaimDate() != null) {
            calculatePaymentDatePO(detailEntity);
        } else {
            detailEntity.setPaymentDate(null);
        }

    }

    public void calculatePaymentDateBasedPaymentTerms() {
        for (CashflowDetailEntity detailEntity : cashflowDetailList) {
            calculatePaymentDatePO(detailEntity);
        }
    }

    private void calculatePaymentDatePO(CashflowDetailEntity detailEntity) {
        if (cashflow.getPaymentTerms() != null && detailEntity.getClaimDate() != null) {
            switch (cashflow.getPaymentTerms()) {
                case NET_60:
                    detailEntity.setPaymentDate(DateUtil.sumNDays(detailEntity.getClaimDate(), 60));
                    break;
                case NET_45:
                    detailEntity.setPaymentDate(DateUtil.sumNDays(detailEntity.getClaimDate(), 45));
                    break;
                case NET_30:
                    detailEntity.setPaymentDate(DateUtil.sumNDays(detailEntity.getClaimDate(), 30));
                    break;
                case NET_14:
                    detailEntity.setPaymentDate(DateUtil.sumNDays(detailEntity.getClaimDate(), 15));
                    break;
                case NET_7:
                    detailEntity.setPaymentDate(DateUtil.sumNDays(detailEntity.getClaimDate(), 7));
                    break;
                default:
                    detailEntity.setPaymentDate(detailEntity.getClaimDate());
                    break;
            }
        } else if (cashflow.getPaymentTerms() == null && detailEntity.getClaimDate() != null) {
            detailEntity.setPaymentDate(Util.convertUTC(detailEntity.getClaimDate(), configuration.getTimeZone()));
        }
    }


    public CashflowEntity getCashflow() {
        return cashflow;
    }

    public void setCashflow(CashflowEntity cashflow) {
        this.cashflow = cashflow;
    }

    public List<CashflowDetailEntity> getCashflowDetailList() {
        return cashflowDetailList;
    }

    public Map<ProjectCurrencyEntity, BigDecimal> calculateAmount(Map<ProjectCurrencyEntity, BigDecimal> currencies, BigDecimal percentage) {
        Map<ProjectCurrencyEntity, BigDecimal> percentages = new HashMap<>();
        BigDecimal per = percentage != null ? percentage.divide(new BigDecimal("100")) : new BigDecimal("0");
        for (ProjectCurrencyEntity currency : currencies.keySet()) {
            BigDecimal value = currencies.get(currency);
            percentages.put(currency, value != null ? value.multiply(per) : new BigDecimal("0"));
        }
        return percentages;

    }

    public void loadRetentionSecurityDeposit(boolean retentionSecurityDeposit) {
        cashflow.setApplyRetentionSecurityDeposit(retentionSecurityDeposit);
    }
}
