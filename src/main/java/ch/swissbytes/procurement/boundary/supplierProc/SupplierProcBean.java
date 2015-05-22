package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;

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

    private List<CategoryEntity> categories;
    private List<BrandEntity> brands;

    private String supplierId;

    private boolean addingCategory = false;
    private boolean addingBrand = false;
    private boolean editing =false;

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
        categories = service.getCategories(id);
        brands = service.getBrands(id);
        if (id != null && id > 0) {
            supplier = service.findById(id);
            editing =true;
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }
    }


    public String doSave() {
        collectData();
        service.save(supplier);
        return "list?faces-redirect=true";
    }

    public String doUpdate() {
        collectData();
        service.update(supplier);
        return "list?faces-redirect=true";
    }

    public String doDelete() {
        service.doDelete(supplier);
        return "list?faces-redirect=true";
    }

    private void collectData() {
        supplier.getCategories().clear();
        supplier.getCategories().addAll(categories);
        supplier.getBrands().clear();
        supplier.getBrands().addAll(brands);
    }

    public void putModeCategory() {
        addingCategory = true;
        categoryBrandBean.addlistLoaded(categories, brands);

    }

    public boolean isSupplierMode() {
        return !addingBrand && !addingCategory;
    }

    public void putModeBrand() {
        addingBrand = true;
        categoryBrandBean.addlistLoaded(categories, brands);
    }

    public void putModeSupplier() {
        addingCategory = false;
        addingBrand = false;
    }

    public void addCategoryBrand() {
        if (addingCategory) {
            categories.clear();
            categories.addAll(categoryBrandBean.getCategories().getTarget());
        }
        if (addingBrand) {
            brands.clear();
            brands.addAll(categoryBrandBean.getBrands().getTarget());
        }
        addingCategory = addingBrand = false;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public List<BrandEntity> getBrands() {
        return brands;
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
}
