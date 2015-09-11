package ch.swissbytes.Service.business.deliverable;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.DeliverableItem;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/11/2015.
 */
public class DeliverableItemService extends Service<DeliverableItem> implements Serializable {


    private static final Logger log = Logger.getLogger(DeliverableItemService.class.getName());

    @Inject
    private DeliverableItemDao dao;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    public List<DeliverableItem> findAllDeliverableItems(){
        return dao.findAllDeliverableItems();
    }
}
