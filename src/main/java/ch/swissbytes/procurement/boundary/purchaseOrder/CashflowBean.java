package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.domain.model.entities.CashflowDetailEntity;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.types.PaymentTermsEnum;
import ch.swissbytes.domain.types.RetentionFormEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.DateUtil;
import javafx.scene.layout.BackgroundRepeat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.primefaces.event.DragDropEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private CashflowEntity cashflow;

    private List<CashflowDetailEntity> cashflowDetailList;

    private Long preId = -1L;

    @PostConstruct
    public void create() {
        log.info("create poTextBean");
        cashflow = new CashflowEntity();
        cashflowDetailList = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy poTextBean");
    }

    public void loadCashflow(final Long poId){
        List<CashflowEntity> list=cashflowService.findByPoId(poId);
        if(!list.isEmpty()) {
            cashflow = list.get(0);
            cashflowDetailList = cashflowService.findDetailByCashflowId(cashflow.getId());
            if (!cashflowDetailList.isEmpty()) {
                sortBean.sortCashFlowDetailEntity(cashflowDetailList);
            }
        }
    }

    public boolean validateRetentionForm(){
        boolean applyRetentionSelected=cashflow.getApplyRetention()!=null&&cashflow.getApplyRetention().booleanValue();

        return (applyRetentionSelected&&StringUtils.isNotEmpty(cashflow.getForm())&&StringUtils.isNotBlank(cashflow.getForm()))||!applyRetentionSelected;
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

    public String formattedPercentage(BigDecimal percentage){
        if(percentage==null){
            return "";
        }
        Double percentageN = percentage.doubleValue();
        Integer percentageI = percentageN.intValue();
        return percentageI.toString();
    }

    public List<PaymentTermsEnum> getPaymentTerms(){
        List<PaymentTermsEnum> paymentTerms = new ArrayList<>();
        for(PaymentTermsEnum s : PaymentTermsEnum.values()){
            paymentTerms.add(s);
        }
        return paymentTerms;
    }

    public List<RetentionFormEnum> getRetentionForms(){
        List<RetentionFormEnum> retentionForms = new ArrayList<>();
        for(RetentionFormEnum s : RetentionFormEnum.values()){
            retentionForms.add(s);
        }
        return retentionForms;
    }

    public Date calculatePaymentDate(CashflowDetailEntity detailEntity){
        if(cashflow.getPaymentTerms() != null && detailEntity.getClaimDate() != null){
            switch (cashflow.getPaymentTerms()){
                case NET_30 : detailEntity.setPaymentDate(Util.convertUTC(DateUtil.sumNDays(detailEntity.getClaimDate(), 30), configuration.getTimeZone()));
                    break;
                case NET_14: detailEntity.setPaymentDate(Util.convertUTC(DateUtil.sumNDays(detailEntity.getClaimDate(), 15), configuration.getTimeZone()));
                    break;
                case NET_7: detailEntity.setPaymentDate(Util.convertUTC(DateUtil.sumNDays(detailEntity.getClaimDate(),7),configuration.getTimeZone()));
                    break;
                default: detailEntity.setPaymentDate(Util.convertUTC(detailEntity.getClaimDate(),configuration.getTimeZone()));
                    break;
            }
        }else if(cashflow.getPaymentTerms() == null && detailEntity.getClaimDate() != null){
            detailEntity.setPaymentDate(Util.convertUTC(detailEntity.getClaimDate(),configuration.getTimeZone()));
        }
        return detailEntity.getPaymentDate();
    }

    public void calculatePaymentDateBasedPaymentTerms(){
        for(CashflowDetailEntity detailEntity : cashflowDetailList) {
            if (cashflow.getPaymentTerms() != null && detailEntity.getClaimDate() != null) {
                switch (cashflow.getPaymentTerms()) {
                    case NET_30:
                        detailEntity.setPaymentDate(Util.convertUTC(DateUtil.sumNDays(detailEntity.getClaimDate(), 30), configuration.getTimeZone()));
                        break;
                    case NET_14:
                        detailEntity.setPaymentDate(Util.convertUTC(DateUtil.sumNDays(detailEntity.getClaimDate(), 15), configuration.getTimeZone()));
                        break;
                    case NET_7:
                        detailEntity.setPaymentDate(Util.convertUTC(DateUtil.sumNDays(detailEntity.getClaimDate(), 7), configuration.getTimeZone()));
                        break;
                    default:
                        detailEntity.setPaymentDate(Util.convertUTC(detailEntity.getClaimDate(), configuration.getTimeZone()));
                        break;
                }
            } else if (cashflow.getPaymentTerms() == null && detailEntity.getClaimDate() != null) {
                detailEntity.setPaymentDate(Util.convertUTC(detailEntity.getClaimDate(), configuration.getTimeZone()));
            }
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
}
