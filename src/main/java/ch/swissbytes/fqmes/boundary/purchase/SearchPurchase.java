package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.Service.infrastructure.Filter;

import java.util.Date;

/**
 * Created by alvaro on 9/25/14.
 */

public class SearchPurchase implements Filter {


    private String project;
    private String po;
    private String variation;
    private String supplier;
    private String poTitle;
    private String responsibleExpediting;
    private String incoTerm;
    private Date deliveryDateStart;
    private Date deliveryDateEnd;
    private String variance="all";
    private Integer leadTime;
    private Integer dueIn;
    private Integer forecastDueDate;
    private String statuses;

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

    public boolean hasAnyValueForScopeSupplyActive(){
        return (variance!=null&&!variance.equalsIgnoreCase("all"))||leadTime!=null||forecastDueDate!=null||dueIn!=null||deliveryDateStart!=null||deliveryDateEnd!=null;
    }

    public boolean isStatusSelected(String statusId){
        return statuses==null?false:statuses.contains(statusId);
    }

    @Override
    public void clean(){
        po=null;
        project=null;
        variation=null;
        supplier=null;
        poTitle=null;
        responsibleExpediting=null;
        incoTerm=null;
        deliveryDateEnd=null;
        deliveryDateStart=null;
        variance="all";
        leadTime=null;
        dueIn=null;
        forecastDueDate=null;
        statuses=null;
    }
}
