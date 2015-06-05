package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.RequisitionEntity;
import ch.swissbytes.procurement.boundary.BeanEditableList;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.logging.Logger;


/**
 * Created by Alvaro 04/06/2015
 */

@Named
@ViewScoped
public class DeliverableBean extends BeanEditableList<DeliverableEntity> {

    public static final Logger log = Logger.getLogger(DeliverableBean.class.getName());

    protected void initialize(){
        log.info("my list size starting  " + list.size());
    }

    protected void ending(){
        log.info("my list size ending  "+list.size());
    }

    public void add(){
        super.add(new DeliverableEntity());
        log.info("elements "+this.list.size());
    }
    protected boolean canEdit(){
        return rowsBeingEdited()==0;
    }




}
