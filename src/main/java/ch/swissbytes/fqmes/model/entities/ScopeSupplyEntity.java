package ch.swissbytes.fqmes.model.entities;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Table(name = "scope_supply")
public class ScopeSupplyEntity implements Serializable, EntityTbl{

    private Long id;
    private BigDecimal cost;
    private String currency;
    private String code;
    private Integer quantity;
    private String unit;
    private String description;
    private Date exWorkDate;
    private String exWorkDateDescription;
    private Integer deliveryLeadTimeQt;
    private TimeMeasurementEnum deliveryLeadTimeMs;
    private String getDeliveryLeadTimeDescription;
    private Date siteDate;
    private String siteDateDescription;
    private Date lastUpdate;
    private PurchaseOrderEntity purchaseOrder;
    private StatusEntity status;
    private Date deliveryDate;
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


    private List<TransitDeliveryPointEntity> tdpList=new ArrayList<>();



    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "SCOPE_SUPPLY_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name="cost", precision=18, scale=5)
    @DecimalMin(value = "0",message = "just positive values")
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
@Size(max = 50)
    @Column(name="code", length = 50)
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

    @Size(max = 50)
    @Column(name="unit", length = 50)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Size(max = 1000)
    @Column(name="description",length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="EX_WORK_DATE", nullable = false)
    public Date getExWorkDate() {
        return exWorkDate;
    }

    public void setExWorkDate(Date exWorkDate) {
        this.exWorkDate = exWorkDate;
    }

    @Size(max = 1000)
    @Column(name="EX_WORK_DATE_DESCRIPTION", length = 1000)
    public String getExWorkDateDescription() {
        return exWorkDateDescription;
    }

    public void setExWorkDateDescription(String exWorkDateDescription) {
        this.exWorkDateDescription = exWorkDateDescription;
    }
    @Column(name="DELIVERY_LEAD_TIME_QT", nullable = false)
    public Integer getDeliveryLeadTimeQt() {
        return deliveryLeadTimeQt;
    }

    public void setDeliveryLeadTimeQt(Integer deliveryLeadTimeQt) {
        this.deliveryLeadTimeQt = deliveryLeadTimeQt;
    }


    @Column(name="DELIVERY_LEAD_TIME_MS")
    @Enumerated(EnumType.ORDINAL)
    public TimeMeasurementEnum getDeliveryLeadTimeMs() {
        return deliveryLeadTimeMs;
    }

    public void setDeliveryLeadTimeMs(TimeMeasurementEnum deliveryLeadTimeMs) {
        this.deliveryLeadTimeMs = deliveryLeadTimeMs;
    }
    @Column(name="DELIVERY_LEAD_TIME_DESCRIPTION", length = 1000)
    public String getGetDeliveryLeadTimeDescription() {
        return getDeliveryLeadTimeDescription;
    }

    public void setGetDeliveryLeadTimeDescription(String getDeliveryLeadTimeDescription) {
        this.getDeliveryLeadTimeDescription = getDeliveryLeadTimeDescription;
    }
    @Column(name="SITE_DATE", nullable = false)
    public Date getSiteDate() {
        return siteDate;
    }

    public void setSiteDate(Date siteDate) {
        this.siteDate = siteDate;
    }

    @Size(max = 1000)
    @Column(name="SITE_DATE_DESCRIPTION", length = 1000)
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
    @Column(name="LAST_UPDATE", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="status_id", nullable=false)
    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopeSupplyEntity)) return false;

        ScopeSupplyEntity that = (ScopeSupplyEntity) o;
        return that.hashCode()==this.hashCode();
    }


    @Column(name="DELIVERY_DATE")
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Column(name="ACTUAL_EX_WORK_DATE")
    public Date getActualExWorkDate() {
        return actualExWorkDate;
    }

    public void setActualExWorkDate(Date actualExWorkDate) {
        this.actualExWorkDate = actualExWorkDate;
    }


    @Column(name="REQUIRED_SITE_DATE")
    public Date getRequiredSiteDate() {
        return requiredSiteDate;
    }

    public void setRequiredSiteDate(Date requiredSiteDate) {
        this.requiredSiteDate = requiredSiteDate;
    }

    @Column(name="ACTUAL_SITE_DATE")
    public Date getActualSiteDate() {
        return actualSiteDate;
    }

    public void setActualSiteDate(Date actualSiteDate) {
        this.actualSiteDate = actualSiteDate;
    }


    @Column(name="DELIVERY_DATE_OBS",length = 1000)
    public String getDeliveryDateObs() {
        return deliveryDateObs;
    }

    public void setDeliveryDateObs(String deliveryDateObs) {
        this.deliveryDateObs = deliveryDateObs;
    }

    @Column(name="ACTUAL_EX_WORK_DATE_OBS",length = 1000)
    public String getActualExWorkDateObs() {
        return actualExWorkDateObs;
    }

    public void setActualExWorkDateObs(String actualExWorkDateObs) {
        this.actualExWorkDateObs = actualExWorkDateObs;
    }
    @Column(name="REQUIRED_SITE_DATE_OBS",length = 1000)
    public String getRequiredSiteDateObs() {
        return requiredSiteDateObs;
    }

    public void setRequiredSiteDateObs(String requiredSiteDateObs) {
        this.requiredSiteDateObs = requiredSiteDateObs;
    }
    @Column(name="ACTUAL_SITE_DATE_OBS",length = 1000)
    public String getActualSiteDateObs() {
        return actualSiteDateObs;
    }

    public void setActualSiteDateObs(String actualSiteDateObs) {
        this.actualSiteDateObs = actualSiteDateObs;
    }

    @NotNull
    @Column(name="IS_FORECAST_SITE_DATE_MANUAL",nullable = false)
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

    @Size(max = 5, message = "currency at most need 5 characters")
    @Column(name="CURRENCY",length = 5)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="sp_inco_term",length=50)
    public String getSpIncoTerm() {
        return spIncoTerm;
    }

    public void setSpIncoTerm(String spIncoTerm) {
        this.spIncoTerm = spIncoTerm;
    }
    @Column(name="sp_inco_term_description", length=1000)
    public String getSpIncoTermDescription() {
        return spIncoTermDescription;
    }

    public void setSpIncoTermDescription(String spIncoTermDescription) {
        this.spIncoTermDescription = spIncoTermDescription;
    }

    @Column(name="RESPONSIBLE_EXPEDITING",length=100)
    public String getResponsibleExpediting() {
        return responsibleExpediting;
    }


    public void setResponsibleExpediting(String responsibleExpediting) {
        this.responsibleExpediting = responsibleExpediting;
    }

    @Column(name="RESPONSIBLE_EXPEDITING_OBSERVATION",length=1000)
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

    @Column(name="ORDERED")
    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (exWorkDate != null ? exWorkDate.hashCode() : 0);
        result = 31 * result + (exWorkDateDescription != null ? exWorkDateDescription.hashCode() : 0);
        result = 31 * result + (deliveryLeadTimeQt != null ? deliveryLeadTimeQt.hashCode() : 0);
        result = 31 * result + (deliveryLeadTimeMs != null ? deliveryLeadTimeMs.hashCode() : 0);
        result = 31 * result + (getDeliveryLeadTimeDescription != null ? getDeliveryLeadTimeDescription.hashCode() : 0);
        result = 31 * result + (siteDate != null ? siteDate.hashCode() : 0);
        result = 31 * result + (siteDateDescription != null ? siteDateDescription.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (purchaseOrder != null ? purchaseOrder.hashCode() : 0);
        result = 31 * result + (status != null ? status.getId() : 0);
        result = 31 * result + (deliveryDate != null ? deliveryDate.hashCode() : 0);
        result = 31 * result + (actualExWorkDate != null ? actualExWorkDate.hashCode() : 0);
        result = 31 * result + (requiredSiteDate != null ? requiredSiteDate.hashCode() : 0);
        result = 31 * result + (actualSiteDate != null ? actualSiteDate.hashCode() : 0);
        result = 31 * result + (deliveryDateObs != null ? deliveryDateObs.hashCode() : 0);
        result = 31 * result + (actualExWorkDateObs != null ? actualExWorkDateObs.hashCode() : 0);
        result = 31 * result + (requiredSiteDateObs != null ? requiredSiteDateObs.hashCode() : 0);
        result = 31 * result + (actualSiteDateObs != null ? actualSiteDateObs.hashCode() : 0);
        result = 31 * result + (isForecastSiteDateManual != null ? isForecastSiteDateManual.hashCode() : 0);
        result = 31 * result + (spIncoTerm != null ? spIncoTerm.hashCode() : 0);
        result = 31 * result + (spIncoTermDescription != null ? spIncoTermDescription.hashCode() : 0);
        result = 31 * result + (responsibleExpediting != null ? responsibleExpediting.hashCode() : 0);
        result = 31 * result + (responsibleExpeditingObservation != null ? responsibleExpeditingObservation.hashCode() : 0);
        return result;
    }
}
