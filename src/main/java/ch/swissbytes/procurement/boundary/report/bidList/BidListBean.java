package ch.swissbytes.procurement.boundary.report.bidList;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.procurement.report.ReportProcBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 6/19/2015.
 */
@Named
@ViewScoped
public class BidListBean implements Serializable {

    public static final Logger log = Logger.getLogger(BidListBean.class.getName());


    @Inject
    private SupplierProcService supplierService;
    @Inject
    private ReportProcBean reportProcBean;

    private ProjectEntity project;
    private List<String> countries;
    private List<String> countriesSelected;
    private boolean categorySelection = true;
    private String packageNo;
    private String description;
    private String comments;
    private List<Long>supplierSelected;

    @PostConstruct
    public void create() {
        log.log(Level.FINE, "creating bidListBean");
        countries = new ArrayList<>();
        countriesSelected = new ArrayList<>();
        supplierSelected=new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.log(Level.FINE, "destroying bidListBean");
    }

    public ProjectEntity getProject() {
        return project;
    }

    public List<String> findCountriesByCategory(CategoryEntity categoryEntity) {
        if (categoryEntity != null) {
            countries = supplierService.findCountriesByCategory(categoryEntity.getId());
        }
        Collections.sort(countriesSelected);
        return countries;
    }

    public void selectCountry(String country) {
        if (!countriesSelected.contains(country)) {
            countriesSelected.add(country);
        } else {
            countriesSelected.remove(country);
        }
    }

    public void generateReport(){
        packageNo = packageNo.toUpperCase();
        description = description.toUpperCase();
        reportProcBean.printBidderList(supplierSelected,packageNo,description,comments,project);
    }

    public void selectSupplier(Long idSupplier){
        if(supplierSelected.contains(idSupplier)){
            supplierSelected.remove(idSupplier);
        }else{
            supplierSelected.add(idSupplier);
        }
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public boolean isCategorySelection() {
        return categorySelection;
    }

    public void putModeCategory() {
        categorySelection = true;
    }

    public void putModeSupplier() {
        categorySelection = false;
    }

    public String getPackageNo() {
        return packageNo;
    }

    public void setPackageNo(String packageNo) {
        this.packageNo = packageNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<SupplierProcEntity> suppliers(CategoryEntity categoryEntity) {
        return supplierService.findSupplierByCountriesAndCategory(categoryEntity!=null?categoryEntity.getId():null,countriesSelected);
    }
}
