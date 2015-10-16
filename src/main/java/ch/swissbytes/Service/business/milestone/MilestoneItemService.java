package ch.swissbytes.Service.business.milestone;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.DeliverableItem;
import ch.swissbytes.domain.model.entities.MilestoneItem;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/11/2015.
 */
public class MilestoneItemService extends Service<MilestoneItem> implements Serializable {


    private static final Logger log = Logger.getLogger(MilestoneItemService.class.getName());

    @Inject
    private MilestoneItemDao dao;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    public List<DeliverableItem> findAllDeliverableItems(){
        return dao.findAllMilestoneItems();
    }
}
