package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/18/2015.
 */
@Named
@ViewScoped
public class SupplierProcBean implements Serializable {

    private static final Logger log = Logger.getLogger(SupplierProcBean.class.getName());

    private SupplierProcEntity supplier=new SupplierProcEntity();

    @Inject
    private SupplierProcService service;

    private String supplierId;

    private boolean addingCategory =false;
    private boolean addingBrand =false;

    @PostConstruct
    public void create(){
        log.info("SupplierProcBean bean created");

        supplier.setStatus(StatusEnum.ENABLE);
    }
    public void load(){
        //supplier=service.findById(//supplierId);
    }

    public String doSave(){
        supplier.setLastUpdate(new Date());
        service.doSave(supplier);
        return "list?faces-redirect=true";
    }
    public String doUpdate(){
        supplier.setLastUpdate(new Date());
        service.doUpdate(supplier);
        return "";
    }
    public String doDelete(){
        supplier.setLastUpdate(new Date());
        return "";
    }

    public void putModeCategory(){
        addingCategory =true;
    }

    public boolean isSupplierMode(){
        return !addingBrand&&!addingCategory;
    }
    public void putModeBrand(){
        addingBrand =true;
    }
    public void putModeSupplier(){
        addingCategory =false;
        addingBrand =false;
    }



    @PreDestroy
    public void destroy(){
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
}
