package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/18/2015.
 */
@Named
@ViewScoped
public class SupplierProcBean implements Serializable {

    private static final Logger log = Logger.getLogger(SupplierProcBean.class.getName());

    private SupplierProcEntity supplier = new SupplierProcEntity();

    @Inject
    private SupplierProcService service;

    @Inject
    private CategoryBrandBean categoryBrandBean;

    @Inject
    private ContactBean contactBean;

    private String supplierId;

    private boolean addingCategory = false;
    private boolean addingBrand = false;
    private boolean editing =false;
    private boolean addingContact=false;

    private Long temporaryId=-1L;

    @PostConstruct
    public void create() {
        log.info("SupplierProcBean bean created");
        //  supplier.setStatus(StatusEnum.ENABLE);

    }

    public void load() {
        Long id = null;
        if (supplierId != null) {
            try {
                id = Long.parseLong(supplierId);
            } catch (NumberFormatException nfe) {
            }
        }
        ContactEntity emptyContact=new ContactEntity();
        emptyContact.setWithNoData(true);
        if (id != null && id > 0) {
            supplier = service.findById(id);
            editing =true;
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }

        supplier.getBrands().addAll(service.getBrands(id));
        supplier.getCategories().addAll(service.getCategories(id));
        supplier.getContacts().addAll(service.getContacts(id));
        supplier.getContacts().add(emptyContact);
    }


    public String doSave() {
        if(service.isAlreadyBeingUsed(supplier.getSupplierId(),supplier.getId())){
            Messages.addFlashError("supplierID","supplier id is already being used");
            return "";
        }
        service.save(supplier);
        return "list?faces-redirect=true";
    }

    public String doUpdate() {
        if(service.isAlreadyBeingUsed(supplier.getSupplierId(),supplier.getId())){
            Messages.addFlashError("supplierID","supplier id is already being used");
            return "";
        }
        service.update(supplier);
        return "list?faces-redirect=true";
    }

    public String doDelete() {
        service.doDelete(supplier);
        return "list?faces-redirect=true";
    }

    public void putModeCategory() {
        addingCategory = true;
        categoryBrandBean.addlistLoaded(supplier.getCategories(), supplier.getBrands());
    }

    public void saveContact(){
        contactBean.saveContact();
        supplier.getContacts().clear();
        supplier.getContacts().addAll(contactBean.getContacts());
        putModeSupplier();
    }

    public boolean isSupplierMode() {
        return !addingBrand && !addingCategory;
    }

    public void putModeBrand() {
        addingBrand = true;
        categoryBrandBean.addlistLoaded(supplier.getCategories(), supplier.getBrands());
    }

    public void putModeContact(Long id){
       // System.out.println("mode contact.......")
        contactBean.getContacts().clear();
        contactBean.getContacts().addAll(supplier.getContacts());
        if(id==null||id==0){
            contactBean.putModeCreation();
            contactBean.setCurrentId(temporaryId--);
        }else{
            contactBean.putModeEdition();
            contactBean.setCurrentId(id);
        }
        addingContact=true;
    }

    public void putModeSupplier() {
        addingCategory = false;
        addingBrand = false;
        addingContact=false;
    }

    public void addCategoryBrand() {
        if (addingCategory) {
            supplier.getCategories().clear();
            supplier.getCategories().addAll(categoryBrandBean.getCategories().getTarget());
        }
        if (addingBrand) {
            supplier.getBrands().clear();
            supplier.getBrands().addAll(categoryBrandBean.getBrands().getTarget());
        }
        addingCategory = addingBrand = false;
    }





    @PreDestroy
    public void destroy() {
        log.info("SupplierProcBean bean destroyed");
    }

    public SupplierProcEntity getSupplier() {
        return supplier;
    }

    public boolean isAddingCategory() {
        return addingCategory;
    }


    public boolean isAddingBrand() {
        return addingBrand;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public boolean isEditing() {
        return editing;
    }

    public boolean isAddingContact() {
        return addingContact;
    }

    public void setAddingContact(boolean addingContact) {
        this.addingContact = addingContact;
    }
}
