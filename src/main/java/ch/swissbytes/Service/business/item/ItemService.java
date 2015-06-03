package ch.swissbytes.Service.business.item;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.currency.CurrencyDao;
import ch.swissbytes.Service.business.poitem.PoItemService;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.model.entities.PoItemEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
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
    private PoItemService poItemService;

    public void doSave(ItemEntity entity){
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        dao.doSave(entity);
    }

    public void doSave(List<ItemEntity> itemList, POEntity po) {
        log.info("saving Item and PoItem");
        for(ItemEntity entity : itemList){
            entity.setId(null);
            entity.setLastUpdate(new Date());
            entity.setStatus(StatusEnum.ENABLE);
            dao.doSave(entity);
            PoItemEntity poItemEntity = new PoItemEntity();
            poItemEntity.setItem(entity);
            poItemEntity.setPo(po);
            poItemService.doSave(poItemEntity);
        }
    }

    public void doUpdate(ItemEntity entity){
        entity.setLastUpdate(new Date());
        dao.doUpdate(entity);
    }

    public void delete(ItemEntity entity) {
        entity.setStatus(StatusEnum.DELETED);
        entity.setLastUpdate(new Date());
        dao.update(entity);
    }

    public ItemEntity findById(Long id) {
        List<ItemEntity> list = dao.findById(ItemEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<ItemEntity> findAll() {
        return dao.findAll();
    }
}
