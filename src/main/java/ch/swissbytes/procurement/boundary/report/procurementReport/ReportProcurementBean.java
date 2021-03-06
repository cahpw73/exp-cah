package ch.swissbytes.procurement.boundary.report.procurementReport;


import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.procurement.report.ReportProcBean;
import ch.swissbytes.procurement.report.dtos.ProjectProcurementDto;
import ch.swissbytes.procurement.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
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

    private boolean sortByPoNo;
    private boolean sortByVarNo;
    private boolean sortBySupplier;
    private boolean sortByDeliveryDate;
    private boolean sortMrNo;
    private boolean sortRtfNo;
    private boolean sortOriginator;
    private boolean sortCurrency;
    private boolean sortCode;
    private boolean sortCountry;

    private Date sortFrom;
    private Date sortTo;

    private String reportName;

    private String reportTitle;
    private String projectProcurementReport = "Project Procurement Report";
    private String requiredRetentionReport = "Required Retentions Report";
    private String requiredSecurityReport = "Required Security Deposits Report";
    private String summaryPOReport = "Summary Purchase Order Report";
    private String detailProcurementReport = "Detail Procurement Report";
    private String supplierContactInformation = "Supplier Contact Information";
    private String detailedSupplierReport = "Detailed Supplier Report";
    private String uncommitedDataReport = "Uncommitted Data Report";
    private String materialRequisitionReport = "Materials Requisition Report";
    private String committedCurrenciesReport = "Committed Currencies Report";

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
        sortFrom = DateUtil.getDateMinHour(sortFrom);
        sortTo = DateUtil.getDateMaxHour(sortTo);
        Timestamp sqSortFrom = new java.sql.Timestamp(sortFrom != null ? sortFrom.getTime() : DateUtil.getFirstDateDefault().getTime());
        Timestamp sqSortTo = new java.sql.Timestamp(sortTo != null ? sortTo.getTime() : DateUtil.getLastDateDefault().getTime());

        log.info("sortFrom = " + sortFrom);
        log.info("sortTo = " + sortTo);
        log.info("sortFromSq = " + sqSortFrom);
        log.info("sortToSq = " + sqSortTo);
        Map<String, Boolean> sortMap = new HashMap<>();
        sortMap.put("poNo", sortByPoNo);
        sortMap.put("supplier", sortBySupplier);
        sortMap.put("deliveryDate", sortByDeliveryDate);
        sortMap.put("mrNo",sortMrNo);
        sortMap.put("rtfNo",sortRtfNo);
        sortMap.put("originator",sortOriginator);
        sortMap.put("currency",sortCurrency);
        sortMap.put("code",sortCode);
        sortMap.put("country",sortCountry);
        if (selectedProject != null) {
            switch (reportName) {
                case "ppr":
                    verifyDataToSortDate();
                    reportProcBean.printProjectPurchaseOrder(selectedProject, sortMap,sqSortFrom,sqSortTo);
                    break;
                case "rrr":
                    reportProcBean.printRequiredRetentions(selectedProject, sortMap);
                    break;
                case "sci":
                    reportProcBean.printSupplierContactInformation(selectedProject, sortMap);
                    break;
                case "dsi":
                    reportProcBean.printDetailedSupplierInformation(selectedProject, sortMap);
                    break;
                case "udr":
                    reportProcBean.printUncommittedData(selectedProject, sortMap);
                    break;
                case "mrr":
                    reportProcBean.printMaterialRequisition(selectedProject, sortMap);
                    break;
                case "spor":
                    verifyDataToSortDate();
                    reportProcBean.printSummaryPurchaseOrder(selectedProject, sortMap,sqSortFrom,sqSortTo);
                    break;
                case "pdp":
                    reportProcBean.printDetailedProcurementReport(selectedProject, sortMap);
                    break;
                case "cc":
                    reportProcBean.printCommittedCurrenciesReport(selectedProject, sortMap);
                    break;
                case "rrs":
                    reportProcBean.printRequiredSecurity(selectedProject,sortMap);
                    break;
            }
        } else {
            Messages.addFlashGlobalError("Select a project first");
        }
    }

    private void verifyDataToSortDate(){
        if(sortFrom == null){
            sortFrom = DateUtil.getFirstDateDefault();
        }
        if(sortTo == null){
            sortTo = DateUtil.getLastDateDefault();
        }
    }

    public void loadNameReport() {
        if (StringUtils.isNotEmpty(reportName)) {
            switch (reportName) {
                case "ppr":
                    reportTitle = projectProcurementReport;
                    break;
                case "rrr":
                    reportTitle = requiredRetentionReport;
                    break;
                case "spor":
                    reportTitle = summaryPOReport;
                    break;
                case "pdp":
                    reportTitle = detailProcurementReport;
                    break;
                case "sci":
                    reportTitle = supplierContactInformation;
                    break;
                case "dsi":
                    reportTitle = detailedSupplierReport;
                    addAllProjectToProjectList();
                    break;
                case "udr":
                    reportTitle = uncommitedDataReport;
                    break;
                case "mrr":
                    reportTitle = materialRequisitionReport;
                    break;
                case "cc":
                    reportTitle = committedCurrenciesReport;
                    break;
                case "rrs":
                    reportTitle = requiredSecurityReport;
                    break;
            }
        }
        log.info("report title: " + reportTitle);
    }

    private void addAllProjectToProjectList() {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(-1L);
        projectEntity.setProjectNumber("All Projects");
        List<ProjectEntity> auxList = new ArrayList<>();
        auxList.add(projectEntity);
        for (ProjectEntity p : projectList) {
            auxList.add(p);
        }
        projectList.clear();
        projectList.addAll(auxList);
    }

    public boolean isMaterialRequisitionReport(){
        return reportTitle.equals(materialRequisitionReport);
    }
    public boolean canshowFilterDeliveryDate(){
        return reportTitle.equals(materialRequisitionReport) || reportTitle.equals(committedCurrenciesReport)||
                reportTitle.equals(detailedSupplierReport) || reportTitle.equals(summaryPOReport) || reportTitle.equals(detailProcurementReport);
    }
    public boolean isDetailSupplier(){
        return reportTitle.equals(detailedSupplierReport);
    }

    public boolean canShowFilterCountry(){
        return reportTitle.equals(summaryPOReport) || reportTitle.equals(detailProcurementReport);
    }

    public boolean canshowFilterPoDeliveryDate(){
        return reportTitle.equals(summaryPOReport) || reportTitle.equals(projectProcurementReport);
    }


    public boolean canShowFilterCurrency(){
        return reportTitle.equals(committedCurrenciesReport);
    }

    public String backToReports() {
        log.info("back to reports");
        return "report?faces-redirect=true";
    }

    public void resetOptionPrint(Integer option){
        switch (option){
            case 1://sortPO
                sortBySupplier = false;
                sortByDeliveryDate = false;
                sortMrNo = false;
                sortRtfNo = false;
                sortOriginator = false;
                sortCurrency = false;
                sortCode = false;
                sortCountry = false;
                break;
            case 2://sortSupplier
                sortByPoNo = false;
                sortByDeliveryDate = false;
                sortMrNo = false;
                sortRtfNo = false;
                sortOriginator = false;
                sortCurrency = false;
                sortCode = false;
                sortCountry = false;
                break;
            case 3://sortDeliveryDate
                sortByPoNo = false;
                sortBySupplier = false;
                sortMrNo = false;
                sortRtfNo = false;
                sortOriginator = false;
                sortCurrency = false;
                sortCode = false;
                sortCountry = false;
                break;
            case 4://sortMrNo;
                sortByDeliveryDate = false;
                sortByPoNo = false;
                sortBySupplier = false;
                sortRtfNo = false;
                sortOriginator = false;
                sortCurrency = false;
                sortCode = false;
                sortCountry = false;
                break;
            case 5://sortRtfNo
                sortByDeliveryDate = false;
                sortByPoNo = false;
                sortBySupplier = false;
                sortMrNo = false;
                sortOriginator = false;
                sortCurrency = false;
                sortCode = false;
                sortCountry = false;
                break;
            case 6://sortOriginator
                sortByDeliveryDate = false;
                sortByPoNo = false;
                sortBySupplier = false;
                sortMrNo = false;
                sortRtfNo = false;
                sortCurrency = false;
                sortCode = false;
                sortCountry = false;
                break;
            case 7://sortCurrency
                sortOriginator = false;
                sortByDeliveryDate = false;
                sortByPoNo = false;
                sortBySupplier = false;
                sortMrNo = false;
                sortRtfNo = false;
                sortCode = false;
                sortCountry = false;
                break;
            case 8://sortCode
                sortOriginator = false;
                sortByDeliveryDate = false;
                sortByPoNo = false;
                sortBySupplier = false;
                sortMrNo = false;
                sortRtfNo = false;
                sortCurrency = false;
                break;
            case 9://sortCountry
                sortOriginator = false;
                sortByDeliveryDate = false;
                sortByPoNo = false;
                sortBySupplier = false;
                sortMrNo = false;
                sortRtfNo = false;
                sortCurrency = false;
                sortCode = false;
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

    public Boolean getSortMrNo() {
        return sortMrNo;
    }

    public void setSortMrNo(Boolean sortMrNo) {
        this.sortMrNo = sortMrNo;
    }

    public Boolean getSortRtfNo() {
        return sortRtfNo;
    }

    public void setSortRtfNo(Boolean sortRtfNo) {
        this.sortRtfNo = sortRtfNo;
    }

    public Boolean getSortOriginator() {
        return sortOriginator;
    }

    public void setSortOriginator(Boolean sortOriginator) {
        this.sortOriginator = sortOriginator;
    }

    public boolean isSortCurrency() {
        return sortCurrency;
    }

    public void setSortCurrency(boolean sortCurrency) {
        this.sortCurrency = sortCurrency;
    }

    public boolean isSortCode() {
        return sortCode;
    }

    public void setSortCode(boolean sortCode) {
        this.sortCode = sortCode;
    }

    public boolean isSortCountry() {
        return sortCountry;
    }

    public void setSortCountry(boolean sortCountry) {
        this.sortCountry = sortCountry;
    }

    public Date getSortFrom() {
        return sortFrom;
    }

    public void setSortFrom(Date sortFrom) {
        this.sortFrom = sortFrom;
    }

    public Date getSortTo() {
        return sortTo;
    }

    public void setSortTo(Date sortTo) {
        this.sortTo = sortTo;
    }
}
