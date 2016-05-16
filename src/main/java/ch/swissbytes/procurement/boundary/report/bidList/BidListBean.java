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
import java.util.Date;
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
        log.info("generation bidder list report");
        log.info("SupplierProcList size: " + supplierProcList.size());
        supplierSelected.clear();
        packageNo = packageNo.toUpperCase();
        description = description.toUpperCase();
        for(SupplierProcEntity sp : supplierProcList) {
            if (sp.isCanPrint()){
                supplierSelected.add(sp.getId());
            }
        }
        log.info("supplierSelected size: "+ supplierSelected.size());
        doUpdateCommentsForEachSupplier();
        reportProcBean.printBidderList(supplierSelected, packageNo, description, project);
        resetCanPrint();
    }

    private void resetCanPrint(){
        for(SupplierProcEntity sp : supplierProcList) {
            sp.setCanPrint(false);
        }
    }

    private void doUpdateCommentsForEachSupplier(){
        log.info("doUpdateCommentsForEachSupplier");
        List<SupplierProcEntity> supplierList = new ArrayList<>();
        for (Long supId : supplierSelected){
            for(SupplierProcEntity sp : supplierProcList){
                if(sp.getId().longValue()==supId.longValue()){
                    supplierList.add(sp);
                }
            }
        }
        for (SupplierProcEntity sp : supplierList){
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
        Date start = new Date();
        boolean canAdd = true;
        if(!supplierSelected.isEmpty()) {
            for (Long id : supplierSelected) {
                if (id.longValue() == sp.getId().longValue()) {
                    supplierSelected.remove(sp.getId());
                    canAdd = false;
                    break;
                }
            }
        }
        if (canAdd) {
            supplierSelected.add(sp.getId());
        }
        Date end = new Date();
        log.info("process time= " + (end.getTime()-start.getTime())+" ms");
        log.info("supplierSelected size: " + supplierSelected.size());
    }

    public void supplierListClear(final CategoryEntity category) {
        supplierSelected.clear();
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
        return supplierProcList;
    }

}
