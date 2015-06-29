package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
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

    private String supplierId;

    private boolean addingCategory = false;
    private boolean addingBrand = false;
    private boolean editing = false;
    private String mode;


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
        supplier = new SupplierProcEntity();
        putModeSupplier();
    }


    public String doSave() {
        if (!validate()) {
            return "";
        }
        supplier.getContacts().clear();
        supplier.getContacts().addAll(contactBean.getList());
        service.save(supplier);
        return "list?faces-redirect=true";
    }

    public SupplierProcEntity save() {
        if (!validate()) {
            return null;
        }
        supplier.getContacts().clear();
        supplier.getContacts().addAll(contactBean.getList());
        return service.save(supplier);

    }

    public String doUpdate() {
        if (!validate()) {
            return "";
        }
        supplier.getContacts().clear();
        supplier.getContacts().addAll(contactBean.getList());
        service.update(supplier);
        return "list?faces-redirect=true";
    }

    private boolean validate() {
        boolean validated = true;
        if (service.isAlreadyBeingUsed(supplier.getSupplierId(), supplier.getId())) {
            Messages.addFlashError("supplierID", "supplier id is already being used");
            validated = false;
        }
        if (!hasEitherAtLeastOneCategoryOrOneBrand()) {
            Messages.addGlobalError("You must select either a category or a brand");
            validated = false;
        }
        return validated;
    }

    private boolean hasEitherAtLeastOneCategoryOrOneBrand() {
        return (categoryBrandBean.getBrands()!=null?categoryBrandBean.getBrands().getTarget().size():0) + (categoryBrandBean.getCategories()!=null?categoryBrandBean.getCategories().getTarget().size():0) >= 1;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
