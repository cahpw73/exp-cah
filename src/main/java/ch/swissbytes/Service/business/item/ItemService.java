package ch.swissbytes.Service.business.item;


import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
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


    public void doSave(ScopeSupplyEntity entity){
        entity.setStatus(enumService.getStatusEnumEnable());
        entity.setLastUpdate(new Date());
        dao.saveAndFlush(entity);
    }

    public void doSave(List<ScopeSupplyEntity> supplyList, PurchaseOrderEntity po) {
        for(ScopeSupplyEntity entity : supplyList){
            entity.setId(null);
            entity.setLastUpdate(new Date());
            entity.setPurchaseOrder(po);
            entity.setStatus(enumService.getStatusEnumEnable());
            entity.setIsForecastSiteDateManual(false);
            //@TODO Review Date_SS  estamos poniendo este valor por defecto con la fecha del dia, este campo aparentemente esta siendo usado en attachment modulo expediting
            entity.setDate(new Date());
            dao.doSave(entity);
        }
    }

    public void doUpdate(List<ScopeSupplyEntity> supplyList, PurchaseOrderEntity po) {
        for (ScopeSupplyEntity entity : supplyList){
            if(entity!=null) {
                if (entity.getId()==null||entity.getId() < 0L) {
                    entity.setId(null);
                    entity.setStatus(enumService.getStatusEnumEnable());
                    entity.setPurchaseOrder(po);
                    entity.setIsForecastSiteDateManual(false);
                    //@TODO Review Date_SS  estamos poniendo este valor por defecto con la fecha del dia, este campo aparentemente esta siendo usado en attachment modulo expediting
                    entity.setDate(new Date());
                }
                entity.setLastUpdate(new Date());
                dao.doUpdate(entity);
            }
        }
    }

    @Transactional
    public void doUpdate(ScopeSupplyEntity entity){
        entity.setLastUpdate(new Date());
        dao.doUpdate(entity);
    }

    public void delete(ScopeSupplyEntity entity) {
        entity.setStatusEnum(StatusEnum.DELETED);
        entity.setLastUpdate(new Date());
        dao.update(entity);
    }

    public ScopeSupplyEntity findById(final Long id){
        List<ScopeSupplyEntity> list = dao.findById(ScopeSupplyEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<ScopeSupplyEntity> findByPoId(Long poEntityId) {
        return dao.findByPoId(poEntityId);
    }

}
