package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "item")
public class ItemEntity implements Serializable{

    private Long id;
    private String itemNo;
    private String qty;
    private String unit;
    private String description;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private BigDecimal costCode;
    private Date deliveryDate;
    private StatusEnum status;
    private Date lastUpdate;
    private CurrencyEntity currency;

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
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
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

    @Column(name = "delivery_date",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="currency_id")
    public CurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }
}