package ch.swissbytes.fqmes.model.entities;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by alvaro on 9/9/14.
 */
@Named
@Entity
@Table(name = "attachment")
public class AttachmentEntity implements Serializable, EntityTbl {

    private Long id;
    private String name;
    private String fileName;
    private String mimeType;
    private byte[]  file;
    private Date lastUpdate;
    private StatusEntity status;
    private PurchaseOrderEntity purchaseOrder;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "ATTACHMENT_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="name", nullable=false, length=50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="purchase_order_id")
    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Column(name="LAST_UPDATE", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name="f_name", nullable=false, length=50)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    @Column(name="mime_type", nullable=false, length=50)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    @Column(name="file_attached", nullable=false, length = 10485760)
    @Basic(fetch = FetchType.LAZY)
    public byte[]  getFile() {
        return file;
    }

    public void setFile(byte[]  file) {
        this.file = file;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="status_id", nullable=false)
    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }
}
