package ch.swissbytes.Service.business.item;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.currency.CurrencyDao;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
import ch.swissbytes.domain.model.entities.ItemEntity;
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

    public void doSave(ItemEntity entity){
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        dao.doSave(entity);
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
