package ch.swissbytes.domain.model.entities;

import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by alvaro on 6/3/2015.
 */

@Entity
@Table(name = "requisition")
public class RequisitionEntity extends RecordEditable<RequisitionEntity> implements Serializable {

    private Long id;
    private String requisitionNumber;
    private String originator;
    private Date requisitionDate;
    private Date requiredOnSiteDate;
    private StatusEnum statusEnum;
    private Date lastUpdate;
    private String rTFNo;
    private PurchaseOrderProcurementEntity purchaseOrderProcurementEntity;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "REQUISITION_ID_SEQ", allocationSize = 1)
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

    @Size(max = 250)
    @Column(name = "requisition_number", nullable = false,length = 250)
    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(String reqNo) {
        this.requisitionNumber = reqNo;
    }

    @Size(max=250)
    @Column(name = "originator",length = 250)
    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    @Column(name = "requisition_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRequisitionDate() {
        return requisitionDate;
    }

    public void setRequisitionDate(Date requisitionDate) {
        this.requisitionDate = requisitionDate;
    }

    @Column(name = "required_on_site_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRequiredOnSiteDate() {
        return requiredOnSiteDate;
    }

    public void setRequiredOnSiteDate(Date requiredOnSiteDate) {
        this.requiredOnSiteDate = requiredOnSiteDate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public PurchaseOrderProcurementEntity getPurchaseOrderProcurementEntity() {
        return purchaseOrderProcurementEntity;
    }

    public void setPurchaseOrderProcurementEntity(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity) {
        this.purchaseOrderProcurementEntity = purchaseOrderProcurementEntity;
    }

    @Column(name = "RTF_NO" ,length = 250)
    public String getrTFNo() {
        return rTFNo;
    }

    public void setrTFNo(String rTFNo) {
        this.rTFNo = rTFNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequisitionEntity entity = (RequisitionEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
