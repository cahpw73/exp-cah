package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import ch.swissbytes.domain.types.ClassEnum;
import ch.swissbytes.domain.types.POStatusEnum;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Named
@Entity
@Table(name = "p_order")
public class POEntity implements Serializable{

    private Long id;

    private Long transactionNumber;
    private Date orderDate;
    private String orderTitle;
    private String varNumber;
    private String orderNumber;
    private ProjectCurrencyEntity currency;
    private String deliveryInstruction;
    private SupplierProcEntity supplier;
    private String point;
    private ClassEnum clazz;
    private POStatusEnum poProcStatus;

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
    @Column(name="delivery_instruction",length=1000)
    public String getDeliveryInstruction() {
        return deliveryInstruction;
    }

    public void setDeliveryInstruction(String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="currency_id")
    public ProjectCurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(ProjectCurrencyEntity currency) {
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
    @JoinColumn(name="supplier_id")
    public SupplierProcEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierProcEntity supplier) {
        this.supplier = supplier;
    }

    @Column(name="order_number",length = 250)
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    @Column(name="point",length = 250)
    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    private SupplierProcEntity supplierHeader;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="supplier_header_id")
    public SupplierProcEntity getSupplierHeader() {
        return supplierHeader;
    }

    public void setSupplierHeader(SupplierProcEntity supplierHeader) {
        this.supplierHeader = supplierHeader;
    }

    @Column(name = "po_status", nullable = false)
    public POStatusEnum getPoProcStatus() {
        return poProcStatus;
    }

    public void setPoProcStatus(POStatusEnum poProcStatus) {
        this.poProcStatus = poProcStatus;
    }

    @Column(name = "class", nullable = false)
    public ClassEnum getClazz() {
        return clazz;
    }

    public void setClazz(ClassEnum clazz) {
        this.clazz = clazz;
    }
}