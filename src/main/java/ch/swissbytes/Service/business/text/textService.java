package ch.swissbytes.Service.business.text;


import ch.swissbytes.Service.business.item.ItemDao;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.model.entities.TextEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class textService implements Serializable {

    private static final Logger log = Logger.getLogger(textService.class.getName());

    @Inject
    private textDao dao;

    public void doSave(TextEntity entity){
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        dao.doSave(entity);
    }

    public void doSave(List<TextEntity> itemList) {
        log.info("saving Item");

    }

    public void doUpdate(List<ItemEntity> itemList, POEntity po) {
        log.info("updating Item");
        log.info("ItemList size: " + itemList.size());

    }

    public void doUpdate(ItemEntity entity){

    }

    public void delete(ItemEntity entity) {

    }

    public ItemEntity findById(Long id) {
        List<ItemEntity> list = dao.findById(ItemEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<ItemEntity> findAll() {
        return null;
    }

    public List<ItemEntity> findByPoId(Long poEntityId) {
        return null;
    }

}
