package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.procurement.boundary.Bean;
import ch.swissbytes.procurement.boundary.BeanEditableList;
import org.apache.commons.beanutils.BeanUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 30-05-15.
 */
@Named
@ViewScoped
public class ContactBean extends BeanEditableList<ContactEntity> {

    private static final Logger log = Logger.getLogger(ContactBean.class.getName());

    public void add(){
        super.add(new ContactEntity());
        log.info("elements "+this.list.size());
    }
    protected boolean canEdit(){
        return rowsBeingEdited()==0;
    }

}
