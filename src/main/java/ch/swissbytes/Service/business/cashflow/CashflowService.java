package ch.swissbytes.Service.business.cashflow;


import ch.swissbytes.domain.model.entities.CashflowDetailEntity;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderProcurementEntity;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class CashflowService implements Serializable {

    private static final Logger log = Logger.getLogger(CashflowService.class.getName());

    @Inject
    private CashflowDao dao;

    @Inject
    private CashflowDetailDao cashflowDetailDao;



    public void doSave(CashflowEntity entity, PurchaseOrderProcurementEntity po){
        log.info("do save cashflow");
        if(entity != null) {
            CashflowEntity cashflow = new CashflowEntity();
            cashflow.setStatusEnum(StatusEnum.ENABLE);
            cashflow.setLastUpdate(new Date());
            cashflow.setPo(po);
            cashflow.setPaymentTerms(entity.getPaymentTerms());
            dao.doSave(cashflow);
        }
    }

    public void doUpdate(CashflowEntity entity, PurchaseOrderProcurementEntity po){
        entity.setLastUpdate(new Date());
        if(entity.getId()!=null){
            dao.doUpdate(entity);
        }else{
            entity.setStatusEnum(StatusEnum.ENABLE);
            entity.setPo(po);
            dao.doSave(entity);
        }
        for(CashflowDetailEntity cf : entity.getCashflowDetailList()){
            if(cf.getId() < 0L) {
                cf.setId(null);
                cf.setCashflowEntity(entity);
                cf.setStatus(StatusEnum.ENABLE);
            }
            cf.setLastUpdate(new Date());
            cashflowDetailDao.doUpdate(cf);
        }
    }

    public void delete(CashflowEntity entity) {
        entity.setStatusEnum(StatusEnum.DELETED);
        entity.setLastUpdate(new Date());
        dao.update(entity);
    }

    public CashflowEntity findById(Long id) {
        List<CashflowEntity> list = dao.findById(CashflowEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<CashflowEntity> findAll() {
        return dao.findAll();
    }

    public List<CashflowEntity> findByPoId(Long poEntityId) {
        return dao.findByPoId(poEntityId);
    }

    public List<CashflowDetailEntity> findDetailByCashflowId(final Long cashflowId){
        return cashflowDetailDao.findByCashflowId(cashflowId);
    }

    public BigDecimal getTotalMilestonePayments(List<CashflowDetailEntity> detail,ProjectCurrencyEntity currency){
        BigDecimal total=new BigDecimal("0");
        for(CashflowDetailEntity milestonePayment:detail){
            if(milestonePayment.getProjectCurrency()!=null&&currency!=null&&currency.getCurrency().getId().longValue()==milestonePayment.getProjectCurrency().getCurrency().getId().longValue())
            total=total.add(milestonePayment.getOrderAmt()!=null?milestonePayment.getOrderAmt():new BigDecimal("0"));
        }
        return total;
    }

}
