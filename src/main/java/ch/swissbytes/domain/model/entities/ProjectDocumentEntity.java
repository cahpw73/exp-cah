package ch.swissbytes.domain.model.entities;

/**
 * Created by christian on 27/01/16.
 */


import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "project_document")
public class ProjectDocumentEntity extends RecordEditable<ProjectDocumentEntity> implements Serializable{

    private Long id;
    private String description;
    private String code;
    private StatusEnum status;
    private Date lastUpdate;
    private ProjectEntity project;
    private MainDocumentEntity mainDocumentEntity;
    private PurchaseOrderEntity purchaseOrder;
    private AttachmentMainDocumentEntity attachmentProjectDocument;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PROJECT_DOCUMENT_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }



    @Column (name = "status",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Column(name = "last_update",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Lob
    @Size(max = 20000)
    @Column(name = "description", nullable = true, length = 20000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Size(max = 250)
    @Column(name = "code", nullable = false, length = 250)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_id", nullable = false)
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="main_document_id")
    public MainDocumentEntity getMainDocumentEntity() {
        return mainDocumentEntity;
    }

    public void setMainDocumentEntity(MainDocumentEntity mainDocumentEntity) {
        this.mainDocumentEntity = mainDocumentEntity;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="attachment_project_document_id")
    public AttachmentMainDocumentEntity getAttachmentProjectDocument() {
        return attachmentProjectDocument;
    }

    public void setAttachmentProjectDocument(AttachmentMainDocumentEntity attachmentProjectDocument) {
        this.attachmentProjectDocument = attachmentProjectDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectDocumentEntity that = (ProjectDocumentEntity) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (!id.equals(that.id)) return false;
        if (lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null) return false;
        if (mainDocumentEntity != null ? !mainDocumentEntity.equals(that.mainDocumentEntity) : that.mainDocumentEntity != null)
            return false;
        if (project != null ? !project.equals(that.project) : that.project != null) return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (mainDocumentEntity != null ? mainDocumentEntity.hashCode() : 0);
        return result;
    }
}
