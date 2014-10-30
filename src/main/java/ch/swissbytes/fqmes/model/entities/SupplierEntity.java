package ch.swissbytes.fqmes.model.entities;

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
@Table(name = "supplier")
public class SupplierEntity implements Serializable{

    private Long id;
    private StatusEntity status;
    private String supplier;
    private String address;
    private String addresDescription;
    private String contactName;
    private String contactNameDescription;
    private PurchaseOrderEntity purchaseOrder;
    private Date lastUpdate;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "SUPPLIER_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Size(max=500)
    @Column(name="address",  length=500)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    @Column(name="address_description",length=1000)
    public String getAddresDescription() {
        return addresDescription;
    }

    public void setAddresDescription(String addresDescription) {
        this.addresDescription = addresDescription;
    }

    @Size(max=100)
    @Column(name="contact_name", length=100)
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Size(max=1000)
    @Column(name="contact_name_description", length=1000)
    public String getContactNameDescription() {
        return contactNameDescription;
    }

    public void setContactNameDescription(String contactNameDescription) {
        this.contactNameDescription = contactNameDescription;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="PURCHASE_ORDER_ID", nullable=false)
    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Size(max=50)
    @Column(name="supplier", nullable = false,length=50)
    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
        if (!(o instanceof SupplierEntity)) return false;

        SupplierEntity that = (SupplierEntity) o;
        return that.hashCode()==this.hashCode();
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (status != null ? status.getId().hashCode() : 0);
        result = 31 * result + (supplier != null ? supplier.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (addresDescription != null ? addresDescription.hashCode() : 0);
        result = 31 * result + (contactName != null ? contactName.hashCode() : 0);
        result = 31 * result + (contactNameDescription != null ? contactNameDescription.hashCode() : 0);
        result = 31 * result + (purchaseOrder != null ? purchaseOrder.getId().hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        return result;
    }
}
