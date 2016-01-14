package ch.swissbytes.procurement.boundary.report.bidList;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.procurement.report.ReportProcBean;
import org.apache.commons.lang.StringUtils;

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
    private List<String> countriesSelected;
    private boolean categorySelection = true;
    private String packageNo;
    private String description;
    private String supplierComments;
    private List<Long> supplierSelected;
    private boolean canPrintSupplier = false;
    private List<SupplierProcEntity> supplierProcList;

    @PostConstruct
    public void create() {
        log.log(Level.FINE, "creating bidListBean");
        supplierProcList = new ArrayList<>();
        countriesSelected = new ArrayList<>();
        supplierSelected = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.log(Level.FINE, "destroying bidListBean");
    }

    public void generateReport() {
        doUpdateCommentsForEachSupplier();
        packageNo = packageNo.toUpperCase();
        description = description.toUpperCase();
        reportProcBean.printBidderList(supplierSelected, packageNo, description, project);
    }

    private void doUpdateCommentsForEachSupplier(){
        log.info("doUpdateCommentsForEachSupplier");
        List<SupplierProcEntity> list = new ArrayList<>();
        for (Long supId : supplierSelected){
            for(SupplierProcEntity sp : supplierProcList){
                if(sp.getId().longValue()==supId){
                    list.add(sp);
                }
            }
        }
        supplierProcList.clear();
        supplierProcList.addAll(list);
        for (SupplierProcEntity sp : supplierProcList){
            StringBuilder comments = new StringBuilder();
            if(StringUtils.isNotEmpty(sp.getComments())) {
                comments.append(sp.getComments());
                comments.append("\n");
            }
            comments.append(supplierComments);
            sp.setComments(comments.toString());
            supplierService.doUpdateSupplierOnly(sp);
        }
    }

    public void selectSupplier(SupplierProcEntity sp) {
        log.info("selectSupplier: " + sp.getCompany());
        if (supplierSelected.contains(sp.getId())) {
            supplierSelected.remove(sp.getId());
        } else {
            supplierSelected.add(sp.getId());
        }
    }

    public void supplierListClear(final CategoryEntity category) {
        supplierSelected.clear();
        canPrintSupplier = false;
        if (category != null) {
            description = category.getName();
        }
    }

    public ProjectEntity getProject() {
        return project;
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

    public String getSupplierComments() {
        return supplierComments;
    }

    public void setSupplierComments(String supplierComments) {
        this.supplierComments = supplierComments;
    }

    public List<SupplierProcEntity> suppliers(CategoryEntity categoryEntity) {
        return supplierService.findSupplierByCountriesAndCategory(categoryEntity != null ? categoryEntity.getId() : null, countriesSelected);
    }

    public List<SupplierProcEntity> supplieres1(CategoryEntity category) {
        List<SupplierProcEntity> list = supplierService.findSupplierByProjectAndCategory(category != null ? category.getId() : null, project != null ? project.getId() : null);
        supplierProcList.clear();
        supplierProcList.addAll(list);
        return list;
    }

    public boolean isCanPrintSupplier() {
        return canPrintSupplier;
    }

    public void setCanPrintSupplier(boolean canPrintSupplier) {
        this.canPrintSupplier = canPrintSupplier;
    }
}
