package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.ModeOperationEnum;
import ch.swissbytes.procurement.boundary.Bean;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/18/2015.
 */
@Named
@ViewScoped
public class SupplierProcBean extends Bean implements Serializable {

    private static final Logger log = Logger.getLogger(SupplierProcBean.class.getName());

    private SupplierProcEntity supplier = new SupplierProcEntity();

    @Inject
    private SupplierProcService service;

    @Inject
    private CategoryBrandBean categoryBrandBean;

    @Inject
    private ContactBean contactBean;

    @Inject
    private SupplierManagerTable managerTable;

    private String supplierId;

    private boolean addingCategory = false;

    private boolean addingBrand = false;

    private boolean editing = false;

    private String mode;

    private String criteria;


    @PostConstruct
    public void create() {
        log.info("SupplierProcBean bean created");
    }

    public void load() {
        Long id = null;
        putModeCreation();
        if (supplierId != null) {
            try {
                id = Long.parseLong(supplierId);
            } catch (NumberFormatException nfe) {
            }
        }
        if (id != null && id > 0) {
            supplier = service.findById(id);
            editing = true;
            putModeEdition();
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }
        supplier.getBrands().addAll(service.getBrands(id));
        supplier.getCategories().addAll(service.getCategories(id));
        supplier.getContacts().addAll(service.getContacts(id));
        contactBean.getList().clear();
        contactBean.getList().addAll(supplier.getContacts());
        if (StringUtils.isNotBlank(mode) && StringUtils.isNotBlank(mode) && mode.equalsIgnoreCase(ModeOperationEnum.VIEW.name())) {
            putModeView();
        }
    }

    public void start() {
        log.info("START PROCUREMENT BEAN");
        supplier = new SupplierProcEntity();
        putModeSupplier();
      //  categoryBrandBean.restart();
        contactBean.reset();
    }


    public String doSave() {
        if (!validate()) {
            return "";
        }
        supplier.getContacts().clear();
        supplier.getContacts().addAll(contactBean.getList());
        service.save(supplier);
        Messages.addFlashGlobalInfo("The supplier has been saved!");
        managerTable.rememberPage();
        return "list?faces-redirect=true";
    }

    public SupplierProcEntity save() {
        if(contactBean.filteredList().size()<=0){
            Messages.addGlobalError("You must add at least one contact");
            return null;
        }
        if (!validate()) {
            return null;
        }
        supplier.getContacts().clear();
        supplier.getContacts().addAll(contactBean.getList());
        return service.save(supplier);

    }

    public String doUpdate() {
        supplier.getContacts().clear();
        System.out.println("current page" + managerTable.getCurrentPage());
        supplier.getContacts().addAll(contactBean.getList());
        if (!validate()) {
            return "";
        }
        service.update(supplier);
        Messages.addFlashGlobalInfo("The supplier has been saved!");
        managerTable.setLastEdited(supplier.getId());
        managerTable.rememberPage();
        return "list?faces-redirect=true";
    }

    private boolean validate() {
        boolean validated = true;
        if (service.isAlreadyBeingUsed(supplier.getSupplierId(), supplier.getId())) {
            Messages.addFlashError("supplierID1", "Supplier ID is already being used");
            validated = false;
        }
        if (!hasEitherAtLeastOneCategory()) {
            Messages.addGlobalError("You must select at least one category");
            validated = false;
        }
        return validated;
    }

    private boolean validateContacts() {
        boolean validated = true;
        String regexp = "\\s*$|^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?";
        for (ContactEntity contact : supplier.getContacts()) {
            if(StringUtils.isNotEmpty(contact.getEmail())&&StringUtils.isNotBlank(contact.getEmail())&&!regexp.matches(contact.getEmail())) {
                validated=false;
            }
        }
        return validated;
    }

    private boolean hasEitherAtLeastOneCategory() {
        return supplier.getCategories().size() + (categoryBrandBean.getCategories() != null ? categoryBrandBean.getCategories().getTarget().size() : 0) >= 1;
    }

    public String doDelete(Long id) {
        supplier = service.findById(id);
        service.doDelete(supplier);
        return "list?faces-redirect=true";
    }

    public void putModeCategory() {
        addingCategory = true;
        categoryBrandBean.addListLoaded(supplier.getCategories(), supplier.getBrands());
    }

    public boolean isSupplierMode() {
        return !addingBrand && !addingCategory;
    }

    public void putModeBrand() {
        addingBrand = true;
        categoryBrandBean.addListLoaded(supplier.getCategories(), supplier.getBrands());
    }

    public void putModeSupplier() {
        addingCategory = false;
        addingBrand = false;
    }

    public void addCategoryBrand() {
        if (addingCategory) {
            List<CategoryEntity> list =  new ArrayList<>();
            list.addAll(categoryBrandBean.getCategories().getTarget());
            supplier.getCategories().clear();
            supplier.getCategories().addAll(list);
        }
        if (addingBrand) {
            supplier.getBrands().clear();
            supplier.getBrands().addAll(categoryBrandBean.getBrands().getTarget());
        }
        addingCategory = addingBrand = false;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
