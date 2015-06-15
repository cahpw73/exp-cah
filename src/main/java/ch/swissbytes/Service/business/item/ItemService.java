package ch.swissbytes.Service.business.item;


import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
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

    public void doSave(ItemEntity entity){
        entity.setLastUpdate(new Date());
        entity.setStatusEnum(StatusEnum.ENABLE);
        dao.doSave(entity);
    }

    public void doSave(List<ItemEntity> itemList, POEntity po) {
        for(ItemEntity entity : itemList){
            entity.setId(null);
            entity.setLastUpdate(new Date());
            entity.setStatusEnum(StatusEnum.ENABLE);
            entity.setPo(po);
            dao.doSave(entity);
        }
    }

    public void doUpdate(List<ItemEntity> itemList, POEntity po) {
        for(ItemEntity entity : itemList){
            if(entity.getId() < 0L) {
                entity.setId(null);
                entity.setStatusEnum(StatusEnum.ENABLE);
                entity.setPo(po);
            }
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);
        }
    }

    public void doUpdate(ItemEntity entity){
        entity.setLastUpdate(new Date());
        dao.doUpdate(entity);
    }

    public void delete(ItemEntity entity) {
        entity.setStatusEnum(StatusEnum.DELETED);
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

    public List<ItemEntity> findByPoId(Long poEntityId) {
        return dao.findByPoId(poEntityId);
    }

}
