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
@Table(name = "cashflow_detail")
public class CashflowDetailEntity extends RecordEditable<CashflowDetailEntity> implements Serializable{

    private Long id;
    private String item;
    private String milestone;
    private BigDecimal orderAmt;
    private BigDecimal projectAmt;
    private BigDecimal percentage;
    private Date claimDate;
    private Date paymentDate;
    private Date lastUpdate;
    private StatusEnum status;
    private CashflowEntity cashflowEntity;
    private ProjectCurrencyEntity projectCurrency;
    private Integer ordered;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "CLAUSES_TEXT_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "item")
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Column(name = "milestone")
    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    @Column(name="order_amt", precision=18, scale=5)
    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }

    @Column(name="project_amt", precision=18, scale=5)
    public BigDecimal getProjectAmt() {
        return projectAmt;
    }

    public void setProjectAmt(BigDecimal projectAmt) {
        this.projectAmt = projectAmt;
    }

    @Column(name = "claim_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column (name = "status",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="cashflow_id")
    public CashflowEntity getCashflowEntity() {
        return cashflowEntity;
    }

    public void setCashflowEntity(CashflowEntity cashflowEntity) {
        this.cashflowEntity = cashflowEntity;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_currency_id")
    public ProjectCurrencyEntity getProjectCurrency() {
        return projectCurrency;
    }

    public void setProjectCurrency(ProjectCurrencyEntity projectCurrency) {
        this.projectCurrency = projectCurrency;
    }

    @Column(name="percentage", precision=18, scale=5)
    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @Column(name="ORDERED")
    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashflowDetailEntity entity = (CashflowDetailEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
