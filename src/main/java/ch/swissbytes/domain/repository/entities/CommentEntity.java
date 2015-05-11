package ch.swissbytes.domain.repository.entities;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alvaro on 9/9/14.
 */
@Named
@Entity
@Table(name = "comments")
public class CommentEntity implements Serializable, EntityTbl {

    private Long id;
    private String name;
    private String subject;
    private String to;
    private String description;
    private Date lastUpdate;
    private PurchaseOrderEntity purchaseOrder;
    private StatusEntity status;
    private List<AttachmentComment> attachments=new ArrayList<>();

    private Boolean justLoaded = false;


    private Boolean fileWasChanged = false;

    private int previousHascode=0;

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

    @Size(max = 50)
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 100)
    @Column(name = "TO_CMT", nullable = false, length = 100)
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Size(max = 50)
    @Column(name = "reason", nullable = false, length = 50)
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purchase_order_id")
    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Column(name = "LAST_UPDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
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

        return that.hashCode() == this.hashCode();
    }

    @Transient
    public Boolean getJustLoaded() {
        return justLoaded;
    }

    public void setJustLoaded(Boolean justLoaded) {
        this.justLoaded = justLoaded;
    }


    @Transient
    public Boolean getFileWasChanged() {
        return fileWasChanged;
    }

    public void setFileWasChanged(Boolean fileWasChanged) {
        this.fileWasChanged = fileWasChanged;
    }

    @Transient
    public int getPreviousHascode() {
        return previousHascode;
    }

    public void setPreviousHascode(int previousHascode) {
        this.previousHascode = previousHascode;
    }

    @Transient
     public List<AttachmentComment> getAttachments(){
         return attachments;
     }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (purchaseOrder != null ? purchaseOrder.getId().hashCode() : 0);
        result = 31 * result + (status != null ? status.getId().hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
