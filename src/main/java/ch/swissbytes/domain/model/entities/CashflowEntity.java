package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.PaymentTermsEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cashflow")
public class CashflowEntity extends RecordEditable<CashflowEntity> implements Serializable{

    private Long id;
    private Boolean applyRetention;
    private Float percentage;
    private BigDecimal amt;
    private String form;
    private String orderValue;
    private String balance;
    private Date expDate;
    private StatusEnum statusEnum;
    private PaymentTermsEnum paymentTerms;
    private Date lastUpdate;
    private POEntity po;
    private List<CashflowDetailEntity> cashflowDetailList = new ArrayList<>();

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


    @Column(name = "apply_retention")
    public Boolean getApplyRetention() {
        return applyRetention;
    }

    public void setApplyRetention(Boolean applyRetention) {
        this.applyRetention = applyRetention;
    }

    @Column(name = "percentage")
    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    @Column(name="amt", precision=18, scale=5)
    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    @Column(name = "form")
    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Column(name = "order_value", length = 1000)
    public String getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(String orderValue) {
        this.orderValue = orderValue;
    }

    @Column(name = "balance", length = 1000)
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Column (name = "status",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(StatusEnum status) {
        this.statusEnum = status;
    }

    @Column (name = "payment_terms")
    @Enumerated(EnumType.ORDINAL)
    public PaymentTermsEnum getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(PaymentTermsEnum paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    @Column(name = "exp_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name = "last_update",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public POEntity getPo() {
        return po;
    }

    public void setPo(POEntity po) {
        this.po = po;
    }


    @Transient
    public List<CashflowDetailEntity> getCashflowDetailList() {
        return cashflowDetailList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashflowEntity entity = (CashflowEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
