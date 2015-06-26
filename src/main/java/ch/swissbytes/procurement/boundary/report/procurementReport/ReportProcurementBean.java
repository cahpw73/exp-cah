package ch.swissbytes.procurement.boundary.report.procurementReport;

import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;
import ch.swissbytes.procurement.report.ReportProcBean;
import ch.swissbytes.procurement.report.dtos.ProjectProcurementDto;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

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
public class ReportProcurementBean implements Serializable {

    public static final Logger log = Logger.getLogger(ReportProcurementBean.class.getName());

    @Inject
    private PurchaseOrderService poService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ReportProcBean reportProcBean;

    private List<ProjectEntity> projectList;

    private List<ProjectProcurementDto> ppdtos;

    private ProjectEntity selectedProject;

    private Boolean sortByPoNo;
    private Boolean sortByVarNo;
    private Boolean sortBySupplier;
    private Boolean sortByDeliveryDate;

    private String reportName;

    private String reportTitle;
    private String projectProcurementReport = "Project Procurement Report";
    private String requiredRetentionReport = "Required Retentions Report";

    @PostConstruct
    public void create() {
        log.info("creating reportDeliverableBean");
        ppdtos = new ArrayList<>();
        projectList = new ArrayList<>();
        loadProjects();
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
        sortMap.put("varNo", sortByVarNo);
        sortMap.put("supplier", sortBySupplier);
        sortMap.put("deliveryDate", sortByDeliveryDate);
        if (selectedProject != null){
            switch (reportName){
                case "ppr" : List<PurchaseOrderEntity> list = poService.findByProjectIdCustomizedSort(selectedProject.getId(), sortMap);
                             reportProcBean.printProjectPurchaseOrder(selectedProject,list,getDescriptionSort(sortMap));
                    break;
                case "rrr" : reportProcBean.printRequiredRetentions(selectedProject,sortMap);
                    break;
            }
        }else{
            Messages.addFlashGlobalError("Select a project first");
        }

    }

    public void resetValuesProjectProc(){
        sortByPoNo = false;
        sortByVarNo = false;
        sortBySupplier = false;
        sortByDeliveryDate = false;
        selectedProject = new ProjectEntity();
    }

    public String backToReports(){
        log.info("back to reports");
        return "report?faces-redirect=true";
    }

    public void loadNameReport(){
        switch (reportName){
            case "ppr" : reportTitle = projectProcurementReport;
                break;
            case "rrr" : reportTitle = requiredRetentionReport;
                break;
        }
        log.info("report title: " + reportTitle);
    }

    private String getDescriptionSort(Map<String,Boolean> sortMap){
        Boolean poNo = sortMap.get("poNo");
        Boolean varNo = sortMap.get("varNo");
        Boolean supplier = sortMap.get("supplier");
        Boolean deliveryDate = sortMap.get("deliveryDate");
        String strSort = "";
        if(poNo){
            strSort = strSort+"PO No,";
        }
        if(varNo){
            strSort = strSort+"Var No,";
        }
        if (supplier){
            strSort = strSort+"Supplier,";
        }
        if(deliveryDate){
            strSort = strSort+"Delivery Date,";
        }
        if(strSort.length()>1){
            strSort = strSort.substring(0,strSort.length() - 1);
        }
        return  strSort;
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

    public Boolean getSortByVarNo() {
        return sortByVarNo;
    }

    public void setSortByVarNo(Boolean sortByVarNo) {
        this.sortByVarNo = sortByVarNo;
    }

    public Boolean getSortBySupplier() {
        return sortBySupplier;
    }

    public void setSortBySupplier(Boolean sortBySupplier) {
        this.sortBySupplier = sortBySupplier;
    }

    public Boolean getSortByDeliveryDate() {
        return sortByDeliveryDate;
    }

    public void setSortByDeliveryDate(Boolean sortByDeliveryDate) {
        this.sortByDeliveryDate = sortByDeliveryDate;
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
}
