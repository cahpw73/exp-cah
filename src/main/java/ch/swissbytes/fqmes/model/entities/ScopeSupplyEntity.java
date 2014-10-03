package ch.swissbytes.fqmes.model.entities;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by alvaro on 9/10/14.
 */

@Named
@Entity
@Table(name = "scope_supply")
public class ScopeSupplyEntity implements Serializable, EntityTbl{

    private Long id;
    private BigDecimal cost;
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
    @Column(name="description",length = 510)
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
    @Column(name="EX_WORK_DATE_DESCRIPTION", length = 255)
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
    @Column(name="DELIVERY_LEAD_TIME_DESCRIPTION")
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
    @Column(name="SITE_DATE_DESCRIPTION")
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
        result = 31 * result + (purchaseOrder != null ? purchaseOrder.getId().hashCode() : 0);
        result = 31 * result + (status != null ? status.getId().hashCode() : 0);
        return result;
    }
}
