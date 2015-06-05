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
public class RequisitionBean extends BeanEditableList<RequisitionEntity> {

    public static final Logger log = Logger.getLogger(RequisitionBean.class.getName());

    public void add(){
        this.add(new RequisitionEntity());

    }

    protected void initialize(){
        log.info("my list size starting  " + list.size());
    }

    protected void ending(){
        log.info("my list size ending  "+list.size());
    }
    protected boolean canEdit(){
        return rowsBeingEdited()==0;
    }


}
