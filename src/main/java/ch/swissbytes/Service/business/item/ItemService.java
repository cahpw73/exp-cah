package ch.swissbytes.Service.business.item;


import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.types.IncoTermsEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class ItemService  implements Serializable {

    private static final Logger log = Logger.getLogger(ItemService.class.getName());

    @Inject
    private ItemDao dao;

    @Inject
    private EnumService enumService;
    @Inject
    private ScopeSupplyDao scopeSupplyDao;


    public void doSave(ScopeSupplyEntity entity){
        entity.setStatus(enumService.getStatusEnumEnable());
        entity.setLastUpdate(new Date());
        dao.saveAndFlush(entity);
    }

    public void doSave(List<ItemEntity> supplyList, PurchaseOrderEntity po,String incoTerms,String fullIncoTerms) {
        for(ItemEntity entity : supplyList){
            entity.setId(null);
            entity.setLastUpdate(new Date());
            entity.setPurchaseOrder(po);
            entity.setStatus(enumService.getStatusEnumEnable());
            entity.setIsForecastSiteDateManual(false);
            entity.setTotalCost(entity.getCost() != null && entity.getQuantity() != null ? entity.getCost().multiply(entity.getQuantity()) : new BigDecimal("0"));
            if(exitsDeliveryPointInIncoTerms(incoTerms)){
                entity.setSpIncoTerm(incoTerms);
                entity.setSpIncoTermDescription(fullIncoTerms);
            }else{
                entity.setSpIncoTerm(null);
                entity.setSpIncoTermDescription(null);
            }
            //@TODO Review Date_SS  estamos poniendo este valor por defecto con la fecha del dia, este campo aparentemente esta siendo usado en attachment modulo expediting
            entity.setDate(new Date());
            dao.doSave(entity);
            scopeSupplyDao.createScopeSupplyFrom(entity);
        }
    }

    public void doUpdate(List<ItemEntity> supplyList, PurchaseOrderEntity po) {
        boolean newOne=false;
        for (ItemEntity entity : supplyList){
            newOne=false;
            if(entity!=null) {
                entity.setTotalCost(entity.getCost()!=null&&entity.getQuantity()!=null?entity.getCost().multiply(entity.getQuantity()):new BigDecimal("0"));
                entity.setLastUpdate(new Date());
                if (entity.getId()==null||entity.getId() < 0L) {
                    entity.setId(null);
                    newOne=true;
                    entity.setStatus(enumService.getStatusEnumEnable());
                    entity.setPurchaseOrder(po);
                    entity.setIsForecastSiteDateManual(false);
                    //@TODO Review Date_SS  estamos poniendo este valor por defecto con la fecha del dia, este campo aparentemente esta siendo usado en attachment modulo expediting
                    entity.setDate(new Date());
                    dao.doSave(entity);
                }else {
                    dao.doUpdate(entity);
                }
                if(newOne){
                    scopeSupplyDao.createScopeSupplyFrom(entity);
                }else {
                    scopeSupplyDao.updateScopeSupplyFrom(entity);
                }
            }
        }
    }

    @Transactional
    public void doUpdate(ItemEntity entity){
        entity.setLastUpdate(new Date());
        dao.doUpdate(entity);
    }

    public void delete(ItemEntity entity) {
        entity.setStatusEnum(StatusEnum.DELETED);
        entity.setLastUpdate(new Date());
        dao.update(entity);
    }

    public ItemEntity findById(final Long id){
        List<ItemEntity> list = dao.findById(ItemEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<ItemEntity> findByPoId(Long poEntityId) {
        return dao.findByPoId(poEntityId);
    }

    public boolean exitsDeliveryPointInIncoTerms(String point){
        for(IncoTermsEnum i : IncoTermsEnum.values()){
            if(i.name().equalsIgnoreCase(point)){
                return true;
            }
        }
        return  false;
    }

}
