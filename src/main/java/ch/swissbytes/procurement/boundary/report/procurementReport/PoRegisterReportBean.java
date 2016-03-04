package ch.swissbytes.procurement.boundary.report.procurementReport;


import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.procurement.report.ReportProcBean;
import ch.swissbytes.procurement.report.dtos.ProjectProcurementDto;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by christian on 12-06-2015
 */
@Named
@ViewScoped
public class PoRegisterReportBean implements Serializable {

    public static final Logger log = Logger.getLogger(PoRegisterReportBean.class.getName());

    @Inject
    private PurchaseOrderService poService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ReportProcBean reportProcBean;

    private List<ProjectEntity> projectList;

    private List<ProjectProcurementDto> ppdtos;

    private ProjectEntity selectedProject;

    private boolean filterAllPOsAndContract=true;
    private boolean filterPOsOnly;
    private boolean filterContractsOnly;

    private boolean sortByPoNo;
    private boolean sortBySupplier;
    private boolean sortTitle;
    private String reportName;
    private String reportTitle;
    private String posRegisterReport = "POs Register Report";

    @PostConstruct
    public void create() {
        log.info("creating reportDeliverableBean");
        ppdtos = new ArrayList<>();
        projectList = new ArrayList<>();
        loadProjects();
        reportTitle = posRegisterReport;
    }

    @PreDestroy
    public void destroy() {
        log.info("destroyed reportDeliverableBean");
    }

    public void loadProjects() {
        log.info("loading projects");
        projectList = projectService.findAllProjects();
    }

    public void printProjectProcurementReport() {
        log.info("print project procurement");
        Map<String, Boolean> sortMap = new HashMap<>();
        sortMap.put("poNo", sortByPoNo);
        sortMap.put("supplier", sortBySupplier);
        sortMap.put("title",sortTitle);

        Map<String,Boolean> filterMap = new HashMap<>();
        filterMap.put("allPos",filterAllPOsAndContract);
        filterMap.put("contractOnly",filterContractsOnly);
        filterMap.put("posOnly",filterPOsOnly);

        if (selectedProject != null) {
            reportProcBean.printPosRegister(selectedProject, sortMap,filterMap);
        } else {
            Messages.addFlashGlobalError("Select a project first");
        }
    }

    public String backToReports() {
        log.info("back to reports");
        return "report?faces-redirect=true";
    }

    public void resetOptionPrint(Integer option){
        switch (option){
            case 1://sortPO
                sortBySupplier = false;
                sortTitle = false;
                break;
            case 2://sortSupplier
                sortByPoNo = false;
                sortTitle = false;
                break;
            case 3://sortTitle
                sortByPoNo = false;
                sortBySupplier = false;
                break;
        }
    }

    public void resetFilterPrint(Integer option){
        switch (option){
            case 1://filterAllPOs
                filterContractsOnly = false;
                filterPOsOnly = false;
                break;
            case 2://filterContractOnly
                filterAllPOsAndContract = false;
                filterPOsOnly = false;
                break;
            case 3://filterPosOnly
                filterAllPOsAndContract = false;
                filterContractsOnly = false;
                break;
        }
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public ProjectEntity getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(ProjectEntity selectedProject) {
        this.selectedProject = selectedProject;
    }

    public Boolean getSortByPoNo() {
        return sortByPoNo;
    }

    public void setSortByPoNo(Boolean sortByPoNo) {
        this.sortByPoNo = sortByPoNo;
    }

    public Boolean getSortBySupplier() {
        return sortBySupplier;
    }

    public void setSortBySupplier(Boolean sortBySupplier) {
        this.sortBySupplier = sortBySupplier;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public boolean isSortTitle() {
        return sortTitle;
    }

    public void setSortTitle(boolean sortTitle) {
        this.sortTitle = sortTitle;
    }

    public boolean isFilterAllPOsAndContract() {
        return filterAllPOsAndContract;
    }

    public void setFilterAllPOsAndContract(boolean filterAllPOsAndContract) {
        this.filterAllPOsAndContract = filterAllPOsAndContract;
    }

    public boolean isFilterPOsOnly() {
        return filterPOsOnly;
    }

    public void setFilterPOsOnly(boolean filterPOsOnly) {
        this.filterPOsOnly = filterPOsOnly;
    }

    public boolean isFilterContractsOnly() {
        return filterContractsOnly;
    }

    public void setFilterContractsOnly(boolean filterContractsOnly) {
        this.filterContractsOnly = filterContractsOnly;
    }
}
