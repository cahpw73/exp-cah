package ch.swissbytes.Service.business.poitem;


import ch.swissbytes.Service.business.item.ItemDao;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.PoItemEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class PoItemService implements Serializable {

    private static final Logger log = Logger.getLogger(PoItemService.class.getName());

    @Inject
    private PoItemDao dao;

    public void doSave(PoItemEntity entity){
        dao.doSave(entity);
    }

    public void doUpdate(PoItemEntity entity){
        dao.doUpdate(entity);
    }

    public void delete(PoItemEntity entity) {
        dao.update(entity);
    }

    public PoItemEntity findById(Long id) {
        List<PoItemEntity> list = dao.findById(PoItemEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<PoItemEntity> findAll() {
        return dao.findAll();
    }
}
