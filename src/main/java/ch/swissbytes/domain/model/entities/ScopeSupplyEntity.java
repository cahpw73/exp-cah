package ch.swissbytes.domain.model.entities;

import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.TimeMeasurementEnum;

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
public class ScopeSupplyEntity extends RecordEditable<ScopeSupplyEntity> implements Serializable, EntityTbl{

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
    private String from;
    private String to;
    private Date date;
    private String descriptionAttachment;
    //new fields
    private BigDecimal totalCost;
    private String costCode;
    private Boolean excludeFromExpediting;
    //@TODO change this value replace to currency
    private ProjectCurrencyEntity projectCurrency;



    private List<TransitDeliveryPointEntity> tdpList=new ArrayList<>();
    private List<AttachmentScopeSupply> attachments=new ArrayList<>();



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
//    @DecimalMin(value = "0",message = "just positive values")
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

    @Column(name="quantity")
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

    //@TODO Cambiamos el valur nullable por true, se tiene que controlar la nullabilidad desde el modulo expediting
    @Column(name="EX_WORK_DATE")
    public Date getForecastExWorkDate() {
        return forecastExWorkDate;
    }

    public void setForecastExWorkDate(Date forecastExWorkDate) {
        this.forecastExWorkDate = forecastExWorkDate;
    }

    @Size(max = 1000)
    @Column(name="EX_WORK_DATE_DESCRIPTION", length = 1000)
    public String getExWorkDateDescription() {
        return exWorkDateDescription;
    }

    public void setExWorkDateDescription(String exWorkDateDescription) {
        this.exWorkDateDescription = exWorkDateDescription;
    }

    //TODO Cambiamos el valur nullable por true, se tiene que controlar la nullabilidad desde el modulo expediting
    @Column(name="DELIVERY_LEAD_TIME_QT")
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

    //TODO Cambiamos el valur nullable por true, se tiene que controlar la nullabilidad desde el modulo expediting
    @Column(name="SITE_DATE")
    public Date getForecastSiteDate() {
        return forecastSiteDate;
    }

    public void setForecastSiteDate(Date forecastSiteDate) {
        this.forecastSiteDate = forecastSiteDate;
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

    @Override
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
    public Date getPoDeliveryDate() {
        return poDeliveryDate;
    }

    public void setPoDeliveryDate(Date poDeliveryDate) {
        this.poDeliveryDate = poDeliveryDate;
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

    @Transient
    public List<AttachmentScopeSupply> getAttachments() {
        return attachments;
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

    @Size(max = 1000)
    @Column(name="TAG_NO", length = 1000)
    public String getTagNo() {
        return tagNo;
    }

    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }

    @Size(max = 100)
    @Column(name = "TO_SS",  length = 100)
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    @Size(max = 100)
    @Column(name = "FROM_SS", length = 100)
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    @Column(name = "DATE_SS", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Size(max = 950)
    @Column(name = "DESCRIPTION_ATTACHMENT", length = 1000)
    public String getDescriptionAttachment() {
        return descriptionAttachment;
    }

    public void setDescriptionAttachment(String descriptionAttachment) {
        this.descriptionAttachment = descriptionAttachment;
    }

    @Column(name="total_cost", precision=18, scale=5)
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Column(name="cost_code")
    public String getCostCode() {
        return costCode;
    }

    public void setCostCode(String costCode) {
        this.costCode = costCode;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_currency_id")
    public ProjectCurrencyEntity getProjectCurrency() {
        return projectCurrency;
    }

    public void setProjectCurrency(ProjectCurrencyEntity projectCurrency) {
        this.projectCurrency = projectCurrency;
    }

    @Column(name = "exclude_from_expediting")
    public Boolean getExcludeFromExpediting() {
        return excludeFromExpediting;
    }

    public void setExcludeFromExpediting(Boolean excludeFromExpediting) {
        this.excludeFromExpediting = excludeFromExpediting;
    }

    @Transient
    public BigDecimal calculateTotal(){
        return quantity!=null&&cost!=null?cost.multiply(new BigDecimal(quantity.toString())):null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (forecastExWorkDate != null ? forecastExWorkDate.hashCode() : 0);
        result = 31 * result + (exWorkDateDescription != null ? exWorkDateDescription.hashCode() : 0);
        result = 31 * result + (deliveryLeadTimeQt != null ? deliveryLeadTimeQt.hashCode() : 0);
        result = 31 * result + (deliveryLeadTimeMs != null ? deliveryLeadTimeMs.hashCode() : 0);
        result = 31 * result + (getDeliveryLeadTimeDescription != null ? getDeliveryLeadTimeDescription.hashCode() : 0);
        result = 31 * result + (forecastSiteDate != null ? forecastSiteDate.hashCode() : 0);
        result = 31 * result + (siteDateDescription != null ? siteDateDescription.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (purchaseOrder != null ? purchaseOrder.hashCode() : 0);
        result = 31 * result + (status != null ? status.getId() : 0);
        result = 31 * result + (poDeliveryDate != null ? poDeliveryDate.hashCode() : 0);
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
