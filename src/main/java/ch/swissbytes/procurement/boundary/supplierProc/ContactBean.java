package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.contact.ContactService;
import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.procurement.boundary.BeanEditableList;
import org.primefaces.context.RequestContext;

import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Created by alvaro on 30-05-15.
 */
@Named
@ViewScoped
public class ContactBean extends BeanEditableList<ContactEntity> {

    private static final Logger log = Logger.getLogger(ContactBean.class.getName());

    private ContactEntity contact;
    @Inject
    private ContactService service;
    @Inject
    private SupplierProcService supplierService;
    private Long idSupplier;


    public void add(){
        super.add(new ContactEntity());
        log.info("elements "+this.list.size());
    }
    protected boolean canEdit(){
        return rowsBeingEdited()==0;
    }

    public void start(Long idSupplier){
        this.idSupplier=idSupplier;
        contact=new ContactEntity();

    }

    public ContactEntity getContact() {
        return contact;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
    }

    public void doSave(){
        SupplierProcEntity supplier=supplierService.findById(idSupplier);
        if(supplier!=null&&validate()) {
            service.doSave(contact, supplier);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('contactModal').show();");
        }
    }

    private boolean validate(){
        return true;
    }

}
