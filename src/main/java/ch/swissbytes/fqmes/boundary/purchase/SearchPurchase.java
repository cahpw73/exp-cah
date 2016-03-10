package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.types.TypeDateReportEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alvaro on 9/25/14.
 */

public class SearchPurchase extends Filter {


    private String project;
    private String po;
    private String variation;
    private String supplier;
    private String poTitle;
    private String responsibleExpediting;
    private String incoTerm;
    private String variance = "all";
    private Integer leadTime;
    private Integer dueIn;
    private Integer forecastDueDate;
    private String statuses;
    private String expeditingTitle;
    private List<Long> projectsAssignedId = new ArrayList<>();

    private Date deliveryDateStart;
    private Date deliveryDateEnd;
    private Date nextKeyDateStart;
    private Date nextKeyDateEnd;
    private Date forecastExWorkDateStart;
    private Date forecastExWorkDateEnd;

    private TypeDateReportEnum typeDateReport = TypeDateReportEnum.FORECAST_EX_WORKS_DATE;
    private Date startDateReport;
    private Date endDateReport;



    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }

    public String getResponsibleExpediting() {
        return responsibleExpediting;
    }

    public void setResponsibleExpediting(String responsibleExpediting) {
        this.responsibleExpediting = responsibleExpediting;
    }

    public String getIncoTerm() {
        return incoTerm;
    }

    public void setIncoTerm(String incoTerm) {
        this.incoTerm = incoTerm;
    }

    public Date getDeliveryDateStart() {
        return deliveryDateStart;
    }

    public void setDeliveryDateStart(Date deliveryDateStart) {
        this.deliveryDateStart = deliveryDateStart;
    }

    public Date getDeliveryDateEnd() {
        return deliveryDateEnd;
    }

    public void setDeliveryDateEnd(Date deliveryDateEnd) {
        this.deliveryDateEnd = deliveryDateEnd;
    }

    public Date getNextKeyDateStart() {
        return nextKeyDateStart;
    }

    public void setNextKeyDateStart(Date nextKeyDateStart) {
        this.nextKeyDateStart = nextKeyDateStart;
    }

    public Date getNextKeyDateEnd() {
        return nextKeyDateEnd;
    }

    public void setNextKeyDateEnd(Date nextKeyDateEnd) {
        this.nextKeyDateEnd = nextKeyDateEnd;
    }

    public Integer getForecastDueDate() {
        return forecastDueDate;
    }

    public void setForecastDueDate(Integer forecastDueDate) {
        this.forecastDueDate = forecastDueDate;
    }

    public Integer getDueIn() {
        return dueIn;
    }

    public void setDueIn(Integer dueIn) {
        this.dueIn = dueIn;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public String getVariance() {
        return variance;
    }

    public void setVariance(String variance) {
        this.variance = variance;
    }

    public String getStatuses() {
        return statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }

    public boolean hasAnyValueForScopeSupplyActive() {
        return (variance != null && !variance.equalsIgnoreCase("all")) || leadTime != null || forecastDueDate != null || dueIn != null || deliveryDateStart != null || deliveryDateEnd != null;
    }

    public boolean isStatusSelected(String statusId) {
        return statuses == null ? false : statuses.contains(statusId);
    }

    public String getExpeditingTitle() {
        return expeditingTitle;
    }

    public void setExpeditingTitle(String expeditingTitle) {
        this.expeditingTitle = expeditingTitle;
    }

    public Date getForecastExWorkDateStart() {
        return forecastExWorkDateStart;
    }

    public void setForecastExWorkDateStart(Date forecastExWorkDateStart) {
        this.forecastExWorkDateStart = forecastExWorkDateStart;
    }

    public Date getForecastExWorkDateEnd() {
        return forecastExWorkDateEnd;
    }

    public void setForecastExWorkDateEnd(Date forecastExWorkDateEnd) {
        this.forecastExWorkDateEnd = forecastExWorkDateEnd;
    }

    public List<Long> getProjectsAssignedId() {
        return projectsAssignedId;
    }

    public void setProjectsAssignedId(List<Long> projectsAssignedId) {
        this.projectsAssignedId = projectsAssignedId;
    }

    public TypeDateReportEnum getTypeDateReport() {
        return typeDateReport;
    }

    public void setTypeDateReport(TypeDateReportEnum typeDateReport) {
        this.typeDateReport = typeDateReport;
    }

    public Date getStartDateReport() {
        return startDateReport;
    }

    public void setStartDateReport(Date startDateReport) {
        this.startDateReport = startDateReport;
    }

    public Date getEndDateReport() {
        return endDateReport;
    }

    public void setEndDateReport(Date endDateReport) {
        this.endDateReport = endDateReport;
    }

    @Override
    public void clean() {
        po = null;
        project = null;
        variation = null;
        supplier = null;
        poTitle = null;
        responsibleExpediting = null;
        incoTerm = null;
        deliveryDateEnd = null;
        deliveryDateStart = null;
        nextKeyDateStart = null;
        nextKeyDateEnd = null;
        variance = "all";
        leadTime = null;
        dueIn = null;
        forecastDueDate = null;
        statuses = null;
        expeditingTitle = null;
        forecastExWorkDateStart = null;
        forecastExWorkDateEnd = null;
        startDateReport = null;
        endDateReport = null;
        typeDateReport = null;
    }
}
