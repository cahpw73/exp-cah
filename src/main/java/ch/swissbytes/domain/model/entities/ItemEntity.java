package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "item")
public class ItemEntity extends RecordEditable<ItemEntity> implements Serializable{

    private Long id;
    private String itemNo;//Code*
    private String equipNo;//TagNo*
    private String qty;//quantity*
    private String unit;//unit*
    private String description;//description*
    private BigDecimal unitCost;//cost*
    private BigDecimal totalCost;//new
    private BigDecimal costCode;//new
    private Date deliveryDate;//poDeliveryDate*
    private StatusEnum statusEnum;
    private Date lastUpdate;
    private ProjectCurrencyEntity projectCurrency;//new
    private PurchaseOrderProcurementEntity po;//no copy

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "ITEM_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column (name = "status",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(StatusEnum status) {
        this.statusEnum = status;
    }

    @Column(name = "last_update",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name = "item_no", nullable = false)
    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    @Column(name = "equip_no")
    public String getEquipNo() {
        return equipNo;
    }

    public void setEquipNo(String equipNo) {
        this.equipNo = equipNo;
    }

    @Column(name = "qty")
    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    @Column(name = "unit")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="unit_cost", precision=18, scale=5)
    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    @Column(name="total_cost", precision=18, scale=5)
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Column(name="cost_code", precision=18, scale=5)
    public BigDecimal getCostCode() {
        return costCode;
    }

    public void setCostCode(BigDecimal costCode) {
        this.costCode = costCode;
    }

    @Column(name = "delivery_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_currency_id")
    public ProjectCurrencyEntity getProjectCurrency() {
        return projectCurrency;
    }

    public void setProjectCurrency(ProjectCurrencyEntity projectCurrency) {
        this.projectCurrency = projectCurrency;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public PurchaseOrderProcurementEntity getPo() {
        return po;
    }

    public void setPo(PurchaseOrderProcurementEntity po) {
        this.po = po;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemEntity entity = (ItemEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
