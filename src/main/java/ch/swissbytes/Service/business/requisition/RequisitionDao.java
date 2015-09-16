package ch.swissbytes.Service.business.requisition;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.PurchaseOrderProcurementEntity;
import ch.swissbytes.domain.model.entities.RequisitionEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 04-06-15.
 */
public class RequisitionDao extends GenericDao<RequisitionEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(RequisitionDao.class.getName());

    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }
    public void doSave(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity,List<RequisitionEntity>requisitions){
        Date now=new Date();
        for(RequisitionEntity requisitionEntity:requisitions){
            requisitionEntity.setId(null);
            requisitionEntity.setStatusEnum(StatusEnum.ENABLE);
            requisitionEntity.setLastUpdate(now);
            requisitionEntity.setPurchaseOrderProcurementEntity(purchaseOrderProcurementEntity);
            save(requisitionEntity);
        }
    }
    public void doUpdate(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity,List<RequisitionEntity>requisitions){
        Date now=new Date();
        for(RequisitionEntity requisitionEntity:requisitions){
            requisitionEntity.setLastUpdate(now);
            requisitionEntity.setPurchaseOrderProcurementEntity(purchaseOrderProcurementEntity);
            if(requisitionEntity.getId()<0){
                requisitionEntity.setId(null);
                requisitionEntity.setStatusEnum(StatusEnum.ENABLE);
                save(requisitionEntity);
            }else{
                update(requisitionEntity);
            }
        }

    }
    public List<RequisitionEntity> findRequisitionByPurchaseOrder(Long id){

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT r ");
        sb.append(" FROM RequisitionEntity r ");
        sb.append(" WHERE r.statusEnum = :ENABLE ");
        sb.append(" AND r.purchaseOrderProcurementEntity.id=:ID");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("ID",id!=null?id:id);
        return super.findBy(sb.toString(),params);
    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }
}
