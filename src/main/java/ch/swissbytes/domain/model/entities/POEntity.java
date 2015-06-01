package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Named
@Entity
@Table(name = "purchase_order")
public class POEntity implements Serializable{

    private Long id;

    private Long transactionNumber;
    private Date orderDate;
    private String orderTitle;
    private String varNumber;
    private CurrencyEntity currency;
    private String deliveryInstruction;
    private SupplierProcEntity supplier;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PO_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Size(max = 950)
    @Column(name="delivery_instruction", nullable=false, length=1000)
    public String getDeliveryInstruction() {
        return deliveryInstruction;
    }

    public void setDeliveryInstruction(String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="currency_id")
    public CurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }
    @Size(max = 250)
    @Column(name="var_number",  length=250)
    public String getVarNumber() {
        return varNumber;
    }

    public void setVarNumber(String varNumber) {
        this.varNumber = varNumber;
    }

    @Size(max = 250)
    @Column(name="order_title", length=250)
    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }


    @Column(name="transaction_number")
    public Long getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(Long transactionNumber) {
        this.transactionNumber = transactionNumber;
    }
    @Column(name="ORDER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="suppplier_id", nullable=false)
    public SupplierProcEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierProcEntity supplier) {
        this.supplier = supplier;
    }
}
