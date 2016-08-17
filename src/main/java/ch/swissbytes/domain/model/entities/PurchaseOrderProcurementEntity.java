package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import ch.swissbytes.domain.types.ClassEnum;
import ch.swissbytes.domain.types.ProcurementStatus;

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
public class PurchaseOrderProcurementEntity implements Serializable{

    private Long id;
    private Date orderDate;
    private String orderNumber;
    private ProjectCurrencyEntity currency;
    private String deliveryInstruction;
    private SupplierProcEntity supplier;
    private String point;
    private String procManager;
    private String procManagerDetail;
    private ClassEnum clazz;
    private ProcurementStatus poProcStatus;
    private List<ItemEntity> scopeSupplyList = new ArrayList<>();
    private List<ScopeSupplyEntity> scopeSupplyEntities = new ArrayList<>();
    private List<RequisitionEntity> requisitions = new ArrayList<>();
    private List<DeliverableEntity> deliverables = new ArrayList<>();
    private List<PODocumentEntity> poDocumentList = new ArrayList<>();
    private List<ProjectDocumentEntity> projectDocList = new ArrayList<>();
    private List<ProjectTextSnippetEntity> projectTextSnippetList = new ArrayList<>();
    private List<ProjectDocumentEntity> projectDocumentList = new ArrayList<>();
    private CashflowEntity cashflow;
    private TextEntity textEntity;
    private String RTFNo;
    private Boolean liquidatedDamagesApplicable;
    private Boolean exchangeRateVariation;
    private Boolean vendorDrawingData;
    private Boolean securityDeposit;
    private ContactEntity contactEntity;
    private ContactEntity contactExpediting;
    private Boolean cmsExported=false;
    private Boolean jdeExported=false;

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

    @Transient
    public ProjectCurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(ProjectCurrencyEntity currency) {
        this.currency = currency;
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

    @Column(name = "po_status")
    public ProcurementStatus getPoProcStatus() {
        return poProcStatus;
    }

    public void setPoProcStatus(ProcurementStatus poProcStatus) {
        this.poProcStatus = poProcStatus;
    }

    @Column(name = "class")
    public ClassEnum getClazz() {
        return clazz;
    }

    public void setClazz(ClassEnum clazz) {
        this.clazz = clazz;
    }



    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="contact_id")
    public ContactEntity getContactEntity() {
        return contactEntity;
    }

    public void setContactEntity(ContactEntity contactEntity) {
        this.contactEntity = contactEntity;
    }

    @Column(name = "RTF_NO" ,length = 250)
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

    /*@Transient
    public List<ItemEntity> getItemList() {
        return itemList;
    }*/

    @Transient
    public List<ItemEntity> getScopeSupplyList() {
        return scopeSupplyList;
    }

    @Transient
    public List<ScopeSupplyEntity> getScopeSupplyEntities() {
        return scopeSupplyEntities;
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
    public List<PODocumentEntity> getPoDocumentList() {
        return poDocumentList;
    }

    @Transient
    public List<ProjectDocumentEntity> getProjectDocList() {
        return projectDocList;
    }

    @Transient
    public List<ProjectTextSnippetEntity> getProjectTextSnippetList() {
        return projectTextSnippetList;
    }

    @Transient
    public List<ProjectDocumentEntity> getProjectDocumentList() {
        return projectDocumentList;
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

    @Column(name = "SECURITY_DEPOSIT")
    public Boolean getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(Boolean securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="contact_expediting_id")
    public ContactEntity getContactExpediting() {
        return contactExpediting;
    }

    public void setContactExpediting(ContactEntity contactEntity) {
        this.contactExpediting = contactEntity;
    }

    public Boolean getCmsExported() {
        return cmsExported;
    }

    public void setCmsExported(Boolean cmsExported) {
        this.cmsExported = cmsExported;
    }

    public Boolean getJdeExported() {
        return jdeExported;
    }

    public void setJdeExported(Boolean jdeExported) {
        this.jdeExported = jdeExported;
    }
}
