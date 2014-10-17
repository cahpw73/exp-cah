package ch.swissbytes.fqmes.model.entities;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by alvaro on 9/9/14.
 */
@Named
@Entity
@Table(name = "comments")
public class CommentEntity implements Serializable, EntityTbl{

    private Long id;
    private String name;
    private String reason;
    private String description;
    private String fileName;
    private String mimeType;
    private byte[]  file;
    private Date lastUpdate;
    private PurchaseOrderEntity purchaseOrder;
    private StatusEntity status;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "COMMENT_ID_SEQ", allocationSize = 1)
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

    @Column(name="reason", nullable=false, length=50)
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    @Column(name="description", length=1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Column(name="f_name",  length=50)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    @Column(name="mime_type",  length=50)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    @Column(name="file_attached")
    public byte[]  getFile() {
        return file;
    }

    public void setFile(byte[]  file) {
        this.file = file;
    }
    @ManyToOne(fetch=FetchType.EAGER)
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
        if (!(o instanceof CommentEntity)) return false;

        CommentEntity that = (CommentEntity) o;

      return that.hashCode()==this.hashCode();
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (file != null ? Arrays.hashCode(file) : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (purchaseOrder != null ? purchaseOrder.getId().hashCode() : 0);
        result = 31 * result + (status != null ? status.getId().hashCode() : 0);
        return result;
    }
}
