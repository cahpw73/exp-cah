package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.domain.model.entities.RequisitionEntity;
import ch.swissbytes.procurement.boundary.BeanEditableList;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;
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
    protected boolean canEdit(){
        Integer row=rowsBeingEdited();
        return row==0;
    }

    public List<RequisitionEntity> getList(){
        return this.list;
    }
}
