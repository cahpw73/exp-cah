package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.deliverable.DeliverableItemService;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.DeliverableItem;
import ch.swissbytes.domain.model.entities.RequisitionEntity;
import ch.swissbytes.procurement.boundary.BeanEditableList;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Alvaro 04/06/2015
 */

@Named
@ViewScoped
public class DeliverableBean extends BeanEditableList<DeliverableEntity> {

    public static final Logger log = Logger.getLogger(DeliverableBean.class.getName());

    @Inject
    private DeliverableItemService deliverableService;
    private List<DeliverableItem> deliverableItems;

    public void add(){
        super.add(new DeliverableEntity());
        log.info("elements "+this.list.size());
    }
    protected boolean canEdit(){
        return rowsBeingEdited()==0;
    }

    @PostConstruct
    public void create(){
        deliverableItems=deliverableService.findAllDeliverableItems();
    }


    public List<DeliverableItem> getDeliverableItems() {
        return deliverableItems;
    }
}
