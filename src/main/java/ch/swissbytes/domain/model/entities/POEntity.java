package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import ch.swissbytes.domain.types.ClassEnum;
import ch.swissbytes.domain.types.POStatusEnum;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@Entity
@Table(name = "p_order")
public class POEntity implements Serializable{

    private Long id;
    private Date orderDate;
    private String orderTitle;
    private String varNumber;
    private String orderNumber;
    private ProjectCurrencyEntity currency;
    private String deliveryInstruction;
    private SupplierProcEntity supplier;
    private String point;
    private String procManager;
    private String procManagerDetail;
    private ClassEnum clazz;
    private POStatusEnum poProcStatus;
    private List<ScopeSupplyEntity> scopeSupplyList = new ArrayList<>();
    private List<ItemEntity> itemList = new ArrayList<>();
    private List<RequisitionEntity> requisitions = new ArrayList<>();
    private List<DeliverableEntity> deliverables = new ArrayList<>();
    private CashflowEntity cashflow;
    private TextEntity textEntity;
    private String RTFNo;
    private Boolean liquidatedDamagesApplicable;
    private Boolean exchangeRateVariation;
    private Boolean vendorDrawingData;

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


    @Size(max = 250)
    @Column(name="point",length = 250)
    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Size(max = 250)
    @Column(name="proc_manager",length = 250)
    public String getProcManager() {
        return procManager;
    }

    public void setProcManager(String procManager) {
        this.procManager = procManager;
    }

    @Size(max = 1000)
    @Column(name="proc_manager_detail",length = 1000)
    public String getProcManagerDetail() {
        return procManagerDetail;
    }

    public void setProcManagerDetail(String procManagerDetail) {
        this.procManagerDetail = procManagerDetail;
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

    @Column(name = "po_status")
    public POStatusEnum getPoProcStatus() {
        return poProcStatus;
    }

    public void setPoProcStatus(POStatusEnum poProcStatus) {
        this.poProcStatus = poProcStatus;
    }

    @Column(name = "class")
    public ClassEnum getClazz() {
        return clazz;
    }

    public void setClazz(ClassEnum clazz) {
        this.clazz = clazz;
    }

    private ContactEntity contactEntity;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="contact_id")
    public ContactEntity getContactEntity() {
        return contactEntity;
    }

    public void setContactEntity(ContactEntity contactEntity) {
        this.contactEntity = contactEntity;
    }

    public String getRTFNo() {
        return RTFNo;
    }

    public void setRTFNo(String RTFNo) {
        this.RTFNo = RTFNo;

    }
    @Column(name = "LIQUIDATED_DAMAGES_APPLICABLE")
    public Boolean getLiquidatedDamagesApplicable() {
        return liquidatedDamagesApplicable;
    }


    public void setLiquidatedDamagesApplicable(Boolean liquidatedDamagesApplicable) {
        this.liquidatedDamagesApplicable = liquidatedDamagesApplicable;
    }
    @Column(name = "EXCHANGE_RATE_VARIATION")
    public Boolean getExchangeRateVariation() {
        return exchangeRateVariation;
    }

    public void setExchangeRateVariation(Boolean exchangeRateVariation) {
        this.exchangeRateVariation = exchangeRateVariation;
    }
    @Column(name = "VENDOR_DRAWING_DATA")
    public Boolean getVendorDrawingData() {
        return vendorDrawingData;
    }

    public void setVendorDrawingData(Boolean vendorDrawingData) {
        this.vendorDrawingData = vendorDrawingData;
    }

    @Transient
    public List<ItemEntity> getItemList() {
        return itemList;
    }

    @Transient
    public List<ScopeSupplyEntity> getScopeSupplyList() {
        return scopeSupplyList;
    }

    @Transient
    public List<RequisitionEntity> getRequisitions() {
        return requisitions;
    }

    @Transient
    public List<DeliverableEntity> getDeliverables() {
        return deliverables;
    }

    @Transient
    public CashflowEntity getCashflow() {
        return cashflow;
    }

    public void setCashflow(CashflowEntity cashflow) {
        this.cashflow = cashflow;
    }

    @Transient
    public TextEntity getTextEntity() {
        return textEntity;
    }

    public void setTextEntity(TextEntity textEntity) {
        this.textEntity = textEntity;
    }
}
