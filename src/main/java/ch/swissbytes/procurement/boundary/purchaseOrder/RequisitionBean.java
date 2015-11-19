package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.model.entities.RequisitionEntity;
import ch.swissbytes.procurement.boundary.BeanEditableList;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

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
    protected boolean canEdit(){
        return rowsBeingEdited()==0;
    }


    public void confirm(RequisitionEntity entity) {
        if(StringUtils.isEmpty(entity.getrTFNo()) && StringUtils.isEmpty(entity.getOriginator())){
            Messages.addFlashGlobalError("Must be added either a RTF or an Originator");
        }else{
            RecordEditable record = find(entity.getId());
            if (record != null) {
                if (validate(record)) {
                    record.stopEditing();
                }
            }
        }
    }
}
