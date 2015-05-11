package ch.swissbytes.domain.repository.entities;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alvaro on 9/10/14.
 */

@Named
@Entity
@Table(name = "v_scope_supply")
public class VScopeSupply implements Serializable{

    private Long id;
    private BigDecimal cost;
    private String currency;
    private String code;
    private Integer quantity;
    private String unit;
    private String description;
    private Date forecastExWorkDate;
    private String exWorkDateDescription;
    private Integer deliveryLeadTimeQt;
    private TimeMeasurementEnum deliveryLeadTimeMs;
    private String getDeliveryLeadTimeDescription;
    private Date forecastSiteDate;
    private String siteDateDescription;
    private Date lastUpdate;
    private PurchaseOrderEntity purchaseOrder;
    private StatusEntity status;
    private Date poDeliveryDate;
    private Date actualExWorkDate;
    private Date requiredSiteDate;
    private Date actualSiteDate;
    private String deliveryDateObs;
    private String actualExWorkDateObs;
    private String requiredSiteDateObs;
    private String actualSiteDateObs;
    private Boolean isForecastSiteDateManual;
    private String spIncoTerm;
    private String spIncoTermDescription;
    private String responsibleExpediting;
    private String responsibleExpeditingObservation;
    private Integer ordered;
    private String tagNo;
    private Integer leadTimeDays;


    private Integer variance;
    private List<TransitDeliveryPointEntity> tdpList=new ArrayList<>();
    private List<AttachmentScopeSupply> attachments=new ArrayList<>();



    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name="cost",updatable = false, insertable = false)
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Column(name="code", updatable = false, insertable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="quantity",nullable = false)

    public Integer getQuantity() {
        return quantity;
    }


    @Column(name="unit",updatable = false, insertable = false)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    @Column(name="description",updatable = false, insertable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="EX_WORK_DATE", updatable = false, insertable = false)
    public Date getForecastExWorkDate() {
        return forecastExWorkDate;
    }

    public void setForecastExWorkDate(Date forecastExWorkDate) {
        this.forecastExWorkDate = forecastExWorkDate;
    }


    @Column(name="EX_WORK_DATE_DESCRIPTION", updatable = false, insertable = false)
    public String getExWorkDateDescription() {
        return exWorkDateDescription;
    }

    public void setExWorkDateDescription(String exWorkDateDescription) {
        this.exWorkDateDescription = exWorkDateDescription;
    }
    @Column(name="DELIVERY_LEAD_TIME_QT",updatable = false, insertable = false)
    public Integer getDeliveryLeadTimeQt() {
        return deliveryLeadTimeQt;
    }

    public void setDeliveryLeadTimeQt(Integer deliveryLeadTimeQt) {
        this.deliveryLeadTimeQt = deliveryLeadTimeQt;
    }


    @Column(name="DELIVERY_LEAD_TIME_MS",updatable = false, insertable = false)
    @Enumerated(EnumType.ORDINAL)
    public TimeMeasurementEnum getDeliveryLeadTimeMs() {
        return deliveryLeadTimeMs;
    }

    public void setDeliveryLeadTimeMs(TimeMeasurementEnum deliveryLeadTimeMs) {
        this.deliveryLeadTimeMs = deliveryLeadTimeMs;
    }
    @Column(name="DELIVERY_LEAD_TIME_DESCRIPTION", updatable = false, insertable = false)
    public String getGetDeliveryLeadTimeDescription() {
        return getDeliveryLeadTimeDescription;
    }

    public void setGetDeliveryLeadTimeDescription(String getDeliveryLeadTimeDescription) {
        this.getDeliveryLeadTimeDescription = getDeliveryLeadTimeDescription;
    }
    @Column(name="SITE_DATE", updatable = false, insertable = false)
    public Date getForecastSiteDate() {
        return forecastSiteDate;
    }

    public void setForecastSiteDate(Date forecastSiteDate) {
        this.forecastSiteDate = forecastSiteDate;
    }


    @Column(name="SITE_DATE_DESCRIPTION",updatable = false, insertable = false)
    public String getSiteDateDescription() {
        return siteDateDescription;
    }

    public void setSiteDateDescription(String siteDateDescription) {
        this.siteDateDescription = siteDateDescription;
    }
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="purchase_order_id")
    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }
    @Column(name="LAST_UPDATE",updatable = false, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    /*@JoinColumn(name="status_id", updatable = false, insertable = false)
    public StatusEntity getStatus() {
        return status;
    }*/

  //  public void setStatus(StatusEntity status) {
    //    this.status = status;
    //}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VScopeSupply)) return false;

        VScopeSupply that = (VScopeSupply) o;
        return that.hashCode()==this.hashCode();
    }


    @Column(name="DELIVERY_DATE",updatable = false, insertable = false)
    public Date getPoDeliveryDate() {
        return poDeliveryDate;
    }

    public void setPoDeliveryDate(Date poDeliveryDate) {
        this.poDeliveryDate = poDeliveryDate;
    }

    @Column(name="ACTUAL_EX_WORK_DATE",updatable = false, insertable = false)
    public Date getActualExWorkDate() {
        return actualExWorkDate;
    }

    public void setActualExWorkDate(Date actualExWorkDate) {
        this.actualExWorkDate = actualExWorkDate;
    }


    @Column(name="REQUIRED_SITE_DATE",updatable = false, insertable = false)
    public Date getRequiredSiteDate() {
        return requiredSiteDate;
    }

    public void setRequiredSiteDate(Date requiredSiteDate) {
        this.requiredSiteDate = requiredSiteDate;
    }

    @Column(name="ACTUAL_SITE_DATE",updatable = false, insertable = false)
    public Date getActualSiteDate() {
        return actualSiteDate;
    }

    public void setActualSiteDate(Date actualSiteDate) {
        this.actualSiteDate = actualSiteDate;
    }


    @Column(name="DELIVERY_DATE_OBS",updatable = false, insertable = false)
    public String getDeliveryDateObs() {
        return deliveryDateObs;
    }

    public void setDeliveryDateObs(String deliveryDateObs) {
        this.deliveryDateObs = deliveryDateObs;
    }

    @Column(name="ACTUAL_EX_WORK_DATE_OBS",updatable = false, insertable = false)
    public String getActualExWorkDateObs() {
        return actualExWorkDateObs;
    }

    public void setActualExWorkDateObs(String actualExWorkDateObs) {
        this.actualExWorkDateObs = actualExWorkDateObs;
    }
    @Column(name="REQUIRED_SITE_DATE_OBS",updatable = false, insertable = false)
    public String getRequiredSiteDateObs() {
        return requiredSiteDateObs;
    }

    public void setRequiredSiteDateObs(String requiredSiteDateObs) {
        this.requiredSiteDateObs = requiredSiteDateObs;
    }
    @Column(name="ACTUAL_SITE_DATE_OBS",updatable = false, insertable = false)
    public String getActualSiteDateObs() {
        return actualSiteDateObs;
    }

    public void setActualSiteDateObs(String actualSiteDateObs) {
        this.actualSiteDateObs = actualSiteDateObs;
    }


    @Column(name="IS_FORECAST_SITE_DATE_MANUAL",updatable = false, insertable = false)
    public Boolean getIsForecastSiteDateManual() {
        return isForecastSiteDateManual;
    }

    public void setIsForecastSiteDateManual(Boolean isForecastSiteDateManual) {
        this.isForecastSiteDateManual = isForecastSiteDateManual;
    }

    @Transient
    public List<TransitDeliveryPointEntity> getTdpList() {
        return tdpList;
    }

    @Transient
    public List<AttachmentScopeSupply> getAttachments() {
        return attachments;
    }


    @Column(name="CURRENCY",updatable = false, insertable = false)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="sp_inco_term",updatable = false, insertable = false)
    public String getSpIncoTerm() {
        return spIncoTerm;
    }

    public void setSpIncoTerm(String spIncoTerm) {
        this.spIncoTerm = spIncoTerm;
    }
    @Column(name="sp_inco_term_description",updatable = false, insertable = false)
    public String getSpIncoTermDescription() {
        return spIncoTermDescription;
    }

    public void setSpIncoTermDescription(String spIncoTermDescription) {
        this.spIncoTermDescription = spIncoTermDescription;
    }

    @Column(name="RESPONSIBLE_EXPEDITING",updatable = false, insertable = false)
    public String getResponsibleExpediting() {
        return responsibleExpediting;
    }


    public void setResponsibleExpediting(String responsibleExpediting) {
        this.responsibleExpediting = responsibleExpediting;
    }

    @Column(name="RESPONSIBLE_EXPEDITING_OBSERVATION",length=1000,updatable = false, insertable = false)
    public String getResponsibleExpeditingObservation() {
        return responsibleExpeditingObservation;
    }

    public void setResponsibleExpeditingObservation(String responsibleExpeditingObservation) {
        this.responsibleExpeditingObservation = responsibleExpeditingObservation;
    }

    @Transient
    public String getCustomLeadTime(){
        return Integer.toString(deliveryLeadTimeQt!=null?deliveryLeadTimeQt.intValue():0)+"-"+Integer.toString(deliveryLeadTimeMs.ordinal());

    }

    @Column(name="ORDERED",updatable = false, insertable = false)
    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    @Column(name="TAG_NO",updatable = false, insertable = false)
    public String getTagNo() {
        return tagNo;
    }

    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }

    @Column(name="VARIANCE",updatable = false, insertable = false)
    public Integer getVariance(){
       // Integer difference=10;
     /*   if(requiredSiteDate!=null&&forecastSiteDate!=null){
            Calendar with = Calendar.getInstance();
            with.setTime(forecastSiteDate);
            Calendar to = Calendar.getInstance();
            to.setTime(requiredSiteDate);
            to.set(Calendar.YEAR, with.get(Calendar.YEAR));
            int withDAY = with.get(Calendar.DAY_OF_YEAR);
            int toDAY = to.get(Calendar.DAY_OF_YEAR);
            difference =toDAY  - withDAY;
        }*/
        return variance;
    }

    public void setVariance(Integer variance) {
        this.variance = variance;
    }

    @Column(name="LEADTIMEDAYS",updatable = false, insertable = false)
    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }
}
